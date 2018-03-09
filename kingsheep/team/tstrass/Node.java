package kingsheep.team.tstrass;

import java.util.List;

public class Node<T> {
    private MapState mapState;
    private Node<T> parent;
    private List<Node<T>> children;
}
