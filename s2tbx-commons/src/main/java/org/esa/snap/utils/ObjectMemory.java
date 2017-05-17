package org.esa.snap.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Jean Coravu.
 */
public class ObjectMemory {
    private static final int REFERENCE_SIZE;
    private static final int HEADER_SIZE;

    private static final int BYTE_SIZE = 1;
    private static final int BOOLEAN_SIZE = 1;
    private static final int CHAR_SIZE = 2;
    private static final int SHORT_SIZE = 2;
    private static final int INT_SIZE = 4;
    private static final int FLOAT_SIZE = 4;
    private static final int LONG_SIZE = 8;
    private static final int DOUBLE_SIZE = 8;

    private static final int ALIGNMENT = 8;

    static {
        if (System.getProperties().get("java.vm.name").toString().contains("64")) {
            REFERENCE_SIZE = 8;
            HEADER_SIZE = 16;
        } else {
            REFERENCE_SIZE = 4;
            HEADER_SIZE = 8;
        }
    }

    private ObjectMemory() {
    }

    public static long computeSizeOf(Object objectToComputeSizeOf) throws IllegalAccessException {
        return computeSizeOf(objectToComputeSizeOf, new HashSet<Object>());
    }

    private static long computeSizeOf(Object objectToComputeSizeOf, Set<Object> visited) throws IllegalAccessException {
        if (objectToComputeSizeOf == null) {
            return 0;
        }
        ItemWrapper objectWrapper = new ItemWrapper(objectToComputeSizeOf);
        if (visited.contains(objectWrapper)) {
            return 0; // the item already exists
        }
        visited.add(objectWrapper);
        long size = HEADER_SIZE;
        Class objectClass = objectToComputeSizeOf.getClass();
        if (objectClass.isArray()) {
            if (objectClass == long[].class) {
                long[] objs = (long[]) objectToComputeSizeOf;
                size += objs.length * LONG_SIZE;
            } else if (objectClass == int[].class) {
                int[] objs = (int[]) objectToComputeSizeOf;
                size += objs.length * INT_SIZE;
            } else if (objectClass == byte[].class) {
                byte[] objs = (byte[]) objectToComputeSizeOf;
                size += objs.length * BYTE_SIZE;
            } else if (objectClass == boolean[].class) {
                boolean[] objs = (boolean[]) objectToComputeSizeOf;
                size += objs.length * BOOLEAN_SIZE;
            } else if (objectClass == char[].class) {
                char[] objs = (char[]) objectToComputeSizeOf;
                size += objs.length * CHAR_SIZE;
            } else if (objectClass == short[].class) {
                short[] objs = (short[]) objectToComputeSizeOf;
                size += objs.length * SHORT_SIZE;
            } else if (objectClass == float[].class) {
                float[] objs = (float[]) objectToComputeSizeOf;
                size += objs.length * FLOAT_SIZE;
            } else if (objectClass == double[].class) {
                double[] objs = (double[]) objectToComputeSizeOf;
                size += objs.length * DOUBLE_SIZE;
            } else {
                Object[] objs = (Object[]) objectToComputeSizeOf;
                for (int i = 0; i < objs.length; i++) {
                    size += computeSizeOf(objs[i], visited) + REFERENCE_SIZE;
                }
            }
            size += INT_SIZE;
        } else {
            Field[] fields = objectToComputeSizeOf.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (Modifier.isStatic(fields[i].getModifiers())) {
                    continue;
                }
                fields[i].setAccessible(true);
                String fieldType = fields[i].getGenericType().toString();
                if (fieldType.equals("long")) {
                    size += LONG_SIZE;
                } else if (fieldType.equals("int")) {
                    size += INT_SIZE;
                } else if (fieldType.equals("byte")) {
                    size += BYTE_SIZE;
                } else if (fieldType.equals("boolean")) {
                    size += BOOLEAN_SIZE;
                } else if (fieldType.equals("char")) {
                    size += CHAR_SIZE;
                } else if (fieldType.equals("short")) {
                    size += SHORT_SIZE;
                } else if (fieldType.equals("float")) {
                    size += FLOAT_SIZE;
                } else if (fieldType.equals("double")) {
                    size += DOUBLE_SIZE;
                } else {
                    Object item = fields[i].get(objectToComputeSizeOf);
                    if (item instanceof Iterable<?>) {
                        Iterator<?> it = ((Iterable<?>)item).iterator();
                        while (it.hasNext()) {
                            size += computeSizeOf(it.next(), visited) + REFERENCE_SIZE;
                        }
                    } else {
                        size += computeSizeOf(item, visited) + REFERENCE_SIZE;
                    }
                }
            }
        }

        if ((size % ALIGNMENT) != 0) {
            size = ALIGNMENT * (size / ALIGNMENT) + ALIGNMENT;
        }
        return size;
    }

    private static final class ItemWrapper {
        private final Object item;

        ItemWrapper(Object item) {
            this.item = item;
        }

        @Override
        public boolean equals(Object inputItem) {
            if (inputItem == this) {
                return true;
            }
            if ((inputItem == null) || (!(inputItem instanceof ItemWrapper))) {
                return false;
            }
            ItemWrapper item = (ItemWrapper) inputItem;
            return this.item == item.item;
        }

        @Override
        public int hashCode() {
            return 47 + System.identityHashCode(this.item);
        }
    }
}
