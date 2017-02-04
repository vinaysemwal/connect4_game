package com.gluck.gaming.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.gluck.gaming.dao.GameDao;
import com.gluck.gaming.dao.model.Game;
import com.gluck.gaming.db.connection.MongoDbConnectionManager;

/**
 * Implementation class for {@link GameDao}
 *
 * @author Vinay Semwal
 */
public class GameDaoImpl implements GameDao {

    private static final Logger logger = LogManager.getLogger(GameDaoImpl.class);

    private static final String GRID = "grid";

    private static final String LAST_TURN_PLAYED_BY = "lastTurnPlayedBy";

    private static final String STATE = "state";

    private static final String SESSION_ID = "sessionId";

    private static final String SECOND_PLAYER = "secondPlayer";

    private static final String FIRST_PLAYER = "firstPlayer";

    private static final String GAME = "Game";

    private final MongoDbConnectionManager connectionManager;

    /**
     * @param mongoDBConnectionManager connection manager to manage DB connection to a particular Mongo DB database using at specified host and port.
     */
    public GameDaoImpl(final MongoDbConnectionManager mongoDBConnectionManager) {
        connectionManager = mongoDBConnectionManager;
    }

    @Override
    public String createGame(final String firstPlayerName, final String secondPlayerName, final String sessionId) {
        final Document game = getGameDocumentToCreate(firstPlayerName, secondPlayerName, sessionId);
        logger.info("Adding Game doument to Database : {}", game);
        return connectionManager.createDocument(GAME, game);
    }

    @Override
    public Optional<Game> findGameById(final String id) {
        final Optional<Document> gameDocument = connectionManager.findDocumentById(GAME, id);
        return getGame(gameDocument);
    }

    @Override
    public Optional<Game> findGameByPlayers(final String firstPlayerName, final String secondPlayerName) {
        final Map<String, String> filterMap = new HashMap<String, String>();
        final Optional<Document> document = connectionManager.findDocumentUsingFilters(GAME, filterMap);
        return getGame(document);

    }

    @Override
    public void updateGame(final Game game) {
        final Document updatedGameDocument = new Document(STATE, game.getGameState()).append(LAST_TURN_PLAYED_BY, game.getLastTurnedPlayedBy())
            .append(SESSION_ID, game.getSessionId()).append(GRID, convertGridArrayToList(game.getGrid()));
        connectionManager.updateDocument(game.getGameId(), updatedGameDocument, GAME);
    }

    @Override
    public void deleteGame(final Game game) {
        connectionManager.deleteDocument(game.getGameId(), GAME);

    }

    private Document getGameDocumentToCreate(final String firstPlayerName, final String secondPlayerName, final String sessionId) {
        return new Document(FIRST_PLAYER, firstPlayerName).append(SECOND_PLAYER, secondPlayerName).append(SESSION_ID, sessionId).append(STATE, "NEW")
            .append(LAST_TURN_PLAYED_BY, null).append(GRID, initializeGrid());
    }

    /**
     * @return grid initialized with value 0.
     */
    private List<List<Integer>> initializeGrid() {
        final List<List<Integer>> gridList = new ArrayList<List<Integer>>();
        final Integer[][] grid = new Integer[6][7];
        for (int i = 0; i < 6; i++) {
            Arrays.fill(grid[i], 0);
            gridList.add(Arrays.asList(grid[i]));
        }
        return gridList;
    }

    /**
     * @param gameDocument
     * @return optional game
     */
    @SuppressWarnings("unchecked")
    private Optional<Game> getGame(final Optional<Document> optionalGameDocument) {
        if (optionalGameDocument.isPresent()) {
            final Document gameDocument = optionalGameDocument.get();
            return Optional.of(
                new Game(
                    gameDocument.getObjectId("_id").toString(),
                    gameDocument.getString(SESSION_ID),
                    gameDocument.getString(STATE),
                    gameDocument.getString(LAST_TURN_PLAYED_BY),
                    gameDocument.getString(FIRST_PLAYER),
                    gameDocument.getString(SECOND_PLAYER),
                    getGridArray(gameDocument.get(GRID, List.class))));
        }
        return Optional.empty();
    }

    /**
     * @param gridList
     * @return Two dimensional array of size [6][7] representing the grid initialized to default value 0.
     */
    private Integer[][] getGridArray(final List<List<Integer>> gridList) {
        final Integer[][] grid = new Integer[6][7];
        int i = 0;
        for (final List<Integer> list : gridList) {
            grid[i] = list.toArray(grid[i]);
            i++;
        }

        return grid;
    }

    private List<List<Integer>> convertGridArrayToList(final Integer[][] grid) {
        final List<List<Integer>> gridList = new ArrayList<List<Integer>>();
        for (final Integer[] element : grid) {
            gridList.add(Arrays.asList(element));
        }
        return gridList;
    }

}
