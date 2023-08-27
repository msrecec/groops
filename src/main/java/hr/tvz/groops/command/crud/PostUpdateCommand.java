package hr.tvz.groops.command.crud;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostUpdateCommand extends PostCommand {
    @NotNull
    private Boolean removeMedia;
}
