package org.esa.beam.dataio.s3.manifest;

import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Product;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

public abstract class ManifestProductReader extends XProductReader {

    private Manifest manifest;

    protected ManifestProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
    }

    @Override
    protected final List<String> getFileNames() throws IOException {
        manifest = createManifest(getInputFile());
        return getFileNames(manifest);
    }

    protected abstract List<String> getFileNames(Manifest manifest);

    @Override
    protected void setTimes(Product targetProduct) {
        super.setTimes(targetProduct);
        if (targetProduct.getStartTime() == null) {
            targetProduct.setStartTime(manifest.getStartTime());
        }
        if (targetProduct.getEndTime() == null) {
            targetProduct.setEndTime(manifest.getStopTime());
        }
    }

    private Manifest createManifest(File file) throws IOException {
        final InputStream inputStream = new FileInputStream(file);
        try {
            return Manifest.createManifest(createXmlDocument(inputStream));
        } finally {
            inputStream.close();
        }
    }

    private Document createXmlDocument(InputStream inputStream) throws IOException {
        final String msg = "Cannot create document from manifest XML file.";

        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        } catch (SAXException e) {
            logger.log(Level.SEVERE, msg, e);
            throw new IOException(msg, e);
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, msg, e);
            throw new IOException(msg, e);
        }
    }
}
