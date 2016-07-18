package org.esa.s2tbx.s2msi.idepix.util;


import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.FlagCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.util.BitSetter;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.math.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * @author Olaf Danne
 * @version $Revision: $ $Date:  $
 */
public class IdepixUtils {

    public static final String F_INVALID_DESCR_TEXT = "Invalid pixels";
    public static final String F_CLOUD_DESCR_TEXT = "Pixels which are either cloud_sure or cloud_ambiguous";
    public static final String F_CLOUD_AMBIGUOUS_DESCR_TEXT = "Semi transparent clouds, or clouds where the detection level is uncertain";
    public static final String F_CLOUD_SURE_DESCR_TEXT = "Fully opaque clouds with full confidence of their detection";
    public static final String F_CLOUD_BUFFER_DESCR_TEXT = "A buffer of n pixels around a cloud. n is a user supplied parameter. Applied to pixels masked as 'cloud'";
    public static final String F_CLOUD_SHADOW_DESCR_TEXT = "Pixels is affect by a cloud shadow";
    public static final String F_CIRRUS_AMBIGUOUS_DESCR_TEXT = "Cirrus clouds, or clouds where the detection level is uncertain";
    public static final String F_CIRRUS_SURE_DESCR_TEXT = "Cirrus clouds with full confidence of their detection";
    public static final String F_COASTLINE_DESCR_TEXT = "Pixels at a coastline";
    public static final String F_CLEAR_SNOW_DESCR_TEXT = "Clear snow/ice pixels";
    public static final String F_CLEAR_LAND_DESCR_TEXT = "Clear land pixels";
    public static final String F_CLEAR_WATER_DESCR_TEXT = "Clear water pixels";
    public static final String F_LAND_DESCR_TEXT = "Land pixels";
    public static final String F_WATER_DESCR_TEXT = "Water pixels";
    public static final String F_BRIGHT_DESCR_TEXT = "Bright pixels";
    public static final String F_WHITE_DESCR_TEXT = "White pixels";
    public static final String F_BRIGHTWHITE_DESCR_TEXT = "'Brightwhite' pixels";
    public static final String F_HIGH_DESCR_TEXT = "High pixels";
    public static final String F_VEG_RISK_DESCR_TEXT = "Pixels with vegetation risk";
    public static final String F_SEAICE_DESCR_TEXT = "Sea ice pixels";

    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("idepix");

    public static final String IDEPIX_CLASSIF_FLAGS = "pixel_classif_flags";

    private IdepixUtils() {
    }


    public static Product cloneProduct(Product sourceProduct, boolean copySourceBands) {
        return cloneProduct(sourceProduct, sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight(), copySourceBands);
    }

    public static boolean isIdepixSpectralBand(Band b) {
        return b.getName().startsWith("B");
    }

    public static boolean validateInputProduct(Product inputProduct, AlgorithmSelector algorithm) {
        return isInputValid(inputProduct) && isInputConsistent(inputProduct, algorithm);
    }


    public static boolean isInputValid(Product inputProduct) {
        if (!isValidSentinel2(inputProduct)) {
            logErrorMessage("Input sensor must be Sentinel-2 MSI!");
        }
        return true;
    }

    public static boolean isValidSentinel2(Product sourceProduct) {
        for (String bandName : IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES) {
            if (!sourceProduct.containsBand(bandName)) {
                return false;
            }
        }
        return true;
    }

    public static void logErrorMessage(String msg) {
        if (System.getProperty("gpfMode") != null && "GUI".equals(System.getProperty("gpfMode"))) {
            JOptionPane.showOptionDialog(null, msg, "IDEPIX - Error Message", JOptionPane.DEFAULT_OPTION,
                                         JOptionPane.ERROR_MESSAGE, null, null, null);
        } else {
            info(msg);
        }
    }

    public static void info(final String msg) {
        logger.info(msg);
        System.out.println(msg);
    }


    public static float spectralSlope(float ch1, float ch2, float wl1, float wl2) {
        return (ch2 - ch1) / (wl2 - wl1);
    }


    public static boolean areAllReflectancesValid(float[] reflectance) {
        for (float aReflectance : reflectance) {
            if (Float.isNaN(aReflectance) || aReflectance <= 0.0f) {
                return false;
            }
        }
        return true;
    }

    public static void setNewBandProperties(Band band, String description, String unit, double noDataValue,
                                            boolean useNoDataValue) {
        band.setDescription(description);
        band.setUnit(unit);
        band.setNoDataValue(noDataValue);
        band.setNoDataValueUsed(useNoDataValue);
    }

    public static FlagCoding createIdepixFlagCoding(String flagIdentifier) {
        FlagCoding flagCoding = new FlagCoding(flagIdentifier);
        flagCoding.addFlag("F_INVALID", BitSetter.setFlag(0, IdepixConstants.F_INVALID), F_INVALID_DESCR_TEXT);
        flagCoding.addFlag("F_CLOUD", BitSetter.setFlag(0, IdepixConstants.F_CLOUD), F_CLOUD_DESCR_TEXT);
        flagCoding.addFlag("F_CLOUD_AMBIGUOUS", BitSetter.setFlag(0, IdepixConstants.F_CLOUD_AMBIGUOUS), F_CLOUD_AMBIGUOUS_DESCR_TEXT);
        flagCoding.addFlag("F_CLOUD_SURE", BitSetter.setFlag(0, IdepixConstants.F_CLOUD_SURE), F_CLOUD_SURE_DESCR_TEXT);
        flagCoding.addFlag("F_CLOUD_BUFFER", BitSetter.setFlag(0, IdepixConstants.F_CLOUD_BUFFER), F_CLOUD_BUFFER_DESCR_TEXT);
        flagCoding.addFlag("F_CLOUD_SHADOW", BitSetter.setFlag(0, IdepixConstants.F_CLOUD_SHADOW), F_CLOUD_SHADOW_DESCR_TEXT);
        flagCoding.addFlag("F_CIRRUS_AMBIGUOUS", BitSetter.setFlag(0, IdepixConstants.F_CIRRUS_AMBIGUOUS), F_CIRRUS_AMBIGUOUS_DESCR_TEXT);
        flagCoding.addFlag("F_CIRRUS_SURE", BitSetter.setFlag(0, IdepixConstants.F_CIRRUS_SURE), F_CIRRUS_SURE_DESCR_TEXT);
        flagCoding.addFlag("F_COASTLINE", BitSetter.setFlag(0, IdepixConstants.F_COASTLINE), F_COASTLINE_DESCR_TEXT);
        flagCoding.addFlag("F_CLEAR_SNOW", BitSetter.setFlag(0, IdepixConstants.F_CLEAR_SNOW), F_CLEAR_SNOW_DESCR_TEXT);
        flagCoding.addFlag("F_CLEAR_LAND", BitSetter.setFlag(0, IdepixConstants.F_CLEAR_LAND), F_CLEAR_LAND_DESCR_TEXT);
        flagCoding.addFlag("F_CLEAR_WATER", BitSetter.setFlag(0, IdepixConstants.F_CLEAR_WATER), F_CLEAR_WATER_DESCR_TEXT);
        flagCoding.addFlag("F_LAND", BitSetter.setFlag(0, IdepixConstants.F_LAND), F_LAND_DESCR_TEXT);
        flagCoding.addFlag("F_WATER", BitSetter.setFlag(0, IdepixConstants.F_WATER), F_WATER_DESCR_TEXT);
        flagCoding.addFlag("F_BRIGHT", BitSetter.setFlag(0, IdepixConstants.F_BRIGHT), F_BRIGHT_DESCR_TEXT);
        flagCoding.addFlag("F_WHITE", BitSetter.setFlag(0, IdepixConstants.F_WHITE), F_WHITE_DESCR_TEXT);
        flagCoding.addFlag("F_BRIGHTWHITE", BitSetter.setFlag(0, IdepixConstants.F_BRIGHTWHITE), F_BRIGHTWHITE_DESCR_TEXT);
        flagCoding.addFlag("F_HIGH", BitSetter.setFlag(0, IdepixConstants.F_HIGH), F_HIGH_DESCR_TEXT);
        flagCoding.addFlag("F_VEG_RISK", BitSetter.setFlag(0, IdepixConstants.F_VEG_RISK), F_VEG_RISK_DESCR_TEXT);
        flagCoding.addFlag("F_SEAICE", BitSetter.setFlag(0, IdepixConstants.F_SEAICE), F_SEAICE_DESCR_TEXT);

        return flagCoding;
    }

    public static int setupIdepixCloudscreeningBitmasks(Product gaCloudProduct) {

        int index = 0;
        int w = gaCloudProduct.getSceneRasterWidth();
        int h = gaCloudProduct.getSceneRasterHeight();
        Mask mask;
        Random r = new Random();

        mask = Mask.BandMathsType.create("lc_invalid",
                                         F_INVALID_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_INVALID",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_cloud",
                                         F_CLOUD_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_CLOUD or pixel_classif_flags.F_CLOUD_SURE or pixel_classif_flags.F_CLOUD_AMBIGUOUS",
                                         new Color(178, 178, 0), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_cloud_ambiguous",
                                         F_CLOUD_AMBIGUOUS_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_CLOUD_AMBIGUOUS",
                                         new Color(255, 219, 156), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_cloud_sure",
                                         F_CLOUD_SURE_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_CLOUD_SURE",
                                         new Color(224, 224, 30), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_cloud_buffer",
                                         F_CLOUD_BUFFER_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_CLOUD_BUFFER",
                                         Color.red, 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_cloud_shadow",
                                         F_CLOUD_SHADOW_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_CLOUD_SHADOW",
                                         Color.cyan, 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_cirrus_sure",
                                         F_CIRRUS_SURE_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_CIRRUS_SURE",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_cirrus_ambiguous",
                                         F_CIRRUS_AMBIGUOUS_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_CIRRUS_AMBIGUOUS",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_coastline",
                                         F_COASTLINE_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_COASTLINE",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_clear_snow",
                                         F_CLEAR_SNOW_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_CLEAR_SNOW",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_clear_land",
                                         F_CLEAR_LAND_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_CLEAR_LAND",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_clear_water",
                                         F_CLEAR_WATER_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_CLEAR_WATER",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_land",
                                         F_LAND_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_LAND",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_water",
                                         F_WATER_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_WATER",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_bright",
                                         F_BRIGHT_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_BRIGHT",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_white",
                                         F_WHITE_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_WHITE",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_brightwhite",
                                         F_BRIGHTWHITE_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_BRIGHTWHITE",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_high",
                                         F_HIGH_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_HIGH",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("lc_veg_risk",
                                         F_VEG_RISK_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.F_VEG_RISK",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);

        return index;
    }


    public static double convertGeophysicalToMathematicalAngle(double inAngle) {
        if (0.0 <= inAngle && inAngle < 90.0) {
            return (90.0 - inAngle);
        } else if (90.0 <= inAngle && inAngle < 360.0) {
            return (90.0 - inAngle + 360.0);
        } else {
            // invalid
            return Double.NaN;
        }
    }

    public static boolean isNoReflectanceValid(float[] reflectance) {
        for (float aReflectance : reflectance) {
            if (!Float.isNaN(aReflectance) && aReflectance > 0.0f) {
                return false;
            }
        }
        return true;
    }

    public static void consolidateCloudAndBuffer(Tile targetTile, int x, int y) {
        if (targetTile.getSampleBit(x, y, IdepixConstants.F_CLOUD)) {
            targetTile.setSample(x, y, IdepixConstants.F_CLOUD_BUFFER, false);
        }
    }


    public static void combineFlags(int x, int y, Tile sourceFlagTile, Tile targetTile) {
        int sourceFlags = sourceFlagTile.getSampleInt(x, y);
        int computedFlags = targetTile.getSampleInt(x, y);
        targetTile.setSample(x, y, sourceFlags | computedFlags);
    }

    public static double calcScatteringAngle(double sza, double vza, double saa, double vaa) {
        final double sins = (float) Math.sin(sza * MathUtils.DTOR);
        final double sinv = (float) Math.sin(vza * MathUtils.DTOR);
        final double coss = (float) Math.cos(sza * MathUtils.DTOR);
        final double cosv = (float) Math.cos(vza * MathUtils.DTOR);

        // delta azimuth in degree
        final double deltaAzimuth = (float) MathUtils.RTOD * Math.acos(Math.cos(MathUtils.DTOR * (vaa - saa)));

        // Compute the geometric conditions
        final double cosphi = Math.cos(deltaAzimuth * MathUtils.DTOR);

        // scattering angle in degree
        return MathUtils.RTOD * Math.acos(-coss * cosv - sins * sinv * cosphi);
    }


    private static Color getRandomColour(Random random) {
        int rColor = random.nextInt(256);
        int gColor = random.nextInt(256);
        int bColor = random.nextInt(256);
        return new Color(rColor, gColor, bColor);
    }

    private static Product cloneProduct(Product sourceProduct, int width, int height, boolean copySourceBands) {
        Product clonedProduct = new Product(sourceProduct.getName(),
                                            sourceProduct.getProductType(),
                                            width,
                                            height);

        ProductUtils.copyMetadata(sourceProduct, clonedProduct);
        ProductUtils.copyGeoCoding(sourceProduct, clonedProduct);
        ProductUtils.copyFlagCodings(sourceProduct, clonedProduct);
        ProductUtils.copyFlagBands(sourceProduct, clonedProduct, true);
        ProductUtils.copyMasks(sourceProduct, clonedProduct);
        clonedProduct.setStartTime(sourceProduct.getStartTime());
        clonedProduct.setEndTime(sourceProduct.getEndTime());

        if (copySourceBands) {
            // copy all bands from source product
            for (Band b : sourceProduct.getBands()) {
                if (!clonedProduct.containsBand(b.getName())) {
                    ProductUtils.copyBand(b.getName(), sourceProduct, clonedProduct, true);
                    if (isIdepixSpectralBand(b)) {
                        ProductUtils.copyRasterDataNodeProperties(b, clonedProduct.getBand(b.getName()));
                    }
                }
            }

            for (int i = 0; i < sourceProduct.getNumTiePointGrids(); i++) {
                TiePointGrid srcTPG = sourceProduct.getTiePointGridAt(i);
                if (!clonedProduct.containsTiePointGrid(srcTPG.getName())) {
                    clonedProduct.addTiePointGrid(srcTPG.cloneTiePointGrid());
                }
            }
        }

        return clonedProduct;
    }

    private static boolean isInputConsistent(Product sourceProduct, AlgorithmSelector algorithm) {
        if (AlgorithmSelector.MSI == algorithm) {
            return (isValidSentinel2(sourceProduct));
        }  else {
            throw new OperatorException("Algorithm " + algorithm.toString() + "not supported.");
        }
    }

}
