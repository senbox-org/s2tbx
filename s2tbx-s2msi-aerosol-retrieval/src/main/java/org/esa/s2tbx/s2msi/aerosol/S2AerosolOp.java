package org.esa.s2tbx.s2msi.aerosol;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.s2msi.aerosol.lut.S2LutAccessor;
import org.esa.s2tbx.s2msi.aerosol.lut.S2LutUtils;
import org.esa.s2tbx.s2msi.aerosol.math.BrentFitFunction;
import org.esa.s2tbx.s2msi.aerosol.util.AerosolUtils;
import org.esa.s2tbx.s2msi.aerosol.util.PixelGeometry;
import org.esa.s2tbx.s2msi.idepix.util.S2IdepixConstants;
import org.esa.snap.core.datamodel.*;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.Guardian;
import org.esa.snap.core.util.math.LookupTable;

import javax.media.jai.BorderExtender;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Aerosol retrieval operator from S2 MSI following USwansea algorithm as used in GlobAlbedo project.
 *
 * @author olafd
 */
@OperatorMetadata(alias = "AerosolRetrieval.S2.Aerosol",
        description = "Aerosol retrieval operator from S2 MSI following USwansea algorithm as used in GlobAlbedo project.",
        authors = "Olaf Danne, Marco Zuehlke, Grit Kirches, Andreas Heckel",
        internal = true,
        version = "1.0",
        copyright = "(C) 2010, 2016 by University Swansea and Brockmann Consult")
public class S2AerosolOp extends Operator {

    @SourceProduct
    private Product sourceProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(description = "Full path to S2 Lookup Table.",
            label = "Path to S2 Lookup Table")
    private String pathToLut;

    // todo: define what we need from this
    private String surfaceSpecName = "surface_reflectance_spec.asc";

    //    @Parameter(defaultValue = "2")
//    private int vegSpecId;
    private int vegSpecId = 2;

    //    @Parameter(defaultValue = "1")
//    private int soilSpecId;
    private int soilSpecId = 1;

    @Parameter(defaultValue = "20")
    private int scale;

    private String productName;
    private String productType;

    private int srcRasterWidth;
    private int srcRasterHeight;
    private int tarRasterWidth;
    private int tarRasterHeight;

    private int nSpecWvl;
    private float[][] specWvl;
    private double[] soilSurfSpec;
    private double[] vegSurfSpec;
    private double[] specWeights;

    private LookupTable s2Lut;
    private double[] aotGrid;

    private BorderExtender borderExt;
    private Rectangle pixelWindow;

    private Band validBand;
    private Band aotBand;
    private Band aotErrorBand;
    private Band latBand;
    private Band lonBand;

    private String validName;
    private String[] auxRasterDataNodeNames;

    private double julianDay;
    private double distanceCorrection;

//    private static int _xdebug = 20;
//    private static int _ydebug = 70;

    @Override
    public void initialize() throws OperatorException {
        productName = sourceProduct.getName() + "_AOT";
        productType = sourceProduct.getProductType() + "_AOT";

        julianDay = sourceProduct.getStartTime().getMJD();
        final int dayOfYear = sourceProduct.getStartTime().getAsCalendar().get(Calendar.DAY_OF_YEAR);
        distanceCorrection = S2LutUtils.getDistanceCorr(dayOfYear);

        initRasterDimensions(sourceProduct, scale);

        final String validExpression = InstrumentConsts.VALID_RETRIEVAL_EXPRESSION;
        validBand = AerosolUtils.createBooleanExpressionBand(validExpression, sourceProduct);
        validName = validBand.getName();

        auxRasterDataNodeNames = new String[]{
                InstrumentConsts.OZONE_NAME,
                InstrumentConsts.SURFACE_PRESSURE_NAME,
                InstrumentConsts.ELEVATION_NAME,
                validName
        };

        specWeights = AerosolUtils.normalize(InstrumentConsts.FIT_WEIGHTS);
        specWvl = getSpectralWvl(InstrumentConsts.REFLEC_NAMES);
        nSpecWvl = specWvl[0].length;

        readSurfaceSpectra(surfaceSpecName);

        // in the source product we have:
        // - B1,...,B12
        // - sun_zenith, sun_azimuth, view_zenith_mean, view_azimuth_mean
        // - pixel_classif_flag
        // - elevation
        // - surfPressEstimate

        try {
            createS2LookupTable();
        } catch (IOException e) {
            throw new OperatorException("Failed to read LUTs. " + e.getMessage(), e);
        }

        borderExt = BorderExtender.createInstance(BorderExtender.BORDER_COPY);
        pixelWindow = new Rectangle(0, 0, scale, scale);

        createTargetProduct();
    }

    @Override
    public void dispose() {
        targetProduct.setSceneGeoCoding(new PixelGeoCoding(latBand, lonBand, null, 2));
        super.dispose();
    }

    @Override
    public void computeTileStack(Map<Band, Tile> targetTiles, Rectangle targetRectangle, ProgressMonitor pm) throws OperatorException {

        Rectangle srcRec = getSourceRectangle(targetRectangle, pixelWindow);

        if (!containsTileValidData(srcRec)) {
            setInvalidTargetSamples(targetTiles);
            return;
        }

        Map<String, Tile> sourceTiles = new HashMap<>();
        sourceTiles.putAll(getSourceTiles(InstrumentConsts.REFLEC_NAMES, srcRec, borderExt));
        sourceTiles.putAll(getSourceTiles(InstrumentConsts.GEOM_NAMES, srcRec, borderExt));
        sourceTiles.putAll(getSourceTiles(auxRasterDataNodeNames, srcRec, borderExt));

        for (int y = targetRectangle.y; y < targetRectangle.y + targetRectangle.height; y++) {
            checkForCancellation();
            for (int x = targetRectangle.x; x < targetRectangle.x + targetRectangle.width; x++) {
                processSuperPixel(sourceTiles, x, y, targetTiles);
            }
            pm.worked(1);
        }
        pm.done();
    }

    private void processSuperPixel(Map<String, Tile> sourceTiles, int x, int y, Map<Band, Tile> targetTiles) {
        // read pixel data and init brent fit
        BrentFitFunction brentFitFunction = null;
        InputPixelData[] inPixField = readDarkestNPixels(sourceTiles, x, y, pixelWindow);
        if (inPixField != null) {
            brentFitFunction = new BrentFitFunction(BrentFitFunction.SPECTRAL_MODEL,
                                                    inPixField,
                                                    s2Lut,
                                                    julianDay,
                                                    distanceCorrection,
                                                    aotGrid,
                                                    specWeights,
                                                    soilSurfSpec,
                                                    vegSurfSpec);
        }
        retrieveAndSetTarget(inPixField, brentFitFunction, targetTiles, x, y);
    }

    private void retrieveAndSetTarget(InputPixelData[] inPixField,
                                      BrentFitFunction brentFitFunction,
                                      Map<Band, Tile> targetTiles,
                                      int x, int y) {
        // run retrieval and set target samples
        if (inPixField != null) {
            RetrievalResults result = executeRetrieval(brentFitFunction);
            if (!result.isRetrievalFailed()) {
                setTargetSamples(targetTiles, x, y, result);
            } else {
                setInvalidTargetSamples(targetTiles, x, y);
            }
        } else {
            setInvalidTargetSamples(targetTiles, x, y);
        }
    }

    private RetrievalResults executeRetrieval(BrentFitFunction brentFitFunction) {
        final double maxAOT = brentFitFunction.getMaxAOT();
        final PointRetrieval pR = new PointRetrieval(brentFitFunction);
        return pR.runRetrieval(maxAOT);
    }

    private InputPixelData createInPixelData(double[] tileValues) {
        PixelGeometry geomNadir;
        PixelGeometry geomFward;
        double[][] toaRefl = new double[2][nSpecWvl];
        int skip = 0;
        geomNadir = new PixelGeometry(tileValues[0], tileValues[1], tileValues[2], tileValues[3]);
        geomFward = geomNadir;
        skip += 4;
        for (int i = 0; i < nSpecWvl; i++) {
            toaRefl[0][i] = tileValues[skip + i];
            toaRefl[1][i] = tileValues[skip + i];
        }
        skip += nSpecWvl;
        double ozone = tileValues[skip++];
        double surfP = Math.min(tileValues[skip++], 101325.0);
        double elevation = tileValues[skip];
        //todo derive water vapour from LUT
        double wvCol = 2500.0;
        return new InputPixelData(geomNadir, geomFward, elevation, ozone, surfP, wvCol, specWvl[0], toaRefl[0], toaRefl[1]);
    }

    private void createTargetProduct() {
        targetProduct = new Product(productName, productType, tarRasterWidth, tarRasterHeight);
        targetProduct.setPreferredTileSize(new Dimension(610 / scale, 610 / scale));
        createTargetProductBands();

//        targetProduct.setSceneGeoCoding(new PixelGeoCoding(latBand, lonBand, null, 2));
        setTargetProduct(targetProduct);

    }

    private void createTargetProductBands() {
        aotBand = AerosolUtils.createTargetBand(AotConsts.aot, tarRasterWidth, tarRasterHeight);
        targetProduct.addBand(aotBand);

        aotErrorBand = AerosolUtils.createTargetBand(AotConsts.aotErr, tarRasterWidth, tarRasterHeight);
        aotErrorBand.setValidPixelExpression(InstrumentConsts.VALID_RETRIEVAL_EXPRESSION);
        targetProduct.addBand(aotErrorBand);

        latBand = new Band("latitude", ProductData.TYPE_FLOAT32, tarRasterWidth, tarRasterHeight);
        targetProduct.addBand(latBand);
        lonBand = new Band("longitude", ProductData.TYPE_FLOAT32, tarRasterWidth, tarRasterHeight);
        targetProduct.addBand(lonBand);
    }

    private Rectangle getSourceRectangle(Rectangle targetRectangle, Rectangle pixelWindow) {
        return new Rectangle(targetRectangle.x * pixelWindow.width + pixelWindow.x,
                             targetRectangle.y * pixelWindow.height + pixelWindow.y,
                             targetRectangle.width * pixelWindow.width,
                             targetRectangle.height * pixelWindow.height);
    }

    private void initRasterDimensions(Product sourceProduct, int scale) {
        srcRasterHeight = sourceProduct.getSceneRasterHeight();
        srcRasterWidth = sourceProduct.getSceneRasterWidth();
        tarRasterHeight = srcRasterHeight / scale;
        tarRasterWidth = srcRasterWidth / scale;
    }

    private Map<String, Tile> getSourceTiles(String[] rasterDataNodeNames, Rectangle srcRec, BorderExtender borderExt) {
        Map<String, Tile> tileMap = new HashMap<>(rasterDataNodeNames.length);
        for (String name : rasterDataNodeNames) {
            RasterDataNode b = (name.equals(validName)) ? validBand : sourceProduct.getRasterDataNode(name);
            tileMap.put(name, getSourceTile(b, srcRec, borderExt));
        }
        return tileMap;
    }

    private InputPixelData[] readDarkestNPixels(Map<String, Tile> sourceTiles, int iX, int iY, Rectangle pixelWindow) {
        boolean valid = uniformityTest(sourceTiles, iX, iY);
        if (!valid) {
            return null;
        }

        int NPixel = 30;  // we want to find the 30 darkest pixels
        ArrayList<InputPixelData> inPixelList = new ArrayList<>(pixelWindow.height * pixelWindow.width);
        InputPixelData[] inPixField = null;

        float b3Refl;
        float[] b3Arr = new float[pixelWindow.height * pixelWindow.width];

        double[] tileValues = new double[sourceTiles.size()];

        int nValid = 0;
        int xOffset = iX * pixelWindow.width + pixelWindow.x;
        int yOffset = iY * pixelWindow.height + pixelWindow.y;
        for (int y = yOffset; y < yOffset + pixelWindow.height; y++) {
            for (int x = xOffset; x < xOffset + pixelWindow.width; x++) {
                valid = sourceTiles.get(validName).getSampleBoolean(x, y);
                final int b3ArrIndex = (y - yOffset) * pixelWindow.width + (x - xOffset);
                b3Arr[b3ArrIndex] = (valid) ?
                        sourceTiles.get(S2IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES[2]).getSampleFloat(x, y) : -1;
                if (valid) {
                    nValid++;
                }
            }
        }

        // return null if not enough valid pixels
        if (nValid < 0.25 * pixelWindow.width * pixelWindow.height) {
            return null;
        }

        Arrays.sort(b3Arr);
        double b3Threshold = 0.0; // todo: define. Do we need this??
        if (b3Arr[b3Arr.length - NPixel] > b3Threshold) {
            for (int y = yOffset; y < yOffset + pixelWindow.height; y++) {
                for (int x = xOffset; x < xOffset + pixelWindow.width; x++) {
                    valid = sourceTiles.get(validName).getSampleBoolean(x, y);
                    b3Refl = sourceTiles.get(S2IdepixConstants.S2_MSI_REFLECTANCE_BAND_NAMES[2]).getSampleFloat(x, y);
                    if (valid && (b3Refl >= b3Arr[b3Arr.length - 2 * NPixel])
                            && (b3Refl <= b3Arr[b3Arr.length - NPixel - 1])) {
                        valid = readAllValues(x, y, sourceTiles, tileValues);
                        InputPixelData ipd = createInPixelData(tileValues);
                        if (valid && S2LutUtils.isInsideLut(ipd, s2Lut)) {
                            inPixelList.add(ipd);
                        }
                    }
                }
            }
            if (inPixelList.size() > 3) {
                inPixField = new InputPixelData[inPixelList.size()];
                inPixelList.toArray(inPixField);
            }
        }
        return inPixField;
    }

    private void setTargetSamples(Map<Band, Tile> targetTiles, int x, int y, RetrievalResults result) {

        float[] latLon = getLatLon(x, y, pixelWindow, sourceProduct);
        targetTiles.get(latBand).setSample(x, y, latLon[0]);
        targetTiles.get(lonBand).setSample(x, y, latLon[1]);

        targetTiles.get(aotBand).setSample(x, y, result.getOptAOT());
        targetTiles.get(aotErrorBand).setSample(x, y, result.getRetrievalErr());
    }

    private void setInvalidTargetSamples(Map<Band, Tile> targetTiles, int x, int y) {
        float[] latLon = getLatLon(x, y, pixelWindow, sourceProduct);
        for (Tile t : targetTiles.values()) {
            if (t.getRasterDataNode() == latBand) {
                targetTiles.get(targetProduct.getBand("latitude")).setSample(x, y, latLon[0]);
            } else if (t.getRasterDataNode() == lonBand) {
                targetTiles.get(targetProduct.getBand("longitude")).setSample(x, y, latLon[1]);
            } else {
                t.setSample(x, y, t.getRasterDataNode().getNoDataValue());
            }
        }
    }

    private void setInvalidTargetSamples(Map<Band, Tile> targetTiles) {
        for (Tile.Pos pos : targetTiles.get(targetProduct.getBandAt(0))) {
            setInvalidTargetSamples(targetTiles, pos.x, pos.y);
        }
    }

    private void readSurfaceSpectra(String fname) {
        Guardian.assertNotNull("specWvl", specWvl);
        final InputStream inputStream = S2AerosolOp.class.getResourceAsStream(fname);
        Guardian.assertNotNull("surface spectra InputStream", inputStream);
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        List<Float> fullWvlList = new ArrayList<>();
        List<Float> fullSoilList = new ArrayList<>();
        List<Float> fullVegList = new ArrayList<>();

        int nWvl = 0;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!(line.isEmpty() || line.startsWith("#") || line.startsWith("*"))) {
                    String[] stmp = line.split("[ \t]+");
                    float val = Float.valueOf(stmp[0]);
                    if (val < 100) {
                        val *= 1000;  // conversion from um to nm
                    }
                    fullWvlList.add(val);
                    fullSoilList.add(Float.valueOf(stmp[this.soilSpecId]));
                    fullVegList.add(Float.valueOf(stmp[this.vegSpecId]));
                    nWvl++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(S2AerosolOp.class.getName()).log(Level.SEVERE, null, ex);
            throw new OperatorException(ex.getMessage(), ex.getCause());
        }

        soilSurfSpec = new double[nSpecWvl];
        vegSurfSpec = new double[nSpecWvl];
        int j = 0;
        for (int i = 0; i < nSpecWvl; i++) {
            float wvl = specWvl[0][i];
            float width = specWvl[1][i];
            int count = 0;
            while (j < nWvl && fullWvlList.get(j) < wvl - width / 2) {
                j++;
            }
            if (j == nWvl) {
                throw new OperatorException("wavelength not found reading surface spectra");
            }
            while (fullWvlList.get(j) < wvl + width / 2) {
                soilSurfSpec[i] += fullSoilList.get(j);
                vegSurfSpec[i] += fullVegList.get(j);
                count++;
                j++;
            }
            if (j == nWvl) {
                throw new OperatorException("wavelength window exceeds surface spectra range");
            }
            if (count > 0) {
                soilSurfSpec[i] /= count;
                vegSurfSpec[i] /= count;
            }
        }
    }

    private void createS2LookupTable() throws IOException {
        File lutFile;
        URL lutResource;
        if (pathToLut != null && (new File(pathToLut)).exists()) {
            lutFile = new File(pathToLut);
        } else if ((lutResource = S2LutAccessor.class.getResource("sentinel-2a_lut_smsi_v0.6.memmap.d")) != null) {
            // search in resources as backup
            lutFile = new File(lutResource.getPath());
        } else {
            throw new IOException("");
        }

        S2LutAccessor s2LutAccessor = new S2LutAccessor(lutFile);
        s2Lut = s2LutAccessor.readLut(ProgressMonitor.NULL);
        aotGrid = s2Lut.getDimension(1).getSequence();
    }

    private float[][] getSpectralWvl(String[] bandNames) {
        float[][] wvl = new float[2][bandNames.length];
        for (int i = 0; i < bandNames.length; i++) {
            wvl[0][i] = sourceProduct.getBand(bandNames[i]).getSpectralWavelength();
            wvl[1][i] = sourceProduct.getBand(bandNames[i]).getSpectralBandwidth();
        }
        return wvl;
    }

    private boolean readAllValues(int x, int y, Map<String, Tile> sourceTiles, double[] tileValues) {
        boolean valid = true;
        for (int i = 0; i < InstrumentConsts.GEOM_NAMES.length; i++) {
            tileValues[i] = sourceTiles.get(InstrumentConsts.GEOM_NAMES[i]).getSampleDouble(x, y);
            valid = valid && sourceTiles.get(validName).getSampleBoolean(x, y);
        }
        int skip = InstrumentConsts.GEOM_NAMES.length;
        for (int i = 0; i < InstrumentConsts.REFLEC_NAMES.length; i++) {
            tileValues[i + skip] = sourceTiles.get(InstrumentConsts.REFLEC_NAMES[i]).getSampleDouble(x, y);
            valid = valid && sourceTiles.get(validName).getSampleBoolean(x, y);
        }
        skip += InstrumentConsts.REFLEC_NAMES.length;

        // ozone data
        tileValues[skip++] = sourceTiles.get(InstrumentConsts.OZONE_NAME).getSampleDouble(x, y);
        valid = valid && sourceTiles.get(validName).getSampleBoolean(x, y);

        // surface pressure data
        tileValues[skip++] = sourceTiles.get(InstrumentConsts.SURFACE_PRESSURE_NAME).getSampleDouble(x, y);
        valid = valid && sourceTiles.get(validName).getSampleBoolean(x, y);

        // elevation data
        tileValues[skip] = sourceTiles.get(InstrumentConsts.ELEVATION_NAME).getSampleDouble(x, y);
        valid = valid && sourceTiles.get(validName).getSampleBoolean(x, y);

        return valid;
    }

    //      Test whether validBand contains any valid datapoint in the given source rectangle
    private boolean containsTileValidData(Rectangle srcRec) {
        Tile validTile = getSourceTile(validBand, srcRec);
        for (Tile.Pos pos : validTile) {
            if (validTile.getSampleBoolean(pos.x, pos.y)) {
                return true;
            }
        }
        return false;
    }

    //     Tests uniformity on the given bin pixel (e.g. 9x9 block)
//     based on the NIR reflectance (max - min < 0.2)
    private boolean uniformityTest(Map<String, Tile> sourceTiles, int iX, int iY) {
        String nirName = InstrumentConsts.REFLEC_NAMES[11];  // todo: define
        Guardian.assertNotNullOrEmpty("nirName is empty", nirName);
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        int xOffset = iX * pixelWindow.width + pixelWindow.x;
        int yOffset = iY * pixelWindow.height + pixelWindow.y;
        for (int y = yOffset; y < yOffset + pixelWindow.height; y++) {
            for (int x = xOffset; x < xOffset + pixelWindow.width; x++) {
                boolean valid = sourceTiles.get(validName).getSampleBoolean(x, y);
                double value = sourceTiles.get(nirName).getSampleDouble(x, y);
                if (valid && !Double.isNaN(value)) {
                    if (value < min) min = value;
                    if (value > max) max = value;
                }
            }
        }
        return ((max - min) < 0.2);
    }

    private float[] getLatLon(int iX, int iY, Rectangle pixelWindow, Product sourceProduct) {
        float xOffset = ((iX + 0.5f) * pixelWindow.width + pixelWindow.x);
        float yOffset = ((iY + 0.5f) * pixelWindow.height + pixelWindow.y);
        GeoCoding geoCoding = sourceProduct.getSceneGeoCoding();
        GeoPos geoPos = geoCoding.getGeoPos(new PixelPos(xOffset, yOffset), null);
        return new float[]{(float) geoPos.lat, (float) geoPos.lon};
    }

    public static class Spi extends OperatorSpi {
        public Spi() {
            super(S2AerosolOp.class);
        }
    }
}
