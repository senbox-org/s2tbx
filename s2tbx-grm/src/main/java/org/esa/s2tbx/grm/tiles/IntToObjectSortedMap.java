package org.esa.s2tbx.grm.tiles;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Jean Coravu
 */
public class IntToObjectSortedMap<E> extends AbstractIntCollection {
    private static final Object[] OBJECT = new Object[0];
    private static final Object DELETED = new Object();

    private boolean garbage;
    private Object[] values;

    public IntToObjectSortedMap() {
        this(10);
    }

    public IntToObjectSortedMap(int initialCapacity) {
        super();

        if (initialCapacity == 0) {
            this.keys = INT;
            this.values = OBJECT;
        } else {
            this.keys = new int[initialCapacity];
            this.values = new Object[initialCapacity];
        }
        this.size = 0;
        this.garbage = false;
    }

    @SuppressWarnings("unchecked")
    public E get(int key) {
        int i = binarySearch(this.keys, this.size, key);
        if (i < 0 || this.values[i] == DELETED) {
            return null; // the key does not exist
        }
        // the key exists
        return (E) this.values[i];
    }

    @SuppressWarnings("unchecked")
    public E remove(int key) {
        int i = binarySearch(this.keys, this.size, key);
        if (i >= 0) {
            // the key exists
            if (this.values[i] != DELETED) {
                E old = (E) this.values[i];
                this.values[i] = DELETED;
                this.garbage = true;
                return old;
            }
        }
        return null;
    }

    public boolean containsKey(int key) {
        E value = get(key);
        return (value != null && value != DELETED);
    }

    public void put(int key, E value) {
        if (value == null) {
            throw new NullPointerException("value = null");
        }
        int i = binarySearch(this.keys, this.size, key);
        if (i >= 0) {
            // the key already exists
            this.values[i] = value;
        } else {
            // the key does not exist
            i = ~i;
            if (i < this.size && this.values[i] == DELETED) {
                this.keys[i] = key;
                this.values[i] = value;
            } else {
                if (this.garbage && this.size >= this.keys.length) {
                    garbageCollector();
                    // search again because indices may have changed
                    i = ~binarySearch(this.keys, this.size, key);
                }
                this.keys = insertKey(this.keys, this.size, i, key);
                this.values = insertValue(this.values, this.size, i, value);
                this.size++;
            }
        }
    }

    public int size() {
        if (this.garbage) {
            garbageCollector();
        }
        return this.size;
    }

    public void clear() {
        int n = this.size;
        Object[] values = this.values;
        for (int i = 0; i < n; i++) {
            values[i] = null;
        }
        this.size = 0;
        this.garbage = false;
    }

    public Iterator<E> valuesIterator() {
        return new MapValuesIterator();
    }

    public Iterator<Entry<E>> entriesIterator() {
        return new MapEntriesIterator();
    }

    private void garbageCollector() {
        int number = this.size;
        int count = 0;
        int[] keys = this.keys;
        Object[] values = this.values;
        for (int i = 0; i < number; i++) {
            Object val = values[i];
            if (val != DELETED) {
                if (i != count) {
                    keys[count] = keys[i];
                    values[count] = val;
                    values[i] = null;
                }
                count++;
            }
        }
        this.garbage = false;
        this.size = count;
    }

    private static Object[] insertValue(Object[] array, int currentSize, int index, Object element) {
        assert (currentSize <= array.length);
        if (currentSize + 1 <= array.length) {
            System.arraycopy(array, index, array, index + 1, currentSize - index);
            array[index] = element;
            return array;
        }
        Object[] newArray = new Object[computeGrowSize(currentSize)];
        System.arraycopy(array, 0, newArray, 0, index);
        newArray[index] = element;
        System.arraycopy(array, index, newArray, index + 1, array.length - index);
        return newArray;
    }

    public interface Entry<E> {

        int getKey();

        E getValue();
    }

    private class MapEntriesIterator extends AbstractMapIterator<Entry<E>> {
        private final Entry<E> entry;

        MapEntriesIterator() {
            this.entry = new Entry<E>() {
                @Override
                public int getKey() {
                    return IntToObjectSortedMap.this.keys[MapEntriesIterator.this.cursor-1];
                }

                @Override
                public E getValue() {
                    return (E) IntToObjectSortedMap.this.values[MapEntriesIterator.this.cursor-1];
                }
            };
        }

        @Override
        public Entry<E> next() {
            moveCursor();
            return this.entry;
        }
    }

    private class MapValuesIterator extends AbstractMapIterator<E> {

        MapValuesIterator() {
        }

        @Override
        public E next() {
            moveCursor();
            return (E) IntToObjectSortedMap.this.values[this.cursor-1];
        }
    }

    private abstract class AbstractMapIterator<E> implements Iterator<E> {
        protected int cursor; // index of next element to return

        AbstractMapIterator() {
            this.cursor = 0;
        }

        @Override
        public final boolean hasNext() {
            return (IntToObjectSortedMap.this.size > 0 && computeIndexForNextValue() >= 0);
        }

        final void moveCursor() {
            int index = computeIndexForNextValue();
            if (index < 0) {
                throw new NoSuchElementException();
            }
            this.cursor = index + 1;
        }

        private int computeIndexForNextValue() {
            int index = this.cursor;
            Object[] elementData = IntToObjectSortedMap.this.values;
            Object value = null;
            do {
                value = elementData[index];
                index++;
            } while ((value == null || value == IntToObjectSortedMap.DELETED) && index < elementData.length);
            if (value == null || value == IntToObjectSortedMap.DELETED) {
                return -1; // no such value
            }
            return (index - 1); // return the index of the next value
        }
    }
}
