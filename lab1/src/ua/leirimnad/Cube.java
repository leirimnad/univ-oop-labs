package ua.leirimnad;

import java.util.LinkedList;
import java.util.List;

public class Cube {
    private List<Pair<Integer, Float>> edges = new LinkedList<>();

    /**
     * A cube with a wanted amount of edges.
     * If possibilities do not add up to 1, they will be recalculated proportionately.
     * @param edges Pair of the edge value and edge possibility
     */
    public Cube(Pair<Integer, Float>... edges){
        float sum = 0;
        for (int i=0; i < edges.length; i++){
            sum += edges[i].right();
        }
        for (int i=0; i < edges.length; i++){
            edges[i].setRight(edges[i].right() / sum);
            this.edges.add(edges[i]);
        }
    }

    /**
     * Returns a list of all the edges of the cube.
     * @return list of all the edges
     */
    public List<Pair<Integer, Float>> getEdges(){
        return this.edges;
    }
}
