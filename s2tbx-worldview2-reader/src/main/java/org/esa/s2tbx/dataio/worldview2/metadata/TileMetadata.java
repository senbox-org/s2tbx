package org.esa.s2tbx.dataio.worldview2.metadata;

import com.bc.ceres.core.Assert;
import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
import org.esa.snap.core.datamodel.ProductData;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Basic reader for WorldView 2 tiles.
 *
 * @author Razvan Dumitrascu
 * @see XmlMetadata
 */

public class TileMetadata extends XmlMetadata {
    private TileComponent tileComponent;
    private static class TileMetadataParser extends XmlMetadataParser<TileMetadata> {

        TileMetadataParser(Class metadataFileClass) {
            super(metadataFileClass);
        }

        @Override
        protected boolean shouldValidateSchema() {
            return false;
        }
    }

    /**
     * Constructs an instance of metadata class and assigns a name to the root <code>MetadataElement</code>.
     *
     * @param name The name of this instance, and also the initial name of the root element.
     */
    public TileMetadata(String name) {
        super(name);
        this.tileComponent = new TileComponent();
    }

    private void setTileComponent (TileComponent tileComponent) {
        this.tileComponent = tileComponent;
    }

    public TileComponent getTileComponent(){
        return this.tileComponent;
    }

    @Override
    public int getNumBands() {
        return 0;
    }

    @Override
    public String getProductName() {
        return null;
    }

    @Override
    public String getFormatName() {
        return null;
    }

    @Override
    public int getRasterWidth() {
        return 0;
    }

    @Override
    public int getRasterHeight() {
        return 0;
    }

    @Override
    public String[] getRasterFileNames() {
        return new String[0];
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        return null;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        return null;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        return null;
    }

    @Override
    public String getProductDescription() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public String getMetadataProfile() {
        return null;
    }
    /**
     * Creates the metadata element and the component associated to it
     *
     * @param path path to xml metadata file
     * @return TileMetadata object
     * @throws IOException
     */
    public static TileMetadata create(Path path) throws IOException {
        Assert.notNull(path);
        TileMetadata result = null;
        TileComponent tileComponent = new TileComponent();
        try (InputStream inputStream = Files.newInputStream(path)) {
            TileMetadataParser parser = new TileMetadataParser(TileMetadata.class);
            result = parser.parse(inputStream);
            result.setPath(path.toString());
            result.setFileName(path.getFileName().toString());
            String[] tileNames = result.getAttributeValues(WorldView2Constants.PATH_TILE_FILENAME);
            String numRows = result.getAttributeValue(WorldView2Constants.PATH_NUM_ROWS, "0");
            String numColumns = result.getAttributeValue(WorldView2Constants.PATH_NUM_COLUMNS, "0");
            String bitsPerPixel = result.getAttributeValue(WorldView2Constants.PATH_BITS_PER_PIXEL, "0");
            String bandID = result.getAttributeValue(WorldView2Constants.PATH_BAND_ID, null);
            String originX = result.getAttributeValue(WorldView2Constants.PATH_ORIGIN_X, null);
            String originY = result.getAttributeValue(WorldView2Constants.PATH_ORIGIN_Y, null);
            String stepSize = result.getAttributeValue(WorldView2Constants.PATH_PIXEL_STEP_SIZE, null);
            String mapZone = result.getAttributeValue(WorldView2Constants.PATH_MAP_ZONE, null);
            String mapHemisphere = result.getAttributeValue(WorldView2Constants.PATH_MAP_HEMISPHERE, null);
            String numOfTiles = result.getAttributeValue(WorldView2Constants.PATH_NUMBER_OF_TILES, null);
            String[] upperLeftColumnOffset = result.getAttributeValues(WorldView2Constants.PATH_UPPER_LEFT_COLUMN_OFFSET);
            String[] upperLeftRowOffset = result.getAttributeValues(WorldView2Constants.PATH_UPPER_LEFT_ROW_OFFSET);
            String[] upperRightColumnOffset = result.getAttributeValues(WorldView2Constants.PATH_UPPER_RIGHT_COLUMN_OFFSET);
            String[] upperRightRowOffset = result.getAttributeValues(WorldView2Constants.PATH_UPPER_RIGHT_ROW_OFFSET);
            String[] lowerLeftColumnOffset = result.getAttributeValues(WorldView2Constants.PATH_LOWER_LEFT_COLUMN_OFFSET);
            String[] lowerLeftRowOffset = result.getAttributeValues(WorldView2Constants.PATH_LOWER_LEFT_ROW_OFFSET);
            String[] lowerRightColumnOffset = result.getAttributeValues(WorldView2Constants.PATH_LOWER_RIGHT_COLUMN_OFFSET);
            String[] lowerRightRowOffset = result.getAttributeValues(WorldView2Constants.PATH_LOWER_RIGHT_ROW_OFFSET);

            tileComponent.setTileNames(tileNames);
            tileComponent.setBandID(bandID);
            if(numRows != null){
                tileComponent.setNumRows(Integer.parseInt(numRows));
            }
            if(numColumns != null) {
                tileComponent.setNumColumns(Integer.parseInt(numColumns));
            }
            if(bitsPerPixel != null) {
                tileComponent.setBitsPerPixel(Integer.parseInt(bitsPerPixel));
            }
            if(originX != null) {
                tileComponent.setOriginX(Double.parseDouble(originX));
            }
            if(originY != null) {
                tileComponent.setOriginY(Double.parseDouble(originY));
            }
            if(stepSize != null) {
                tileComponent.setStepSize(Double.parseDouble(stepSize));
            }
            if(mapZone != null) {
                tileComponent.setMapZone(Integer.parseInt(mapZone));
            }
            tileComponent.setMapHemisphere(mapHemisphere);
            if(numOfTiles != null) {
                tileComponent.setNumOfTiles(Integer.parseInt(numOfTiles));
            }
            assert upperLeftColumnOffset != null;
            assert upperLeftRowOffset != null;
            assert upperRightColumnOffset != null;
            assert upperRightRowOffset != null;
            assert lowerLeftColumnOffset != null;
            assert lowerLeftRowOffset != null;
            assert lowerRightColumnOffset != null;
            assert lowerRightRowOffset != null;

            int[] upperLeftColumnOffsetInt = new int[upperLeftColumnOffset.length];
            int[] upperLeftRowOffsetInt = new int[upperLeftRowOffset.length];
            int[] upperRightColumnOffsetInt = new int[upperRightColumnOffset.length];
            int[] upperRightRowOffsetInt = new int[upperRightRowOffset.length];
            int[] lowerLeftColumnOffsetInt = new int[lowerLeftColumnOffset.length];
            int[] lowerLeftRowOffsetInt = new int[lowerLeftRowOffset.length];
            int[] lowerRightColumnOffsetInt = new int[lowerRightColumnOffset.length];
            int[] lowerRightRowOffsetInt = new int[lowerRightRowOffset.length];
            for(int index = 0; index < upperLeftColumnOffset.length; index++) {
                upperLeftColumnOffsetInt[index] = Integer.parseInt(upperLeftColumnOffset[index]);
                upperLeftRowOffsetInt[index] = Integer.parseInt(upperLeftRowOffset[index]);
                upperRightColumnOffsetInt[index] = Integer.parseInt(upperRightColumnOffset[index]);
                upperRightRowOffsetInt[index] = Integer.parseInt(upperRightRowOffset[index]);
                lowerLeftColumnOffsetInt[index] = Integer.parseInt(lowerLeftColumnOffset[index]);
                lowerLeftRowOffsetInt[index] = Integer.parseInt(lowerLeftRowOffset[index]);
                lowerRightColumnOffsetInt[index] = Integer.parseInt(lowerRightColumnOffset[index]);
                lowerRightRowOffsetInt[index] = Integer.parseInt(lowerRightRowOffset[index]);
            }

            tileComponent.setUpperLeftColumnOffset(upperLeftColumnOffsetInt);
            tileComponent.setUpperLeftRowOffset(upperLeftRowOffsetInt);
            tileComponent.setUpperRightColumnOffset(upperRightColumnOffsetInt);
            tileComponent.setUpperRightRowOffset(upperRightRowOffsetInt);
            tileComponent.setLowerLeftColumnOffset(lowerLeftColumnOffsetInt);
            tileComponent.setLowerLeftRowOffset(lowerLeftRowOffsetInt);
            tileComponent.setLowerRightColumnOffset(lowerRightColumnOffsetInt);
            tileComponent.setLowerRightRowOffset(lowerRightRowOffsetInt);

        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        assert result != null;
        result.setTileComponent(tileComponent);
        return result;
    }
}
