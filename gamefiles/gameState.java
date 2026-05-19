package gamefiles;

import java.util.ArrayList;

public class gameState {
    public int p1Tokens;
    public int p2Tokens;
    public int currentRound;
    public int p1bet;
    public int p2bet;
    public ArrayList<Integer>[] state;

    @SuppressWarnings("unchecked")
    public gameState(int startingTokens){
        p1Tokens = p2Tokens = startingTokens;
        currentRound = 0;
        state = (ArrayList<Integer>[]) new ArrayList[2];
        state[0] = new ArrayList<>();
        state[1] = new ArrayList<>();
    }

    public readOnlyGameState getReadOnly(boolean player1){

        if (player1) return new readOnlyGameState(p1Tokens, p2Tokens, currentRound, p1bet, p2bet, state);

        ArrayList<Integer>[] state2 = state.clone();
        state2[0] = state[1];
        state2[1] = state[0];

        return new readOnlyGameState(p2Tokens, p1Tokens, currentRound, p2bet, p1bet, state);
    }

    /**
     * 
     * @param p1bet the amount p1 bet this turn
     * @param p2bet the amount p2 bet this turn
     */
    public void newTurn(int p1bet, int p2bet){
        state[0].add(p1bet);
        state[1].add(p2bet);
    }

}

