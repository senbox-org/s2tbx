package org.esa.s2tbx.dataio.worldview2.metadata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This maps to the corresponding WorldView2 Tile Component element.
 *
 * @author Razvan Dumitrascu
 */
public class TileComponent {

    private String[] tileNames;
    private Set<String> deliveredTiles;
    private String bandID;
    private int numRows;
    private int numColumns;
    private int bitsPerPixel;
    private double originX;
    private double originY;
    private double stepSize;
    private Map<String, Double> scalingFactor;
    private int mapZone;
    private String mapHemisphere;
    private int numOfTiles;
    private int[] upperLeftColumnOffset;
    private int[] upperLeftRowOffset;
    private int[] upperRightColumnOffset;
    private int[] upperRightRowOffset;
    private int[] lowerLeftColumnOffset;
    private int[] lowerLeftRowOffset;
    private int[] lowerRightColumnOffset;
    private int[] lowerRightRowOffset;

    public TileComponent() {
        this.deliveredTiles = new HashSet<>();
        this.scalingFactor = new HashMap<>();
    }

    public String[] getTileNames() {
        return tileNames;
    }

    public void setTileNames(String[] tileNames) {
        this.tileNames = tileNames;
    }

    public String getBandID() {
        return bandID;
    }

    public void setBandID(String bandID) {
        this.bandID = bandID;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public void setBitsPerPixel(int bitsPerPixel) {
        this.bitsPerPixel = bitsPerPixel;
    }

    public double getOriginX() {
        return originX;
    }

    public void setOriginX(double originX) {
        this.originX = originX;
    }

    public double getOriginY() {
        return originY;
    }

    public void setOriginY(double originY) {
        this.originY = originY;
    }

    public double getStepSize() {
        return stepSize;
    }

    public void setStepSize(double stepSize) {
        this.stepSize = stepSize;
    }

    public int getMapZone() {
        return mapZone;
    }

    public void setMapZone(int mapZone) {
        this.mapZone = mapZone;
    }

    public String getMapHemisphere() {
        return mapHemisphere;
    }

    public void setMapHemisphere(String mapHemisphere) {
        this.mapHemisphere = mapHemisphere;
    }

    public int getNumOfTiles() {
        return numOfTiles;
    }

    public void setNumOfTiles(int numOfTiles) {
        this.numOfTiles = numOfTiles;
    }

    public int[] getUpperLeftColumnOffset() {
        return upperLeftColumnOffset;
    }

    public void setUpperLeftColumnOffset(int[] upperLeftColumnOffset) {
        this.upperLeftColumnOffset = upperLeftColumnOffset;
    }

    public int[] getUpperLeftRowOffset() {
        return upperLeftRowOffset;
    }

    public void setUpperLeftRowOffset(int[] upperLeftRowOffset) {
        this.upperLeftRowOffset = upperLeftRowOffset;
    }

    public int[] getUpperRightColumnOffset() {
        return upperRightColumnOffset;
    }

    public void setUpperRightColumnOffset(int[] upperRightColumnOffset) {
        this.upperRightColumnOffset = upperRightColumnOffset;
    }

    public int[] getUpperRightRowOffset() {
        return upperRightRowOffset;
    }

    public void setUpperRightRowOffset(int[] upperRightRowOffset) {
        this.upperRightRowOffset = upperRightRowOffset;
    }

    public int[] getLowerLeftColumnOffset() {
        return lowerLeftColumnOffset;
    }

    public void setLowerLeftColumnOffset(int[] lowerLeftColumnOffset) {
        this.lowerLeftColumnOffset = lowerLeftColumnOffset;
    }

    public int[] getLowerLeftRowOffset() {
        return lowerLeftRowOffset;
    }

    public void setLowerLeftRowOffset(int[] lowerLeftRowOffset) {
        this.lowerLeftRowOffset = lowerLeftRowOffset;
    }

    public int[] getLowerRightColumnOffset() {
        return lowerRightColumnOffset;
    }

    public void setLowerRightColumnOffset(int[] lowerRightColumnOffset) {
        this.lowerRightColumnOffset = lowerRightColumnOffset;
    }

    public int[] getLowerRightRowOffset() {
        return lowerRightRowOffset;
    }

    public void setLowerRightRowOffset(int[] lowerRightRowOffset) {
        this.lowerRightRowOffset = lowerRightRowOffset;
    }

    /**
     * Mpas the corresponding CRS code for a respective UTM zone
     * @return CRS code for a respective zone
     */
    public String computeCRSCode() {
        String CRS = null;
        if (mapHemisphere !=  null) {
            if (mapHemisphere.equals("S")) {
                CRS = "EPSG:327"+ mapZone;
            } else {
                CRS = "EPSG:326"+ mapZone;
            }
        }
        return CRS;
    }

    public int getTileIndex(String tileName){
        int tileIndex = -1;
       for(int index = 0; index < this.tileNames.length; index++) {
           if(this.tileNames[index].equals(tileName)) {
               tileIndex =  index;
           }
       }
       return tileIndex;
    }

    public double getScalingFactor(String name) {
        return this.scalingFactor.get(name);
    }

    public void setScalingFactor(HashMap<String, Double> abscalfactor,HashMap<String, Double> effectivebandwidth ) {
        for (String key:abscalfactor.keySet()) {
            this.scalingFactor.put(key,0.1*abscalfactor.get(key)/effectivebandwidth.get(key));
        }
    }

    public String[] getDeliveredTiles() {
        return this.deliveredTiles.toArray(new String[this.deliveredTiles.size()]);
    }

    public void addDeliveredTile(String deliveredTile) {
        this.deliveredTiles.add(deliveredTile);
    }
}
