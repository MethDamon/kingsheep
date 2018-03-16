package kingsheep.team.tstrass;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

import java.util.*;
import java.util.function.BinaryOperator;
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

    /*  I actually do A* reverse here, so I start from the objectives and look for the shortest path
        to the agent I am navigating
        So "start" and "goal" are actually the "wrong" way around
    */
    Move getMove(Type[][] map, Type[] objectives) {

        // Set of nodes that are already evaluated
        Set<Field> closedFields = new HashSet<>();

        // Fields that are currently discovered, but not yet evaluated
        Queue<Field> openFields = new PriorityQueue<>(
                (o1, o2) -> Float.compare(o1.getfCost(), o2.getfCost()));

        // For each node, which node it can most efficiently be reached from
        Map<Field, Field> cameFrom = new HashMap<>();

        // Create the goal node (= the field the agent is in at the moment)
        Field start = new Field(this.x, this.y);
        start.setType(this.type);
        start.setgCost(0);
        start.sethCost(0);
        openFields.add(start);

        List<Field> fieldsContainingObjective = getFieldsContainingTypes(map, objectives);
        Field goal;
        Optional<Field> goalOptional = fieldsContainingObjective.stream().min(new Comparator<Field>() {
            @Override
            public int compare(Field o1, Field o2) {
                return Float.compare(UzhShortNameCreature.this.getHeuristic(map, start, o1), UzhShortNameCreature.this.getHeuristic(map, start, o2));
            }
        });
        if (goalOptional.isPresent()) {
            goal = goalOptional.get();
            goal.setChildren(Collections.emptyList());
            goal.setParent(null);
            goal.sethCost(Float.MAX_VALUE);
        } else {
            // There are no more objective available, leave it to the subclasses to say what to do
            goal = getGoalWhenNoMoreObjectives(map);
        }

        while (!openFields.isEmpty()) {
            Field current = openFields.poll();

            System.out.println("Evaluating: " + "x: " + current.getX() + " y: " + current.getY());


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
                    child.setgCost(fieldMatchingCoordinates.getgCost());
                    child.sethCost(fieldMatchingCoordinates.gethCost());
                }

                float distance = 1;
                float tentativeG = distance + current.getgCost();

                if (tentativeG < child.getgCost()) {
                    child.setgCost(tentativeG);
                    child.sethCost(getHeuristic(map, child, goal));

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
            System.out.println (current.getX() + " " + current.getY() + " " + Move.values()[current.getMoveFromParentToThis()] + " " + current.getType());
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

    private boolean hasDuplicate(Collection<Field> fields) {
        return fields.stream().reduce(new BinaryOperator<Field>() {
            @Override
            public Field apply(Field field, Field field2) {
                if (hasTheSameCoordinates(field, field2))
                    return field;
                else
                    return null;
            }
        }).isPresent();
    }

    private boolean isIn(Collection<Field> fieldSet, Field f1) {
        return fieldSet.stream().anyMatch(field -> hasTheSameCoordinates(f1, field));
    }

    private boolean hasTheSameCoordinates(Field f1, Field f2) {
        return f1.getX() == f2.getX() && f1.getY() == f2.getY();
    }

    private Field getGotHereFrom(Field field, Map<Field, Field> cameFrom) {
        Optional<Field> result = cameFrom.keySet().stream().filter(f -> field.getX() == f.getX() && field.getY() == f.getY()).findFirst();
        if (result.isPresent()) {
            return cameFrom.get(result);
        }
        return null;
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

    protected abstract float getHeuristic(Type[][] map, Field start, Field goal);

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
                // This will throw an ArrayOutOfBoundsException if the move is not on the field anymore
                fieldAfterMove.setType(map[fieldAfterMove.getY()][fieldAfterMove.getX()]);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Invalid move: " + move);
            }

            validMoves.add(fieldAfterMove);
        }

        // Filter out fence fields
        return validMoves.stream().filter(field -> field.getType() != Type.FENCE).collect(Collectors.toList());
    }
}
