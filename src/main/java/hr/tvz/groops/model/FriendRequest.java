package hr.tvz.groops.model;

import hr.tvz.groops.model.pk.FriendRequestId;
import hr.tvz.groops.model.pk.GroupRequestId;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "friend_request", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class FriendRequest extends BaseEntity {
    @EmbeddedId
    private FriendRequestId friendRequestId;
    @MapsId(value = "senderId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;
    @MapsId(value = "recipientId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    private User recipient;


}
