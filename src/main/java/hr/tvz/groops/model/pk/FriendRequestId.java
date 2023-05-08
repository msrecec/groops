package hr.tvz.groops.model.pk;

import javax.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class FriendRequestId implements Serializable {
    private Long senderId;
    private Long recipientId;
}
