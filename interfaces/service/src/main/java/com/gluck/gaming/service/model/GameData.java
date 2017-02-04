package com.gluck.gaming.service.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Object to hold Game data.
 *
 * @author Vinay Semwal
 */
public class GameData implements Serializable {

    private static final long serialVersionUID = -5569703420496813653L;

    private String gameId;

    private String firstPlayerName;

    private String secondPlayerName;

    private String gameState;

    private String sessionId;

    private String lastTurnPlayedBy;

    private Integer[][] connect4Grid;

    /**
     * Default constructor to enable JSON parsing
     */
    public GameData() {
        //For JSON Parsing
    }

    /**
     * @param gameId unique identifier of game
     * @param sessionId session id of game
     * @param firstPlayerName name of first player
     * @param secondPlayerName name of second player
     * @param gameState current state of the game
     * @param lastTurnPlayedBy name of player who played last turn
     * @param connect4Grid game grid
     */
    public GameData(
        final String gameId,
        final String sessionId,
        final String firstPlayerName,
        final String secondPlayerName,
        final String gameState,
        final String lastTurnPlayedBy,
        final Integer[][] connect4Grid) {
        super();
        this.sessionId = sessionId;
        this.gameId = gameId;
        this.firstPlayerName = firstPlayerName;
        this.secondPlayerName = secondPlayerName;
        this.gameState = gameState;
        this.lastTurnPlayedBy = lastTurnPlayedBy;
        this.connect4Grid = connect4Grid;
    }

    /**
     * @return the gameId
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * @return the firstPlayerName
     */
    public String getFirstPlayerName() {
        return firstPlayerName;
    }

    /**
     * @return the secondPlayerName
     */
    public String getSecondPlayerName() {
        return secondPlayerName;
    }

    /**
     * @return the gameState
     */
    public String getGameState() {
        return gameState;
    }

    /**
     * @return the lastTurnPlayedBy
     */
    public String getLastTurnPlayedBy() {
        return lastTurnPlayedBy;
    }

    /**
     * @return the connect4Grid
     */
    public Integer[][] getConnect4Grid() {
        return connect4Grid;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}