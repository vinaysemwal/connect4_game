package com.gluck.gaming.service.exception;

/**
 * Exception thrown when same player tries to play consecutive turns.
 *
 * @author Vinay Semwal
 */
public class ConsecutiveTurnsNotAllowedException extends BaseConnect4ServiceException {

    private static final long serialVersionUID = 3132682775396954403L;

    /**
     * @param message exception description
     */
    public ConsecutiveTurnsNotAllowedException(final String message) {
        super(message);
    }

}
