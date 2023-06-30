package hr.tvz.groops.model;

import hr.tvz.groops.model.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "role", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "id", columnNames = "id"),
        @UniqueConstraint(name = "role", columnNames = "role")
})
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class Role extends BaseEntity {
    @SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    @ManyToMany(targetEntity = Permission.class, mappedBy = "roles")
    private List<Permission> permissions;
    @ManyToMany(targetEntity = User.class)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private List<User> users;
}
