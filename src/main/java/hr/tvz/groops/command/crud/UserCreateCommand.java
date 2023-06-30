package hr.tvz.groops.command.crud;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Getter
@Setter
@SuperBuilder
@ToString
public class UserCreateCommand extends UserCommand {

    public UserCreateCommand(String username, String email, String firstName, String lastName, Date dateOfBirth, String description, String password) {
        super(username, email, firstName, lastName, dateOfBirth, description);
        this.password = password;
    }

    @NotBlank(message = "password is required")
    private String password;
}
