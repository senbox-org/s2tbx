package org.esa.beam.dataio.s2;


import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_INFO;
import https.psd_12_sentinel2_eo_esa_int.dico._1_0.pdgs.dimap.A_PRODUCT_ORGANIZATION;
import https.psd_12_sentinel2_eo_esa_int.psd.s2_pdi_level_1c_tile_metadata.Level1C_Tile;
import https.psd_12_sentinel2_eo_esa_int.psd.user_product_level_1b.Level1B_User_Product;
import junit.framework.Assert;
import org.esa.beam.dataio.s2.filepatterns.S2L1bGranuleDirFilename;
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

    public Level1B_User_Product getUserProduct() throws Exception
    {
        Level1B_User_Product o = null;

        JAXBContext jaxbContext = JAXBContext
                .newInstance(MetadataType.L1C);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();


        InputStream stream = getClass().getResourceAsStream("l1c/metadata/S2A_OPER_MTD_SAFL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.xml");

        Object ob =  unmarshaller.unmarshal(stream);

        o = (Level1B_User_Product) ((JAXBElement)ob).getValue();

        return o;
    }

    public Level1C_Tile getTileProduct() throws Exception
    {
        Level1C_Tile o = null;

        JAXBContext jaxbContext = JAXBContext
                .newInstance(MetadataType.L1C);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();


        InputStream stream = getClass().getResourceAsStream("l1c/metadata/S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLF.xml");

        Object ob =  unmarshaller.unmarshal(stream);

        o = (Level1C_Tile) ((JAXBElement)ob).getValue();

        return o;
    }



    @Test
    public void test1() throws Exception
    {
        Level1B_User_Product o = getUserProduct();

        Assert.assertNotNull(o);

        L1bMetadata.ProductCharacteristics pchar = L1bMetadataProc.parseCharacteristics(o);
        assertEquals("Sentinel-2A", pchar.spacecraft);
        assertEquals("2013-06-21T12:00:00Z", pchar.datasetProductionDate);
        assertEquals("LEVEL___1_C", pchar.processingLevel);
        assertEquals(0.0000082, pchar.bandInformations[0].spectralResponseValues[1], 1e-15);
    }

    @Test
    public void test2() throws Exception
    {
        Level1B_User_Product product = getUserProduct();

        Assert.assertNotNull(product);

        A_PRODUCT_INFO.Product_Organisation info = product.getGeneral_Info().getProduct_Info().getProduct_Organisation();

        A_PRODUCT_ORGANIZATION.Granules granulesList = info.getGranule_List().get(0).getGranules();
        String granuleId = granulesList.getGranuleIdentifier();

        assertEquals("S2A_OPER_MSI_L1C_TL_CGS1_20130621T120000_A000065_T14SLD_N01.01", granuleId);

        S2L1bGranuleDirFilename gdir = S2L1bGranuleDirFilename.create(granuleId);

        Assert.assertEquals("S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLD.xml", gdir.getMetadataFilename().name);
    }

    @Test
    public void test3() throws Exception
    {
        Level1B_User_Product product = getUserProduct();

        Assert.assertNotNull(product);

        Collection<String> tiles = L1bMetadataProc.getTiles(product);

        for (String granuleName: tiles)
        {
            System.err.println(granuleName);
        }

    }


    @Test
    public void testTileProductsMetadataExistence() throws Exception
    {
        Level1B_User_Product product = getUserProduct();

        Assert.assertNotNull(product);

        Collection<String> tiles = L1bMetadataProc.getTiles(product);

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

                    S2L1bGranuleDirFilename aGranuleDir = S2L1bGranuleDirFilename.create(granuleName);
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
        Level1C_Tile product = getTileProduct();

        Assert.assertNotNull(product);

        Map<Integer, L1bMetadata.TileGeometry> geoms = L1bMetadataProc.getTileGeometries(product);
    }


    @Test
    public void testPopulateOtherTileInfo() throws Exception
    {
        Level1C_Tile product = getTileProduct();

        Assert.assertNotNull(product);

        L1bMetadata.AnglesGrid sunGrid = L1bMetadataProc.getSunGrid(product);

        L1bMetadata.AnglesGrid[] otherGrid = L1bMetadataProc.getAnglesGrid(product);
    }

}
