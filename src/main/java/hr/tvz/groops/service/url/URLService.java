package hr.tvz.groops.service.url;

import hr.tvz.groops.exception.InternalServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import static hr.tvz.groops.constants.ProfileConstants.DEV;
import static hr.tvz.groops.constants.ProfileConstants.PROD;

@Service
public class URLService {

    private final Environment environment;
    private final String contextPath;
    private final String hostnameBase;

    @Autowired
    public URLService(Environment environment,
                          @Value("${server.servlet.contextPath}") String contextPath,
                          @Value("${groops.hostname.base.default}") String hostnameBase) {
        this.environment = environment;
        this.contextPath = contextPath;
        this.hostnameBase = hostnameBase;
    }

    public @NotNull String getBackendBaseURL() {
        return getBaseURL(false);
    }

    public String getFrontendBaseURL() {
        return getBaseURL(true);
    }

    public String getBaseURL(boolean frontend) {
        String protocol = isProd() ? "https://" : "http://";
        if (isProd() || isDev()) {
            return !frontend ? protocol + hostnameBase + contextPath.replaceFirst("/", "") : protocol + hostnameBase;
        }
        throw new InternalServerException("Invalid profile");
    }

    private boolean isProd() {
        return environment.acceptsProfiles(Profiles.of(PROD));
    }

    private boolean isDev() {
        return environment.acceptsProfiles(Profiles.of(DEV));
    }
}
