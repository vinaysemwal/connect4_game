package com.gluck.gaming.dao;

import java.util.Optional;

import com.gluck.gaming.dao.model.Game;

/**
 * Interface to perform CRUD operations on Game.
 *
 * @author Vinay Semwal
 */
public interface GameDao {

    /**
     * API to create new game.
     *
     * @param firstPlayerName Name of the first player to play the game
     * @param secondPlayerName Name of the second player to play the game
     * @param sessionId unique identifier of the current session of the newly created game
     * @return unique identifier of the game created
     */
    public String createGame(String firstPlayerName, String secondPlayerName, String sessionId);

    /**
     * API to retrieve a game using the game id.
     *
     * @param id unique identifier of the game to look up.
     * @return an Optional {@link Game} POJO containing game information
     */
    public Optional<Game> findGameById(String id);

    /**
     * API to retrieve a game by name of the players playing the game.
     *
     * @param firstPlayerName Name of the first player to play the game
     * @param secondPlayerName Name of the second player to play the game
     * @return an Optional {@link Game} POJO containing game information
     */
    public Optional<Game> findGameByPlayers(String firstPlayerName, String secondPlayerName);

    /**
     * API to update a Game.
     *
     * @param game {@link Game} with relevant updated fields.
     */
    public void updateGame(Game game);

    /**
     * API to delete a Game.
     *
     * @param game game to be deleted.
     */
    public void deleteGame(Game game);

}
