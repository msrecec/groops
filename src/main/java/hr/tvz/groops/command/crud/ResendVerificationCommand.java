package hr.tvz.groops.command.crud;

import hr.tvz.groops.model.enums.VerificationTypeEnum;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResendVerificationCommand {
    @NotNull
    private VerificationTypeEnum verificationType;
}
