package org.esa.beam.dataio.s2;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.CrsGeoCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.TiePointGrid;
import org.esa.beam.jai.ImageManager;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.util.io.FileUtils;
import org.esa.beam.util.logging.BeamLogManager;
import org.geotools.geometry.Envelope2D;
import org.jdom.JDOMException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.esa.beam.dataio.s2.L1cMetadata.ProductCharacteristics;
import static org.esa.beam.dataio.s2.L1cMetadata.ResampleData;
import static org.esa.beam.dataio.s2.L1cMetadata.SpectralInformation;
import static org.esa.beam.dataio.s2.L1cMetadata.Tile;
import static org.esa.beam.dataio.s2.L1cMetadata.parseHeader;
import static org.esa.beam.dataio.s2.S2Config.DEFAULT_JAI_TILE_SIZE;
import static org.esa.beam.dataio.s2.S2Config.FILL_CODE_MOSAIC_BG;
import static org.esa.beam.dataio.s2.S2Config.L1C_TILE_LAYOUTS;
import static org.esa.beam.dataio.s2.S2Config.S2_WAVEBAND_INFOS;
import static org.esa.beam.dataio.s2.S2Config.SAMPLE_PRODUCT_DATA_TYPE;

// todo - register reasonable RGB profile(s)
// todo - set a band's validMaskExpr or no-data value (read from GML)
// todo - set band's ImageInfo from min,max,histogram found in header (--> L1cMetadata.quicklookDescriptor)
// todo - tie point grids have been added to L1C tiles, but not to entire scene mosaics
// todo - viewing incidence tie-point grids contain NaN values - find out how to correctly treat them
// todo - configure BEAM module / SUHET installer so that OpenJPEG "opj_decompress" executable is accessible on all platforms

// todo - better collect problems during product opening and generate problem report (requires reader API change), see {@report "Problem detected..."} code marks
// todo - Replace print() calls by using a logger

/**
 * <p>
 * This product reader can currently read single L1C tiles (also called L1C granules) and entire L1C scenes composed of
 * multiple L1C tiles.
 * </p>
 * <p>
 * To read single tiles, select any tile image file (IMG_*.jp2) within a product package. The reader will then
 * collect other band images for the selected tile and wiull also try to read the metadata file (MTD_*.xml).
 * </p>
 * <p>To read an entire scene, select the metadata file (MTD_*.xml) within a product package. The reader will then
 * collect other tile/band images and create a mosaic on the fly.
 * </p>
 *
 * @author Norman Fomferra
 */
public class Sentinel2ProductReader extends AbstractProductReader {

    private File cacheDir;

    static class BandInfo {
        final Map<String, File> tileIdToFileMap;
        final int bandIndex;
        final S2WavebandInfo wavebandInfo;
        final L1cTileLayout imageLayout;

        BandInfo(Map<String, File> tileIdToFileMap, int bandIndex, S2WavebandInfo wavebandInfo, L1cTileLayout imageLayout) {
            this.tileIdToFileMap = Collections.unmodifiableMap(tileIdToFileMap);
            this.bandIndex = bandIndex;
            this.wavebandInfo = wavebandInfo;
            this.imageLayout = imageLayout;
        }
    }


    Sentinel2ProductReader(Sentinel2ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // Should never not come here, since we have an OpImage that reads data
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }
        if (S2MetadataFilename.isMetadataFilename(inputFile.getName())) {
            return getL1cMosaicProduct(inputFile);
        } else if (S2ImageFilename.isImageFilename(inputFile.getName())) {
            return getL1cTileProduct(inputFile);
        } else {
            throw new IOException("Unhandled file type.");
        }
    }

    private Product getL1cTileProduct(File imageFile) throws IOException {

        S2ImageFilename imgFilename = S2ImageFilename.create(imageFile.getName());
        if (imgFilename == null) {
            throw new IOException();
        }

        File productDir = getProductDir(imageFile);
        initCacheDir(productDir);

        // Try to find metadata header

        L1cMetadata metadataHeader = null;
        File[] metadataFiles = productDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return S2MetadataFilename.isMetadataFilename(name);
            }
        });
        if (metadataFiles != null && metadataFiles.length > 0) {
            File metadataFile = metadataFiles[0];
            try {
                metadataHeader = parseHeader(metadataFile);
            } catch (JDOMException e) {
                // {@report "Failed to parse metadata file"}
                BeamLogManager.getSystemLogger().warning("Failed to parse metadata file: " + metadataFile);
            }
        } else {
            // {@report "No metadata file found"}
            BeamLogManager.getSystemLogger().warning("No metadata file found");
        }

        // Try to find other band images

        Map<Integer, BandInfo> bandInfoMap = new HashMap<Integer, BandInfo>();
        File[] files = productDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return S2ImageFilename.isImageFilename(name);
            }
        });
        if (files != null) {
            for (File file : files) {
                int bandIndex = imgFilename.getBand(file.getName());
                if (metadataHeader != null) {
                    SpectralInformation[] bandInformations = metadataHeader.getProductCharacteristics().bandInformations;
                    if (bandIndex >= 0 && bandIndex < bandInformations.length) {
                        BandInfo bandInfo = createBandInfoFromHeaderInfo(bandInformations[bandIndex],
                                                                         metadataHeader.getResampleData(),
                                                                         createFileMap(imgFilename.tileId, file));
                        bandInfoMap.put(bandIndex, bandInfo);
                    } else {
                        // {@report "Illegal band index"}
                    }
                } else {
                    if (bandIndex >= 0 && bandIndex < S2_WAVEBAND_INFOS.length) {
                        S2WavebandInfo wavebandInfo = S2_WAVEBAND_INFOS[bandIndex];
                        BandInfo bandInfo = createBandInfoFromDefaults(bandIndex, wavebandInfo,
                                                                       imgFilename.tileId,
                                                                       file);
                        bandInfoMap.put(bandIndex, bandInfo);
                    } else {
                        // {@report "Illegal band index"}
                    }
                }
            }
        }

        Product product = new Product(FileUtils.getFilenameWithoutExtension(imageFile).substring("IMG_".length()),
                                      "S2_MSI_" + imgFilename.procLevel,
                                      L1C_TILE_LAYOUTS[S2SpatialResolution.R10M.id].width,
                                      L1C_TILE_LAYOUTS[S2SpatialResolution.R10M.id].height);

        setStartStopTime(product, imgFilename.start, imgFilename.stop);
        if (metadataHeader != null) {
            L1cSceneDescription sceneDescription = L1cSceneDescription.create(metadataHeader);
            int tileIndex = sceneDescription.getTileIndex(imgFilename.tileId);
            Envelope2D tileEnvelope = sceneDescription.getTileEnvelope(tileIndex);
            setGeoCoding(product, tileEnvelope);
            addL1cTileTiePointGrids(metadataHeader, product, tileIndex);
            product.getMetadataRoot().addElement(metadataHeader.getMetadataElement());
        }

        addBands(product, bandInfoMap, new L1cTileMultiLevelImageFactory(ImageManager.getImageToModelTransform(product.getGeoCoding())));

        return product;
    }

    private Product getL1cMosaicProduct(File metadataFile) throws IOException {
        L1cMetadata metadataHeader;

        try {
            metadataHeader = parseHeader(metadataFile);
        } catch (JDOMException e) {
            throw new IOException("Failed to parse metadata in " + metadataFile.getName());
        }

        S2MetadataFilename mtdFilename = S2MetadataFilename.create(metadataFile.getName());
        L1cSceneDescription sceneDescription = L1cSceneDescription.create(metadataHeader);

        File productDir = getProductDir(metadataFile);
        initCacheDir(productDir);

        ProductCharacteristics productCharacteristics = metadataHeader.getProductCharacteristics();
        ResampleData resampleData = metadataHeader.getResampleData();

        Map<Integer, BandInfo> bandInfoMap = new HashMap<Integer, BandInfo>();
        List<L1cMetadata.Tile> tileList = metadataHeader.getTileList();
        for (SpectralInformation bandInformation : productCharacteristics.bandInformations) {
            int bandIndex = bandInformation.bandId;
            if (bandIndex >= 0 && bandIndex < productCharacteristics.bandInformations.length) {

                HashMap<String, File> tileFileMap = new HashMap<String, File>();
                for (Tile tile : tileList) {
                    String imgFilename = mtdFilename.getImgFilename(bandIndex, tile.id);
                    File file = new File(productDir, imgFilename);
                    if (file.exists()) {
                        tileFileMap.put(tile.id, file);
                    } else {
                        // {@report "missing file"}
                        System.out.printf("Warning: missing file %s\n", file);
                    }
                }

                if (!tileFileMap.isEmpty()) {
                    BandInfo bandInfo = createBandInfoFromHeaderInfo(bandInformation, resampleData, tileFileMap);
                    bandInfoMap.put(bandIndex, bandInfo);
                } else {
                    // {@report "no image files found"}
                    System.out.printf("Warning: no image files found for band %s\n", bandInformation.physicalBand);
                }
            } else {
                // {@report "illegal band index"}
                System.out.printf("Warning: illegal band index detected for band %s\n", bandInformation.physicalBand);
            }
        }

        Product product = new Product(FileUtils.getFilenameWithoutExtension(metadataFile).substring("MTD_".length()),
                                      "S2_MSI_" + productCharacteristics.processingLevel,
                                      sceneDescription.getSceneRectangle().width,
                                      sceneDescription.getSceneRectangle().height);

        product.getMetadataRoot().addElement(metadataHeader.getMetadataElement());
        setStartStopTime(product, mtdFilename.start, mtdFilename.stop);
        setGeoCoding(product, sceneDescription.getSceneEnvelope());

        addTiePointGridBand(product, metadataHeader, sceneDescription, "sun_zenith", 0);
        addTiePointGridBand(product, metadataHeader, sceneDescription, "sun_azimuth", 1);
        addTiePointGridBand(product, metadataHeader, sceneDescription, "view_zenith", 2);
        addTiePointGridBand(product, metadataHeader, sceneDescription, "view_azimuth", 3);
        addBands(product, bandInfoMap, new L1cSceneMultiLevelImageFactory(sceneDescription, ImageManager.getImageToModelTransform(product.getGeoCoding())));

        return product;
    }

    private void addTiePointGridBand(Product product, L1cMetadata metadataHeader, L1cSceneDescription sceneDescription, String name, int tiePointGridIndex) {
        final Band band = product.addBand(name, ProductData.TYPE_FLOAT32);
        band.setSourceImage(new DefaultMultiLevelImage(new TiePointGridL1cSceneMultiLevelSource(sceneDescription, metadataHeader, ImageManager.getImageToModelTransform(product.getGeoCoding()), 6, tiePointGridIndex)));
    }

    private void addBands(Product product, Map<Integer, BandInfo> bandInfoMap, MultiLevelImageFactory mlif) throws IOException {
        product.setPreferredTileSize(DEFAULT_JAI_TILE_SIZE, DEFAULT_JAI_TILE_SIZE);
        product.setNumResolutionsMax(L1C_TILE_LAYOUTS[0].numResolutions);
        product.setAutoGrouping("reflec:radiance");

        ArrayList<Integer> bandIndexes = new ArrayList<Integer>(bandInfoMap.keySet());
        Collections.sort(bandIndexes);

        if (bandIndexes.isEmpty()) {
            throw new IOException("No valid bands found.");
        }

        for (Integer bandIndex : bandIndexes) {
            BandInfo bandInfo = bandInfoMap.get(bandIndex);
            Band band = addBand(product, bandInfo);
            band.setSourceImage(mlif.createSourceImage(bandInfo));
        }

        // todo - S2 spec is unclear about the use of this variable "Resample_Data/Reflectance_Conversion/U"
        // todo - Uncomment setting the spectral properties as soon as we have groups in VISAT's spectrum view

        // For testing - add TOA reflectance bands
        for (Integer bandIndex : bandIndexes) {
            BandInfo bandInfo = bandInfoMap.get(bandIndex);
            Band reflec = product.addBand(bandInfo.wavebandInfo.bandName.replace("B", "reflec_"),
                                          bandInfo.wavebandInfo.bandName
                                                  + " * (" + bandInfo.wavebandInfo.reflecUnit
                                                  + " / " + bandInfo.wavebandInfo.quantificationValue + ")");
            // reflec.setSpectralBandIndex(bandIndex);
            // reflec.setSpectralWavelength((float) bandInfo.wavebandInfo.wavelength);
            reflec.setDescription("TOA reflectance in " + bandInfo.wavebandInfo.bandName + " for demonstration purpose");
        }

        // For testing - add TOA radiance bands
        for (Integer bandIndex : bandIndexes) {
            BandInfo bandInfo = bandInfoMap.get(bandIndex);
            Band radiance = product.addBand(bandInfo.wavebandInfo.bandName.replace("B", "radiance_"),
                                            bandInfo.wavebandInfo.bandName
                                                    + " * (" + bandInfo.wavebandInfo.solarIrradiance
                                                    + " * " + bandInfo.wavebandInfo.reflecUnit
                                                    + " / " + bandInfo.wavebandInfo.quantificationValue + ")");
            // radiance.setSpectralBandIndex(bandIndex);
            // radiance.setSpectralWavelength((float) bandInfo.wavebandInfo.wavelength);
            radiance.setDescription("TOA radiance in " + bandInfo.wavebandInfo.bandName + " for demonstration purpose");
        }

        Band ndvi = product.addBand("ndvi", "(reflec_4 - reflec_9) / (reflec_4 + reflec_9)");
        ndvi.setDescription("TOA NDVI for demonstration purpose");
    }

    private Band addBand(Product product, BandInfo bandInfo) {
        final Band band = product.addBand(bandInfo.wavebandInfo.bandName, SAMPLE_PRODUCT_DATA_TYPE);
        band.setSpectralBandIndex(bandInfo.bandIndex);
        band.setSpectralWavelength((float) bandInfo.wavebandInfo.wavelength);
        band.setSpectralBandwidth((float) bandInfo.wavebandInfo.bandwidth);
        band.setSolarFlux((float) bandInfo.wavebandInfo.solarIrradiance);

        // todo - We don't use the scaling factor because we want to stay with 16bit unsigned short samples due to the large
        // amounts of data when saving the images. We provide virtual reflectance bands for this reason. We can use the
        // scaling factor again, once we have product writer parameters, so that users can decide to write data as
        // 16bit samples.
        //
        //band.setScalingFactor(bandInfo.wavebandInfo.scalingFactor);

        return band;
    }

    private void addL1cTileTiePointGrids(L1cMetadata metadataHeader, Product product, int tileIndex) {
        final TiePointGrid[] tiePointGrids = createL1cTileTiePointGrids(metadataHeader, tileIndex);
        for (TiePointGrid tiePointGrid : tiePointGrids) {
            product.addTiePointGrid(tiePointGrid);
        }
    }

    private TiePointGrid[] createL1cTileTiePointGrids(L1cMetadata metadataHeader, int tileIndex) {
        Tile tile = metadataHeader.getTileList().get(tileIndex);
        int gridHeight = tile.sunAnglesGrid.zenith.length;
        int gridWidth = tile.sunAnglesGrid.zenith[0].length;
        float[] sunZeniths = new float[gridWidth * gridHeight];
        float[] sunAzimuths = new float[gridWidth * gridHeight];
        float[] viewingZeniths = new float[gridWidth * gridHeight];
        float[] viewingAzimuths = new float[gridWidth * gridHeight];
        Arrays.fill(viewingZeniths, Float.NaN);
        Arrays.fill(viewingAzimuths, Float.NaN);
        L1cMetadata.AnglesGrid sunAnglesGrid = tile.sunAnglesGrid;
        L1cMetadata.AnglesGrid[] viewingIncidenceAnglesGrids = tile.viewingIncidenceAnglesGrids;
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                final int index = y * gridWidth + x;
                sunZeniths[index] = sunAnglesGrid.zenith[y][x];
                sunAzimuths[index] = sunAnglesGrid.azimuth[y][x];
                for (L1cMetadata.AnglesGrid grid : viewingIncidenceAnglesGrids) {
                    if (!Float.isNaN(grid.zenith[y][x])) {
                        viewingZeniths[index] = grid.zenith[y][x];
                    }
                    if (!Float.isNaN(grid.azimuth[y][x])) {
                        viewingAzimuths[index] = grid.azimuth[y][x];
                    }
                }
            }
        }
        return new TiePointGrid[]{
                createTiePointGrid("sun_zenith", gridWidth, gridHeight, sunZeniths),
                createTiePointGrid("sun_azimuth", gridWidth, gridHeight, sunAzimuths),
                createTiePointGrid("view_zenith", gridWidth, gridHeight, viewingZeniths),
                createTiePointGrid("view_azimuth", gridWidth, gridHeight, viewingAzimuths)
        };
    }

    private TiePointGrid createTiePointGrid(String name, int gridWidth, int gridHeight, float[] values) {
        final TiePointGrid tiePointGrid = new TiePointGrid(name, gridWidth, gridHeight, 0.0F, 0.0F, 500.0F, 500.0F, values);
        tiePointGrid.setNoDataValue(Double.NaN);
        tiePointGrid.setNoDataValueUsed(true);
        return tiePointGrid;
    }

    private static Map<String, File> createFileMap(String tileId, File imageFile) {
        Map<String, File> tileIdToFileMap = new HashMap<String, File>();
        tileIdToFileMap.put(tileId, imageFile);
        return tileIdToFileMap;
    }

    private void setStartStopTime(Product product, String start, String stop) {
        try {
            product.setStartTime(ProductData.UTC.parse(start, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // {@report "illegal start date"}
        }

        try {
            product.setEndTime(ProductData.UTC.parse(stop, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // {@report "illegal stop date"}
        }
    }

    private BandInfo createBandInfoFromDefaults(int bandIndex, S2WavebandInfo wavebandInfo, String tileId, File imageFile) {
        return new BandInfo(createFileMap(tileId, imageFile),
                            bandIndex,
                            wavebandInfo,
                            L1C_TILE_LAYOUTS[wavebandInfo.resolution.id]);
    }

    private BandInfo createBandInfoFromHeaderInfo(SpectralInformation bandInformation, ResampleData resampleData, Map<String, File> tileFileMap) {
        S2SpatialResolution spatialResolution = S2SpatialResolution.valueOfResolution(bandInformation.resolution);
        return new BandInfo(tileFileMap,
                            bandInformation.bandId,
                            new S2WavebandInfo(bandInformation.bandId,
                                               bandInformation.physicalBand,
                                               spatialResolution, bandInformation.wavelenghtCentral,
                                               bandInformation.wavelenghtMax - bandInformation.wavelenghtMin,
                                               resampleData.reflectanceConversion.solarIrradiances[bandInformation.bandId],
                                               resampleData.quantificationValue,
                                               resampleData.reflectanceConversion.u),
                            L1C_TILE_LAYOUTS[spatialResolution.id]);
    }

    private void setGeoCoding(Product product, Envelope2D envelope) {
        try {
            product.setGeoCoding(new CrsGeoCoding(envelope.getCoordinateReferenceSystem(),
                                                  product.getSceneRasterWidth(),
                                                  product.getSceneRasterHeight(),
                                                  envelope.getMinX(),
                                                  envelope.getMaxY(),
                                                  S2SpatialResolution.R10M.resolution,
                                                  S2SpatialResolution.R10M.resolution,
                                                  0.0, 0.0));
        } catch (FactoryException e) {
            // {@report "illegal CRS"}
        } catch (TransformException e) {
            // {@report "illegal projection"}
        }
    }

    static File getProductDir(File productFile) throws IOException {
        final File resolvedFile = productFile.getCanonicalFile();
        if (!resolvedFile.exists()) {
            throw new FileNotFoundException("File not found: " + productFile);
        }

        if (productFile.getParentFile() == null) {
            return new File(".").getCanonicalFile();
        }

        return productFile.getParentFile();
    }

    void initCacheDir(File productDir) throws IOException {
        cacheDir = new File(new File(SystemUtils.getApplicationDataDir(), "beam-sentinel2-reader/cache"),
                            productDir.getName());
        //noinspection ResultOfMethodCallIgnored
        cacheDir.mkdirs();
        if (!cacheDir.exists() || !cacheDir.isDirectory() || !cacheDir.canWrite()) {
            throw new IOException("Can't access package cache directory");
        }
    }


    private abstract class MultiLevelImageFactory {
        protected final AffineTransform imageToModelTransform;

        protected MultiLevelImageFactory(AffineTransform imageToModelTransform) {
            this.imageToModelTransform = imageToModelTransform;
        }

        public abstract MultiLevelImage createSourceImage(BandInfo bandInfo);
    }

    private class L1cTileMultiLevelImageFactory extends MultiLevelImageFactory {
        private L1cTileMultiLevelImageFactory(AffineTransform imageToModelTransform) {
            super(imageToModelTransform);
        }

        public MultiLevelImage createSourceImage(BandInfo bandInfo) {
            return new DefaultMultiLevelImage(new L1cTileMultiLevelSource(bandInfo, imageToModelTransform));
        }
    }

    private class L1cSceneMultiLevelImageFactory extends MultiLevelImageFactory {

        private final L1cSceneDescription sceneDescription;

        public L1cSceneMultiLevelImageFactory(L1cSceneDescription sceneDescription, AffineTransform imageToModelTransform) {
            super(imageToModelTransform);
            this.sceneDescription = sceneDescription;
        }

        @Override
        public MultiLevelImage createSourceImage(BandInfo bandInfo) {
            return new DefaultMultiLevelImage(new BandL1cSceneMultiLevelSource(sceneDescription, bandInfo, imageToModelTransform));
        }
    }

    /**
     * A MultiLevelSource for single L1C tiles.
     */
    private class L1cTileMultiLevelSource extends AbstractMultiLevelSource {
        final BandInfo bandInfo;

        public L1cTileMultiLevelSource(BandInfo bandInfo, AffineTransform imageToModelTransform) {
            super(new DefaultMultiLevelModel(bandInfo.imageLayout.numResolutions,
                                             imageToModelTransform,
                                             L1C_TILE_LAYOUTS[0].width,
                                             L1C_TILE_LAYOUTS[0].height));
            this.bandInfo = bandInfo;
        }

        @Override
        protected RenderedImage createImage(int level) {
            File imageFile = bandInfo.tileIdToFileMap.values().iterator().next();
            return L1cTileOpImage.create(imageFile,
                                         cacheDir,
                                         null,
                                         bandInfo.imageLayout,
                                         getModel(),
                                         bandInfo.wavebandInfo.resolution,
                                         level);
        }

    }

    /**
     * A MultiLevelSource for a scene made of multiple L1C tiles.
     */
    private abstract class AbstractL1cSceneMultiLevelSource extends AbstractMultiLevelSource {
        protected final L1cSceneDescription sceneDescription;

        AbstractL1cSceneMultiLevelSource(L1cSceneDescription sceneDescription, AffineTransform imageToModelTransform, int numResolutions) {
            super(new DefaultMultiLevelModel(numResolutions,
                                             imageToModelTransform,
                                             sceneDescription.getSceneRectangle().width,
                                             sceneDescription.getSceneRectangle().height));
            this.sceneDescription = sceneDescription;
        }

        @Override
        protected RenderedImage createImage(int level) {
            ArrayList<RenderedImage> tileImages = new ArrayList<RenderedImage>();

            for (String tileId : sceneDescription.getTileIds()) {

                int tileIndex = sceneDescription.getTileIndex(tileId);
                Rectangle tileRectangle = sceneDescription.getTileRectangle(tileIndex);

                PlanarImage opImage = createL1cTileImage(tileId, level);


                    /*
                    opImage = new MoveOriginOpImage(opImage,
                                                    tileRectangle.x >> level,
                                                    tileRectangle.y >> level,
                                                    null);
                                                    */

                // todo - This translation step is actually not required because we can create L1cTileOpImages
                // with minX, minY set as it is required by the MosaicDescriptor and indicated by its API doc.
                // But if we do it like that, we get lots of weird visual artifacts in the resulting mosaic.
                opImage = TranslateDescriptor.create(opImage,
                                                     (float) (tileRectangle.x >> level),
                                                     (float) (tileRectangle.y >> level),
                                                     Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);


                //System.out.printf("opImage added for level %d at (%d,%d)%n", level, opImage.getMinX(), opImage.getMinY());
                tileImages.add(opImage);
            }

            if (tileImages.isEmpty()) {
                // {@report "no tile images for mosaic"}
                return null;
            }

            ImageLayout imageLayout = new ImageLayout();
            imageLayout.setMinX(0);
            imageLayout.setMinY(0);
            imageLayout.setTileWidth(DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileHeight(DEFAULT_JAI_TILE_SIZE);
            imageLayout.setTileGridXOffset(0);
            imageLayout.setTileGridYOffset(0);
            RenderedOp mosaicOp = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                                                          MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                          null, null, new double[][]{{1.0}}, new double[]{FILL_CODE_MOSAIC_BG},
                                                          new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));

            System.out.printf("mosaicOp created for level %d at (%d,%d)%n", level, mosaicOp.getMinX(), mosaicOp.getMinY());

            return mosaicOp;
        }

        protected abstract PlanarImage createL1cTileImage(String tileId, int level);
    }

    /**
     * A MultiLevelSource used by bands for a scene made of multiple L1C tiles.
     */
    private final class BandL1cSceneMultiLevelSource extends AbstractL1cSceneMultiLevelSource {
        private final BandInfo bandInfo;

        public BandL1cSceneMultiLevelSource(L1cSceneDescription sceneDescription, BandInfo bandInfo, AffineTransform imageToModelTransform) {
            super(sceneDescription, imageToModelTransform, bandInfo.imageLayout.numResolutions);
            this.bandInfo = bandInfo;
        }

        @Override
        protected PlanarImage createL1cTileImage(String tileId, int level) {
            File imageFile = bandInfo.tileIdToFileMap.get(tileId);
            PlanarImage planarImage = L1cTileOpImage.create(imageFile,
                                                            cacheDir,
                                                            null, // tileRectangle.getLocation(),
                                                            bandInfo.imageLayout,
                                                            getModel(),
                                                            bandInfo.wavebandInfo.resolution,
                                                            level);
            System.out.printf("%s %s: minX=%d, minY=%d, width=%d, height=%d\n",
                              bandInfo.wavebandInfo.bandName, tileId,
                              planarImage.getMinX(), planarImage.getMinY(),
                              planarImage.getWidth(), planarImage.getHeight());
            return planarImage;
        }
    }

    /**
     * A MultiLevelSource used by bands for a scene made of multiple L1C tiles.
     */
    private final class TiePointGridL1cSceneMultiLevelSource extends AbstractL1cSceneMultiLevelSource {

        private final L1cMetadata metadata;
        private final int tiePointGridIndex;
        private HashMap<String, TiePointGrid[]> tiePointGridsMap;

        public TiePointGridL1cSceneMultiLevelSource(L1cSceneDescription sceneDescription, L1cMetadata metadata, AffineTransform imageToModelTransform, int numResolutions, int tiePointGridIndex) {
            super(sceneDescription, imageToModelTransform, numResolutions);
            this.metadata = metadata;
            this.tiePointGridIndex = tiePointGridIndex;
            tiePointGridsMap = new HashMap<String, TiePointGrid[]>();
        }

        @Override
        protected PlanarImage createL1cTileImage(String tileId, int level) {
            TiePointGrid[] tiePointGrids = tiePointGridsMap.get(tileId);
            if (tiePointGrids == null) {
                final int tileIndex = sceneDescription.getTileIndex(tileId);
                tiePointGrids = createL1cTileTiePointGrids(metadata, tileIndex);
                tiePointGridsMap.put(tileId, tiePointGrids);
            }
            return (PlanarImage) tiePointGrids[tiePointGridIndex].getSourceImage().getImage(level);
        }
    }
}
