package hr.tvz.groops.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import static hr.tvz.groops.constants.ProfileConstants.DEV;

@Profile(DEV)
@Configuration
@EnableWebSecurity
public class ApplicationDevSecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/**/v2/api-docs",
            "/**/v3/api-docs",
            "/**/swagger-resources/**",
            "/**/swagger-ui/**",
            "/**/version"
    };

    private static final String ACTUATOR_PATTERN = "/**/actuator/**";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf()
//                .disable()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .addFilterBefore(mailJwtTokenVerifier, BasicAuthenticationFilter.class)
//                .addFilterAfter(appJwtTokenVerifier, MailJwtTokenVerifier.class)
//                .addFilterAfter(mdcFilter, AppJwtTokenVerifier.class)
//                .authorizeRequests()
//                .antMatchers("/**/login/**")
//                .permitAll()
//                .antMatchers(AUTH_WHITELIST)
//                .permitAll()
//                .antMatchers(ACTUATOR_PATTERN)
//                .permitAll()
//                .antMatchers("/**")
//                .authenticated()
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        return http.csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/**")
                .permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .build();
    }

}
