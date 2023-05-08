package hr.tvz.groops.command.searchPaginated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserPaginatedSearchCommand extends BaseEntitySearchCommand {
    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> firstName;
    private Optional<String> lastName;
    private Optional<Date> dateOfBirthFrom;
    private Optional<Date> dateOfBirthTo;
    private Optional<String> description;
}
