/*
 *
 *  * Copyright (C) 2016 CS ROMANIA
 *  *
 *  * This program is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published by the Free
 *  * Software Foundation; either version 3 of the License, or (at your option)
 *  * any later version.
 *  * This program is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  * more details.
 *  *
 *  * You should have received a copy of the GNU General Public License along
 *  *  with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.jp2.internal;

import java.awt.geom.Point2D;

/**
 * @author Cosmin Cara
 */
public class GmlEnvelope<T extends Number> {
    private String xmlTag;
    private String lowTag;
    private String highTag;
    private T lowerX;
    private T lowerY;
    private T upperX;
    private T upperY;
    private boolean usePolygon;
    private Point2D.Double[] origin = new Point2D.Double[4];

    public GmlEnvelope(T lowerX, T lowerY, T upperX, T upperY, String xmlelement) {
        this.lowerX = lowerX;
        this.lowerY = lowerY;
        this.upperX = upperX;
        this.upperY = upperY;
        this.xmlTag = xmlelement;
        if ("Envelope".equals(this.xmlTag)) {
            this.lowTag = "lowerCorner";
            this.highTag = "upperCorner";
        } else {
            this.lowTag = "low";
            this.highTag = "high";
        }
    }
    public void setPolygonUse(boolean state) {
        this.usePolygon = state;
    }

    public boolean isPolygonUsed() {
        return this.usePolygon;
    }

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
    public void setPolygonCorners(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4 ) {
        this.origin[0] = new Point2D.Double(x1, y1);
        this.origin[1] = new Point2D.Double(x2, y2);
        this.origin[2] = new Point2D.Double(x3, y3);
        this.origin[3] = new Point2D.Double(x4, y4);

    }
    public String getPolygon(){
        return "<gml:" + xmlTag + ">\n" +
                "<gml:" + lowTag + ">" + "</gml:" + lowTag + ">\n" +
                "<gml:" + highTag + ">" + "</gml:" + highTag + ">\n" +
                "<gml:Polygon>\n" +
                "<gml:exterior>\n" +
                "<gml:LinearRing>\n" +
                "<gml:posList>" +
                this.origin[0].getX() + " " + this.origin[0].getY() + " " +
                this.origin[1].getX() + " " + this.origin[1].getY() + " " +
                this.origin[2].getX() + " " + this.origin[2].getY() + " " +
                this.origin[3].getX() + " " + this.origin[3].getY() + " " +
                this.origin[0].getX() + " " + this.origin[0].getY() +
                "</gml:posList> "+
                "</gml:LinearRing>\n" +
                "</gml:exterior>\n" +
                "</gml:Polygon>\n" +
                "</gml:" + xmlTag + ">\n";
    }

    @Override
    public String toString() {
        return "<gml:" + xmlTag + ">\n" +
                "<gml:" + lowTag + ">" + lowerX + " " + lowerY + "</gml:" + lowTag + ">\n" +
                "<gml:" + highTag + ">" + upperX + " " + upperY + "</gml:" + highTag + ">\n" +
                "</gml:" + xmlTag + ">\n";
    }
}
