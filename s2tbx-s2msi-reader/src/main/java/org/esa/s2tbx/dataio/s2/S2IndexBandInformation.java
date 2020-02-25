/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.s2;

import org.esa.snap.core.datamodel.ColorPaletteDef;
import org.esa.snap.core.datamodel.ImageInfo;
import org.esa.snap.core.datamodel.IndexCoding;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author J. Malik
 */
public class S2IndexBandInformation extends S2BandInformation {

    private final List<S2IndexBandIndex> indexList;
    private final String prefix;

    public S2IndexBandInformation(String physicalBand,
                                  S2SpatialResolution resolution,
                                  String imageFileTemplate,
                                  String description,
                                  String unit,
                                  List<S2IndexBandIndex> indexList,
                                  String prefix) {
        super(physicalBand, resolution, imageFileTemplate, description, unit, 1.0);
        this.indexList = indexList;
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public ImageInfo getImageInfo() {
        List<ColorPaletteDef.Point> points = new ArrayList<>(indexList.size());
        for (S2IndexBandIndex index : indexList) {
            points.add(new ColorPaletteDef.Point(index.sample, index.color, index.label));
        }

        if(points.size() < 1) return null;

        //Add dummy point
        if(points.size() == 1) {
            points.add(new ColorPaletteDef.Point(0.0, Color.BLACK, "-"));
        }

        return new ImageInfo(
                new ColorPaletteDef(
                        points.toArray(
                                new ColorPaletteDef.Point[points.size()]),
                                points.size())
        );
    }

    public IndexCoding getIndexCoding() {
        IndexCoding indexCoding = new IndexCoding(getPhysicalBand());
        for (S2IndexBandIndex index : indexList) {
            indexCoding.addIndex(index.label, index.sample, index.description);
        }
        return indexCoding;
    }

    public List<Color> getColors() {
        List<Color> colorList = new ArrayList<>();
        for (S2IndexBandIndex index : indexList) {
            colorList.add(index.color);
        }
        return colorList;
    }

    public Integer findIndexSample(String name) {
        if (name == null) {
            throw new NullPointerException("The name is null.");
        }
        for (S2IndexBandIndex index : indexList) {
            if (index.label.equals(name)) {
                return index.sample;
            }
        }
        return null;
    }

    public static S2IndexBandIndex makeIndex(int sample, Color color, String label, String description) {
        return new S2IndexBandIndex(sample, color, label, description);
    }

    public static class S2IndexBandIndex {
        private final int sample;
        private final Color color;
        private final String label;
        private final String description;

        S2IndexBandIndex(int sample, Color color, String label, String description) {
            this.sample = sample;
            this.color = color;
            this.label = label;
            this.description = description;
        }
    }
}

