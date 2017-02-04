package com.gluck.gaming.service.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Game Data Response
 *
 * @author Vinay Semwal
 */
public class GameDataResponse implements Serializable {

    private static final long serialVersionUID = -6914842922856552386L;

    private final GameData gameData;

    /**
     * @param gameData {@link GameData}
     */
    public GameDataResponse(final GameData gameData) {
        super();
        this.gameData = gameData;

    }

    /**
     * @return the gameData
     */
    public GameData getGameData() {
        return gameData;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
