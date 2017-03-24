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

    public void add(int key) {
        int i = binarySearch(this.keys, this.size, key);
        if (i >= 0) {
            // the key already exists
        } else {
            // the key does not exist
            i = ~i;
            this.keys = insertKey(this.keys, this.size, i, key);
            this.size++;
        }
    }

    public int size() {
        return this.size;
    }
}
