package ua.leirimnad;

public class PriorityBinaryTreeNode<T> extends Node <T> {
    public int priority;
    public PriorityBinaryTreeNode<T> left, right;

    public PriorityBinaryTreeNode(T elem, int priority) {
        super(elem);
        this.priority = priority;
        this.left = this.right = null;
    }
}
