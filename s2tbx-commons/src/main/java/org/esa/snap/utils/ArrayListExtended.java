package org.esa.snap.utils;

import java.util.ArrayList;

/**
 * @author Jean Coravu
 */
public class ArrayListExtended<ItemType> extends ArrayList<ItemType> {

    public ArrayListExtended(int numberOfNodes) {
        super(numberOfNodes);
    }

    public void removeItems(int fromIndexInclusive, int toIndexExclusive) {
        removeRange(fromIndexInclusive, toIndexExclusive);
    }

    public void clearItems() {
        removeRange(0, size());
    }
}
