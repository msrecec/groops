package hr.tvz.groops.command.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = CustomPasswordValidatorHandler.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomPasswordValidator {
    String message() default "Password validation failed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
