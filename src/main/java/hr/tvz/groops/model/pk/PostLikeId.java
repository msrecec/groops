package hr.tvz.groops.model.pk;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class PostLikeId implements Serializable {
    private Long postId;
    private Long userId;
}
