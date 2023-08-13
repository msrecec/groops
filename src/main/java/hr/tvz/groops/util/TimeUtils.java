package hr.tvz.groops.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    public static Instant now() {
        return Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
