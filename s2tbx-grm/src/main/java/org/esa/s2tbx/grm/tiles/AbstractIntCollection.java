package org.esa.s2tbx.grm.tiles;

/**
 * @author Jean Coravu
 */
public abstract class AbstractIntCollection {
    protected static final int[] INT = new int[0];

    protected int[] keys;
    protected int size;

    protected AbstractIntCollection() {
    }

    protected static int computeGrowSize(int currentSize) {
        return currentSize <= 4 ? 8 : currentSize * 2;
    }

    protected static int binarySearch(int[] array, int size, int value) {
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

    protected static int[] insertKey(int[] array, int currentSize, int index, int element) {
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

}
