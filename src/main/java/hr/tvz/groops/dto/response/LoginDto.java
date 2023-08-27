package hr.tvz.groops.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class LoginDto {
    private Instant exp;
}
