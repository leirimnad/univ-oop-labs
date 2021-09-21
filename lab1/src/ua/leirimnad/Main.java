package ua.leirimnad;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting");
        PriorityQueue<Integer> myQueue = new LinkedList<>();
        myQueue.push(123, 1);
        myQueue.push(52, 3);
        myQueue.push(53, 3);
        myQueue.push(25, 2);
        System.out.println("Popped "+myQueue.pop());
        System.out.println("Popped "+myQueue.pop());
        System.out.println("Popped "+myQueue.pop());
        System.out.println("Popped "+myQueue.pop());
    }

}
