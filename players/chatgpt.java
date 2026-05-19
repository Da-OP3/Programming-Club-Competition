package players;

import gamefiles.botInterface;
import gamefiles.readOnlyGameState;

public class chatgpt implements botInterface {
    public int getBet(readOnlyGameState g) {
        int myTokens = g.getMyTokens();
        int oppTokens = g.getOpponentTokens();
        int round = g.getCurrentRound();
        int oppBet = g.getOpponentPreviousBet();
        int base = Math.max(5, Math.min(20, myTokens / 5));

        if (round == 0) {
            return Math.min(10, myTokens);
        }

        if (myTokens > oppTokens + 10) {
            return Math.min(myTokens, oppBet + 5);
        }

        if (myTokens < oppTokens - 10) {
            return Math.min(myTokens, oppBet + 10);
        }

        if (oppBet >= 15) {
            return Math.min(myTokens, oppBet + 1);
        }

        if (round > 10 && myTokens > oppTokens) {
            return Math.min(myTokens, base + 5);
        }

        return Math.min(myTokens, Math.max(5, oppBet));
    }
}
