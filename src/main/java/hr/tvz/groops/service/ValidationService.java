package hr.tvz.groops.service;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ValidationService {
    public <T> void validate(T value) {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<T>> validateResult = validator.validate(value);
        if (validateResult.isEmpty()) {
            return;
        }
        String message = validateResult.stream().filter(v -> v.getMessage() != null)
                .map(validation -> validation.getMessage().trim())
                .collect(Collectors.joining(";"));
        throw new IllegalArgumentException(message);
    }
}
