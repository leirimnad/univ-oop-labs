package ua.leirimnad;

public class Node <T>{
    public T data;
    public Node<T> next;

    public Node(T elem){
        this.data = elem;
        this.next = null;
    }
}

