package hr.tvz.groops.service;

import com.querydsl.core.BooleanBuilder;
import hr.tvz.groops.command.crud.UserCreateCommand;
import hr.tvz.groops.command.crud.UserUpdateCommand;
import hr.tvz.groops.command.search.UserSearchCommand;
import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.dto.response.UserDto;
import hr.tvz.groops.event.notification.verification.*;
import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.model.PendingVerification;
import hr.tvz.groops.model.QUser;
import hr.tvz.groops.model.User;
import hr.tvz.groops.model.constants.Constants;
import hr.tvz.groops.model.enums.VerificationTypeEnum;
import hr.tvz.groops.repository.*;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import hr.tvz.groops.service.verification.VerificationPublisherService;
import hr.tvz.groops.util.QueryBuilderUtil;
import hr.tvz.groops.util.SecurityUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class UserService implements Searchable {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PendingVerificationRepository pendingVerificationRepository;
    private final VerificationPublisherService verificationPublisherService;
    private final AuthenticationService authenticationService;

    @Autowired
    public UserService(ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       PendingVerificationRepository pendingVerificationRepository,
                       VerificationPublisherService verificationPublisherService,
                       AuthenticationService authenticationService) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.pendingVerificationRepository = pendingVerificationRepository;
        this.verificationPublisherService = verificationPublisherService;
        this.authenticationService = authenticationService;
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT)
    public UserDto register(@Valid UserCreateCommand command) {
        logger.debug("Creating user...");
        Instant now = now();
        User user = modelMapper.map(command, User.class);
        user.setPasswordHash(passwordEncoder.encode(command.getPassword()));
        validateUser(user);
        user.setVerified(false);
        user.setCreatedTs(now);
        user.setCreatedBy(authenticationService.getCurrentLoggedInUserUsername());
        verificationPublisherService.verifyEmail(user, now, VerificationTypeEnum.MAIL);
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT)
    public UserDto update(Long id, @Valid UserUpdateCommand command) {
        logger.debug("Updating user...");
        Instant now = now();
        User user = findUserEntityById(id, userRepository);
        modelMapper.map(command, user);

        if (command.getEmail().compareTo(user.getEmail()) != 0) {
            verificationPublisherService.verifyEmail(user, now, VerificationTypeEnum.MAIL_CHANGE);
        }
        if (command.getPassword() != null) {
            if (!SecurityUtil.isValidPassword(command.getPassword())) {
                logger.debug(ExceptionEnum.INVALID_PASSWORD_EXCEPTION.getFullMessage());
                throw new IllegalArgumentException(ExceptionEnum.INVALID_PASSWORD_EXCEPTION.getShortMessage());
            }
            user.setVerified(false);
            verificationPublisherService.verifyPasswordChange(user, now);
        }
        user.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        user.setModifiedTs(now());
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    private void validateUser(User user) {
        if (SecurityUtil.isValidPassword(user.getPasswordHash())) {
            logger.debug(ExceptionEnum.INVALID_PASSWORD_EXCEPTION.getFullMessage());
            throw new IllegalArgumentException(ExceptionEnum.INVALID_PASSWORD_EXCEPTION.getShortMessage());
        }
        if (user.getDateOfBirth().compareTo(new Date()) > 0) {
            logger.debug(ExceptionEnum.INVALID_DATE_OF_BIRTH_EXCEPTION.getFullMessage());
            throw new IllegalArgumentException(ExceptionEnum.INVALID_DATE_OF_BIRTH_EXCEPTION.getShortMessage());
        }
    }

    public UserDto findById(Long id) {
        logger.debug("Fetching user with id {} ...", id);
        User user = findUserEntityById(id, userRepository);
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto getCurrent() {
        logger.debug("Fetching current user...");
        GroopsUserDataToken groopsUserDataToken = authenticationService.getCurrentLoggedInUser();
        return findById(groopsUserDataToken.getId());
    }

    public List<UserDto> findAll() {
        logger.debug("Fetching all users...");
        return userRepository.findAll()
                .stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .collect(Collectors.toList());
    }

    public Page<UserDto> search(UserSearchCommand command, Pageable pageable) {
        QUser user = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();
        QueryBuilderUtil.buildCreatedModifiedAndIdConditions(command, user._super, user.id, builder);
        QueryBuilderUtil.like(builder, user.username, command.getUsername());
        QueryBuilderUtil.like(builder, user.email, command.getEmail());
        QueryBuilderUtil.like(builder, user.firstName, command.getFirstName());
        QueryBuilderUtil.like(builder, user.lastName, command.getLastName());
        QueryBuilderUtil.gte(builder, user.dateOfBirth, command.getDateOfBirthFrom());
        QueryBuilderUtil.lte(builder, user.dateOfBirth, command.getDateOfBirthTo());
        QueryBuilderUtil.like(builder, user.description, command.getDescription());

        return userRepository.findAll(builder.getValue() != null ? builder.getValue() : builder, pageable)
                .map(u -> modelMapper.map(u, UserDto.class));
    }

    @Transactional(timeout = Constants.DEFAULT_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public List<VerificationEvent> findNonVerifiedEmailUserEvents() {
        List<VerificationEvent> verificationEvents = new ArrayList<>();
        List<PendingVerification> pendingVerifications = pendingVerificationRepository.findAll();
        for (PendingVerification pendingVerification : pendingVerifications) {
            switch (pendingVerification.getVerificationType()) {
                case PASSWORD_CHANGE:
                    verificationEvents.add(new PasswordChangeVerificationEvent(this, pendingVerification.getId()));
                    break;
                case MAIL:
                    verificationEvents.add(new MailCreateVerificationEvent(this, pendingVerification.getId()));
                    break;
                case MAIL_CHANGE:
                    verificationEvents.add(new MailChangeVerificationEvent(this, pendingVerification.getId()));
                    break;
                default:
                    throw new InternalServerException("Non supported verification type", new Throwable());
            }
        }
        return verificationEvents;
    }

    @Transactional(timeout = Constants.SHORT_TIMEOUT)
    public Long getIdOrCreateJobUserByName(String jobName, String jobMail, String jobDescription) {
        return userRepository.findIdByUsername(jobName).orElseGet(getJobUserIdSupplier(jobName, jobMail, jobDescription));
    }

    private Supplier<Long> getJobUserIdSupplier(String jobName, String jobMail, String jobDescription) {
        return () -> {
            Instant now = now();
            User user = User.builder()
                    .username(jobName)
                    .passwordHash("empty_hash")
                    .email(jobMail)
                    .firstName(jobName)
                    .lastName(jobName)
                    .dateOfBirth(new java.sql.Date(now.toEpochMilli()))
                    .description(jobDescription)
                    .verified(true)
                    .createdBy(jobName)
                    .createdTs(now)
                    .build();
            user = userRepository.saveAndFlush(user);
            return user.getId();
        };
    }

    @Transactional(timeout = Constants.DEFAULT_TIMEOUT)
    public void deleteCurrent() {
        logger.debug("Deleting current user...");
        GroopsUserDataToken token = authenticationService.getCurrentLoggedInUser();
        User user = findUserEntityById(token.getId(), userRepository);
        userRepository.delete(user);
    }

    @Transactional(timeout = Constants.DEFAULT_TIMEOUT)
    public void deleteById(Long id) {
        logger.debug("Deleting current user with id {}", id);
        User user = findUserEntityById(id, userRepository);
        userRepository.delete(user);
    }


}
