package hr.tvz.groops.command.validator;

import hr.tvz.groops.util.SecurityUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class CustomPasswordValidatorHandler implements ConstraintValidator<CustomPasswordValidator, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null || password.isBlank()) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("password is required *").addConstraintViolation();
            return false;
        }
        List<String> exceptionMessages = new ArrayList<>();
        if (SecurityUtil.isValid(password, exceptionMessages)) {
            return true;
        }
        constraintValidatorContext.buildConstraintViolationWithTemplate(String.join(";", exceptionMessages)).addConstraintViolation();
        return false;
    }
}
