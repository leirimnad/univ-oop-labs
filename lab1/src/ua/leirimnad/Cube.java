package ua.leirimnad;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a cube with edges and possibilities.
 */
public class Cube {
    private List<Pair<Integer, Float>> edges = new LinkedList<>();

    /**
     * A cube with a wanted amount of edges.
     * If possibilities do not add up to 1, they will be recalculated proportionately.
     * @param edges Pair of the edge value and edge possibility
     */
    public Cube(Pair<Integer, Float>... edges){
        float sum = 0;
        for (Pair<Integer, Float> integerFloatPair : edges) {
            sum += integerFloatPair.right();
        }
        for (Pair<Integer, Float> edge : edges) {
            edge.setRight(edge.right() / sum);
            this.edges.add(edge);
        }
    }

    /**
     * A cube with a random amount of edges and random possibilities.
     */
    public Cube(){
        int min = 1, max = 6; float sum = 0;
        int edgesN = min + (int)(Math.random() * ((max - min) + 1));
        Pair<Integer, Float>[] edges = new Pair[edgesN];
        for (int i=0; i < edgesN; i++){
            edges[i] = new Pair<>(1 + (int)(Math.random() * ((10 - 1) + 1)), (float) Math.random());
            sum += edges[i].right();
        }
        for (Pair<Integer, Float> edge : edges) {
            edge.setRight(edge.right() / sum);
            this.edges.add(edge);
        }
    }

    /**
     * Returns a list of all the edges of the cube.
     * @return list of all the edges
     */
    public List<Pair<Integer, Float>> getEdges(){
        return this.edges;
    }

    /**
     * String representation of a cube
     * @return string representation
     */
    @Override
    public String toString() {
        return "Cube{" +
                "edges=" + edges +
                '}';
    }
}
