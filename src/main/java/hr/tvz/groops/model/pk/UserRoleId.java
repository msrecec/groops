package hr.tvz.groops.model.pk;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class UserRoleId implements Serializable {
    private Long userId;
    private Long roleId;
}
