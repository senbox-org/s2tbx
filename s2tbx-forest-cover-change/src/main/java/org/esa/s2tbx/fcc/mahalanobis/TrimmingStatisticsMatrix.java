package org.esa.s2tbx.fcc.mahalanobis;

import org.esa.s2tbx.fcc.intern.PixelSourceBands;

import java.util.Collection;

/**
 * Created by jcoravu on 20/6/2017.
 */
public class TrimmingStatisticsMatrix extends Matrix {
    private final PixelSourceBands[] points;
    private final float meanValueB4Band;
    private final float meanValueB8Band;
    private final float meanValueB11Band;
    private final float standardDeviationValueB11Band;

    public TrimmingStatisticsMatrix(Collection<PixelSourceBands> points, float meanValueB4Band, float meanValueB8Band, float meanValueB11Band, float standardDeviationValueB11Band) {
        super();

        this.meanValueB4Band = meanValueB4Band;
        this.meanValueB8Band = meanValueB8Band;
        this.meanValueB11Band = meanValueB11Band;
        this.standardDeviationValueB11Band = standardDeviationValueB11Band;

        this.points = new PixelSourceBands[points.size()];
        int index = -1;
        for (PixelSourceBands point : points) {
            index++;
            this.points[index] = point;
        }
    }

    @Override
    public float getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return this.points[rowIndex].getMeanValueB4Band() - this.meanValueB4Band;
        }
        if (columnIndex == 1) {
            return this.points[rowIndex].getMeanValueB8Band() - this.meanValueB8Band;
        }
        if (columnIndex == 2) {
            return this.points[rowIndex].getMeanValueB11Band() - this.meanValueB11Band;
        }
        if (columnIndex == 3) {
            return this.points[rowIndex].getStandardDeviationValueB8Band() - this.standardDeviationValueB11Band;
        }
        throw new IllegalArgumentException("Unknown column index " + columnIndex + ".");
    }

    @Override
    public int getRowCount() {
        return this.points.length;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    public PixelSourceBands getPointAt(int index) {
        return this.points[index];
    }
}
