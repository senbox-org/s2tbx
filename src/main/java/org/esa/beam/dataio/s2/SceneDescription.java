package org.esa.beam.dataio.s2;

import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author Norman Fomferra
 */
public class SceneDescription {

    private static final double PIXEL_RESOLUTION_10M = 10.0;
    private static final int TILE_SIZE_10M = 10960;
    private static final double TILE_RESOLUTION_10M = PIXEL_RESOLUTION_10M * TILE_SIZE_10M;

    private final TileInfo[] tileInfo;
    private final Envelope2D sceneEnvelope;
    private final Rectangle sceneRectangle;

    private static class TileInfo {
        private final String id;
        private final Envelope2D envelope;
        private final Rectangle rectangle;

        public TileInfo(String id, Envelope2D envelope, Rectangle rectangle) {
            this.id = id;
            this.envelope = envelope;
            this.rectangle = rectangle;
        }
    }

    public static SceneDescription create(Header header) {

        List<Header.Tile> tileList = header.getTileList();
        CoordinateReferenceSystem crs = null;
        Envelope2D[] tileEnvelopes = new Envelope2D[tileList.size()];
        TileInfo[] tileInfos = new TileInfo[tileList.size()];
        Envelope2D sceneEnvelope = null;
        for (int i = 0; i < tileList.size(); i++) {
            Header.Tile tile = tileList.get(i);
            if (crs == null) {
                try {
                    crs = CRS.decode(tile.horizontalCsCode);
                    //System.out.println("crs = " + crs);
                } catch (FactoryException e) {
                    System.err.println("Unknown CRS: " + tile.horizontalCsCode);
                }
            }
            Header.TileGeometry tileGeometry10M = tile.tileGeometry10M;
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
            tileInfos[i] = new TileInfo(tile.id, envelope, new Rectangle());
        }

        if (sceneEnvelope == null) {
            throw new IllegalStateException();
        }

        double imageX = sceneEnvelope.getX();
        double imageY = sceneEnvelope.getY() + sceneEnvelope.getHeight();
        Rectangle sceneBounds = null;
        for (int i = 0; i < tileEnvelopes.length; i++) {
            Header.Tile tile = tileList.get(i);
            Header.TileGeometry tileGeometry10M = tile.tileGeometry10M;
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
            tileInfos[i] = new TileInfo(tile.id, tileEnvelope, rectangle);
        }

        return new SceneDescription(tileInfos, sceneEnvelope, sceneBounds);
    }

    private SceneDescription(TileInfo[] tileInfo, Envelope2D sceneEnvelope, Rectangle sceneRectangle) {
        this.tileInfo = tileInfo;
        this.sceneEnvelope = sceneEnvelope;
        this.sceneRectangle = sceneRectangle;
    }

    public Rectangle getSceneRectangle() {
        return sceneRectangle;
    }

    public Envelope2D getSceneEnvelope() {
        return sceneEnvelope;
    }

    public int getTileCount() {
        return tileInfo.length;
    }

    public String getTileId(int tileIndex) {
        return tileInfo[tileIndex].id;
    }

    public Envelope2D getTileEnvelope(int tileIndex) {
        return tileInfo[tileIndex].envelope;
    }

    public Rectangle getTileRectangle(int tileIndex) {
        return tileInfo[tileIndex].rectangle;
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

        for (int i = 0; i < tileInfo.length; i++) {
            Rectangle rect = tileInfo[i].rectangle;
            graphics.setPaint(addAlpha(colors[i % colors.length].brighter(), 100));
            graphics.fill(rect);
        }
        for (int i = 0; i < tileInfo.length; i++) {
            Rectangle rect = tileInfo[i].rectangle;
            graphics.setPaint(addAlpha(colors[i % colors.length].darker(), 100));
            graphics.draw(rect);
            graphics.setPaint(colors[i % colors.length].darker().darker());
            graphics.drawString("Tile " + (i + 1) + ": " + tileInfo[i].id,
                                rect.x + 1200F,
                                rect.y + 2200F);
        }
        return image;
    }

    private static Color addAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
