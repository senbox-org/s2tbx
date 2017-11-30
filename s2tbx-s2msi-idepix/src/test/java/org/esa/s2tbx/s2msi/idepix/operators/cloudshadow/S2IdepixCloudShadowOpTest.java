package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.datamodel.Product;
import org.junit.Before;
import org.junit.Test;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import static junit.framework.Assert.assertEquals;

/**
 * @author Tonio Fincke
 */
public class S2IdepixCloudShadowOpTest {

    private S2IdepixCloudShadowOp cloudShadowOp;

    @Before
    public void setUp() {
        cloudShadowOp = new S2IdepixCloudShadowOp();
    }

    @Test
    public void testDetermineSearchBorderRadius() {
        assertEquals(1715.605536407647, cloudShadowOp.determineSearchBorderRadius(10, 65), 1e-8);
        assertEquals(857.8027682038235, cloudShadowOp.determineSearchBorderRadius(20, 65), 1e-8);
        assertEquals(285.9342560679412, cloudShadowOp.determineSearchBorderRadius(60, 65), 1e-8);
        assertEquals(142.9671280339706, cloudShadowOp.determineSearchBorderRadius(120, 65), 1e-8);

        assertEquals(214.35935394489815, cloudShadowOp.determineSearchBorderRadius(10, 15), 1e-8);
        assertEquals(107.17967697244907, cloudShadowOp.determineSearchBorderRadius(20, 15), 1e-8);
        assertEquals(35.72655899081636, cloudShadowOp.determineSearchBorderRadius(60, 15), 1e-8);
        assertEquals(17.86327949540818, cloudShadowOp.determineSearchBorderRadius(120, 15), 1e-8);
    }

    @Test
    public void testGetRightBorderExtension() {
        assertEquals(0, cloudShadowOp.getRightBorderExtension(100, 0, 0));
        assertEquals(100, cloudShadowOp.getRightBorderExtension(100, 0, 90));
        assertEquals(100, cloudShadowOp.getRightBorderExtension(100, 90, 0));
        assertEquals(100, cloudShadowOp.getRightBorderExtension(100, 90, 180));
        assertEquals(1, cloudShadowOp.getRightBorderExtension(100, 0, 180));
        assertEquals(71, cloudShadowOp.getRightBorderExtension(100, 45, 180));
        assertEquals(35, cloudShadowOp.getRightBorderExtension(100, 10, 20));
        assertEquals(0, cloudShadowOp.getRightBorderExtension(100, 225, 315));
    }

    @Test
    public void testGetLeftBorderExtension() {
        assertEquals(0, cloudShadowOp.getLeftBorderExtension(100, 0, 0));
        assertEquals(100, cloudShadowOp.getLeftBorderExtension(100, 0, 270));
        assertEquals(100, cloudShadowOp.getLeftBorderExtension(100, 270, 0));
        assertEquals(100, cloudShadowOp.getLeftBorderExtension(100, 270, 180));
        assertEquals(0, cloudShadowOp.getLeftBorderExtension(100, 0, 180));
        assertEquals(71, cloudShadowOp.getLeftBorderExtension(100, 315, 180));
        assertEquals(35, cloudShadowOp.getLeftBorderExtension(100, 350, 340));
        assertEquals(0, cloudShadowOp.getLeftBorderExtension(100, 135, 45));
    }

    @Test
    public void testGetTopBorderExtension() {
        assertEquals(1, cloudShadowOp.getTopBorderExtension(100, 90, 90));
        assertEquals(100, cloudShadowOp.getTopBorderExtension(100, 0, 0));
        assertEquals(100, cloudShadowOp.getTopBorderExtension(100, 90, 0));
        assertEquals(100, cloudShadowOp.getTopBorderExtension(100, 0, 270));
        assertEquals(0, cloudShadowOp.getTopBorderExtension(100, 270, 180));
        assertEquals(71, cloudShadowOp.getTopBorderExtension(100, 45, 270));
        assertEquals(35, cloudShadowOp.getTopBorderExtension(100, 80, 70));
        assertEquals(0, cloudShadowOp.getTopBorderExtension(100, 235, 135));
    }

    @Test
    public void testGetBottomBorderExtension() {
        assertEquals(0, cloudShadowOp.getBottomBorderExtension(100, 90, 90));
        assertEquals(100, cloudShadowOp.getBottomBorderExtension(100, 90, 180));
        assertEquals(100, cloudShadowOp.getBottomBorderExtension(100, 180, 90));
        assertEquals(100, cloudShadowOp.getBottomBorderExtension(100, 270, 180));
        assertEquals(1, cloudShadowOp.getBottomBorderExtension(100, 0, 270));
        assertEquals(71, cloudShadowOp.getBottomBorderExtension(100, 135, 270));
        assertEquals(35, cloudShadowOp.getBottomBorderExtension(100, 100, 110));
        assertEquals(0, cloudShadowOp.getBottomBorderExtension(100, 45, 315));
    }

    @Test
    public void testDetermineSourceTileSize() {
        assertEquals(610, cloudShadowOp.determineSourceTileSize(10980, 610, 0, 0));
        assertEquals(122, cloudShadowOp.determineSourceTileSize(10980, 122, 0, 0));
        assertEquals(61, cloudShadowOp.determineSourceTileSize(10980, 61, 0, 0));
        assertEquals(61, cloudShadowOp.determineSourceTileSize(10980, 61, 61, 0));
        assertEquals(61, cloudShadowOp.determineSourceTileSize(10980, 61, 0, 61));
        assertEquals(610, cloudShadowOp.determineSourceTileSize(10980, 610, 375, 376));

        assertEquals(610, cloudShadowOp.determineSourceTileSize(1830, 610, 0, 0));
        assertEquals(183, cloudShadowOp.determineSourceTileSize(1830, 610, 610, 610));
        assertEquals(915, cloudShadowOp.determineSourceTileSize(1830, 915, 0, 0));
    }

    @Test
    public void testGetSourceRectangle_normalTargetRectangle_5_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 30, 30));
        Rectangle targetRecangle = new Rectangle(10, 10, 10, 10);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(5, 5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(10, 10, 15, 15);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_normalTargetRectangle_5_minus_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 30, 30));
        Rectangle targetRecangle = new Rectangle(10, 10, 10, 10);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(5, -5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(10, 5, 15, 15);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_normalTargetRectangle_minus_5_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 30, 30));
        Rectangle targetRecangle = new Rectangle(10, 10, 10, 10);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(-5, 5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(5, 10, 15, 15);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_normalTargetRectangle_minus_5_minus_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 30, 30));
        Rectangle targetRecangle = new Rectangle(10, 10, 10, 10);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(-5, -5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(5, 5, 15, 15);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_cutTargetRectangle_5_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 10, 10));
        Rectangle targetRecangle = new Rectangle(2, 2, 6, 6);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(5, 5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(2, 2, 8, 8);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_cutTargetRectangle_5_minus_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 10, 10));
        Rectangle targetRecangle = new Rectangle(2, 2, 6, 6);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(5, -5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(2, 0, 8, 8);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_cutTargetRectangle_minus_5_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 10, 10));
        Rectangle targetRecangle = new Rectangle(2, 2, 6, 6);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(-5, 5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(0, 2, 8, 8);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_cutTargetRectangle_minus_5_minus_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 10, 10));
        Rectangle targetRecangle = new Rectangle(2, 2, 6, 6);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(-5, -5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(0, 0, 8, 8);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

}