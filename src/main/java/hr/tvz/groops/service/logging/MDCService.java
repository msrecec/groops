package hr.tvz.groops.service.logging;


import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MDCService {
    private final String loggingUsername;

    @Autowired
    public MDCService(@Value("${groops.logging.placeholder.username}") String loggingUsername) {
        this.loggingUsername = loggingUsername;
    }

    public void setMDCUsername(String username) {
        MDC.put(this.loggingUsername, "user: " + username + " --- ");
    }

    public void clearMDCUsername() {
        MDC.remove(this.loggingUsername);
    }
}
