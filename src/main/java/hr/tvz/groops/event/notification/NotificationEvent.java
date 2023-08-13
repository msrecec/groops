package hr.tvz.groops.event.notification;

import org.springframework.context.ApplicationEvent;

public abstract class NotificationEvent extends ApplicationEvent {

    public NotificationEvent(Object source) {
        super(source);
    }
}
