package kingsheep.team.tstrass;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by kama on 04.03.16.
 */
public abstract class UzhShortNameCreature extends Creature {

    public UzhShortNameCreature(Type type, Simulator parent, int playerID, int x, int y) {
        super(type, parent, playerID, x, y);
    }

    public String getNickname(){
        return "shaun_the_sheep";
    }

    protected MapState parseMap(Type [][] map) {
        MapState mapState = new MapState();
        Map<Coordinate, Field> typeMap = new HashMap<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                Coordinate coordinate = new Coordinate(x, y);
                Field field = new Field(map[y][x]);
                typeMap.put(coordinate, field);
            }
        }
        mapState.setFields(typeMap);
        return mapState;
    }

    protected Move getMove(Type[] positiveObjectives, Type[] negativeObjectives, Type[][] map) {
        Node<Type[][]> currentNode = new Node<>(map, null);
        List<Node<Type[][]>> successors = getValidSuccessorStates(currentNode);
        successors = successors
                .stream()
                .filter(node -> node.getMoveFromParentToThis() != Move.WAIT.ordinal()).collect(Collectors.toList());
        if (successors.isEmpty()) {
            return Move.WAIT;
        }
        Node<Type[][]> bestSuccessor = Collections.min(successors, (o1, o2) -> {
            double evalO1 = getEval(o1.getState(), positiveObjectives, negativeObjectives);
            double evalO2 = getEval(o2.getState(), positiveObjectives, negativeObjectives);
            return Double.compare(evalO1, evalO2);
        });
       /* if (successors.stream().allMatch(node -> getEval(node.getState(), positiveObjectives, negativeObjectives)
                == getEval(bestSuccessor.getState(), positiveObjectives, negativeObjectives))) {
            Random random = new Random();
            System.out.println("Returning random move");
            return Move.values()[random.nextInt(5)];
        }*/
        System.out.println("Optimal move is " + Move.values()[bestSuccessor.getMoveFromParentToThis()]);
        return Move.values()[bestSuccessor.getMoveFromParentToThis()];
    }

    private List<Node<Type[][]>> getValidSuccessorStates(Node<Type[][]> parentNode) {
        List<Node<Type[][]>> validSuccessorStates = new ArrayList<>();
        List<Move> moves = new ArrayList<>();
        Collections.addAll(moves, Move.values());
        List<Move> validMoves = moves
                .stream()
                .filter(move -> isValidMove(parentNode.getState(), move))
                .collect(Collectors.toList());

        try {
            Coordinate oldCoordinates = getCoordinatesOfType(parentNode.getState(), this.type).get(0);
            int oldX = oldCoordinates.getX();
            int oldY = oldCoordinates.getY();
            for (Move move : validMoves) {
                int newX = 0;
                int newY = 0;
                if (move == Move.UP) {
                    newX = oldX;
                    newY = oldY - 1;
                } else if (move == Move.DOWN) {
                    newX = oldX;
                    newY = oldY + 1;
                } else if (move == Move.RIGHT) {
                    newX = oldX + 1;
                    newY = oldY;
                } else if (move == Move.LEFT) {
                    newX = oldX - 1;
                    newY = oldY;
                } else if (move == Move.WAIT) {
                    newX = oldX;
                    newY = oldY;
                }
                Type[][] parentMap = parentNode.getState();
                Type[][] successor = new Type[parentMap.length][parentMap[0].length];
                for (int i = 0; i < parentMap.length; i++) {
                    for (int j = 0; j < parentMap.length; j++) {
                        successor[i][j] = parentMap[i][j];
                    }
                }
                successor[oldY][oldX] = Type.EMPTY;
                successor[newY][newX] = this.type;
                Node<Type[][]> node = new Node<>(successor, parentNode);
                node.setMoveFromParentToThis(move.ordinal());
                validSuccessorStates.add(node);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(move + " is not a valid move for " + this.type + " now!");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Game was over");
        }
        return validSuccessorStates;
    }

    private boolean isValidMove(Type[][] map, Move move) {
        Type typeOnNewField = Type.EMPTY;

        try {
            if (move == Move.UP) {
                typeOnNewField = map[y - 1][x];
            } else if (move == Move.DOWN) {
                typeOnNewField = map[y + 1][x];
            } else if (move == Move.LEFT) {
                typeOnNewField = map[y][x - 1];
            } else if (move == Move.RIGHT) {
                typeOnNewField = map[y][x + 1];
            } else if (move == Move.WAIT) {
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }

        boolean isAccessibleForWolf;
        boolean isAccessibleForSheep = typeOnNewField == Type.EMPTY || typeOnNewField == Type.GRASS || typeOnNewField == Type.RHUBARB;
        if (playerID == 1) {
            isAccessibleForWolf = typeOnNewField == Type.EMPTY || (typeOnNewField == Type.GRASS) ||
                    typeOnNewField == Type.SHEEP2 || (typeOnNewField == Type.RHUBARB);

        } else {
            isAccessibleForWolf = typeOnNewField == Type.EMPTY || (typeOnNewField == Type.GRASS) ||
                    typeOnNewField == Type.SHEEP1 || (typeOnNewField == Type.RHUBARB);
        }

        if (isSheep()) {
            return isAccessibleForSheep;
        } else {
            return isAccessibleForWolf;
        }
    }

    private double getEval(Type[][] map, Type[] positiveObjectives, Type[] negativeObjectives) {
        return getDistanceFromObjectives(map, positiveObjectives) - getDistanceFromObjectives(map, negativeObjectives);
    }

    private double getDistanceFromObjectives(Type[][] map, Type[] objectives) {
        List<Double> distances = new ArrayList<>(objectives.length);

        for (Type objective : objectives) {
            distances.add(getDistanceFromObjective(map, objective));
        }

        OptionalDouble optionalDouble = distances.stream().mapToDouble(Double::doubleValue).average();
        return optionalDouble.isPresent() ? optionalDouble.getAsDouble() : 0;
    }

    private List<Coordinate> getCoordinatesOfType(Type[][] map, Type type) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == type) {
                    coordinates.add(new Coordinate(j, i));
                }
            }
        }
        return coordinates;
    }

    private double getDistanceFromObjective(Type[][] map, Type objective) {
        Coordinate coordinateOfThisCreature = getCoordinatesOfType(map, this.type).get(0);
        List<Coordinate> coordinatesOfObjectives = getCoordinatesOfType(map, objective);

        double sum = 0;
        for (Coordinate c : coordinatesOfObjectives) {
            int x1 = coordinateOfThisCreature.getX();
            int y1 = coordinateOfThisCreature.getY();
            int x2 = c != null ? c.getX() : 0;
            int y2 = c != null ? c.getY() : 0;

            int realX1 = Math.min(x1, x2);
            int realX2 = Math.max(x1, x2);
            int realY1 = Math.min(y1, y2);
            int realY2 = Math.max(y1, y2);

            int xDiff = realX2 - realX1;
            int yDiff = realY2 - realY1;

            double result = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
            //double result = xDiff / yDiff;
            sum += result;
        }

        return sum / coordinatesOfObjectives.size();
    }

    private void printMap(Type map[][]){
        for (int i = 0; i < map.length; ++i)
        {
            for (int j = 0; j < map[0].length; ++j)
            {
                System.out.print(map[i][j].ordinal());
            }
            System.out.println("");
        }
        System.out.println("-------------------");
    }

    private int getNumberOfFencesInBetween(Type[][] map, int realX1, int realX2, int realY1, int realY2) {
        int count = 0;
        for(int i = realY1; i < realY2; i++) {
            for(int j = realX1; j < realX2; j++) {
                if (map[i][j] == Type.FENCE)
                    count++;
            }
        }
        return count;
    }
}
