package org.esa.s2tbx.dataio.metadata;

import com.bc.ceres.core.Assert;
import org.esa.snap.core.metadata.XmlMetadataParser;
import org.esa.snap.core.metadata.GenericXmlMetadata;
import org.esa.snap.core.datamodel.MetadataElement;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Simple XML metadata container.
 */
public class PlainXmlMetadata extends GenericXmlMetadata {

    public static MetadataElement parse(Path inputFile, Set<String> exclusions) {
        Assert.notNull(inputFile);
        MetadataElement result = null;
        InputStream stream = null;
        try {
            if (Files.exists(inputFile)) {
                XmlMetadataParser<PlainXmlMetadata> parser = new XmlMetadataParser<>(PlainXmlMetadata.class);
                result = parser.parse(inputFile, exclusions);
            }
        } catch (Exception e) {
            Logger.getLogger(GenericXmlMetadata.class.getName()).severe(e.getMessage());
        }
        return result;
    }

    public PlainXmlMetadata(String name) {
        super(name);
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public String getMetadataProfile() {
        return null;
    }
}
