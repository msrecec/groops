package hr.tvz.groops.command.crud;

import hr.tvz.groops.model.enums.RoleEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoleCommand {
    private Optional<RoleEnum> role;
}
