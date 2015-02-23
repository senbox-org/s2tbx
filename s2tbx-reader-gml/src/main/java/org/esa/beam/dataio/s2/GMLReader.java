package org.esa.beam.dataio.s2;

import org.geotools.GML;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by opicas-p on 20/02/2015.
 */
public class GMLReader
{
    public void parseGMLReader(InputStream in) throws IOException, ParserConfigurationException, SAXException {
        GML gml = new GML(GML.Version.GML2);
        SimpleFeatureIterator iter = gml.decodeFeatureIterator(in);

        int count = 0;
        while (iter.hasNext()) {
            SimpleFeature feature = iter.next();
            count++;
        }
    }
}
