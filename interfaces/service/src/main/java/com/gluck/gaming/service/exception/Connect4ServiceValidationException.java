package com.gluck.gaming.service.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception thrown in case request failed validation checks.
 * 
 * @author Vinay Semwal
 */
public class Connect4ServiceValidationException extends BaseConnect4ServiceException {

    private static final long serialVersionUID = 1L;

    private List<String> errors = null;

    /**
     * @param message reason message for the exception.
     */
    public Connect4ServiceValidationException(final String message) {
        super(message);
        errors = new ArrayList<>();
    }

    /**
     * @param message specific exception of request just has one error.
     * @param errors list of error messages for each incorrect request data.
     */
    public Connect4ServiceValidationException(final String message, final List<String> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * @return the errors
     */
    public List<String> getErrors() {
        if (errors == null) {
            return Collections.emptyList();
        }
        return errors;
    }
}
