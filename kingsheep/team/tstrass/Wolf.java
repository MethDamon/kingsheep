package kingsheep.team.tstrass;

import kingsheep.Simulator;
import kingsheep.Type;

import java.util.List;
import java.util.stream.Collectors;

public class Wolf extends UzhShortNameCreature {

    public Wolf(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    @Override
    protected Field getGoalWhenNoMoreObjectives(Type[][] map) {
        return null;
    }

    protected void think(Type map[][]) {
/*        if (playerID == 1) {
            move = getMove(map, new Type[]{Type.SHEEP2});
        } else {
            move = getMove(map, new Type[]{Type.SHEEP1});
        }*/
        move = Move.WAIT;
    }

    // Manhattan distance
    @Override
    protected float getHeuristic(Type[][] map, Field start, Field goal) {
        int dX = Math.abs(start.getX() - goal.getX());
        int dY = Math.abs(start.getY() - goal.getY());
        return dX + dY;
    }

    @Override
    protected List<Field> filterOutInvalidMoves(List<Field> validMoves, Type[][] map) {
        return validMoves.stream().filter(field -> {
            if (playerID == 1) {
                return field.getType() != Type.SHEEP1 && field.getType() != Type.WOLF2;
            } else {
                return field.getType() != Type.SHEEP2 && field.getType() != Type.WOLF1;
            }
        }).collect(Collectors.toList());
    }
}
