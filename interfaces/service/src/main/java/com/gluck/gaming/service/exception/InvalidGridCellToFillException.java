package com.gluck.gaming.service.exception;

/**
 * Exception thrown when an invalid grid cell is being sent to fill, that is, either the grid cell below the grid sent to fill is still unfilled or the grid is
 * already filled.
 *
 * @author Vinay Semwal
 */
public class InvalidGridCellToFillException extends BaseConnect4ServiceException {

    private static final long serialVersionUID = -8769323300373826046L;

    /**
     * @param message exception description
     */
    public InvalidGridCellToFillException(final String message) {
        super(message);
    }

}
