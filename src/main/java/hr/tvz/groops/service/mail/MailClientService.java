package hr.tvz.groops.service.mail;

import javax.mail.MessagingException;

public interface MailClientService {
    void sendTo(String sender, String recipient, String subject, String htmlMessage, String txtMessage) throws MessagingException;
}
