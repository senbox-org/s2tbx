package org.esa.beam.dataio.s2;

import _int.esa.s2.pdgs.psd.s2_pdi_level_2a_datastrip_structure.Level2A_Datastrip;
import _int.esa.s2.pdgs.psd.s2_pdi_level_2a_tile_metadata.Level2A_Tile;
import _int.esa.s2.pdgs.psd.s2_user_product_level_2a_metadata.Level2A_User_Product;
import junit.framework.Assert;
import org.junit.Test;

import javax.xml.bind.*;
import java.io.InputStream;

/**
 * Created by opicas-p on 24/06/2014.
 */
public class MetadataReaderTest {

    public Level2A_User_Product getUserProduct() throws Exception
    {
        Level2A_User_Product o = (Level2A_User_Product) readJaxbFromStreamResource("l1c/metadata/S2A_OPER_MTD_SAFL2A_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.xml");
        return o;
    }

    public Object readJaxbFromStreamResource(String streamResource) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext
                .newInstance("_int.esa.s2.pdgs.psd.user_product_level_1c:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_datastrip_metadata:_int.esa.gs2.dico._1_0.pdgs.dimap");
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
        Level2A_User_Product o = getUserProduct();

        Assert.assertNotNull(o);
    }

    @Test
    public void test2() throws Exception
    {
        Level2A_Tile o = null;

        try {
            JAXBContext jaxbContext = JAXBContext
                    .newInstance("_int.esa.s2.pdgs.psd.user_product_level_1c:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_datastrip_metadata:_int.esa.gs2.dico._1_0.pdgs.dimap");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Marshaller marshaller = jaxbContext.createMarshaller();

            InputStream stream = getClass().getResourceAsStream("l1c/metadata/S2A_OPER_MTD_L2A_TL_CGS1_20130621T120000_A000065_T14SLF.xml");

            Object ob =  unmarshaller.unmarshal(stream);

            o = (Level2A_Tile) ((JAXBElement)ob).getValue();


        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() throws Exception
    {
        Level2A_Datastrip o = null;

        try {
            JAXBContext jaxbContext = JAXBContext
                    .newInstance("_int.esa.s2.pdgs.psd.user_product_level_1c:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_datastrip_metadata:_int.esa.gs2.dico._1_0.pdgs.dimap");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Marshaller marshaller = jaxbContext.createMarshaller();

            InputStream stream = getClass().getResourceAsStream("l1c/metadata/S2A_OPER_MTD_L2A_DS_CGS1_20130621T120000_S20091211T165928.xml");

            Object ob =  unmarshaller.unmarshal(stream);

            o = (Level2A_Datastrip) ((JAXBElement)ob).getValue();

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
