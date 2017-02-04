package com.gluck.gaming.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gluck.gaming.objects.factory.Connect4Factory;
import com.gluck.gaming.service.exception.Connect4ServiceValidationException;
import com.gluck.gaming.service.exception.ConsecutiveTurnsNotAllowedException;
import com.gluck.gaming.service.exception.GameNotFoundException;
import com.gluck.gaming.service.exception.IncorrectGameStartException;
import com.gluck.gaming.service.exception.InvalidGameStateException;
import com.gluck.gaming.service.exception.InvalidGridCellToFillException;
import com.gluck.gaming.service.model.CreateGameRequest;
import com.gluck.gaming.service.model.CreateGameResponse;
import com.gluck.gaming.service.model.GameDataResponse;
import com.gluck.gaming.service.model.GameState;
import com.gluck.gaming.service.model.PlayTurnRequest;

/**
 * Test class for {@link Connect4ServiceImpl}
 *
 * @author Vinay Semwal
 */
public class Connect4ServiceImplTest {

    private static Connect4ServiceImpl connect4Service;

    private static GameDataResponse gameDataResponse;

    /**
     * Test data setup
     */
    @BeforeClass
    public static void setUp() {
        connect4Service = (Connect4ServiceImpl) Connect4Factory.getConnect4Service();
        try {
            final CreateGameResponse response = connect4Service.createGame(new CreateGameRequest("p1", "p2"));
            gameDataResponse = connect4Service.getGameData(response.getGameId());
        } catch (Connect4ServiceValidationException | GameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete resources created in the DB
     *
     * @throws Exception if system error occurs
     */
    @AfterClass
    public static void cleanUp() throws Exception {
        if (connect4Service != null && gameDataResponse != null) {
            connect4Service.abandonGame(gameDataResponse.getGameData().getGameId());
            connect4Service.deleteGame(gameDataResponse.getGameData().getGameId());
        }
    }

    /**
     * Happy path for createGame API.
     *
     * @throws Exception if system error occurs
     */
    @Test
    public void testGameCreation() throws Exception {
        CreateGameResponse response = null;
        try {
            response = connect4Service.createGame(new CreateGameRequest("p1", "p2"));
            assertEquals(GameState.NEW, response.getGameState());
            final GameDataResponse fetchedgameData = connect4Service.getGameData(response.getGameId());
            assertGameDataResponse(response, fetchedgameData);
        } finally {
            connect4Service.suspendGame(response.getGameId());
            connect4Service.abandonGame(response.getGameId());
            connect4Service.deleteGame(response.getGameId());
        }

    }

    /**
     * Test playTurn API.
     *
     * @throws Exception if system error occurs
     */
    @Test
    public void testPlayTurn() throws Exception {
        //Second Player tries to play first turn.
        try {
            connect4Service.playTurn(
                new PlayTurnRequest(
                    gameDataResponse.getGameData().getGameId(),
                    gameDataResponse.getGameData().getSessionId(),
                    gameDataResponse.getGameData().getSecondPlayerName(),
                    1,
                    5));
            fail("play Turn API must fail if second Player tries to play the first turn.");
        } catch (final IncorrectGameStartException e) {
            assertEquals("First player should start the game.", e.getMessage());
        }

        //suspend NEW game, and try to play turn
        connect4Service.suspendGame(gameDataResponse.getGameData().getGameId());
        assertEquals(GameState.SUSPENDED.name(), connect4Service.getGameData(gameDataResponse.getGameData().getGameId()).getGameData().getGameState());

        //Try to play turn in a suspended Game
        try {
            connect4Service.playTurn(
                new PlayTurnRequest(
                    gameDataResponse.getGameData().getGameId(),
                    gameDataResponse.getGameData().getSessionId(),
                    gameDataResponse.getGameData().getSecondPlayerName(),
                    1,
                    5));
            fail("play Turn API must fail if a Player tries to play the turn in a suspended Game.");
        } catch (final InvalidGameStateException e) {
            assertEquals("Game is not in a playable state. Current Game state: SUSPENDED", e.getMessage());
        }

        //resume game
        connect4Service.resumeGame(gameDataResponse.getGameData().getGameId());
        gameDataResponse = connect4Service.getGameData(gameDataResponse.getGameData().getGameId());
        assertEquals(GameState.IN_PROGRESS.name(), gameDataResponse.getGameData().getGameState());

        //Second player tries to play the first turn again after game is resumed
        try {
            connect4Service.playTurn(
                new PlayTurnRequest(
                    gameDataResponse.getGameData().getGameId(),
                    gameDataResponse.getGameData().getSessionId(),
                    gameDataResponse.getGameData().getSecondPlayerName(),
                    1,
                    5));
            fail("play Turn API must fail if second Player tries to play the first turn.");
        } catch (final IncorrectGameStartException e) {
            assertEquals("First player should start the game.", e.getMessage());
        }

        //First player plays the first turn but incorrect grid sent to fill.
        try {
            connect4Service.playTurn(
                new PlayTurnRequest(
                    gameDataResponse.getGameData().getGameId(),
                    gameDataResponse.getGameData().getSessionId(),
                    gameDataResponse.getGameData().getFirstPlayerName(),
                    1,
                    4));
            fail("play Turn API must fail if a Player tries to fill incorrect grid.");
        } catch (final InvalidGridCellToFillException e) {
            assertEquals("Cannot fill the grid. The grid below it is still unfilled.", e.getMessage());
        }

        //First player plays the first turn with correct grid coordinates:Successful scenario
        connect4Service.playTurn(
            new PlayTurnRequest(
                gameDataResponse.getGameData().getGameId(),
                gameDataResponse.getGameData().getSessionId(),
                gameDataResponse.getGameData().getFirstPlayerName(),
                1,
                5));
        gameDataResponse = connect4Service.getGameData(gameDataResponse.getGameData().getGameId());
        assertEquals(gameDataResponse.getGameData().getFirstPlayerName(), gameDataResponse.getGameData().getLastTurnPlayedBy());

        //First player plays the turn again. Should fail
        try {
            connect4Service.playTurn(
                new PlayTurnRequest(
                    gameDataResponse.getGameData().getGameId(),
                    gameDataResponse.getGameData().getSessionId(),
                    gameDataResponse.getGameData().getFirstPlayerName(),
                    1,
                    4));
            fail("play Turn API must fail if a Player tries to play consecutive turns.");
        } catch (final ConsecutiveTurnsNotAllowedException e) {
            assertEquals("The same player is not allowed to play consecutive turns.", e.getMessage());
        }

        //Second Player plays the turn. But incorrect filled grid is tried to fill again.
        try {
            connect4Service.playTurn(
                new PlayTurnRequest(
                    gameDataResponse.getGameData().getGameId(),
                    gameDataResponse.getGameData().getSessionId(),
                    gameDataResponse.getGameData().getSecondPlayerName(),
                    1,
                    5));
            fail("play Turn API must fail if a Player tries to fill already filled grid.");
        } catch (final InvalidGridCellToFillException e) {
            assertEquals("Cannot fill the grid. It is already filled.", e.getMessage());
        }

        //Second Player plays the correct turn.
        connect4Service.playTurn(
            new PlayTurnRequest(
                gameDataResponse.getGameData().getGameId(),
                gameDataResponse.getGameData().getSessionId(),
                gameDataResponse.getGameData().getSecondPlayerName(),
                2,
                5));

        gameDataResponse = connect4Service.getGameData(gameDataResponse.getGameData().getGameId());
        assertEquals(gameDataResponse.getGameData().getSecondPlayerName(), gameDataResponse.getGameData().getLastTurnPlayedBy());

    }

    /**
     * Test abandonGame API
     *
     * @throws Exception if system error occurs
     */
    @Test
    public void testAbandonGame() throws Exception {
        CreateGameResponse response = null;
        try {
            response = connect4Service.createGame(new CreateGameRequest("p1", "p2"));
            assertEquals(GameState.NEW, response.getGameState());
            connect4Service.abandonGame(response.getGameId());
            final GameDataResponse fetchedGameData = connect4Service.getGameData(response.getGameId());
            assertEquals(GameState.ABANDONED.name(), fetchedGameData.getGameData().getGameState());
            assertNull(fetchedGameData.getGameData().getSessionId());
        } finally {
            connect4Service.deleteGame(response.getGameId());
        }
    }

    /**
     * Test completeGame API
     *
     * @throws Exception if system error occurs
     */
    @Test
    public void testCompleteGame() throws Exception {
        CreateGameResponse response = null;
        try {
            response = connect4Service.createGame(new CreateGameRequest("p1", "p2"));
            assertEquals(GameState.NEW, response.getGameState());
            connect4Service.playTurn(new PlayTurnRequest(response.getGameId(), response.getSessionId(), "p1", 2, 5));
            connect4Service.completeGame(response.getGameId());
            final GameDataResponse fetchedGameData = connect4Service.getGameData(response.getGameId());
            assertEquals(GameState.COMPLETED.name(), fetchedGameData.getGameData().getGameState());
            assertNull(fetchedGameData.getGameData().getSessionId());
        } finally {
            connect4Service.deleteGame(response.getGameId());
        }
    }

    /**
     * Test drawGame API
     *
     * @throws Exception if system error occurs
     */
    @Test
    public void testDrawGame() throws Exception {
        CreateGameResponse response = null;
        try {
            response = connect4Service.createGame(new CreateGameRequest("p1", "p2"));
            assertEquals(GameState.NEW, response.getGameState());
            connect4Service.playTurn(new PlayTurnRequest(response.getGameId(), response.getSessionId(), "p1", 2, 5));
            connect4Service.drawGame(response.getGameId());
            final GameDataResponse fetchedGameData = connect4Service.getGameData(response.getGameId());
            assertEquals(GameState.DRAWN.name(), fetchedGameData.getGameData().getGameState());
            assertNull(fetchedGameData.getGameData().getSessionId());
        } finally {
            connect4Service.deleteGame(response.getGameId());
        }
    }

    /**
     * Test suspendGame API
     *
     * @throws Exception if system error occurs
     */
    @Test
    public void testSuspendGame() throws Exception {
        CreateGameResponse response = null;
        try {
            response = connect4Service.createGame(new CreateGameRequest("p1", "p2"));
            assertEquals(GameState.NEW, response.getGameState());
            connect4Service.suspendGame(response.getGameId());
            final GameDataResponse fetchedGameData = connect4Service.getGameData(response.getGameId());
            assertEquals(GameState.SUSPENDED.name(), fetchedGameData.getGameData().getGameState());
            assertNull(fetchedGameData.getGameData().getSessionId());
        } finally {
            connect4Service.abandonGame(response.getGameId());
            connect4Service.deleteGame(response.getGameId());
        }
    }

    private void assertGameDataResponse(final CreateGameResponse response, final GameDataResponse fetchedResponse) {
        assertNotNull(fetchedResponse);
        assertNotNull(fetchedResponse.getGameData());
        assertTrue(6 == fetchedResponse.getGameData().getConnect4Grid().length);
        assertEquals(response.getGameState().name(), fetchedResponse.getGameData().getGameState());
        assertEquals(response.getGameId(), fetchedResponse.getGameData().getGameId());
        assertEquals(response.getSessionId(), fetchedResponse.getGameData().getSessionId());
    }

}
