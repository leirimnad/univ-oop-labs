package ua.leirimnad;

import java.util.ArrayList;
import java.util.List;

/**
 * A set that stores cubes and gives information about all the possible results of throwing them.
 */
public class CubeSet implements Comparable<CubeSet> {
    public Cube[] cubes;

    /**
     * @param cubes An array of cubes to be stored in a set
     */
    public CubeSet(Cube[] cubes){
        this.cubes = cubes;
    }

    /**
     * Gets a list of all the possible sums and probabilities produced by this set.
     * @return List of pairs (Sum, Probability)
     */
    public List<Pair<Integer, Float>> possibleSums(){
        List<Pair<Integer, Float>> res = new ArrayList<>();
        List<List<Pair<Integer, Float>>> combinations = this.combination(0);
        for(List<Pair<Integer, Float>> combination : combinations){
            int sum = 0; float prob = 1f;
            for (Pair<Integer, Float> edge : combination){
                sum += edge.left();
                prob *= edge.right();
            }
            boolean found = false;
            for (int i=0; i < res.size(); i++){
                Pair<Integer, Float> s = res.get(i);
                if(s.left() == sum){
                    s.setRight(s.right() + prob);
                    res.set(i, s);
                    found = true;
                }
            }
            if(!found) res.add(new Pair<>(sum, prob));
        }
        return res;
    }

    /**
     * Gets an average expected result of throwing all the cubes in the set.
     * @return expected result
     */
    public float expectedResult(){
        float res = 0;
        for (Pair<Integer, Float> sum : this.possibleSums()){
            res += sum.left() * sum.right();
        }
        return res;
    }

    private List<List<Pair<Integer, Float>>> combination(int fromIndex) {
        List<List<Pair<Integer, Float>>> res = new java.util.ArrayList<>();

        if (fromIndex == this.cubes.length - 1) {
            for (Pair<Integer, Float> edge : this.cubes[fromIndex].getEdges()){
                res.add(List.of(edge));
            }
            return res;
        }

        List<List<Pair<Integer, Float>>> rest = this.combination(fromIndex+1);

        for (Pair<Integer, Float> edge : this.cubes[fromIndex].getEdges()) {
            for(List<Pair<Integer, Float>> possibleRest : rest){
                List<Pair<Integer, Float>> newList = new ArrayList<>(possibleRest);
                newList.add(edge);
                res.add(newList);
            }
        }

        return res;
    }

    /**
     * Compares the set to another one by the expected result
     * @param o Set to be compared with
     * @return 1 if the expected result is greater, 0 if equals, -1 if less
     */
    @Override
    public int compareTo(CubeSet o) {
        return Float.compare(this.expectedResult(), o.expectedResult());
    }
}
