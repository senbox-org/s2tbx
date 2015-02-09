package org.esa.beam.dataio.s2;

import com.jcabi.aspects.Loggable;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.geotools.geometry.Envelope2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * @author Norman Fomferra
 */
public class L1bSceneDescription {

    private static final double PIXEL_RESOLUTION_10M = S2L1bSpatialResolution.R10M.resolution;
    private static final int TILE_SIZE_10M = S2L1bConfig.L1B_TILE_LAYOUTS[0].width;
    private static final double TILE_RESOLUTION_10M = PIXEL_RESOLUTION_10M * TILE_SIZE_10M;

    private final TileInfo[] tileInfos;
    private final Envelope2D sceneEnvelope;
    private final Rectangle sceneRectangle;
    private final Map<String, TileInfo> tileInfoMap;
    private final L1bMetadata.Tile.idGeom geometry;

    public L1bMetadata.Tile.idGeom getGeometry() {
        return geometry;
    }

    private static class TileInfo {
        private final int index;
        private final String id;
        private final Envelope2D envelope;
        private final Rectangle rectangle;

        public TileInfo(int index, String id, Envelope2D envelope, Rectangle rectangle) {
            this.index = index;
            this.id = id;
            this.envelope = envelope;
            this.rectangle = rectangle;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    @Loggable
    public static L1bSceneDescription create(L1bMetadata header, L1bMetadata.Tile.idGeom index)
    {
        // todo OPP look if Scenedescription object has to be redefined (add a range) or use multiple Scenedescription objects

        List<L1bMetadata.Tile> tileList = header.getTileList();
        CoordinateReferenceSystem crs = GeometryFactory.getDefaultCrs();
        Envelope2D[] tileEnvelopes = new Envelope2D[tileList.size()];
        TileInfo[] tileInfos = new TileInfo[tileList.size()];
        Envelope2D sceneEnvelope = null;

        if(tileList.isEmpty())
        {
            // todo OPP Add help text
            throw new IllegalStateException();
        }
        for (int i = 0; i < tileList.size(); i++) {
            L1bMetadata.Tile tile = tileList.get(i);

            L1bMetadata.TileGeometry selectedGeometry = tile.getGeometry(index);
            // Envelope2D envelope = new Envelope2D(selectedGeometry.envelope);

            Envelope2D envelope = null;

            // todo OPP Change the position using tiepointgrids
            int detectorId = Integer.valueOf(selectedGeometry.detector);

            // data is referenced through 1 based indexes
            int xOffset = (detectorId - 1) * S2L1bConfig.L1B_TILE_LAYOUTS[S2L1bConfig.LAYOUTMAP.get(selectedGeometry.resolution)].width * selectedGeometry.resolution;
            int yOffsetIndex = (selectedGeometry.position - 1) / S2L1bConfig.L1B_TILE_LAYOUTS[S2L1bConfig.LAYOUTMAP.get(10)].height;
            int yWidth = yOffsetIndex * selectedGeometry.yDim * S2L1bConfig.L1B_TILE_LAYOUTS[S2L1bConfig.LAYOUTMAP.get(selectedGeometry.resolution)].height;

            // todo OPP check memory usage when xOffset is 0...
            xOffset = 0;

            envelope = new Envelope2D(crs,
                    xOffset,
                    yWidth + selectedGeometry.numRows * selectedGeometry.yDim,
                    selectedGeometry.numCols * selectedGeometry.xDim,
                    -selectedGeometry.numRows * selectedGeometry.yDim);

            tileEnvelopes[i] = envelope;

            if (sceneEnvelope == null) {
                sceneEnvelope = new Envelope2D(crs, envelope);
            } else {
                sceneEnvelope.add(envelope);
            }
            tileInfos[i] = new TileInfo(i, tile.id, envelope, new Rectangle());
        }

        if (sceneEnvelope == null) {
            throw new IllegalStateException();
        }

        // get back to upperLeft info in scene
        double imageX = sceneEnvelope.getX();
        double imageY = sceneEnvelope.getY() + sceneEnvelope.getHeight();
        Rectangle sceneBounds = null;
        for (int i = 0; i < tileEnvelopes.length; i++)
        {
            L1bMetadata.Tile tile = tileList.get(i);
            L1bMetadata.TileGeometry selectedGeometry = tile.getGeometry(index);
            Envelope2D tileEnvelope = tileEnvelopes[i];

            // upperLeft again
            double tileX = tileEnvelope.getX();
            double tileY = tileEnvelope.getY() + tileEnvelope.getHeight();

            Rectangle rectangle = new Rectangle((int) ((tileX - imageX) / selectedGeometry.xDim),
                    (int) ((imageY - tileY) / -selectedGeometry.yDim),
                    selectedGeometry.numCols,
                    selectedGeometry.numRows);
            if (sceneBounds == null) {
                sceneBounds = new Rectangle(rectangle);
            } else {
                sceneBounds.add(rectangle);
            }
            tileInfos[i] = new TileInfo(i, tile.id, tileEnvelope, rectangle);
        }

        return new L1bSceneDescription(tileInfos, sceneEnvelope, sceneBounds, index);
    }

    private L1bSceneDescription(TileInfo[] tileInfos, Envelope2D sceneEnvelope, Rectangle sceneRectangle, L1bMetadata.Tile.idGeom geometry) {
        this.tileInfos = tileInfos;
        this.sceneEnvelope = sceneEnvelope;
        this.sceneRectangle = sceneRectangle;
        this.geometry = geometry;
        this.tileInfoMap = new HashMap<String, TileInfo>();
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

    public int getTileCount() {
        return tileInfos.length;
    }

    public List<String> getTileIds() {
        final String[] tileIds = new String[tileInfos.length];
        for (int i = 0; i < tileInfos.length; i++) {
            tileIds[i] = tileInfos[i].id;
        }

        List<String> myList = new ArrayList<String>(Arrays.asList(tileIds));
        return myList;
    }

    public int getTileIndex(String tileId) {
        TileInfo tileInfo = tileInfoMap.get(tileId);
        return tileInfo != null ? tileInfo.index : -1;
    }

    public String getTileId(int tileIndex) {
        return tileInfos[tileIndex].id;
    }

    public Envelope2D getTileEnvelope(int tileIndex) {
        return tileInfos[tileIndex].envelope;
    }

    public Rectangle getTileRectangle(int tileIndex) {
        return tileInfos[tileIndex].rectangle;
    }

    public int getTileGridWidth() {
        return (int) Math.round(sceneEnvelope.getWidth() / TILE_RESOLUTION_10M);
    }

    public int getTileGridHeight() {
        return (int) Math.round(sceneEnvelope.getHeight() / TILE_RESOLUTION_10M);
    }

    public BufferedImage createTilePicture(int width) {

        Color[] colors = new Color[]{
                Color.GREEN,
                Color.RED,
                Color.BLUE,
                Color.YELLOW};

        double scale = width / sceneRectangle.getWidth();
        int height = (int) Math.round(sceneRectangle.getHeight() * scale);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.scale(scale, scale);
        graphics.translate(-sceneRectangle.getX(), -sceneRectangle.getY());
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setPaint(Color.WHITE);
        graphics.fill(sceneRectangle);
        graphics.setStroke(new BasicStroke(100F));
        graphics.setFont(new Font("Arial", Font.PLAIN, 800));

        for (int i = 0; i < tileInfos.length; i++) {
            Rectangle rect = tileInfos[i].rectangle;
            graphics.setPaint(addAlpha(colors[i % colors.length].brighter(), 100));
            graphics.fill(rect);
        }
        for (int i = 0; i < tileInfos.length; i++) {
            Rectangle rect = tileInfos[i].rectangle;
            graphics.setPaint(addAlpha(colors[i % colors.length].darker(), 100));
            graphics.draw(rect);
            graphics.setPaint(colors[i % colors.length].darker().darker());
            graphics.drawString("Tile " + (i + 1) + ": " + tileInfos[i].id,
                                rect.x + 1200F,
                                rect.y + 2200F);
        }
        return image;
    }

    private static Color addAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
