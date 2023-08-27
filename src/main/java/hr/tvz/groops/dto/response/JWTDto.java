package hr.tvz.groops.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class JWTDto extends LoginDto {
    private String tokenB64;
}
