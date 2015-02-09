package org.esa.beam.dataio.s2;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultDerivedCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.cs.DefaultCartesianCS;
import org.geotools.referencing.operation.transform.ProjectiveTransform;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.awt.geom.AffineTransform;

public class GeometryFactory {

    public static CoordinateReferenceSystem getFallbackCrs(String sourceCrsText) throws FactoryException {
        String aTargetCrsText = sourceCrsText;
        if(sourceCrsText.equals("EPSG:4326"))
        {
            // we need an alternative crs
            aTargetCrsText = "EPSG:3857";
        }

        CoordinateReferenceSystem targetCRS = CRS.decode(aTargetCrsText, true);
        return targetCRS;
    }

    public static CoordinateReferenceSystem getDefaultCrs()
    {
        return DefaultGeographicCRS.WGS84;
    }

    public static MathTransform findMathTransform(final String sourceCrsText, final String targetCrsText) throws FactoryException
    {
        CoordinateReferenceSystem sourceCRS = CRS.decode(sourceCrsText, true);
        CoordinateReferenceSystem targetCRS = getFallbackCrs(targetCrsText);
        MathTransform tr = CRS.findMathTransform(sourceCRS, targetCRS);

        return tr;
    }

    public static MathTransform findMathTransform(final CoordinateReferenceSystem sourceCRS, final String targetCrsText) throws FactoryException
    {
        CoordinateReferenceSystem targetCRS = getFallbackCrs(targetCrsText);
        MathTransform tr = CRS.findMathTransform(sourceCRS, targetCRS);

        return tr;
    }

    public static Coordinate rotate(Coordinate[] arr, Coordinate p, CoordinateReferenceSystem sourceCRS)
    {
        // todo OPP rotation test
        // todo OPP Add UT

        CoordinateReferenceSystem intermediateCRS = null;
        try {
            intermediateCRS = CRS.decode("EPSG:3857");
        } catch (FactoryException e) {
            e.printStackTrace();
        }

        double radAng = Math.atan( (arr[1].getOrdinate(1) - arr[0].getOrdinate(1)) / (arr[1].getOrdinate(0) - arr[0].getOrdinate(0)) );

        AffineTransform affineTransform = new
                AffineTransform();
        affineTransform.rotate(-radAng);
        MathTransform transform =
                ProjectiveTransform.create(affineTransform);

        DefaultCartesianCS cartesianCS =
                DefaultCartesianCS.GENERIC_2D;
        DefaultDerivedCRS derivedCRS = new
                DefaultDerivedCRS("Test",sourceCRS,transform,cartesianCS);

        Coordinate targetCoordinate = null;

        try {
            targetCoordinate = JTS.transform(p, null, CRS.findMathTransform(sourceCRS, derivedCRS));
            targetCoordinate = JTS.transform(targetCoordinate, null, CRS.findMathTransform(sourceCRS, intermediateCRS));
        } catch (TransformException e) {
            e.printStackTrace();
        } catch (FactoryException e) {
            e.printStackTrace();
        }

        return targetCoordinate;
    }
}
