package com.gluck.gaming.service.exception;

/**
 * Exception thrown when second player tries to play the first turn in a game.
 *
 * @author Vinay Semwal
 */
public class IncorrectGameStartException extends BaseConnect4ServiceException {

    private static final long serialVersionUID = 3711284848087660687L;

    /**
     * @param message exception description.
     */
    public IncorrectGameStartException(final String message) {
        super(message);
    }

}
