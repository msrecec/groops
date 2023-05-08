package hr.tvz.groops.service.token;

import javax.servlet.http.HttpServletResponse;

public interface VerificationResendService {
    void setResendTokenForUser(String username, HttpServletResponse response);

    void setResendTokenForUser(HttpServletResponse response);
}
