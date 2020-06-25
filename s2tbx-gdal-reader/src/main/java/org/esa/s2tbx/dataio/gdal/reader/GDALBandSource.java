package org.esa.s2tbx.dataio.gdal.reader;

import java.nio.file.Path;

public interface GDALBandSource {

    public Path getSourceLocalFile();

    public int getBandIndex();
}
