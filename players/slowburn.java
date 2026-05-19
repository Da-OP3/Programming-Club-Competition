package players;

import gamefiles.botInterface;
import gamefiles.readOnlyGameState;

public class slowburn implements botInterface {
    public int getBet(readOnlyGameState g) {
        if (g.getCurrentRound() < 10) {
            return 5;
        }
        return Math.min(20, g.getMyTokens());
    }
}
