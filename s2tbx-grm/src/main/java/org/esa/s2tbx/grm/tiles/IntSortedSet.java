package org.esa.s2tbx.grm.tiles;

/**
 * @author Jean Coravu
 */
public class IntSortedSet extends AbstractIntCollection {

    public IntSortedSet() {
        this(10);
    }

    public IntSortedSet(int initialCapacity) {
        super();

        if (initialCapacity == 0) {
            this.keys = INT;
        } else {
            this.keys = new int[initialCapacity];
        }
        this.size = 0;
    }

    public boolean add(int key) {
        int index = binarySearch(this.keys, this.size, key);
        if (index >= 0) {
            return false; // the key already exists
        }
        // the key does not exist
        index = ~index;
        this.keys = insertKey(this.keys, this.size, index, key);
        this.size++;
        return true;
    }

    public boolean contains(int key) {
        int index = binarySearch(this.keys, this.size, key);
        return (index >= 0);
    }

    public int size() {
        return this.size;
    }

    public int get(int index) {
        return this.keys[index];
    }
}
