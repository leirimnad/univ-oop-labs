package ua.leirimnad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeapTest {

    @Test
    void pop() {
        PriorityQueue<Integer> myQueue = new Heap<>();
        myQueue.push(17, 1);
        myQueue.push(2361, 228);
        myQueue.push(245, 2136);
        myQueue.push(16, 135);
        myQueue.push(2362, 2438);
        myQueue.push(246, 2438);
        myQueue.push(19, 228);
        myQueue.push(2360, 228);
        myQueue.push(18, 1);
        assertEquals(2362, myQueue.pop());
        assertEquals(246, myQueue.pop());
        assertEquals(245, myQueue.pop());
        assertEquals(2361, myQueue.pop());
        assertEquals(19, myQueue.pop());
        assertEquals(2360, myQueue.pop());
        assertEquals(16, myQueue.pop());
        assertEquals(17, myQueue.pop());
        assertEquals(18, myQueue.pop());
        assertNull(myQueue.pop());

    }

    @Test
    void top() {
        PriorityQueue<Integer> myQueue = new Heap<>();
        myQueue.push(16, 1);
        assertEquals(16, myQueue.top());
        myQueue.push(2361, 228);
        assertEquals(2361, myQueue.top());
        myQueue.push(246, 1);
        assertEquals(2361, myQueue.top());
    }
}