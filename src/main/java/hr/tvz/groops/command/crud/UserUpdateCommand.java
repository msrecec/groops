package hr.tvz.groops.command.crud;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Getter
@Setter
@SuperBuilder
@ToString
public class UserUpdateCommand extends UserCommand {

    public UserUpdateCommand() {
    }

    public UserUpdateCommand(String username, String email, String firstName, String lastName, Date dateOfBirth, String description) {
        super(username, email, firstName, lastName, dateOfBirth, description);
    }
}
