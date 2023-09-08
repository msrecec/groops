package hr.tvz.groops.controller;

import hr.tvz.groops.command.crud.PasswordCommand;
import hr.tvz.groops.security.constants.RoleConstants;
import hr.tvz.groops.service.url.URLService;
import hr.tvz.groops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/templates")
public class TemplateController extends ControllerBase {
    private final UserService userService;
    private final URLService urlService;

    @Autowired
    public TemplateController(UserService userService,
                              URLService urlService) {
        this.userService = userService;
        this.urlService = urlService;
    }

    @GetMapping("/mail/create")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_MAIL_CREATE + "')")
    ResponseEntity<?> mailCreateView(HttpServletRequest request, HttpServletResponse response) throws IOException {
        userService.confirmEmailCreate();
        String url = urlService.getFrontendBaseURL() + "mail-create-confirmation";
        response.sendRedirect(url);
        return ResponseEntity.created(URI.create(url)).build();
    }

    @GetMapping("/mail/change")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_MAIL_CHANGE + "')")
    ResponseEntity<?> mailChangeView(HttpServletRequest request, HttpServletResponse response) throws IOException {
        userService.confirmEmailChange();
        String url = urlService.getFrontendBaseURL() + "mail-change-confirmation";
        response.sendRedirect(url);
        return ResponseEntity.created(URI.create(url)).build();
    }

    @GetMapping("/password/change")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_PASSWORD_CHANGE + "')")
    ResponseEntity<?> passwordChangeView(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        userService.confirmPasswordChange();
        String url = urlService.getFrontendBaseURL() + "password-change-confirmation";
        response.sendRedirect(url);
        return ResponseEntity.created(URI.create(url)).build();
    }

    @PostMapping("/forgot-password/confirm")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_PASSWORD_FORGOT + "')")
    void forgotUserPasswordConfirm(@RequestBody @Valid PasswordCommand command, HttpServletResponse response) throws IOException {
        userService.confirmPasswordForgot(command);
    }

}
