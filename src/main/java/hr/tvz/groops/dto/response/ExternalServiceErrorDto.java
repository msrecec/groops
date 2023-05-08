package hr.tvz.groops.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalServiceErrorDto extends ErrorDto {

    public ExternalServiceErrorDto(Boolean success, String message, String serviceResponse, Integer status) {
        super(success, message, status);
        this.serviceResponse = serviceResponse;
    }

    @JsonRawValue
    private String serviceResponse;
}
