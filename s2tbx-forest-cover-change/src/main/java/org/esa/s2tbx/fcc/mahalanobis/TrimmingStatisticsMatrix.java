package org.esa.s2tbx.fcc.mahalanobis;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.fcc.trimming.PixelSourceBands;

/**
 * Created by jcoravu on 20/6/2017.
 */
public class TrimmingStatisticsMatrix extends Matrix {
    private final Int2ObjectMap.Entry<?>[] validRegions;
    private final float meanValueB4Band;
    private final float meanValueB8Band;
    private final float meanValueB11Band;
    private final float standardDeviationMeanValueB11Band;

    public TrimmingStatisticsMatrix(Int2ObjectMap<PixelSourceBands> validRegionStatistics) {
        super();

        float totalValueB4Band = 0.0f;
        float totalValueB8Band = 0.0f;
        float totalValueB11Band = 0.0f;
        float totalStandardDeviationValueB11Band = 0.0f;

        int validRegionCount = validRegionStatistics.size();
        this.validRegions = new Int2ObjectMap.Entry<?>[validRegionCount];

        int index = -1;

        ObjectIterator<Int2ObjectMap.Entry<PixelSourceBands>> it = validRegionStatistics.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<PixelSourceBands> entry = it.next();
            PixelSourceBands point = entry.getValue();
            this.validRegions[++index] = entry;
            totalValueB4Band += point.getMeanValueB4Band();
            totalValueB8Band += point.getMeanValueB8Band();
            totalValueB11Band += point.getMeanValueB11Band();
            totalStandardDeviationValueB11Band += point.getStandardDeviationValueB8Band();
        }

        this.meanValueB4Band = totalValueB4Band / (float)validRegionCount;
        this.meanValueB8Band = totalValueB8Band / (float)validRegionCount;
        this.meanValueB11Band = totalValueB11Band / (float)validRegionCount;
        this.standardDeviationMeanValueB11Band = totalStandardDeviationValueB11Band / (float)validRegionCount;
    }

    @Override
    public float getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return getRegionMeanPixelsAt(rowIndex).getMeanValueB4Band() - this.meanValueB4Band;
        }
        if (columnIndex == 1) {
            return getRegionMeanPixelsAt(rowIndex).getMeanValueB8Band() - this.meanValueB8Band;
        }
        if (columnIndex == 2) {
            return getRegionMeanPixelsAt(rowIndex).getMeanValueB11Band() - this.meanValueB11Band;
        }
        if (columnIndex == 3) {
            return getRegionMeanPixelsAt(rowIndex).getStandardDeviationValueB8Band() - this.standardDeviationMeanValueB11Band;
        }
        throw new IllegalArgumentException("Unknown column index " + columnIndex + ".");
    }

    @Override
    public int getRowCount() {
        return this.validRegions.length;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    public PixelSourceBands getRegionMeanPixelsAt(int index) {
        return (PixelSourceBands)this.validRegions[index].getValue();
    }

    public int getRegionKeyAt(int index) {
        return this.validRegions[index].getIntKey();
    }
}
