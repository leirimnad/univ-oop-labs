package ua.leirimnad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinaryTreeTest {

    @Test
    void pop() {

        PriorityQueue<Integer> myQueue = new BinaryTree<>();
        assertNull(myQueue.pop());
        myQueue.push(16, 1);
        assertEquals(16, myQueue.pop());
        myQueue.push(123, 1);
        myQueue.push(23, 2);
        myQueue.push(243, 2);
        assertEquals(23, myQueue.pop());
        assertEquals(243, myQueue.pop());
        assertEquals(123, myQueue.pop());
        assertNull(myQueue.pop());
    }

    @Test
    void top() {
        PriorityQueue<Integer> myQueue = new ArrayList<>();
        assertNull(myQueue.top());
        myQueue.push(16, 1);
        assertEquals(16, myQueue.top());
        myQueue.push(2361, 228);
        assertEquals(2361, myQueue.top());
        myQueue.push(246, 1);
        assertEquals(2361, myQueue.top());
    }
}