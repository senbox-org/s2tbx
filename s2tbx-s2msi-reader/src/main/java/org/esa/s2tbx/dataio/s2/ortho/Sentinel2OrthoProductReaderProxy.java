package org.esa.s2tbx.dataio.s2.ortho;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.esa.s2tbx.dataio.s2.filepatterns.S2NamingConventionUtils;
import org.esa.s2tbx.dataio.s2.l1c.Sentinel2L1CProductReader;
import org.esa.s2tbx.dataio.s2.l2a.Sentinel2L2AProductReader;
import org.esa.s2tbx.dataio.s2.l2hf.l2h.Sentinel2L2HProductReader;
import org.esa.s2tbx.dataio.s2.l2hf.l2f.Sentinel2L2FProductReader;
import org.esa.s2tbx.dataio.s2.l3.Sentinel2L3ProductReader;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductSubsetDef;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by obarrile on 08/06/2016.
 */
public class Sentinel2OrthoProductReaderProxy implements ProductReader {

    private Sentinel2OrthoProductReader reader;
    private final S2OrthoProductReaderPlugIn readerPlugIn;
    private final String epsgCode;

    public Sentinel2OrthoProductReaderProxy(S2OrthoProductReaderPlugIn readerPlugIn, String epsgCode) {
        this.readerPlugIn = readerPlugIn;
        this.epsgCode = epsgCode;
    }

    @Override
    public ProductReaderPlugIn getReaderPlugIn() {
        return this.readerPlugIn;
    }

    @Override
    public Object getInput() {
        if (this.reader == null) {
            return null;
        }
        return this.reader.getInput();
    }

    @Override
    public ProductSubsetDef getSubsetDef() {
        if (this.reader == null) {
            return null;
        }
        return this.reader.getSubsetDef();
    }

    @Override
    public Product readProductNodes(Object input, ProductSubsetDef subsetDef) throws IOException {
        if (!(input instanceof File)) {
            throw new IOException("Invalid input");
        }
        VirtualPath virtualPath = null;
        if (this.reader == null) {
            File file = (File) input;
            Path inputPath = file.toPath();
            VirtualPath sentinel2VirtualPath = S2NamingConventionUtils.transformToSentinel2VirtualPath(inputPath);
            INamingConvention namingConvention = NamingConventionFactory.createOrthoNamingConvention(sentinel2VirtualPath);
            if (namingConvention == null) {
                throw new IOException("Invalid input");
            }
            S2Config.Sentinel2ProductLevel level = namingConvention.getProductLevel();
            if (level == S2Config.Sentinel2ProductLevel.L2A) {
                this.reader = new Sentinel2L2AProductReader(this.readerPlugIn, this.epsgCode);
            }else if (level == S2Config.Sentinel2ProductLevel.L2H) {
                this.reader = new Sentinel2L2HProductReader(this.readerPlugIn, this.epsgCode);
            }else if (level == S2Config.Sentinel2ProductLevel.L2F) {
                this.reader = new Sentinel2L2FProductReader(this.readerPlugIn, this.epsgCode);
            } else if (level == S2Config.Sentinel2ProductLevel.L1C) {
                this.reader = new Sentinel2L1CProductReader(this.readerPlugIn, this.epsgCode);
            } else if (level == S2Config.Sentinel2ProductLevel.L3) {
                this.reader = new Sentinel2L3ProductReader(this.readerPlugIn, this.epsgCode);
            } else {
                throw new IOException("Invalid level " + level + ".");
            }
            virtualPath = namingConvention.getInputXml();
        }
        return this.reader.readProductNodes(virtualPath, subsetDef);
    }

    @Override
    public void readBandRasterData(Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm)
                                   throws IOException {
        // so nothing
    }

    @Override
    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }
}
