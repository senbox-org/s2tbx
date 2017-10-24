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
public class GmlRectifiedGrid {
    private GmlEnvelope<Integer> gridEnvelope;
    private Point2D origin;
    private int epsgNumber;
    private Point2D.Double offsetVectorX;
    private Point2D.Double offsetVectorY;

    public GmlEnvelope<Integer> getGridEnvelope() {
        return gridEnvelope;
    }

    public void setGridEnvelope(GmlEnvelope<Integer> gridEnvelope) {
        this.gridEnvelope = gridEnvelope;
    }

    public Point2D getOrigin() {
        return origin;
    }

    public void setOrigin(Point2D origin) {
        this.origin = origin;
    }

    public int getEpsgNumber() {
        return epsgNumber;
    }

    public void setEpsgNumber(int epsgNumber) {
        this.epsgNumber = epsgNumber;
    }

    public Point2D.Double getOffsetVectorX() {
        return offsetVectorX;
    }

    public void setOffsetVectorX(Point2D.Double offsetVectorX) {
        this.offsetVectorX = offsetVectorX;
    }

    public Point2D.Double getOffsetVectorY() {
        return offsetVectorY;
    }

    public void setOffsetVectorY(Point2D.Double offsetVectorY) {
        this.offsetVectorY = offsetVectorY;
    }

    @Override
    public String toString() {
        return "<gml:RectifiedGrid dimension=\"2\">\n" +
                "<gml:limits>" + gridEnvelope.toString() + "</gml:limits>\n" +
                "<gml:axisName>x</gml:axisName>\n" +
                "<gml:axisName>y</gml:axisName>\n" +
                "<gml:origin><gml:Point gml:id=\"P0001\" srsName=\"urn:ogc:def:crs:EPSG::" + epsgNumber + "\">\n" +
                "<gml:pos>" + origin.getX() + " " + origin.getY() + "</gml:pos>\n</gml:Point>\n</gml:origin>\n" +
                "<gml:offsetVector srsName=\"urn:ogc:def:crs:EPSG::" + epsgNumber + "\">" + offsetVectorX.x + " " + offsetVectorX.y + "</gml:offsetVector>\n" +
                "<gml:offsetVector srsName=\"urn:ogc:def:crs:EPSG::" + epsgNumber + "\">" + offsetVectorY.x + " " + offsetVectorY.y + "</gml:offsetVector>\n" +
                "</gml:RectifiedGrid>\n";
    }
}
