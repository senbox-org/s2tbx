package org.esa.s2tbx.dataio.s2.l1b;


import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1b_granule_metadata.Level1B_Granule;
import https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1b.Level1B_User_Product;
import junit.framework.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Created by opicas-p on 24/06/2014.
 */
public class MetadataTest1B {

    public Level1B_User_Product getUserProduct() throws Exception
    {
        Level1B_User_Product o = null;

        JAXBContext jaxbContext = JAXBContext
                .newInstance(MetadataType.L1B);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();


        InputStream stream = getClass().getResourceAsStream("S2A_OPER_MTD_SAFL1B_PDMC_20140926T120000_R069_V20130707T171925_20130707T172037.xml");

        Object ob =  unmarshaller.unmarshal(stream);

        o = (Level1B_User_Product) ((JAXBElement)ob).getValue();

        return o;
    }

    public Level1B_Granule getTileProduct() throws Exception
    {
        Level1B_Granule o = null;

        JAXBContext jaxbContext = JAXBContext
                .newInstance(MetadataType.L1B);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();


        InputStream stream = getClass().getResourceAsStream("S2A_OPER_MTD_L1B_GR_MPS__20140926T120000_S20130707T171927_D06.xml");

        Object ob =  unmarshaller.unmarshal(stream);

        o = (Level1B_Granule) ((JAXBElement)ob).getValue();

        return o;
    }

    @Test
    public void test1() throws Exception
    {
        Level1B_User_Product o = getUserProduct();
        Assert.assertNotNull(o);
    }

    @Test
    public void test2() throws Exception
    {
        Level1B_Granule o = getTileProduct();
        Assert.assertNotNull(o);
    }

}
