package org.esa.s2tbx.grm;

import com.sun.javafx.scene.shape.PathUtils;

/**
 * @author Jean Coravu
 */
public class Contour {
    public static final int TOP_MOVE_INDEX = 0;
    public static final int RIGHT_MOVE_INDEX = 1;
    public static final int BOTTOM_MOVE_INDEX = 2;
    public static final int LEFT_MOVE_INDEX = 3;

    private byte[] bits;
    private short size;

    public Contour() {
        this.size = 0;
        this.bits = new byte[] {0};
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i=0; i<size; i+=2) {
            if (getBitAt(i) == 1) {
                str.append("1");
            } else {
                str.append("0");
            }
            if (getBitAt(i+1) == 1) {
                str.append("1");
            } else {
                str.append("0");
            }
            if (i < size-2) {
                str.append(" "); // add an empty space
            }
        }
        return str.toString();
    }

    public void pushTop() { // Push0  contour.push_back(0); contour.push_back(0);
        pushBit((byte)0);
        pushBit((byte)0);
    }

    public void pushRight() { // Push1  contour.push_back(1); contour.push_back(0);
        pushBit((byte)0);
        pushBit((byte)1);
    }

    public void pushBottom() { // Push2  contour.push_back(0); contour.push_back(1);
        pushBit((byte)1);
        pushBit((byte)0);
    }

    public void pushLeft() { // Push3  contour.push_back(1); contour.push_back(1);
        pushBit((byte)1);
        pushBit((byte)1);
    }

    public byte getBitAt(int index) {
        int arrayIndex = index / 8;
        int positionsToMove = 7 - (index % 8);
        int value = (this.bits[arrayIndex] >> positionsToMove) & 0x01;
        return (byte)value;
    }

    public int getMove(int index) {
        int bitIndex = 2 * index;
        return (2 * getBitAt(bitIndex)) + getBitAt(bitIndex + 1);
    }

    public short size() {
        return this.size;
    }

    private void pushBit(byte bitValue) {
        int arrayLength = (this.size / 8) + 1;
        if (this.bits.length < arrayLength) {
            byte[] newBits = new byte[arrayLength];
            for (int i=0; i<this.bits.length; i++) {
                newBits[i] = this.bits[i];
            }
            for (int i=this.bits.length; i<arrayLength; i++) {
                newBits[i] = 0; // fill with zero
            }
            this.bits = newBits;
        }
        int arrayIndex = this.size / 8;
        this.size++;
        int positionsToMove = 8 - (this.size % 8);
        if (positionsToMove == 8) {
            positionsToMove = 0; // no position to move
        }
        int x = this.bits[arrayIndex] >> positionsToMove;
        x = x | bitValue;
        x = x << positionsToMove;
        this.bits[arrayIndex] = (byte)x;
    }
}
