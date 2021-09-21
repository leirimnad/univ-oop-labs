package ua.leirimnad;

/**
 * Triplet, used to store three elements in one object.
 * @param <L> Left element
 * @param <M> Middle element
 * @param <R> Right element
 *
 */
public class Triplet<L, M, R> {
    private L left;
    private M middle;
    private R right;

    public Triplet(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
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
     * Get the middle element
     * @return Middle element
     */
    public M middle() {
        return middle;
    }

    /**
     * Get the right element
     * @return Right element
     */
    public R right() {
        return right;
    }
}
