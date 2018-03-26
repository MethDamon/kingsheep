package kingsheep.team.tstrass;

import kingsheep.Simulator;
import kingsheep.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sheep extends UzhShortNameCreature {

    int moveNr = 1;

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
        System.out.println(fieldMax.getX() + " " + fieldMax.getY());
        return fieldMax;
    }

    @Override
    protected float getHeuristic(Type[][] map, Field start, Field goal) {
        float manhattan = getManhattan(start, goal);
        float rhubarbFactor = getRhubarbFactor(goal);
        float x = manhattan + 5*rhubarbFactor + getWolfFactor(map, goal);

        float a = -5;
        float b = 32;

        float c = 0;
        float d = 1;

        float result = getNormalizedValue(x, a, b, c, d);
        return result;
    }

    @Override
    protected List<Field> filterOutInvalidMoves(List<Field> validMoves, Type[][] map) {
        return validMoves.stream().filter(field -> {
            Type type = field.getType();
            if (playerID == 1) {
                return type != Type.WOLF2 && type != Type.WOLF1 && type != Type.SHEEP2 && !couldBeEatenByWolfInNextTurnOnThatField(field, map);
            } else {
                return type != Type.WOLF2 && type != Type.WOLF1 && type != Type.SHEEP1 && !couldBeEatenByWolfInNextTurnOnThatField(field, map);
            }
        }).collect(Collectors.toList());
    }

    // Transforms x from range [a,b] to [c,d]
    private float getNormalizedValue(float x, float a, float b, float c, float d) {
        return ((x - a) * ((d - c) / (b - a))) + c;
    }

    private float getDistanceFromWolf(Type[][] map) {
        Field fieldOfWolf = getFieldOfEnemyWolf(map);
        return getManhattan(new Field(this.x, this.y), fieldOfWolf);
    }

    private float getWolfFactor(Type[][] map, Field goal) {
        Field fieldOfEnemyWolf = getFieldOfEnemyWolf(map);
        float manhattan = getManhattan(goal, fieldOfEnemyWolf);
        return manhattan;
    }

    private Field getFieldOfEnemyWolf(Type[][] map) {
        List<Field> fieldList = new ArrayList<>();
        if (playerID == 1) {
            fieldList.addAll(getFieldsContainingTypes(map, new Type[]{Type.WOLF2}));
        } else {
            fieldList.addAll(getFieldsContainingTypes(map, new Type[]{Type.WOLF1}));
        }
        return fieldList.get(0);
    }

    private float getRhubarbFactor(Field field) {
        if (field.getType() == Type.RHUBARB) {
            return -1;
        } else {
            return 0;
        }
    }

    private boolean couldBeEatenByWolfInNextTurnOnThatField(Field field, Type[][] map) {
        Field fieldOfWolf = getFieldOfEnemyWolf(map);

        if (moveNr == 2) {
            Field up = new Field(fieldOfWolf.getX(), fieldOfWolf.getY() - 1);
            Field down = new Field(fieldOfWolf.getX(), fieldOfWolf.getY() + 1);
            Field left = new Field(fieldOfWolf.getX() - 1, fieldOfWolf.getY());
            Field right = new Field(fieldOfWolf.getX() + 1, fieldOfWolf.getY());

            boolean isDangerous = hasTheSameCoordinates(field, up) || hasTheSameCoordinates(field, down)
                    || hasTheSameCoordinates(field, left) || hasTheSameCoordinates(field, right);

            return isDangerous;
        }
        return false;
    }

    protected void think(Type map[][]) {
        if (moveNr == 3) {
            moveNr = 1;
        }

        if (alive) {
            move = getMove(map, new Type[]{Type.GRASS});
            System.out.println(move);
            moveNr++;
        }
    }
}
