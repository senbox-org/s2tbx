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

package org.esa.s2tbx.landcover.dataio;

import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductReader;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.dataop.resamp.Resampling;
import org.esa.snap.core.util.SystemUtils;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.landcover.dataio.FileLandCoverModel;
import org.esa.snap.landcover.dataio.FileLandCoverTile;
import org.esa.snap.landcover.dataio.LandCoverModelDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Cosmin Cara
 */
public class JaxaFileModel extends FileLandCoverModel {

    static final String EAST_LONGITUDE = "E%03d";
    static final String WEST_LONGITUDE = "W%03d";
    static final String NORTH_LATITUDE = "N%02d";
    static final String SOUTH_LATITUDE = "S%03d";
    private static final String JAXA_FORMAT = "ENVI";
    private static final String ARCHIVE_EXT = ".tar.gz";
    private static final String FILE_EXT = ".hdr";
    private static final String TILE_SUFFIX = "_16_C_F02DAR";
    private static final String TILE_TOKEN = "_C_";
    private static final String SUPER_TILE_TOKEN = "_FNF_";
    static final int DEGREES_PER_SUPERTILE = 5;
    private final Set<File> unpackedDirs;
    private final Map<String, FileLandCoverTile> tiles;

    public JaxaFileModel(LandCoverModelDescriptor descriptor, File[] files, Resampling resamplingMethod) throws IOException {
        super(descriptor, files, resamplingMethod, ARCHIVE_EXT);
        this.unpackedDirs = new HashSet<>();
        this.tiles = new HashMap<>();
    }

    @Override
    public synchronized double getLandCover(GeoPos geoPos) throws Exception {
        try {
            FileLandCoverTile tile = loadTile(geoPos);
            if (tile != null && tile.getTileGeocoding() != null) {
                final PixelPos pix = tile.getTileGeocoding().getPixelPos(geoPos, null);
                resampling.computeIndex(pix.x, pix.y, tile.getWidth(), tile.getHeight(), resamplingIndex);

                final double value = resampling.resample(tile, resamplingIndex);
                if (Double.isNaN(value)) {
                    return tile.getNoDataValue();
                }
                return value;
            } else {
                return Double.NaN;
            }
        } catch (Exception e) {
            throw new Exception("Problem reading : " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        this.tiles.values().forEach(FileLandCoverTile::dispose);
        this.tiles.clear();
        this.unpackedDirs.forEach(f -> {
            try {
                FileUtils.deleteTree(f);
            } catch (Exception ex) {
                SystemUtils.LOG.warning(ex.getMessage());
            }
        });
        this.unpackedDirs.clear();
    }

    private FileLandCoverTile loadTile(GeoPos geoPos) throws Exception {
        FileLandCoverTile tile = null;
        final double lat = geoPos.getLat();
        final double lon = geoPos.getLon();
        String superTileLon = lon >= 0 ?
                String.format(EAST_LONGITUDE, Math.floorDiv((int) lon, DEGREES_PER_SUPERTILE) * DEGREES_PER_SUPERTILE) :
                String.format(WEST_LONGITUDE, (Math.floorDiv((int) -lon, DEGREES_PER_SUPERTILE) + 1) * DEGREES_PER_SUPERTILE);
        String superTileLat = lat > 0 ?
                String.format(NORTH_LATITUDE, (Math.floorDiv((int) lat, DEGREES_PER_SUPERTILE) + 1) * DEGREES_PER_SUPERTILE) :
                lat > -DEGREES_PER_SUPERTILE ?
                        String.format(NORTH_LATITUDE, 0) :
                        String.format(SOUTH_LATITUDE, Math.floorDiv((int) -lat, DEGREES_PER_SUPERTILE) * DEGREES_PER_SUPERTILE);
        String superTileFolder = superTileLat + superTileLon;
        JaxaForestMapModelDescriptor descriptor = (JaxaForestMapModelDescriptor) this.descriptor;
        descriptor.changeRemoteDir(superTileFolder);
        try {
            File tileFolder = new File(this.descriptor.getInstallDir(), superTileFolder);
            tileFolder.mkdirs();
            this.unpackedDirs.add(tileFolder);
            String subTileFilename = createSubTileFilename(geoPos);
            if (!this.tiles.containsKey(subTileFilename)) {
                File subTileFile = new File(tileFolder, subTileFilename);
                File fakeTileFile = new File(tileFolder, subTileFilename.replace(TILE_TOKEN, SUPER_TILE_TOKEN));
                File subTileArchive = new File(tileFolder, fakeTileFile.getName().replace(FILE_EXT, ARCHIVE_EXT));
                final ProductReader reader = ProductIO.getProductReader(JAXA_FORMAT);
                if (!subTileArchive.exists()) {
                    tile = new FileLandCoverTile(this, fakeTileFile, reader, ARCHIVE_EXT);
                    uncompress(subTileArchive.getName(), tileFolder);
                } else if (!subTileFile.exists()) {
                    uncompress(subTileArchive.getName(), tileFolder);
                }
                tiles.put(subTileFilename, new FileLandCoverTile(this, subTileFile, reader, ARCHIVE_EXT));
            }
            tile = this.tiles.get(subTileFilename);
        } catch (IOException e) {
            SystemUtils.LOG.severe(e.getMessage());
        }
        return tile;
    }

    private void uncompress(String archiveName, File destDir) {
        final TarGZipUnArchiver archiver = new TarGZipUnArchiver();
        ConsoleLoggerManager manager = new ConsoleLoggerManager();
        manager.initialize();
        archiver.enableLogging(manager.getLoggerForComponent(JaxaFileModel.class.getName()));
        File archive = new File(destDir, archiveName);
        archiver.setSourceFile(archive);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        archiver.setDestDirectory(destDir);
        archiver.extract();
    }

    private String createSubTileFilename(GeoPos geoPos) {
        StringBuilder name = new StringBuilder();
        final double lat = geoPos.getLat();
        final double lon = geoPos.getLon();
        int tileX = lon >= 0 ? (int) lon : -(int) lon + 1;
        int tileY = lat >= 0 ? (int) lat + 1 : -(int) lat;
        if (lat >= 0) {
            name.append(String.format(NORTH_LATITUDE, tileY));
        } else {
            name.append(String.format(SOUTH_LATITUDE, tileY));
        }
        if (lon >= 0) {
            name.append(String.format(EAST_LONGITUDE, tileX));
        } else {
            name.append(String.format(WEST_LONGITUDE, tileX));
        }
        name.append(TILE_SUFFIX).append(FILE_EXT);
        return name.toString();
    }
}
