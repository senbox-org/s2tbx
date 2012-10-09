package org.esa.beam.dataio.s2.jp2;

import java.io.File;
import java.io.IOException;

/**
 * Binding for the JasPer JPEG2000 library (http://www.ece.uvic.ca/~frodo/jasper/).
 *
 * @author Norman Fomferra
 */
public class JasPerImageAccess implements ImageAccess {

    @Override
    public native ImageRef openImage(File file) throws IOException;

    @Override
    public native void disposeImage(ImageRef imageRef) throws IOException;

    @Override
    public native int getNumResolutionLevels(ImageRef imageRef);

    @Override
    public native int getNumComponents(ImageRef imageRef);

    @Override
    public native int getSampleDataType(ImageRef imageRef, int componentIndex);

    @Override
    public native int getImageWidth(ImageRef imageRef, int resolutionLevel);

    @Override
    public native int getImageHeight(ImageRef imageRef, int resolutionLevel);

    @Override
    public native void readRasterData(ImageRef imageRef, int componentIndex, int resolutionLevel,
                                      int x, int y, int width, int height, Object buffer) throws IOException;
}
