package org.esa.s2tbx.reflectance2radiance;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;

import java.awt.*;
import java.util.Map;

/**
 * Created by dmihailescu on 11/08/2016.
 */

@OperatorMetadata(
        alias = "ReflectanceToRadianceOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "The 'Reflectance To Radiance Processor' operator retrieves the radiance from reflectance using Sentinel-2 products",
        authors = "Dragos Mihailescu",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class ReflectanceToRadianceOp  extends Operator {


    @Override
    public void initialize() throws OperatorException {

    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle rectangle, ProgressMonitor pm) throws OperatorException {
        pm.beginTask("Computing Reflectance to Radiance", rectangle.height);
        try {
            //Tile redTile = getSourceTile(getSourceProduct().getBand(redSourceBand), rectangle);
            //Tile nirTile = getSourceTile(getSourceProduct().getBand(nirSourceBand), rectangle);

            //Tile ndvi = targetTiles.get(targetProduct.getBand(NDVI_BAND_NAME));
            //Tile ndviFlags = targetTiles.get(targetProduct.getBand(NDVI_FLAGS_BAND_NAME));

            float ndviValue;
            int ndviFlagsValue;

            for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
                for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                    //final float nir = nirFactor * nirTile.getSampleFloat(x, y);
                    //final float red = redFactor * redTile.getSampleFloat(x, y);

                    //ndviValue = (nir - red) / (nir + red);

                    /*ndviFlagsValue = 0;
                    if (Float.isNaN(ndviValue) || Float.isInfinite(ndviValue)) {
                        ndviFlagsValue |= NDVI_ARITHMETIC_FLAG_VALUE;
                        ndviValue = 0.0f;
                    }
                    if (ndviValue < 0.0f) {
                        ndviFlagsValue |= NDVI_LOW_FLAG_VALUE;
                    }
                    if (ndviValue > 1.0f) {
                        ndviFlagsValue |= NDVI_HIGH_FLAG_VALUE;
                    }
                    ndvi.setSample(x, y, ndviValue);
                    ndviFlags.setSample(x, y, ndviFlagsValue);*/
                }
                checkForCancellation();
                pm.worked(1);
            }
        } finally {
            pm.done();
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ReflectanceToRadianceOp.class);
        }

    }


    public enum L1CInput {
        B1(0, "B1"),
        B2(1, "B2"),
        B3(2, "B3"),
        B4(3, "B4"),
        B5(4, "B5"),
        B6(5, "B6"),
        B7(6, "B7"),
        B8(7, "B8"),
        B9(8, "B9"),
        B10(9, "B10"),
        B11(10, "B11"),
        B12(11, "B12"),
        B8A(12, "B8A"),
        SUN_ZENITH(13, "sun_zenith"),
        SUN_AZIMUTH(14, "sun_azimuth");

        private final int index;
        private final String bandName;

        L1CInput(int index, String bandName) {
            this.index = index;
            this.bandName = bandName;
        }

        public int getIndex() {
            return this.index;
        }

        public String getBandName() {
            return this.bandName;
        }
    }

    public enum S2BandConstant {
        B1("B1", "B01", 0, 414, 472, 443),
        B2("B2", "B02", 1, 425, 555, 490),
        B3("B3", "B03", 2, 510, 610, 560),
        B4("B4", "B04", 3, 617, 707, 665),
        B5("B5", "B05", 4, 625, 722, 705),
        B6("B6", "B06", 5, 720, 760, 740),
        B7("B7", "B07", 6, 741, 812, 783),
        B8("B8", "B08", 7, 752, 927, 842),
        B8A("B8A", "B8A", 8, 823, 902, 865),
        B9("B9", "B09", 9, 903, 982, 945),
        B10("B10", "B10", 10, 1338, 1413, 1375),
        B11("B11", "B11", 11, 1532, 1704, 1610),
        B12("B12", "B12", 12, 2035, 2311, 2190);

        private String physicalName;
        private String filenameBandId;
        private int bandIndex;
        private double wavelengthMin;
        private double wavelengthMax;
        private double wavelengthCentral;

        S2BandConstant(String physicalName,
                       String filenameBandId,
                       int bandIndex,
                       double wavelengthMin,
                       double wavelengthMax,
                       double wavelengthCentral ) {
            this.physicalName = physicalName;
            this.filenameBandId = filenameBandId;
            this.bandIndex = bandIndex;
            this.wavelengthMin = wavelengthMin;
            this.wavelengthMax = wavelengthMax;
            this.wavelengthCentral = wavelengthCentral;
        }

        public String getPhysicalName() {
            return physicalName;
        }

        public String getFilenameBandId() {
            return filenameBandId;
        }

        public int getBandIndex() {
            return bandIndex;
        }

        public double getWavelengthMin() {
            return wavelengthMin;
        }

        public double getWavelengthMax() {
            return wavelengthMax;
        }

        public double getBandwidth() { return wavelengthMax - wavelengthMin; }

        public double getWavelengthCentral() {
            return wavelengthCentral;
        }
    }

}
