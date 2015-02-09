package org.esa.beam.dataio.s2;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import com.bc.ceres.glevel.support.DefaultMultiLevelSource;
import com.jcabi.aspects.Loggable;
import jp2.TileLayout;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.math3.util.Pair;
import org.esa.beam.dataio.Utils;
import org.esa.beam.dataio.s2.filepatterns.S2L1bGranuleDirFilename;
import org.esa.beam.dataio.s2.filepatterns.S2L1bGranuleImageFilename;
import org.esa.beam.dataio.s2.filepatterns.S2L1bProductFilename;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.jai.ImageManager;
import org.esa.beam.util.Guardian;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.util.io.FileUtils;
import org.esa.beam.util.logging.BeamLogManager;
import org.geotools.geometry.Envelope2D;
import org.jdom.JDOMException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.*;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import javax.media.jai.operator.TranslateDescriptor;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.esa.beam.dataio.s2.L1bMetadata.*;
import static org.esa.beam.dataio.s2.S2L1bConfig.*;

// import org.github.jamm.MemoryMeter;

// todo - register reasonable RGB profile(s)
// todo - set a band's validMaskExpr or no-data value (read from GML)
// todo - set band's ImageInfo from min,max,histogram found in header (--> L1cMetadata.quicklookDescriptor)
// todo - viewing incidence tie-point grids contain NaN values - find out how to correctly treat them
// todo - configure BEAM module / SUHET installer so that OpenJPEG "opj_decompress" executable is accessible on all platforms

// todo - better collect problems during product opening and generate problem report (requires reader API change), see {@report "Problem detected..."} code marks

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
public class Sentinel2L1BProductReader extends AbstractProductReader {

    private File cacheDir;
    protected final Logger logger;
    // private MemoryMeter meter;

    static class BandInfo {
        final Map<String, File> tileIdToFileMap;
        final int bandIndex;
        final S2L1bWavebandInfo wavebandInfo;
        final TileLayout imageLayout;
        final String detectorId;

        BandInfo(Map<String, File> tileIdToFileMap, int bandIndex, String detector, S2L1bWavebandInfo wavebandInfo, TileLayout imageLayout) {
            this.tileIdToFileMap = Collections.unmodifiableMap(tileIdToFileMap);
            this.bandIndex = bandIndex;
            this.detectorId = detector == null ? "" : detector;
            this.wavebandInfo = wavebandInfo;
            this.imageLayout = imageLayout;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }


    Sentinel2L1BProductReader(Sentinel2L1BProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        logger = BeamLogManager.getSystemLogger();
        // meter = new MemoryMeter();
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        // Should never not come here, since we have an OpImage that reads data
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        logger.fine("readProductNodeImpl, " + getInput().toString());

        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }

        //todo do we have to read a standalone granule or jp2 file ?

        if (S2L1bProductFilename.isProductFilename(inputFile.getName()))
        {
            return getL1bMosaicProduct(inputFile);
        }
        else {
            throw new IOException("Unhandled file type.");
        }
    }

    private Product getL1bMosaicProduct(File metadataFile) throws IOException {
        L1bMetadata metadataHeader;

        try {
            metadataHeader = parseHeader(metadataFile);
        } catch (JDOMException e) {
            BeamLogManager.getSystemLogger().severe(Utils.getStackTrace(e));
            throw new IOException("Failed to parse metadata in " + metadataFile.getName());
        }

        L1bSceneDescription sceneDescription = L1bSceneDescription.create(metadataHeader, Tile.idGeom.G10M);
        logger.fine("Scene Description: " + sceneDescription);

        File productDir = getProductDir(metadataFile);
        initCacheDir(productDir);

        ProductCharacteristics productCharacteristics = metadataHeader.getProductCharacteristics();

        // todo OPP what if bandInformations are optional ?
        Map<Integer, BandInfo> bandInfoMap = new HashMap<Integer, BandInfo>();
        List<L1bMetadata.Tile> tileList = metadataHeader.getTileList();

        if(productCharacteristics.bandInformations != null) {
            for (SpectralInformation bandInformation : productCharacteristics.bandInformations) {
                int bandIndex = bandInformation.bandId;
                if (bandIndex >= 0 && bandIndex < productCharacteristics.bandInformations.length) {

                    HashMap<String, File> tileFileMap = new HashMap<String, File>();
                    for (Tile tile : tileList) {
                        S2L1bGranuleDirFilename gf = S2L1bGranuleDirFilename.create(tile.id);
                        Guardian.assertNotNull("Product files don't match regular expressions", gf);

                        // todo change how imagefilename is retrieved...

                        S2L1bGranuleImageFilename granuleFileName = gf.getImageFilename(bandInformation.physicalBand);

                        String imgFilename = "GRANULE" + File.separator + tile.id + File.separator + "IMG_DATA" + File.separator + granuleFileName.name;

                        logger.finer("Adding file " + imgFilename + " to band: " + bandInformation.physicalBand);

                        File file = new File(productDir, imgFilename);
                        if (file.exists()) {
                            tileFileMap.put(tile.id, file);
                        } else {
                            logger.warning(String.format("Warning: missing file %s\n", file));
                        }
                    }

                    if (!tileFileMap.isEmpty()) {
                        BandInfo bandInfo = createBandInfoFromHeaderInfo(bandInformation, tileFileMap);
                        bandInfoMap.put(bandIndex, bandInfo);
                    } else {
                        logger.warning(String.format("Warning: no image files found for band %s\n", bandInformation.physicalBand));
                    }
                } else {
                    logger.warning(String.format("Warning: illegal band index detected for band %s\n", bandInformation.physicalBand));
                }
            }
        }
        else
        {
            logger.warning("There are no spectral information here !");
        }

        // todo OPP remove hardcoded indexes

        String[] detectors = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        // Order bands by physicalBand
        Map<String, SpectralInformation> sin = new HashMap<String, SpectralInformation>();
        for (SpectralInformation bandInformation : productCharacteristics.bandInformations)
        {
            sin.put(bandInformation.physicalBand, bandInformation);
        }

        // todo OPP here we must split by detector and band..., detector first
        Map<Pair<String, String>, Map<String, File>> detectorBandInfoMap = new HashMap<Pair<String, String>, Map<String, File>>();
        Map<String, BandInfo> bandInfoByKey = new HashMap<String, BandInfo>();
        if(productCharacteristics.bandInformations != null)
        {
            for (Tile tile : tileList)
            {
                S2L1bGranuleDirFilename gf = S2L1bGranuleDirFilename.create(tile.id);
                Guardian.assertNotNull("Product files don't match regular expressions", gf);

                for (SpectralInformation bandInformation : productCharacteristics.bandInformations) {
                    S2L1bGranuleImageFilename granuleFileName = gf.getImageFilename(bandInformation.physicalBand);
                    String imgFilename = "GRANULE" + File.separator + tile.id + File.separator + "IMG_DATA" + File.separator + granuleFileName.name;

                    logger.finer("Adding file " + imgFilename + " to band: " + bandInformation.physicalBand);

                    File file = new File(productDir, imgFilename);
                    if (file.exists())
                    {
                        Pair<String, String> key = new Pair<String, String>(bandInformation.physicalBand, gf.getDetectorId());
                        Map<String, File> fileMapper = detectorBandInfoMap.getOrDefault(key, new HashMap<String, File>());
                        fileMapper.put(tile.id,file);
                        if(!detectorBandInfoMap.containsKey(key))
                        {
                            detectorBandInfoMap.put(key, fileMapper);
                        }
                    } else {
                        logger.warning(String.format("Warning: missing file %s\n", file));
                    }
                }
            }

            if(!detectorBandInfoMap.isEmpty())
            {
                for(Pair<String, String> key :detectorBandInfoMap.keySet())
                {
                    BandInfo bandInfo = createBandInfoFromHeaderInfo(key.getSecond(), sin.get(key.getFirst()), detectorBandInfoMap.get(key));
                    bandInfoByKey.put(key.getFirst() + key.getSecond(), bandInfo);
                }
            }
        }
        else
        {
            // todo OPP Look for optional info in schema
            logger.warning("There are no spectral information here !");
        }

        Product product = new Product(FileUtils.getFilenameWithoutExtension(metadataFile),
                                      "S2_MSI_" + productCharacteristics.processingLevel,
                                      sceneDescription.getSceneRectangle().width,
                                      sceneDescription.getSceneRectangle().height);

        product.getMetadataRoot().addElement(metadataHeader.getMetadataElement());
        // setStartStopTime(product, mtdFilename.start, mtdFilename.stop);
        setGeoCoding(product, sceneDescription.getSceneEnvelope());
        addDetectorBands(product, bandInfoByKey, new L1bSceneMultiLevelImageFactory(sceneDescription, ImageManager.getImageToModelTransform(product.getGeoCoding())));

        return product;
    }

    private void addBands(Product product, Map<Integer, BandInfo> bandInfoMap, MultiLevelImageFactory mlif) throws IOException {
        product.setPreferredTileSize(DEFAULT_JAI_TILE_SIZE, DEFAULT_JAI_TILE_SIZE);
        product.setNumResolutionsMax(L1B_TILE_LAYOUTS[0].numResolutions);
        product.setAutoGrouping("D01:D02:D03:D04:D05:D06:D07:D08:D09:D10:D11:D12");

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
    }

    // todo OPP Add band-related detector
    private void addDetectorBands(Product product, Map<String, BandInfo> stringBandInfoMap, MultiLevelImageFactory mlif) throws IOException
    {
        product.setPreferredTileSize(DEFAULT_JAI_TILE_SIZE, DEFAULT_JAI_TILE_SIZE);
        product.setNumResolutionsMax(L1B_TILE_LAYOUTS[0].numResolutions);
        product.setAutoGrouping("B1:B2:B3:B4:B5:B6:B7:B8:B8A:B9:B10:B11:B12");

        ArrayList<String> bandIndexes = new ArrayList<String>(stringBandInfoMap.keySet());
        Collections.sort(bandIndexes);

        if (bandIndexes.isEmpty()) {
            throw new IOException("No valid bands found.");
        }

        for (String bandIndex : bandIndexes) {
            BandInfo bandInfo = stringBandInfoMap.get(bandIndex);

            // if((bandIndex.contains("D02")) || (bandIndex.contains("D03")) )
            {
                Band band = addBand(product, bandInfo);
                band.setSourceImage(mlif.createSourceImage(bandInfo));

                // double aResult = meter.measureDeep(band);
            }
        }
    }

    private Band addBand(Product product, BandInfo bandInfo) {
        final Band band = product.addBand(bandInfo.wavebandInfo.bandName, SAMPLE_PRODUCT_DATA_TYPE);

        band.setSpectralBandIndex(bandInfo.bandIndex);
        band.setSpectralWavelength((float) bandInfo.wavebandInfo.wavelength);
        band.setSpectralBandwidth((float) bandInfo.wavebandInfo.bandwidth);


        //todo add masks from GML metadata files (gml branch)
        setValidPixelMask(band, bandInfo.wavebandInfo.bandName);

        // todo - We don't use the scaling factor because we want to stay with 16bit unsigned short samples due to the large
        // amounts of data when saving the images. We provide virtual reflectance bands for this reason. We can use the
        // scaling factor again, once we have product writer parameters, so that users can decide to write data as
        // 16bit samples.
        //
        //band.setScalingFactor(bandInfo.wavebandInfo.scalingFactor);

        return band;
    }

    private void setValidPixelMask(Band band, String bandName) {
        band.setNoDataValue(0);
        band.setValidPixelExpression(String.format("%s.raw > %s",
                                                   bandName, S2L1bConfig.RAW_NO_DATA_THRESHOLD));
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

    private BandInfo createBandInfoFromDefaults(int bandIndex, S2L1bWavebandInfo wavebandInfo, String tileId, File imageFile) {
        // L1cTileLayout aLayout = CodeStreamUtils.getL1bTileLayout(imageFile.toURI().toString(), null);
        return new BandInfo(createFileMap(tileId, imageFile),
                            bandIndex, null,
                            wavebandInfo,
                            // aLayout);
                            //todo test this
                            L1B_TILE_LAYOUTS[wavebandInfo.resolution.id]);

    }

    private BandInfo createBandInfoFromHeaderInfo(SpectralInformation bandInformation, Map<String, File> tileFileMap) {
        S2L1bSpatialResolution spatialResolution = S2L1bSpatialResolution.valueOfResolution(bandInformation.resolution);
        return new BandInfo(tileFileMap,
                            bandInformation.bandId, null,
                            new S2L1bWavebandInfo(bandInformation.bandId,
                                               bandInformation.physicalBand,
                                               spatialResolution, bandInformation.wavelenghtCentral,
                                               Math.abs(bandInformation.wavelenghtMax + bandInformation.wavelenghtMin)),
                            L1B_TILE_LAYOUTS[spatialResolution.id]);
    }

    private BandInfo createBandInfoFromHeaderInfo(String detector, SpectralInformation bandInformation, Map<String, File> tileFileMap) {
        S2L1bSpatialResolution spatialResolution = S2L1bSpatialResolution.valueOfResolution(bandInformation.resolution);
        return new BandInfo(tileFileMap,
                bandInformation.bandId, detector,
                new S2L1bWavebandInfo(bandInformation.bandId,
                        bandInformation.physicalBand + detector,
                        spatialResolution, bandInformation.wavelenghtCentral,
                        Math.abs(bandInformation.wavelenghtMax + bandInformation.wavelenghtMin)),
                L1B_TILE_LAYOUTS[spatialResolution.id]);
    }

    private void setGeoCoding(Product product, Envelope2D envelope) {
        try {
            product.setGeoCoding(new CrsGeoCoding(envelope.getCoordinateReferenceSystem(),
                                                  product.getSceneRasterWidth(),
                                                  product.getSceneRasterHeight(),
                                                  envelope.getMinX(),
                                                  envelope.getMaxY(),
                                                  S2L1bSpatialResolution.R10M.resolution,
                                                  S2L1bSpatialResolution.R10M.resolution,
                                                  0.0, 0.0));
        } catch (FactoryException e) {
            logger.severe("Illegal CRS");
        } catch (TransformException e) {
            logger.severe("Illegal projection");
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

    private class L1bSceneMultiLevelImageFactory extends MultiLevelImageFactory {

        private final L1bSceneDescription sceneDescription;

        public L1bSceneMultiLevelImageFactory(L1bSceneDescription sceneDescription, AffineTransform imageToModelTransform) {
            super(imageToModelTransform);

            BeamLogManager.getSystemLogger().fine("Model factory: " + ToStringBuilder.reflectionToString(imageToModelTransform));

            this.sceneDescription = sceneDescription;
        }

        @Override
        @Loggable
        public MultiLevelImage createSourceImage(BandInfo bandInfo)
        {
            BandL1cSceneMultiLevelSource bandScene = new BandL1cSceneMultiLevelSource(sceneDescription, bandInfo, imageToModelTransform);
            BeamLogManager.getSystemLogger().warning("BandScene: " + bandScene);
            return new DefaultMultiLevelImage(bandScene);
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
                                             L1B_TILE_LAYOUTS[0].width, //todo we must use data from jp2 files to update this
                                             L1B_TILE_LAYOUTS[0].height)); //todo we must use data from jp2 files to update this
            this.bandInfo = bandInfo;
        }

        @Override
        protected RenderedImage createImage(int level) {
            File imageFile = bandInfo.tileIdToFileMap.values().iterator().next();
            return L1bTileOpImage.create(imageFile,
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
        protected final L1bSceneDescription sceneDescription;

        AbstractL1cSceneMultiLevelSource(L1bSceneDescription sceneDescription, AffineTransform imageToModelTransform, int numResolutions) {
            super(new DefaultMultiLevelModel(numResolutions,
                                             imageToModelTransform,
                                             sceneDescription.getSceneRectangle().width,
                                             sceneDescription.getSceneRectangle().height));
            this.sceneDescription = sceneDescription;
        }



        protected abstract PlanarImage createL1bTileImage(String tileId, int level);
    }

    /**
     * A MultiLevelSource used by bands for a scene made of multiple L1C tiles.
     */
    private final class BandL1cSceneMultiLevelSource extends AbstractL1cSceneMultiLevelSource {
        private final BandInfo bandInfo;
        // private MemoryMeter meter;

        public BandL1cSceneMultiLevelSource(L1bSceneDescription sceneDescription, BandInfo bandInfo, AffineTransform imageToModelTransform) {
            super(sceneDescription, imageToModelTransform, bandInfo.imageLayout.numResolutions);
            this.bandInfo = bandInfo;
            // this.meter = new MemoryMeter();
        }

        @Override
        protected PlanarImage createL1bTileImage(String tileId, int level)
        {
            File imageFile = bandInfo.tileIdToFileMap.get(tileId);

            PlanarImage planarImage = L1bTileOpImage.create(imageFile,
                    cacheDir,
                    null, // tileRectangle.getLocation(),
                    bandInfo.imageLayout,
                    getModel(),
                    bandInfo.wavebandInfo.resolution,
                    level);

            logger.fine(String.format("Planar image model: %s", getModel().toString()));

            logger.fine(String.format("Planar image created: %s %s: minX=%d, minY=%d, width=%d, height=%d\n",
                    bandInfo.wavebandInfo.bandName, tileId,
                    planarImage.getMinX(), planarImage.getMinY(),
                    planarImage.getWidth(), planarImage.getHeight()));

            return planarImage;
        }

        @Override
        protected RenderedImage createImage(int level)
        {
            ArrayList<RenderedImage> tileImages = new ArrayList<RenderedImage>();

            long preImage = 0;
            long postImage = 0;
            // long zeroImage = meter.measureDeep(tileImages);

            // todo OPP here comes the extra filter coming from extra field in Bandinfo

            List<String> tiles = sceneDescription.getTileIds().stream().filter(x -> x.contains(bandInfo.detectorId)).collect(Collectors.toList());

            for (String tileId : tiles) {
                int tileIndex = sceneDescription.getTileIndex(tileId);
                Rectangle tileRectangle = sceneDescription.getTileRectangle(tileIndex);

                // todo OPP look at tiles without associated filename, look at tileId list
                PlanarImage opImage = createL1bTileImage(tileId, level);
                // preImage = meter.measureDeep(opImage);

                {
                    double factorX = 1.0 / (Math.pow(2, level) * (this.bandInfo.wavebandInfo.resolution.resolution / S2L1bSpatialResolution.R10M.resolution));
                    double factorY = 1.0 / (Math.pow(2, level) * (this.bandInfo.wavebandInfo.resolution.resolution / S2L1bSpatialResolution.R10M.resolution));

                    opImage = TranslateDescriptor.create(opImage,
                            (float) Math.floor((tileRectangle.x * factorX)),
                            (float) Math.floor((tileRectangle.y * factorY)),
                            Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);

                    logger.warning(String.format("Translate descriptor: %s", ToStringBuilder.reflectionToString(opImage)));

                    // postImage = meter.measureDeep(opImage);
                }

                logger.warning(String.format("opImage added for level %d at (%d,%d) with size (%d,%d)%n", level, opImage.getMinX(), opImage.getMinY(), opImage.getWidth(), opImage.getHeight() ));
                tileImages.add(opImage);
            }

            // long deadShot = meter.measureDeep(tileImages);

            if (tileImages.isEmpty()) {
                logger.warning("No tile images for mosaic");
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

            // todo add crop or extend here to ensure "right" size...
            Rectangle fitrect = new Rectangle(0, 0, (int) sceneDescription.getSceneEnvelope().getWidth() / bandInfo.wavebandInfo.resolution.resolution, (int) sceneDescription.getSceneEnvelope().getHeight() / bandInfo.wavebandInfo.resolution.resolution);
            final Rectangle destBounds = DefaultMultiLevelSource.getLevelImageBounds(fitrect, Math.pow(2.0, level));

            BorderExtender borderExtender = BorderExtender.createInstance(BorderExtender.BORDER_COPY);

            if (mosaicOp.getWidth() < destBounds.width || mosaicOp.getHeight() < destBounds.height) {
                int rightPad = destBounds.width - mosaicOp.getWidth();
                int bottomPad = destBounds.height - mosaicOp.getHeight();
                BeamLogManager.getSystemLogger().warning(String.format("Border: (%d, %d), (%d, %d)", mosaicOp.getWidth(), destBounds.width, mosaicOp.getHeight(), destBounds.height));

                mosaicOp = BorderDescriptor.create(mosaicOp, 0, rightPad, 0, bottomPad, borderExtender, null);
            }

            if (this.bandInfo.wavebandInfo.resolution != S2L1bSpatialResolution.R10M)
            {
                PlanarImage scaled = L1bTileOpImage.createGenericScaledImage(mosaicOp, sceneDescription.getSceneEnvelope(), this.bandInfo.wavebandInfo.resolution, level);

                logger.warning(String.format("mosaicOp created for level %d at (%d,%d) with size (%d, %d)%n", level, scaled.getMinX(), scaled.getMinY(), scaled.getWidth(), scaled.getHeight()));

                return scaled;
            }
            // todo add crop ?

            logger.warning(String.format("mosaicOp created for level %d at (%d,%d) with size (%d, %d)%n", level, mosaicOp.getMinX(), mosaicOp.getMinY(), mosaicOp.getWidth(), mosaicOp.getHeight()));

            return mosaicOp;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
