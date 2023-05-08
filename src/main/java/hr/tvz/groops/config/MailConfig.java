package hr.tvz.groops.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    private final String host;
    private final Integer port;
    private final String username;
    private final String password;
    private final String protocol;
    private final Boolean auth;
    private final Boolean tls;
    private final Boolean debug;

    public MailConfig(@Value("${spring.mail.host}") String host,
                      @Value("${spring.mail.port}") Integer port,
                      @Value("${spring.mail.username}") String username,
                      @Value("${spring.mail.password}") String password,
                      @Value("${spring.mail.protocol}") String protocol,
                      @Value("${spring.mail.properties.mail.smtp.auth}") Boolean auth,
                      @Value("${spring.mail.properties.mail.smtp.starttls.enable}") Boolean tls,
                      @Value("${spring.mail.properties.debug}") Boolean debug) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.protocol = protocol;
        this.auth = auth;
        this.tls = tls;
        this.debug = debug;
    }

    @Bean("javaMailSender")
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.host);
        mailSender.setPort(this.port);

        mailSender.setUsername(this.username);
        mailSender.setPassword(this.password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", this.protocol);
        props.put("mail.smtp.auth", this.auth);
        props.put("mail.smtp.starttls.enable", this.tls);
        props.put("mail.debug", this.debug);

        return mailSender;
    }
}
