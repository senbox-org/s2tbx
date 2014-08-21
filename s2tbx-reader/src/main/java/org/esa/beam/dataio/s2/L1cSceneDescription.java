package org.esa.beam.dataio.s2;

import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Norman Fomferra
 */
public class L1cSceneDescription {

    private static final double PIXEL_RESOLUTION_10M = S2SpatialResolution.R10M.resolution;
    private static final int TILE_SIZE_10M = S2Config.L1C_TILE_LAYOUTS[0].width;
    private static final double TILE_RESOLUTION_10M = PIXEL_RESOLUTION_10M * TILE_SIZE_10M;

    private final TileInfo[] tileInfos;
    private final Envelope2D sceneEnvelope;
    private final Rectangle sceneRectangle;
    private final Map<String, TileInfo> tileInfoMap;


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
    }

    public static L1cSceneDescription create(L1cMetadata header) {

        List<L1cMetadata.Tile> tileList = header.getTileList();
        CoordinateReferenceSystem crs = null;
        Envelope2D[] tileEnvelopes = new Envelope2D[tileList.size()];
        TileInfo[] tileInfos = new TileInfo[tileList.size()];
        Envelope2D sceneEnvelope = null;

        if(tileList.isEmpty())
        {
            throw new IllegalStateException();
        }
        for (int i = 0; i < tileList.size(); i++) {
            L1cMetadata.Tile tile = tileList.get(i);
            if (crs == null) {
                try {
                    crs = CRS.decode(tile.horizontalCsCode);
                    System.out.println("crs = " + crs);
                } catch (FactoryException e) {
                    System.err.println("Unknown CRS: " + tile.horizontalCsCode);
                }
            }
            L1cMetadata.TileGeometry tileGeometry10M = tile.tileGeometry10M;
            Envelope2D envelope = new Envelope2D(crs,
                                                 tileGeometry10M.upperLeftX,
                                                 tileGeometry10M.upperLeftY + tileGeometry10M.numRows * tileGeometry10M.yDim,
                                                 tileGeometry10M.numCols * tileGeometry10M.xDim,
                                                 -tileGeometry10M.numRows * tileGeometry10M.yDim);
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
        double imageX = sceneEnvelope.getX();
        double imageY = sceneEnvelope.getY() + sceneEnvelope.getHeight();
        Rectangle sceneBounds = null;
        for (int i = 0; i < tileEnvelopes.length; i++) {
            L1cMetadata.Tile tile = tileList.get(i);
            L1cMetadata.TileGeometry tileGeometry10M = tile.tileGeometry10M;
            Envelope2D tileEnvelope = tileEnvelopes[i];
            double tileX = tileEnvelope.getX();
            double tileY = tileEnvelope.getY() + tileEnvelope.getHeight();
            Rectangle rectangle = new Rectangle((int) ((tileX - imageX) / tileGeometry10M.xDim),
                                                (int) ((imageY - tileY) / -tileGeometry10M.yDim),
                                                tileGeometry10M.numCols,
                                                tileGeometry10M.numRows);
            if (sceneBounds == null) {
                sceneBounds = new Rectangle(rectangle);
            } else {
                sceneBounds.add(rectangle);
            }
            tileInfos[i] = new TileInfo(i, tile.id, tileEnvelope, rectangle);
        }

        return new L1cSceneDescription(tileInfos, sceneEnvelope, sceneBounds);
    }

    private L1cSceneDescription(TileInfo[] tileInfos, Envelope2D sceneEnvelope, Rectangle sceneRectangle) {
        this.tileInfos = tileInfos;
        this.sceneEnvelope = sceneEnvelope;
        this.sceneRectangle = sceneRectangle;
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
}
