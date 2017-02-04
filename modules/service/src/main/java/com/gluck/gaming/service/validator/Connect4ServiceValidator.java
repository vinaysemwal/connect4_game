package com.gluck.gaming.service.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import com.gluck.gaming.service.Connect4Service;
import com.gluck.gaming.service.exception.Connect4ServiceValidationException;
import com.gluck.gaming.service.model.CreateGameRequest;
import com.gluck.gaming.service.model.PlayTurnRequest;

/**
 * Validator class to validate API requests served by {@link Connect4Service}
 *
 * @author Vinay Semwal
 */
public class Connect4ServiceValidator {

    private static final Logger logger = LogManager.getLogger(Connect4ServiceValidator.class);

    /**
     * Method to validate the request to create game
     *
     * @param createGameRequest {@link CreateGameRequest}
     * @throws Connect4ServiceValidationException if the request validation fails.
     */
    public void validate(final CreateGameRequest createGameRequest) throws Connect4ServiceValidationException {
        if (Objects.isNull(createGameRequest)) {
            logger.error("Null request sent for creating game.");
            throw new Connect4ServiceValidationException("Request to create a new game must not be null.");
        }
        if (isStringNullOrEmpty(createGameRequest.getFirstPlayerName()) || isStringNullOrEmpty(createGameRequest.getSecondPlayerName())) {
            logger.error("Request failed validation. Player names to create the game must not be null or empty");
            throw new Connect4ServiceValidationException("Player names to create the game must not be null or empty");
        }

    }

    /**
     * Method to validate the request to play turn
     *
     * @param playTurnRequest {@link PlayTurnRequest}
     * @throws Connect4ServiceValidationException if the request validation fails.
     */
    public void validate(final PlayTurnRequest playTurnRequest) throws Connect4ServiceValidationException {
        final List<String> errors = new ArrayList<>();
        if (Objects.isNull(playTurnRequest)) {
            logger.error("Request to play turn in an existing game must not be null.");
            throw new Connect4ServiceValidationException("Request to play turn in an existing game must not be null.");
        }
        try {
            valicateGameId(playTurnRequest.getGameId());
        } catch (final Connect4ServiceValidationException ex) {
            errors.add(ex.getMessage());
        }

        if (isStringNullOrEmpty(playTurnRequest.getSessionId())) {
            errors.add("Session id is mandatory to play turn.");
        }
        if (isStringNullOrEmpty(playTurnRequest.getPlayerName())) {
            errors.add("Player name cannot be null or empty.");
        }
        if (playTurnRequest.getGridColumnToFill() < 0 || playTurnRequest.getGridRowToFill() < 0) {
            errors.add("Invalid grid cell coordinates provided to fill.");
        }
        if (!errors.isEmpty()) {
            logger.error("Play turn request failed validation rules. Errors: {}", errors);
            throw new Connect4ServiceValidationException("Request failed validation checks.", errors);
        }

    }

    /**
     * Method to validate game id.
     *
     * @param gameId unique identifier that is to be validated.
     * @throws Connect4ServiceValidationException if the game id is invalid.
     */
    public void valicateGameId(final String gameId) throws Connect4ServiceValidationException {
        if (isStringNullOrEmpty(gameId)) {
            logger.error("Game id is mandatory, cannot be null or empty.");
            throw new Connect4ServiceValidationException("Game id is mandatory, cannot be null or empty.");
        }
        try {
            new ObjectId(gameId);
        } catch (final IllegalArgumentException e) {
            logger.error("Invalid game Id provided : {}", gameId);
            throw new Connect4ServiceValidationException("Invalid game Id provided.");
        }

    }

    /**
     * Method to validate session id sent in request against the session id stored in database.
     * 
     * @param savedSessionId session Id stored in the database
     * @param requestSessionId session id sent in the request.
     * @throws Connect4ServiceValidationException the session IDs don't match.
     */
    public void validateSession(final String savedSessionId, final String requestSessionId) throws Connect4ServiceValidationException {
        if (isStringNullOrEmpty(savedSessionId)) {}
        if (!requestSessionId.equals(savedSessionId)) {
            logger.error("Session Id mismatch. Saved session id is : {} , where as the session id sent in request : {}", savedSessionId, requestSessionId);
            throw new Connect4ServiceValidationException("Invalid session Id provided.");
        }

    }

    /**
     * Method to check if a string is null or empty.
     *
     * @param target string to check for null and empty values
     * @return true if the string is null or empty
     */
    public boolean isStringNullOrEmpty(final String target) {
        return Objects.isNull(target) || target.isEmpty();
    }

}
