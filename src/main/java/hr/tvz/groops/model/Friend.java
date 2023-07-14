package hr.tvz.groops.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "friend", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class Friend extends BaseEntity {
    @SequenceGenerator(name = "friend_id_seq", sequenceName = "friend_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_user_id", referencedColumnName = "id")
    private User firstUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_user_id", referencedColumnName = "id")
    private User secondUser;
}
