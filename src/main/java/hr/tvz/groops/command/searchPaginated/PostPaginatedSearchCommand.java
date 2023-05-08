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
public class PostPaginatedSearchCommand extends BaseEntitySearchCommand {
    private Optional<String> text;
    private Optional<Long> groupId;
    private Optional<Long> userId;
}
