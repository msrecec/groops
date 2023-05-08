package hr.tvz.groops.command.crud;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ForgotPasswordCommand {
    @NotBlank(message = "username is required")
    private String username;
}
