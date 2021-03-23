package org.esa.s2tbx.dataio.s2.metadata;

import org.esa.s2tbx.commons.FilePath;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;
import org.esa.snap.dataio.geotiff.GeoTiffProductReaderPlugIn;
import org.esa.snap.lib.openjpeg.jp2.TileLayout;
import org.esa.snap.lib.openjpeg.utils.OpenJpegUtils;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.s2.*;
import org.esa.s2tbx.dataio.s2.filepatterns.INamingConvention;
import org.esa.s2tbx.dataio.s2.filepatterns.NamingConventionFactory;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jcoravu on 10/1/2020.
 */
public abstract class AbstractS2MetadataReader {

    private static final Logger logger = Logger.getLogger(AbstractS2MetadataReader.class.getName());

    protected final INamingConvention namingConvention;

    protected AbstractS2MetadataReader(INamingConvention namingConvention) throws IOException {
        if (namingConvention == null) {
            throw new NullPointerException("The naming convention is null.");
        } else if (!namingConvention.hasValidStructure()) {
            throw new IllegalStateException("The naming convention structure is invalid for input file '"+namingConvention.getInputXml().getFullPathString()+"'");
        }
        this.namingConvention = namingConvention;
    }

    /**
     * For a given resolution, gets the list of band names.
     * For example, for 10m L1C, {"B02", "B03", "B04", "B08"} should be returned
     *
     * @param resolution the resolution for which the band names should be returned
     * @return then band names or {@code null} if not applicable.
     */
    protected abstract String[] getBandNames(S2SpatialResolution resolution);

    public abstract S2Metadata readMetadataHeader(VirtualPath metadataPath, S2Config config) throws IOException, ParserConfigurationException, SAXException;

    public boolean isGranule() {
        return (this.namingConvention.getInputType() == S2Config.Sentinel2InputType.INPUT_TYPE_GRANULE_METADATA);
    }

    public INamingConvention getNamingConvention() {
        return namingConvention;
    }

    /**
     * Update the tile layout in S2Config
     *
     * @param metadataFilePath the path to the product metadata file
     * @return false when every tileLayout is null
     */
    public final S2Config readTileLayouts(VirtualPath metadataFilePath) {
        S2Config config = null;
        for (S2SpatialResolution layoutResolution : S2SpatialResolution.values()) {
            TileLayout tileLayout;
            if (isGranule()) {
                tileLayout = retrieveTileLayoutFromGranuleMetadataFile(metadataFilePath, layoutResolution);
            } else {
                tileLayout = retrieveTileLayoutFromProduct(metadataFilePath, layoutResolution);
            }
            if (tileLayout != null) {
                if (config == null) {
                    config = new S2Config();
                }
                config.updateTileLayout(layoutResolution, tileLayout);
            }
        }
        return config;
    }

    /**
     * From a granule path, search a jpeg file for the given resolution, extract tile layout
     * information and update
     *
     * @param granuleMetadataFilePath the complete path to the granule metadata file
     * @param resolution              the resolution for which we wan to find the tile layout
     * @return the tile layout for the resolution, or {@code null} if none was found
     */
    public final TileLayout retrieveTileLayoutFromGranuleMetadataFile(VirtualPath granuleMetadataFilePath, S2SpatialResolution resolution) {
        TileLayout tileLayoutForResolution = null;
        if (granuleMetadataFilePath.exists() && granuleMetadataFilePath.getFileName().toString().endsWith(".xml")) {
            VirtualPath granuleDirPath = granuleMetadataFilePath.getParent();
            tileLayoutForResolution = retrieveTileLayoutFromGranuleDirectory(granuleDirPath, resolution);
        }
        return tileLayoutForResolution;
    }

    /**
     * From a product path, search a jpeg file for the given resolution, extract tile layout
     * information and update
     *
     * @param productMetadataFilePath the complete path to the product metadata file
     * @param resolution              the resolution for which we wan to find the tile layout
     * @return the tile layout for the resolution, or {@code null} if none was found
     */
    public final TileLayout retrieveTileLayoutFromProduct(VirtualPath productMetadataFilePath, S2SpatialResolution resolution) {
        TileLayout tileLayoutForResolution = null;
        if (productMetadataFilePath.exists() && productMetadataFilePath.getFileName().toString().endsWith(".xml")) {
            VirtualPath granulesFolder = productMetadataFilePath.resolveSibling("GRANULE");

            try {
                VirtualPath[] granulesFolderList = granulesFolder.listPaths();
                if (granulesFolderList != null && granulesFolderList.length > 0) {
                    for (VirtualPath granulePath : granulesFolderList) {
                        tileLayoutForResolution = retrieveTileLayoutFromGranuleDirectory(granulePath, resolution);
                        if (tileLayoutForResolution != null) {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not retrieve tile layout for product " + productMetadataFilePath.getFullPathString() + " error returned: " + e.getMessage(), e);
            }
        }
        return tileLayoutForResolution;
    }

    /**
     * Get an iterator to image files in pathToImages containing files for the given resolution
     * <p>
     * This method is based on band names, if resolution can't be based on band names or if image files are not in
     * pathToImages (like for L2A products), this method has to be overriden
     *
     * @param pathToImages the path to the directory containing the images
     * @param resolution   the resolution for which we want to get images
     * @return a {@link DirectoryStream < Path >}, iterator on the list of image path
     * @throws IOException if an I/O error occurs
     */
    protected List<VirtualPath> getImageDirectories(VirtualPath pathToImages, S2SpatialResolution resolution) throws IOException {
        long startTime = System.currentTimeMillis();

        List<VirtualPath> imageDirectories = new ArrayList<>();
        String[] bandNames = getBandNames(resolution);
        if (bandNames != null && bandNames.length > 0) {
            VirtualPath[] imagePaths = pathToImages.listPaths();
            if (imagePaths != null && imagePaths.length > 0) {
                for (String bandName : bandNames) {
                    for (VirtualPath imagePath : imagePaths) {
                        String fileNameStr= imagePath.getFileName().toString();
                        if (fileNameStr.endsWith(bandName + ".jp2")||fileNameStr.endsWith(bandName + ".TIF")) {
                            imageDirectories.add(imagePath);
                        }
                    }
                }
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            double elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000.d;
            logger.log(Level.FINE, "Finish finding the image directories using the images folder '" +pathToImages.getFullPathString()+"', size: "+imageDirectories.size()+"', elapsed time: " + elapsedTimeInSeconds + " seconds.");
        }

        return imageDirectories;
    }

    /**
     * From a granule path, search a jpeg file for the given resolution, extract tile layout
     * information and update
     *
     * @param granuleMetadataPath the complete path to the granule directory
     * @param resolution          the resolution for which we wan to find the tile layout
     * @return the tile layout for the resolution, or {@code null} if none was found
     */
    private TileLayout retrieveTileLayoutFromGranuleDirectory(VirtualPath granuleMetadataPath, S2SpatialResolution resolution) {
        TileLayout tileLayoutForResolution = null;
        VirtualPath pathToImages = granuleMetadataPath.resolve("IMG_DATA");
        try {
            List<VirtualPath> imageDirectories = getImageDirectories(pathToImages, resolution);
            for (VirtualPath imageFilePath : imageDirectories) {
                try {
                    if(imageFilePath.getFileName().toString().endsWith(".TIF"))
                        tileLayoutForResolution = readTileLayoutFromTIFFile(imageFilePath);
                    else
                        tileLayoutForResolution = readTileLayoutFromJP2File(imageFilePath);
                    if (tileLayoutForResolution != null) {
                        break;
                    }
                } catch (IOException | InterruptedException e) {
                    // if we have an exception, we try with the next file (if any) // and log a warning
                    logger.log(Level.WARNING, "Could not retrieve tile layout for file " + imageFilePath.toString() + " error returned: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not retrieve tile layout for granule " + granuleMetadataPath.toString() + " error returned: " + e.getMessage(), e);
        }

        return tileLayoutForResolution;
    }

    public static TileLayout readTileLayoutFromJP2File(VirtualPath imageFilePath) throws IOException, InterruptedException {
        TileLayout tileLayout;
        if (OpenJpegUtils.canReadJP2FileHeaderWithOpenJPEG()) {
            Path jp2FilePath = imageFilePath.getLocalFile();
            tileLayout = OpenJpegUtils.getTileLayoutWithOpenJPEG(S2Config.OPJ_INFO_EXE, jp2FilePath);
        } else {
            try (FilePath filePath = imageFilePath.getFilePath()) {
                boolean canSetFilePosition = !imageFilePath.getVirtualDir().isArchive();
                tileLayout = OpenJpegUtils.getTileLayoutWithInputStream(filePath.getPath(), 5 * 1024, canSetFilePosition);
            }
        }
        return tileLayout;
    }

    public static TileLayout readTileLayoutFromTIFFile(VirtualPath imageFilePath) throws IOException, InterruptedException {
        TileLayout tileLayout=null;
        ImageInputStream inputStream = null;
        try {
            inputStream = ImageIO.createImageInputStream(imageFilePath);
            
            File inputFile=imageFilePath.getFilePath().getPath().toFile();
            VirtualDirEx virtualDir = VirtualDirEx.build(imageFilePath.getFilePath().getPath(), false, true);
            if (virtualDir == null) {
                throw new NullPointerException("The virtual dir is null for input path '" + imageFilePath.getFullPathString() + "'.");
            }
    
            try {
                inputFile = virtualDir.getFile(imageFilePath.getFullPathString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            GeoTiffProductReaderPlugIn geoTiffReaderPlugIn = new GeoTiffProductReaderPlugIn();
            GeoTiffProductReader geoTiffProductReader = new GeoTiffProductReader(geoTiffReaderPlugIn);
            
            Product tiffProduct = geoTiffProductReader.readProductNodes(inputFile, null);
            int width = tiffProduct.getSceneRasterWidth();
            int height = tiffProduct.getSceneRasterHeight();
            int tileWidth = tiffProduct.getSceneRasterWidth();
            int tileHeight = tiffProduct.getSceneRasterHeight();
            int numXTiles = 1;
            int numYTiles = 1;
            int numResolutions = 1;
            tileLayout = new TileLayout(width, height, tileWidth, tileHeight, numXTiles, numYTiles, numResolutions);
        } catch (IOException e) {
            logger.warning(String.format("Unable to get tile layout",imageFilePath.getFullPathString()));
        }
        
        
        return tileLayout;
    }
}
