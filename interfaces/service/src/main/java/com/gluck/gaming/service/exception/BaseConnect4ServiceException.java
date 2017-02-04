package com.gluck.gaming.service.exception;

/**
 * Parent abstract class for all connect4 game service exceptions.
 * 
 * @author Vinay Semwal
 */
public abstract class BaseConnect4ServiceException extends Exception {

    private static final long serialVersionUID = 1962052519680138749L;

    /**
     * @param message message description
     */
    public BaseConnect4ServiceException(final String message) {
        super(message);

    }

}
