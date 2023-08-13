package hr.tvz.groops.command.crud;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCreateCommand {
    @NotBlank(message = "password is required")
    private String password;
}
