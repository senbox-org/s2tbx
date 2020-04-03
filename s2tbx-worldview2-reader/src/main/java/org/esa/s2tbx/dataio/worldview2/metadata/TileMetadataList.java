package org.esa.s2tbx.dataio.worldview2.metadata;

import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.util.ImageUtils;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by jcoravu on 7/1/2020.
 */
public class TileMetadataList {

    public static final String PANCHROMATIC_BAND_ID = "P";

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

    public void addTileMetadata(TileMetadata tileMetadata) {
        this.tiles.add(tileMetadata);
    }

    public void sortTilesByFileName() {
        if (this.tiles.size() > 1) {
            Comparator<TileMetadata> comparator = new Comparator<TileMetadata>() {
                @Override
                public int compare(TileMetadata leftItem, TileMetadata rightItem) {
                    return leftItem.getFileName().compareTo(rightItem.getFileName());
                }
            };
            Collections.sort(this.tiles, comparator);
        }
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
            if (tileComponent.getBandID().equals(TileMetadataList.PANCHROMATIC_BAND_ID)) {
                return new Dimension(tileComponent.getNumColumns(), tileComponent.getNumRows());
            }
        }
        return null;
    }

    public CrsGeoCoding buildProductGeoCoding(Rectangle subsetBounds) throws FactoryException, TransformException {
        int rasterWidth = 0;
        int rasterHeight = 0;
        double stepSize = 0.0d;
        double originX = 0.0d;
        double originY = 0.0d;
        String crsCode = null;
        for (TileMetadata tileMetadata : this.tiles) {
            TileComponent tileComponent = tileMetadata.getTileComponent();
            if (tileComponent.getBandID().equals(TileMetadataList.PANCHROMATIC_BAND_ID)) {
                rasterWidth = tileComponent.getNumColumns();
                rasterHeight = tileComponent.getNumRows();
                stepSize = tileComponent.getStepSize();
                originX = tileComponent.getOriginX();
                originY = tileComponent.getOriginY();
                crsCode = tileComponent.computeCRSCode();
                break;
            }
        }
        if (crsCode != null) {
            CoordinateReferenceSystem mapCRS = CRS.decode(crsCode);
            return ImageUtils.buildCrsGeoCoding(originX, originY, stepSize, stepSize, rasterWidth, rasterHeight, mapCRS, subsetBounds);
        }
        return null;
    }
}
