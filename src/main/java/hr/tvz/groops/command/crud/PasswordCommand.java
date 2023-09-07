package hr.tvz.groops.command.crud;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PasswordCommand {
    @NotBlank(message = "First password must not be blank")
    private String password1;
    @NotBlank(message = "Second password must not be blank")
    private String password2;
}
