package hr.tvz.groops.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "user_group", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class UserGroup extends BaseEntity {
    @SequenceGenerator(name = "user_group_id_seq", sequenceName = "user_group_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_group_id_seq")
    @Column(name = "id")
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;
    @ManyToMany(targetEntity = Role.class)
    @JoinTable(
            name = "user_group_role",
            joinColumns = {@JoinColumn(name = "user_group_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private List<Role> roles;
    @OneToMany(targetEntity = UserGroupRole.class, mappedBy = "userGroup")
    private List<UserGroupRole> userGroupRoles;
}
