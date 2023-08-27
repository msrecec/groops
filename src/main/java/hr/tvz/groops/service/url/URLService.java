package hr.tvz.groops.service.url;

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
    private final String serverPort;

    @Autowired
    public URLService(Environment environment,
                          @Value("${server.servlet.contextPath}") String contextPath,
                          @Value("${groops.hostname.base.default}") String hostnameBase,
                          @Value("${server.port}") String serverPort) {
        this.environment = environment;
        this.contextPath = contextPath;
        this.hostnameBase = hostnameBase;
        this.serverPort = serverPort;
    }

    public @NotNull String getApplicationBaseURL() {
        String protocol = isProd() ? "https://" : "http://";
        if (isProd()) {
            return protocol + hostnameBase + contextPath.replaceFirst("/", "");
        } else if (isDev()) {
            return protocol + "localhost:" + serverPort + contextPath;
        }
        return protocol + "localhost:" + serverPort + contextPath;
    }

    private boolean isProd() {
        return environment.acceptsProfiles(Profiles.of(PROD));
    }

    private boolean isDev() {
        return environment.acceptsProfiles(Profiles.of(DEV));
    }
}
