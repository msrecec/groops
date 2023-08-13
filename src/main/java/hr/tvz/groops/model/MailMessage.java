package hr.tvz.groops.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "mail_message", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class MailMessage extends BaseEntity {
    @SequenceGenerator(name = "mail_message_id_seq", sequenceName = "mail_message_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mail_message_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    
    @Column(name = "subject")
    private String subject;

    @Column(name = "html_message")
    private String htmlMessage;

    @Column(name = "txt_message")
    private String txtMessage;

    @OneToMany(targetEntity = Mail.class, mappedBy = "mailMessage")
    private List<Mail> mails;
}
