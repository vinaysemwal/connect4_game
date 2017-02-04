package com.gluck.gaming.service.exception;

/**
 * Exception thrown when a game is tried to be deleted when it is not in a terminal state.
 * 
 * @author Vinay Semwal
 */
public class GameDeletionNotAllowedException extends BaseConnect4ServiceException {

    private static final long serialVersionUID = -9079176047089147214L;

    /**
     * @param message exception message
     */
    public GameDeletionNotAllowedException(final String message) {
        super(message);
    }

}
