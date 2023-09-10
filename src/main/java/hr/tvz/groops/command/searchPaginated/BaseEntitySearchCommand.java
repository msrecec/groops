package hr.tvz.groops.command.searchPaginated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseEntitySearchCommand extends IdCommand {
    private Optional<Instant> createdTsFrom;
    private Optional<Instant> createdTsTo;
    private Optional<Instant> modifiedTsFrom;
    private Optional<Instant> modifiedTsTo;
    private Optional<String> createdBy;
    private Optional<String> modifiedBy;

}
