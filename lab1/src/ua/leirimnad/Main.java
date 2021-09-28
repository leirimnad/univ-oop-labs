package ua.leirimnad;

import java.util.List;
import java.util.Vector;

public class Main {
    public static void main(String[] args) {
        intDemonstration();
        //doubleDemonstration();
        //stringDemonstration();
//        vectorDemonstration();
        //cubeSetDemonstration();
    }
    
    public static void intDemonstration(){
        PriorityQueue<Integer> queue = new LinkedList<>();
        queue.push(123, 1);
        queue.push(222, 2);
        queue.push(669, 3);
        queue.push(670, 2);
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
    }

    public static void doubleDemonstration(){
        PriorityQueue<Double> queue = new ArrayList<>();
        queue.push(123.4444d, 1);
        queue.push(222.22d, 2);
        queue.push(669.1d, 3);
        queue.push(670.333d, 2);
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
    }

    public static void stringDemonstration(){
        PriorityQueue<String> queue = new BinaryTree<>();
        queue.push("Not important", -1);
        queue.push("Somewhat important #1", 50);
        queue.push("Most important", 100);
        queue.push("Somewhat important #2", 50);
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
    }

    public static void vectorDemonstration(){
        PriorityQueue<Vector<String>> queue = new Heap<>();
        queue.push(new Vector<>(List.of("Not an important task #1", "Not an important task #2")), -1);
        queue.push(new Vector<>(List.of("Somewhat important task #1", "Somewhat important task #2")), 50);
        queue.push(new Vector<>(List.of("Very important task", "Incredibly important task")), 100);
        queue.push(new Vector<>(List.of("Somewhat important task #3", "Not an important task #4")), 50);
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
    }

    public static void cubeSetDemonstration(){
        PriorityQueue<CubeSet> queue = new LinkedList<>();

        CubeSet set1 = new CubeSet(new Cube[]{
                new Cube(
                        new Pair<>(1, 1f),
                        new Pair<>(2, 1f),
                        new Pair<>(3, 1f),
                        new Pair<>(4, 1f)
                ),
                new Cube(
                        new Pair<>(10, 0.7f),
                        new Pair<>(20, 0.3f)
                ),
                new Cube(
                        new Pair<>(-1, 0.5f),
                        new Pair<>(1, 0.5f)
                )
        });

        CubeSet set2 = new CubeSet(new Cube[]{
                new Cube(
                        new Pair<>(1, 0.2f),
                        new Pair<>(2, 0.2f),
                        new Pair<>(3, 0.2f),
                        new Pair<>(4, 0.2f),
                        new Pair<>(5, 0.2f),
                        new Pair<>(6, 0.2f)
                ),
                new Cube(
                        new Pair<>(1, 1f),
                        new Pair<>(2, 1f),
                        new Pair<>(3, 1f),
                        new Pair<>(4, 1f),
                        new Pair<>(5, 1f),
                        new Pair<>(6, 1f)
                )
        });

        CubeSet set3 = new CubeSet(new Cube[]{
                new Cube(
                        new Pair<>(-1, 0.5f),
                        new Pair<>(1, 0.5f)
                ),
                new Cube(
                        new Pair<>(-1, 2f),
                        new Pair<>(1, 2f)
                )
        });

        CubeSet set4 = new CubeSet(new Cube[]{
                new Cube(),
                new Cube()
        });

        queue.push(set1, 0);
        queue.push(set4, -1);
        queue.push(set2, 1);
        queue.push(set3, 0);

        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
    }
}
