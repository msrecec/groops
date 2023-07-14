package hr.tvz.groops.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class DuplicateImportException extends RuntimeException {
    private Collection<String> duplicates;

    public DuplicateImportException(Collection<String> duplicates) {
        this.duplicates = duplicates;
    }
}
