package org.esa.s2tbx.dataio.worldview2.metadata;

import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.geotools.referencing.CRS;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by jcoravu on 7/1/2020.
 */
public class TileMetadataList {

    private final Set<String> tiffImageRelativeFiles;
    private final List<TileMetadata> tiles;

    private int multiSpectralBandCount;
    private int bandsDataType;

    public TileMetadataList() {
        this.tiles = new ArrayList<>();
        this.tiffImageRelativeFiles = new HashSet<>();
        this.multiSpectralBandCount = 0;
        this.bandsDataType = 0;
    }

    public void setBandsData(int multiSpectralBandCount, int bandsDataType) {
        this.multiSpectralBandCount = multiSpectralBandCount;
        this.bandsDataType = bandsDataType;
    }

    private String[] getBandNames() {
        if (this.multiSpectralBandCount == 4) {
            return WorldView2Constants.BAND_NAMES_MULTISPECTRAL_4_BANDS;
        }
        return WorldView2Constants.BAND_NAMES_MULTISPECTRAL_8_BANDS;
    }

    public String[] computeBandNames(TileMetadata tileMetadata) {
        String[] availableBandNames = getBandNames();
        String tileBandId = tileMetadata.getTileComponent().getBandID();
        if (tileBandId.equals("MS1") || tileBandId.equals("Multi")) {
            if (this.multiSpectralBandCount <= 0) {
                throw new IllegalStateException("Invalid multi spectral band count " + this.multiSpectralBandCount + ".");
            }
            String[] bandNames = new String[this.multiSpectralBandCount];
            System.arraycopy(availableBandNames, 0, bandNames, 0, bandNames.length);
            return bandNames;
        }
        return new String[]{availableBandNames[availableBandNames.length - 1]};
    }

    public int getBandsDataType() {
        return bandsDataType;
    }

    public List<TileMetadata> getTiles() {
        return tiles;
    }

    public Set<String> getTiffImageRelativeFiles() {
        return tiffImageRelativeFiles;
    }

    public Dimension computeDefaultProductSize() {
        for (TileMetadata tileMetadata : this.tiles) {
            TileComponent tileComponent = tileMetadata.getTileComponent();
            if (tileComponent.getBandID().equals("P")) {
                return new Dimension(tileComponent.getNumColumns(), tileComponent.getNumRows());
            }
        }
        return null;
    }
}
