package org.esa.s2tbx.mapper.pixels.computing;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.esa.s2tbx.mapper.common.SpectrumInput;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * For each spectrumInput class computes the location of each pixel from the sourceProduct
 *
 * @author Razvan Dumitrascu
 */

public class SpectrumClassPixelsComputing implements Runnable {

    private final SpectrumInput spectrumInput;
    private final SpectrumClassReferencePixelsContainer spectrumClassReferencePixelsContainer;
    public SpectrumClassPixelsComputing(SpectrumInput spectrumInput, SpectrumClassReferencePixelsContainer spectrumClassReferencePixelsContainer){
        this.spectrumInput = spectrumInput;
        this.spectrumClassReferencePixelsContainer = spectrumClassReferencePixelsContainer;
    }

    private void execute(){
        SpectrumClassReferencePixels spec = new SpectrumClassReferencePixels(spectrumInput.getName());
        final int length = getSpectrumClassNumElements();
        switch (length){
            case 1: {
                spec.addElements(spectrumInput.getXPixelPolygonPositions()[0], spectrumInput.getYPixelPolygonPositions()[0]);
                int minX = spectrumInput.getXPixelPolygonPositions()[0];
                int maxX = spectrumInput.getXPixelPolygonPositions()[0];
                int minY = spectrumInput.getYPixelPolygonPositions()[0];
                int maxY = spectrumInput.getYPixelPolygonPositions()[0];
                spec.setBoundingBoxValues(minX, minY, maxX, maxY);
                break;
            }
            case 2: {
                spec.addElements(spectrumInput.getXPixelPolygonPositions()[0], spectrumInput.getYPixelPolygonPositions()[0]);
                spec.addElements(spectrumInput.getXPixelPolygonPositions()[1], spectrumInput.getYPixelPolygonPositions()[1]);
                break;
            }
            default: {
                final GeometryFactory gf = new GeometryFactory();
                int minX = spectrumInput.getXPixelPolygonPositions()[0];
                int maxX = spectrumInput.getXPixelPolygonPositions()[0];
                int minY = spectrumInput.getYPixelPolygonPositions()[0];
                int maxY = spectrumInput.getYPixelPolygonPositions()[0];

                final ArrayList<Coordinate> points = new ArrayList<>();
                for(int coordinateIndex = 0; coordinateIndex < length; coordinateIndex++) {
                    int xCoordinate = spectrumInput.getXPixelPolygonPositions()[coordinateIndex];
                    int yCoordinate = spectrumInput.getYPixelPolygonPositions()[coordinateIndex];
                    if (xCoordinate > maxX) {
                        maxX = xCoordinate;
                    } else if (xCoordinate < minX) {
                        minX = xCoordinate;
                    }
                    if (yCoordinate > maxY) {
                        maxY = yCoordinate;
                    } else if (yCoordinate < minY) {
                        minY = yCoordinate;
                    }
                    points.add(new Coordinate(xCoordinate, yCoordinate));

                }
                points.add(new Coordinate(spectrumInput.getXPixelPolygonPositions()[0],  spectrumInput.getYPixelPolygonPositions()[0]));
                final Polygon polygon = gf.createPolygon(new LinearRing(new CoordinateArraySequence(points
                        .toArray(new Coordinate[points.size()])), gf), null);
                getAllPointsInPolygon(spec, polygon, new Rectangle(minX, minY, maxX-minX+1, maxY - minY+1));
                spec.setBoundingBoxValues(minX, minY, maxX, maxY);
            }
        }

       this.spectrumClassReferencePixelsContainer.addElements(spec);
    }

    private void getAllPointsInPolygon(SpectrumClassReferencePixels spec, Polygon polygon, Rectangle rectangle) {
        final GeometryFactory gf = new GeometryFactory();
        for (int y = rectangle.y; y < rectangle.y + rectangle.height; y++) {
            for (int x = rectangle.x; x < rectangle.x + rectangle.width; x++) {
                final Coordinate coord1 = new Coordinate(x, y);
                final Point point = gf.createPoint(coord1);
                if(point.within(polygon) || point.intersects(polygon)){
                    spec.addElements(x, y);
                }
            }
        }
    }

    private int getSpectrumClassNumElements() {
        int counter = 0;
        int elements = this.spectrumInput.getXPixelPolygonPositions().length;
        int[] positions = this.spectrumInput.getXPixelPolygonPositions();
        for (int elementIndex = 0; elementIndex < elements; elementIndex++) {
            if (positions[elementIndex] == -1) {
                counter++;
            }
        }
        return elements - counter;
    }

    @Override
    public void run() {
        execute();
    }
}
