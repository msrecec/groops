package hr.tvz.groops.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {
    private Boolean success;
    private String message;
    private Integer status;

    public ErrorDto(Boolean success, String message, Integer status) {
        this.success = success;
        this.message = message;
        this.status = status;
    }

    public ErrorDto() {
    }
}