package hr.tvz.groops.command.crud;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmailUpdateCommand {
    @NotBlank(message = "email is required")
    @Email(message = "format must be a valid email")
    private String email;
}
