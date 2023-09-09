package hr.tvz.groops.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "group", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "id", columnNames = "id"),
        @UniqueConstraint(name = "name", columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class Group extends BaseEntity {
    @SequenceGenerator(name = "group_id_seq", sequenceName = "group_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "profile_picture_key")
    private String profilePictureKey;
    @Column(name = "profile_picture_thumbnail_key")
    private String profilePictureThumbnailKey;
    @ManyToMany(targetEntity = User.class, mappedBy = "groups")
    private List<User> users;
    @OneToMany(targetEntity = GroupMessage.class, mappedBy = "group")
    private List<GroupMessage> groupMessages;
    @OneToMany(targetEntity = Post.class, mappedBy = "group")
    private List<Post> posts;
}
