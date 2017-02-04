package com.gluck.gaming.service.exception;

/**
 * Response codes for Error scenarios
 *
 * @author Vinay Semwal
 */
public enum Connect4ResponseErrorCode {

    /**
     * Indicates a validation failure in the incoming request.
     */
    VALIDATION(28001, "Request failed validation."),

    /**
     * Indicates consecutive turns tried to be played by same player.
     */
    CONSECUTIVE_TURNS_NOT_ALLOWED(28002, "The same player is not allowed to play consecutive turns."),

    /**
     * Indicates game with given details is not present in the system.
     */
    GAME_NOT_FOUND(28003, "Game with the given details does not exist."),

    /**
     * Indicates game is in an invalid state to perform the operation.
     */
    INVALID_GAME_STATE(28004, "Game is not in a playable state."),

    /**
     * Indicates that the action cannot be performed since game transition will be invalid.
     */
    INVALID_GAME_STATE_TRANSITION(28005, "Cannot perform the given action on the Game."),

    /**
     * Indicates that an incorrect grid cell was tried to be filled.
     */
    INVALID_GRID_CELL_TO_FILL(28006, "Incorrect grid cell sent to fill."),

    /**
     * Indicates that first turn was tried to be played by second player.
     */
    INCORRECT_GAME_START(28007, "First player should play the first turn in a game."),

    /**
     * Indicates that game deletion was tried for a game that is not in a terminal state.
     */
    GAME_DELETION_NOT_ALLOWED(28008, "Game cannot be deleted when it is not in a terminal state."),

    /**
     * Indicates that an internal error occurred in the system.
     */
    INTERNAL_SYSTEM_ERROR(28099, "Internal system error.");

    private Integer code;

    private String message;

    private Connect4ResponseErrorCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @return code of the current response code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @return response message
     */
    public String getMessage() {
        return message;
    }

}
