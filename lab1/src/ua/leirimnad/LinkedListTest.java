package ua.leirimnad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {

    @Test
    void pop() {
        LinkedList<Integer> ls = new LinkedList<>();
        ls.push(42, 1);
        ls.push(-123, 3);
        ls.push(23, 3);
        ls.push(423, 2);

        assertEquals(-123, ls.pop());
        assertEquals(23, ls.pop());
        assertEquals(423, ls.pop());
        assertEquals(42, ls.pop());
        assertEquals(null, ls.pop());
    }

    @Test
    void top() {
        LinkedList<Integer> ls = new LinkedList<>();
        ls.push(42, 1);
        ls.push(-123, 3);
        ls.push(23, 3);
        ls.push(423, 2);

        assertEquals(-123, ls.top());
    }
}