package ua.leirimnad;

public interface PriorityQueue <T> {
    /**
     * Pushes an element into the queue, on the place that matches the priority.
     * The greater the param "priority" is, the greater the element priority gets.
     * @param elem Element to be pushed
     * @param priority Int, priority of the element in queue
     */
    void push(T elem, int priority);

    /**
     * Removes the element from the queue that has the highest priority, and returns it.
     * If there are no elements - returns null.
     * @return Element with the highest priority or null
     */
    T pop();

    /**
     * Returns the highest-priority element but does not modify the queue.
     * If there are no elements - returns null.
     * @return Element with the highest priority or null
     */
    T top();
}

