package hr.tvz.groops.event.notification;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;

@Getter
public abstract class NotificationEvent extends ApplicationEvent {
    @NotNull
    private final Long userId;

    public NotificationEvent(Object source, @NotNull Long userId) {
        super(source);
        this.userId = userId;
    }
}
