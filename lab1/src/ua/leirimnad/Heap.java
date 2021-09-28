package ua.leirimnad;

import java.util.Objects;

/**
 * A heap implementation of priority queue.
 * Uses heap data structure.
 * @param <T> Data type
 */
public class Heap <T> implements PriorityQueue <T> {
    private int maxSize = 64;
    private Triplet<T, Integer, Integer>[] array = new Triplet[maxSize];
    private int size = 0;
    private int counter = 0;

    @Override
    public void push(T elem, int priority) {
        if(size + 1 > maxSize) this.reallocateMemory();
        array[size++] = new Triplet<>(elem, priority, counter++);
        up(size - 1);
    }

    @Override
    public T pop() {
        if (this.size < 1) return null;

        int minCounterIndex = 0;
        for (int i=0; i < size; i++){
            if(Objects.equals(array[i].middle(), array[0].middle()) && array[i].right() < array[minCounterIndex].right()){
                minCounterIndex = i;
            }
        }
        T ret = array[minCounterIndex].left();
        array[minCounterIndex] = array[--this.size];
        down(minCounterIndex);
        return ret;
    }

    @Override
    public T top() {
        if (this.size < 1) return null;
        return array[0].left();
    }

    private void up(int i) {
        while (i != 0 && array[i].middle() > array[parent(i)].middle()) {
            Triplet<T, Integer, Integer> temp = array[i];
            array[i] = array[parent(i)];
            array[parent(i)] = temp;
            i = parent(i);
        }
    }

    private void down(int i) {
        while (i < this.size / 2) {
            int maxI = leftChild(i);
            if (rightChild(i) < size && array[rightChild(i)].middle() > array[leftChild(i)].middle())
                maxI = rightChild(i);
            if (array[i].middle() >= array[maxI].middle())
                return;
            Triplet<T, Integer, Integer> temp = array[i];
            array[i] = array[maxI];
            array[maxI] = temp;
            i = maxI;
        }
    }

    /**
     * Enlarges the array, taking x1.5 more memory for itself.
     */
    protected void reallocateMemory(){
        this.maxSize *= 1.5;
        Triplet<T, Integer, Integer>[] newArray = new Triplet[this.maxSize];
        System.arraycopy(array, 0, newArray, 0, this.size);
        this.array = newArray;
    }

    private int parent(int i) { return (i - 1) / 2; }
    private int leftChild(int i) { return 2 * i + 1; }
    private int rightChild(int i) { return 2 * i + 2; }
}
