package hr.tvz.groops.command.crud;

import hr.tvz.groops.model.enums.RoleEnum;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoleCommand {
    @NotNull
    private RoleEnum role;
}
