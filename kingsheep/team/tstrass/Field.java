package kingsheep.team.tstrass;

import kingsheep.Type;

import java.util.List;
import java.util.UUID;

final class Field {
    private UUID uuid;
    private int x;
    private int y;
    private float gCost;
    private float hCost;
    private float fCost;
    private Field parent;
    private List<Field> children;
    private int moveFromParentToThis;
    private Type type;

    public Field(int x, int y) {
        this.x = x;
        this.y = y;
        this.gCost = Float.MAX_VALUE;
        this.uuid = UUID.nameUUIDFromBytes((String.valueOf(x) + String.valueOf(y)).getBytes());
    }

    public UUID getUuid() {
        return uuid;
    }

    public float getfCost() {
        return gCost + hCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        if (x != field.x) return false;
        if (y != field.y) return false;
        return type == field.type;
    }

    @Override
    public String toString() {
        return "Field{" +
                ", x=" + x +
                ", y=" + y + "}";
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getgCost() {
        return gCost;
    }

    public void setgCost(float gCost) {
        this.gCost = gCost;
    }

    public float gethCost() {
        return hCost;
    }

    public void sethCost(float hCost) {
        this.hCost = hCost;
    }

    public Field getParent() {
        return parent;
    }

    public void setParent(Field parent) {
        this.parent = parent;
    }

    public List<Field> getChildren() {
        return children;
    }

    public void setChildren(List<Field> children) {
        this.children = children;
    }

    public int getMoveFromParentToThis() {
        return moveFromParentToThis;
    }

    public void setMoveFromParentToThis(int moveFromParentToThis) {
        this.moveFromParentToThis = moveFromParentToThis;
    }
}
