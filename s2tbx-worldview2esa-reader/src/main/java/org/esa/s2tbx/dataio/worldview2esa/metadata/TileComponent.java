package org.esa.s2tbx.dataio.worldview2esa.metadata;

import java.util.*;

/**
 * This maps to the corresponding WorldView2 ESA archive Tile Component element.
 *
 * @author Denisa Stefanescu
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

    /**
     * Transform UTM format (ex: UTM34N) to EPSG
     *
     * @return CRS code for a respective zone
     */
    public String computeCRSCode() {
        String crs = null;
        if (mapHemisphere != null) {
            if (mapHemisphere.equals("S")) {
                crs = "EPSG:327" + mapZone;
            } else {
                crs = "EPSG:326" + mapZone;
            }
        }
        return crs;
    }


    public double getScalingFactor(String name) {
        return this.scalingFactor.get(name);
    }

    public void setScalingFactor(Map<String, Double> abscalfactor, Map<String, Double> effectivebandwidth) {
        for (String key : abscalfactor.keySet()) {
            this.scalingFactor.put(key, abscalfactor.get(key) / effectivebandwidth.get(key));
        }
    }

    public String[] getDeliveredTiles() {
        return this.deliveredTiles.toArray(new String[this.deliveredTiles.size()]);
    }

    public void addDeliveredTile(String deliveredTile) {
        this.deliveredTiles.add(deliveredTile);
    }
}
