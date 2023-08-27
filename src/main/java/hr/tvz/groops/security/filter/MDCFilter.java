package hr.tvz.groops.security.filter;

import hr.tvz.groops.service.logging.MDCService;
import hr.tvz.groops.util.SecurityUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MDCFilter extends OncePerRequestFilter {

    private final MDCService mdcService;

    @Autowired
    public MDCFilter(MDCService mdcService) {
        this.mdcService = mdcService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (!SecurityUtil.hasAuthentication() || !SecurityUtil.hasPrincipal()) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            mdcService.setMDCUsername(SecurityUtil.getCurrentLoggedInUserUsername());
            filterChain.doFilter(request, response);
        } finally {
            mdcService.clearMDCUsername();
        }
    }
}
