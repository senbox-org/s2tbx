package org.esa.s2tbx.dataio.s2.l2a.metadata;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.s2tbx.dataio.s2.VirtualPath;
import org.esa.s2tbx.dataio.s2.ortho.metadata.AbstractS2OrthoMetadataReader;
import org.esa.s2tbx.dataio.s2.ortho.metadata.S2OrthoMetadata;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcoravu on 10/1/2020.
 */
public class S2L2aProductMetadataReader extends AbstractS2OrthoMetadataReader {

    public S2L2aProductMetadataReader(VirtualPath virtualPath, String epsgCode) throws IOException {
        super(virtualPath, epsgCode);
    }

    @Override
    protected S2OrthoMetadata parseHeader(VirtualPath path, String granuleName, S2Config config, String epsg, boolean isAGranule) throws IOException {
        try {
            return L2aMetadata.parseHeader(path, granuleName, config, epsg, this.namingConvention.getResolution(), isAGranule, this.namingConvention);
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Failed to parse metadata field '" + path.getFileName().toString() + "'.", e);
        }
    }

    @Override
    protected String[] getBandNames(S2SpatialResolution resolution) {
        //TODO Jean implement the method to return the correct band names
        return null;
    }

    @Override
    protected List<VirtualPath> getImageDirectories(VirtualPath pathToImages, S2SpatialResolution spatialResolution) throws IOException {
        //TODO Jean use the band names to filter the image files because thne result list contains more files than the band names
        // and the files order may be different on each operating system or from a folder of a zip archive

        List<VirtualPath> imageDirectories = new ArrayList<>();
        String resolutionFolder = "R" + Integer.toString(spatialResolution.resolution) + "m";
        VirtualPath pathToImagesOfResolution = pathToImages.resolve(resolutionFolder);
        if (pathToImagesOfResolution.exists()) {
            VirtualPath[] imagePaths = pathToImagesOfResolution.listPaths();
            if (imagePaths != null && imagePaths.length > 0) {
                for (VirtualPath imagePath : imagePaths) {
                    if (imagePath.getFileName().toString().endsWith("_" + spatialResolution.resolution + "m.jp2")) {
                        imageDirectories.add(imagePath);
                    }
                }
            }
        }
        return imageDirectories;
    }
}
