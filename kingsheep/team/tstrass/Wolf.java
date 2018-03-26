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
        if (playerID == 1) {
            move = getMove(map, new Type[]{Type.SHEEP2});
        } else {
            move = getMove(map, new Type[]{Type.SHEEP1});
        }
    }

    @Override
    protected float getHeuristic(boolean noMoreGoalsLeft, Type[][] map, Field start, Field goal) {
        float manhattan = getManhattan(start, goal);

        if (noMoreGoalsLeft) {
            return manhattan;
        }

        float rhubarbFactor = goal.getType() == Type.RHUBARB ? -4 : 0;
        float grassFactor = goal.getType() == Type.GRASS ? -1 : 0;
        float x = manhattan + rhubarbFactor + grassFactor;

        float a = -4;
        float b = 32;

        float c = 0;
        float d = 32;

        float result = getNormalizedValue(x, a, b, c, d);
        return result;
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
