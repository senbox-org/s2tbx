package org.esa.s2tbx.grm.tiles;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntToObjectMap<E> {
    private static final int[] INT = new int[0];
    private static final Object[] OBJECT = new Object[0];
    private static final Object DELETED = new Object();

    private boolean garbage;
    private int[] keys;
    private Object[] values;
    private int size;

    public IntToObjectMap() {
        this(10);
    }

    public IntToObjectMap(int initialCapacity) {
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
        return (E) this.values[i];
    }

    @SuppressWarnings("unchecked")
    public E remove(int key) {
        int i = binarySearch(this.keys, this.size, key);
        if (i >= 0) {
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
        int i = binarySearch(this.keys, this.size, key);
        if (i >= 0) {
            this.values[i] = value;
        } else {
            i = ~i;
            if (i < this.size && this.values[i] == DELETED) {
                this.keys[i] = key;
                this.values[i] = value;
            } else {
                if (this.garbage && this.size >= this.keys.length) {
                    gc();
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
            gc();
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

    private void gc() {
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

    private static int[] insertKey(int[] array, int currentSize, int index, int element) {
        assert (currentSize <= array.length);
        if (currentSize + 1 <= array.length) {
            System.arraycopy(array, index, array, index + 1, currentSize - index);
            array[index] = element;
            return array;
        }
        int[] newArray = new int[computeGrowSize(currentSize)];
        System.arraycopy(array, 0, newArray, 0, index);
        newArray[index] = element;
        System.arraycopy(array, index, newArray, index + 1, array.length - index);
        return newArray;
    }

    private static int computeGrowSize(int currentSize) {
        return currentSize <= 4 ? 8 : currentSize * 2;
    }

    private static int binarySearch(int[] array, int size, int value) {
        int lo = 0;
        int hi = size - 1;

        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int midVal = array[mid];

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

    private class MapValuesIterator implements Iterator<E> {
        private int cursor; // index of next element to return

        MapValuesIterator() {
            this.cursor = 0;
        }

        @Override
        public boolean hasNext() {
            return (computeIndexForNextValue() >= 0);
        }

        @Override
        public E next() {
            int index = computeIndexForNextValue();
            if (index < 0) {
                throw new NoSuchElementException();
            }
            this.cursor = index + 1;
            return (E) IntToObjectMap.this.values[index];
        }

        private int computeIndexForNextValue() {
            int index = this.cursor;
            Object[] elementData = IntToObjectMap.this.values;
            E value = null;
            do {
                value = (E) elementData[index];
                index++;
            } while ((value == null || value == IntToObjectMap.DELETED) && index < elementData.length);
            if (value == null || value == IntToObjectMap.DELETED) {
                return -1; // no such value
            }
            return (index - 1); // return the index of the next value
        }
    }
}
