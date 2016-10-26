package org.esa.s2tbx.dataio.s2.ortho;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.Sentinel2ProductReader;
import org.esa.s2tbx.dataio.s2.l1c.Sentinel2L1CProductReader;
import org.esa.s2tbx.dataio.s2.l2a.Sentinel2L2AProductReader;
import org.esa.s2tbx.dataio.s2.l3.Sentinel2L3ProductReader;
import org.esa.snap.core.dataio.IllegalFileFormatException;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;

import java.io.File;
import java.io.IOException;

/**
 * Created by obarrile on 08/06/2016.
 */
public class Sentinel2OrthoProductReaderProxy implements ProductReader {


    private Sentinel2OrthoProductReader reader;
    private static S2ProductCRSCache crsCache = new S2ProductCRSCache();
    private final S2OrthoProductReaderPlugIn readerPlugIn;
    private final String epsgCode;


    public Sentinel2OrthoProductReaderProxy(S2OrthoProductReaderPlugIn readerPlugIn, String epsgCode) {
        this.readerPlugIn = readerPlugIn;
        this.epsgCode = epsgCode;
    }

    public ProductReaderPlugIn getReaderPlugIn(){
        return readerPlugIn;
    }

    public Object getInput() {
        if(reader == null){
            return null;
        }
        return reader.getInput();
    }

    public ProductSubsetDef getSubsetDef() {
        if(reader == null){
            return null;
        }
        return reader.getSubsetDef();
    }

    public Product readProductNodes(Object input,
                             ProductSubsetDef subsetDef) throws IOException, IllegalFileFormatException {
        File file = null;
        if (reader == null) {
            file = S2OrthoProductReaderPlugIn.preprocessInput(input).toFile();
            if(file == null) {
                throw new IOException("Invalid input");
            }

            crsCache.ensureIsCached(file.getAbsolutePath());
            S2Config.Sentinel2ProductLevel level = crsCache.getProductLevel(file.getAbsolutePath());

            if (level == S2Config.Sentinel2ProductLevel.L2A) {
                reader = new Sentinel2L2AProductReader(readerPlugIn, epsgCode);
            } else if (level == S2Config.Sentinel2ProductLevel.L1C) {
                reader = new Sentinel2L1CProductReader(readerPlugIn, epsgCode);
            } else if (level == S2Config.Sentinel2ProductLevel.L3) {
                reader = new Sentinel2L3ProductReader(readerPlugIn, epsgCode);
            } else {
                throw new IOException("Invalid input");
            }
        }
        return reader.readProductNodes(file, subsetDef);
    }


    public void readBandRasterData(Band destBand,
                            int destOffsetX, int destOffsetY,
                            int destWidth, int destHeight,
                            ProductData destBuffer, ProgressMonitor pm) throws IOException {
        return;
    }

    public void close() throws IOException{
        if(reader == null){
            return;
        }
        reader.close();
    }
}
