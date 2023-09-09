package hr.tvz.groops.security.config;

import hr.tvz.groops.security.entrypoint.ForbiddenAuthenticationEntrypoint;
import hr.tvz.groops.security.filter.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static hr.tvz.groops.constants.ProfileConstants.DEV;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile(DEV)
public class ApplicationDevSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/**/v2/api-docs",
            "/**/v3/api-docs",
            "/**/swagger-resources/**",
            "/**/swagger-ui/**",
            "/**/version",
            "/**/authentication/login",
            "/**/authentication/logout",
            "/**/users/register",
            "/**/users/forgot-password"
    };

    private static final String ACTUATOR_PATTERN = "/**/actuator/**";
    private final AppJWTVerifier appJWTVerifier;
    private final MailCreateJWTVerifier mailCreateJWTVerifier;
    private final MailChangeJWTVerifier mailChangeJWTVerifier;
    private final PasswordChangeJWTVerifier passwordChangeJWTVerifier;
    private final PasswordForgotJWTVerifier passwordForgotJWTVerifier;
    private final MDCFilter mdcFilter;
    private final ForbiddenAuthenticationEntrypoint forbiddenAuthenticationEntrypoint;

    @Autowired
    public ApplicationDevSecurityConfig(AppJWTVerifier appJWTVerifier,
                                        MailCreateJWTVerifier mailCreateJWTVerifier,
                                        MailChangeJWTVerifier mailChangeJWTVerifier,
                                        PasswordChangeJWTVerifier passwordChangeJWTVerifier,
                                        PasswordForgotJWTVerifier passwordForgotJWTVerifier,
                                        MDCFilter mdcFilter,
                                        ForbiddenAuthenticationEntrypoint forbiddenAuthenticationEntrypoint) {
        this.appJWTVerifier = appJWTVerifier;
        this.mailCreateJWTVerifier = mailCreateJWTVerifier;
        this.mailChangeJWTVerifier = mailChangeJWTVerifier;
        this.passwordChangeJWTVerifier = passwordChangeJWTVerifier;
        this.passwordForgotJWTVerifier = passwordForgotJWTVerifier;
        this.mdcFilter = mdcFilter;
        this.forbiddenAuthenticationEntrypoint = forbiddenAuthenticationEntrypoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .cors()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(mailCreateJWTVerifier, BasicAuthenticationFilter.class)
                .addFilterBefore(mailChangeJWTVerifier, MailCreateJWTVerifier.class)
                .addFilterBefore(passwordChangeJWTVerifier, MailChangeJWTVerifier.class)
                .addFilterBefore(passwordForgotJWTVerifier, PasswordChangeJWTVerifier.class)
                .addFilterBefore(appJWTVerifier, PasswordChangeJWTVerifier.class)
                .addFilterAfter(mdcFilter, AppJWTVerifier.class)
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST)
                .permitAll()
                .mvcMatchers(HttpMethod.POST, "/**/users/sessions")
                .permitAll()
                .antMatchers(ACTUATOR_PATTERN)
                .permitAll()
                .antMatchers("/**")
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(forbiddenAuthenticationEntrypoint);
    }
}
