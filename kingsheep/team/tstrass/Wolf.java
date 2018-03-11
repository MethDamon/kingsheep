package kingsheep.team.tstrass;

import kingsheep.*;

public class Wolf extends UzhShortNameCreature {

    public Wolf(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    protected void think(Type map[][]) {
        Type[] positiveObjectives;
        if (playerID == 1) {
             positiveObjectives = new Type[]{Type.SHEEP2};
        } else {
            positiveObjectives = new Type[]{Type.SHEEP2};
        }
        move = getMove(positiveObjectives, new Type[]{}, map);
        //move = Move.WAIT;
    }
}
