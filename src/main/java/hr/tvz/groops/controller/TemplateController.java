package hr.tvz.groops.controller;

import hr.tvz.groops.security.constants.RoleConstants;
import hr.tvz.groops.service.URLService;
import hr.tvz.groops.service.UserService;
import hr.tvz.groops.service.token.MailChangeJWTService;
import hr.tvz.groops.service.token.MailCreateJWTService;
import hr.tvz.groops.service.token.PasswordChangeJWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/templates")
public class TemplateController extends ControllerBase {
    private final MailCreateJWTService mailCreateJWTService;
    private final MailChangeJWTService mailChangeJWTService;
    private final PasswordChangeJWTService passwordChangeJWTService;
    private final UserService userService;
    private final URLService urlService;

    @Autowired
    public TemplateController(MailCreateJWTService mailCreateJWTService,
                              MailChangeJWTService mailChangeJWTService,
                              PasswordChangeJWTService passwordChangeJWTService,
                              UserService userService,
                              URLService urlService) {
        this.mailCreateJWTService = mailCreateJWTService;
        this.mailChangeJWTService = mailChangeJWTService;
        this.passwordChangeJWTService = passwordChangeJWTService;
        this.userService = userService;
        this.urlService = urlService;
    }

    @GetMapping("/mail/create")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_MAIL_CREATE + "')")
    String mailCreateView(HttpServletRequest request, HttpServletResponse response, Model model) {
        userService.confirmEmailCreate();
        return "confirmed email";
    }

    @GetMapping("/mail/change")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_MAIL_CREATE + "')")
    String mailChangeView(HttpServletRequest request, HttpServletResponse response, Model model) {
        userService.confirmEmailChange();
        return "confirmed email change";
    }

    @GetMapping("/password/change")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_MAIL_CREATE + "')")
    String passwordChangeView(HttpServletRequest request, HttpServletResponse response, Model model) {
        userService.confirmPasswordChange();
        return "confirmed password change";
    }

    // todo add password reset

}
