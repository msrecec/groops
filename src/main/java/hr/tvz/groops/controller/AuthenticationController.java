package hr.tvz.groops.controller;

import hr.tvz.groops.command.crud.LoginCommand;
import hr.tvz.groops.dto.response.LoginDto;
import hr.tvz.groops.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController extends ControllerBase {
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    LoginDto login(@RequestBody @Valid LoginCommand command, HttpServletResponse response) {
        LoginDto loginDto = userService.login(command.getUsername(), command.getPassword(), response);
        return LoginDto.builder().exp(loginDto.getExp()).build();
    }

    @DeleteMapping("/logout")
    void logout(HttpServletResponse response) {
        userService.logout(response);
    }
}
