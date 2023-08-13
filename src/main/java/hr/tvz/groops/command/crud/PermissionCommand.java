package hr.tvz.groops.command.crud;

import hr.tvz.groops.model.enums.PermissionEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PermissionCommand {
    private PermissionEnum permission;
}
