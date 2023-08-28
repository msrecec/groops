package hr.tvz.groops.controller;

import hr.tvz.groops.command.crud.LoginCommand;
import hr.tvz.groops.dto.response.LoginDto;
import hr.tvz.groops.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    LoginDto login(@RequestBody @Valid LoginCommand command, HttpServletResponse response) {
        LoginDto loginDto = userService.login(command.getUsername(), command.getPassword(), response);
        return LoginDto.builder().exp(loginDto.getExp()).build();
    }

    @GetMapping("/nop")
    void nop() {
        logger.debug("Running nop...");
    }

    @DeleteMapping("/logout")
    void logout(HttpServletResponse response) {
        userService.logout(response);
    }
}
