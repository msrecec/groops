package hr.tvz.groops.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    public static Instant now() {
        return Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
