package org.esa.s2tbx.dataio.s2;

import _int.esa.earth.hma.MaskType;
import junit.framework.Assert;
import net.opengis.gml.LinearRingType;
import net.opengis.gml.PolygonType;
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

    public MaskType getMask(String uri) throws Exception
    {
        GMLReader gr = new GMLReader();
        MaskType o = (MaskType) gr.readJaxbFromStreamResource(uri);
        return o;
    }

    @Test
    public void testDefect() throws Exception
    {
        MaskType mat = getMask("l1c/gml/S2A_OPER_MSK_DEFECT_MPS__20140915T120000_A000069_T14RNV_B01_MSIL1C.gml");

        Assert.assertEquals("S2A_OPER_MSK_DEFECT_GPPL1C_069_20130707171925_20130707172037_00_000000_14RNV_0001", mat.getId());

        Assert.assertNotNull(mat);

        // 524203.159674621 3413026.34706561 523448.820731278 3409605.63068322 523328.496255698 3409033.32495276 521398.219557571 3400290.83751075 521112.934490422 3398957.02512509 520363.957736875 3395534.24603791 519750.693774946 3392677.42764802 519620.993230712 3392108.67505505 519205.628363814 3390206.35476628 519226.52626814 3390202.59397566 519641.654838131 3392105.0032592 519771.516188512 3392673.69584772 520384.860412706 3395530.48563135 521133.906044866 3398953.24028254 521418.889258897 3400287.1660577 523177.632655826 3408270.72090761 523350.187899497 3409029.27469735 523469.639446139 3409601.90666031 524223.848408108 3413022.6726559 524203.159674621 3413026.34706561</gml:posList>

        PolygonType part = (PolygonType) mat.getMaskMembers().getMaskFeature().get(0).getExtentOf().get_Surface().getValue();
        LinearRingType ring = (LinearRingType) part.getExterior().get_Ring().getValue();
        Double coo = ring.getPosList().getValue().get(0);
        Assert.assertEquals(524203.159674621, coo);
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
