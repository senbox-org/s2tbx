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

    @Override
    public String toString() {
        return "<gml:" + xmlTag + ">\n" +
                "<gml:" + lowTag + ">" + lowerX + " " + lowerY + "</gml:" + lowTag + ">\n" +
                "<gml:" + highTag + ">" + upperX + " " + upperY + "</gml:" + highTag + "\n>" +
                "</gml:" + xmlTag + ">\n";
    }
}
