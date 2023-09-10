package hr.tvz.groops.websocket;

import hr.tvz.groops.constants.JWTConstants;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.exception.UnauthorizedException;
import hr.tvz.groops.model.User;
import hr.tvz.groops.repository.UserRepository;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import hr.tvz.groops.security.token.JwtConfig;
import hr.tvz.groops.service.security.AuthenticationService;
import hr.tvz.groops.service.token.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static hr.tvz.groops.util.SecurityUtil.getRoles;

@Component("authenticationChannelInterceptor")
public class AuthenticationChannelInterceptorAdapter implements ChannelInterceptor, Searchable {
    private static final String invalidTokenErrorMessage = "Token %s cannot be trusted";
    private final UserRepository userRepository;
    private final JWTService appJWTService;
    private final AuthenticationService authenticationService;
    private final JwtConfig appJwtConfig;

    @Autowired
    public AuthenticationChannelInterceptorAdapter(UserRepository userRepository,
                                                   JWTService appJWTService,
                                                   AuthenticationService authenticationService,
                                                   JwtConfig appJwtConfig) {
        this.userRepository = userRepository;
        this.appJWTService = appJWTService;
        this.authenticationService = authenticationService;
        this.appJwtConfig = appJwtConfig;
    }

    // todo check if this runs when we send messages internally

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new InternalServerException("Missing accessor");
        }
        if (isNotPermittedStompCommand(accessor.getCommand())) {
            throw new UnauthorizedException("Unauthorized");
        }
        if (isNotAuthenticatedStompCommand(accessor.getCommand())) {
            return message;
        }

        String header = appJwtConfig.getHeaderName();
        String token = accessor.getFirstNativeHeader(header);
        if (token == null) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (appJwtConfig.getTokenPrefix() == null) {
            throw new InternalServerException("Missing token prefix");
        }
        validate(token);
        authenticate(accessor);
        return message;
    }

    private void authenticate(StompHeaderAccessor accessor) {
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        Optional<User> currentUser = userRepository.findById(currentUserId);
        if (currentUser.isEmpty()) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (!currentUser.get().getVerified()) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (accessor.getCommand() != StompCommand.SUBSCRIBE) {
            return;
        }
        if (accessor.getDestination() == null) {
            throw new AccessDeniedException("Unauthorized");
        }
        String[] tokens = accessor.getDestination().split("/");
        if (tokens.length != 6) {
            throw new AccessDeniedException("Unauthorized");
        }
        String idToken = tokens[5];
        if (idToken == null || idToken.isBlank()) {
            throw new AccessDeniedException("Unauthorized");
        }
        Long id = Long.parseLong(idToken);
        if (currentUser.get().getId().compareTo(id) != 0) {
            throw new AccessDeniedException("Unauthorized");
        }
    }

    private boolean isNotPermittedStompCommand(StompCommand command) {
        return command == StompCommand.MESSAGE || command == StompCommand.SEND;
    }

    private boolean isNotAuthenticatedStompCommand(StompCommand command) {
        return !(command == StompCommand.CONNECT ||
                        command == StompCommand.SUBSCRIBE ||
                        command == StompCommand.MESSAGE ||
                        command == StompCommand.SEND);
    }

    private void validate(String token) {
        try {
            Jws<Claims> claimsJws = appJWTService.getClaimsFromToken(token);
            Claims body = claimsJws.getBody();
            Long id = body.get(JWTConstants.ID, Long.class);
            String username = body.get(JWTConstants.USERNAME, String.class);
            var roles = (Collection<String>) body.get(JWTConstants.ROLES);
            Set<SimpleGrantedAuthority> authorities = getRoles(roles);

            Authentication authentication = new GroopsUserDataToken(id, username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);


        } catch (JwtException e) {
            throw new IllegalStateException(String.format(invalidTokenErrorMessage, token));
        }
    }

}
