package org.esa.s2tbx.dataio.worldview2.metadata;

import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.metadata.XmlMetadata;
import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.ImageUtils;
import org.esa.snap.engine_utilities.util.Pair;
import org.esa.snap.utils.DateHelper;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Basic reader for WorldView 2 products.
 *
 * @author Razvan Dumitrascu
 * @see XmlMetadata
 */
public class WorldView2Metadata extends XmlMetadata {

    public static final String EXCLUSION_STRING = "README";

    private final List<Pair<String, TileMetadataList>> subProducts;

    /**
     * Constructs an instance of metadata class and assigns a name to the root <code>MetadataElement</code>.
     *
     * @param name The name of this instance, and also the initial name of the root element.
     */
    public WorldView2Metadata(String name) {
        super(name);

        this.subProducts = new ArrayList<>();
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

    public String[] findSubProductNames() {
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

    public void addSubProductTileMetadataList(String subProductName, TileMetadataList tileMetadataList) {
        for (int i = 0; i<this.subProducts.size(); i++) {
            String existingProductName = this.subProducts.get(i).getFirst();
            if (existingProductName.equalsIgnoreCase(subProductName)) {
                throw new IllegalArgumentException("The subproduct name '" + subProductName + "' already exists.");
            }
        }
        this.subProducts.add(new Pair<>(subProductName, tileMetadataList));
    }

    public void sortSubProductsByName() {
        if (this.subProducts.size() > 1) {
            Comparator<Pair<String, TileMetadataList> > comparator = new Comparator<Pair<String, TileMetadataList> >() {
                @Override
                public int compare(Pair<String, TileMetadataList> leftItem, Pair<String, TileMetadataList> rightItem) {
                    return leftItem.getFirst().compareTo(rightItem.getFirst());
                }
            };
            Collections.sort(this.subProducts, comparator);
        }
    }

    public int getSubProductCount() {
        return this.subProducts.size();
    }

    public String getSubProductNameAt(int index) {
        Pair<String, TileMetadataList> pair = this.subProducts.get(index);
        return pair.getFirst();
    }

    public TileMetadataList getSubProductTileMetadataListAt(int index) {
        Pair<String, TileMetadataList> pair = this.subProducts.get(index);
        return pair.getSecond();
    }

    public Dimension computeDefaultProductSize() {
        int defaultProductWidth = 0;
        int defaultProductHeight = 0;
        for (int i = 0; i<this.subProducts.size(); i++) {
            TileMetadataList tileMetadataList = this.subProducts.get(i).getSecond();
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

    public boolean isMultiSize() {
        int defaultProductWidth = 0;
        int defaultProductHeight = 0;
        boolean isMultiSize = false;
        for (int i = 0; i<this.subProducts.size(); i++) {
            TileMetadataList tileMetadataList = this.subProducts.get(i).getSecond();
            for (TileMetadata tileMetadata : tileMetadataList.getTiles()) {
                TileComponent tileComponent = tileMetadata.getTileComponent();
                if (defaultProductWidth == 0) {
                    defaultProductWidth = tileComponent.getNumColumns();
                } else if (defaultProductWidth != tileComponent.getNumColumns()) {
                    isMultiSize = true;
                    break;
                }
                if (defaultProductHeight == 0) {
                    defaultProductHeight = tileComponent.getNumRows();
                } else if (defaultProductHeight != tileComponent.getNumRows()) {
                    isMultiSize = true;
                    break;
                }
            }
            if(isMultiSize){
                break;
            }
        }
        return isMultiSize;
    }

    public CrsGeoCoding buildProductGeoCoding(Rectangle subsetBounds) throws FactoryException, TransformException {
        int defaultProductWidth = 0;
        int defaultProductHeight = 0;
        Double stepSize = null;
        String crsCode = null;
        double originX = Double.MAX_VALUE;//0.0d;
        double originY = -Double.MAX_VALUE;//0.0d;

        for (int i = 0; i<this.subProducts.size(); i++) {
            TileMetadataList tileMetadataList = this.subProducts.get(i).getSecond();
            java.util.List<TileMetadata> tiles = tileMetadataList.getTiles();
            for (TileMetadata tileMetadata : tiles) {
                TileComponent tileComponent = tileMetadata.getTileComponent();
                if (tileComponent.getBandID().equals(TileMetadataList.PANCHROMATIC_BAND_ID)) {
                    if (originX > tileComponent.getOriginX()) {
                        originX = tileComponent.getOriginX();
                    }
                    if (originY < tileComponent.getOriginY()) {
                        originY = tileComponent.getOriginY();
                    }

                    if (stepSize == null) {
                        stepSize = tileComponent.getStepSize();
                    } else if (stepSize.doubleValue() != tileComponent.getStepSize()) {
                        throw new IllegalStateException("Different value for step size: previous value="+stepSize.doubleValue()+", new value="+tileComponent.getStepSize()+", number of subproducts="+this.subProducts.size()+".");
                    }

                    String currentCRSCode = tileComponent.computeCRSCode();
                    if (crsCode == null) {
                        crsCode = currentCRSCode;
                    } else if (!crsCode.equalsIgnoreCase(currentCRSCode)) {
                        throw new IllegalStateException("Different value for coordinate reference system: previous value="+crsCode+", new value="+currentCRSCode+", number of subproducts="+this.subProducts.size()+".");
                    }

                    if (defaultProductWidth < tileComponent.getNumColumns()) {
                        defaultProductWidth = tileComponent.getNumColumns();
                    }
                    if (defaultProductHeight < tileComponent.getNumRows()) {
                        defaultProductHeight = tileComponent.getNumRows();
                    }

                }
            }
        }
        if (crsCode != null && stepSize != null) {
            CoordinateReferenceSystem mapCRS = CRS.decode(crsCode);
            return ImageUtils.buildCrsGeoCoding(originX, originY, stepSize.doubleValue(), stepSize.doubleValue(), defaultProductWidth, defaultProductHeight, mapCRS, subsetBounds);
        }
        return null;
    }

}
