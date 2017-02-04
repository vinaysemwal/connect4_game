package com.gluck.gaming.service.model;

/**
 * Enumeration specifying the possible states that a Game could be in during its life cycle. COMPLETED,DRAWN and ABANDONED are terminal states meaning that Game
 * cannot be played again if it has reached any of these states. While a game could be in IN_PROGRESS and SUSPENDED states back and forth.
 *
 * @author Vinay Semwal
 */
public enum GameState {

    /**
     * Indicates a new game first turn of which is yet to be played.
     */
    NEW,

    /**
     * Indicates a game that is in progress.
     */
    IN_PROGRESS,

    /**
     * Indicates a game that has been temporarily suspended on session close.
     */
    SUSPENDED,

    /**
     * Indicates a game that has been abandoned before completion.
     */
    ABANDONED,

    /**
     * Indicates a game in which all turns have been played and one of the players has won.
     */
    COMPLETED,

    /**
     * Indicates a game in which all turns have been played and none of the players won.
     */
    DRAWN;

    /**
     * Specifies if Game state transition from source state to target state is valid.
     *
     * @param sourceState state from which Game is to transition
     * @param targetState state to which Game is to transition.
     * @return true if transition is valid else false.
     */
    public static boolean isStateTransitionValid(final GameState sourceState, final GameState targetState) {
        switch (sourceState) {
            case NEW:
                return targetState.equals(IN_PROGRESS) || targetState.equals(ABANDONED) || targetState.equals(SUSPENDED);
            case IN_PROGRESS:
                return targetState.equals(ABANDONED) || targetState.equals(COMPLETED) || targetState.equals(DRAWN) || targetState.equals(SUSPENDED);
            case SUSPENDED:
                return targetState.equals(ABANDONED) || targetState.equals(IN_PROGRESS);
            case ABANDONED:
            case COMPLETED:
            case DRAWN:
                return false;
            default:
                throw new EnumConstantNotPresentException(GameState.class, "No Game State is defined as :" + sourceState);

        }

    }

}
