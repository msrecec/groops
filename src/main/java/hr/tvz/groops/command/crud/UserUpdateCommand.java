package hr.tvz.groops.command.crud;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserUpdateCommand extends UserCommand {
    private String password;
}
