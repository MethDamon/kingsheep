package kingsheep.team.tstrass;

import kingsheep.*;

public class Wolf extends UzhShortNameCreature {

    public Wolf(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    @Override
    protected Field getGoalWhenNoMoreObjectives(Type[][] map) {
        return null;
    }

    protected void think(Type map[][]) {
        move = getMove(map, new Type[]{Type.SHEEP2});
    }

    // Manhattan distance
    @Override
    protected float getHeuristic(Type[][] map, Field start, Field goal) {
        int dX = Math.abs(start.getX() - goal.getX());
        int dY = Math.abs(start.getY() - goal.getY());
        return dX + dY;
    }
}
