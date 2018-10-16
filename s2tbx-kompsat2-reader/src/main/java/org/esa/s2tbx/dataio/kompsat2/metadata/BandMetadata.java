package org.esa.s2tbx.dataio.kompsat2.metadata;


/**
 * Class to parse metadata files for specific image files
 *
 * @author  Razvan Dumitrascu
 */
public class BandMetadata {

    private int bitsPerPixel;
    private int numColumns;
    private int numLines;
    private double stepSizeX;
    private double stepSizeY;
    private String imageFileName;
    private double bandwidth;
    private double azimuth;
    private double incidenceAngle;

    public BandMetadata(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public void setBitsPerPixel(int bitsPerPixel) {
        this.bitsPerPixel = bitsPerPixel;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public int getNumLines() {
        return numLines;
    }

    public void setNumLines(int numLines) {
        this.numLines = numLines;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }

    public double getStepSizeX() {
        return stepSizeX;
    }

    public void setStepSizeX(double stepSizeX) {
        this.stepSizeX = stepSizeX;
    }

    public double getStepSizeY() {
        return stepSizeY;
    }

    public void setStepSizeY(double stepSizeY) {
        this.stepSizeY = stepSizeY;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public double getIncidenceAngle() {
        return incidenceAngle;
    }

    public void setIncidenceAngle(double incidenceAngle) {
        this.incidenceAngle = incidenceAngle;
    }
}
