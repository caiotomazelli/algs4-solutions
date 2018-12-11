import java.util.Iterator;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] list;
    private int capacity;
    private int size;

    public RandomizedQueue() {
        capacity = 10;
        list = (Item[]) new Object[capacity];
        size = 0;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public int size() {
        return size;
    }
    private void increaseCapacity() {
        if (2*size > capacity) {
            capacity = 2*capacity;
            Item[] newList = (Item []) new Object[capacity];
            for (int i = 0; i < size; i++) {
                newList[i] = list[i];
            }
            list = newList;
        }
    }
    private void decreaseCapacity() {
        if (4*size < capacity) {
            capacity = capacity/2;
            Item[] newList = (Item []) new Object[capacity];
            for (int i = 0; i < size; i++) {
                newList[i] = list[i];
            }
            list = newList;
        }
    }
    public void enqueue(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException();
        }
        increaseCapacity();
        list[size++] = item;
    }
    public Item dequeue() {
        if (size == 0) {
            throw new java.util.NoSuchElementException();
        }
        decreaseCapacity();
        Item item = list[--size];
        list[size] = null;
        return item;
    }
    public Item sample() {
        if (size == 0) {
            throw new java.util.NoSuchElementException();
        }
        int idx = StdRandom.uniform(size);
        return list[idx];
    }
    @Override
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            int[] permutation = StdRandom.permutation(size);
            int i = 0;
            @Override
            public boolean hasNext() {
                return i < size;
            }
            @Override
            public Item next() {
                if (hasNext()) {
                    return list[permutation[i++]];
                } else throw new java.util.NoSuchElementException();
            }
            @Override
            public void remove() {
                throw new java.lang.UnsupportedOperationException();
            }
        };
    }
}
