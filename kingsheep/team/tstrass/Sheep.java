package kingsheep.team.tstrass;

import kingsheep.Simulator;
import kingsheep.Type;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
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
        PriorityQueue<Field> validFields = new PriorityQueue<>(
                (o1, o2) -> -Float.compare(getManhattan(o1, fieldOfEnemyWolf), getManhattan(o2, fieldOfEnemyWolf)));

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                Field tempField = new Field(x, y);
                tempField.setType(map[y][x]);
                if (isValidField(tempField, map) && tempField.getType() != Type.FENCE) {
                    validFields.add(tempField);
                }
            }
        }

        System.out.println(validFields.peek().getX() + " " + validFields.peek().getY());

        return validFields.poll();
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

    private boolean isValidField(Field field, Type[][] map) {
        return !hasTheSameCoordinates(field, getFieldOfEnemyWolf(map)) && !couldBeEatenByWolfInNextTurnOnThatField(field, map);
    }

    @Override
    protected List<Field> filterOutInvalidMoves(List<Field> validMoves, Type[][] map) {
        return validMoves.stream().filter(field -> isValidField(field, map)).collect(Collectors.toList());
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
        } else {
            return false;
        }
    }

    private boolean couldBeEatenByWolfInSecondNextTurnOnThatField(Field field, Type[][] map) {
        Field fieldOfWolf = getFieldOfEnemyWolf(map);

            Field up = new Field(fieldOfWolf.getX(), fieldOfWolf.getY() - 2);
            Field down = new Field(fieldOfWolf.getX(), fieldOfWolf.getY() + 2);
            Field left = new Field(fieldOfWolf.getX() - 2, fieldOfWolf.getY());
            Field right = new Field(fieldOfWolf.getX() + 2, fieldOfWolf.getY());

            boolean isDangerous = hasTheSameCoordinates(field, up) || hasTheSameCoordinates(field, down)
                    || hasTheSameCoordinates(field, left) || hasTheSameCoordinates(field, right);

            return isDangerous;

    }

    protected void think(Type map[][]) {
        if (moveNr == 3) {
            moveNr = 1;
        }

        if (alive) {
            move = getMove(map, new Type[]{Type.GRASS, Type.RHUBARB});
            System.out.println(move);
            moveNr++;
        }
    }
}
