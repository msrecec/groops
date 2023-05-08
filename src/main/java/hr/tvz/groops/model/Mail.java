package hr.tvz.groops.model;

import hr.tvz.groops.model.enums.MailStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "mail", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Mail extends BaseEntity {
    @SequenceGenerator(name = "mail_id_seq", sequenceName = "mail_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mail_id_seq")
    @Column(name = "id")
    @Id
    private Long id;

    @OneToOne(mappedBy = "mail",fetch = FetchType.LAZY,  cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private MailExceptionLog mailExceptionLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mail_message_id", referencedColumnName = "id")
    private MailMessage mailMessage;

    @Column(name = "mail_status")
    @Enumerated(EnumType.STRING)
    private MailStatusEnum mailStatus;

    @Column(name = "expires")
    private Instant expires;

}
