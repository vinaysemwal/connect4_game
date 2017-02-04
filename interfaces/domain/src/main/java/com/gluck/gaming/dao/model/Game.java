package com.gluck.gaming.dao.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data class containing the domain Game document data stored in the database.
 * 
 * @author Vinay Semwal
 */
public class Game {

    private String gameId;

    private String sessionId;

    private String gameState;

    private String lastTurnedPlayedBy;

    private String firstPlayer;

    private String secondplayer;

    private Integer[][] grid;

    /**
     * @param gameId unique identifier of the Game
     * @param sessionId current session Id. Its null if Game is SUSPENDED,COMPLETED,DRAWN or ABANDONED
     * @param gameState current game state
     * @param lastTurnedPlayedBy name of player who player the last turn
     * @param firstPlayer first player name
     * @param secondplayer second player name
     * @param grid two dimensional array representing the game grid
     */
    public Game(
        final String gameId,
        final String sessionId,
        final String gameState,
        final String lastTurnedPlayedBy,
        final String firstPlayer,
        final String secondplayer,
        final Integer[][] grid) {
        super();
        this.gameId = gameId;
        this.sessionId = sessionId;
        this.gameState = gameState;
        this.lastTurnedPlayedBy = lastTurnedPlayedBy;
        this.firstPlayer = firstPlayer;
        this.secondplayer = secondplayer;
        this.grid = grid;
    }

    /**
     * @return the gameId
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @return the gameState
     */
    public String getGameState() {
        return gameState;
    }

    /**
     * @return the lastTurnedPlayedBy
     */
    public String getLastTurnedPlayedBy() {
        return lastTurnedPlayedBy;
    }

    /**
     * @return the firstPlayer
     */
    public String getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * @return the secondplayer
     */
    public String getSecondplayer() {
        return secondplayer;
    }

    /**
     * @return the grid
     */
    public Integer[][] getGrid() {
        return grid;
    }

    /**
     * @param gameId the gameId to set
     */
    public void setGameId(final String gameId) {
        this.gameId = gameId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @param gameState the gameState to set
     */
    public void setGameState(final String gameState) {
        this.gameState = gameState;
    }

    /**
     * @param lastTurnedPlayedBy the lastTurnedPlayedBy to set
     */
    public void setLastTurnedPlayedBy(final String lastTurnedPlayedBy) {
        this.lastTurnedPlayedBy = lastTurnedPlayedBy;
    }

    /**
     * @param firstPlayer the firstPlayer to set
     */
    public void setFirstPlayer(final String firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    /**
     * @param secondplayer the second player to set
     */
    public void setSecondplayer(final String secondplayer) {
        this.secondplayer = secondplayer;
    }

    /**
     * @param grid the grid to set
     */
    public void setGrid(final Integer[][] grid) {
        this.grid = grid;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
