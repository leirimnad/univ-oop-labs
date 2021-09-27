package ua.leirimnad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CubeSetTest {

    @Test
    void expectedResult() {
        Cube[] myCubes = new Cube[1];
        myCubes[0] = new Cube(new Pair<>(1, 1f), new Pair<>(2, 1f), new Pair<>(3, 1f), new Pair<>(4, 1f), new Pair<>(5, 1f), new Pair<>(6, 1f));
        CubeSet set = new CubeSet(myCubes);
        assertEquals(set.expectedResult(), 3.5);

        myCubes[0] = new Cube(new Pair<>(2, 1f), new Pair<>(2, 1f));
        set = new CubeSet(myCubes);
        assertEquals(set.expectedResult(), 2);
    }

    @Test
    void compareTo() {
        Cube[] myCubes = new Cube[1];
        Cube[] myCubes1 = new Cube[1];
        myCubes[0] = new Cube(new Pair<>(1, 1f), new Pair<>(2, 1f), new Pair<>(3, 1f), new Pair<>(4, 1f), new Pair<>(5, 1f), new Pair<>(6, 1f));
        myCubes1[0] = new Cube(new Pair<>(1, 1f), new Pair<>(2, 1f));


        CubeSet set = new CubeSet(myCubes);
        CubeSet set1 = new CubeSet(myCubes1);

        assertEquals(set.compareTo(set1), 1);
        assertEquals(set1.compareTo(set), -1);
        assertEquals(set1.compareTo(set1), 0);
    }
}