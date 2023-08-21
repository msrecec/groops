package hr.tvz.groops.command.crud;

import hr.tvz.groops.model.enums.RoleEnum;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoleCommand {
    private RoleEnum role;
}
