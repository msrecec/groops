package hr.tvz.groops.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "mail_exception_log", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class MailExceptionLog extends BaseEntity {
    @Id
    @Column(name = "mail_id", updatable = false, nullable = false)
    private Long mailId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mail_id", referencedColumnName = "id")
    private Mail mail;

    @Column(name = "message")
    private String message;

    @Column(name = "stack_trace")
    private String stackTrace;
}
