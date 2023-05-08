package hr.tvz.groops.model;

import javax.persistence.*;
import javax.validation.constraints.Email;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "user", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "id", columnNames = "id"),
        @UniqueConstraint(name = "username", columnNames = "username")
})
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class User extends BaseEntity {
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "password_hash")
    private String passwordHash;
    @Column(name = "email")
    @Email(message = "Value is not a valid email")
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "description")
    private String description;
    @Column(name = "profile_picture_key")
    private String profilePictureKey;
    @Column(name = "profile_picture_thumbnail_key")
    private String profilePictureThumbnailKey;
    @Column(name = "verified")
    private Boolean verified;
    @Column(name = "token_issued_at")
    private Integer tokenIssuedAt;
    @OneToMany(targetEntity = GroupRequest.class, mappedBy = "user")
    private List<GroupRequest> groupRequests;
    @ManyToMany(targetEntity = Group.class)
    @JoinTable(
            name = "user_group",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")}
    )
    private List<Group> groups;
    @OneToMany(targetEntity = FriendRequest.class, mappedBy = "sender")
    private List<FriendRequest> sentFriendRequests;
    @OneToMany(targetEntity = FriendRequest.class, mappedBy = "recipient")
    private List<FriendRequest> receivedFriendRequests;
    @OneToMany(targetEntity = PendingVerification.class, mappedBy = "user")
    private List<PendingVerification> pendingVerifications;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(
            name = "friend",
            joinColumns = {@JoinColumn(name = "first_user_id")},
            inverseJoinColumns = {@JoinColumn(name = "second_user_id")}
    )
    private List<User> primaryFriends;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(
            name = "friend",
            joinColumns = {@JoinColumn(name = "second_user_id")},
            inverseJoinColumns = {@JoinColumn(name = "first_user_id")}
    )
    private List<User> secondaryFriends;
    @OneToMany(targetEntity = DirectMessage.class, mappedBy = "sender")
    private List<DirectMessage> sentDirectMessages;
    @OneToMany(targetEntity = DirectMessage.class, mappedBy = "recipient")
    private List<DirectMessage> receivedDirectMessages;
    @OneToMany(targetEntity = GroupMessage.class, mappedBy = "sender")
    private List<GroupMessage> sentGroupMessages;
    @OneToMany(targetEntity = Post.class, mappedBy = "user")
    private List<Post> posts;
    @OneToMany(targetEntity = Comment.class, mappedBy = "user")
    private List<Comment> comments;
    @OneToMany(targetEntity = PostLike.class, mappedBy = "user")
    private List<PostLike> postLikes;
}
