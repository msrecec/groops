package hr.tvz.groops.command.crud;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GroupCommand {
    @NotBlank(message = "name is required")
    private String name;
}
