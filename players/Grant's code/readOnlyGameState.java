import java.util.ArrayList;

public class readOnlyGameState {

    // state[0] = my bets history, state[1] = opponent's bets history
    private ArrayList<Integer>[] state;
    private int myTokens;
    private int opponentTokens;
    private int currentRound;

    @SuppressWarnings("unchecked")
    public readOnlyGameState(int myTokens, int opponentTokens, int currentRound,
                             ArrayList<Integer> myBets, ArrayList<Integer> opponentBets) {
        this.myTokens = myTokens;
        this.opponentTokens = opponentTokens;
        this.currentRound = currentRound;
        this.state = new ArrayList[2];
        this.state[0] = new ArrayList<>(myBets);
        this.state[1] = new ArrayList<>(opponentBets);
    }

    /** Returns your current token count. */
    public int getMyTokens() {
        return myTokens;
    }

    /** Returns the opponent's current token count. */
    public int getOpponentTokens() {
        return opponentTokens;
    }

    /** Returns the current round number (1-indexed). */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * Returns the opponent's bet in the most recent completed round,
     * or 0 if no rounds have been completed yet.
     */
    public int getOpponentBet() {
        if (state[1].isEmpty()) return 0;
        return state[1].get(state[1].size() - 1);
    }

    /**
     * Returns your bet in the most recent completed round,
     * or 0 if no rounds have been completed yet.
     */
    public int getMyBet() {
        if (state[0].isEmpty()) return 0;
        return state[0].get(state[0].size() - 1);
    }

    /**
     * Returns the full bet history.
     * state[0] = list of all your bets (one per round completed so far)
     * state[1] = list of all opponent's bets (one per round completed so far)
     */
    public ArrayList<Integer>[] getFullState() {
        return state;
    }
}
