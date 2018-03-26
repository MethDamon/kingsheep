package kingsheep.team.tstrass;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kama on 04.03.16.
 */
public abstract class UzhShortNameCreature extends Creature {

    public UzhShortNameCreature(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    public String getNickname() {
        return "shaun_the_sheep";
    }

    // A*
    Move getMove(Type[][] map, Type[] objectives) {

        // Set of nodes that are already evaluated
        Set<Field> closedFields = new HashSet<>();

        // Fields that are currently discovered, but not yet evaluated
        Queue<Field> openFields = new PriorityQueue<>(
                (o1, o2) -> Float.compare(o1.calculateFCost(), o2.calculateFCost()));

        // For each node, which node it can most efficiently be reached from
        Map<Field, Field> cameFrom = new HashMap<>();

        boolean noMoreGoalsLeft = false;

        Field start = new Field(this.x, this.y);
        start.setType(this.type);
        start.setgCost(0);
        start.sethCost(0);
        openFields.add(start);

        List<Field> fieldsContainingObjective = getFieldsContainingTypes(map, objectives);

        Field goal = new Field(this.x, this.y);

        if (fieldsContainingObjective.isEmpty()) {
            noMoreGoalsLeft = true;
            // There are no more objective available, leave it to the subclasses to say what to do
            goal = getGoalWhenNoMoreObjectives(map);
            goal.setChildren(Collections.emptyList());
            goal.setParent(null);
            goal.sethCost(Float.MAX_VALUE);
        } else {
            boolean finalNoMoreGoalsLeft = false;
            Optional<Field> goalOptional = fieldsContainingObjective.stream().min((o1, o2)
                    -> Float.compare(UzhShortNameCreature.this.getHeuristic(finalNoMoreGoalsLeft, map, start, o1), UzhShortNameCreature.this.getHeuristic(finalNoMoreGoalsLeft, map, start, o2)));
            if (goalOptional.isPresent()) {
                goal = goalOptional.get();
                goal.setChildren(Collections.emptyList());
                goal.setParent(null);
                goal.sethCost(Float.MAX_VALUE);
            }
        }

        while (!openFields.isEmpty()) {
            Field current = openFields.poll();

            if (hasTheSameCoordinates(current, goal)) {
                List<Field> totalPath = reconstructPath(cameFrom, current);
                if (totalPath.size() == 0) {
                    return Move.values()[current.getMoveFromParentToThis()];
                }
                Move firstMoveTowardsGoal;
                firstMoveTowardsGoal = Move.values()[totalPath.get(totalPath.size() - 1).getMoveFromParentToThis()];
                return firstMoveTowardsGoal;
            }

            closedFields.add(current);

            for (Field child : getValidMovesFromField(current, map)) {

                if (isIn(closedFields, child)) continue; // Ignore the already evaluated field

                if (!isIn(openFields, child)) {
                    openFields.add(child);
                } else {
                    Field fieldMatchingCoordinates = getFieldWithMatchingCoordinates(openFields, child);
                    if (fieldMatchingCoordinates != null) {
                        child.setgCost(fieldMatchingCoordinates.getgCost());
                        child.sethCost(fieldMatchingCoordinates.gethCost());
                    }
                }

                float distance = 1;
                float tentativeG = distance + current.getgCost();

                if (tentativeG < child.getgCost()) {
                    child.setgCost(tentativeG);
                    child.sethCost(getHeuristic(noMoreGoalsLeft, map, child, goal));

                    if (!isIn(cameFrom.keySet(), child)) {
                        cameFrom.put(child, current);
                    } else {
                        cameFrom.remove(child);
                        cameFrom.put(child, current);
                    }
                }
            }
        }
        return Move.WAIT;
    }

    protected abstract Field getGoalWhenNoMoreObjectives(Type[][] map);

    private List<Field> reconstructPath(Map<Field, Field> cameFrom, Field current) {
        List<Field> path = new ArrayList<>();
        while (current.getParent() != null) {
            current = getFieldWithMatchingCoordinates(cameFrom.keySet(), current.getParent());
            if (current == null)
                break;
            path.add(current);
        }
        return path;
    }

    private Field getFieldWithMatchingCoordinates(Collection<Field> openFields, Field child) {
        for (Field field : openFields) {
            if (hasTheSameCoordinates(field, child)) {
                return field;
            }
        }
        return null;
    }

    private boolean isIn(Collection<Field> fieldSet, Field f1) {
        return fieldSet.stream().anyMatch(field -> hasTheSameCoordinates(f1, field));
    }

    protected boolean hasTheSameCoordinates(Field f1, Field f2) {
        return f1.getX() == f2.getX() && f1.getY() == f2.getY();
    }

    protected List<Field> getFieldsContainingTypes(Type[][] map, Type[] objectives) {
        List<Field> result = new ArrayList<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (Arrays.asList(objectives).contains(map[y][x])) {
                    Field field = new Field(x, y);
                    field.setY(y);
                    field.setX(x);
                    field.setType(map[y][x]);
                    result.add(field);
                }
            }
        }
        return result;
    }

    protected abstract float getHeuristic(boolean noMoreGoalsLeft, Type[][] map, Field start, Field goal);

    private List<Field> getValidMovesFromField(Field start, Type[][] map) {

        List<Field> validMoves = new ArrayList<>();

        for (Move move : Move.values()) {
            int x = start.getX();
            int y = start.getY();

            if (move == Move.UP) {
                y--;
            } else if (move == Move.DOWN) {
                y++;
            } else if (move == Move.LEFT) {
                x--;
            } else if (move == Move.RIGHT) {
                x++;
            }

            Field fieldAfterMove = new Field(x, y);
            fieldAfterMove.setMoveFromParentToThis(move.ordinal());
            fieldAfterMove.setChildren(Collections.emptyList());
            fieldAfterMove.setParent(start);

            try {
                // This will throw an ArrayOutOfBoundsException if the moveNr is not on the field anymore
                fieldAfterMove.setType(map[fieldAfterMove.getY()][fieldAfterMove.getX()]);
                validMoves.add(fieldAfterMove);
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
        }

        // Filter out fence fields
        validMoves = validMoves.stream().filter(field -> field.getType() != Type.FENCE).collect(Collectors.toList());

        // Let the subclasses filter out some other moves
        return filterOutInvalidMoves(validMoves, map);
    }


    float getManhattan(Field start, Field goal) {
        int dX = Math.abs(start.getX() - goal.getX());
        int dY = Math.abs(start.getY() - goal.getY());
        return (dX + dY);
    }

    // Transforms x from range [a,b] to [c,d]
    float getNormalizedValue(float x, float a, float b, float c, float d) {
        return ((x - a) * ((d - c) / (b - a))) + c;
    }

    protected abstract List<Field> filterOutInvalidMoves(List<Field> validMoves, Type[][] map);
}
