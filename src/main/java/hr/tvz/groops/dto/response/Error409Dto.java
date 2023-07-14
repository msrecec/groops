package hr.tvz.groops.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Error409Dto extends ErrorDto {
    private String constraint;

    public Error409Dto(Boolean success, String constraint, String message) {
        super(success, message);
        this.constraint = constraint;
    }
}
