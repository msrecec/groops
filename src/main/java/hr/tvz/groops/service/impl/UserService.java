package hr.tvz.groops.service.impl;

import com.querydsl.core.BooleanBuilder;
import hr.tvz.groops.command.crud.UserCreateCommand;
import hr.tvz.groops.command.crud.UserUpdateCommand;
import hr.tvz.groops.command.search.UserSearchCommand;
import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.dto.response.UserDto;
import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.model.QUser;
import hr.tvz.groops.model.User;
import hr.tvz.groops.repository.*;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import hr.tvz.groops.service.Searchable;
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
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class UserService implements Searchable {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationPublisherService verificationPublisherService;
    private final AuthenticationService authenticationService;

    @Autowired
    public UserService(ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       VerificationPublisherService verificationPublisherService,
                       AuthenticationService authenticationService) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
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
        user.setConfirmed(false);
        user.setCreatedTs(now);
        user.setCreatedBy(authenticationService.getCurrentLoggedInUserUsername());
        verificationPublisherService.verifyEmail(user.getId(), now);
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT)
    public UserDto update(Long id, @Valid UserUpdateCommand command) {
        logger.debug("Updating user...");
        Instant now = now();
        User user = findUserEntityById(id, userRepository);
        modelMapper.map(command, user);

        if (command.getEmail().compareTo(user.getEmail()) != 0) {
            user.setConfirmed(false);
            verificationPublisherService.verifyEmail(user.getId(), now);
        }
        if (command.getPassword() != null) {
            if (!SecurityUtil.isValidPassword(command.getPassword())) {
                logger.debug(ExceptionEnum.INVALID_PASSWORD_EXCEPTION.getFullMessage());
                throw new IllegalArgumentException(ExceptionEnum.INVALID_PASSWORD_EXCEPTION.getShortMessage());
            }
            verificationPublisherService.verifyPasswordChange(user.getId());
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
        QueryBuilderUtil.equals(builder, user.confirmed, command.getConfirmed());

        return userRepository.findAll(builder.getValue() != null ? builder.getValue() : builder, pageable)
                .map(u -> modelMapper.map(u, UserDto.class));
    }

    @Transactional
    public void deleteCurrent() {
        logger.debug("Deleting current user...");
        GroopsUserDataToken token = authenticationService.getCurrentLoggedInUser();
        User user = findUserEntityById(token.getId(), userRepository);
        userRepository.delete(user);
    }

    @Transactional
    public void deleteById(Long id) {
        logger.debug("Deleting current user with id {}", id);
        User user = findUserEntityById(id, userRepository);
        userRepository.delete(user);
    }


}
