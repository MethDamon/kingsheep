package kingsheep.team.tstrass;

import kingsheep.Simulator;
import kingsheep.Type;

import java.util.ArrayList;
import java.util.List;

public class Sheep extends UzhShortNameCreature {

    public Sheep(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    @Override
    protected Field getGoalWhenNoMoreObjectives(Type[][] map) {
        // Return the field as goal that maximizes the heuristic distance to the enemy wolf and find the shortest path to it
        Field fieldOfEnemyWolf = getFieldOfEnemyWolf(map);
        float max = 0;
        Field fieldMax = null;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                Field tempField = new Field(x, y);
                float temp = getManhattan(tempField, fieldOfEnemyWolf);
                if (temp > max) {
                    max = temp;
                    fieldMax = tempField;
                }
            }
        }
        return fieldMax;
    }

    // Manhattan distance
    @Override
    protected float getHeuristic(Type[][] map, Field start, Field goal) {
        return getManhattan(start, goal) + getRhubarbFactor(goal) + getWolfFactor(map, goal) / 10;
    }

    private float getManhattan(Field start, Field goal) {
        int dX = Math.abs(start.getX() - goal.getX());
        int dY = Math.abs(start.getY() - goal.getY());
        return (dX + dY);
    }

    private float getWolfFactor(Type[][] map, Field goal) {
        Field fieldOfEnemyWolf = getFieldOfEnemyWolf(map);
        return getManhattan(goal, fieldOfEnemyWolf);
    }

    private Field getFieldOfEnemyWolf(Type[][] map) {
        List<Field> fieldList = new ArrayList<>();
        fieldList.addAll(getFieldsContainingTypes(map, new Type[]{Type.WOLF2}));
        return fieldList.get(0);
    }

    private float getRhubarbFactor(Field field) {
        if (field.getType() == Type.RHUBARB) {
            return -1;
        } else {
            return 0;
        }
    }

    protected void think(Type map[][]) {
        if (alive) {
            move = getMove(map, new Type[]{});
        }
    }
}
