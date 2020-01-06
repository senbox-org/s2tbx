package org.esa.s2tbx.dataio.worldview2esa.metadata;

import org.esa.s2tbx.dataio.worldview2esa.common.WorldView2ESAConstants;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jcoravu on 6/1/2020.
 */
public class TileMetadataList {

    private final List<TileMetadata> tiles;
    private final Set<String> tiffImageRelativeFiles;

    private int multiSpectralBandCount;

    public TileMetadataList() {
        this.tiles = new ArrayList<>();
        this.tiffImageRelativeFiles = new HashSet<>();
        this.multiSpectralBandCount = 0;
    }

    public void setMultiSpectralBandCount(int multiSpectralBandCount) {
        this.multiSpectralBandCount = multiSpectralBandCount;
    }

    public List<TileMetadata> getTiles() {
        return tiles;
    }

    public Set<String> getTiffImageRelativeFiles() {
        return tiffImageRelativeFiles;
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

    public Dimension computeDefaultProductSize() {
        for (TileMetadata tileMetadata : this.tiles) {
            TileComponent tileComponent = tileMetadata.getTileComponent();
            if (tileComponent.getBandID().equals("P")) {
                return new Dimension(tileMetadata.getRasterWidth(), tileMetadata.getRasterHeight());
            }
        }
        return null;
    }

    private String[] getBandNames() {
        if (this.multiSpectralBandCount == 4) {
            return WorldView2ESAConstants.BAND_NAMES_MULTISPECTRAL_4_BANDS;
        }
        return WorldView2ESAConstants.BAND_NAMES_MULTISPECTRAL_8_BANDS;
    }
}
