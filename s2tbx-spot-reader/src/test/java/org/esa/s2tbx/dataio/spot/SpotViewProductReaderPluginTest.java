package org.esa.s2tbx.dataio.spot;

import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.snap.framework.dataio.ProductIOPlugInManager;
import org.esa.snap.framework.dataio.ProductReaderPlugIn;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * @author Ramona Manda
 */
public class SpotViewProductReaderPluginTest {
    @Test
    public void spotViewReaderIsLoaded() {
        final Iterator iterator = ProductIOPlugInManager.getInstance().getReaderPlugIns(SpotConstants.SPOTVIEW_FORMAT_NAMES[0]);
        final ProductReaderPlugIn plugIn = (ProductReaderPlugIn) iterator.next();
        assertEquals(SpotViewProductReaderPlugin.class, plugIn.getClass());
    }

    public void testGetDefaultFileExtension() {
        final SpotViewProductReaderPlugin plugIn = new SpotViewProductReaderPlugin();

        final String[] defaultFileExtensions = plugIn.getDefaultFileExtensions();
        assertEquals(".xml", defaultFileExtensions[0]);
        assertEquals(".XML", defaultFileExtensions[1]);
        assertEquals(".zip", defaultFileExtensions[2]);
        assertEquals(".ZIP", defaultFileExtensions[3]);
    }
}
