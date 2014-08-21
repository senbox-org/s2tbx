package org.esa.beam.dataio.metadata;

import com.bc.ceres.core.Assert;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.util.logging.BeamLogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Base class that encapsulates metadata read from XML file and exposes
 * helper methods for easier access of interesting metadata values.
 * @author Cosmin Cara
 */
public abstract class XmlMetadata {
    protected static final String MISSING_ELEMENT_WARNING = "Metadata: %s element is missing or has a bad value";
    protected MetadataElement rootElement;
    protected String name;
    protected int width;
    protected int height;
    protected int numBands;
    protected String path;
    protected Logger logger;

    /**
     * Factory method for creating instances of classes that (at least) extend
     * <code>XmlMetadata</code> class.
     *
     * @param clazz     The actual class of the metadata. It should extend <code>XmlMetadata</code>.
     * @param inputFile The <code>File</code> object that points to the file to be parsed.
     * @param <T>       Generic type of the metadata class.
     * @return          An instance of <code>T</code> type.
     */
    public static <T extends XmlMetadata> T create(Class<T> clazz, File inputFile) {
        Assert.notNull(inputFile);
        T result = null;
        FileInputStream stream = null;
        try {
            if (inputFile.exists()) {
                stream = new FileInputStream(inputFile);
                //noinspection unchecked
                result = (T)XmlMetadataParserFactory.getParser(clazz).parse(stream);
                result.setPath(inputFile.getPath());
                String metadataProfile = result.getMetadataProfile();
                if (metadataProfile != null)
                    result.setName(metadataProfile);
            }
        } catch (Exception e) {
            BeamLogManager.getSystemLogger().severe(e.getMessage());
        } finally {
            if (stream != null) try {
                stream.close();
            } catch (IOException e) {
                // swallowed exception
            }
        }
        return result;
    }

    /**
     * Helper method that copies the child nodes of a metadata element as child nodes
     * of another metadata element.
     * @param source    The metadata element holding the nodes to be copied
     * @param target    The destination metadata element
     */
    public static void CopyChildElements(MetadataElement source, MetadataElement target) {
        Assert.notNull(source);
        Assert.notNull(target);
        MetadataAttribute[] attributes = source.getAttributes();
        if (attributes != null) {
            for (MetadataAttribute attribute : attributes) {
                if (!target.containsAttribute(attribute.getName()) &&
                        attribute.getName().indexOf(":") == -1) {
                    target.addAttribute(attribute);
                }
            }
        }
        while (source.getNumElements() > 0) {
            MetadataElement currentElement = source.getElementAt(0);
            target.addElement(currentElement);
            source.removeElement(currentElement);
        }
    }

    /**
     * Constructs an instance of metadata class and assigns a name to the root <code>MetadataElement</code>.
     *
     *  @param name     The name of this instance, and also the initial name of the root element.
     */
    public XmlMetadata(String name) {
        this.name = name;
        this.rootElement = new MetadataElement(this.name);
        this.logger = BeamLogManager.getSystemLogger();
    }

    /**
     * Returns the root node of this metadata file.
     * @return The root metadata element
     */
    public MetadataElement getRootElement() {
        return rootElement;
    }

    /**
     * Gets the name of the metadata file used to obtain this instance.
     *
     * @return  A string representing the name of the file used to obtain the instance.
     */
    public abstract String getFileName();

    /**
     * Sets the name of the file used to obtain this instance.
     *
     * @param actualName    The name of the file
     */
    public void setFileName(String actualName) {
        name = actualName;
    }

    /**
     * Returns the number of bands of the product.
     * This getter should be overridden in all derived classes because
     * each metadata type may have a different hierarchy of nodes for
     * getting this value.
     *
     * @return  the number of bands
     */
    public abstract int getNumBands();

    /**
     * Returns the name of the product, as found in metadata.
     * This getter should be overridden in all derived classes because
     * each metadata type may have a different hierarchy of nodes for
     * getting this value.
     *
     *  @return     The name of the product
     */
    public abstract String getProductName();

    /**
     * Returns the name of the raster format (for example: TIFF, NITF, etc).
     *
     * This getter should be overridden in all derived classes because
     * each metadata type may have a different hierarchy of nodes for
     * getting this value.
     *
     * @return  The raster format name/code.
     */
    public abstract String getFormatName();

    /**
     * Returns the metadata profile (for example: L1A, etc.)
     * This getter should be overridden in all derived classes because
     * each metadata type may have a different hierarchy of nodes for
     * getting this value.
     *
     * @return  The metadata profile
     */
    public abstract String getMetadataProfile();

    /**
     * Returns the width of the product, in pixels.
     * This getter should be overridden in all derived classes because
     * each metadata type may have a different hierarchy of nodes for
     * getting this value.
     *
     * @return  The width (in pixels) of the product.
     */
    public abstract int getRasterWidth();

    /**
     * Returns the height of the product, in pixels.
     * This getter should be overridden in all derived classes because
     * each metadata type may have a different hierarchy of nodes for
     * getting this value.
     *
     * @return  The height (in pixels) of the product.
     */
    public abstract int getRasterHeight();

    /**
     * Returns the names of raster files for the product.
     * This getter should be overridden in all derived classes because
     * each metadata type may have a different hierarchy of nodes for
     * getting this value.
     *
     * @return  An array of raster file names.
     */
    public abstract String[] getRasterFileNames();

    /**
     * Returns the path of the metadata file.
     *
     * @return  The path of the metadata file.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path of the metadata file.
     *
     * @param value The path of the file.
     */
    public void setPath(String value) {
        path = value;
    }

    /**
     * Sets the name of this metadata (and also the name of the root element).
     *
     * @param value The name of the metadata file.
     */
    public void setName(String value) {
        this.name = value;
        this.rootElement.setName(value);
    }
}
