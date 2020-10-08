package org.esa.s2tbx.mosaic;

import com.bc.ceres.binding.Converter;
import com.bc.ceres.binding.ConverterRegistry;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.mosaic.internal.MosaicOpConditionConverter;
import org.esa.s2tbx.mosaic.internal.MosaicOpConditionDomConverter;
import org.esa.s2tbx.mosaic.internal.MosaicOpVariableConverter;
import org.esa.s2tbx.mosaic.internal.MosaicOpVariableDomConverter;
import org.esa.s2tbx.mosaic.internal.S2MosaicMultiLevelSource;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.ColorPaletteDef;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.ImageInfo;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.SourceProducts;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.common.MosaicOp;
import org.esa.snap.core.transform.AffineTransform2D;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.core.util.math.MathUtils;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

/**
 * A mosaic operator that performs mosaicking operations on multisize products
 *
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
@OperatorMetadata(alias = "Multi-size Mosaic",
        category = "Raster/Geometric",
        version = "1.0",
        authors = "Razvan Dumitrascu",
        copyright = "(c) 2017 by CS Romania",
        description = "Creates a multi-size mosaic out of a set of source products.",
        internal = false)

public final class S2tbxMosaicOp extends Operator {

    @SuppressWarnings({"PackageVisibleField"})
    @SourceProducts(count = -1, description = "The source products to be used for mosaicking.")
    private Product[] sourceProducts;

    @SourceProduct(description = "A product to be updated.", optional = true)
    Product updateProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(itemAlias = "variable", converter = MosaicOpVariableConverter.class, domConverter = MosaicOpVariableDomConverter.class, description = "Specifies the bands in the target product.")
    MosaicOp.Variable[] variables;

    @Parameter(itemAlias = "condition", converter = MosaicOpConditionConverter.class,domConverter = MosaicOpConditionDomConverter.class, description = "Specifies valid pixels considered in the target product.")
    MosaicOp.Condition[] conditions;

    @Parameter(description = "Specifies the way how conditions are combined.", defaultValue = "OR",
            valueSet = {"OR", "AND"})
    String combine;

    @Parameter(defaultValue = "EPSG:4326",
            description = "The CRS of the target product, represented as WKT or authority code.")
    private String crs;

    @Parameter(description = "Whether the source product should be orthorectified.", defaultValue = "false")
    private boolean orthorectify;

    @Parameter(description = "The name of the elevation model for the orthorectification.")
    private String elevationModelName;

    @Parameter(description = "The western longitude.", interval = "[-180,180]", defaultValue = "-15.0")
    double westBound;
    @Parameter(description = "The northern latitude.", interval = "[-90,90]", defaultValue = "75.0")
    double northBound;
    @Parameter(description = "The eastern longitude.", interval = "[-180,180]", defaultValue = "30.0")
    double eastBound;
    @Parameter(description = "The southern latitude.", interval = "[-90,90]", defaultValue = "35.0")
    double southBound;

    @Parameter(description = "Size of a pixel in X-direction in map units.", defaultValue = "0.005")
    double pixelSizeX;
    @Parameter(description = "Size of a pixel in Y-direction in map units.", defaultValue = "0.005")
    double pixelSizeY;

    @Parameter(alias = "resampling", label = "Resampling Method", description = "The method used for resampling.",
            valueSet = {"Nearest", "Bilinear", "Bicubic"}, defaultValue = "Nearest")
    private String resamplingName;

    @Parameter(description = "Whether the resulting mosaic product should use the native resolutions of the source products.", defaultValue = "true")
    boolean nativeResolution;

    @Parameter(label = "Overlapping Method",
            description = "The method used for overlapping pixels.",
            valueSet = {"MOSAIC_TYPE_BLEND", "MOSAIC_TYPE_OVERLAY"},
            defaultValue = "MOSAIC_TYPE_OVERLAY")
    String overlappingMethod;


    private ReferencedEnvelope targetEnvelope;
    private CoordinateReferenceSystem targetCRS;

    @Override
    public void initialize() throws OperatorException {
        //order the products so that they match the order in which the user selected them to be
        this.sourceProducts = orderSourceProductsAfterRefNo();
        try {
            targetCRS = CRS.parseWKT(this.crs);
        } catch (FactoryException e) {
            try {
                targetCRS = CRS.decode(this.crs);
            } catch (FactoryException e1) {
                throw new OperatorException(e1);
            }
        }

        // STEP 1: extract only needed bands
        if(this.variables!=null && this.variables.length>0) {
            for (int i = 0; i < this.sourceProducts.length; i++) {
                this.sourceProducts[i] = generateSelectedBandsProduct(this.sourceProducts[i]);
            }
        }

        // STEP 2: reproject the source products
        if(!nativeResolution){
            this.targetEnvelope = computeReprojectedBounds();
            final int width = MathUtils.floorInt(this.targetEnvelope.getSpan(0) / this.pixelSizeX);
            final int height = MathUtils.floorInt(this.targetEnvelope.getSpan(1) / this.pixelSizeY);
            this.sourceProducts = resample(this.sourceProducts, width, height);
        }
        for (int i = 0; i < this.sourceProducts.length; i++) {
            this.sourceProducts[i] = reproject(this.sourceProducts[i],
                    i > 0 ? this.sourceProducts[0] : null);
        }

        // STEP 3: subset, if needed, the reprojected products
        this.targetEnvelope = computeReprojectedBounds();// computeMosaicBounds();

        //this.reprojectedProducts = createSubsetProducts();
        // STEP 4: initialize the target product
        if (isUpdateMode()) {
            initFields();
            this.targetProduct = this.updateProduct;
            updateMetadata(this.targetProduct);
        } else {
            this.targetProduct = createTargetProduct();
        }
        addTargetBands(this.targetProduct);
    }

    @Override
    public void doExecute(ProgressMonitor pm) throws OperatorException {
        // STEP 5: create target band images
        try {
            createBandImages();
        } catch (TransformException e) {
            e.printStackTrace();
        }
    }

    /**
     * STEP 1 methods
     */

    private Product[] orderSourceProductsAfterRefNo(){
        Arrays.sort(this.sourceProducts, Comparator.comparingInt(Product::getRefNo));
        return this.sourceProducts;
    }

    private Product[] resample(Product[] source, int targetWidth, int targetHeight) {
        List<Product> resampledProductList = new ArrayList<>(source.length);
        for (Product sourceProduct : source) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("targetWidth", targetWidth);
            parameters.put("targetHeight", targetHeight);
            parameters.put("resampleOnPyramidLevels", true);
            resampledProductList.add(GPF.createProduct("Resample", parameters, sourceProduct));
        }
        return resampledProductList.toArray(new Product[resampledProductList.size()]);
    }

    private Product generateSelectedBandsProduct(final Product product) {
        Product localProduct;
        AbstractSet<String> bandFromExpression = new HashSet<>();
        if (product == null) {
            String msg = "Source product is null.";
            getLogger().warning(msg);
            throw new OperatorException("received product is null");
        }
        for (String bandName : product.getBandNames()) {
            if (this.variables != null) {
                for (MosaicOp.Variable variable : this.variables) {
                    String variableExpression = variable.getExpression();
                    if (variableExpression.equals(bandName)) {
                        bandFromExpression.add(bandName);
                        break;
                    } else if (variableExpression.contains(bandName)) {
                        bandFromExpression.add(bandName);
                        break;
                    }
                }
            }
            if (this.conditions != null){
                for (MosaicOp.Condition condition : this.conditions) {
                    String conditionExpression = condition.getExpression();
                    if (conditionExpression.equals(bandName)) {
                        bandFromExpression.add(bandName);
                        break;
                    } else if (conditionExpression.contains(bandName)) {
                        bandFromExpression.add(bandName);
                        break;
                    }
                }
            }
        }

        String[] BandNamesArray = bandFromExpression.toArray(new String[bandFromExpression.size()]);
        localProduct = new Product(product.getName(), product.getProductType(), product.getSceneRasterWidth(), product.getSceneRasterHeight());
        localProduct.setStartTime(product.getStartTime());
        localProduct.setEndTime(product.getEndTime());
        localProduct.setNumResolutionsMax(product.getNumResolutionsMax());
        ProductUtils.copyMetadata(product, localProduct);
        ProductUtils.copyGeoCoding(product, localProduct);
        ProductUtils.copyTiePointGrids(product, localProduct);
        ProductUtils.copyVectorData(product, localProduct);
        for (Band band : product.getBands()) {
            for (String bandName : BandNamesArray) {
                if (band.getName().equals(bandName)){
                    ProductUtils.copyBand(band.getName(), product, localProduct, true);
                    ProductUtils.copyGeoCoding(band, localProduct.getBand(bandName));
                }
            }
        }
        return localProduct;
    }

    /**
     * STEP 2 methods
     */

    private Product reproject(Product product, Product referenceProduct) {
        final HashMap<String, Object> projParameters = createProjectionParameters();
        final GeoCoding geoCoding = product.getSceneGeoCoding();
        if (geoCoding == null) {
            String msg = "Source product: '" + product.getName() + "' contains no geo-coding. Skipped for further processing.";
            getLogger().warning(msg);
            return product;
        } else {
            HashMap<String, Product> projProducts = new HashMap<>();
            projProducts.put("source", product);
            projParameters.put("crs", this.crs);
            if(!this.nativeResolution){
                projParameters.put("pixelSizeX", this.pixelSizeX);
                projParameters.put("pixelSizeY", this.pixelSizeY);
            }
            if (referenceProduct != null) {
                projParameters.put("pixelSizeX", computeStepX(referenceProduct));
                projParameters.put("pixelSizeY", computeStepY(referenceProduct));
                projProducts.put("reference", referenceProduct);
            }
            product = GPF.createProduct("S2tbx-Reproject", projParameters, projProducts);
        }
        return product;
    }

    /**
     * STEP 3 methods
     */

    private ReferencedEnvelope computeMosaicBounds() {
        final Rectangle2D initialBounds = new Rectangle2D.Double();
        initialBounds.setFrameFromDiagonal(this.westBound, this.southBound, this.eastBound, this.northBound);
        final ReferencedEnvelope boundsEnvelope = new ReferencedEnvelope(initialBounds, DefaultGeographicCRS.WGS84);
        try {
            return boundsEnvelope.transform(this.targetCRS, true);
        } catch (TransformException | FactoryException e) {
            throw new OperatorException(e);
        }
    }

    private List<Product> createSubsetProducts() {
        List<Product> subsetProducts = new ArrayList<>();
        for (Product product : this.sourceProducts) {
            Rectangle2D productArea = new Rectangle2D.Double();
            final GeoCoding geoCoding = product.getSceneGeoCoding();
            GeoPos minPoint = geoCoding.getGeoPos(new PixelPos(0, 0), null);
            GeoPos maxPoint = geoCoding.getGeoPos(new PixelPos(product.getSceneRasterWidth(),
                    product.getSceneRasterHeight()), null);

            productArea.setFrameFromDiagonal(minPoint.getLon(), minPoint.getLat(),
                    maxPoint.getLon(), maxPoint.getLat());
            ReferencedEnvelope productEnvelope = new ReferencedEnvelope(productArea, this.targetCRS);
            final DirectPosition targetLower = this.targetEnvelope.getLowerCorner();
            final DirectPosition targetUpper = this.targetEnvelope.getUpperCorner();
            final DirectPosition productLower = productEnvelope.getLowerCorner();
            final DirectPosition productUpper = productEnvelope.getUpperCorner();
            if (targetLower.getOrdinate(0) > productLower.getOrdinate(0) ||
                    targetLower.getOrdinate(1) > productLower.getOrdinate(1) ||
                    targetUpper.getOrdinate(0) < productUpper.getOrdinate(0) ||
                    targetUpper.getOrdinate(1) < productUpper.getOrdinate(1)) {
                final HashMap<String, Product> sourceProductMap = new HashMap<>();
                sourceProductMap.put("source", product);
                final HashMap<String, Object> subsetParams = new HashMap<>();
                subsetParams.put("bandNames", product.getBandNames());
                subsetParams.put("copyMetadata", true);
                Envelope intersection = productEnvelope.intersection(this.targetEnvelope);
                String builder = "POLYGON((" +
                        intersection.getMinX() + " " + intersection.getMinY() + "," +
                        intersection.getMinX() + " " + intersection.getMaxY() + "," +
                        intersection.getMaxX() + " " + intersection.getMaxY() + "," +
                        intersection.getMaxX() + " " + intersection.getMinY() + "," +
                        intersection.getMinX() + " " + intersection.getMinY() +
                        "))";
                subsetParams.put("geoRegion", builder);

                subsetProducts.add(GPF.createProduct("Subset", subsetParams, sourceProductMap));
            } else {
                subsetProducts.add(product);
            }
        }
        return subsetProducts;
    }

    /**
     * STEP 4 methods
     */

    private boolean isUpdateMode() {
        return this.updateProduct != null;
    }

    private void initFields() {
        final Map<String, Object> params = getOperatorParameters(this.updateProduct);
        initObject(params, this);
    }

    private void updateMetadata(Product product) {
        final MetadataElement graphElement = product.getMetadataRoot().getElement("Processing_Graph");
        for (MetadataElement nodeElement : graphElement.getElements()) {
            if (getSpi().getOperatorAlias().equals(nodeElement.getAttributeString("operator"))) {
                final MetadataElement sourcesElement = nodeElement.getElement("sources");
                for (int i = 0; i < this.sourceProducts.length; i++) {
                    final String oldIndex = String.valueOf(i + 1);
                    final String newIndex = String.valueOf(sourcesElement.getNumAttributes() + i + 1);
                    final Product sourceProduct = this.sourceProducts[i];
                    final String attributeName = getSourceProductId(sourceProduct).replaceFirst(oldIndex, newIndex);
                    final File location = sourceProduct.getFileLocation();
                    final ProductData attributeValue;
                    if (location == null) {
                        attributeValue = ProductData.createInstance(product.toString());
                    } else {
                        attributeValue = ProductData.createInstance(location.getPath());
                    }
                    final MetadataAttribute attribute = new MetadataAttribute(attributeName, attributeValue, true);
                    sourcesElement.addAttribute(attribute);
                }
            }
        }
    }

    private ReferencedEnvelope computeReprojectedBounds() {
        final Rectangle2D bounds = new Rectangle2D.Double();
        bounds.setFrameFromDiagonal(westBound, northBound, eastBound, southBound);
        final ReferencedEnvelope boundsEnvelope = new ReferencedEnvelope(bounds, DefaultGeographicCRS.WGS84);
        ReferencedEnvelope targetEnvelope = null;
        try {
            targetEnvelope = boundsEnvelope.transform(targetCRS, true);
        } catch (TransformException e) {
            e.printStackTrace();
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        if (targetEnvelope == null) {
            throw new OperatorException("Cannot compute reprojected bounds");
        }
        return targetEnvelope; //new Rectangle2D.Double(envelope.getMinX(), envelope.getMinY(), envelope.getWidth(), envelope.getHeight());
    }


    private Product createTargetProduct() {
        try {
            if(nativeResolution) {
                final double[] pixelSize = getPixelSize(this.sourceProducts[0].getSceneGeoCoding(), targetCRS);
                this.pixelSizeX = pixelSize[0];
                this.pixelSizeY = pixelSize[1];
            }
            final double[] maximumPixelSizeSourceProduct = computeSourceProductMaximumResolution(this.sourceProducts[0]);
            if(maximumPixelSizeSourceProduct[0] != this.pixelSizeX ||
                    maximumPixelSizeSourceProduct[1] != this.pixelSizeY) {
                this.pixelSizeX = maximumPixelSizeSourceProduct[0];
                this.pixelSizeY = maximumPixelSizeSourceProduct[1];
            }
            final int width = MathUtils.floorInt(this.targetEnvelope.getSpan(0) / this.pixelSizeX);
            final int height = MathUtils.floorInt(this.targetEnvelope.getSpan(1) / this.pixelSizeY);
            final CrsGeoCoding geoCoding = new CrsGeoCoding(targetCRS,
                    width, height,
                    targetEnvelope.getLowerCorner().getOrdinate(0),
                    targetEnvelope.getUpperCorner().getOrdinate(1),
                    this.pixelSizeX, this.pixelSizeY);
            final Product product = new Product("Multi-size Mosaic", "BEAM_MOSAIC", width, height);
            product.setSceneGeoCoding(geoCoding);
            final Dimension tileSize = JAIUtils.computePreferredTileSize(width, height, 1);
            product.setPreferredTileSize(tileSize);
            product.setNumResolutionsMax(sourceProducts[0].getNumResolutionsMax());
            if(sourceProducts[0].getNumBands()==1){
                product.setNumResolutionsMax(sourceProducts[0].getBandAt(0).getSourceImage().getModel().getLevelCount());
            }
            return product;
        } catch (Exception e) {
            throw new OperatorException(e);
        }
    }

    private double[] computeSourceProductMaximumResolution(Product sourceProduct) {
        AffineTransform affTransform = sourceProduct.getBandAt(0).getSourceImage().getModel().getImageToModelTransform(0);
        double[] productResolution = new double[] {affTransform.getScaleX(),Math.abs(affTransform.getScaleY())};
        for(int index = 0;index<sourceProduct.getNumBands();index++)
        {
            affTransform = sourceProduct.getBandAt(index).getSourceImage().getModel().getImageToModelTransform(0);
            if(( affTransform.getScaleX() < productResolution[0])||(Math.abs(affTransform.getScaleY()) < productResolution[1])){
                productResolution[0] = affTransform.getScaleX();
                productResolution[1] = Math.abs(affTransform.getScaleY());
            }
        }
        return productResolution;
    }

    private void addTargetBands(Product product) {
        int sceneWidth = product.getSceneRasterWidth();
        int sceneHeight = product.getSceneRasterHeight();
        for (MosaicOp.Variable outputVariable : this.variables) {
            final int targetDataType;
            final Band firstSourceBand = this.sourceProducts[0].getBand(getSourceBandName(outputVariable.getExpression()));
            if (firstSourceBand.isScalingApplied()) {
                targetDataType = firstSourceBand.getGeophysicalDataType();
            } else {
                targetDataType = firstSourceBand.getDataType();
            }
            final AffineTransform affineTransformSourceBand =
                    firstSourceBand.getSourceImage().getModel().getImageToModelTransform(0);
            double stepX = Math.abs(affineTransformSourceBand.getScaleX());
            double stepY = Math.abs(affineTransformSourceBand.getScaleY());
            int bandWidth = MathUtils.floorInt(this.targetEnvelope.getSpan(0) / Math.abs(stepX));
            int bandHeight = MathUtils.floorInt(this.targetEnvelope.getSpan(1) / Math.abs(stepY));
            Band targetBand = new Band(outputVariable.getName(), targetDataType, bandWidth, bandHeight);
            targetBand.setDescription(outputVariable.getExpression());
            targetBand.setUnit(firstSourceBand.getUnit());
            targetBand.setScalingFactor(firstSourceBand.getScalingFactor());
            targetBand.setScalingOffset(firstSourceBand.getScalingOffset());
            targetBand.setLog10Scaled(firstSourceBand.isLog10Scaled());
            targetBand.setNoDataValue(firstSourceBand.getNoDataValue());
            targetBand.setValidPixelExpression(firstSourceBand.getValidPixelExpression());
            targetBand.setSpectralWavelength(firstSourceBand.getSpectralWavelength());
            targetBand.setSpectralBandwidth(firstSourceBand.getSpectralBandwidth());

            ImageInfo sourceImageInfo = firstSourceBand.getImageInfo();
            if (sourceImageInfo != null) {
                targetBand.setImageInfo(new ImageInfo(new ColorPaletteDef(sourceImageInfo.getColorPaletteDef().getPoints())));
            }
            if (sceneWidth != bandWidth) {
                AffineTransform2D transform2D =
                        new AffineTransform2D((float) sceneWidth / bandWidth, 0.0, 0.0,
                                (float) sceneHeight / bandHeight, 0.0, 0.0);
                targetBand.setImageToModelTransform(transform2D);
            }
            try {
                CrsGeoCoding geoCoding = new CrsGeoCoding(targetCRS, bandWidth, bandHeight,
                        targetEnvelope.getMinX(), targetEnvelope.getMaxY(),
                        stepX, stepY);
                targetBand.setGeoCoding(geoCoding);
            } catch (FactoryException | TransformException e) {
                e.printStackTrace();
            }
            product.addBand(targetBand);
        }
    }

    /**
     * STEP 5 methods
     */

    private void createBandImages() throws TransformException {
        for (Band band : this.targetProduct.getBands()) {
            Band[] srcBands = new Band[this.sourceProducts.length];
            for (int index = 0; index < this.sourceProducts.length; index++){
                for(MosaicOp.Variable outputVariable : this.variables) {
                    if(outputVariable.getName().equals(band.getName())) {
                        srcBands[index] = this.sourceProducts[index].getBand(getSourceBandName(outputVariable.getExpression()));
                    }
                }
            }
            final Dimension tileSize = JAIUtils.computePreferredTileSize(band.getRasterWidth(), band.getRasterHeight(), 1);
            int levels = srcBands[0].getSourceImage().getModel().getLevelCount();
            for(Product product: this.sourceProducts){
                int lowestLevel = product.getBandAt(0).getSourceImage().getModel().getLevelCount();
                if(lowestLevel < levels){
                    levels = lowestLevel;
                }
            }
            MathTransform mapTransform = band.getGeoCoding().getImageToMapTransform();
            DirectPosition bandOrigin = mapTransform.transform(new DirectPosition2D(0, 0), null);
            S2MosaicMultiLevelSource bandSource =
                    new S2MosaicMultiLevelSource(srcBands,
                            bandOrigin.getOrdinate(0),
                            bandOrigin.getOrdinate(1),
                            band.getRasterWidth(), band.getRasterHeight(),
                            tileSize.width, tileSize.height, levels,
                            band.getGeoCoding(),
                            this.overlappingMethod);
            band.setSourceImage(new DefaultMultiLevelImage(bandSource));
        }
    }

    /**
     * Utility methods
     */

    private double[] getPixelSize(GeoCoding sourceGeoCoding, CoordinateReferenceSystem targetCRS) {
        double[] size = null;
        try {
            size = new double[2];
            DirectPosition geoPos1 = sourceGeoCoding.getImageToMapTransform()
                    .transform(new DirectPosition2D(0, 0), null);
            Coordinate c1 = new Coordinate(geoPos1.getOrdinate(0), geoPos1.getOrdinate(1));
            DirectPosition geoPos2 = sourceGeoCoding.getImageToMapTransform()
                    .transform(new DirectPosition2D(0, 1), null);
            Coordinate c2 = new Coordinate(geoPos2.getOrdinate(0), geoPos2.getOrdinate(1));
            DirectPosition geoPos3 = sourceGeoCoding.getImageToMapTransform()
                    .transform(new DirectPosition2D(1, 0), null);
            Coordinate c3 = new Coordinate(geoPos3.getOrdinate(0), geoPos3.getOrdinate(1));
            final CoordinateReferenceSystem sourceCRS = sourceGeoCoding.getMapCRS();
            size[0] = distance(sourceCRS, targetCRS, c3, c1);
            size[1] = distance(sourceCRS, targetCRS, c2, c1);
        } catch (TransformException tex) {
            tex.printStackTrace();
        }
        return size;
    }

    private double distance(CoordinateReferenceSystem fromCRS,
                            CoordinateReferenceSystem toCRS,
                            Coordinate point1, Coordinate point2) {
        try {
            MathTransform transform = CRS.findMathTransform(fromCRS, toCRS);
            Coordinate tPoint1 = JTS.transform(point1, null, transform);
            Coordinate tPoint2 = JTS.transform(point2, null, transform);
            return tPoint1.distance(tPoint2);
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
            return Double.NaN;
        }
    }

    private ReferencedEnvelope transformEnvelope(CoordinateReferenceSystem fromCRS,
                                                 CoordinateReferenceSystem toCRS,
                                                 Rectangle2D bounds) {
        try {
            MathTransform transform = CRS.findMathTransform(fromCRS, toCRS);
            Envelope sourceEnvelope = new Envelope(bounds.getMinX(), bounds.getMaxX(),
                    bounds.getMinY(), bounds.getMaxY());
            final Envelope envelope = JTS.transform(sourceEnvelope, transform);
            return new ReferencedEnvelope(envelope.getMinX(), envelope.getMaxX(),
                    envelope.getMinY(), envelope.getMaxY(),
                    toCRS);
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getSourceBandName(String variable){
        String bandName = null;
        for(Band Band : this.sourceProducts[0].getBands()){
            if (variable.equals(Band.getName())) {
                bandName = Band.getName();
                break;
            }
        }
        return bandName;
    }

    private HashMap<String, Object> createProjectionParameters() {
        HashMap<String, Object> projParameters = new HashMap<>();
        if(this.nativeResolution) {
            projParameters.put("resamplingName", this.resamplingName);
        }
        projParameters.put("includeTiePointGrids", true);  // ensure tie-points are reprojected
        if (this.orthorectify) {
            projParameters.put("orthorectify", true);
            projParameters.put("elevationModelName", this.elevationModelName);
        }
        return projParameters;
    }

    private double computeStepX(Product product){
        OptionalDouble result = Arrays.stream(product.getBands())
                .mapToDouble(band -> Math.abs(band.getSourceImage().getModel().getImageToModelTransform(0).getScaleX()))
                .min();
        if (result.isPresent()) {
            return result.getAsDouble();
        } else {
            return this.pixelSizeX;
        }
    }

    private double computeStepY(Product product){
        OptionalDouble result = Arrays.stream(product.getBands())
                .mapToDouble(band -> Math.abs(band.getSourceImage().getModel().getImageToModelTransform(0).getScaleY()))
                .min();
        if (result.isPresent()) {
            return result.getAsDouble();
        } else {
            return this.pixelSizeY;
        }
    }


    public static Map<String, Object> getOperatorParameters(Product product) throws OperatorException {
        final MetadataElement graphElement = product.getMetadataRoot().getElement("Processing_Graph");
        if (graphElement == null) {
            throw new OperatorException("Product has no metadata element named 'Processing_Graph'");
        }
        final String operatorAlias = "Multi-size Mosaic";
        final Map<String, Object> parameters = new HashMap<>();
        boolean operatorFound = false;
        for (MetadataElement nodeElement : graphElement.getElements()) {
            if (operatorAlias.equals(nodeElement.getAttributeString("operator"))) {
                operatorFound = true;
                collectParameters(S2tbxMosaicOp.class, nodeElement.getElement("parameters"), parameters);
            }
        }
        if (!operatorFound) {
            throw new OperatorException("No metadata found for operator '" + operatorAlias + "'");
        }
        return parameters;
    }
    private static void collectParameters(Class<?> operatorClass, MetadataElement parentElement,
                                          Map<String, Object> parameters) {
        for (Field field : operatorClass.getDeclaredFields()) {
            final Parameter annotation = field.getAnnotation(Parameter.class);
            if (annotation != null) {
                final Class<?> fieldType = field.getType();
                if (fieldType.isArray()) {
                    initArrayParameter(parentElement, field, parameters);
                } else {
                    initParameter(parentElement, field, parameters);
                }
            }
        }
    }
    private static void initParameter(MetadataElement parentElement, Field field,
                                      Map<String, Object> parameters) throws
            OperatorException {
        Parameter annotation = field.getAnnotation(Parameter.class);
        String name = annotation.alias();
        if (name.isEmpty()) {
            name = field.getName();
        }
        try {
            if (parentElement.containsAttribute(name)) {
                final Converter converter = getConverter(field.getType(), annotation);
                final String parameterText = parentElement.getAttributeString(name);
                final Object value = converter.parse(parameterText);
                parameters.put(name, value);
            } else {
                final MetadataElement element = parentElement.getElement(name);
                if (element != null) {
                    final Object obj = field.getType().newInstance();
                    final HashMap<String, Object> objParams = new HashMap<>();
                    collectParameters(obj.getClass(), element, objParams);
                    initObject(objParams, obj);
                    parameters.put(name, obj);
                }
            }
        } catch (Exception e) {
            throw new OperatorException(String.format("Cannot initialise operator parameter '%s'", name), e);
        }
    }

    private static void initArrayParameter(MetadataElement parentElement, Field field,
                                           Map<String, Object> parameters) throws OperatorException {
        String name = field.getAnnotation(Parameter.class).alias();
        if (name.isEmpty()) {
            name = field.getName();
        }
        final MetadataElement element = parentElement.getElement(name);
        try {
            if (element != null) {
                final MetadataElement[] elements = element.getElements();
                final Class<?> componentType = field.getType().getComponentType();
                final Object array = Array.newInstance(componentType, elements.length);
                for (int i = 0; i < elements.length; i++) {
                    MetadataElement arrayElement = elements[i];
                    final Object componentInstance = componentType.newInstance();
                    final HashMap<String, Object> objParams = new HashMap<>();
                    collectParameters(componentInstance.getClass(), arrayElement, objParams);
                    initObject(objParams, componentInstance);
                    Array.set(array, i, componentInstance);
                }
                parameters.put(name, array);
            }
        } catch (Exception e) {
            throw new OperatorException(String.format("Cannot initialise operator parameter '%s'", name), e);
        }
    }
    private static void initObject(Map<String, Object> params, Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            final Parameter annotation = field.getAnnotation(Parameter.class);
            if (annotation != null) {
                String name = annotation.alias();
                if (name.isEmpty()) {
                    name = field.getName();
                }
                try {
                    field.set(object, params.get(name));
                } catch (Exception e) {
                    final String msg = String.format("Cannot initialise operator parameter '%s'", name);
                    throw new OperatorException(msg, e);
                }
            }
        }
    }

    private static Converter<?> getConverter(Class<?> type, Parameter parameter) throws OperatorException {
        final Class<? extends Converter> converter = parameter.converter();
        if (converter == Converter.class) {
            return ConverterRegistry.getInstance().getConverter(type);
        } else {
            try {
                return converter.newInstance();
            } catch (Exception e) {
                final String message = String.format("Cannot find converter for  type '%s'", type);
                throw new OperatorException(message, e);
            }
        }
    }

    public static class Spi extends OperatorSpi {
        public Spi() {
            super(S2tbxMosaicOp.class);
        }
    }
}
