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
public class GroupDto extends DtoBase {
    private Long id;
    private String name;
    private String profilePictureDownloadLink;
    private String profilePictureThumbnailDownloadLink;
    private Boolean my;
    private Boolean sentJoin;
}
