package org.esa.s2tbx.dataio.s2;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Created by opicas-p on 20/02/2015.
 */
public class GMLReader
{
    public String GML = "net.opengis.gml:_int.esa.earth.atm:_int.esa.earth.hma:_int.esa.earth.ohr:_int.esa.earth.sar";

    private JAXBContext context;
    private Unmarshaller unmarshaller;

    public GMLReader() throws JAXBException {
        context = JAXBContext.newInstance(GML);
        unmarshaller = context.createUnmarshaller();
    }

    public Object readJaxbFromStreamResource(String streamResource) throws JAXBException {
        InputStream stream = getClass().getResourceAsStream(streamResource);

        Object ob =  unmarshaller.unmarshal(stream);
        Object casted = ((JAXBElement)ob).getValue();

        return casted;
    }
}
