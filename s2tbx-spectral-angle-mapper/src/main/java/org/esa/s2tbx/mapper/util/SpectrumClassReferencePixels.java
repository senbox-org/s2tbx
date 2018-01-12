package org.esa.s2tbx.mapper.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 *
 * @author Razvan Dumitrascu
 */
public class SpectrumClassReferencePixels {
    private String className;
    private IntArrayList xPixelPositions;
    private IntArrayList yPixelPositions;
    public SpectrumClassReferencePixels(String className) {
        this.className = className;
        xPixelPositions = new IntArrayList();
        yPixelPositions = new IntArrayList();
    }
   public void addElements(int x, int y) {
       this.xPixelPositions.add(x);
       this.yPixelPositions.add(y);
   }


   public IntArrayList getXPixelPositions() {
       return this.xPixelPositions;
   }
   public IntArrayList getYPixelPositions() {
       return this.yPixelPositions;
   }

    public String getClassName() {
        return className;
    }

}
