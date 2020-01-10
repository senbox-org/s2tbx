package org.esa.s2tbx.dataio.s2.l1b;

import org.esa.snap.core.dataio.MetadataInspector;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 10/1/2020.
 */
public class Sentinel2L1BMetadataInspector implements MetadataInspector {

    private final Sentinel2L1BProductReader.ProductInterpretation interpretation;

    public Sentinel2L1BMetadataInspector(Sentinel2L1BProductReader.ProductInterpretation interpretation) {
        this.interpretation = interpretation;
    }

    @Override
    public Metadata getMetadata(Path productPath) throws IOException {
        return null;
    }
}
