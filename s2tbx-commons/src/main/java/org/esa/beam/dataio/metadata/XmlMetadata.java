package org.esa.beam.dataio.metadata;

import com.bc.ceres.core.Assert;
import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.logging.BeamLogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Base class that encapsulates metadata read from XML file and exposes
 * helper methods for easier access of interesting metadata values.
 * @author Cosmin Cara
 */
public abstract class XmlMetadata {
    protected static final String MISSING_ELEMENT_WARNING = "Metadata: [%s] element is missing or has a bad value";
    protected static final String NO_SUCH_PATH_WARNING = "Metadata: the path to element [%s] does not exist";
    protected final MetadataElement rootElement;
    protected String name;
    protected int width;
    protected int height;
    protected int numBands;
    protected String path;
    protected Logger logger;

    protected Map<String, List<MetadataAttribute>> attributeMap;

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
                if (!target.containsAttribute(attribute.getName()) && !attribute.getName().contains(":")) {
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
        this.attributeMap = new HashMap<>();
    }

    /**
     * Returns the root node of this metadata file.
     * @return The root metadata element
     */
    public MetadataElement getRootElement() { return rootElement; }

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

    /**
     * Returns the value of the attribute (or the default value) specified by its XPath expression.
     * @param attributePath the XPath location of the attribute
     * @param defaultValue  the default value if the attribute is not found or has a <code>null</code> value
     * @return  The attribute (or default) value
     */
    public String getAttributeValue(String attributePath, String defaultValue) {
        return getAttributeValue(attributePath, 0, defaultValue);
    }

    /**
     * Returns the value of the attribute (or the default value) specified by its XPath expression and the given index,
     * when multiple such attributes are present at the same XPath location.
     * @param attributePath the XPath location of the attribute
     * @param attributeIndex    the index of the attribute in the attribute array
     * @param defaultValue  the default value if the attribute is not found or has a <code>null</code> value
     * @return  The attribute (or default) value
     */
    public String getAttributeValue(String attributePath, int attributeIndex, String defaultValue) {
        String value = defaultValue;
        if (attributePath != null) {
            attributePath = ensureAttributeTagPresent(attributePath);
            List<MetadataAttribute> attributes;
            if (attributeMap.containsKey(attributePath) && (attributes = attributeMap.get(attributePath)) != null && attributes.size() > attributeIndex) {
                value = attributes.get(attributeIndex).getData().getElemString();
                if (value == null) {
                    value = defaultValue;
                    warn(MISSING_ELEMENT_WARNING, attributes.get(attributeIndex).getName());
                }
            } else {
                warn(NO_SUCH_PATH_WARNING, attributePath);
            }
        }
        return value;
    }

    /**
     * Returns the value of the attribute (or the default value) specified by its XPath expression, and whose sibling value is equal to a certain value.
     * @param attributePath The path of the attribute to be tested
     * @param attributeValue    The test value
     * @param siblingPath   The path of the attribute whose value we want to retrieve
     * @param defaultSiblingValue   The default value if the attribute does not exist or the condition of the sibling value is not met.
     * @return  The attribute (or default) value.
     */
    public String getAttributeSiblingValue(String attributePath, String attributeValue, String siblingPath, String defaultSiblingValue) {
        String value = defaultSiblingValue;
        if (attributePath != null && siblingPath != null && attributeValue != null) {
            attributePath = ensureAttributeTagPresent(attributePath);
            List<MetadataAttribute> attributes;
            if (attributeMap.containsKey(attributePath) && (attributes = attributeMap.get(attributePath)) != null && attributes.size() > 0) {
                int foundIndex = -1;
                for (int i = 0; i < attributes.size(); i++) {
                    if (attributeValue.equalsIgnoreCase(attributes.get(i).getData().getElemString())) {
                        foundIndex = i;
                        break;
                    }
                }
                if (foundIndex != -1) {
                    value = getAttributeValue(siblingPath, foundIndex, null);
                    if (value == null) {
                        value = defaultSiblingValue;
                    }
                }
            } else {
                warn(NO_SUCH_PATH_WARNING, attributePath);
            }
        }
        return value;
    }

    /**
     * Returns the product start time in UTC format.
     * This method has to be implemented by subclasses since the value may be or may not be present in metadata,
     * or may be in different formats for different products.
     * @return  the product start time
     */
    public abstract ProductData.UTC getProductStartTime();

    /**
     * Returns the product end time in UTC format.
     * This method has to be implemented by subclasses since the value may be or may not be present in metadata,
     * or may be in different formats for different products.
     * @return  the product end time
     */
    public abstract ProductData.UTC getProductEndTime();

    /**
     * Returns the product center time in UTC format.
     * This method has to be implemented by subclasses since the value may be or may not be present in metadata,
     * or may be in different formats for different products.
     * @return  the product center time.
     */
    public abstract ProductData.UTC getCenterTime();

    /**
     * Returns the product description, preferably from metadata
     * @return  the product description
     */
    public abstract String getProductDescription();

    protected void warn(String message, String argument) {
        if (argument == null) {
            logger.warning(message);
        } else
        {
            argument = argument.replace("@", "");
            if (argument.contains("/")) {
                logger.warning(String.format(message, argument.substring(argument.lastIndexOf("/") + 1)));
            } else {
                logger.warning(String.format(message, argument));
            }
        }
    }

    void indexAttribute(String parentElementPath, MetadataAttribute attribute) {
        String key = (parentElementPath + "@" + attribute.getName()).toLowerCase();
        if (!this.attributeMap.containsKey(key)) {
            this.attributeMap.put(key, new ArrayList<>());
        }
        this.attributeMap.get(key).add(attribute);
    }

    private String ensureAttributeTagPresent(String path) {
        if (path != null) {
            path = path.toLowerCase();
            if (!path.contains("@")) {
                int idx = path.lastIndexOf("/") + 1;
                path = path.substring(0, idx) + "@" + path.substring(idx);
            }
        }
        return path;
    }

}
