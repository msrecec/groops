package hr.tvz.groops.model;

import hr.tvz.groops.model.enums.PermissionEnum;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "permission", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "id", columnNames = "id"),
        @UniqueConstraint(name = "permission", columnNames = "permission")
})
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class Permission extends BaseEntity {
    @SequenceGenerator(name = "permission_id_seq", sequenceName = "permission_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private PermissionEnum permission;
    @ManyToMany(targetEntity = Role.class, mappedBy = "permissions")
    private List<Role> roles;
}
