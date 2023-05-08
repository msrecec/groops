package hr.tvz.groops.dto.response;

import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class DtoBase {
    private Instant createdTs;
    private Instant modifiedTs;
    private String createdBy;
    private String modifiedBy;
}
