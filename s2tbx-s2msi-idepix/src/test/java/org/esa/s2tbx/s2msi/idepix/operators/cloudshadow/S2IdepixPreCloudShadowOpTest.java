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
public class S2IdepixPreCloudShadowOpTest {

    private S2IdepixPreCloudShadowOp cloudShadowOp;

    @Before
    public void setUp() {
        cloudShadowOp = new S2IdepixPreCloudShadowOp();
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
        Rectangle expectedSourceRectangle = new Rectangle(5, 5, 20, 20);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_normalTargetRectangle_5_minus_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 30, 30));
        Rectangle targetRecangle = new Rectangle(10, 10, 10, 10);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(5, -5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(5, 5, 20, 20);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_normalTargetRectangle_minus_5_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 30, 30));
        Rectangle targetRecangle = new Rectangle(10, 10, 10, 10);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(-5, 5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(5, 5, 20, 20);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_normalTargetRectangle_minus_5_minus_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 30, 30));
        Rectangle targetRecangle = new Rectangle(10, 10, 10, 10);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(-5, -5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(5, 5, 20, 20);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_cutTargetRectangle_5_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 10, 10));
        Rectangle targetRecangle = new Rectangle(2, 2, 6, 6);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(5, 5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(0, 0, 10, 10);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_cutTargetRectangle_5_minus_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 10, 10));
        Rectangle targetRecangle = new Rectangle(2, 2, 6, 6);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(5, -5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(0, 0, 10, 10);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_cutTargetRectangle_minus_5_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 10, 10));
        Rectangle targetRecangle = new Rectangle(2, 2, 6, 6);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(-5, 5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(0, 0, 10, 10);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

    @Test
    public void testGetSourceRectangle_cutTargetRectangle_minus_5_minus_5() {
        cloudShadowOp.setSourceProduct(new Product("dummy", "dummy", 10, 10));
        Rectangle targetRecangle = new Rectangle(2, 2, 6, 6);
        Point2D[] relativePath = new Point2D[]{new Point2D.Double(-5, -5)};
        Rectangle sourceRectangle = cloudShadowOp.getSourceRectangle(targetRecangle, relativePath);
        Rectangle expectedSourceRectangle = new Rectangle(0, 0, 10, 10);
        assertEquals(expectedSourceRectangle, sourceRectangle);
    }

}