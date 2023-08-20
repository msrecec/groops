package hr.tvz.groops.command.crud;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCreateCommand extends UserCommand {
    @NotBlank(message = "email is required")
    @Email(message = "format must be a valid email")
    private String email;
    @NotBlank(message = "password is required")
    private String password;
}
