/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.s2.ortho;

import org.esa.s2tbx.dataio.s2.S2Config;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.jdom.JDOMException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Represents the Sentinel-2 MSI L1C XML metadata header file.
 * <p>
 * Note: No data interpretation is done in this class, it is intended to serve the pure metadata content only.
 *
 * @author Norman Fomferra
 */
public class S2OrthoMetadata extends S2Metadata {

    private Map<String, List<Tile>> allTileLists; // Key is UTM zone, values are the tiles associated to a UTM zone

    protected S2OrthoMetadata(S2Config config, JAXBContext context, String psd) throws JDOMException, JAXBException, FileNotFoundException {
        super(config, context, psd);

        allTileLists = new HashMap<>();
    }

    public Set<String> getUTMZonesList() {
        return allTileLists.keySet();
    }

    protected void addTileListToAllTileList(String horizontalCsCode, List<Tile> tileList) {
        allTileLists.put(horizontalCsCode, tileList);
    }

    public List<Tile> getTileList(String utmZone) {
        return allTileLists.get(utmZone);
    }
}
