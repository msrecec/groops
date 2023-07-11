package hr.tvz.groops.service.impl;

import hr.tvz.groops.command.crud.UserCreateCommand;
import hr.tvz.groops.command.crud.UserUpdateCommand;
import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.model.User;
import hr.tvz.groops.repository.*;
import hr.tvz.groops.utils.TimeUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hr.tvz.groops.constants.CrudExceptionEnum.USER_NOT_FOUND_BY_ID;

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
    public User create(@Valid UserCreateCommand command) {
        logger.debug("Creating user...");
        User user = User.builder()
                .username(command.getUsername())
                .password(passwordEncoder.encode(command.getPassword()))
                .email(command.getEmail())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .dateOfBirth(command.getDateOfBirth())
                .description(command.getDescription())
                .confirmed(false)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(TimeUtils.now())
                .build();
        return userRepository.save(user);
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT)
    public User update(Long id, @Valid UserUpdateCommand command) {
        logger.debug("Updating user...");
        User user = findById(id);
        user.setUsername(command.getUsername());
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        user.setDateOfBirth(command.getDateOfBirth());
        user.setDescription(command.getDescription());
        user.setModifiedBy(authenticationService.getCurrentLoggedInUserUsername());
        user.setModifiedTs(TimeUtils.now());
        return userRepository.save(user);
    }

    public User findById(Long id) {
        logger.debug("Fetching user with id {} ...", id);
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }

    public List<User> findAll() {
        logger.debug("Fetching all users...");
        return userRepository.findAll();
    }

    public Page<User> search() {
        return null;
    }


}
