package hr.tvz.groops.model;

import hr.tvz.groops.model.enums.EntityTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "notification", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Notification extends BaseEntity {
    @SequenceGenerator(name = "notification_id_seq", sequenceName = "notification_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id_seq")
    @Column(name = "id")
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "message")
    private String message;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @Column(name = "read")
    private Boolean read;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private EntityTypeEnum entityType;

}
