package hr.tvz.groops.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "direct_message", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class DirectMessage extends BaseEntity {
    @SequenceGenerator(name = "direct_message_id_seq", sequenceName = "direct_message_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "direct_message_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @Column(name = "message")
    private String message;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", referencedColumnName = "id")
    private Friend friend;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    private User recipient;
}
