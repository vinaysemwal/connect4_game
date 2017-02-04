package com.gluck.gaming.service.exception;

/**
 * Exception thrown when an action is performed to send the game to an invalid state.<br>
 * For example- if it is tried to suspend a game that is already completed.
 *
 * @author Vinay Semwal
 */
public class InvalidGameStateTransitionException extends BaseConnect4ServiceException {

    private static final long serialVersionUID = -7795213257442282965L;

    /**
     * @param message exception description.
     */
    public InvalidGameStateTransitionException(final String message) {
        super(message);
    }

}