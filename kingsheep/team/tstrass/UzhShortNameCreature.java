package kingsheep.team.tstrass;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

import java.util.HashMap;
import java.util.Map;

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

    protected Move getMove(MapState mapState) {
        printMapState(mapState);
        return Move.UP;
    }

    private void printMapState(MapState mapState) {
        Map<Coordinate, Field> fields = mapState.getFields();
        for(Coordinate coordinate : fields.keySet()) {
            System.out.println(fields.get(coordinate).getType());
        }
    }
}
