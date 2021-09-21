package ua.leirimnad;

public class PriorityNode<T> extends Node <T> {
    public int priority;
    public PriorityNode<T> next;

    public PriorityNode(T elem, int priority) {
        super(elem);
        this.priority = priority;
    }
}
