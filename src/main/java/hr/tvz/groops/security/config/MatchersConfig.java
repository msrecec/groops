package hr.tvz.groops.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

import static hr.tvz.groops.constants.URLConstants.MAIL_CHANGE_TEMPLATE_URL;
import static hr.tvz.groops.constants.URLConstants.REFRESH_TOKEN_URL;

@Configuration
public class MatchersConfig {
    @Bean("andRequestMatchers")
    public List<RequestMatcher> getAndRequestMatchers() {
        List<RequestMatcher> matchers = new ArrayList<>();
        matchers.add(new AndRequestMatcher(new AntPathRequestMatcher(MAIL_CHANGE_TEMPLATE_URL)));
        matchers.add(new AndRequestMatcher(new AntPathRequestMatcher(REFRESH_TOKEN_URL)));
        return matchers;
    }
}
