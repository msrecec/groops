package hr.tvz.groops.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class FriendRequestDto extends DtoBase {
    private UserDto sender;
    private UserDto recipient;
}
