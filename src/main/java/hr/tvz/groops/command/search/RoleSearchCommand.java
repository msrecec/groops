package hr.tvz.groops.command.search;

import hr.tvz.groops.model.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RoleSearchCommand extends BaseEntitySearchCommand {
    Optional<RoleEnum> role;
}