package org.esa.s2tbx.s2msi.aerosol.util;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Tonio Fincke
 */
public class AerosolTypeProvider {

    private static final String CLIMATOLOGY_FILE_NAME = "climatology_ratios.nc";
    private static final String DUST_RATIO_BAND_NAME_START = "AOD550_aer_dust_coarse_ratio_mo_time";
    private static final String FINE_TOTAL_RATIO_BAND_NAME_START = "AOD550_aer_fine_total_ratio_mo_time";
    private static final String FINE_LESS_ABS_BAND_NAME_START = "fine_less_abs_type_fraction_mo_time";

    private final GeoCoding geoCoding;
    private final double fraction;
    private final Band startDustRatioBand;
    private final Band endDustRatioBand;
    private final Band startFineTotalRatioBand;
    private final Band endFineTotalRatioBand;
    private final Band startFineAbsLessBand;
    private final Band endFineAbsLessBand;

    public AerosolTypeProvider(int dayOfYear) throws IOException {
        installClimatologyFile();
        final Product climatologiesProduct = getClimatologiesProduct(getAuxdataInstallationPath().toString());

        geoCoding = climatologiesProduct.getSceneGeoCoding();
        final float[] times = getAttributes(climatologiesProduct, "time");
        final TimeData timeData = getTimeData(dayOfYear, times);

        fraction = timeData.fraction;

        startDustRatioBand = climatologiesProduct.getBand(DUST_RATIO_BAND_NAME_START + timeData.startMonth);
        endDustRatioBand = climatologiesProduct.getBand(DUST_RATIO_BAND_NAME_START + timeData.endMonth);
        startFineTotalRatioBand = climatologiesProduct.getBand(FINE_TOTAL_RATIO_BAND_NAME_START + timeData.startMonth);
        endFineTotalRatioBand = climatologiesProduct.getBand(FINE_TOTAL_RATIO_BAND_NAME_START + timeData.endMonth);
        startFineAbsLessBand = climatologiesProduct.getBand(FINE_LESS_ABS_BAND_NAME_START + timeData.startMonth);
        endFineAbsLessBand = climatologiesProduct.getBand(FINE_LESS_ABS_BAND_NAME_START + timeData.endMonth);
    }

    static Product getClimatologiesProduct(String auxDataDir) throws IOException {
        final File climatologiesFile = new File(auxDataDir, CLIMATOLOGY_FILE_NAME);
        final ProductReader netCDFReader = ProductIO.getProductReader("NetCDF");
        return netCDFReader.readProductNodes(climatologiesFile, null);
    }

    private float[] getAttributes(Product product, String attribute) {
        return (float[]) product.getMetadataRoot().getElement("Variable_Attributes").
                getElement(attribute).getElement("Values").getAttribute("data").getDataElems();
    }

    public float getAerosolType(GeoPos geoPos) {
        final PixelPos pixelPos = new PixelPos();
        geoCoding.getPixelPos(geoPos, pixelPos);
        int startX = pixelPos.getX() - 0.5 < 0 ? 359 : (int) (pixelPos.getX() - 0.5);
        int endX = (startX + 1) % 359;
        int startY = (int) (pixelPos.getY() - 0.5);
        int endY = startY + 1;

        final double[] startDustRatios = getPixelCenterFloats(startDustRatioBand, startX, endX, startY, endY);
        final double[] endDustRatios = getPixelCenterFloats(endDustRatioBand, startX, endX, startY, endY);
        final double[] startFineTotalRatios = getPixelCenterFloats(startFineTotalRatioBand, startX, endX, startY, endY);
        final double[] endFineTotalRatios = getPixelCenterFloats(endFineTotalRatioBand, startX, endX, startY, endY);
        final double[] startFineAbsLesses = getPixelCenterFloats(startFineAbsLessBand, startX, endX, startY, endY);
        final double[] endFineAbsLesses = getPixelCenterFloats(endFineAbsLessBand, startX, endX, startY, endY);

        final double[] dustRatios = performTemporalInterpolation(startDustRatios, endDustRatios);
        final double[] fineTotalRatios = performTemporalInterpolation(startFineTotalRatios, endFineTotalRatios);
        final double[] fineAbsLesses = performTemporalInterpolation(startFineAbsLesses, endFineAbsLesses);

        final double dustRatio = performSpatialInterpolation(pixelPos, dustRatios);
        final double fineTotalRatio = performSpatialInterpolation(pixelPos, fineTotalRatios);
        final double fineAbsLess = performSpatialInterpolation(pixelPos, fineAbsLesses);

        final double seaSaltFraction = (1.0 - dustRatio)  * (1.0 - fineTotalRatio);
        if (seaSaltFraction > 0.5) {
            return 1.f;
        }
        final double dustFraction = dustRatio  * (1.0 - fineTotalRatio);
        if (dustFraction > 0.5) {
            return 3.f;
        }
        final double fineModeFractionStronglyAbsorbing = (1.0 - fineAbsLess)  * fineTotalRatio;
        if (fineModeFractionStronglyAbsorbing > 0.5) {
            return 2.f;
        }
        return 0.f;
    }

    static double performSpatialInterpolation(PixelPos pixelPos, double[] values) {
        double d_x = pixelPos.getX() + 0.5 - (int) (pixelPos.getX() + 0.5);
        double d_y = pixelPos.getY() + 0.5 - (int) (pixelPos.getY() + 0.5);

        double interpolated = values[0] * (1. - d_y) * (1. - d_x);
        interpolated += values[1] * (1. - d_y) * d_x;
        interpolated += values[2] * d_y * (1. - d_x);
        interpolated += values[3] * d_y * d_x;

        return interpolated;
    }

    private double[] performTemporalInterpolation(double[] start, double[] end) {
        double[] interpolated = new double[4];
        interpolated[0] = (1. - fraction) * start[0] + fraction * end[0];
        interpolated[1] = (1. - fraction) * start[1] + fraction * end[1];
        interpolated[2] = (1. - fraction) * start[2] + fraction * end[2];
        interpolated[3] = (1. - fraction) * start[3] + fraction * end[3];
        return interpolated;
    }

    private double[] getPixelCenterFloats(Band band, int startX, int endX, int startY, int endY) {
        double[] pixelCenterFloats = new double[4];
        pixelCenterFloats[0] = band.getSampleFloat(startX, startY);
        pixelCenterFloats[1] = band.getSampleFloat(endX, startY);
        pixelCenterFloats[2] = band.getSampleFloat(startX, endY);
        pixelCenterFloats[3] = band.getSampleFloat(endX, endY);
        return pixelCenterFloats;
    }


    static TimeData getTimeData(int dayOfYear, float[] times) {
        int startMonth = 11;
        int endMonth = 0;
        for (int i = 0; i < 12; i++) {
            if (dayOfYear < times[i]) {
                float start = times[startMonth];
                float end = times[endMonth];
                if (start > end) {
                    if (dayOfYear < start) {
                        start -= 365.0;
                    } else {
                        end += 365.0;
                    }
                }
                final TimeData timeData = new TimeData();
                timeData.startMonth = "" + (startMonth + 1);
                timeData.endMonth = "" + (endMonth + 1);
                timeData.fraction = (dayOfYear - start) / (end - start);
                return timeData;
            }
            startMonth++;
            startMonth %= 12;
            endMonth++;
            endMonth %= 12;
        }
        float start = times[startMonth];
        float end = times[endMonth] + 365.f;

        final TimeData timeData = new TimeData();
        timeData.startMonth = "" + (startMonth + 1);
        timeData.endMonth = "" + (endMonth + 1);
        timeData.fraction = (dayOfYear - start) / (end - start);
        return timeData;
    }

    private void installClimatologyFile() throws IOException {
        Path auxdataDirPath = getAuxdataInstallationPath();
        Path sourcePath = ResourceInstaller.findModuleCodeBasePath(getClass()).resolve("auxdata");
        new ResourceInstaller(sourcePath, auxdataDirPath).install(".*" + CLIMATOLOGY_FILE_NAME, ProgressMonitor.NULL);
    }

    // package local for testing purposes
    Path getAuxdataInstallationPath() {
        return SystemUtils.getAuxDataPath().resolve("s2msi/aerosol-retrieval").toAbsolutePath();
    }

    static class TimeData {
        String startMonth;
        String endMonth;
        double fraction;
    }

}
