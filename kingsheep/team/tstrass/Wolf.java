package kingsheep.team.tstrass;

import kingsheep.*;

public class Wolf extends UzhShortNameCreature {

    public Wolf(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    protected void think(Type map[][]) {
        MapState mapState = parseMap(map);
        move = getMove(mapState);
    }


}
