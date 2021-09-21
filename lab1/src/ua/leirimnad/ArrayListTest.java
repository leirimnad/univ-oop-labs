package ua.leirimnad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayListTest {

    @Test
    void pop() {
        PriorityQueue<Integer> myQueue = new ArrayList<>();
        myQueue.push(16, 1);
        myQueue.push(2361, 228);
        myQueue.push(246, 1);
        assertEquals(2361, myQueue.pop());
        assertEquals(16, myQueue.pop());
        assertEquals(246, myQueue.pop());
        assertNull(myQueue.pop());
    }

    @Test
    void top() {
        PriorityQueue<Integer> myQueue = new ArrayList<>();
        myQueue.push(16, 1);
        assertEquals(16, myQueue.top());
        myQueue.push(2361, 228);
        assertEquals(2361, myQueue.top());
        myQueue.push(246, 1);
        assertEquals(2361, myQueue.top());
    }
}