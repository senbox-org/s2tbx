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

package org.esa.s2tbx.dataio.s2.ortho;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.esa.s2tbx.dataio.s2.S2Metadata;
import org.esa.s2tbx.dataio.s2.S2SceneDescription;
import org.esa.s2tbx.dataio.s2.S2SpatialResolution;

import java.awt.*;
import java.awt.List;
import java.io.IOException;
import java.util.*;

/**
 * Provides information about the scene layout
 * and where to lay out each tile into the scene
 *
 * @author Julien Malik
 */
public class S2OrthoSceneLayout extends S2SceneDescription {
    private final Map<String, TileInfo> tileInfoMap;
    Map<S2SpatialResolution, Dimension> sceneDimensions;
    double[] sceneOrigin;

    private static class TileInfo {
        private final String id;
        private final Map<S2SpatialResolution, Rectangle> pixelPositionInScene;

        public TileInfo(String id, Map<S2SpatialResolution, Rectangle> pixelPositionInScene) {
            this.id = id;
            this.pixelPositionInScene = pixelPositionInScene;
        }

        Rectangle getPositionInScene(S2SpatialResolution resolution) {
            return pixelPositionInScene.get(resolution);
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public static S2OrthoSceneLayout create(S2Metadata metadata) throws IOException {

        // Find scene upper left corner
        double sceneUpperLeftX = 0, sceneUpperLeftY = 0;
        boolean firstPass = true;
        for (S2Metadata.Tile tile : metadata.getTileList()) {
            // All UpperLeft positions are the same whatever the resolution (pixel upper left corner convention)
            S2Metadata.TileGeometry tileGeom = tile.getTileGeometry(S2SpatialResolution.R10M);

            double tileUpperLeftX = tileGeom.getUpperLeftX();
            double tileUpperLeftY = tileGeom.getUpperLeftY();

            if (firstPass) {
                sceneUpperLeftX = tileUpperLeftX;
                sceneUpperLeftY = tileUpperLeftY;
                firstPass = false;
            }
            else {
                // In 'x' direction, we look for the min. In 'y' direction, for the max.
                sceneUpperLeftX = Math.min(sceneUpperLeftX, tileUpperLeftX);
                sceneUpperLeftY = Math.max(sceneUpperLeftY, tileUpperLeftY);
            }
        }
        double[] sceneOrigin = new double[2];
        sceneOrigin[0] = sceneUpperLeftX;
        sceneOrigin[1] = sceneUpperLeftY;

        // Lay out each tile in the scene
        Map<String, TileInfo> tileInfos = new HashMap<>();
        for (S2Metadata.Tile tile : metadata.getTileList()) {
            Map<S2SpatialResolution, Rectangle> tilePositionInScene = new HashMap<>();
            for (S2SpatialResolution resolution : S2SpatialResolution.values()) {
                S2Metadata.TileGeometry tileGeom = tile.getTileGeometry(resolution);
                if(tileGeom!=null) {
                    Rectangle tilePosition = new Rectangle();
                    tilePosition.x = (int)((tileGeom.getUpperLeftX() - sceneUpperLeftX) / resolution.resolution);
                    tilePosition.y = (int)((sceneUpperLeftY - tileGeom.getUpperLeftY()) / resolution.resolution);
                    tilePosition.width = tileGeom.getNumCols();
                    tilePosition.height = tileGeom.getNumRows();
                    tilePositionInScene.put(resolution, tilePosition);
                }
            }
            tileInfos.put(tile.getId(), new TileInfo(tile.getId(), tilePositionInScene));
        }

        // Deduce scene dimension at each resolution level
        // Adjacent tiles overlap each other, so we compute it
        // by getting the maximum of (tilePosition.x+tilePosition.width, tilePosition.y+tilePosition.height)
        Map<S2SpatialResolution, Dimension> sceneDimensions = new HashMap<>();
        for (S2SpatialResolution resolution : S2SpatialResolution.values()) {
            Dimension dimension = null;
            for (TileInfo tileInfo : tileInfos.values()) {
                Rectangle position = tileInfo.getPositionInScene(resolution);
                if(position==null)
                    break;
                if (dimension == null) {
                    dimension = new Dimension(position.x+position.width, position.y+position.height);
                }
                else {
                    dimension.width = Math.max(position.x+position.width, dimension.width);
                    dimension.height = Math.max(position.y+position.height, dimension.height);
                }
            }
            if(dimension!=null)
                sceneDimensions.put(resolution, dimension);
        }

        return new S2OrthoSceneLayout(tileInfos, sceneDimensions, sceneOrigin);
    }

    private S2OrthoSceneLayout(Map<String, TileInfo> tileInfos, Map<S2SpatialResolution, Dimension> sceneDimensions, double[] sceneOrigin) {
        super();
        this.tileInfoMap = tileInfos;
        this.sceneDimensions = sceneDimensions;
        this.sceneOrigin = sceneOrigin;
    }

    public Dimension getSceneDimension(S2SpatialResolution resolution) {
        return sceneDimensions.get(resolution);
    }

    double[] getSceneOrigin() {
        return sceneOrigin;
    }

    public Set<String> getTileIds() {
        return tileInfoMap.keySet();
    }

    public java.util.List<String> getOrderedTileIds() {
        Set<String> tileIds = this.getTileIds();
        return asSortedList(tileIds);
    }

    @Override
    public Rectangle getMatrixTileRectangle(String tileId, S2SpatialResolution resolution) {
        return getTilePositionInScene(tileId, resolution);
    }

    public Rectangle getTilePositionInScene(String tileId, S2SpatialResolution resolution) {
        return tileInfoMap.get(tileId).getPositionInScene(resolution);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public static <T extends Comparable<? super T>> java.util.List<T> asSortedList(Collection<T> c) {
        java.util.List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }
}
