package hr.tvz.groops.model;

import javax.persistence.*;

import hr.tvz.groops.model.pk.PostLikeId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "post_like", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PostLike extends BaseEntity {
    @EmbeddedId
    private PostLikeId postLikeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    @MapsId(value = "postId")
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @MapsId(value = "userId")
    private User user;
}
