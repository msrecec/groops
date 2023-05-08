package hr.tvz.groops.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GroupRoleDto {
    private Long groupId;
    private List<RoleDto> roles;
}
