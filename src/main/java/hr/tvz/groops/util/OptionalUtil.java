package hr.tvz.groops.util;

import java.util.Optional;

public class OptionalUtil {
    public static <T> T getOrNull(Optional<T> value) {
        return value.orElse(null);
    }

    public static <T> Optional<T> getNonEmptyOptionalOrNull(T value) {
        return value != null ? Optional.of(value) : null;
    }
}
