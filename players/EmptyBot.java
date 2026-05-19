package players;

import gamefiles.botInterface;
import gamefiles.readOnlyGameState;

/**
 * Template bot for competitors.
 *
 * Rules:
 * - The bot may use the provided readOnlyGameState to decide its bet.
 * - It must return an integer bet for each round.
 * - Valid bets are > 5 if the bot has more than 5 tokens
 * - If the bot has less than 5, bets must be at least 1.
 * - If the bot returns an invalid bet, it is immediately eliminated from the match.
 * - Keep logic deterministic, rng is not allowed for fairness.
 * 
 * What coders can do here:
 * - Read the current round number with g.getCurrentRound().
 * - Use g.getMyTokens() and g.getOpponentTokens() to size bets.
 * - Inspect the opponent's previous bet with g.getOpponentPreviousBet().
 * - Use g.getFullState() to see the full history of bets if desired.
 *
 * Notes:
 * - This file is a safe entry point for bot development.
 * - The game engine will only call getBet() once per round.
 */
public class EmptyBot implements botInterface{
    public int getBet(readOnlyGameState g) {
        return 0;
    }
}
