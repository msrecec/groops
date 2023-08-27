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
public class CommentDto extends DtoBase {
    private Long id;
    private String text;
    private UserDto user;
    private PostDto post;
}
