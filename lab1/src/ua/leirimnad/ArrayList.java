package ua.leirimnad;

/**
 * An array implementation of priority queue.
 * Uses dynamic arrays.
 * @param <T> Data type
 */
public class ArrayList <T> implements PriorityQueue <T> {

    private int maxSize = 64;
    private Pair<T, Integer>[] array = new Pair[maxSize];

    private int size = 0;

    @Override
    public void push(T elem, int priority) {
        if(size + 1 > maxSize) this.reallocateMemory();
        int i = 0;
        for (; i < size; i++){
            if (array[i].right() < priority) break;
        }
        this.insertByIndex(new Pair<>(elem, priority), i);
    }

    @Override
    public T pop() {
        if (this.size < 1) return null;
        T ret = array[0].left();
        for (int i=1; i < this.size; i++){
            array[i-1] = array[i];
        }
        this.size--;
        return ret;
    }

    @Override
    public T top() {
        if (this.size < 1) return null;
        return array[0].left();
    }

    /**
     * Inserts element onto the nth position in the list's array.
     * @param pair Pair [Element, priority]
     * @param index Index to be inserted into
     */
    protected void insertByIndex(Pair<T, Integer> pair, int index){
        if (this.size + 1 > this.maxSize) this.reallocateMemory();
        if (index != this.size) {
            for (int t = this.size-1; t >= index; t--){
                array[t+1] = array[t];
            }
        }
        array[index] = pair;
        this.size++;
    }

    /**
     * Enlarges the array, taking x1.5 more memory for itself.
     */
    protected void reallocateMemory(){
        this.maxSize *= 1.5;
        Pair<T, Integer>[] newArray = new Pair[this.maxSize];
        System.arraycopy(array, 0, newArray, 0, this.size);
        this.array = newArray;
    }
}
