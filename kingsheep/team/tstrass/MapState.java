package kingsheep.team.tstrass;

import java.util.Map;

public class MapState {

    public Map<Coordinate, Field> getFields() {
        return fields;
    }

    public void setFields(Map<Coordinate, Field> fields) {
        this.fields = fields;
    }

    private Map<Coordinate, Field> fields;
}
