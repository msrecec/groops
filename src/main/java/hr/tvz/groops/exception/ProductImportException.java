package hr.tvz.groops.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ProductImportException extends RuntimeException {
    private Collection<String> existingProducts;
    private String errors;

    public ProductImportException(Collection<String> existingProducts, String errorMessage) {
        this.existingProducts = existingProducts;
        this.errors = errorMessage;
    }

    public ProductImportException(Collection<String> existingProducts) {
        this.existingProducts = existingProducts;
        this.errors = null;
    }
}
