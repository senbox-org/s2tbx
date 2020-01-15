package org.esa.s2tbx.dataio.worldview2.metadata;

import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.utils.DateHelper;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Basic reader for WorldView 2 products.
 *
 * @author Razvan Dumitrascu
 * @see XmlMetadata
 */
public class WorldView2Metadata extends XmlMetadata {

    public static final String EXCLUSION_STRING = "README";

    private final Map<String, TileMetadataList> products;

    /**
     * Constructs an instance of metadata class and assigns a name to the root <code>MetadataElement</code>.
     *
     * @param name The name of this instance, and also the initial name of the root element.
     */
    public WorldView2Metadata(String name) {
        super(name);

        this.products = new HashMap<>();
    }

    @Override
    public int getNumBands() {
        return 0;
    }

    @Override
    public String getProductName() {
        return null;
    }

    @Override
    public String getFormatName() {
        return  WorldView2Constants.PRODUCT_GENERIC_NAME;
    }

    @Override
    public int getRasterWidth() {
        return 0;
    }

    @Override
    public int getRasterHeight() {
        return 0;
    }

    @Override
    public String[] getRasterFileNames() {
        return new String[0];
    }

    @Override
    public ProductData.UTC getProductStartTime() {
        ProductData.UTC date = null;
        String value = null;
        try{
            value = getAttributeValue(WorldView2Constants.PATH_START_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldView2Constants.PATH_START_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, WorldView2Constants.WORLDVIEW2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getProductEndTime() {
        ProductData.UTC date = null;
        String value = null;
        try {
            value = getAttributeValue(WorldView2Constants.PATH_END_TIME, null);
        } catch (Exception e) {
            warn(MISSING_ELEMENT_WARNING, WorldView2Constants.PATH_END_TIME);
        }
        if (value != null && !value.isEmpty()) {
            date = DateHelper.parseDate(value, WorldView2Constants.WORLDVIEW2_UTC_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public ProductData.UTC getCenterTime() {
        return null;
    }

    @Override
    public String getProductDescription() {
        return this.name;
    }

    @Override
    public String getFileName() {
        return this.name;
    }

    @Override
    public String getMetadataProfile() {
        return null;
    }

    public String getOrderNumber() {
        String[] attributeValues = getAttributeValues(WorldView2Constants.PATH_ORDER_NUMBER);
        if (attributeValues != null && attributeValues.length > 0) {
            return attributeValues[0];
        }
        return null;
    }

    public String[] findProductNames() {
        Set<String> products = new HashSet<>();
        final String[] fileNames = getAttributeValues(WorldView2Constants.PATH_FILE_LIST);
        for (String file : fileNames) {
            if (file.endsWith(WorldView2Constants.METADATA_EXTENSION) && !file.contains(EXCLUSION_STRING)) {
                String filename = file.substring(file.lastIndexOf("/") + 1, file.lastIndexOf(WorldView2Constants.METADATA_EXTENSION));
                String value = filename.substring(0, filename.indexOf("-"));
                products.add(value);
            }
        }
        return products.toArray(new String[0]);
    }

    public void addProductTileMetadataList(String productName, TileMetadataList tileMetadataList) {
        this.products.put(productName, tileMetadataList);
    }

    public Map<String, TileMetadataList> getProducts() {
        return products;
    }

    public Dimension computeDefaultProductSize() {
        int defaultProductWidth = 0;
        int defaultProductHeight = 0;
        for (TileMetadataList tileMetadataList : this.products.values()) {
            Dimension size = tileMetadataList.computeDefaultProductSize();
            if (defaultProductWidth < size.width) {
                defaultProductWidth = size.width;
            }
            if (defaultProductHeight < size.height) {
                defaultProductHeight = size.height;
            }
        }
        return new Dimension(defaultProductWidth, defaultProductHeight);
    }
}
