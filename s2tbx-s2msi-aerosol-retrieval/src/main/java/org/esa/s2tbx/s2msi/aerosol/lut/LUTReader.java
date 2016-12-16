package org.esa.s2tbx.s2msi.aerosol.lut;

import com.bc.ceres.binding.ValidationException;
import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.RasterDataNode;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * todo: add comment
 * To change this template use File | Settings | File Templates.
 * Date: 16.12.2016
 * Time: 15:07
 *
 * @author olafd
 */
public class LUTReader {

    private final LUTAccessor lutAccessor;
    private int[] bandIndices;
    private float[][] lut;

    private int[][] minMaxIndices;

    public LUTReader(LUTAccessor lutAccessor) throws IOException {
        this.lutAccessor = lutAccessor;

        try {
            lutAccessor.validate();
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        initIndices();
        lut = readLut(ProgressMonitor.NULL);
    }

    public float[][] readLut(ProgressMonitor progressMonitor) throws IOException {
        final int[] lutshape = lutAccessor.getLUTShapes();
        final int lutLength = getLutLength();
        if (progressMonitor != null) {
            progressMonitor.beginTask("Reading Look-Up-Table", lutLength);
        }
        float[][] newLut = new float[lutLength][LUTConstants.LUT_RESULT_VECTOR_LENGTH];
        ImageInputStream stream = lutAccessor.openLUTStream();
//        readLut(newLut, lutshape, 0, stream, progressMonitor, 0);
        readValuesFromStream(newLut, lutshape, stream, progressMonitor, 0);
        stream.close();
        return newLut;
    }

    private void readValuesFromStream(float[][] lutArray, int[] lutshape, ImageInputStream stream,
                                      ProgressMonitor progressMonitor, int newPosition) throws IOException {
        int currentIndex = 0;
        float[] values = new float[lutshape[lutshape.length - 1]];
        for (int i = 0; i < values.length; i++) {
            values[i] = stream.readFloat();
        }
        if (stream.getStreamPosition() > 0) {
            stream.flushBefore(stream.getStreamPosition() - 1);
        }
        for (Integer bandIndex : bandIndices) {
            lutArray[newPosition][currentIndex++] = values[bandIndex];    // todo: fill as 1D stream, see BbdrUtils.getAotLookupTable and test
        }
        if (progressMonitor != null) {
            if (progressMonitor.isCanceled()) {
                stream.close();
                lut = null;
                return;
            }
            progressMonitor.worked(1);
        }
    }

    private int getLutLength() {
        int lutLength = 1;
        for (int[] minMaxIndex : minMaxIndices) {
            lutLength *= (minMaxIndex[1] - minMaxIndex[0] + 1);
        }
        return lutLength;
    }

    private void initIndices() {
        bandIndices = new int[lutAccessor.getNumberOfNonSpectralProperties()];
        minMaxIndices = new int[lutAccessor.getNumberOfNonSpectralProperties()][2];
        for (int i = 0; i < minMaxIndices.length; i++) {
            bandIndices[i] = i;
            minMaxIndices[i][0] = 0;
            minMaxIndices[i][1] = LUTConstants.dimValues[i].length - 1;
        }
    }

}
