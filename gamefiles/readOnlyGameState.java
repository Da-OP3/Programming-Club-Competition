package gamefiles;

import java.util.ArrayList;

public class readOnlyGameState {
    private int myTokens;
    private int opponentTokens;
    private int currentRound;
    private int myBet;
    private int opponentBet;
    private ArrayList<Integer>[] state;

    public readOnlyGameState(int myTokens, int opponentTokens, int currentRound, int myBet, int opponentBet, ArrayList<Integer>[] state){
        this.myTokens = myTokens;
        this.opponentTokens = opponentTokens;
        this.currentRound = currentRound;
        this.myBet = myBet;
        this.opponentBet = opponentBet;
        this.state = state;
    }

    public int getMyTokens(){
        return myTokens;
    }
    public int getOpponentTokens(){
        return opponentTokens;
    }
    public int getCurrentRound(){
        return currentRound;
    }
    public int getMyPreviousBet(){
        return myBet;
    }
    public int getOpponentPreviousBet(){
        return opponentBet;
    }

    /**
     * 
     * @return A size-2 array of ArrayLists, each of which have an entry per round with the player's bet.
     */
    public ArrayList<Integer>[] getFullState(){
        ArrayList<Integer>[] copy = state.clone();
        copy[0] = new ArrayList<>(state[0]);
        copy[1] = new ArrayList<>(state[1]);
        return copy;
    }

}
