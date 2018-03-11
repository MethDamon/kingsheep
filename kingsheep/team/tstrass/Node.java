package kingsheep.team.tstrass;

import java.util.List;

public class Node<T> {
    private T state;
    private Node<T> parent;
    private List<Node<T>> children;
    private int moveFromParentToThis;

    public Node(T state, Node<T> parent) {
        this.state = state;
        this.parent = parent;
    }

    public T getState() {
        return state;
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public void setChildren(List<Node<T>> children) {
        this.children = children;
    }

    public int getMoveFromParentToThis() {
        return moveFromParentToThis;
    }

    public void setMoveFromParentToThis(int moveFromParentToThis) {
        this.moveFromParentToThis = moveFromParentToThis;
    }
}
