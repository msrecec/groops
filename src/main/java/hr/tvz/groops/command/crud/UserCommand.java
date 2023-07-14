package hr.tvz.groops.command.crud;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.util.Optional;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCommand {
    private Optional<@NotBlank(message = "username is required") String> username;
    private Optional<@NotBlank(message = "email is required") @Email(message = "format must be a valid email") String> email;
    private Optional<@NotBlank(message = "password is required") String> password;
    private Optional<@NotBlank(message = "first name is required") String> firstName;
    private Optional<@NotBlank(message = "last name is required") String> lastName;
    private Optional<@NotNull(message = "date of birth is required") Date> dateOfBirth;
    private Optional<String> description;
}
