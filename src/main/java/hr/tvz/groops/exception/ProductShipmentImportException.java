package hr.tvz.groops.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ProductShipmentImportException extends RuntimeException {
    private String errors;
    private Collection<String> nonMatchingLotProducts;

    public ProductShipmentImportException(String errors, Collection<String> nonMatchingLotProducts) {
        this.errors = errors;
        this.nonMatchingLotProducts = nonMatchingLotProducts;
    }

    public ProductShipmentImportException(Collection<String> nonMatchingLotProducts) {
        this.errors = null;
        this.nonMatchingLotProducts = nonMatchingLotProducts;
    }
}
