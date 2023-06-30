package hr.tvz.groops.command.crud;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Getter
@Setter
@SuperBuilder
@ToString
public abstract class UserCommand {
    public UserCommand() {
    }

    public UserCommand(String username, String email, String firstName, String lastName, Date dateOfBirth, String description) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.description = description;
    }

    @NotBlank(message = "username is required")
    private String username;
    @NotBlank(message = "email is required")
    @Email(message = "format must be a valid email")
    private String email;
    @NotBlank(message = "first name is required")
    private String firstName;
    @NotBlank(message = "last name is required")
    private String lastName;
    @NotNull(message = "date of birth is required")
    private Date dateOfBirth;
    private String description;
}
