package com.gluck.gaming.service;

import javax.naming.OperationNotSupportedException;

import com.gluck.gaming.service.exception.Connect4ServiceValidationException;
import com.gluck.gaming.service.exception.ConsecutiveTurnsNotAllowedException;
import com.gluck.gaming.service.exception.GameDeletionNotAllowedException;
import com.gluck.gaming.service.exception.GameNotFoundException;
import com.gluck.gaming.service.exception.IncorrectGameStartException;
import com.gluck.gaming.service.exception.InvalidGameStateException;
import com.gluck.gaming.service.exception.InvalidGameStateTransitionException;
import com.gluck.gaming.service.exception.InvalidGridCellToFillException;
import com.gluck.gaming.service.model.CreateGameRequest;
import com.gluck.gaming.service.model.CreateGameResponse;
import com.gluck.gaming.service.model.GameData;
import com.gluck.gaming.service.model.GameDataResponse;
import com.gluck.gaming.service.model.GameState;
import com.gluck.gaming.service.model.PlayTurnRequest;

/**
 * Interface to expose connect4 game services.
 *
 * @author Vinay Semwal
 */
public interface Connect4Service {

    /**
     * API to create a Game.<br>
     * When a new game is created its state is NEW until the first turn has been played in the game.
     *
     * @param createGameRequest {@link CreateGameRequest}
     *            <ul>
     *            <p>
     *            {@link CreateGameRequest#getFirstPlayerName()}: Name of the first player to play the game. First player should play the first turn after game
     *            is successfully created.Mandatory input, cannot be null or empty.
     *            </p>
     *            <p>
     *            {@link CreateGameRequest#getSecondPlayerName()}: Name of the second player to play the game.Mandatory input, cannot be null or empty.
     *            </p>
     *            </ul>
     * @return {@link CreateGameResponse}
     *         <ul>
     *         <p>
     *         {@link CreateGameResponse#getGameId()}: Unique identifier of the newly created game.
     *         </p>
     *         <p>
     *         {@link CreateGameResponse#getSessionId()}: Unique identifier of the session created as part of the game creation.
     *         </p>
     *         <p>
     *         {@link CreateGameResponse#getGameState()}: Current game state. {@link GameState#NEW}
     *         </p>
     *         </ul>
     * @throws Connect4ServiceValidationException when request fails validations.
     */
    public CreateGameResponse createGame(CreateGameRequest createGameRequest) throws Connect4ServiceValidationException;

    /**
     * API to fetch all the details of a game using its unique identifier.<br>
     * Data of all the games can be retrieved except the deleted ones.
     *
     * @param gameId unique identifier of the game, data of which is to be fetched.
     * @return {@link GameDataResponse}
     *         <ul>
     *         <p>
     *         {@link GameDataResponse#getGameData()}: Data object containing game information.
     *         </p>
     *         <ul>
     *         <p>
     *         {@link GameData#getGameId()}: Unique identifier of the game
     *         <p>
     *         {@link GameData#getSessionId()}: Unique identifier of the current session in which game is being played.
     *         <p>
     *         {@link GameData#getFirstPlayerName()}: Name of the first player playing the game.
     *         <p>
     *         {@link GameData#getSecondPlayerName()}: Name of the second player playing the game.
     *         <p>
     *         {@link GameData#getGameState()}: Current state that the game is in.
     *         <p>
     *         {@link GameData#getLastTurnPlayedBy()}: Name of the player who played the last turn.
     *         <p>
     *         {@link GameData#getConnect4Grid()}: Grid representation of the game grid in two dimensional array format. Unfilled grid cells have value 0;cells
     *         filled by first player have value 1 and the cells filled by second player have value 2.
     *         </ul>
     *         </ul>
     * @throws GameNotFoundException when no game with the given ID exists in the system.
     * @throws Connect4ServiceValidationException when a null or empty game id is provided as API input.
     */
    public GameDataResponse getGameData(final String gameId) throws GameNotFoundException, Connect4ServiceValidationException;

    /**
     * API to play turn in a game. Turns can be played in a game only if it is in NEW or IN_PROGRESS state. The first player must play the first turn in the
     * game. The same player isn't allowed to play consecutive turns in a game. The grid cell to fill shouldn't already be filled and a grid cell cannot be
     * filled if there is a non-filled grid below the grid cell sent to fill.
     *
     * @param playTurnRequest {@link PlayTurnRequest}
     *            <ul>
     *            <p>
     *            {@link PlayTurnRequest#getGameId()}: Unique identifier of the game for which turn is being played.
     *            <p>
     *            {@link PlayTurnRequest#getSessionId()}: Identifier of the session in which game is being played currently.
     *            <p>
     *            {@link PlayTurnRequest#getPlayerName()}: Name of the player playing the turn.
     *            <p>
     *            {@link PlayTurnRequest#getGridRowToFill()}: Row number of the grid cell to fill. The number is indexed starting from 0, with 0 being the top
     *            most row of the grid.
     *            <p>
     *            {@link PlayTurnRequest#getGridColumnToFill()}: Column number of the grid cell to fill. The number is indexed starting from 0, with 0 being the
     *            leftmost column of the grid.
     *            </ul>
     * @return Name of the player to play next turn.
     * @throws Connect4ServiceValidationException when request fails validation.
     * @throws GameNotFoundException when no game with the given ID exists in the system.
     * @throws InvalidGameStateException if game is not in IN_PROGRESS or NEW state.
     * @throws ConsecutiveTurnsNotAllowedException if same player tried to play consecutive turns.
     * @throws InvalidGridCellToFillException if either the grid cell is already filled or there is an unfilled grid cell below the grid cell sent to fill.
     * @throws IncorrectGameStartException if second player tried to play the first turn in a game.
     */
    public String playTurn(PlayTurnRequest playTurnRequest) throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateException,
        ConsecutiveTurnsNotAllowedException, InvalidGridCellToFillException, IncorrectGameStartException;

    /**
     * API to resume a game.<br>
     * A game can only be resumed if it is in a non-terminal SUSPENDED state.<br>
     * A game resume operation creates a fresh session to play the game.
     *
     * @param gameId unique identifier of the game to be resumed.
     * @return {@link GameDataResponse}
     *         <ul>
     *         <p>
     *         {@link GameDataResponse#getGameData()}: Data object containing game information.
     *         </p>
     *         <ul>
     *         <p>
     *         {@link GameData#getGameId()}: Unique identifier of the game
     *         <p>
     *         {@link GameData#getSessionId()}: Unique identifier of the current session in which game is being played.
     *         <p>
     *         {@link GameData#getFirstPlayerName()}: Name of the first player playing the game.
     *         <p>
     *         {@link GameData#getSecondPlayerName()}: Name of the second player playing the game.
     *         <p>
     *         {@link GameData#getGameState()}: Current state that the game is in.
     *         <p>
     *         {@link GameData#getLastTurnPlayedBy()}: Name of the player who played the last turn.
     *         <p>
     *         {@link GameData#getConnect4Grid()}: Grid representation of the game grid in two dimensional array format.
     *         </ul>
     *         </ul>
     * @throws Connect4ServiceValidationException when request fails validations.
     * @throws GameNotFoundException when no game with the given ID exists in the system.
     * @throws InvalidGameStateTransitionException when game cannot be marked completed with current game state.
     */
    public GameDataResponse resumeGame(final String gameId)
        throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException;

    /**
     * API to suspend a game<br>
     * A suspended game doesn't have any session associated with it. Hence any existing session is invalidated if game is being suspended.<br>
     * A game in NEW and IN_PROGRESS state can only be suspended.
     *
     * @param gameId unique identifier of the game to be suspended.
     * @throws Connect4ServiceValidationException when request fails validations.
     * @throws GameNotFoundException when no game with the given ID exists in the system.
     * @throws InvalidGameStateTransitionException if game cannot be suspended with current game state.
     */
    public void suspendGame(final String gameId) throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException;

    /**
     * API to complete a game<br>
     * This operation is a terminal operation and should be invoked if the game has a winner decided.<br>
     * A game can be completed only if it is in IN_PROGRESS state.
     *
     * @param gameId unique identifier of the game to be marked completed.
     * @throws Connect4ServiceValidationException when request fails validations.
     * @throws GameNotFoundException when no game with the given ID exists in the system.
     * @throws InvalidGameStateTransitionException if game cannot be completed with current game state.
     */
    public void completeGame(final String gameId) throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException;

    /**
     * API to draw a game<br>
     * This operation is a terminal operation and should be invoked if the game did not have a winner.<br>
     * A game can be drawn only if it is in IN_PROGRESS state.
     *
     * @param gameId unique identifier of the game to be marked drawn
     * @throws Connect4ServiceValidationException when request fails validations.
     * @throws GameNotFoundException when no game with the given ID exists in the system.
     * @throws InvalidGameStateTransitionException if game cannot be drawn with current game state.
     */
    public void drawGame(final String gameId) throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException;

    /**
     * API to abandon a game<br>
     * This operation is a terminal operation and should be invoked if any of the player decides to leave the game.<br>
     * A game can be abandoned from any of the non-terminal states, that is, NEW, IN_PROGRESS AND SUSPENDED states.
     *
     * @param gameId unique identifier of the game to be abandoned
     * @throws Connect4ServiceValidationException when request fails validations.
     * @throws GameNotFoundException when no game with the given ID exists in the system.
     * @throws InvalidGameStateTransitionException if game cannot be abandoned with current game state.
     */
    public void abandonGame(final String gameId) throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException;

    /**
     * API to delete a game<br>
     * This operation performs removes the game data from the system.<br>
     * A game can be deleted only if it is either completed, drawn or abandoned.
     *
     * @param gameId unique identifier of the game to be deleted.
     * @throws Connect4ServiceValidationException when request fails validations.
     * @throws GameNotFoundException when no game with the given ID exists in the system.
     * @throws InvalidGameStateTransitionException if game cannot be marked completed with current game state.
     * @throws OperationNotSupportedException if game to be deleted is not in a terminal state.Terminal states are ABANDONED, DRAWN, COMPLETED
     * @throws GameDeletionNotAllowedException when a game is tried to be deleted when it is not in a terminal state.
     */
    public void deleteGame(final String gameId) throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException,
        OperationNotSupportedException, GameDeletionNotAllowedException;

}
