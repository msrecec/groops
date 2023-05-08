package hr.tvz.groops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "permission", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "id", columnNames = "id"),
        @UniqueConstraint(name = "name", columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class Permission extends NamedEntity {
    @SequenceGenerator(name = "permission_id_seq", sequenceName = "permission_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @ManyToMany(targetEntity = Role.class)
    @JoinTable(
            name = "role_permission",
            joinColumns = {@JoinColumn(name = "role_permission_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")}
    )
    private List<Role> roles;
}
