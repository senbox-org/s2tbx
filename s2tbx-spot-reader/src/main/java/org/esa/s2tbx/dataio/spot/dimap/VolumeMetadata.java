/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
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

package org.esa.s2tbx.dataio.spot.dimap;

import com.bc.ceres.core.Assert;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a DIMAP volume metadata file, which points to individual components (products).
 * As of now, SPOT scene products have only one component.
 *
 * @author Cosmin Cara
 */
public class VolumeMetadata {

    String formatVersion;
    String datasetName;
    String producerName;
    String producerURL;
    Date productionDate;
    String profileName;
    final List<VolumeComponent> components;
    private int[][] tileComponentIndices;

    public static VolumeMetadata create(InputStream inputStream) throws IOException {
        Assert.notNull(inputStream);
        VolumeMetadata result = null;
        try {
            result = VolumeMetadataParser.parse(inputStream);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return result;
    }

    VolumeMetadata() {
        components = new ArrayList<VolumeComponent>();
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getProducerName() {
        return producerName;
    }

    public String getProducerURL() {
        return producerURL;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public String getProfileName() {
        return profileName;
    }

    public int[][] getTileComponentIndices() {
        if (tileComponentIndices == null) {
            tileComponentIndices = new int[components.size()][2];
            for (int i = 0; i < components.size(); i++) {
                int[] cidx = components.get(i).getIndex();
                if (cidx == null) {
                    tileComponentIndices = null;
                    break;
                }
                tileComponentIndices[i][0] = cidx[0];
                tileComponentIndices[i][1] = cidx[1];
            }
        }
        return tileComponentIndices;
    }

    public List<VolumeComponent> getComponents() {
        return components;
    }

    public List<VolumeComponent> getDimapComponents() {
        List<VolumeComponent> results = new ArrayList<VolumeComponent>();
        for (VolumeComponent current : this.components) {
            if (current.getType().equals(SpotConstants.DIMAP)) {
                results.add(current);
            }
        }
        return results;
    }

}
