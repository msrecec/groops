package hr.tvz.groops.exception;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class NotificationServiceException extends Exception {
    private final Exception causingException;

    public NotificationServiceException(@NotNull Exception causingException) {
        super(ExceptionEnum.NOTIFICATION_EXCEPTION.getFullMessage(), causingException);
        this.causingException = causingException;
    }
}
