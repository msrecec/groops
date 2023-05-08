package hr.tvz.groops.dto.response;

import hr.tvz.groops.model.enums.EntityTypeEnum;
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
public class NotificationDto extends DtoBase {
    private Long id;
    private UserDto user;
    private String message;
    private Long entityId;
    private Long relatedEntityId;
    private EntityTypeEnum entityType;
    private Boolean read;
}
