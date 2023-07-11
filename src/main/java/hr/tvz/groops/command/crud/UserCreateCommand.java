package hr.tvz.groops.command.crud;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Getter
@Setter
@SuperBuilder
@ToString
public class UserCreateCommand extends UserCommand {

    public UserCreateCommand() {
    }

    public UserCreateCommand(String username, String email, String firstName, String lastName, Date dateOfBirth, String description, String password) {
        super(username, firstName, lastName, dateOfBirth, description);
        this.email = email;
        this.password = password;
    }

    @NotBlank(message = "email is required")
    @Email(message = "format must be a valid email")
    private String email;
    @NotBlank(message = "password is required")
    private String password;
}
