package hr.tvz.groops.command.crud;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentCommand {
    @NotBlank(message = "Text must not be null")
    private String text;
}
