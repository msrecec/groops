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
public class PostDto extends DtoBase {
    private Long id;
    private String mediaKey;
    private String text;
    private UserDto user;
    private GroupDto group;
    private String mediaDownloadLink;
    private String mediaThumbnailDownloadLink;
    private Integer likeCount = 0;
    private Boolean youLike = false;
    private Integer commentCount;
}
