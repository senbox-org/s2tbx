package org.esa.s2tbx.dataio.worldview2esa.metadata;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.commons.FilePathInputStream;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.worldview2esa.common.WorldView2ESAConstants;
import org.esa.snap.core.datamodel.ProductData;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic reader for WorldView 2 ESA archive tiles.
 *
 * @author Denisa Stefanescu
 * @see XmlMetadata
 */

public class TileMetadata extends XmlMetadata {

    private static final Logger log = Logger.getLogger(TileMetadata.class.getName());

    private TileComponent tileComponent;
    private int tileRowsCount;
    private int tileColsCount;

    /**
     * Constructs an instance of metadata class and assigns a name to the root <code>MetadataElement</code>.
     *
     * @param name The name of this instance, and also the initial name of the root element.
     */
    public TileMetadata(String name) {
        super(name);

        this.tileComponent = new TileComponent();
        this.tileRowsCount = 0;
        this.tileColsCount = 0;
    }

    private void setTileComponent(TileComponent tileComponent) {
        this.tileComponent = tileComponent;
    }

    public TileComponent getTileComponent() {
        return this.tileComponent;
    }

    @Override
    public int getNumBands() {
        return 0;
    }

    @Override
    public String getProductName() {
        return null;
    }

    @Override
    public String getFormatName() {
        return null;
    }

    @Override
    public int getRasterWidth() {
        return Integer.parseInt(getAttributeValue(WorldView2ESAConstants.PATH_NUM_COLUMNS, "0"));
    }

    @Override
    public int getRasterHeight() {
        return Integer.parseInt(getAttributeValue(WorldView2ESAConstants.PATH_NUM_ROWS, "0"));
    }

    @Override
    public String[] getRasterFileNames() {
        return this.tileComponent.getDeliveredTiles();
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        return null;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        return null;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        return null;
    }

    @Override
    public String getProductDescription() {
        return this.name;
    }

    @Override
    public String getFileName() {
        return this.name;
    }

    @Override
    public String getMetadataProfile() {
        return null;
    }

    public static TileMetadata create(FilePathInputStream filePathInputStream) throws IOException, ParserConfigurationException, SAXException, InstantiationException {
        Assert.notNull(filePathInputStream);

        TileMetadata result = (TileMetadata) XmlMetadataParserFactory.getParser(TileMetadata.class).parse(filePathInputStream);
        result.setPath(filePathInputStream.getPath());
        result.setFileName(filePathInputStream.getPath().getFileName().toString());
        final Map<String, Double> abscalfactor = new HashMap<>();
        final Map<String, Double> effectivebandwidth = new HashMap<>();
        final String[] tileNames = result.getAttributeValues(WorldView2ESAConstants.PATH_TILE_FILENAME);
        final String numRows = result.getAttributeValue(WorldView2ESAConstants.PATH_TILE_NUM_ROWS, "0");
        final String numColumns = result.getAttributeValue(WorldView2ESAConstants.PATH_TILE_NUM_COLUMNS, "0");
        final String bitsPerPixel = result.getAttributeValue(WorldView2ESAConstants.PATH_BITS_PER_PIXEL, "0");
        final String bandID = result.getAttributeValue(WorldView2ESAConstants.PATH_BAND_ID, null);
        final String originX = result.getAttributeValue(WorldView2ESAConstants.PATH_ORIGIN_X, null);
        final String originY = result.getAttributeValue(WorldView2ESAConstants.PATH_ORIGIN_Y, null);
        final String stepSize = result.getAttributeValue(WorldView2ESAConstants.PATH_PIXEL_STEP_SIZE, null);
        final String mapZone = result.getAttributeValue(WorldView2ESAConstants.PATH_MAP_ZONE, null);
        final String mapHemisphere = result.getAttributeValue(WorldView2ESAConstants.PATH_MAP_HEMISPHERE, null);
        final String numOfTiles = result.getAttributeValue(WorldView2ESAConstants.PATH_NUMBER_OF_TILES, null);
        if (bandID.equalsIgnoreCase("MS1") || bandID.equalsIgnoreCase("Multi")) {
            for (String pathFactor : WorldView2ESAConstants.BAND_MS1_ABSCALFACTOR_PATTERNS) {
                String caseVal = pathFactor.substring(pathFactor.indexOf('_') + 1, pathFactor.lastIndexOf('/'));
                switch (caseVal) {
                    case "b":
                        abscalfactor.put("Blue", Double.parseDouble(result.getAttributeValue(pathFactor, "0")));
                        break;
                    case "n":
                        abscalfactor.put("NIR1", Double.parseDouble(result.getAttributeValue(pathFactor, "0")));
                        break;
                    case "g":
                        abscalfactor.put("Green", Double.parseDouble(result.getAttributeValue(pathFactor, "0")));
                        break;
                    case "r":
                        abscalfactor.put("Red", Double.parseDouble(result.getAttributeValue(pathFactor, "0")));
                        break;
                    case "c":
                        abscalfactor.put("Coastal", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    case "y":
                        abscalfactor.put("Yellow", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    case "re":
                        abscalfactor.put("Red Edge", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    case "n2":
                        abscalfactor.put("NIR2", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    default:
                        log.log(Level.WARNING, "The " + pathFactor + " information are missing from the metadata");
                }

            }
            for (String pathFactor : WorldView2ESAConstants.BAND_MS1_EFFECTIVEBANDWIDTH_PATTERNS) {
                String caseVal = pathFactor.substring(pathFactor.indexOf('_') + 1, pathFactor.lastIndexOf('/'));
                switch (caseVal) {
                    case "b":
                        effectivebandwidth.put("Blue", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    case "n":
                        effectivebandwidth.put("NIR1", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    case "g":
                        effectivebandwidth.put("Green", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    case "r":
                        effectivebandwidth.put("Red", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    case "c":
                        effectivebandwidth.put("Coastal", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    case "y":
                        effectivebandwidth.put("Yellow", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    case "re":
                        effectivebandwidth.put("Red Edge", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    case "n2":
                        effectivebandwidth.put("NIR2", Double.parseDouble(result.getAttributeValue(pathFactor, "1")));
                        break;
                    default:
                        log.log(Level.WARNING, "The " + pathFactor + " information are missing from the metadata");

                }
            }
        } else {
            abscalfactor.put("Pan", Double.parseDouble(result.getAttributeValue(WorldView2ESAConstants.BAND_P_ABSCALFACTOR, "0")));
            effectivebandwidth.put("Pan", Double.parseDouble(result.getAttributeValue(WorldView2ESAConstants.BAND_P_EFFECTIVEBANDWIDTH, "1")));
        }

        TileComponent tileComponent = new TileComponent();
        tileComponent.setTileNames(tileNames);
        tileComponent.setBandID(bandID);
        if (numRows != null) {
            tileComponent.setNumRows(Integer.parseInt(numRows));
        }
        if (numColumns != null) {
            tileComponent.setNumColumns(Integer.parseInt(numColumns));
        }
        if (bitsPerPixel != null) {
            tileComponent.setBitsPerPixel(Integer.parseInt(bitsPerPixel));
        }
        if (originX != null) {
            tileComponent.setOriginX(Double.parseDouble(originX));
        }
        if (originY != null) {
            tileComponent.setOriginY(Double.parseDouble(originY));
        }
        if (stepSize != null) {
            tileComponent.setStepSize(Double.parseDouble(stepSize));
        }
        if (mapZone != null) {
            tileComponent.setMapZone(Integer.parseInt(mapZone));
        }
        tileComponent.setMapHemisphere(mapHemisphere);
        if (numOfTiles != null) {
            tileComponent.setNumOfTiles(Integer.parseInt(numOfTiles));
        }
        tileComponent.setScalingFactor(abscalfactor, effectivebandwidth);
        result.setTileComponent(tileComponent);
        return result;
    }

    public Map<String, int[]> computeRasterTileInfo() {
        final String[] names = getRasterFileNames();
        final List<Integer> rows = new ArrayList<>();
        final List<Integer> cols = new ArrayList<>();
        for (String name : names) {
            String regex = "R\\d+C\\d+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(name);
            int row = 0;
            int col = 0;
            if (matcher.find()) {
                String splittingAfterRowColsIdentif = matcher.group();
                row = Integer.parseInt(splittingAfterRowColsIdentif.substring(splittingAfterRowColsIdentif.indexOf('R') + 1, splittingAfterRowColsIdentif.indexOf('C')));
                col = Integer.parseInt(splittingAfterRowColsIdentif.substring(splittingAfterRowColsIdentif.indexOf('C') + 1));
            }
            rows.add(row);
            cols.add(col);
        }

        final int minRowIndex = Collections.min(rows);
        final int minColIndex = Collections.min(cols);
        final int maxRowIndex = Collections.max(rows);
        final int maxColIndex = Collections.max(cols);
        setTileRowsCount(maxRowIndex - minRowIndex + 1);
        setTileColsCount(maxColIndex);

        Map<String, int[]> tileInfo = new HashMap<>();
        for (int i = 0; i < names.length; i++) {
            tileInfo.put(names[i], new int[]{rows.get(i) - minRowIndex, cols.get(i) - minColIndex});
        }
        return tileInfo;
    }

    public int getTileRowsCount() {
        return tileRowsCount;
    }

    public int getTileColsCount() {
        return tileColsCount;
    }

    private void setTileRowsCount(int tileRowsCount) {
        this.tileRowsCount = tileRowsCount;
    }

    private void setTileColsCount(int tileColsCount) {
        this.tileColsCount = tileColsCount;
    }

    public int getProductDataType() {
        int retVal;
        int value = Integer.parseInt(getAttributeValue(WorldView2ESAConstants.PATH_BITS_PER_PIXEL, WorldView2ESAConstants.DEFAULT_PIXEL_SIZE));
        switch (value) {
            case 8:
                retVal = ProductData.TYPE_UINT8;
                break;
            case 12:
            case 16:
                retVal = ProductData.TYPE_UINT16;
                break;
            case 32:
                retVal = ProductData.TYPE_UINT32;
                break;
            default:
                retVal = ProductData.TYPE_UINT8;
                break;
        }
        return retVal;
    }
}