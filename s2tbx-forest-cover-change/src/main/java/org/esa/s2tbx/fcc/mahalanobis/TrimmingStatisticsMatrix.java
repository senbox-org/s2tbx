package org.esa.s2tbx.fcc.mahalanobis;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.fcc.intern.PixelSourceBands;

/**
 * Created by jcoravu on 20/6/2017.
 */
public class TrimmingStatisticsMatrix extends Matrix {
    private final Int2ObjectMap.Entry<?>[] regions;
    private final float meanValueB4Band;
    private final float meanValueB8Band;
    private final float meanValueB11Band;
    private final float standardDeviationMeanValueB11Band;

    public TrimmingStatisticsMatrix(Int2ObjectMap<PixelSourceBands> statistics) {
        super();

        float totalValueB4Band = 0.0f;
        float totalValueB8Band = 0.0f;
        float totalValueB11Band = 0.0f;
        float totalStandardDeviationValueB11Band = 0.0f;

        int numberOfPoints = statistics.size();
        this.regions = new Int2ObjectMap.Entry<?>[numberOfPoints];

        int index = -1;

        ObjectIterator<Int2ObjectMap.Entry<PixelSourceBands>> it = statistics.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<PixelSourceBands> entry = it.next();
            PixelSourceBands point = entry.getValue();
            this.regions[++index] = entry;
            totalValueB4Band += point.getMeanValueB4Band();
            totalValueB8Band += point.getMeanValueB8Band();
            totalValueB11Band += point.getMeanValueB11Band();
            totalStandardDeviationValueB11Band += point.getStandardDeviationValueB8Band();
        }

        this.meanValueB4Band = totalValueB4Band / (float)numberOfPoints;
        this.meanValueB8Band = totalValueB8Band / (float)numberOfPoints;
        this.meanValueB11Band = totalValueB11Band / (float)numberOfPoints;
        this.standardDeviationMeanValueB11Band = totalStandardDeviationValueB11Band / (float)numberOfPoints;
    }

    @Override
    public float getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return getRegionAt(rowIndex).getMeanValueB4Band() - this.meanValueB4Band;
        }
        if (columnIndex == 1) {
            return getRegionAt(rowIndex).getMeanValueB8Band() - this.meanValueB8Band;
        }
        if (columnIndex == 2) {
            return getRegionAt(rowIndex).getMeanValueB11Band() - this.meanValueB11Band;
        }
        if (columnIndex == 3) {
            return getRegionAt(rowIndex).getStandardDeviationValueB8Band() - this.standardDeviationMeanValueB11Band;
        }
        throw new IllegalArgumentException("Unknown column index " + columnIndex + ".");
    }

    @Override
    public int getRowCount() {
        return this.regions.length;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    public PixelSourceBands getRegionAt(int index) {
        return (PixelSourceBands)this.regions[index].getValue();
    }

    public int getRegionKeyAt(int index) {
        return this.regions[index].getIntKey();
    }
}
