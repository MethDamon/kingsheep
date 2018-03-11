package kingsheep.team.tstrass;

import kingsheep.Type;

public class Field {

    private Type typeOnThisField;

    public Field(Type type) {
        this.typeOnThisField = type;
    }

    public Type getTypeOnThisField() {
        return typeOnThisField;
    }
}
