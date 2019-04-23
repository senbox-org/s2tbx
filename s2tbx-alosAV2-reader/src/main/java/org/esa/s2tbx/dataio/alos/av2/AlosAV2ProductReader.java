package org.esa.s2tbx.dataio.alos.av2;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.alos.av2.internal.AlosAV2Constants;
import org.esa.s2tbx.dataio.alos.av2.internal.AlosAV2Metadata;
import org.esa.s2tbx.dataio.readers.GeoTiffBasedReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.TiePointGeoCoding;
import org.esa.snap.core.datamodel.TiePointGrid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This product reader is intended for reading ALOS AVNIR-2 files
 * from compressed ESA archive files or from (uncompressed) file system.
 *
 * @author Denisa Stefanescu
 */
public class AlosAV2ProductReader extends GeoTiffBasedReader<AlosAV2Metadata> {

    protected AlosAV2ProductReader(final ProductReaderPlugIn readerPlugIn, final Path colorPaletteFilePath) {
        super(readerPlugIn, colorPaletteFilePath);
    }

    @Override
    protected VirtualDirEx buildProductDirectory(Path inputPath) throws IOException {
        VirtualDirEx productDirectory = super.buildProductDirectory(inputPath);

        boolean copyFilesFromDirectoryOnLocalDisk = false;
        boolean copyFilesFromArchiveOnLocalDisk = true;
        String inputFileName = inputPath.getFileName().toString();

        if (productDirectory.isCompressed()) {
            File file = productDirectory.getFile(inputFileName.substring(0, inputFileName.indexOf(".")) + ".ZIP");
            productDirectory = VirtualDirEx.build(file.toPath(), copyFilesFromDirectoryOnLocalDisk, copyFilesFromArchiveOnLocalDisk);
        } else if (inputFileName.endsWith(AlosAV2Constants.METADATA_FILE_SUFFIX)) {
            String fileNameWithoutExtension = inputFileName.substring(0, inputFileName.indexOf(AlosAV2Constants.METADATA_FILE_SUFFIX));
            if (productDirectory.exists(fileNameWithoutExtension)) {
                File file = productDirectory.getFile(fileNameWithoutExtension);
                productDirectory = VirtualDirEx.build(file.toPath(), copyFilesFromDirectoryOnLocalDisk, copyFilesFromArchiveOnLocalDisk);
            } else {
                File file = productDirectory.getFile(fileNameWithoutExtension + ".ZIP");
                productDirectory = VirtualDirEx.build(file.toPath(), copyFilesFromDirectoryOnLocalDisk, copyFilesFromArchiveOnLocalDisk);
            }
        }
        return productDirectory;
    }

    @Override
    protected String getMetadataExtension() {
        return AlosAV2Constants.METADATA_EXTENSION;
    }

    @Override
    protected String getMetadataProfile() {
        if (metadata != null && !metadata.isEmpty()) {
            return metadata.get(0).getMetadataProfile();
        } else {
            return AlosAV2Constants.VALUE_NOT_AVAILABLE;
        }
    }

    @Override
    protected String getProductGenericName() {
        if (metadata != null && !metadata.isEmpty()) {
            return metadata.get(0).getProductName();
        } else {
            return AlosAV2Constants.VALUE_NOT_AVAILABLE;
        }
    }

    @Override
    protected String getMetadataFileSuffix() {
        return AlosAV2Constants.IMAGE_METADATA_EXTENSION;
    }

    @Override
    protected String[] getBandNames() {
        if (metadata != null && !metadata.isEmpty()) {
            return metadata.get(0).getBandNames();
        } else {
            return new String[0];
        }
    }

    @Override
    protected void addMetadataMasks(AlosAV2Metadata componentMetadata) {
        int noDataValue;
        int saturatedValue;
        if ((noDataValue = componentMetadata.getNoDataValue()) >= 0) {
            this.product.getMaskGroup().add(Mask.BandMathsType.create(AlosAV2Constants.NODATA,
                    AlosAV2Constants.NODATA,
                    this.product.getSceneRasterWidth(),
                    this.product.getSceneRasterHeight(),
                    String.valueOf(noDataValue),
                    componentMetadata.getNoDataColor(),
                    0.5));
        }
        if ((saturatedValue = componentMetadata.getSaturatedPixelValue()) >= 0) {
            this.product.getMaskGroup().add(Mask.BandMathsType.create(AlosAV2Constants.SATURATED,
                    AlosAV2Constants.SATURATED,
                    this.product.getSceneRasterWidth(),
                    this.product.getSceneRasterHeight(),
                    String.valueOf(saturatedValue),
                    componentMetadata.getSaturatedColor(),
                    0.5));
        }
    }

    @Override
    protected void addBands(final AlosAV2Metadata componentMetadata, final int componentIndex) {
        super.addBands(componentMetadata, componentIndex);

        // add the band information from the metadata
        for (Band band : this.product.getBands()) {
            band.setScalingFactor(componentMetadata.getGain(band.getName()));
            band.setScalingOffset(componentMetadata.getBias(band.getName()));
            band.setUnit(componentMetadata.getBandUnits().get(band.getName()));
        }
        if (!AlosAV2Constants.PROCESSING_1B.equals(componentMetadata.getProcessingLevel())) {
            initGeoCoding();
        }
    }

    private void initGeoCoding() {
        final AlosAV2Metadata alosav2metadata = this.metadata.get(0);
        final AlosAV2Metadata.InsertionPoint[] geopositionPoints = alosav2metadata.getGeopositionPoints();
        if (geopositionPoints != null) {
            int numPoints = geopositionPoints.length;
            if (numPoints > 1 && (int) (numPoints / Math.sqrt((double) numPoints)) == numPoints) {
                float stepX = geopositionPoints[1].stepX - geopositionPoints[0].stepX;
                float stepY = geopositionPoints[1].stepY - geopositionPoints[0].stepY;
                float[] latitudes = new float[numPoints];
                float[] longitudes = new float[numPoints];
                for (int i = 0; i < numPoints; i++) {
                    latitudes[i] = geopositionPoints[i].y;
                    longitudes[i] = geopositionPoints[i].x;
                }
                final TiePointGrid latGrid = addTiePointGrid(stepX, stepY, AlosAV2Constants.LAT_DS_NAME, latitudes);
                final TiePointGrid lonGrid = addTiePointGrid(stepX, stepY, AlosAV2Constants.LON_DS_NAME, longitudes);
                final GeoCoding geoCoding = new TiePointGeoCoding(latGrid, lonGrid);
                this.product.setSceneGeoCoding(geoCoding);
            }
        }
    }

    private TiePointGrid addTiePointGrid(final float subSamplingX, final float subSamplingY, final String gridName, final float[] tiePoints) {
        final int gridDim = (int) Math.sqrt(tiePoints.length);
        final TiePointGrid tiePointGrid = createTiePointGrid(gridName, gridDim, gridDim, 0, 0, subSamplingX, subSamplingY, tiePoints);
        this.product.addTiePointGrid(tiePointGrid);
        return tiePointGrid;
    }
}
