package org.esa.beam.dataio.s2;

import _int.esa.earth.hma.MaskType;
import junit.framework.Assert;
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

    public String GML = "net.opengis.gml:_int.esa.earth.atm:_int.esa.earth.hma:_int.esa.earth.ohr:_int.esa.earth.sar";

    public Object readJaxbFromStreamResource(String streamResource) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext
                .newInstance(GML);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        InputStream stream = getClass().getResourceAsStream(streamResource);

        Object ob =  unmarshaller.unmarshal(stream);
        Object casted = ((JAXBElement)ob).getValue();

        return casted;
    }

    public InputStream getStream() throws Exception
    {
        InputStream stream = getClass().getResourceAsStream("l1c/gml/S2A_OPER_MSK_DEFECT_MPS__20140915T120000_A000069_T14RNV_B01_MSIL1C.gml");
        return stream;
    }

    public MaskType getMask() throws Exception
    {
        MaskType o = (MaskType) readJaxbFromStreamResource("l1c/gml/S2A_OPER_MSK_DEFECT_MPS__20140915T120000_A000069_T14RNV_B01_MSIL1C.gml");
        return o;
    }

    public MaskType getMask(String uri) throws Exception
    {
        MaskType o = (MaskType) readJaxbFromStreamResource(uri);
        return o;
    }

    @Test
    public void testDefect() throws Exception
    {
        MaskType mat = getMask("l1c/gml/S2A_OPER_MSK_DEFECT_MPS__20140915T120000_A000069_T14RNV_B01_MSIL1C.gml");

        Assert.assertEquals("S2A_OPER_MSK_DEFECT_GPPL1C_069_20130707171925_20130707172037_00_000000_14RNV_0001", mat.getId());

        Assert.assertNotNull(mat);
    }

    @Test
    public void testClouds() throws Exception
    {
        MaskType mat = getMask("l1c/gml/S2A_OPER_MSK_CLOUDS_MPS__20140915T120000_A000069_T14RNV_B00_MSIL1C.gml");

        Assert.assertEquals("S2A_OPER_MSK_CLOUDS_GPPL1C_069_20130707171925_20130707172037_000000_14RNV_0001", mat.getId());

        Assert.assertNotNull(mat);
    }

    @Test
    public void testDemqua() throws Exception
    {
        MaskType mat = getMask("l1c/gml/S2A_OPER_MSK_DEMQUA_MPS__20140915T120000_A000069_T14RNV_B00_MSIL1C.gml");

        Assert.assertEquals("S2A_OPER_MSK_DEMQUA_GPPL1C_069_20130707171925_20130707172037_ZZ_000000_14RNV_0001", mat.getId());

        Assert.assertNotNull(mat);
    }

    @Test
    public void testDeftoo() throws Exception
    {
        MaskType mat = getMask("l1c/gml/S2A_OPER_MSK_DETFOO_MPS__20140915T120000_A000069_T14RNV_B01_MSIL1C.gml");

        Assert.assertEquals("S2A_OPER_MSK_DETFOO_GPPL1C_069_20130707171925_20130707172037_00_000000_14RNV_0001", mat.getId());

        Assert.assertNotNull(mat);
    }

    @Test
    public void testLanwat() throws Exception
    {
        MaskType mat = getMask("l1c/gml/S2A_OPER_MSK_LANWAT_MPS__20140915T120000_A000069_T14RNV_B00_MSIL1C.gml");

        Assert.assertEquals("S2A_OPER_MSK_LANWAT_GPPL1C_069_20130707171925_20130707172037_000000_14RNV_0001", mat.getId());

        Assert.assertNotNull(mat);
    }

    @Test
    public void testNodata() throws Exception
    {
        MaskType mat = getMask("l1c/gml/S2A_OPER_MSK_NODATA_MPS__20140915T120000_A000069_T14RNV_B01_MSIL1C.gml");

        Assert.assertEquals("S2A_OPER_MSK_NODATA_GPPL1C_069_20130707171925_20130707172037_00_000000_14RNV_0001", mat.getId());

        Assert.assertNotNull(mat);
    }

    @Test
    public void testSatura() throws Exception
    {
        MaskType mat = getMask("l1c/gml/S2A_OPER_MSK_SATURA_MPS__20140915T120000_A000069_T14RNV_B01_MSIL1C.gml");

        Assert.assertEquals("S2A_OPER_MSK_SATURA_GPPL1C_069_20130707171925_20130707172037_00_000000_14RNV_0001", mat.getId());

        Assert.assertNotNull(mat);
    }

    @Test
    public void testTecqua() throws Exception
    {
        MaskType mat = getMask("l1c/gml/S2A_OPER_MSK_TECQUA_MPS__20140915T120000_A000069_T14RNV_B01_MSIL1C.gml");

        Assert.assertEquals("S2A_OPER_MSK_TECQUA_GPPL1C_069_20130707171925_20130707172037_00_000000_14RNV_0001", mat.getId());

        Assert.assertNotNull(mat);
    }
}
