package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.esa.snap.core.util.ShapeRasterizer;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class PotentialPathShapeRasterizer extends ShapeRasterizer {

    @Override
    public Point2D[] rasterize(Point2D[] vertices, int[] vertexIndexes) {
        if (vertices == null || vertices.length <= 1) {
            return vertices;
        }

        if (vertexIndexes != null && vertexIndexes.length < vertices.length) {
            throw new IllegalArgumentException("size of 'vertexIndexes' less than 'vertices'");
        }

        final List<Point> list = new LinkedList<Point>();
        final Point lastPoint = new Point();

        final LinePixelVisitor visitor = new LinePixelVisitor() {

            public void visit(int x, int y) {
                if (list.size() == 0 || lastPoint.x != x || lastPoint.y != y) {
                    lastPoint.x = x;
                    lastPoint.y = y;
                    list.add(new Point(lastPoint));
                }
            }
        };

        int x0 = (int) Math.round(vertices[0].getX());
        int y0 = (int) Math.round(vertices[0].getY());
        if (vertexIndexes != null) {
            vertexIndexes[0] = 0;
        }
        for (int i = 1; i < vertices.length; i++) {
            int x1 = (int) Math.round(vertices[i].getX());
            int y1 = (int) Math.round(vertices[i].getY());
            getLineRasterizer().rasterize(x0, y0, x1, y1, visitor);
            if (vertexIndexes != null) {
                vertexIndexes[i] = (list.size() > 0) ? list.size() - 1 : 0;
            }
            x0 = x1;
            y0 = y1;
        }

        return list.toArray(new Point[list.size()]);
    }
}
