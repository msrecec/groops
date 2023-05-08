package hr.tvz.groops.model;

import hr.tvz.groops.model.pk.UserGroupRoleId;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_group_role", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class UserGroupRole extends BaseEntity {
    @EmbeddedId
    private UserGroupRoleId userGroupRoleId;
    @MapsId(value = "userGroupId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_group_id", referencedColumnName = "id")
    private UserGroup userGroup;
    @MapsId(value = "roleId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

}
