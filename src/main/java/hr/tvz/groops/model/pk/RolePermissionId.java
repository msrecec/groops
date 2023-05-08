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
public class RolePermissionId implements Serializable {
    private Long roleId;
    private Long permissionId;
}
