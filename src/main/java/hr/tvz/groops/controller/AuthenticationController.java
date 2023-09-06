package hr.tvz.groops.controller;

import hr.tvz.groops.command.crud.LoginCommand;
import hr.tvz.groops.dto.response.JWTDto;
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
    JWTDto login(@RequestBody @Valid LoginCommand command, HttpServletResponse response) {
        return userService.login(command.getUsername(), command.getPassword(), response);
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
