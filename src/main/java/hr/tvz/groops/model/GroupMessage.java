package hr.tvz.groops.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "group_message", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class GroupMessage extends BaseEntity {
    @SequenceGenerator(name = "group_message_id_seq", sequenceName = "group_message_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_message_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @Column(name = "message")
    private String message;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;
}
