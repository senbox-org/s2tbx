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
public class GmlFeatureCollection {
    private GmlEnvelope<Double> envelope;
    private GmlRectifiedGrid rectifiedGrid;
    private int numBands;
    private BandDescriptor[] bands;

    public GmlFeatureCollection() { }

    public void setEnvelope(GmlEnvelope<Double> envelope) {
        this.envelope = envelope;
    }

    public void setRectifiedGrid(GmlRectifiedGrid rectifiedGrid) {
        this.rectifiedGrid = rectifiedGrid;
    }

    public void setNumBands(int numBands) {
        this.numBands = numBands;
        this.bands = new BandDescriptor[this.numBands];
    }

    public void setBandInfo(int index, String name, double scale, double offset) {
        this.bands[index] = new BandDescriptor(name, scale, offset);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<gml:FeatureCollection xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengeospatial.net/gml http://schemas.opengis.net/gml/3.1.1/profiles/gmlJP2Profile/1.0.0/gmlJP2Profile.xsd\">\n");
        builder.append("<gml:boundedBy>\n");
        if (this.envelope != null) {
            if(this.envelope.isPolygonUsed()){
                builder.append(this.envelope.getPolygon());
            } else {
                builder.append(this.envelope.toString());
            }
        } else {
            builder.append("<gml:Null>withheld</gml:Null>\n");
        }
        builder.append("</gml:boundedBy>\n");
        builder.append("<gml:featureMember>\n");
        builder.append("<gml:FeatureCollection>\n");
        builder.append("<gml:boundedBy>\n");
        if (this.envelope != null) {
            builder.append(this.envelope.toString());
        } else {
            builder.append("<gml:Null>withheld</gml:Null>\n");
        }
        builder.append("</gml:boundedBy>\n");
        builder.append("<gml:featureMember>\n");
        if (this.bands != null && this.numBands > 0) {
            builder.append("<gml:metaDataProperty>\n");
            builder.append("<GenericMetadata>\n");
            for (int i = 0; i < this.numBands; i++) {
                builder.append("<band>\n");
                builder.append("<name>").append(this.bands[i].name).append("</name>\n");
                builder.append("<scaleFactor>").append(this.bands[i].scale).append("</scaleFactor>\n");
                builder.append("<offset>").append(this.bands[i].offset).append("</offset>\n");
                builder.append("</band>\n");
            }
            builder.append("</GenericMetadata>\n");
            builder.append("</gml:metaDataProperty>\n");
        }
        if (this.rectifiedGrid != null) {
            builder.append("<gml:RectifiedGridCoverage dimension=\"2\" gml:id=\"RGC0001\">\n");
            builder.append("<gml:rectifiedGridDomain>\n");
            builder.append(this.rectifiedGrid.toString());
            builder.append("</gml:rectifiedGridDomain>\n");
            builder.append("<gml:rangeSet>\n");
            builder.append("<gml:File>\n");
            builder.append("<gml:fileName>gmljp2://codestream/0</gml:fileName>\n");
            builder.append("<gml:fileStructure>Record Interleaved</gml:fileStructure>\n");
            builder.append("</gml:File>\n");
            builder.append("</gml:rangeSet>\n");
            builder.append("</gml:RectifiedGridCoverage>\n");
        }
        builder.append("</gml:featureMember>\n");
        builder.append("</gml:FeatureCollection>\n");
        builder.append("</gml:featureMember>\n");
        builder.append("</gml:FeatureCollection>\n");
        return builder.toString();
    }

    private class BandDescriptor {
        String name;
        double scale;
        double offset;

        BandDescriptor(String name, double scale, double offset) {
            this.name = name;
            this.scale = scale;
            this.offset = offset;
        }
    }
}
