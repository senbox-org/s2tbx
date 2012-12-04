package org.esa.beam.dataio.s2;

import org.jdom.JDOMException;
import org.junit.Ignore;
import org.junit.Test;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Norman Fomferra
 */
@Ignore
public class Sentinel2ProductReaderTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new Sentinel2ProductReader(new Sentinel2ProductReaderPlugIn()).readProductNodes("test.j2k", null);
    }

}
