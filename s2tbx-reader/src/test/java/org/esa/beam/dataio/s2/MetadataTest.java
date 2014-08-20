package org.esa.beam.dataio.s2;

import _int.esa.gs2.dico._1_0.pdgs.dimap.APRODUCTINFO;
import _int.esa.gs2.dico._1_0.pdgs.dimap.APRODUCTORGANIZATION;
import _int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata.Level1CTile;
import _int.esa.s2.pdgs.psd.user_product_level_1c.Level1CUserProduct;
import junit.framework.Assert;
import org.esa.beam.dataio.s2.filepatterns.S2GranuleDirFilename;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by opicas-p on 24/06/2014.
 */
public class MetadataTest {

    public Level1CUserProduct getUserProduct() throws Exception
    {
        Level1CUserProduct o = null;

        JAXBContext jaxbContext = JAXBContext
                .newInstance("_int.esa.s2.pdgs.psd.user_product_level_1c:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_datastrip_metadata:_int.esa.gs2.dico._1_0.pdgs.dimap");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();


        InputStream stream = getClass().getResourceAsStream("l1c/metadata/S2A_OPER_MTD_SAFL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.xml");

        Object ob =  unmarshaller.unmarshal(stream);

        o = (Level1CUserProduct) ((JAXBElement)ob).getValue();

        return o;
    }

    public Level1CTile getTileProduct() throws Exception
    {
        Level1CTile o = null;

        JAXBContext jaxbContext = JAXBContext
                .newInstance("_int.esa.s2.pdgs.psd.user_product_level_1c:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_datastrip_metadata:_int.esa.gs2.dico._1_0.pdgs.dimap");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();


        InputStream stream = getClass().getResourceAsStream("l1c/metadata/S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLF.xml");

        Object ob =  unmarshaller.unmarshal(stream);

        o = (Level1CTile) ((JAXBElement)ob).getValue();

        return o;
    }



    @Test
    public void test1() throws Exception
    {
        Level1CUserProduct o = getUserProduct();

        Assert.assertNotNull(o);

        L1cMetadata.ProductCharacteristics pchar = L1cMetadataProc.parseCharacteristics(o);
        assertEquals("Sentinel-2A", pchar.spacecraft);
        assertEquals("2013-06-21T12:00:00Z", pchar.datasetProductionDate);
        assertEquals("LEVEL_1_C", pchar.processingLevel);
        assertEquals(0.0000082, pchar.bandInformations[0].spectralResponseValues[1], 1e-15);
    }

    @Test
    public void test2() throws Exception
    {
        Level1CUserProduct product = getUserProduct();

        Assert.assertNotNull(product);

        APRODUCTINFO.ProductOrganisation info = product.getGeneralInfo().getProductInfo().getProductOrganisation();

        APRODUCTORGANIZATION.Granules beautyQueen = info.getGranuleList().get(0).getGranules();
        String fallApart = beautyQueen.getGranuleIdentifier();

        assertEquals("S2A_OPER_MSI_L1C_TL_CGS1_20130621T120000_A000065_T14SLD_N01.01", fallApart);

        S2GranuleDirFilename gdir = S2GranuleDirFilename.create(fallApart);

        Assert.assertEquals("S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLD.xml", gdir.getMetadataFilename().name);
    }

    @Test
    public void test3() throws Exception
    {
        Level1CUserProduct product = getUserProduct();

        Assert.assertNotNull(product);

        Collection<String> tiles = L1cMetadataProc.getTiles(product);

        for (String granuleName: tiles)
        {
            System.err.println(granuleName);
        }

    }


    @Test
    public void testTileProductsMetadataExistence() throws Exception
    {
        Level1CUserProduct product = getUserProduct();

        Assert.assertNotNull(product);

        Collection<String> tiles = L1cMetadataProc.getTiles(product);

        URL aUrl = getClass().getResource("l1c/data/S2A_OPER_PRD_MSIL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.SAFE");

        if(aUrl != null)
        {
            File baseDir = new File(aUrl.toURI());
            if(baseDir.exists())
            {
                assertTrue(baseDir.exists());
                assertTrue(baseDir.isDirectory());

                for (String granuleName: tiles)
                {
                    File nestedMetadata = new File(baseDir, "GRANULE\\" + granuleName);
                    System.err.println(nestedMetadata.getAbsolutePath());
                    assertTrue(nestedMetadata.exists());
                    assertTrue(nestedMetadata.isDirectory());

                    S2GranuleDirFilename aGranuleDir = S2GranuleDirFilename.create(granuleName);
                    String theName = aGranuleDir.getMetadataFilename().name;

                    File nestedGranuleMetadata = new File(baseDir, "GRANULE\\" + granuleName + "\\" + theName);
                    assertTrue(nestedGranuleMetadata.exists());
                    assertTrue(nestedGranuleMetadata.isFile());
                }
            }
        }
    }


    @Test
    public void testPopulateTileInfo() throws Exception
    {
        Level1CTile product = getTileProduct();

        Assert.assertNotNull(product);

        Map<Integer, L1cMetadata.TileGeometry> geoms = L1cMetadataProc.getTileGeometries(product);
    }


    @Test
    public void testPopulateOtherTileInfo() throws Exception
    {
        Level1CTile product = getTileProduct();

        Assert.assertNotNull(product);

        L1cMetadata.AnglesGrid sunGrid = L1cMetadataProc.getSunGrid(product);

        L1cMetadata.AnglesGrid[] otherGrid = L1cMetadataProc.getAnglesGrid(product);
    }

}
