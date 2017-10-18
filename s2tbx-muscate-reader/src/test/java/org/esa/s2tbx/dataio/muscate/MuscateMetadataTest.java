package org.esa.s2tbx.dataio.muscate;

import org.esa.s2tbx.dataio.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.snap.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * Created by obarrile on 26/01/2017.
 */
public class MuscateMetadataTest {
    private MuscateMetadata metadata;

    @Before
    public void setup() {

        XmlMetadataParserFactory.registerParser(MuscateMetadata.class, new XmlMetadataParser<>(MuscateMetadata.class));
        metadata = XmlMetadata.create(MuscateMetadata.class, new File("D:/Users/obarrile/Documents/borrar/SENTINEL2A_20160205-103556-319_L2A_T31TFK_D_V1-0_MTD_ALL.xml"));
    }

    @After
    public void teardown() {
        metadata = null;
        System.gc();
    }

    @Test
    public void testGetFileName() throws Exception {
        //metadata.getImages();
        //metadata.getMasks();
        //assertEquals("DEIMOS", metadata.getFileName());
    }
}
