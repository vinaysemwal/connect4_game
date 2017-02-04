package com.gluck.gaming.service.exception;

/**
 * Exception to be thrown in cases a game is not present in the system.
 *
 * @author Vinay Semwal
 */
public class GameNotFoundException extends BaseConnect4ServiceException {

    private static final long serialVersionUID = -5884890097751913166L;

    /**
     * @param message exception description.
     */
    public GameNotFoundException(final String message) {
        super(message);
    }

}
