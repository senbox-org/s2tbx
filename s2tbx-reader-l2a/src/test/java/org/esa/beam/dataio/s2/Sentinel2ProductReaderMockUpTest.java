package org.esa.beam.dataio.s2;

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
