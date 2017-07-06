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
public class S2IdepixUtils {

    public static final String IDEPIX_INVALID_DESCR_TEXT = "Invalid pixels";
    public static final String IDEPIX_CLOUD_DESCR_TEXT = "Pixels which are either cloud_sure or cloud_ambiguous";
    public static final String IDEPIX_CLOUD_AMBIGUOUS_DESCR_TEXT = "Semi transparent clouds, or clouds where the detection level is uncertain";
    public static final String IDEPIX_CLOUD_SURE_DESCR_TEXT = "Fully opaque clouds with full confidence of their detection";
    public static final String IDEPIX_CLOUD_BUFFER_DESCR_TEXT = "A buffer of n pixels around a cloud. n is a user supplied parameter. Applied to pixels masked as 'cloud'";
    public static final String IDEPIX_CLOUD_SHADOW_DESCR_TEXT = "Pixels is affect by a cloud shadow";
    public static final String IDEPIX_SNOW_ICE_DESCR_TEXT = "Clear snow/ice pixels";
    public static final String IDEPIX_BRIGHT_DESCR_TEXT = "Bright pixels";
    public static final String IDEPIX_WHITE_DESCR_TEXT = "White pixels";
    public static final String IDEPIX_COASTLINE_DESCR_TEXT = "Pixels at a coastline";
    public static final String IDEPIX_LAND_DESCR_TEXT = "Land pixels";

    public static final String IDEPIX_CIRRUS_SURE_DESCR_TEXT = "Cirrus clouds with full confidence of their detection";
    public static final String IDEPIX_CIRRUS_AMBIGUOUS_DESCR_TEXT = "Cirrus clouds, or clouds where the detection level is uncertain";
    public static final String IDEPIX_CLEAR_LAND_DESCR_TEXT = "Clear land pixels";
    public static final String IDEPIX_CLEAR_WATER_DESCR_TEXT = "Clear water pixels";
    public static final String IDEPIX_WATER_DESCR_TEXT = "Water pixels";
    public static final String IDEPIX_BRIGHTWHITE_DESCR_TEXT = "'Brightwhite' pixels";
    public static final String IDEPIX_VEG_RISK_DESCR_TEXT = "Pixels with vegetation risk";

    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("idepix");

    public static final String IDEPIX_CLASSIF_FLAGS = "pixel_classif_flags";

    private S2IdepixUtils() {
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
        for (String bandName : S2IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES) {
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
        flagCoding.addFlag("IDEPIX_INVALID", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_INVALID), IDEPIX_INVALID_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_CLOUD", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_CLOUD), IDEPIX_CLOUD_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_CLOUD_AMBIGUOUS", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_CLOUD_AMBIGUOUS), IDEPIX_CLOUD_AMBIGUOUS_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_CLOUD_SURE", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_CLOUD_SURE), IDEPIX_CLOUD_SURE_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_CLOUD_BUFFER", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_CLOUD_BUFFER), IDEPIX_CLOUD_BUFFER_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_CLOUD_SHADOW", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_CLOUD_SHADOW), IDEPIX_CLOUD_SHADOW_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_SNOW_ICE", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_SNOW_ICE), IDEPIX_SNOW_ICE_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_BRIGHT", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_BRIGHT), IDEPIX_BRIGHT_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_WHITE", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_WHITE), IDEPIX_WHITE_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_COASTLINE", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_COASTLINE), IDEPIX_COASTLINE_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_LAND", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_LAND), IDEPIX_LAND_DESCR_TEXT);

        flagCoding.addFlag("IDEPIX_CIRRUS_SURE", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_CIRRUS_SURE), IDEPIX_CIRRUS_SURE_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_CIRRUS_AMBIGUOUS", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_CIRRUS_AMBIGUOUS), IDEPIX_CIRRUS_AMBIGUOUS_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_CLEAR_LAND", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_CLEAR_LAND), IDEPIX_CLEAR_LAND_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_CLEAR_WATER", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_CLEAR_WATER), IDEPIX_CLEAR_WATER_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_WATER", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_WATER), IDEPIX_WATER_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_BRIGHTWHITE", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_BRIGHTWHITE), IDEPIX_BRIGHTWHITE_DESCR_TEXT);
        flagCoding.addFlag("IDEPIX_VEG_RISK", BitSetter.setFlag(0, S2IdepixConstants.IDEPIX_VEG_RISK), IDEPIX_VEG_RISK_DESCR_TEXT);

        return flagCoding;
    }

    public static int setupIdepixCloudscreeningBitmasks(Product gaCloudProduct) {

        int index = 0;
        int w = gaCloudProduct.getSceneRasterWidth();
        int h = gaCloudProduct.getSceneRasterHeight();
        Mask mask;
        Random r = new Random();

        mask = Mask.BandMathsType.create("IDEPIX_INVALID",
                                         IDEPIX_INVALID_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_INVALID",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_CLOUD",
                                         IDEPIX_CLOUD_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_CLOUD or pixel_classif_flags.IDEPIX_CLOUD_SURE or pixel_classif_flags.IDEPIX_CLOUD_AMBIGUOUS",
                                         new Color(178, 178, 0), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_CLOUD_AMBIGUOUS",
                                         IDEPIX_CLOUD_AMBIGUOUS_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_CLOUD_AMBIGUOUS",
                                         new Color(255, 219, 156), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_CLOUD_SURE",
                                         IDEPIX_CLOUD_SURE_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_CLOUD_SURE",
                                         new Color(224, 224, 30), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_CLOUD_BUFFER",
                                         IDEPIX_CLOUD_BUFFER_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_CLOUD_BUFFER",
                                         Color.red, 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_CLOUD_SHADOW",
                                         IDEPIX_CLOUD_SHADOW_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_CLOUD_SHADOW",
                                         Color.cyan, 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_SNOW_ICE",
                                         IDEPIX_SNOW_ICE_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_SNOW_ICE",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_BRIGHT",
                                         IDEPIX_BRIGHT_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_BRIGHT",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_WHITE",
                                         IDEPIX_WHITE_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_WHITE",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_COASTLINE",
                                         IDEPIX_COASTLINE_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_COASTLINE",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_LAND",
                                         IDEPIX_CLEAR_LAND_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_CLEAR_LAND",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);

        mask = Mask.BandMathsType.create("IDEPIX_CIRRUS_SURE",
                                         IDEPIX_CIRRUS_SURE_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_CIRRUS_SURE",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_CIRRUS_AMBIGUOUS",
                                         IDEPIX_CIRRUS_AMBIGUOUS_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_CIRRUS_AMBIGUOUS",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_CLEAR_LAND",
                                         IDEPIX_CLEAR_LAND_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_CLEAR_LAND",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_CLEAR_WATER",
                                         IDEPIX_CLEAR_WATER_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_CLEAR_WATER",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_WATER",
                                         IDEPIX_WATER_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_WATER",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_BRIGHTWHITE",
                                         IDEPIX_BRIGHTWHITE_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_BRIGHTWHITE",
                                         getRandomColour(r), 0.5f);
        gaCloudProduct.getMaskGroup().add(index++, mask);
        mask = Mask.BandMathsType.create("IDEPIX_VEG_RISK",
                                         IDEPIX_VEG_RISK_DESCR_TEXT, w, h,
                                         "pixel_classif_flags.IDEPIX_VEG_RISK",
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
        if (targetTile.getSampleBit(x, y, S2IdepixConstants.IDEPIX_CLOUD_SURE) ||
                targetTile.getSampleBit(x, y, S2IdepixConstants.IDEPIX_CLOUD_AMBIGUOUS)) {
            targetTile.setSample(x, y, S2IdepixConstants.IDEPIX_CLOUD_BUFFER, false);
        }
    }


    public static void combineFlags(int x, int y, Tile sourceFlagTile, Tile targetTile) {
        int sourceFlags = sourceFlagTile.getSampleInt(x, y);
        int computedFlags = targetTile.getSampleInt(x, y);
        targetTile.setSample(x, y, sourceFlags | computedFlags);
    }

    public static double calcScatteringCos(double sza, double vza, double saa, double vaa) {
        final double sins = (float) Math.sin(sza * MathUtils.DTOR);
        final double sinv = (float) Math.sin(vza * MathUtils.DTOR);
        final double coss = (float) Math.cos(sza * MathUtils.DTOR);
        final double cosv = (float) Math.cos(vza * MathUtils.DTOR);

        // Compute the geometric conditions
        final double cosphi = Math.cos((vaa - saa) * MathUtils.DTOR);

        // cos of scattering angle
        return -coss * cosv - sins * sinv * cosphi;
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
