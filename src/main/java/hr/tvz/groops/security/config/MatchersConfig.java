package hr.tvz.groops.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

import static hr.tvz.groops.constants.URLConstants.*;

@Configuration
public class MatchersConfig {
    @Bean("andRequestMatchers")
    public List<RequestMatcher> getAndRequestMatchers() {
        List<RequestMatcher> matchers = new ArrayList<>();
        matchers.add(new AndRequestMatcher(new AntPathRequestMatcher(MAIL_CHANGE_TEMPLATE_URL)));
        matchers.add(new AndRequestMatcher(new AntPathRequestMatcher(MAIL_CREATE_TEMPLATE_URL)));
        matchers.add(new AndRequestMatcher(new AntPathRequestMatcher(PASSWORD_CHANGE_TEMPLATE_URL)));
        matchers.add(new AndRequestMatcher(new AntPathRequestMatcher(PASSWORD_FORGOT_TEMPLATE_URL)));
        matchers.add(new AndRequestMatcher(new AntPathRequestMatcher(VERIFICATION_RESEND_URL)));
        return matchers;
    }
}
