package kingsheep.team.tstrass;

import kingsheep.Type;

public class Field {

    private double g;

    private double h;

    private Type typeOnThisField;

    public double getF() {
        return g + h;
    }

    Field(Type type) {
        this.typeOnThisField = type;
    }

    public Type getTypeOnThisField() {
        return typeOnThisField;
    }
}
