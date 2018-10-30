package org.esa.s2tbx.dataio.vectorformats.kml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.bc.ceres.core.ProgressMonitor;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.commons.io.IOUtils;
import org.esa.snap.core.datamodel.PlainFeatureFactory;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.util.Debug;
import org.esa.snap.core.util.FeatureUtils;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureTypes;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotools.geometry.jts.JTS;
import org.geotools.kml.v22.KML;
import org.geotools.kml.v22.KMLConfiguration;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.FeatureTypeStyleImpl;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.xml.Encoder;
import org.geotools.xml.PullParser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.xml.sax.SAXException;
import org.geotools.styling.FeatureTypeStyle;

import javax.xml.stream.XMLStreamException;

import static org.esa.snap.core.util.FeatureUtils.createGeoBoundaryPolygon;
import static org.esa.snap.core.util.FeatureUtils.getTransform;
import static org.esa.snap.rcp.layermanager.layersrc.shapefile.SLDUtils.createStyledFeature;
import static org.esa.snap.rcp.layermanager.layersrc.shapefile.SLDUtils.isFeatureTypeStyleActive;
import static org.esa.snap.rcp.layermanager.layersrc.shapefile.SLDUtils.processRules;

/**
 * Utilities to convert kml to features and back (taken from geotools testcases).
 *
 * @author Razvan Dumitrascu
 */
public class KmlUtils {

    private static final StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);

    /**
     * Transform a kml file in a {@link FeatureCollection}.
     *
     * @param kml the file to convert.
     * @return the generated feature collection.
     * @throws IOException IOException
     */
    public static DefaultFeatureCollection kmlFile2FeatureCollection(File kml, Product product, ProgressMonitor pm)  throws IOException {
        pm.beginTask("Loading KML schema", 100);
        CoordinateReferenceSystem targetCrs = product.getSceneCRS();
        Geometry clipGeometry = createGeoBoundaryPolygon(product);

        try(InputStream inputStream =  new FileInputStream(kml)) {
            PullParser parser = new PullParser(new KMLConfiguration(), inputStream, KML.Placemark);
            DefaultFeatureCollection newCollection = new DefaultFeatureCollection();

            int index = 0;
            SimpleFeature simpleFeature;
            DefaultGeographicCRS crs = DefaultGeographicCRS.WGS84;
            SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
            b.setName(kml.getName());
            b.setCRS(crs);
            b.add("name", String.class);
            b.add("the_geom", Geometry.class);
            SimpleFeatureType type = b.buildFeatureType();

            if (targetCrs == null) {
                targetCrs = crs;
            }
            try {
                GeometryCoordinateSequenceTransformer clip2SourceTransformer = getTransform(crs, type.getCoordinateReferenceSystem());
                clipGeometry = clip2SourceTransformer.transform(clipGeometry);
            } catch (TransformException e) {
                throw new IllegalStateException(e);
            }
            try {
                type = FeatureTypes.transform(type, crs);
            } catch (SchemaException e) {
                throw new IllegalStateException(e);
            }

            GeometryCoordinateSequenceTransformer source2TargetTransformer;
            SimpleFeatureType targetSchema;

            try {
                targetSchema = FeatureTypes.transform(type, targetCrs);
                targetSchema.getUserData().putAll(type.getUserData());
                source2TargetTransformer = getTransform(crs, targetCrs);
            } catch (SchemaException e) {
                throw new IllegalStateException(e);
            }
            SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);

            try {
                while ((simpleFeature = (SimpleFeature) parser.parse()) != null) {
                    Geometry geometry = (Geometry) simpleFeature.getDefaultGeometry();
                    Geometry clippedSourceGeometry = getClippedGeometry(geometry, clipGeometry);

                    Object nameAttribute = null;
                    try {
                        nameAttribute = simpleFeature.getAttribute("name");
                    } catch (Exception e) {
                        // ignore name attribute
                    }
                    if (!clippedSourceGeometry.isEmpty()) {
                        builder.addAll(new Object[]{nameAttribute, clippedSourceGeometry});
                        //SimpleFeature feature = builder.buildFeature(type.getTypeName() + "." + index++);
                        SimpleFeature targetFeature = createTargetFeature(clippedSourceGeometry, targetSchema,
                                simpleFeature, source2TargetTransformer);
                        if (targetFeature != null) {
                            newCollection.add(targetFeature);
                        }
                    }

                }

            } catch (XMLStreamException | SAXException e) {
                e.printStackTrace();
            }
            pm.worked(100);
            return newCollection;
        } finally {
            pm.done();
        }
    }

    private static SimpleFeature createTargetFeature(Geometry sourceGeometry, SimpleFeatureType targetSchema,
                                                     SimpleFeature sourceFeature,
                                                     GeometryCoordinateSequenceTransformer source2TargetTransformer) {
        SimpleFeature targetFeature;
        if (source2TargetTransformer != null) {
            Geometry targetGeometry;
            try {
                targetGeometry = source2TargetTransformer.transform(sourceGeometry);
            } catch (Exception e) {
                Debug.trace(e);
                return null;
            }
            targetFeature = SimpleFeatureBuilder.retype(sourceFeature, targetSchema);
            targetFeature.setDefaultGeometry(targetGeometry);
        } else {
            targetFeature = SimpleFeatureBuilder.copy(sourceFeature);
            targetFeature.setDefaultGeometry(sourceGeometry);
        }

        return targetFeature;
    }
    /**
     * Writes the {@link FeatureCollection} to disk in KML format.
     *
     * @param kmlFile the file to write.
     * @param featureCollection the collection to transform.
     * @throws Exception Exception
     */
    public static void writeKml( File kmlFile, FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection ) throws Exception {

        CoordinateReferenceSystem epsg4326 = DefaultGeographicCRS.WGS84;
        CoordinateReferenceSystem crs = featureCollection.getSchema().getCoordinateReferenceSystem();
        MathTransform mtrans = CRS.findMathTransform(crs, epsg4326, true);

        DefaultFeatureCollection newCollection = new DefaultFeatureCollection();
        FeatureIterator<SimpleFeature> featuresIterator = featureCollection.features();
        while( featuresIterator.hasNext() ) {
            SimpleFeature f = featuresIterator.next();
            Geometry g = (Geometry) f.getDefaultGeometry();
            if (!mtrans.isIdentity()) {
                g = JTS.transform(g, mtrans);
            }
            f.setDefaultGeometry(g);
            newCollection.add(f);
        }

        OutputStream fos = null;
        try {
            if (kmlFile.getName().toLowerCase().endsWith("kmz")) {
                String fileName = kmlFile.getName();
                String entryName = fileName.replace("kmz", "kml");

                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(kmlFile));
                zos.putNextEntry(new ZipEntry(entryName)); //$NON-NLS-1$
                fos = zos;
            } else {
                fos = new FileOutputStream(kmlFile);
            }

            Encoder encoder = new Encoder(new KMLConfiguration());
            encoder.setIndenting(true);

            encoder.encode(newCollection, KML.kml, fos);

        } finally {
            if (fos != null) {
                fos.close();
                fos = null;
            }
        }
    }

    public static FeatureTypeStyle[] loadStyleFromKmlFile(File kml) throws IOException {
        List<FeatureTypeStyle> futureTypeStyleList = new ArrayList<>();
        try(InputStream inputStream =  new FileInputStream(kml)) {
            PullParser parser = new PullParser(new KMLConfiguration(), inputStream, KML.Style);
            FeatureTypeStyle f = null;

            while ((f = (FeatureTypeStyle) parser.parse()) != null) {
                FeatureTypeStyle origFts = f;
                futureTypeStyleList.add(origFts);
            }
        } catch (SAXException | XMLStreamException e) {
            e.printStackTrace();
        }
        return futureTypeStyleList.toArray(new FeatureTypeStyle[futureTypeStyleList.size()]);
    }

    public static void applyStyle(Style[] style, FeatureTypeStyle[] featureTypeStyle, String defaultCss, DefaultFeatureCollection featureCollection, DefaultFeatureCollection styledCollection) {
        List<FeatureTypeStyle> featureTypeStyles = new ArrayList<>();
        if (featureTypeStyle.length > 0) {
           featureTypeStyles.addAll(Arrays.asList(featureTypeStyle));
        }
        if (style.length > 0) {
            for(Style stl : style) {
                featureTypeStyles.addAll(stl.featureTypeStyles());
            }
        }
        SimpleFeatureType featureType = featureCollection.getSchema();
        SimpleFeatureType styledFeatureType = styledCollection.getSchema();

        List<SimpleFeature> featuresToStyle = new ArrayList<>(featureCollection.size());
        try (FeatureIterator<SimpleFeature> iterator = featureCollection.features()) {
            while (iterator.hasNext()) {
                featuresToStyle.add(iterator.next());
            }
        }
        for (FeatureTypeStyle fts : featureTypeStyles) {
            if (isFeatureTypeStyleActive(featureType, fts)) {
                List<Rule> ruleList = new ArrayList<>();
                List<Rule> elseRuleList = new ArrayList<>();
                for (Rule rule : fts.rules()) {
                    if (rule.isElseFilter()) {
                        elseRuleList.add(rule);
                    } else {
                        ruleList.add(rule);
                    }
                }
                Iterator<SimpleFeature> featureIterator = featuresToStyle.iterator();
                while (featureIterator.hasNext()) {
                    SimpleFeature simpleFeature = featureIterator.next();
                    SimpleFeature styledFeature = processRules(simpleFeature, styledFeatureType, ruleList,
                            elseRuleList);
                    if (styledFeature != null) {
                        styledCollection.add(styledFeature);
                        featureIterator.remove();
                    }
                }
            }
        }
        styledCollection.addAll(featuresToStyle.stream().map(simpleFeature -> createStyledFeature(styledFeatureType, simpleFeature, defaultCss)).collect(Collectors.toList()));
    }

    public static Style[] loadSLD(File kmlFile) {
            File sld = getSLDFile(kmlFile);
            if (sld.exists()) {
                return createFromSLD(sld);
            } else {
                return new Style[0];
            }
        }

    private static File getSLDFile(File kmlFile) {
        String filename = kmlFile.getAbsolutePath();
        if (filename.endsWith(".kml") || filename.endsWith(".kmz")) {
            filename = filename.substring(0, filename.length() - 4);
            filename += ".sld";
        } else if (filename.endsWith(".KML") || filename.endsWith(".KMZ")) {
            filename = filename.substring(0, filename.length() - 4);
            filename += ".SLD";
        }
        return new File(filename);
    }

    private static Style[] createFromSLD(File sld) {
        try {
            SLDParser stylereader = new SLDParser(styleFactory, sld.toURI().toURL());
            return stylereader.readXML();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Style[0];
    }

    public static SimpleFeatureType createStyledFeatureType(SimpleFeatureType schema) {
        SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.init(schema);
        sftb.add(PlainFeatureFactory.ATTRIB_NAME_STYLE_CSS, String.class);
        return sftb.buildFeatureType();
    }

    private static Geometry getClippedGeometry(Geometry sourceGeometry, Geometry clipGeometry) {
        Geometry targetGeometry = sourceGeometry.intersection(clipGeometry);
        if (targetGeometry instanceof Polygon) {
            final GeometryFactory geometryFactory = new GeometryFactory();
            if (MultiPolygon.class.isAssignableFrom(sourceGeometry.getClass())) {
                targetGeometry = geometryFactory.createMultiPolygon(new Polygon[]{(Polygon) targetGeometry});
            }
        }
        return targetGeometry;
    }

}

