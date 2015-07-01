package org.esa.s2tbx.dataio.s2;


import https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_datastrip_metadata.Level1C_Datastrip;
import https.psd_13_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_tile_metadata.Level1C_Tile;
import https.psd_13_sentinel2_eo_esa_int.psd.user_product_level_1c.Level1C_User_Product;
import junit.framework.Assert;
import org.esa.s2tbx.dataio.s2.l1c.L1cMetadata;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * @author opicas-p
 */
public class MetadataReaderTest {

    public Level1C_User_Product getUserProduct() throws Exception
    {
        Level1C_User_Product o = (Level1C_User_Product) readJaxbFromStreamResource("l1c/metadata/S2A_OPER_MTD_SAFL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.xml");
        return o;
    }

    public Object readJaxbFromStreamResource(String streamResource) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext
                .newInstance(S2MetadataType.L1C);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();


        InputStream stream = getClass().getResourceAsStream(streamResource);



        Object ob =  unmarshaller.unmarshal(stream);
        Object casted = ((JAXBElement)ob).getValue();

        return casted;
    }

    @Test
    public void test1() throws Exception
    {
        Level1C_User_Product o = getUserProduct();

        Assert.assertNotNull(o);
    }

    @Test
    public void test2() throws Exception
    {
        Level1C_Tile o = null;

        try {
            JAXBContext jaxbContext = JAXBContext
                    .newInstance(S2MetadataType.L1C);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Marshaller marshaller = jaxbContext.createMarshaller();

            InputStream stream = getClass().getResourceAsStream("l1c/metadata/S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLF.xml");

            Object ob =  unmarshaller.unmarshal(stream);

            o = (Level1C_Tile) ((JAXBElement)ob).getValue();


        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() throws Exception
    {
        Level1C_Datastrip o = null;

        try {
            JAXBContext jaxbContext = JAXBContext
                    .newInstance(S2MetadataType.L1C);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Marshaller marshaller = jaxbContext.createMarshaller();

            InputStream stream = getClass().getResourceAsStream("l1c/metadata/S2A_OPER_MTD_L1C_DS_CGS1_20130621T120000_S20091211T165928.xml");

            Object ob =  unmarshaller.unmarshal(stream);

            o = (Level1C_Datastrip) ((JAXBElement)ob).getValue();

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
