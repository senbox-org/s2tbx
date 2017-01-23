package org.esa.s2tbx.dataio.jp2.metadata;

import java.awt.geom.Point2D;

/**
 * Class containing all the resources needed for the JP2metadata
 *
 *  @author  Razvan Dumitrascu
 *  @since 5.0.2
 */
public class JP2MetadataResources {

    private Point2D.Double[] origin = new Point2D.Double[4];
    private int epsgNumber;

    public JP2MetadataResources(){}

    /**
     * sets the latitude and longitude of the 4 corners of the image to be processed
     *
     * @param x1 longitude coordinate of the upper left corner of the product
     * @param y1 latitude coordinate of the upper left corner of the product
     * @param x2 longitude coordinate of the upper right corner of the product
     * @param y2 latitude coordinate of the upper right corner of the product
     * @param x3 longitude coordinate of the lower right corner of the product
     * @param y3 latitude coordinate of the lower right corner of the product
     * @param x4 longitude coordinate of the lower left corner of the product
     * @param y4 latitude coordinate of the lower left corner of the product
     */
    public void setPoint2D(String x1, String y1, String x2, String y2, String x3, String y3, String x4, String y4 ) {
        this.origin[0] = new Point2D.Double(Double.parseDouble(x1), Double.parseDouble(y1));
        this.origin[1] = new Point2D.Double(Double.parseDouble(x2), Double.parseDouble(y2));
        this.origin[2] = new Point2D.Double(Double.parseDouble(x3), Double.parseDouble(y3));
        this.origin[3] = new Point2D.Double(Double.parseDouble(x4), Double.parseDouble(y4));

    }

    /**
     *
     * @return returns the Point2D of the corners of the product
     */
    public Point2D getPoint(int index){
        return this.origin[index];
    }


    /**
     *
     * returns the EPSG code of the UTM zone
     * @return  the EPSG code of the UTM zone where the origin of the product is located
     */
    public int getEpsgNumber() {
        return epsgNumber;
    }

    /**
     *
     * Sets the EPSG code of the UTM zone
     * @param epsgNumber the EPSG code of the UTM zone where the origin of the product is located
     */
    public void setEpsgNumber(int epsgNumber) {
        this.epsgNumber = epsgNumber;
    }

}
