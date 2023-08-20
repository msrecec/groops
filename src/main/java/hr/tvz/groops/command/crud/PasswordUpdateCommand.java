package hr.tvz.groops.command.crud;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PasswordUpdateCommand {
    @NotBlank(message = "password is required")
    private String password;
}
