package com.gluck.gaming.service;

import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gluck.gaming.dao.GameDao;
import com.gluck.gaming.dao.model.Game;
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
import com.gluck.gaming.service.validator.Connect4ServiceValidator;

/**
 * Implementation of {@link Connect4Service}
 *
 * @author Vinay Semwal
 */
public class Connect4ServiceImpl implements Connect4Service {

    private static final Logger logger = LogManager.getLogger(Connect4ServiceImpl.class);

    private static final int HIGHEST_GRID_ROW_NUMBER = 5;

    private final Connect4ServiceValidator connect4ServiceValidator;

    private final GameDao gameDao;

    /**
     * @param connect4ServiceValidator {@link Connect4ServiceValidator} instance to validate the in requests
     * @param gameDao {@link GameDao} implementation to perform database operations for a Game
     */
    public Connect4ServiceImpl(final Connect4ServiceValidator connect4ServiceValidator, final GameDao gameDao) {
        this.connect4ServiceValidator = connect4ServiceValidator;
        this.gameDao = gameDao;
    }

    @Override
    public CreateGameResponse createGame(final CreateGameRequest createGameRequest) throws Connect4ServiceValidationException {
        logger.info("Received create game request : {}", createGameRequest);
        connect4ServiceValidator.validate(createGameRequest);
        final String sessionId = createSession();
        final String gameId = gameDao.createGame(createGameRequest.getFirstPlayerName(), createGameRequest.getSecondPlayerName(), sessionId);
        final CreateGameResponse response = new CreateGameResponse(gameId, sessionId, GameState.NEW);
        logger.info("Created game successfully. Response : {}", response);
        return response;
    }

    @Override
    public GameDataResponse getGameData(final String gameId) throws GameNotFoundException, Connect4ServiceValidationException {
        logger.info("Received request to get game data for game with id: {}", gameId);
        connect4ServiceValidator.valicateGameId(gameId);
        final Game game = fetchGame(gameId);
        logger.info("Successfully fetched game data for id : {}. Data: {}", gameId, game);
        return createGameDataResponse(game);
    }

    @Override
    public String playTurn(final PlayTurnRequest playTurnRequest) throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateException,
        ConsecutiveTurnsNotAllowedException, InvalidGridCellToFillException, IncorrectGameStartException {
        logger.info(
            "Received request to play turn by player : {} for game with id: {} in session : {}",
            playTurnRequest.getPlayerName(),
            playTurnRequest.getGameId(),
            playTurnRequest.getSessionId());
        connect4ServiceValidator.validate(playTurnRequest);
        final Game game = fetchGame(playTurnRequest.getGameId());
        validateGameState(game.getGameState());
        connect4ServiceValidator.validateSession(game.getSessionId(), playTurnRequest.getSessionId());
        validatePlayerTurn(game, playTurnRequest.getPlayerName());
        validateGridToFill(playTurnRequest.getGridRowToFill(), playTurnRequest.getGridColumnToFill(), game);
        updateGame(game, playTurnRequest);
        logger.info(
            "Grid [{},{}] filled successfully by player : {} for game with id : {} ",
            playTurnRequest.getGridRowToFill(),
            playTurnRequest.getGridColumnToFill(),
            playTurnRequest.getPlayerName(),
            playTurnRequest.getGameId());
        return playTurnRequest.getPlayerName();
    }

    @Override
    public void suspendGame(final String gameId) throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException {
        logger.info("suspendGame :: Received request to suspend game with id: {}", gameId);
        connect4ServiceValidator.valicateGameId(gameId);
        final Game game = fetchGame(gameId);
        invalidateCurrentSession(game);
        updateGameState(game, GameState.SUSPENDED, "suspend");
    }

    @Override
    public GameDataResponse resumeGame(final String gameId)
        throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException {
        logger.info("resumeGame :: Received request to resume game with id: {}", gameId);
        connect4ServiceValidator.valicateGameId(gameId);
        final Game game = fetchGame(gameId);
        game.setSessionId(createSession());
        updateGameState(game, GameState.IN_PROGRESS, "resume");
        return createGameDataResponse(game);
    }

    @Override
    public void completeGame(final String gameId) throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException {
        logger.info("completeGame :: Received request to complete game with id: {}", gameId);
        connect4ServiceValidator.valicateGameId(gameId);
        final Game game = fetchGame(gameId);
        invalidateCurrentSession(game);
        updateGameState(game, GameState.COMPLETED, "complete");
    }

    @Override
    public void drawGame(final String gameId) throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException {
        logger.info("drawGame :: Received request to draw game with id: {}", gameId);
        connect4ServiceValidator.valicateGameId(gameId);
        final Game game = fetchGame(gameId);
        invalidateCurrentSession(game);
        updateGameState(game, GameState.DRAWN, "draw");
    }

    @Override
    public void abandonGame(final String gameId) throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException {
        logger.info("abandonGame :: Received request to abandon game with id: {}", gameId);
        connect4ServiceValidator.valicateGameId(gameId);
        final Game game = fetchGame(gameId);
        invalidateCurrentSession(game);
        updateGameState(game, GameState.ABANDONED, "abandon");
    }

    @Override
    public void deleteGame(final String gameId)
        throws Connect4ServiceValidationException, GameNotFoundException, InvalidGameStateTransitionException, GameDeletionNotAllowedException {
        logger.info("deleteGame :: Received request to delete game with id: {}", gameId);
        connect4ServiceValidator.valicateGameId(gameId);
        final Game game = fetchGame(gameId);
        if (!GameState.ABANDONED.name().equals(game.getGameState()) && !GameState.COMPLETED.name().equals(game.getGameState())
            && !GameState.DRAWN.name().equals(game.getGameState())) {
            logger.error(
                "Operation not supported to delete the game with id : {}  since it is not in any of the terminal states(ABANDONED,COMPLETED or DRAWN). Current game state : {}",
                game.getGameId(),
                game.getGameState());
            throw new GameDeletionNotAllowedException(
                "Cannot delete the game. Game must either be in COMPLETED, DRAWN or ABANDONED state in order to be deleted.");
        }
        gameDao.deleteGame(game);
    }

    /**
     * @return unique identifier of the session created
     */
    private String createSession() {
        return UUID.randomUUID().toString();
    }

    private GameDataResponse createGameDataResponse(final Game game) {
        return new GameDataResponse(
            new GameData(
                game.getGameId(),
                game.getSessionId(),
                game.getFirstPlayer(),
                game.getSecondplayer(),
                game.getGameState(),
                game.getLastTurnedPlayedBy(),
                game.getGrid()));
    }

    /**
     * @throws InvalidGameStateTransitionException if action cannot be performed because of the current state that game is in.
     */
    private void updateGameState(final Game game, final GameState targetState, final String action) throws InvalidGameStateTransitionException {
        if (!GameState.isStateTransitionValid(GameState.valueOf(game.getGameState()), targetState)) {
            logger.error(
                "Invalid game state transition. Cannot {} the game with id : {} since the game is in : {} state. ",
                action,
                game.getGameId(),
                game.getGameState());
            throw new InvalidGameStateTransitionException("Invalid state transition from " + game.getGameState() + "to " + targetState);
        }
        game.setGameState(targetState.name());
        gameDao.updateGame(game);
        logger.info("Completed the operation to {} the game with id : {}. Updated game state : {}", action, game.getGameId(), targetState);

    }

    /**
     * @throws InvalidGridCellToFillException if either the grid cell is already filled or the grid cell below the grid cell to fill is still unfilled.
     */
    private void validateGridToFill(final int gridRowToFill, final int gridColumnToFill, final Game game) throws InvalidGridCellToFillException {
        final Integer[][] gameGrid = game.getGrid();
        if (gameGrid[gridRowToFill][gridColumnToFill] != 0) {
            logger.error(
                "Invalid grid cell sent to fill for game with id : {}. The grid : {},{} is already filled.",
                game.getGameId(),
                gridRowToFill,
                gridColumnToFill);
            throw new InvalidGridCellToFillException("Cannot fill the grid. It is already filled.");
        }

        if (gridRowToFill < HIGHEST_GRID_ROW_NUMBER && gameGrid[gridRowToFill + 1][gridColumnToFill] == 0) {
            logger.error(
                "Invalid grid cell sent to fill for game with id : {}. The grid : {},{} has an unfilled grid below it.",
                game.getGameId(),
                gridRowToFill,
                gridColumnToFill);
            throw new InvalidGridCellToFillException("Cannot fill the grid. The grid below it is still unfilled.");
        }

    }

    /**
     * @throws ConsecutiveTurnsNotAllowedException if the same player has tried to play consecutive turns in a game.
     * @throws IncorrectGameStartException if second player has tried to play the first turn in a game.
     */
    private void validatePlayerTurn(final Game game, final String playerName) throws ConsecutiveTurnsNotAllowedException, IncorrectGameStartException {
        //Enforce first turn rule : first player should play the first turn
        validateFirstTurn(game, playerName);
        if ((!GameState.NEW.name().equals(game.getGameState())) && game.getLastTurnedPlayedBy() != null && (playerName.equals(game.getLastTurnedPlayedBy()))) {
            logger.error("Same player : {} tried to play consecutive turns for game with id : {}", playerName, game.getGameId());
            throw new ConsecutiveTurnsNotAllowedException("The same player is not allowed to play consecutive turns.");
        }

    }

    /**
     * @throws IncorrectGameStartException if second player has tried to play the first turn in a game.
     */
    private void validateFirstTurn(final Game game, final String playerName) throws IncorrectGameStartException {
        if (GameState.NEW.name().equals(game.getGameState()) && !playerName.equals(game.getFirstPlayer())) {
            logger.error("Game with id : {} is in NEW state. First player should play the first turn in it.", game.getGameId());
            throw new IncorrectGameStartException("First player should start the game.");

        }
        if (wasGameSuspendedJustAfterCreation(game) && !playerName.equals(game.getFirstPlayer())) {
            logger.error("No turn has been played in the game with id : {}. First player should play the first turn.", game.getGameId());
            throw new IncorrectGameStartException("First player should start the game.");
        }
    }

    private boolean wasGameSuspendedJustAfterCreation(final Game game) {
        if (GameState.IN_PROGRESS.name().equals(game.getGameState()) && (game.getLastTurnedPlayedBy() == null)) {
            return true;
        }
        return false;
    }

    /**
     * @throws InvalidGameStateException if the game is not in NEW or IN_PROGRESS state.
     */
    private void validateGameState(final String currentGameState) throws InvalidGameStateException {
        if (!GameState.IN_PROGRESS.name().equals(currentGameState) && !GameState.NEW.name().equals(currentGameState)) {
            logger.error("Game is not in a plyabale state. Game must be in NEW or IN_PROGRESS state to play the turn successfully.");
            throw new InvalidGameStateException("Game is not in a playable state. Current Game state: " + currentGameState);
        }
    }

    /**
     * @throws GameNotFoundException if the game with given id does not exists in the system.
     */
    private Game fetchGame(final String gameId) throws GameNotFoundException {
        final Optional<Game> optionalGame = gameDao.findGameById(gameId);
        if (!optionalGame.isPresent()) {
            logger.error("Game with id : {} does not exist.", gameId);
            throw new GameNotFoundException("Game with the given ID does not exist.");
        }
        final Game game = optionalGame.get();
        return game;
    }

    private void updateGame(final Game game, final PlayTurnRequest playTurnRequest) {
        game.setLastTurnedPlayedBy(playTurnRequest.getPlayerName());
        if (GameState.NEW.name().equals(game.getGameState())) {
            game.setGameState(GameState.IN_PROGRESS.name());
        }
        final Integer[][] gameGrid = game.getGrid();
        if (game.getFirstPlayer().equals(playTurnRequest.getPlayerName())) {
            gameGrid[playTurnRequest.getGridRowToFill()][playTurnRequest.getGridColumnToFill()] = 1;
        } else if (game.getSecondplayer().equals(playTurnRequest.getPlayerName())) {
            gameGrid[playTurnRequest.getGridRowToFill()][playTurnRequest.getGridColumnToFill()] = 2;
        }
        gameDao.updateGame(game);

    }

    private void invalidateCurrentSession(final Game game) {
        game.setSessionId(null);
    }

    /**
     * @return the gameDao
     */
    public GameDao getGameDao() {
        return gameDao;
    }

}
