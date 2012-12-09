package org.esa.beam.dataio.s2;

import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelModel;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.CrsGeoCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.util.io.FileUtils;
import org.esa.beam.util.logging.BeamLogManager;
import org.geotools.geometry.Envelope2D;
import org.jdom.JDOMException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.*;
import javax.media.jai.operator.MosaicDescriptor;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.List;

public class Sentinel2ProductReader extends AbstractProductReader {
    static final String EXE = System.getProperty("openjpeg2.decompressor.path", "opj_decompress");
    static final int DEFAULT_TILE_SIZE = 512;
    static final int NUM_SHORT_BYTES = 2;
    static final float R20M_X_FACTOR = 1.0252F;
    static final float R20M_Y_FACTOR = 1.0253F;
    static final float R60M_X_FACTOR = 1.02445F;
    static final float R60M_Y_FACTOR = 1.0249F;

    private File cacheDir;

    static class BandInfo {
        final Map<String, File> tileIdToFileMap;
        final int bandIndex;
        final S2WavebandInfo wavebandInfo;
        final Jp2ImageLayout imageLayout;

        BandInfo(String tileId, File imageFile, int bandIndex, S2WavebandInfo wavebandInfo, Jp2ImageLayout imageLayout) {
            Map<String, File> tileIdToFileMap = new HashMap<String, File>();
            tileIdToFileMap.put(tileId, imageFile);
            this.tileIdToFileMap = Collections.unmodifiableMap(tileIdToFileMap);
            this.bandIndex = bandIndex;
            this.wavebandInfo = wavebandInfo;
            this.imageLayout = imageLayout;
        }

        BandInfo(Map<String, File> tileIdToFileMap, int bandIndex, S2WavebandInfo wavebandInfo, Jp2ImageLayout imageLayout) {
            this.tileIdToFileMap = Collections.unmodifiableMap(tileIdToFileMap);
            this.bandIndex = bandIndex;
            this.wavebandInfo = wavebandInfo;
            this.imageLayout = imageLayout;
        }
    }


    S2WavebandInfo[] WAVEBAND_INFOS = new S2WavebandInfo[]{
            new S2WavebandInfo(0, "B1", 443, 20, SpatialResolution.R60M),
            new S2WavebandInfo(1, "B2", 490, 65, SpatialResolution.R10M),
            new S2WavebandInfo(2, "B3", 560, 35, SpatialResolution.R10M),
            new S2WavebandInfo(3, "B4", 665, 30, SpatialResolution.R10M),
            new S2WavebandInfo(4, "B5", 705, 15, SpatialResolution.R20M),
            new S2WavebandInfo(5, "B6", 740, 15, SpatialResolution.R20M),
            new S2WavebandInfo(6, "B7", 775, 20, SpatialResolution.R20M),
            new S2WavebandInfo(7, "B8", 842, 115, SpatialResolution.R10M),
            new S2WavebandInfo(8, "B8a", 865, 20, SpatialResolution.R20M),
            new S2WavebandInfo(9, "B9", 940, 20, SpatialResolution.R60M),
            new S2WavebandInfo(10, "B10", 1380, 30, SpatialResolution.R60M),
            new S2WavebandInfo(11, "B11", 1610, 90, SpatialResolution.R20M),
            new S2WavebandInfo(12, "B12", 2190, 180, SpatialResolution.R20M),
    };


    // these numbers should actually been read from the JP2 files,
    // because they are likely to change if prod. spec. changes
    //
    final static Jp2ImageLayout[] IMAGE_LAYOUTS = new Jp2ImageLayout[]{
            new Jp2ImageLayout(10690, 10690, 4096, 4096, 3, 3, 6),
            new Jp2ImageLayout(5480, 5480, 4096, 4096, 2, 2, 6),
            new Jp2ImageLayout(1826, 1826, 1826, 1826, 1, 1, 6),
    };


    Sentinel2ProductReader(Sentinel2ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }
        if (S2MtdFilename.isMetadataFilename(inputFile.getName())) {
            return readProductNodesImpl(inputFile);
        } else if (S2ImgFilename.isImageFilename(inputFile.getName())) {
            return readTileProductNodesImpl(inputFile);
        } else {
            throw new IOException("Unhandled file type.");
        }
    }

    private Product readProductNodesImpl(File metadataFile) throws IOException {
        Header metadataHeader;

        try {
            metadataHeader = Header.parseHeader(metadataFile);
        } catch (JDOMException e) {
            throw new IOException("Failed to parse metadata in " + metadataFile.getName());
        }

        S2MtdFilename mtdFilename = S2MtdFilename.create(metadataFile.getName());
        SceneDescription sceneDescription = SceneDescription.create(metadataHeader);

        File productDir = getProductDir(metadataFile);
        initCacheDir(productDir);

        Header.ProductCharacteristics productCharacteristics = metadataHeader.getProductCharacteristics();
        Header.ResampleData resampleData = metadataHeader.getResampleData();

        String prodType = "S2_MSI_" + productCharacteristics.processingLevel;
        Product product = new Product(FileUtils.getFilenameWithoutExtension(metadataFile).substring("MTD_".length()),
                                      prodType,
                                      sceneDescription.getSceneRectangle().width,
                                      sceneDescription.getSceneRectangle().height);

        product.setPreferredTileSize(DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE);
        product.setNumResolutionsMax(IMAGE_LAYOUTS[0].numResolutions);

        List<Header.Tile> tileList = metadataHeader.getTileList();
        for (Header.SpectralInformation bandInformation : productCharacteristics.bandInformations) {
            int bandIndex = bandInformation.bandId;
            if (bandIndex >= 0 && bandIndex < productCharacteristics.bandInformations.length) {

                HashMap<String, File> tileFileMap = new HashMap<String, File>();
                for (Header.Tile tile : tileList) {
                    String imgFilename = mtdFilename.getImgFilename(bandIndex, tile.id);
                    File file = new File(productDir, imgFilename);
                    if (file.exists()) {
                        tileFileMap.put(tile.id, file);
                    } else {
                        System.out.printf("Warning: missing file " + file);
                    }
                }

                if (!tileFileMap.isEmpty()) {
                    Band band = product.addBand(bandInformation.physicalBand, ProductData.TYPE_UINT16);
                    band.setSpectralBandIndex(bandInformation.bandId);
                    band.setSpectralWavelength((float) bandInformation.wavelenghtCentral);
                    band.setSpectralBandwidth((float) (bandInformation.wavelenghtMax - bandInformation.wavelenghtMin));
                    band.setSolarFlux((float) resampleData.reflectanceConversion.solarIrradiances[bandInformation.bandId]);
                    //band.setScalingFactor(1.0 / rd.quantificationValue);

                    SpatialResolution spatialResolution = SpatialResolution.valueOfResolution(bandInformation.resolution);
                    BandInfo bandInfo = new BandInfo(tileFileMap,
                                                     bandInformation.bandId,
                                                     new S2WavebandInfo(bandInformation.bandId,
                                                                        bandInformation.physicalBand,
                                                                        bandInformation.wavelenghtCentral,
                                                                        bandInformation.wavelenghtMax - bandInformation.wavelenghtMin,
                                                                        spatialResolution),
                                                     IMAGE_LAYOUTS[spatialResolution.id]);

                    band.setSourceImage(new DefaultMultiLevelImage(new MosaicMultiLevelSource(sceneDescription, bandInfo)));
                } else {
                    System.out.printf("Warning: no image files found for band " + bandInformation.physicalBand);
                }
            } else {
                System.out.printf("Warning: illegal band ID detected for band " + bandInformation.physicalBand);
            }
        }

        try {
            product.setStartTime(ProductData.UTC.parse(mtdFilename.start, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // warn
        }

        try {
            product.setEndTime(ProductData.UTC.parse(mtdFilename.stop, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // warn
        }


        Envelope2D sceneEnvelope = sceneDescription.getSceneEnvelope();
        try {
            product.setGeoCoding(new CrsGeoCoding(sceneEnvelope.getCoordinateReferenceSystem(),
                                                  product.getSceneRasterWidth(),
                                                  product.getSceneRasterHeight(),
                                                  sceneEnvelope.getMinX(),
                                                  sceneEnvelope.getMaxY(),
                                                  SpatialResolution.R10M.resolution,
                                                  SpatialResolution.R10M.resolution,
                                                  0.0, 0.0));
        } catch (FactoryException e) {
            // todo - handle e
        } catch (TransformException e) {
            // todo - handle e
        }

        return product;
    }

    private Product readTileProductNodesImpl(File imageFile) throws IOException {

        S2ImgFilename imgFilename = S2ImgFilename.create(imageFile.getName());
        if (imgFilename == null) {
            throw new IOException();
        }

        File productDir = getProductDir(imageFile);
        initCacheDir(productDir);

        Header metadataHeader = null;
        Map<Integer, BandInfo> fileMap = new HashMap<Integer, BandInfo>();
        if (productDir != null) {
            File[] files = productDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return S2ImgFilename.isImageFilename(name);
                }
            });
            if (files != null) {
                for (File file : files) {
                    int bandIndex = imgFilename.getBand(file.getName());
                    if (bandIndex >= 0 && bandIndex < WAVEBAND_INFOS.length) {
                        final S2WavebandInfo wavebandInfo = WAVEBAND_INFOS[bandIndex];
                        BandInfo bandInfo = new BandInfo(imgFilename.tileId,
                                                         file,
                                                         bandIndex,
                                                         wavebandInfo,
                                                         IMAGE_LAYOUTS[wavebandInfo.resolution.id]);
                        fileMap.put(bandIndex, bandInfo);
                    }
                }
            }
            File[] metadataFiles = productDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return S2MtdFilename.isMetadataFilename(name);
                }
            });
            if (metadataFiles != null && metadataFiles.length > 0) {
                File metadataFile = metadataFiles[0];
                try {
                    metadataHeader = Header.parseHeader(metadataFile);
                } catch (JDOMException e) {
                    BeamLogManager.getSystemLogger().warning("Failed to parse metadata file: " + metadataFile);
                }
            } else {
                BeamLogManager.getSystemLogger().warning("No metadata file found");
            }
        }

        ArrayList<Integer> bandIndexes = new ArrayList<Integer>(fileMap.keySet());
        Collections.sort(bandIndexes);

        if (bandIndexes.isEmpty()) {
            throw new IOException("No valid bands found.");
        }

        String prodType = "S2_MSI_" + imgFilename.procLevel;
        Product product = new Product(String.format("%s_%s_%s", prodType, imgFilename.orbitNo, imgFilename.tileId),
                                      prodType,
                                      IMAGE_LAYOUTS[SpatialResolution.R10M.id].width,
                                      IMAGE_LAYOUTS[SpatialResolution.R10M.id].height);

        product.setPreferredTileSize(DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE);
        product.setNumResolutionsMax(IMAGE_LAYOUTS[0].numResolutions);

        try {
            product.setStartTime(ProductData.UTC.parse(imgFilename.start, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // warn
        }

        try {
            product.setEndTime(ProductData.UTC.parse(imgFilename.stop, "yyyyMMddHHmmss"));
        } catch (ParseException e) {
            // warn
        }

        if (metadataHeader != null) {

            //Header.ProductCharacteristics productCharacteristics = metadataHeader.getProductCharacteristics();

            SceneDescription sceneDescription = SceneDescription.create(metadataHeader);
            int tileIndex = sceneDescription.getTileIndex(imgFilename.tileId);
            Envelope2D tileEnvelope = sceneDescription.getTileEnvelope(tileIndex);
            Header.Tile tile = metadataHeader.getTileList().get(tileIndex);

            try {
                product.setGeoCoding(new CrsGeoCoding(tileEnvelope.getCoordinateReferenceSystem(),
                                                      IMAGE_LAYOUTS[SpatialResolution.R10M.id].width,
                                                      IMAGE_LAYOUTS[SpatialResolution.R10M.id].height,
                                                      tile.tileGeometry10M.upperLeftX,
                                                      tile.tileGeometry10M.upperLeftY,
                                                      tile.tileGeometry10M.xDim,
                                                      -tile.tileGeometry10M.yDim,
                                                      0.0, 0.0));
            } catch (FactoryException e) {
                // todo - handle e
            } catch (TransformException e) {
                // todo - handle e
            }
        }

        for (Integer bandIndex : bandIndexes) {
            final BandInfo bandInfo = fileMap.get(bandIndex);
            final Band band = product.addBand(bandInfo.wavebandInfo.bandName, ProductData.TYPE_UINT16);
            band.setSpectralWavelength((float) bandInfo.wavebandInfo.centralWavelength);
            band.setSpectralBandwidth((float) bandInfo.wavebandInfo.bandwidth);
            band.setSpectralBandIndex(bandIndex);
            band.setSourceImage(new DefaultMultiLevelImage(new Jp2MultiLevelSource(bandInfo)));
        }

        return product;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX, int sourceStepY, Band destBand, int destOffsetX, int destOffsetY, int destWidth, int destHeight, ProductData destBuffer, ProgressMonitor pm) throws IOException {
        throw new IllegalStateException("Should not come here");
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
        cacheDir.mkdirs();
        if (!cacheDir.exists() || !cacheDir.isDirectory() || !cacheDir.canWrite()) {
            throw new IOException("Can't access package cache directory");
        }
    }

    private class MosaicMultiLevelSource extends AbstractMultiLevelSource {
        private final SceneDescription sceneDescription;
        private final BandInfo bandInfo;

        public MosaicMultiLevelSource(SceneDescription sceneDescription, BandInfo bandInfo) {
            super(new DefaultMultiLevelModel(IMAGE_LAYOUTS[0].numResolutions,
                                             new AffineTransform(),
                                             sceneDescription.getSceneRectangle().width,
                                             sceneDescription.getSceneRectangle().height));
            this.sceneDescription = sceneDescription;
            this.bandInfo = bandInfo;
        }

        @Override
        protected RenderedImage createImage(int level) {
            ArrayList<RenderedImage> tileImages = new ArrayList<RenderedImage>();

            Set<String> tileIds = bandInfo.tileIdToFileMap.keySet();
            for (String tileId : tileIds) {
                File imageFile = bandInfo.tileIdToFileMap.get(tileId);
                int tileIndex = sceneDescription.getTileIndex(tileId);
                Rectangle tileRectangle = sceneDescription.getTileRectangle(tileIndex);
                try {
                    PlanarImage opImage = Jp2TileOpImage.create(imageFile, cacheDir, bandInfo.imageLayout, getModel(), bandInfo.wavebandInfo.resolution, level);
                    opImage = new MoveOriginOpImage(opImage, tileRectangle.x, tileRectangle.y, null);
                    tileImages.add(opImage);
                } catch (IOException e) {
                    // todo - handle e
                }
            }

            ImageLayout imageLayout = new ImageLayout();
            imageLayout.setTileWidth(DEFAULT_TILE_SIZE);
            imageLayout.setTileHeight(DEFAULT_TILE_SIZE);
            imageLayout.setWidth(sceneDescription.getSceneRectangle().width);
            imageLayout.setHeight(sceneDescription.getSceneRectangle().height);
            RenderedOp mosaicImage = MosaicDescriptor.create(tileImages.toArray(new RenderedImage[tileImages.size()]),
                                                             MosaicDescriptor.MOSAIC_TYPE_OVERLAY,
                                                             null, null, null, new double[]{0.0},
                                                             new RenderingHints(JAI.KEY_IMAGE_LAYOUT, imageLayout));
            return mosaicImage;
        }
    }

    private class Jp2MultiLevelSource extends AbstractMultiLevelSource {
        final BandInfo bandInfo;

        public Jp2MultiLevelSource(BandInfo bandInfo) {
            super(new DefaultMultiLevelModel(bandInfo.imageLayout.numResolutions,
                                             new AffineTransform(),
                                             IMAGE_LAYOUTS[0].width,
                                             IMAGE_LAYOUTS[0].height));
            this.bandInfo = bandInfo;
        }

        @Override
        protected RenderedImage createImage(int level) {
            try {
                return Jp2TileOpImage.create(bandInfo.tileIdToFileMap.values().iterator().next(),
                                             cacheDir,
                                             bandInfo.imageLayout,
                                             getModel(),
                                             bandInfo.wavebandInfo.resolution,
                                             level);
            } catch (IOException e) {
                return null;
            }
        }

    }


}
