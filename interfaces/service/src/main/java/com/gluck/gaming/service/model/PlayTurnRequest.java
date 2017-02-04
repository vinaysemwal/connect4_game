package com.gluck.gaming.service.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Vinay Semwal
 */
public class PlayTurnRequest implements Serializable {

    private static final long serialVersionUID = 2807802478829584741L;

    private final String playerName;

    private final String gameId;

    private final String sessionId;

    private final int gridColumnToFill;

    private final int gridRowToFill;

    /**
     * @param gameId unique identifier of the Game
     * @param sessionId current session Id
     * @param playerName Name of the player playing the turn
     * @param gridColumnToFill column number of the grid cell to fill.
     * @param gridRowToFill row number of the grid cell to fill.
     */
    public PlayTurnRequest(final String gameId, final String sessionId, final String playerName, final int gridColumnToFill, final int gridRowToFill) {
        this.sessionId = sessionId;
        this.playerName = playerName;
        this.gameId = gameId;
        this.gridColumnToFill = gridColumnToFill;
        this.gridRowToFill = gridRowToFill;
    }

    /**
     *
     */
    public PlayTurnRequest() {
        //For json parsing
        super();
        sessionId = null;
        playerName = null;
        gameId = null;
        gridColumnToFill = -1;
        gridRowToFill = -1;
    }

    /**
     * @return the gameId
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * @return the gridColumnToFill
     */
    public int getGridColumnToFill() {
        return gridColumnToFill;
    }

    /**
     * @return the gridRowToFill
     */
    public int getGridRowToFill() {
        return gridRowToFill;
    }

    /**
     * @return the playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}