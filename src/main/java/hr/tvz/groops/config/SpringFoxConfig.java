package hr.tvz.groops.config;

import com.fasterxml.classmate.TypeResolver;
import hr.tvz.groops.constants.ProfileConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.AlternateTypeBuilder;
import springfox.documentation.builders.AlternateTypePropertyBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.Cookie;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

    private final String BASE_URL_DEFAULT;
    private final String authHeader;
    private static final String JWT_REFERENCE = "JWT";

    @Autowired
    public SpringFoxConfig(@Value("${groops.hostname.base.default:localhost:8080/}") String BASE_URL_DEFAULT,
                           @Value("${groops.jwt.header-name}") String authHeader) {
        this.BASE_URL_DEFAULT = BASE_URL_DEFAULT;
        this.authHeader = authHeader;
    }

    @Profile(ProfileConstants.DEV)
    @Bean
    public Docket apiDev(TypeResolver resolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("xxx")
                //Some other code unrelated to this problem
                .alternateTypeRules(
                        // Rule to correctly process Optional<Instant> variables
                        // and generate "type: string, format: date-time", as for Instant variables,
                        // instead of "$ref" : "#/definitions/Instant"
                        AlternateTypeRules.newRule(
                                resolver.resolve(Optional.class, Instant.class),
                                resolver.resolve(String.class),
                                Ordered.HIGHEST_PRECEDENCE
                        ))
                .genericModelSubstitutes(Optional.class)
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(defaultSecurityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("hr.tvz.groops.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Profile(ProfileConstants.PROD)
    @Bean
    public Docket apiProd(TypeResolver resolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("xxx")
                //Some other code unrelated to this problem
                .alternateTypeRules(
                        // Rule to correctly process Optional<Instant> variables
                        // and generate "type: string, format: date-time", as for Instant variables,
                        // instead of "$ref" : "#/definitions/Instant"
                        AlternateTypeRules.newRule(
                                resolver.resolve(Optional.class, Instant.class),
                                resolver.resolve(String.class),
                                Ordered.HIGHEST_PRECEDENCE
                        ))
                .genericModelSubstitutes(Optional.class)
                .apiInfo(apiInfo())
                .ignoredParameterTypes(Cookie.class)
                .useDefaultResponseMessages(false)
                .host(BASE_URL_DEFAULT)
                .select()
                .apis(RequestHandlerSelectors.basePackage("hr.tvz.groops.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private SecurityContext defaultSecurityContext() {
        return SecurityContext.builder().securityReferences(referenceAuth(JWT_REFERENCE)).build();
    }

    private ApiKey apiKey() {
        return new ApiKey(JWT_REFERENCE, this.authHeader, "header");
    }

    private List<SecurityReference> referenceAuth(String reference) {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference(reference, authorizationScopes));
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Groops project api",
                "API for groops.",
                "1.0",
                "Terms of service",
                new Contact("John Doe", "https://www.example.com/", "example@mail.com"),
                "License of API",
                "API license URL",
                Collections.emptyList());
    }

    @Bean
    public AlternateTypeRuleConvention pageableConvention(
            final TypeResolver resolver) {
        return new AlternateTypeRuleConvention() {

            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                return Arrays.asList(
                        newRule(resolver.resolve(Pageable.class), resolver.resolve(pageableMixin()))
                );
            }
        };
    }

    private Type pageableMixin() {
        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(
                        String.format("%s.generated.%s",
                                Pageable.class.getPackage().getName(),
                                Pageable.class.getSimpleName()))
                .withProperties(Arrays.asList(
                        property(Integer.class, "page"),
                        property(Integer.class, "size"),
                        property(String.class, "sort")
                ))
                .build();
    }

    private AlternateTypePropertyBuilder property(Class<?> type, String name) {
        return new AlternateTypePropertyBuilder()
                .withName(name)
                .withType(type)
                .withCanRead(true)
                .withCanWrite(true);
    }

}
