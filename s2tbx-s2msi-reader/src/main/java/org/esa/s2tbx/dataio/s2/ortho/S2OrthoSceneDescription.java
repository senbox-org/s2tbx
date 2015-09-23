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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SceneDescription;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;
import org.esa.snap.util.SystemUtils;
import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Norman Fomferra
 */
public class S2OrthoSceneDescription extends S2SceneDescription {

    private final TileInfo[] tileInfos;
    private final Envelope2D sceneEnvelope;
    private final Rectangle sceneRectangle;
    private final Map<String, TileInfo> tileInfoMap;

    private static class TileInfo {
        private final int index;
        private final String id;
        private final Rectangle rectangle;

        public TileInfo(int index, String id, Rectangle rectangle) {
            this.index = index;
            this.id = id;
            this.rectangle = rectangle;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public static S2OrthoSceneDescription create(S2Metadata header, S2SpatialResolution productResolution) throws IOException {
        List<S2Metadata.Tile> tileList = header.getTileList();
        CoordinateReferenceSystem crs = null;
        Envelope2D[] tileEnvelopes = new Envelope2D[tileList.size()];
        TileInfo[] tileInfos = new TileInfo[tileList.size()];
        Envelope2D sceneEnvelope = null;

        if (tileList.isEmpty()) {
            throw new IOException("The product contains no tile for this reader");
        }
        for (int i = 0; i < tileList.size(); i++) {
            S2Metadata.Tile tile = tileList.get(i);
            if (crs == null) {
                try {
                    crs = CRS.decode(tile.getHorizontalCsCode());
                    SystemUtils.LOG.fine("crs = " + crs);
                } catch (FactoryException e) {
                    SystemUtils.LOG.severe("Unknown CRS: " + tile.getHorizontalCsCode());
                }
            }

            S2Metadata.TileGeometry selectedGeometry = tile.getGeometry(productResolution);
            Envelope2D envelope = new Envelope2D(crs,
                    selectedGeometry.getUpperLeftX(),
                    selectedGeometry.getUpperLeftY() + selectedGeometry.getNumRows() * selectedGeometry.getyDim(),
                    selectedGeometry.getNumCols() * selectedGeometry.getxDim(),
                    -selectedGeometry.getNumRows() * selectedGeometry.getyDim());
            tileEnvelopes[i] = envelope;

            if (sceneEnvelope == null) {
                sceneEnvelope = new Envelope2D(crs, envelope);
            } else {
                sceneEnvelope.add(envelope);
            }
            tileInfos[i] = new TileInfo(i, tile.getId(), new Rectangle());
        }

        if (sceneEnvelope == null) {
            throw new IllegalStateException("Could not compute scene envelope for the product");
        }
        double imageX = sceneEnvelope.getX();
        double imageY = sceneEnvelope.getY() + sceneEnvelope.getHeight();
        Rectangle sceneBounds = null;
        for (int i = 0; i < tileEnvelopes.length; i++) {
            S2Metadata.Tile tile = tileList.get(i);
            S2Metadata.TileGeometry selectedGeometry = tile.getGeometry(productResolution);
            Envelope2D tileEnvelope = tileEnvelopes[i];
            double tileX = tileEnvelope.getX();
            double tileY = tileEnvelope.getY() + tileEnvelope.getHeight();
            Rectangle rectangle = new Rectangle((int) ((tileX - imageX) / selectedGeometry.getxDim()),
                    (int) ((imageY - tileY) / -selectedGeometry.getyDim()),
                    selectedGeometry.getNumCols(),
                    selectedGeometry.getNumRows());
            if (sceneBounds == null) {
                sceneBounds = new Rectangle(rectangle);
            } else {
                sceneBounds.add(rectangle);
            }
            tileInfos[i] = new TileInfo(i, tile.getId(), rectangle);
        }

        return new S2OrthoSceneDescription(tileInfos, sceneEnvelope, sceneBounds);
    }

    private S2OrthoSceneDescription(TileInfo[] tileInfos,
                                    Envelope2D sceneEnvelope,
                                    Rectangle sceneRectangle) {
        super();

        this.tileInfos = tileInfos;
        this.sceneEnvelope = sceneEnvelope;
        this.sceneRectangle = sceneRectangle;
        this.tileInfoMap = new HashMap<>();
        for (TileInfo tileInfo : tileInfos) {
            tileInfoMap.put(tileInfo.id, tileInfo);
        }
    }

    public Rectangle getSceneRectangle() {
        return sceneRectangle;
    }

    public Envelope2D getSceneEnvelope() {
        return sceneEnvelope;
    }

    public String[] getTileIds() {
        final String[] tileIds = new String[tileInfos.length];
        for (int i = 0; i < tileInfos.length; i++) {
            tileIds[i] = tileInfos[i].id;
        }
        return tileIds;
    }

    public int getTileIndex(String tileId) {
        TileInfo tileInfo = tileInfoMap.get(tileId);
        return tileInfo != null ? tileInfo.index : -1;
    }

    public Rectangle getTileRectangle(int tileIndex) {
        return tileInfos[tileIndex].rectangle;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
