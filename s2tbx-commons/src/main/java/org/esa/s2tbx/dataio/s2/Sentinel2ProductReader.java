package org.esa.s2tbx.dataio.s2;

import org.esa.s2tbx.dataio.jp2.CodeStreamUtils;
import org.esa.s2tbx.dataio.jp2.TileLayout;
import org.esa.s2tbx.dataio.s2.filepatterns.S2ProductFilename;
import org.esa.snap.framework.dataio.AbstractProductReader;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.esa.snap.framework.datamodel.Product;
import org.esa.snap.util.SystemUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Base class for all Sentinel-2 product readers
 *
 * @author Nicolas Ducoin
 */
public abstract class Sentinel2ProductReader  extends AbstractProductReader {

    private S2Config config;


    public Sentinel2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);

        this.config = new S2Config();
    }

    /**
     *
     * @return The configuration file specific to a product reader
     */
    public S2Config getConfig() {
        return config;
    }

    protected abstract Product getMosaicProduct(File granuleMetadataFile) throws IOException;

        @Override
    protected Product readProductNodesImpl() throws IOException {
        SystemUtils.LOG.fine("readProductNodeImpl, " + getInput().toString());

        Product p;

        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }

        if (S2ProductFilename.isMetadataFilename(inputFile.getName())) {
            p = getMosaicProduct(inputFile);

            if (p != null) {
                p.setModified(false);
            }
        } else {
            throw new IOException("Unhandled file type.");
        }

        return p;
    }

    /**
     *
     * update the tile layout in S2Config
     *
     * @param metadataFilePath the path to the product metadata file
     * @param isGranule true if it is the metadata file of a granule
     * @param resolution the resolution we want to update, or null to update all resolutions
     */
    protected void updateTileLayout(Path metadataFilePath, boolean isGranule, S2SpatialResolution resolution) {

        if(resolution == null) {
            for(S2SpatialResolution layoutResolution: S2SpatialResolution.values()) {
                updateTileLayout(metadataFilePath, isGranule, layoutResolution);
            }
        } else {

            TileLayout tileLayout;
            if(isGranule) {
                tileLayout = retrieveTileLayoutFromGranuleMetadataFile(
                        metadataFilePath, resolution);
            } else {
                tileLayout = retrieveTileLayoutFromProduct(metadataFilePath, resolution);
            }
            config.updateTileLayout(resolution,tileLayout);
        }
    }


    /**
     * From a granule path, search a jpeg file for the given resolution, extract tile layout
     * information and update
     *
     * @param granuleMetadataFilePath the complete path to the granule metadata file
     * @param resolution              the resolution for which we wan to find the tile layout
     * @return the tile layout for the resolution, or {@code null} if none was found
     */
    public TileLayout retrieveTileLayoutFromGranuleMetadataFile(Path granuleMetadataFilePath, S2SpatialResolution resolution) {
        TileLayout tileLayoutForResolution = null;

        if(Files.exists(granuleMetadataFilePath) && granuleMetadataFilePath.toString().endsWith(".xml")) {
            Path granuleDirPath = granuleMetadataFilePath.getParent();
            tileLayoutForResolution = retrieveTileLayoutFromGranuleDirectory(granuleDirPath, resolution);
        }

        return tileLayoutForResolution;
    }



        /**
         * From a product path, search a jpeg file for the given resolution, extract tile layout
         * information and update
         *
         * @param productMetadataFilePath the complete path to the product metadata file
         * @param resolution the resolution for which we wan to find the tile layout
         * @return the tile layout for the resolution, or {@code null} if none was found

         */
    public TileLayout retrieveTileLayoutFromProduct(Path productMetadataFilePath, S2SpatialResolution resolution) {
        TileLayout tileLayoutForResolution = null;

        if(Files.exists(productMetadataFilePath) && productMetadataFilePath.toString().endsWith(".xml")) {
            Path productFolder = productMetadataFilePath.getParent();
            Path granulesFolder = productFolder.resolve("GRANULE");
            try {
                DirectoryStream<Path> granulesFolderStream = Files.newDirectoryStream(granulesFolder);

                for(Path granulePath : granulesFolderStream) {
                    tileLayoutForResolution = retrieveTileLayoutFromGranuleDirectory(granulePath, resolution);
                    if(tileLayoutForResolution != null) {
                        break;
                    }
                }
            } catch (IOException e) {
                SystemUtils.LOG.warning("Could not retrieve tile layout for product " +
                                                productMetadataFilePath.toAbsolutePath().toString() +
                                                " error returned: " +
                                                e.getMessage());
            }
        }


        return tileLayoutForResolution;
    }

    /**
     * From a granule path, search a jpeg file for the given resolution, extract tile layout
     * information and update
     *
     * @param granuleMetadataPath the complete path to the granule directory
     * @param resolution the resolution for which we wan to find the tile layout
     * @return the tile layout for the resolution, or {@code null} if none was found
     *
     */
    private TileLayout retrieveTileLayoutFromGranuleDirectory(Path granuleMetadataPath, S2SpatialResolution resolution) {
        TileLayout tileLayoutForResolution = null;

        Path pathToImages = granuleMetadataPath.resolve("IMG_DATA");

        try {

            for (Path imageFilePath : getImageDirectories(pathToImages, resolution)) {
                try {
                    tileLayoutForResolution =
                            CodeStreamUtils.getTileLayoutWithOpenJPEG(S2Config.OPJ_INFO_EXE, imageFilePath.toUri());
                    if(tileLayoutForResolution != null) {
                        break;
                    }
                } catch (IOException | InterruptedException e) {
                    // if we have an exception, we try with the next file (if any)
                    // and log a warning
                    SystemUtils.LOG.warning(
                            "Could not retrieve tile layout for file " +
                                    imageFilePath.toAbsolutePath().toString() +
                                    " error returned: " +
                                    e.getMessage());
                }
            }
        } catch (IOException e) {
            SystemUtils.LOG.warning(
                    "Could not retrieve tile layout for granule " +
                            granuleMetadataPath.toAbsolutePath().toString() +
                            " error returned: " +
                            e.getMessage());
        }

        return tileLayoutForResolution;
    }




    /**
     * For a given resolution, gets the list of band names.
     * For example, for 10m L1C, {"B02", "B03", "B04", "B08"} should be returned
     *
     * @param resolution the resolution for which the band names should be returned
     * @return then band names or {@code null} if not applicable.
     */
    protected abstract String[] getBandNames(S2SpatialResolution resolution);

    /**
     *  get an iterator to image files in pathToImages containing files for the given resolution
     *
     *  This method is based on band names, if resolution can't be based on band names or if image files are not in
     *  pathToImages (like for L2A products), this method has to be overriden
     *
     * @param pathToImages the path to the directory containing the images
     * @param resolution the resolution for which we want to get images
     * @return a {@link DirectoryStream<Path>}, iterator on the list of image path
     * @throws IOException if an I/O error occurs
     */
    protected DirectoryStream<Path> getImageDirectories(Path pathToImages, S2SpatialResolution resolution) throws IOException {
        return Files.newDirectoryStream(pathToImages, entry -> {
            String[] bandNames = getBandNames(resolution);
            if (bandNames != null) {
                for (String bandName : bandNames) {
                    if (entry.toString().endsWith(bandName + ".jp2")) {
                        return true;
                    }
                }
            }
            return false;
        });
    }}
