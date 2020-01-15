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

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.snap.core.datamodel.MetadataElement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class holds the metadata extracted from DIMAP XML file.
 * It exposes convenience methods for fetching various useful metadata values.
 *
 * @author Cosmin Cara
 * modified 20190523 for VFS compatibility by Oana H.
 */
public class SpotSceneMetadata {

    private final VirtualDirEx folder;
    private VolumeMetadata volumeMetadata;
    private final List<SpotDimapMetadata> componentMetadata;
    private final Logger logger;
    private int numComponents;
    private final MetadataElement rootElement;

    static {
        XmlMetadataParserFactory.registerParser(SpotDimapMetadata.class, new XmlMetadataParser<SpotDimapMetadata>(SpotDimapMetadata.class));
    }
    private SpotSceneMetadata(VirtualDirEx folder, Logger logger) {
        this.folder = folder;
        this.logger = logger;
        componentMetadata = new ArrayList<SpotDimapMetadata>();
        try {
            readMetadataFiles();
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
        rootElement = new MetadataElement("SPOT Metadata");
    }

    public static SpotSceneMetadata create(VirtualDirEx folder, Logger logger) {
        return new SpotSceneMetadata(folder, logger);
    }

    public VolumeMetadata getVolumeMetadata() {
        return volumeMetadata;
    }

    public List<SpotDimapMetadata> getComponentsMetadata() {
        return componentMetadata;
    }

    public MetadataElement getRootElement() {
        if (rootElement.getNumElements() == 0) {
            for (SpotDimapMetadata mf : componentMetadata) {
                rootElement.addElement(mf.getRootElement());
            }
        }
        return rootElement;
    }

    public int getNumComponents() {
        return numComponents;
    }

    public SpotDimapMetadata getComponentMetadata(int index) {
        if ((index < 0) || (index > componentMetadata.size() - 1))
            throw new IllegalArgumentException("Invalid index value");
        return componentMetadata.get(index);
    }

    public boolean hasMultipleComponents() {
        return numComponents > 1;
    }

    /*public int[][] getTileComponentIndices() {
        return volumeMetadata.getTileComponentIndices();
    }*/

    public int getExpectedTileComponentRows() {
        int[][] indices = volumeMetadata.getTileComponentIndices();
        int rows = 1;
        if (indices != null) {
            for (int[] indice : indices) {
                rows = Math.max(rows, indice[0] + 1);
            }
        }
        return rows;
    }

    public int getExpectedTileComponentCols() {
        int[][] indices = volumeMetadata.getTileComponentIndices();
        int cols = 1;
        if (indices != null) {
            for (int[] indice : indices) {
                cols = Math.max(cols, indice[1] + 1);
            }
        }
        return cols;
    }

    public int getExpectedVolumeWidth() {
        int[][] indices = volumeMetadata.getTileComponentIndices();
        int width = 0;
        List<VolumeComponent> components = volumeMetadata.getDimapComponents();
        int expectedNumCols = getExpectedTileComponentCols();
        if (indices == null) {
            width = this.componentMetadata.get(0).getRasterWidth();
        } else {
            int cursor = 0;
            for (int i = 0; i < indices.length; i++) {
                if (cursor == expectedNumCols) break;
                int[] compIndex = components.get(i).getIndex();
                if (compIndex != null && compIndex[1] == cursor) {
                    width += componentMetadata.get(i).getRasterWidth();
                    cursor++;
                }
            }
        }
        return width;
    }

    public int getExpectedVolumeHeight() {
        int[][] indices = volumeMetadata.getTileComponentIndices();
        int height = 0;
        List<VolumeComponent> components = volumeMetadata.getDimapComponents();
        int expectedNumRows = getExpectedTileComponentRows();
        if (indices == null) {
            height = this.componentMetadata.get(0).getRasterHeight();
        } else {
            int cursor = 0;
            for (int i = 0; i < indices.length; i++) {
                if (cursor == expectedNumRows) break;
                int[] compIndex = components.get(i).getIndex();
                if (compIndex != null && compIndex[0] == cursor) {
                    height += componentMetadata.get(i).getRasterHeight();
                    cursor++;
                }
            }
        }
        return height;
    }

    private void readMetadataFiles() throws IOException {
        // try to get first the volume metadata file
        File file = null;
        try {
            file = folder.getFile(SpotConstants.DIMAP_VOLUME_FILE);
        } catch (Exception e) {
        }
        if (file == null || !file.exists()) { // no volume, then look for metadata*.dim
            File selectedMetadataFile = folder.getFile(SpotConstants.SPOTSCENE_METADATA_FILE);
            logger.info("Read metadata file " + selectedMetadataFile.getName());
            SpotDimapMetadata metadata = GenericXmlMetadata.create(SpotDimapMetadata.class, selectedMetadataFile);
            if (metadata == null) {
                logger.warning(String.format("Error while reading metadata file %s",
                        selectedMetadataFile.getName()));
            } else {
                metadata.setFileName(selectedMetadataFile.getName());
                componentMetadata.add(metadata);
            }
        } else { // vol_list.dim metadata file is present
            logger.info("Read volume metadata file");
            //FileInputStream stream = null;
            InputStream stream = null;
            try {
                //stream = new FileInputStream(file);
                stream = Files.newInputStream(file.toPath());
                volumeMetadata = VolumeMetadata.create(stream);
            } finally {
                if (stream != null) stream.close();
            }
            List<VolumeComponent> encapsulatedComponents;
            if ((volumeMetadata != null) && ((encapsulatedComponents = volumeMetadata.getDimapComponents()) != null)) {
                for (VolumeComponent component : encapsulatedComponents) {
                    File metadataFile = folder.getFile(component.getPath().toLowerCase());
                    if (metadataFile.getName().toLowerCase().endsWith(".dim")) {
                        logger.info("Read component metadata file " + metadataFile.getName());
                        SpotDimapMetadata metadata = GenericXmlMetadata.create(SpotDimapMetadata.class, metadataFile);
                        if (metadata == null) {
                            logger.warning(String.format("Error while reading metadata file %s",
                                    metadataFile.getName()));
                        } else {
                            metadata.setFileName(metadataFile.getName());
                            metadata.setPath(Paths.get(component.getPath()));
                            componentMetadata.add(metadata);
                        }
                    }
                }
            }
        }
        numComponents = componentMetadata.size();
    }

/*    private boolean canTileComponents() {
        return componentMetadata.size() > 1 && volumeMetadata.getTileComponentIndices() != null;
    }*/

}
