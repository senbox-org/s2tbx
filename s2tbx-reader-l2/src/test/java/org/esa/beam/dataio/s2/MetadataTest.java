package org.esa.beam.dataio.s2;

import _int.esa.s2.pdgs.psd.s2_pdi_level_2a_tile_metadata.Level2A_Tile;
import _int.esa.s2.pdgs.psd.s2_user_product_level_2a_metadata.Level2A_User_Product;
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

    public Level2A_User_Product getUserProduct() throws Exception
    {
        Level2A_User_Product o = null;

        JAXBContext jaxbContext = JAXBContext
                .newInstance("_int.esa.s2.pdgs.psd.user_product_level_1c:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_datastrip_metadata:_int.esa.gs2.dico._1_0.pdgs.dimap");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();


        InputStream stream = getClass().getResourceAsStream("l1c/metadata/S2A_OPER_MTD_SAFL2A_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.xml");

        Object ob =  unmarshaller.unmarshal(stream);

        o = (Level2A_User_Product) ((JAXBElement)ob).getValue();

        return o;
    }

    public Level2A_Tile getTileProduct() throws Exception
    {
        Level2A_Tile o = null;

        JAXBContext jaxbContext = JAXBContext
                .newInstance("_int.esa.s2.pdgs.psd.user_product_level_1c:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_tile_metadata:_int.esa.s2.pdgs.psd.s2_pdi_level_1c_datastrip_metadata:_int.esa.gs2.dico._1_0.pdgs.dimap");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();


        InputStream stream = getClass().getResourceAsStream("l1c/metadata/S2A_OPER_MTD_L2A_TL_CGS1_20130621T120000_A000065_T14SLF.xml");

        Object ob =  unmarshaller.unmarshal(stream);

        o = (Level2A_Tile) ((JAXBElement)ob).getValue();

        return o;
    }



    @Test
    public void test1() throws Exception
    {
        Level2A_User_Product o = getUserProduct();

        Assert.assertNotNull(o);

        L2aMetadata.ProductCharacteristics pchar = L2aMetadataProc.parseCharacteristics(o);
        assertEquals("Sentinel-2A", pchar.spacecraft);
        assertEquals("2013-06-21T12:00:00Z", pchar.datasetProductionDate);
        assertEquals("LEVEL___1_C", pchar.processingLevel);
        assertEquals(0.0000082, pchar.bandInformations[0].spectralResponseValues[1], 1e-15);
    }

    @Test
    public void test3() throws Exception
    {
        Level2A_User_Product product = getUserProduct();

        Assert.assertNotNull(product);

        Collection<String> tiles = L2aMetadataProc.getTiles(product);

        for (String granuleName: tiles)
        {
            System.err.println(granuleName);
        }
    }

    @Test
    public void testTileProductsMetadataExistence() throws Exception
    {
        Level2A_User_Product product = getUserProduct();

        Assert.assertNotNull(product);

        Collection<String> tiles = L2aMetadataProc.getTiles(product);

        URL aUrl = getClass().getResource("l1c/data/S2A_OPER_PRD_MSIL2A_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.SAFE");

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
        Level2A_Tile product = getTileProduct();

        Assert.assertNotNull(product);

        Map<Integer, L2aMetadata.TileGeometry> geoms = L2aMetadataProc.getTileGeometries(product);
    }


    @Test
    public void testPopulateOtherTileInfo() throws Exception
    {
        Level2A_Tile product = getTileProduct();

        Assert.assertNotNull(product);

        L2aMetadata.AnglesGrid sunGrid = L2aMetadataProc.getSunGrid(product);

        L2aMetadata.AnglesGrid[] otherGrid = L2aMetadataProc.getAnglesGrid(product);
    }

}
