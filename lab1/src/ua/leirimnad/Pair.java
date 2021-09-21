package ua.leirimnad;

/**
 * Pair, used to store a pair of elements.
 * @param <L> Left element
 * @param <R> Right element
 */
public class Pair<L, R> {
    private L left;
    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Get the left element
     * @return Left element
     */
    public L left() {
        return left;
    }

    /**
     * Get the right element
     * @return Right element
     */
    public R right() {
        return right;
    }
}
