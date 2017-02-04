package com.gluck.gaming.service.exception;

/**
 * Exception thrown when game is not a valid state to play a turn.
 *
 * @author Vinay Semwal
 */
public class InvalidGameStateException extends BaseConnect4ServiceException {

    private static final long serialVersionUID = 3524410970168452185L;

    /**
     * @param message exception description
     */
    public InvalidGameStateException(final String message) {
        super(message);
    }

}
