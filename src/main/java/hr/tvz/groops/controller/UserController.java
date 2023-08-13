package hr.tvz.groops.controller;

import hr.tvz.groops.command.crud.UserCommand;
import hr.tvz.groops.command.crud.UserCreateCommand;
import hr.tvz.groops.command.crud.UserUpdateCommand;
import hr.tvz.groops.command.search.UserSearchCommand;
import hr.tvz.groops.dto.response.UserDto;
import hr.tvz.groops.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends ControllerBase {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    List<UserDto> findAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    UserDto findUserById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @GetMapping("/current")
    UserDto findCurrentUser() {
        return userService.getCurrent();
    }

    @PostMapping("/register")
    UserDto registerUser(@RequestBody @Valid UserCreateCommand command) {
        return userService.register(command);
    }

    @PutMapping("/{id}")
    UserDto updateUser(@RequestBody @Valid UserUpdateCommand command, @PathVariable("id") Long id) {
        return userService.update(id, command);
    }

    @PostMapping("/search")
    Page<UserDto> searchUser(@RequestBody UserSearchCommand command) {
        return userService.search(command, command.getPageable());
    }

    @DeleteMapping("/current")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCurrentUser() {
        userService.deleteCurrent();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUserById(@PathVariable("id") Long id) {
        userService.deleteById(id);
    }

}
