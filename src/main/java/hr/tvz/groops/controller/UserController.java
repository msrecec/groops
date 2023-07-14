package hr.tvz.groops.controller;

import hr.tvz.groops.command.crud.UserCommand;
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
    List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    UserDto findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @GetMapping("/current")
    UserDto findCurrent() {
        return userService.getCurrent();
    }

    @PostMapping("/register")
    UserDto register(@RequestBody @Valid UserCommand command) {
        return userService.register(command);
    }

    @PutMapping("/{id}")
    UserDto update(@RequestBody @Valid UserCommand command, @PathVariable("id") Long id) {
        return userService.update(id, command);
    }

    @PostMapping("/search")
    Page<UserDto> search(@RequestBody UserSearchCommand command) {
        return userService.searchUsers(command, command.getPageable());
    }

    @DeleteMapping("/current")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCurrent() {
        userService.deleteCurrent();
    }

}
