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
public class SpotDimapProductReaderPluginTest {
    @Test
    public void spotDimapReaderIsLoaded() {
        final Iterator iterator = ProductIOPlugInManager.getInstance().getReaderPlugIns(SpotConstants.DIMAP_FORMAT_NAMES[0]);
        final ProductReaderPlugIn plugIn = (ProductReaderPlugIn) iterator.next();
        assertEquals(SpotDimapProductReaderPlugin.class, plugIn.getClass());
    }

    public void testGetDefaultFileExtension() {
        final SpotDimapProductReaderPlugin plugIn = new SpotDimapProductReaderPlugin();

        final String[] defaultFileExtensions = plugIn.getDefaultFileExtensions();
        assertEquals(".dim", defaultFileExtensions[0]);
        assertEquals(".DIM", defaultFileExtensions[1]);
        assertEquals(".zip", defaultFileExtensions[2]);
        assertEquals(".ZIP", defaultFileExtensions[3]);
    }
}
