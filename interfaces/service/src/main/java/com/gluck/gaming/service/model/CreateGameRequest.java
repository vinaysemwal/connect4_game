package com.gluck.gaming.service.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Request class to be used for game creation.
 *
 * @author Vinay Semwal
 */
public class CreateGameRequest implements Serializable {

    private static final long serialVersionUID = -1336709549131674452L;

    private final String firstPlayerName;

    private final String secondPlayerName;

    /**
     * @param firstPlayerName first player name
     * @param secondPlayerName second player name
     */
    public CreateGameRequest(final String firstPlayerName, final String secondPlayerName) {
        this.firstPlayerName = firstPlayerName;
        this.secondPlayerName = secondPlayerName;
    }

    /**
     * Defining default constructor to support JSON parsing. This shouldn't be used in practice for development.
     */
    public CreateGameRequest() {
        //For JSON parsing
        firstPlayerName = null;
        secondPlayerName = null;
    }

    /**
     * @return the firstplayerName
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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}