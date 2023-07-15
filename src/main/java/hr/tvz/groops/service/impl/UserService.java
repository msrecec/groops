package hr.tvz.groops.service.impl;

import com.querydsl.core.BooleanBuilder;
import hr.tvz.groops.command.crud.UserCommand;
import hr.tvz.groops.command.search.UserSearchCommand;
import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.dto.response.UserDto;
import hr.tvz.groops.model.QUser;
import hr.tvz.groops.model.User;
import hr.tvz.groops.repository.PermissionRepository;
import hr.tvz.groops.repository.RoleRepository;
import hr.tvz.groops.repository.UserGroupRoleRepository;
import hr.tvz.groops.repository.UserRepository;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import hr.tvz.groops.service.Searchable;
import hr.tvz.groops.util.QueryBuilderUtil;
import hr.tvz.groops.utils.TimeUtils;
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
import java.util.List;
import java.util.stream.Collectors;

import static hr.tvz.groops.util.MapUtil.commandToEntity;

@Service
public class UserService implements Searchable {
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
        user.setConfirmed(false);
        user.setCreatedTs(now);
        user.setCreatedBy(authenticationService.getCurrentLoggedInUserUsername());

        // todo send registration confirmation email

        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT)
    public UserDto update(Long id, @Valid UserCommand command) {
        logger.debug("Updating user...");
        User user = findUserEntityById(id, userRepository);
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
