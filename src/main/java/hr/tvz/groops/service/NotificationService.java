package hr.tvz.groops.service;

import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.model.Notification;
import hr.tvz.groops.model.User;
import hr.tvz.groops.model.enums.EntityTypeEnum;
import hr.tvz.groops.repository.NotificationRepository;
import hr.tvz.groops.service.security.AuthenticationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuthenticationService authenticationService;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(SimpMessagingTemplate simpMessagingTemplate, AuthenticationService authenticationService, NotificationRepository notificationRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.authenticationService = authenticationService;
        this.notificationRepository = notificationRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY, timeout = TimeoutConstants.TINY_TIMEOUT)
    public void sendNotificationToUser(User user, String message, Long entityId, EntityTypeEnum entityType, Long relatedEntityId) {
        Instant now = now();
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .entityId(entityId)
                .entityType(entityType)
                .relatedEntityId(relatedEntityId)
                .read(false)
                .createdTs(now)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .build();

        notificationRepository.save(notification);

        sendNotificationAfterSuccessfulCommit(user.getId());
    }

    @Transactional(propagation = Propagation.MANDATORY, timeout = TimeoutConstants.TINY_TIMEOUT)
    public void sendNotificationToUser(User user, String message, Long entityId, EntityTypeEnum entityType) {
        sendNotificationToUser(user, message, entityId, entityType, null);
    }

    private void sendNotificationAfterSuccessfulCommit(@NotNull Long userId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                simpMessagingTemplate.convertAndSend("/ws/secured/notifications/user/" + userId, "ping");
            }
        });
    }

}
