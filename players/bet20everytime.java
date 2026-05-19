package players;

import gamefiles.botInterface;
import gamefiles.readOnlyGameState;

public class bet20everytime implements botInterface{
    public int getBet(readOnlyGameState g) {
        return 20;
    }
}
