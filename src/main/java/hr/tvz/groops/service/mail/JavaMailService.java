package hr.tvz.groops.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Service
public class JavaMailService implements MailClientService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public JavaMailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendTo(String sender, String recipient, String subject, String htmlMessage, String txtMessage) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(sender);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(txtMessage, htmlMessage);
        helper.setSentDate(new Date());
        helper.setValidateAddresses(true);

        javaMailSender.send(mimeMessage);
    }
}
