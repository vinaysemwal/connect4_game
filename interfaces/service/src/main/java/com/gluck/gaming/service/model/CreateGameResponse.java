package com.gluck.gaming.service.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Data object to hold Create Game API response
 *
 * @author Vinay Semwal
 */
public class CreateGameResponse implements Serializable {

    private static final long serialVersionUID = -1122619357955490492L;

    private final String gameId;

    private final String sessionId;

    private final GameState gameState;

    /**
     * @param gameId unique identifier of the newly created Game
     * @param sessionId unique identifier of the current play session
     * @param gameState Current State of the Game
     */
    public CreateGameResponse(final String gameId, final String sessionId, final GameState gameState) {
        super();
        this.gameId = gameId;
        this.sessionId = sessionId;
        this.gameState = gameState;
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
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
