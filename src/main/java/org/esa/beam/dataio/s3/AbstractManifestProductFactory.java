package org.esa.beam.dataio.s3;/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

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

public abstract class AbstractManifestProductFactory extends AbstractProductFactory {

    private Manifest manifest;

    public AbstractManifestProductFactory(Sentinel3ProductReader productReader) {
        super(productReader);
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
            getLogger().log(Level.SEVERE, msg, e);
            throw new IOException(msg, e);
        } catch (ParserConfigurationException e) {
            getLogger().log(Level.SEVERE, msg, e);
            throw new IOException(msg, e);
        }
    }


}
