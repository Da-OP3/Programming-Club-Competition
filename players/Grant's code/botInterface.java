public interface botInterface {
    /**
     * Returns the bot's name (used for display/logging).
     */
    String getName();

    /**
     * Returns the bet for this round.
     * Must be >= 5 if myTokens > 5, must be > 0, and must be <= myTokens.
     *
     * @param state a read-only view of the current game state
     * @return the number of tokens to bet this round
     */
    int getBet(readOnlyGameState state);
}
