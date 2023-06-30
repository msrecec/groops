package hr.tvz.groops.model;

import jakarta.persistence.*;
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
public class Group extends NamedEntity {
    @SequenceGenerator(name = "group_id_seq", sequenceName = "group_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @ManyToMany(targetEntity = User.class)
    @JoinTable(
            name = "user_group_role",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private List<User> users;
}
