package players;

import gamefiles.botInterface;
import gamefiles.readOnlyGameState;

public class copycatplusone implements botInterface{
    public int getBet(readOnlyGameState g) {
        if (g.getCurrentRound() == 0)
            return 20;

        return g.getOpponentPreviousBet() + 1;
    }
}
