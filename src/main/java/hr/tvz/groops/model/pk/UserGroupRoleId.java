package hr.tvz.groops.model.pk;

import javax.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class UserGroupRoleId implements Serializable {
    private Long userGroupId;
    private Long roleId;
}
