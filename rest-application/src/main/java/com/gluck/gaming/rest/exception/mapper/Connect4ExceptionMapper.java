package com.gluck.gaming.rest.exception.mapper;

import java.io.Serializable;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.gluck.gaming.service.exception.BaseConnect4ServiceException;
import com.gluck.gaming.service.exception.Connect4ResponseErrorCode;
import com.gluck.gaming.service.exception.Connect4ServiceValidationException;
import com.gluck.gaming.service.exception.ConsecutiveTurnsNotAllowedException;
import com.gluck.gaming.service.exception.GameDeletionNotAllowedException;
import com.gluck.gaming.service.exception.GameNotFoundException;
import com.gluck.gaming.service.exception.IncorrectGameStartException;
import com.gluck.gaming.service.exception.InvalidGameStateException;
import com.gluck.gaming.service.exception.InvalidGameStateTransitionException;
import com.gluck.gaming.service.exception.InvalidGridCellToFillException;

/**
 * Exception mapping class to map an exception to specific REST response code.
 *
 * @author Vinay Semwal
 */
public class Connect4ExceptionMapper implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Handle exceptional scenarios and returns a Response Builder with relevant status.
     *
     * @param exception Exception for which Response has to be built.
     * @return {@link ResponseBuilder} with {@code Status set}
     */
    public static ResponseBuilder toResponse(final Exception exception) {
        if (exception instanceof BaseConnect4ServiceException) {
            return toConnect4ResponseCode(exception);
        }
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
            new Connect4RestError(Connect4ResponseErrorCode.INTERNAL_SYSTEM_ERROR.getCode(), Connect4ResponseErrorCode.INTERNAL_SYSTEM_ERROR.getMessage()));
    }

    private static ResponseBuilder toConnect4ResponseCode(final Exception ex) {
        if (ex instanceof Connect4ServiceValidationException) {
            return Response.status(Status.BAD_REQUEST).entity(new Connect4RestError(Connect4ResponseErrorCode.VALIDATION.getCode(), ex.getMessage()));
        }
        if (ex instanceof ConsecutiveTurnsNotAllowedException) {
            return Response.status(Status.BAD_REQUEST)
                .entity(new Connect4RestError(Connect4ResponseErrorCode.CONSECUTIVE_TURNS_NOT_ALLOWED.getCode(), ex.getMessage()));
        }
        if (ex instanceof GameNotFoundException) {
            return Response.status(Status.NOT_FOUND).entity(new Connect4RestError(Connect4ResponseErrorCode.GAME_NOT_FOUND.getCode(), ex.getMessage()));
        }
        if (ex instanceof InvalidGameStateException) {
            return Response.status(Status.BAD_REQUEST).entity(new Connect4RestError(Connect4ResponseErrorCode.INVALID_GAME_STATE.getCode(), ex.getMessage()));
        }
        if (ex instanceof InvalidGameStateTransitionException) {
            return Response.status(Status.BAD_REQUEST)
                .entity(new Connect4RestError(Connect4ResponseErrorCode.INVALID_GAME_STATE_TRANSITION.getCode(), ex.getMessage()));
        }
        if (ex instanceof InvalidGridCellToFillException) {
            return Response.status(Status.BAD_REQUEST)
                .entity(new Connect4RestError(Connect4ResponseErrorCode.INVALID_GRID_CELL_TO_FILL.getCode(), ex.getMessage()));
        }
        if (ex instanceof IncorrectGameStartException) {
            return Response.status(Status.BAD_REQUEST).entity(new Connect4RestError(Connect4ResponseErrorCode.INCORRECT_GAME_START.getCode(), ex.getMessage()));
        }
        if (ex instanceof GameDeletionNotAllowedException) {
            return Response.status(Status.FORBIDDEN)
                .entity(new Connect4RestError(Connect4ResponseErrorCode.GAME_DELETION_NOT_ALLOWED.getCode(), ex.getMessage()));
        }
        return Response.status(Connect4ResponseErrorCode.INTERNAL_SYSTEM_ERROR.getCode()).entity(Connect4ResponseErrorCode.INTERNAL_SYSTEM_ERROR.getMessage());

    }

    /**
     * Class to wrap exception code and message to be sent back in the rest response.
     *
     * @author Vinay Semwal
     */
    public static class Connect4RestError implements Serializable {

        private static final long serialVersionUID = 1L;

        private final int code;

        private final String message;

        /**
         * @param code error code to be sent back in rest response
         * @param message error message to be sent back in rest response.
         */
        public Connect4RestError(final int code, final String message) {
            super();
            this.code = code;
            this.message = message;
        }

        /**
         * @return the code
         */
        public int getCode() {
            return code;
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

    }
}
