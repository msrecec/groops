package hr.tvz.groops.service;

import com.querydsl.core.BooleanBuilder;
import hr.tvz.groops.command.crud.PasswordCommand;
import hr.tvz.groops.command.crud.UserCreateCommand;
import hr.tvz.groops.command.crud.UserUpdateCommand;
import hr.tvz.groops.command.searchPaginated.UserPaginatedSearchCommand;
import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.dto.response.FriendRequestDto;
import hr.tvz.groops.dto.response.JWTDto;
import hr.tvz.groops.dto.response.NotificationDto;
import hr.tvz.groops.dto.response.UserDto;
import hr.tvz.groops.event.notification.verification.*;
import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.model.*;
import hr.tvz.groops.model.enums.VerificationTypeEnum;
import hr.tvz.groops.model.pk.FriendRequestId;
import hr.tvz.groops.repository.*;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import hr.tvz.groops.service.s3.S3Service;
import hr.tvz.groops.service.security.AuthenticationService;
import hr.tvz.groops.service.security.AuthorizationService;
import hr.tvz.groops.service.security.CookieService;
import hr.tvz.groops.service.token.AppJWTService;
import hr.tvz.groops.service.token.VerificationResendService;
import hr.tvz.groops.service.verification.VerificationPublisherService;
import hr.tvz.groops.util.QueryBuilderUtil;
import hr.tvz.groops.util.SecurityUtil;
import hr.tvz.groops.util.UploadUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class UserService implements Searchable {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final PendingVerificationRepository pendingVerificationRepository;
    private final VerificationPublisherService verificationPublisherService;
    private final S3Service s3Service;
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final AppJWTService appJWTService;
    private final CookieService appCookieService;
    private final UserGroupRoleRepository userGroupRoleRepository;
    private final VerificationResendService verificationResendService;
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public UserService(ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       NotificationRepository notificationRepository, FriendRepository friendRepository,
                       FriendRequestRepository friendRequestRepository,
                       PendingVerificationRepository pendingVerificationRepository,
                       VerificationPublisherService verificationPublisherService,
                       S3Service s3Service,
                       AuthenticationService authenticationService,
                       AuthorizationService authorizationService, AppJWTService appJWTService,
                       CookieService appCookieService,
                       UserGroupRoleRepository userGroupRoleRepository,
                       VerificationResendService verificationResendService,
                       EntityManager entityManager) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.friendRepository = friendRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.pendingVerificationRepository = pendingVerificationRepository;
        this.verificationPublisherService = verificationPublisherService;
        this.s3Service = s3Service;
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.appJWTService = appJWTService;
        this.appCookieService = appCookieService;
        this.userGroupRoleRepository = userGroupRoleRepository;
        this.verificationResendService = verificationResendService;
        this.entityManager = entityManager;
    }

    @Transactional(timeout = TimeoutConstants.LONG_TIMEOUT)
    public UserDto register(@Valid UserCreateCommand command) {
        logger.debug("Creating user...");
        Instant now = now();

        User user = modelMapper.map(command, User.class);
        if (!command.getPassword1().equals(command.getPassword2())) {
            throw new IllegalArgumentException("Passwords must match");
        }
        String password = command.getPassword1();
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setVerified(false);
        user.setCreatedTs(now);
        user.setCreatedBy(authenticationService.getCurrentLoggedInUserUsername());

        SecurityUtil.validatePassword(password);
        if (user.getDateOfBirth().compareTo(new Date()) > 0) {
            logger.debug(ExceptionEnum.INVALID_DATE_OF_BIRTH_EXCEPTION.getFullMessage());
            throw new IllegalArgumentException(ExceptionEnum.INVALID_DATE_OF_BIRTH_EXCEPTION.getShortMessage());
        }

        user = userRepository.save(user);
        verificationPublisherService.verifyEmailCreate(user, now);

        return modelMapper.map(user, UserDto.class);
    }

    @Transactional(timeout = TimeoutConstants.LONG_TIMEOUT)
    public UserDto update(@Valid UserUpdateCommand command, @Nullable MultipartFile file) {
        logger.debug("Updating user...");
        Instant now = now();
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User user = findUserEntityByIdLockByPessimisticWrite(currentUserId, userRepository);

        modelMapper.map(command, user);
        user.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        user.setModifiedTs(now);

        if (file != null) {
            uploadProfilePictureCompressed(user, file);
        }
        user = userRepository.save(user);

        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Transactional(timeout = TimeoutConstants.LONG_TIMEOUT, propagation = Propagation.MANDATORY)
    public void uploadProfilePictureCompressed(@NotNull User user, @NotNull MultipartFile file) {
        UploadUtil.checkIfImage(file);
        String newProfilePictureKey = s3Service.generateUserProfilePictureKey(user.getId(), file);
        String newProfilePictureThumbnailKey = s3Service.generateUserProfilePictureThumbnailKey(user.getId(), file);
        user.setProfilePictureKey(newProfilePictureKey);
        user.setProfilePictureThumbnailKey(newProfilePictureThumbnailKey);
        s3Service.uploadImageAndThumbnailCompressed(user.getProfilePictureKey(), user.getProfilePictureThumbnailKey(), file);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public JWTDto login(@NotNull String username, @NotNull String password, HttpServletResponse httpServletResponse) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Wrong username or password"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Wrong username or password");
        }
        List<PendingVerification> pendingVerifications = pendingVerificationRepository.findByUser(user);
        List<String> exceptionMessages = new ArrayList<>();
        for (PendingVerification pendingVerification : pendingVerifications) {
            switch (pendingVerification.getVerificationType()) {
                case MAIL_CREATE:
                case MAIL_CHANGE:
                    exceptionMessages.add("You must verify email");
                    verificationResendService.setResendTokenForUser(user.getUsername(), httpServletResponse);
                    break;
                case PASSWORD_CHANGE:
                    exceptionMessages.add("You must verify password");
                    verificationResendService.setResendTokenForUser(user.getUsername(), httpServletResponse);
            }
        }
        if (!exceptionMessages.isEmpty()) {
            throw new AccessDeniedException(String.join("\n", exceptionMessages));
        }
        String[] roles = new String[]{};
        JWTDto token = appJWTService.getToken(user.getId(), user.getUsername(), roles);
        Jws<Claims> claims = appJWTService.getClaimsFromToken(token.getToken());
        Integer iat = (Integer) claims.getBody().get("iat");
        user.setTokenIssuedAt(iat);
        return token;
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void logout(HttpServletResponse httpServletResponse) {
        logger.debug("Logging out...");
//        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
//        User user = findUserEntityById(currentUserId, userRepository);
//        appCookieService.unsetResponseCookie(httpServletResponse);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void changePassword(@NotNull PasswordCommand command) {
        logger.debug("Confirming user password change...");
        Instant now = now();
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User user = findUserEntityByIdLockByPessimisticWrite(currentUserId, userRepository);

        if (!command.getPassword1().equals(command.getPassword2())) {
            throw new IllegalArgumentException("Passwords must match");
        }
        String password = command.getPassword1();

        SecurityUtil.validatePassword(password);
        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("New password must be different than the current one");
        }

        user.setPasswordHash(passwordEncoder.encode(password));
        user.setVerified(false);
        user.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        user.setModifiedTs(now);
        user = userRepository.saveAndFlush(user);

        Optional<PendingVerification> pending = pendingVerificationRepository.findByUserAndVerificationType(user, VerificationTypeEnum.PASSWORD_CHANGE);
        pending.ifPresent(pendingVerificationRepository::delete);
        flushAndClear();
        verificationPublisherService.verifyPasswordChange(user, now);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void resendVerification() {
        User user = findUserEntityByIdLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserId(), userRepository);
        List<PendingVerification> verifications = pendingVerificationRepository.findByUser(user);
        if (verifications.isEmpty()) {
            return;
        }
        for (PendingVerification verification : verifications) {
            verificationPublisherService.publishVerificationEvent(verification);
        }
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public boolean passwordForgot(@NotNull String username) {
        logger.debug("Confirming user password forgot...");
        Instant now = now();
        Optional<User> userOpt = userRepository.findByUsernameLockByPessimisticWrite(username);
        if (userOpt.isEmpty()) {
            logger.debug("No user with username: {}", username);
            return false;
        }
        User user = userOpt.get();

        user.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        user.setModifiedTs(now);
        user = userRepository.saveAndFlush(user);

        Optional<PendingVerification> pending = pendingVerificationRepository.findByUserAndVerificationType(user, VerificationTypeEnum.PASSWORD_FORGOT);
        pending.ifPresent(pendingVerificationRepository::delete);
        flushAndClear();
        verificationPublisherService.verifyPasswordForgot(user, now);
        return true;
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void changeMail(@NotNull String email) {
        logger.debug("Confirming user mail change...");
        Instant now = now();
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User user = findUserEntityByIdLockByPessimisticWrite(currentUserId, userRepository);

        if (email.compareTo(user.getEmail()) == 0) {
            throw new IllegalArgumentException("Email must be different than the current one");
        }

        user.setEmail(email);
        user.setVerified(false);
        user.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        user.setModifiedTs(now);
        user = userRepository.saveAndFlush(user);

        Optional<PendingVerification> pending = pendingVerificationRepository.findByUserAndVerificationType(user, VerificationTypeEnum.MAIL_CHANGE);
        pending.ifPresent(pendingVerificationRepository::delete);
        flushAndClear();
        verificationPublisherService.verifyEmailChange(user, now);
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void confirmEmailCreate() {
        logger.debug("Confirming user mail...");
        confirmationHandler(VerificationTypeEnum.MAIL_CREATE, "User has already confirmed email");
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void confirmPasswordForgot(@NotNull PasswordCommand command) {
        logger.debug("Confirming user password forgot...");
        Instant now = now();
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User user = findUserEntityByIdLockByPessimisticWrite(currentUserId, userRepository);
        Optional<PendingVerification> pendingVerification = pendingVerificationRepository.findByUserAndVerificationType(user, VerificationTypeEnum.PASSWORD_FORGOT);
        if (pendingVerification.isEmpty()) {
            throw new IllegalArgumentException("user already verified password forgot");
        }
        if (!command.getPassword1().equals(command.getPassword2())) {
            throw new IllegalArgumentException("Passwords must match");
        }
        String password = command.getPassword1();

        SecurityUtil.validatePassword(password);
        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("New password must be different than the current one");
        }

        user.setPasswordHash(passwordEncoder.encode(password));
        user.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        user.setModifiedTs(now);
        user = userRepository.saveAndFlush(user);

        Optional<PendingVerification> pending = pendingVerificationRepository.findByUserAndVerificationType(user, VerificationTypeEnum.PASSWORD_FORGOT);
        pending.ifPresent(pendingVerificationRepository::delete);
        flushAndClear();
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void confirmEmailChange() {
        logger.debug("Confirming user mail change...");
        confirmationHandler(VerificationTypeEnum.MAIL_CHANGE, "User has already confirmed email change");
    }

    @Transactional(timeout = hr.tvz.groops.constants.TimeoutConstants.TINY_TIMEOUT)
    public void confirmPasswordChange() {
        logger.debug("Confirming user password change...");
        confirmationHandler(VerificationTypeEnum.PASSWORD_CHANGE, "User has already confirmed password change");
    }

    private void confirmationHandler(VerificationTypeEnum verificationTypeEnum, String exceptionMessage) {
        Instant now = now();
        User user = findUserEntityByUsernameLockByPessimisticWrite(authenticationService.getCurrentLoggedInUserUsername(), userRepository);
        Optional<PendingVerification> pendingVerification = pendingVerificationRepository.findByUserAndVerificationType(user, verificationTypeEnum);
        if (pendingVerification.isEmpty()) {
            throw new IllegalArgumentException(exceptionMessage);
        }
        pendingVerificationRepository.delete(pendingVerification.get());
        flushAndClear();

        List<PendingVerification> pendingVerifications = pendingVerificationRepository.findByUser(user);
        if (isVerifiable(pendingVerifications)) {
            user.setVerified(true);
        }

        user.setModifiedTs(now);
        user.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        userRepository.save(user);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private boolean isVerifiable(Collection<PendingVerification> pendingVerifications) {
        for (PendingVerification pendingVerification : pendingVerifications) {
            switch (pendingVerification.getVerificationType()) {
                case MAIL_CREATE:
                case MAIL_CHANGE:
                case PASSWORD_CHANGE:
                    return false;
            }
        }
        return true;
    }

    public UserDto findById(Long id) {
        logger.debug("Fetching user with id {} ...", id);
        User user = findUserEntityById(id, userRepository);
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto getCurrent() {
        logger.debug("Fetching current user...");
        GroopsUserDataToken groopsUserDataToken = authenticationService.getCurrentLoggedInUser();
        return modelMapper.map(findUserEntityById(groopsUserDataToken.getUserId(), userRepository), UserDto.class);
    }

    public List<UserDto> findAll() {
        logger.debug("Fetching all users...");
        return userRepository.findAll().stream().map(u -> modelMapper.map(u, UserDto.class)).collect(Collectors.toList());
    }

    public Page<UserDto> searchPaginated(UserPaginatedSearchCommand command, Pageable pageable) {
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

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public List<NotificationDto> findAllNotificationsOfCurrentUser() {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User user = findUserEntityById(currentUserId, userRepository);
        List<Notification> notifications = notificationRepository.findAllByUserOrderByIdDesc(user);
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(n -> modelMapper.map(n, NotificationDto.class))
                .collect(Collectors.toList());

        for (Notification notification : notifications) {
            if (notification.getRead()) {
                continue;
            }
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        return notificationDtos;
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public Integer getUnreadNotificationCount() {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User user = findUserEntityById(currentUserId, userRepository);
        return notificationRepository.countAllByUserOfUnread(user);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public List<VerificationEvent> findNonVerifiedEmailUserEvents() {
        List<VerificationEvent> verificationEvents = new ArrayList<>();
        List<PendingVerification> pendingVerifications = pendingVerificationRepository.findAll();
        for (PendingVerification pendingVerification : pendingVerifications) {
            switch (pendingVerification.getVerificationType()) {
                case PASSWORD_CHANGE:
                    verificationEvents.add(new PasswordChangeVerificationEvent(this, pendingVerification.getId(), pendingVerification.getUser().getId()));
                    break;
                case MAIL_CREATE:
                    verificationEvents.add(new MailCreateVerificationEvent(this, pendingVerification.getId(), pendingVerification.getUser().getId()));
                    break;
                case MAIL_CHANGE:
                    verificationEvents.add(new MailChangeVerificationEvent(this, pendingVerification.getId(), pendingVerification.getUser().getId()));
                    break;
                case PASSWORD_FORGOT:
                    verificationEvents.add(new PasswordForgotVerificationEvent(this, pendingVerification.getId(), pendingVerification.getUser().getId()));
                    break;
                default:
                    throw new InternalServerException("Non supported verification type", new Throwable());
            }
        }
        return verificationEvents;
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT)
    public Long createJobUserByNameLockByPessimisticWriteIfNotExistsAndGetId(String jobName, String jobMail, String jobDescription) {
        Instant now = now();
        Optional<Long> userOptional = userRepository.findIdByUsernameLockByPessimisticWrite(jobName);
        if (userOptional.isPresent()) {
            logger.info("No need to create user for job with name: {} because the user already exists", jobName);
            return userOptional.get();
        }
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
        return userRepository.saveAndFlush(user).getId();
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void deleteCurrent() {
        logger.debug("Deleting current user...");
        Instant now = now();
        GroopsUserDataToken token = authenticationService.getCurrentLoggedInUser();
        User user = findUserEntityByIdLockByPessimisticWrite(token.getUserId(), userRepository);
        Role adminRole = authorizationService.getOrCreateAdminRole(now);
        if (userGroupRoleRepository.countAllByUserAndRole(user, adminRole) != 0) {
            throw new IllegalArgumentException("Can't delete account, there are still groups where you are admin");
        }
        userRepository.delete(user);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void deleteById(Long id) {
        logger.debug("Deleting user with id {}", id);
        User user = findUserEntityByIdLockByPessimisticWrite(id, userRepository);
        userRepository.delete(user);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void sendFriendRequest(@NotNull Long recipientId) {
        logger.debug("Sending friend request to user with id {}", recipientId);
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(currentUserId, userRepository);
        User recipientUser = findUserEntityByIdLockByPessimisticWrite(recipientId, userRepository);

        if (friendRepository.existsByFirstUserAndSecondUser(currentUser, recipientUser) ||
                friendRepository.existsByFirstUserAndSecondUser(recipientUser, currentUser)) {
            throw new IllegalArgumentException("You are already friends");
        }

        FriendRequestId friendRequestId = new FriendRequestId();
        friendRequestId.setSenderId(currentUser.getId());
        friendRequestId.setRecipientId(recipientUser.getId());
        FriendRequest friendRequest = FriendRequest.builder()
                .friendRequestId(friendRequestId)
                .sender(currentUser)
                .recipient(recipientUser)
                .build();
        friendRequestRepository.save(friendRequest);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void acceptFriendRequest(@NotNull Long senderUserId) {
        logger.debug("Accepting a friend request by sender user: {}", senderUserId);
        Instant now = now();
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityByIdLockByPessimisticWrite(currentUserId, userRepository);
        User senderUser = findUserEntityByIdLockByPessimisticWrite(senderUserId, userRepository);
        FriendRequest friendRequest = findFriendRequestBySenderAndRecipient(senderUser, currentUser, friendRequestRepository);
        if (friendRepository.existsByFirstUserAndSecondUser(currentUser, senderUser) ||
                friendRepository.existsByFirstUserAndSecondUser(senderUser, currentUser)) {
            logger.debug("Users are already friends, deleting request...");
            friendRequestRepository.delete(friendRequest);
            return;
        }
        Friend friend = Friend.builder()
                .firstUser(senderUser)
                .secondUser(currentUser)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        friendRepository.save(friend);
        friendRequestRepository.delete(friendRequest);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void rejectFriendRequest(@NotNull Long senderUserId) {
        logger.debug("Rejecting a friend request by sender user: {}", senderUserId);
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityById(currentUserId, userRepository);
        User senderUser = findUserEntityById(senderUserId, userRepository);
        FriendRequest friendRequest = findFriendRequestBySenderAndRecipient(senderUser, currentUser, friendRequestRepository);
        friendRequestRepository.delete(friendRequest);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public List<FriendRequestDto> findAllPendingReceivedFriendRequests() {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityById(currentUserId, userRepository);
        return friendRequestRepository.findAllByRecipient(currentUser).stream()
                .map(friendRequest -> modelMapper.map(friendRequest, FriendRequestDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public List<FriendRequestDto> findAllPendingSentFriendRequests() {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityById(currentUserId, userRepository);
        return friendRequestRepository.findAllBySender(currentUser).stream()
                .map(friendRequest -> modelMapper.map(friendRequest, FriendRequestDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public FriendRequestDto findPendingReceivedFriendRequest(Long senderId) {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityById(currentUserId, userRepository);
        User senderUser = findUserEntityById(senderId, userRepository);
        FriendRequest friendRequest = findFriendRequestBySenderAndRecipient(senderUser, currentUser, friendRequestRepository);
        return modelMapper.map(friendRequest, FriendRequestDto.class);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT, isolation = Isolation.REPEATABLE_READ)
    public FriendRequestDto findPendingSentFriendRequest(Long recipientId) {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User currentUser = findUserEntityById(currentUserId, userRepository);
        User recipientUser = findUserEntityById(recipientId, userRepository);
        FriendRequest friendRequest = findFriendRequestBySenderAndRecipient(currentUser, recipientUser, friendRequestRepository);
        return modelMapper.map(friendRequest, FriendRequestDto.class);
    }
}
