package org.esa.s2tbx.dataio.s2.l1c;

import org.junit.Ignore;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
@Ignore
public class Sentinel2ProductReaderMockUpTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new Sentinel2ProductReaderMockUp(new Sentinel2ProductReaderMockUpPlugIn()).readProductNodes("", null);
    }
}
