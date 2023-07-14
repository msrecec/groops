package hr.tvz.groops.service.impl;

import hr.tvz.groops.command.crud.UserCommand;
import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.dto.response.UserDto;
import hr.tvz.groops.model.User;
import hr.tvz.groops.repository.PermissionRepository;
import hr.tvz.groops.repository.RoleRepository;
import hr.tvz.groops.repository.UserGroupRoleRepository;
import hr.tvz.groops.repository.UserRepository;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import hr.tvz.groops.utils.TimeUtils;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static hr.tvz.groops.constants.CrudExceptionEnum.USER_NOT_FOUND_BY_ID;
import static hr.tvz.groops.util.MapUtil.commandToEntity;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;
    private UserGroupRoleRepository userGroupRoleRepository;
    private AuthenticationService authenticationService;

    @Autowired
    public UserService(ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PermissionRepository permissionRepository,
                       UserGroupRoleRepository userGroupRoleRepository,
                       AuthenticationService authenticationService) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userGroupRoleRepository = userGroupRoleRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT)
    public UserDto register(@Valid UserCommand command) {
        logger.debug("Creating user...");
        Instant now = Instant.now();

        // todo add password validation
        // todo add email validation -> optional
        // todo add date of birth validation
        // todo add first name and last name validation
        // todo add username validation

        User user = commandToEntity(command, new User());
        user.setCreatedTs(now);
        user.setCreatedBy(authenticationService.getCurrentLoggedInUserUsername());

        // todo send registration confirmation email

        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT)
    public UserDto update(Long id, @Valid UserCommand command) {
        logger.debug("Updating user...");
        User user = findUserEntityById(id);
        // todo add password validation and confirmation email
        // todo add email validation -> optional
        // todo add date of birth validation
        // todo add first name and last name validation
        // todo add username validation
        // todo add email change confirmation email
        commandToEntity(command, user);
        user.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        user.setModifiedTs(TimeUtils.now());
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    public UserDto findById(Long id) {
        logger.debug("Fetching user with id {} ...", id);
        User user = findUserEntityById(id);
        return modelMapper.map(user, UserDto.class);
    }

    private @NotNull User findUserEntityById(@NotNull Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_BY_ID.getMessageComposed(id)));
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

    public Page<UserDto> searchUsers() {
//        QUser
        return null;
    }


}
