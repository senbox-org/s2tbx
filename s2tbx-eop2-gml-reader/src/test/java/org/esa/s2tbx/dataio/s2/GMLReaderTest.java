package org.esa.s2tbx.dataio.s2;

import junit.framework.Assert;
import net.opengis.gml.v_3_2_1.LinearRingType;
import net.opengis.gml.v_3_2_1.PolygonType;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Created by opicas-p on 20/02/2015.
 */
public class GMLReaderTest {

    public Object readJaxbFromStreamResource(String streamResource) throws JAXBException {
        String GML = "net.opengis.gml:_int.esa.earth.atm:_int.esa.earth.hma:_int.esa.earth.ohr:_int.esa.earth.sar";
        JAXBContext jaxbContext = JAXBContext
                .newInstance(GML);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        InputStream stream = getClass().getResourceAsStream(streamResource);

        Object ob =  unmarshaller.unmarshal(stream);
        Object casted = ((JAXBElement)ob).getValue();

        return casted;
    }

    public Object getMask(String uri) throws Exception
    {
        GMLReader gr = new GMLReader();
        Object o = gr.readJaxbFromStreamResource(uri);
        return o;
    }

    @Test
    public void testDefect() throws Exception
    {
        Object mat = getMask("l1c/gml-eop/polygon.xml");

        Assert.assertNotNull(mat);
    }

    @Test
    public void testPolygon() throws Exception
    {
        Object mat = getMask("l1c/gml-eop/polygon.xml");

        Assert.assertNotNull(mat);

        PolygonType potype = (PolygonType) mat;
        LinearRingType ring = (LinearRingType) potype.getExterior().getAbstractRing().getValue();
        Double coo = ring.getPosList().getValue().get(0);
        Assert.assertEquals(20.166667, coo);
    }


}
