package hr.tvz.groops.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "post", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class Post extends BaseEntity {
    @SequenceGenerator(name = "post_id_seq", sequenceName = "post_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @Column(name = "media_key")
    private String mediaKey;
    @Column(name = "media_thumbnail_key")
    private String mediaThumbnailKey;
    @Column(name = "text")
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;
}
