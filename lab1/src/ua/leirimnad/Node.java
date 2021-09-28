package ua.leirimnad;

/**
 * A node of a linked list.
 * @param <T> Type of data in a node
 */
public class Node <T>{
    /**
     * Data, stored in a node.
     */
    public T data;
    /**
     * Reference to the next Node in a linked list
     */
    public Node<T> next;

    /**
     * Constructs a node with given data, next node sets to null.
     * @param elem Data to be stored in the node
     */
    public Node(T elem){
        this.data = elem;
        this.next = null;
    }
}

