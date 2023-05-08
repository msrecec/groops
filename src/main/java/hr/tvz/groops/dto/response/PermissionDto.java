package hr.tvz.groops.dto.response;

import hr.tvz.groops.model.enums.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class PermissionDto extends DtoBase {
    private Long id;
    private PermissionEnum permission;
}
