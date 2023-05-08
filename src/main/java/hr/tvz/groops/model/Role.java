package hr.tvz.groops.model;

import hr.tvz.groops.model.enums.RoleEnum;
import javax.persistence.*;
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
    @ManyToMany(targetEntity = Permission.class)
    @JoinTable(
            name = "role_permission",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")}
    )
    private List<Permission> permissions;
    @ManyToMany(targetEntity = UserGroup.class, mappedBy = "roles")
    private List<UserGroup> userGroups;
    @OneToMany(targetEntity = RolePermission.class, mappedBy = "role")
    private List<RolePermission> rolePermissions;
}
