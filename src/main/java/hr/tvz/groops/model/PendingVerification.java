package hr.tvz.groops.model;

import hr.tvz.groops.model.enums.VerificationTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "pending_verification", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class PendingVerification extends BaseEntity {
    @SequenceGenerator(name = "pending_verification_id_seq", sequenceName = "pending_verification_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pending_verification_id_seq")
    @Column(name = "id")
    @Id
    private Long id;

    @Column(name = "verification_type")
    @Enumerated(EnumType.STRING)
    private VerificationTypeEnum verificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
