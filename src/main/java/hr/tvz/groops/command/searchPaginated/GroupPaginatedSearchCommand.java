package hr.tvz.groops.command.searchPaginated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GroupPaginatedSearchCommand extends BaseEntitySearchCommand {
    private Optional<String> name;
    private Optional<Boolean> isMember;
}
