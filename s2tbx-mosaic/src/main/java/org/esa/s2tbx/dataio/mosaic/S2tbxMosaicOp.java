package org.esa.s2tbx.dataio.mosaic;

import com.bc.ceres.binding.Converter;
import com.bc.ceres.binding.ConverterRegistry;
import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.mosaic.internal.S2MosaicMultiLevelSource;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.ColorPaletteDef;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.ImageInfo;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.MetadataElement;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.dataop.barithm.BandArithmetic;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.SourceProducts;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.image.ResolutionLevel;
import org.esa.snap.core.image.VirtualBandOpImage;
import org.esa.snap.core.jexp.ParseException;
import org.esa.snap.core.jexp.impl.Tokenizer;
import org.esa.snap.core.transform.AffineTransform2D;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.core.util.math.MathUtils;
import org.geotools.factory.Hints;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.AddCollectionDescriptor;
import javax.media.jai.operator.AddDescriptor;
import javax.media.jai.operator.FormatDescriptor;
import javax.media.jai.operator.MosaicDescriptor;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
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
 * A mosaic operator that performs mosaicing operations on multisize products
 *
 * @author Razvan Dumitrascu
 * @since 5.0.2
 */
@OperatorMetadata(alias = "S2tbx-Mosaic",
        category = "Raster/Geometric",
        version = "1.0",
        authors = "Razvan Dumitrascu",
        copyright = "(c) 2017 by CS Romania",
        description = "Creates a Sentinel2 mosaic out of a set of source products.",
        internal = false)

public final class S2tbxMosaicOp extends Operator {

    @SuppressWarnings({"PackageVisibleField"})
    @SourceProducts(count = -1, description = "The source products to be used for mosaicking.")
    Product[] sourceProducts;

    @SourceProduct(description = "A product to be updated.", optional = true)
    Product updateProduct;

    @TargetProduct
    Product targetProduct;

    @Parameter(itemAlias = "variable", description = "Specifies the bands in the target product.")
    Variable[] variables;

    @Parameter(itemAlias = "condition", description = "Specifies valid pixels considered in the target product.")
    Condition[] conditions;

    @Parameter(description = "Specifies the way how conditions are combined.", defaultValue = "OR",
            valueSet = {"OR", "AND"})
    String combine;

    @Parameter(defaultValue = "EPSG:4326",
            description = "The CRS of the target product, represented as WKT or authority code.")
    String crs;

    @Parameter(description = "Whether the source product should be orthorectified.", defaultValue = "false")
    boolean orthorectify;

    @Parameter(description = "The name of the elevation model for the orthorectification.")
    String elevationModelName;

    @Parameter(description = "The western longitude.", interval = "[-180,180]", defaultValue = "-15.0")
    double westBound;
    @Parameter(description = "The northern latitude.", interval = "[-90,90]", defaultValue = "75.0")
    double northBound;
    @Parameter(description = "The eastern longitude.", interval = "[-180,180]", defaultValue = "30.0")
    double eastBound;
    @Parameter(description = "The southern latitude.", interval = "[-90,90]", defaultValue = "35.0")
    double southBound;

    @Parameter(description = "Size of a pixel in X-direction in map units.", defaultValue = "0.05")
    double pixelSizeX;
    @Parameter(description = "Size of a pixel in Y-direction in map units.", defaultValue = "0.05")
    double pixelSizeY;

    @Parameter(alias = "resampling", label = "Resampling Method", description = "The method used for resampling.",
            valueSet = {"Nearest", "Bilinear", "Bicubic"}, defaultValue = "Nearest")
    String resamplingName;

    @Parameter(description = "Whether the resulting mosaic product should use the native resolutions of the source products.", defaultValue = "true")
    boolean nativeResolution;

    @Parameter(label = "Overlapping Method",
            description = "The method used for overlapping pixels.",
            valueSet = {"MOSAIC_TYPE_BLEND", "MOSAIC_TYPE_OVERLAY"},
            defaultValue = "MOSAIC_TYPE_BLEND")
    String overlappingMethod;

    private static final String LOWER_RESOLUTION = "lower_resolution";
    private static final String HIGHER_RESOLUTION = "higher_resolution";

    private Product[] reprojectedProducts;
    private List<Product> generatedSelectedBandsProducts = new ArrayList<>();
    private double targetStepX;
    private double targetStepY;
    private ReferencedEnvelope targetEnvelope;
    private CoordinateReferenceSystem targetCRS;

    @Override
    public void initialize() throws OperatorException{
        //order the products so that they match the order in which the user selected them to be
        this.sourceProducts = orderSourceProductsAfterRefNo();
        //recreate source products so that they contain only the bands used in mosaicing
        for (Product product : this.sourceProducts) {
            this.generatedSelectedBandsProducts.add(generateSelectedBandsProduct(product));
        }

        try {
            targetCRS = CRS.parseWKT(this.crs);
        } catch (FactoryException e) {
            try {
                targetCRS = CRS.decode(this.crs);
            } catch (FactoryException e1) {
                throw new OperatorException(e1);
            }
        }
        this.sourceProducts = this.generatedSelectedBandsProducts.toArray(new Product[generatedSelectedBandsProducts.size()]);

        if (this.nativeResolution) {
            this.reprojectedProducts = createReprojectedProducts();// createReprojectedProductsNativeResolution();
            this.targetStepX = computeTargetStepX(this.reprojectedProducts[0]);
            this.targetStepY = computeTargetStepY(this.reprojectedProducts[0]);
        }
        if (isUpdateMode()) {
            initFields();
            this.targetProduct = this.updateProduct;
            updateMetadata(this.targetProduct);
        } else {
            this.targetProduct = createTargetProduct();
            addTargetBands(this.targetProduct);
        }

        if (this.nativeResolution) {
            try {
                createSourceImagesNativeResolution();
            } catch (TransformException e) {
                e.printStackTrace();
            }
        } else {
            Product[] resampledProducts;
            this.targetStepX = computeTargetStepX(this.sourceProducts[0]);
            this.targetStepY = computeTargetStepY(this.sourceProducts[0]);
            if (this.pixelSizeX > this.targetStepX) {
                resampledProducts = resample(this.sourceProducts, this.targetProduct.getSceneRasterWidth(),
                        this.targetProduct.getSceneRasterHeight(),LOWER_RESOLUTION);
            } else {
                resampledProducts = resample(this.sourceProducts, this.targetProduct.getSceneRasterWidth(),
                        this.targetProduct.getSceneRasterHeight(),HIGHER_RESOLUTION);
            }
            this.sourceProducts = resampledProducts;
            this.reprojectedProducts = createReprojectedProducts();

            // for each variable and each product one 'alpha' image is created.
            // the alpha value for a pixel is either 0.0 or 1.0
            List<List<PlanarImage>> alphaImageList = createAlphaImages();

            // for each variable and each product one 'source' image is created.
            List<List<RenderedImage>> sourceImageList = createSourceImages();

            List<RenderedImage> mosaicImageList = createMosaicImages(sourceImageList, alphaImageList);

            final List<RenderedImage> variableCountImageList = createVariableCountImages(alphaImageList);
            setTargetBandImages(this.targetProduct, mosaicImageList, variableCountImageList);
            this.reprojectedProducts = null;
        }
    }

    private Product[] orderSourceProductsAfterRefNo(){
        Arrays.sort(this.sourceProducts, Comparator.comparingInt(Product::getRefNo));
        return this.sourceProducts;
    }

    private boolean isUpdateMode() {
        return this.updateProduct != null;
    }


    private void initFields() {
        final Map<String, Object> params = getOperatorParameters(this.updateProduct);
        initObject(params, this);
    }

    private List<RenderedImage> createVariableCountImages(List<List<PlanarImage>> alphaImageList) {
        List<RenderedImage> variableCountImageList = new ArrayList<>(this.variables.length);
        for (List<PlanarImage> variableAlphaImageList : alphaImageList) {
            final RenderedImage countFloatImage = createImageSum(variableAlphaImageList);
            variableCountImageList.add(FormatDescriptor.create(countFloatImage, DataBuffer.TYPE_INT, null));
        }
        return variableCountImageList;
    }

    private List<RenderedImage> createMosaicImages(List<List<RenderedImage>> sourceImageList,
                                                   List<List<PlanarImage>> alphaImageList) {

        ImageLayout imageLayout = ImageManager.createSingleBandedImageLayout(
                ImageManager.getDataBufferType(ProductData.TYPE_FLOAT32),
                this.targetProduct.getSceneRasterWidth(),
                this.targetProduct.getSceneRasterHeight(),
                ImageManager.getPreferredTileSize(this.targetProduct),
                ResolutionLevel.MAXRES);
        Hints hints = new Hints(JAI.KEY_IMAGE_LAYOUT, imageLayout);
        final List<RenderedImage> mosaicImages = new ArrayList<>(sourceImageList.size());
        for (int i = 0; i < sourceImageList.size(); i++) {
            final PlanarImage[] sourceAlphas = alphaImageList.get(i).toArray(new PlanarImage[alphaImageList.size()]);
            final List<RenderedImage> sourceImages = sourceImageList.get(i);
            final RenderedImage[] renderedImages = sourceImages.toArray(new RenderedImage[sourceImages.size()]);
            // we don't need ROIs, cause they are not considered by MosaicDescriptor when sourceAlphas are given
            mosaicImages.add(MosaicDescriptor.create(renderedImages, MosaicDescriptor.MOSAIC_TYPE_BLEND,
                    sourceAlphas, null, null, null, hints));
        }

        return mosaicImages;
    }

    private void setTargetBandImages(Product product, List<RenderedImage> bandImages,
                                     List<RenderedImage> variableCountImageList) {
        for (int i = 0; i < this.variables.length; i++) {
            Variable outputVariable = this.variables[i];
            product.getBand(outputVariable.getName()).setSourceImage(bandImages.get(i));

            final String countBandName = getCountBandName(outputVariable);
            product.getBand(countBandName).setSourceImage(variableCountImageList.get(i));
        }

        if (this.conditions != null) {
            for (Condition condition : this.conditions) {
                if (condition.isOutput()) {
                    // The sum of all conditions of all sources is created.
                    // 1.0 indicates condition is true and 0.0 indicates false.
                    final RenderedImage sumImage = createConditionSumImage(condition);
                    final RenderedImage reformattedImage = FormatDescriptor.create(sumImage, DataBuffer.TYPE_INT, null);
                    RenderedImage condImage = reformattedImage;
                    if (isUpdateMode()) {
                        final RenderedImage updateImage = this.updateProduct.getBand(condition.getName()).getSourceImage();
                        condImage = AddDescriptor.create(reformattedImage, updateImage, null);
                    }
                    Band band = product.getBand(condition.getName());
                    band.setSourceImage(condImage);
                }
            }
        }
    }

    private RenderedImage createConditionSumImage(Condition condition) {
        final List<RenderedImage> renderedImageList = new ArrayList<>(this.reprojectedProducts.length);
        for (Product reprojectedProduct : this.reprojectedProducts) {
            renderedImageList.add(createConditionImage(condition, reprojectedProduct));
        }
        return createImageSum(renderedImageList);
    }

    private PlanarImage createConditionImage(Condition condition, Product reprojectedProduct) {
        String validMaskExpression;
        try {
            validMaskExpression = createValidMaskExpression(reprojectedProduct, condition.getExpression());
        } catch (ParseException e) {
            throw new OperatorException(e);
        }
        String expression = validMaskExpression + " && (" + condition.getExpression() + ")";
        // the condition images are used as sourceAlpha parameter for MosaicOpImage, they have to have the same
        // data type as the source images. That's why we use normal expression images with data type FLOAT32.
        return createExpressionImage(expression, reprojectedProduct);
    }

    private RenderedImage createImageSum(List<? extends RenderedImage> renderedImageList) {
        if (renderedImageList.size() >= 2) {
            return AddCollectionDescriptor.create(renderedImageList, null);
        } else {
            return renderedImageList.get(0);
        }
    }

    private List<List<RenderedImage>> createSourceImages() {
        final List<List<RenderedImage>> sourceImageList = new ArrayList<>(this.variables.length);
        for (final Variable variable : this.variables) {
            final List<RenderedImage> renderedImageList = new ArrayList<>(this.reprojectedProducts.length);
            sourceImageList.add(renderedImageList);
            for (final Product product : this.reprojectedProducts) {
                renderedImageList.add(createExpressionImage(variable.getExpression(), product));
            }
            if (isUpdateMode()) {
                renderedImageList.add(this.updateProduct.getBand(variable.getName()).getSourceImage());
            }
        }
        return sourceImageList;
    }

    private PlanarImage createExpressionImage(final String expression, Product product) {
        MultiLevelImage sourceImage = product.getBandAt(0).getSourceImage();
        ResolutionLevel resolutionLevel = ResolutionLevel.create(sourceImage.getModel(), 0);
        float fillValue = 0.0f;
        Dimension tileSize = new Dimension(sourceImage.getTileWidth(), sourceImage.getTileHeight());
        return VirtualBandOpImage.builder(expression, product)
                .dataType(ProductData.TYPE_FLOAT32)
                .fillValue(fillValue)
                .tileSize(tileSize)
                .mask(false)
                .level(resolutionLevel)
                .create();
    }

    private List<List<PlanarImage>> createAlphaImages() {
        final List<List<PlanarImage>> alphaImageList = new ArrayList<>(this.variables.length);
        for (final Variable variable : this.variables) {
            final ArrayList<PlanarImage> list = new ArrayList<>(this.reprojectedProducts.length);
            alphaImageList.add(list);
            for (final Product product : this.reprojectedProducts) {
                final String validMaskExpression;
                try {
                    validMaskExpression = createValidMaskExpression(product, variable.getExpression());
                } catch (ParseException e) {
                    throw new OperatorException(e);
                }
                final StringBuilder combinedExpression = new StringBuilder(validMaskExpression);
                if (this.conditions != null && this.conditions.length > 0) {
                    combinedExpression.append(" && (");
                    for (int i = 0; i < this.conditions.length; i++) {
                        Condition condition = this.conditions[i];
                        if (i != 0) {
                            combinedExpression.append(" ").append(this.combine).append(" ");
                        }
                        combinedExpression.append(condition.getExpression());
                    }
                    combinedExpression.append(")");
                }
                list.add(createExpressionImage(combinedExpression.toString(), product));
            }
            if (isUpdateMode()) {
                final RenderedImage updateImage = this.updateProduct.getBand(getCountBandName(variable)).getSourceImage();
                list.add(FormatDescriptor.create(updateImage, DataBuffer.TYPE_FLOAT, null));
            }
        }
        return alphaImageList;
    }

    private static String createValidMaskExpression(Product product, final String expression) throws ParseException {
        return BandArithmetic.getValidMaskExpression(expression, product, null);
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

    private double computeTargetStepX(Product product){
        OptionalDouble result = Arrays.stream(product.getBands())
                .mapToDouble(band -> Math.abs(band.getSourceImage().getModel().getImageToModelTransform(0).getScaleX()))
                .min();
        if (result.isPresent()) {
            return result.getAsDouble();
        } else {
            return this.pixelSizeX;
        }
    }

    private double computeTargetStepY(Product product){
        OptionalDouble result = Arrays.stream(product.getBands())
                .mapToDouble(band -> Math.abs(band.getSourceImage().getModel().getImageToModelTransform(0).getScaleY()))
                .min();
        if (result.isPresent()) {
            return result.getAsDouble();
        } else {
            return this.pixelSizeY;
        }
    }

    private Product createTargetProduct() {
        try {
            final Rectangle2D bounds = new Rectangle2D.Double();
            bounds.setFrameFromDiagonal(this.westBound, this.southBound, this.eastBound, this.northBound);
            final ReferencedEnvelope boundsEnvelope = new ReferencedEnvelope(bounds, DefaultGeographicCRS.WGS84);
            this.targetEnvelope = boundsEnvelope.transform(targetCRS, false);
            int height;
            int width;
            CrsGeoCoding geoCoding;
            width = MathUtils.floorInt(this.targetEnvelope.getSpan(0) /
                                               ((this.nativeResolution) ? Math.abs(this.targetStepX) : this.pixelSizeX));
            height = MathUtils.floorInt(this.targetEnvelope.getSpan(1) /
                                                ((this.nativeResolution) ? Math.abs(this.targetStepY): this.pixelSizeY));
            geoCoding = new CrsGeoCoding(targetCRS,
                    width,
                    height,
                    targetEnvelope.getLowerCorner().getOrdinate(0),
                    targetEnvelope.getUpperCorner().getOrdinate(1),
                    (this.nativeResolution) ? this.targetStepX : this.pixelSizeX,
                    (this.nativeResolution) ? this.targetStepY : this.pixelSizeY);
            final Product product = new Product("S2mosaic", "BEAM_MOSAIC", width, height);
            product.setSceneGeoCoding(geoCoding);
            final Dimension tileSize = JAIUtils.computePreferredTileSize(width, height, 1);
            product.setPreferredTileSize(tileSize);
            if (this.nativeResolution) {
                int levels = this.reprojectedProducts[0].getNumResolutionsMax();
                if (levels > product.getNumResolutionsMax()) {
                    product.setNumResolutionsMax(levels);
                }
            }
            return product;
        } catch (Exception e) {
            throw new OperatorException(e);
        }
    }

    private void addTargetBands(Product product) {
        if (this.nativeResolution) {
            int sceneWidth = product.getSceneRasterWidth();
            int sceneHeight = product.getSceneRasterHeight();
            for (Variable outputVariable : this.variables) {
                final int targetDataType;
                final Band firstSourceBand = this.reprojectedProducts[0].getBand(getSourceBandName(outputVariable.getExpression()));
                /*if (firstSourceBand.isScalingApplied()) {
                    targetDataType = firstSourceBand.getGeophysicalDataType();
                } else {*/
                    targetDataType = firstSourceBand.getDataType();
                /*}*/
                final AffineTransform affineTransformSourceBand =
                        firstSourceBand.getSourceImage().getModel().getImageToModelTransform(0);
                double stepX = Math.abs(affineTransformSourceBand.getScaleX());
                double stepY = Math.abs(affineTransformSourceBand.getScaleY());
                int bandWidth = MathUtils.floorInt(this.targetEnvelope.getSpan(0) / Math.abs(stepX));
                int bandHeight = MathUtils.floorInt(this.targetEnvelope.getSpan(1) / Math.abs(stepY));
                Band targetBand = new Band(outputVariable.getName(), targetDataType, bandWidth, bandHeight);
                targetBand.setDescription(firstSourceBand.getDescription());
                targetBand.setUnit(firstSourceBand.getUnit());
                targetBand.setScalingFactor(firstSourceBand.getScalingFactor());
                targetBand.setScalingOffset(firstSourceBand.getScalingOffset());
                targetBand.setLog10Scaled(firstSourceBand.isLog10Scaled());
                targetBand.setNoDataValue(firstSourceBand.getNoDataValue());
                //targetBand.setNoDataValueUsed(firstSourceBand.isNoDataValueUsed());
                //targetBand.setValidPixelExpression(firstSourceBand.getValidPixelExpression());
                targetBand.setSpectralWavelength(firstSourceBand.getSpectralWavelength());
                targetBand.setSpectralBandwidth(firstSourceBand.getSpectralBandwidth());

                ImageInfo sourceImageInfo = firstSourceBand.getImageInfo();
                if (sourceImageInfo != null) {
                    targetBand.setImageInfo(
                            new ImageInfo(
                                    new ColorPaletteDef(
                                            sourceImageInfo.getColorPaletteDef().getPoints()
                                    )));
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
        } else {
            for (Variable outputVariable : this.variables) {
                Band band = product.addBand(outputVariable.getName(), ProductData.TYPE_FLOAT32);
                band.setDescription(outputVariable.getExpression());
                final String countBandName = getCountBandName(outputVariable);
                band.setValidPixelExpression(String.format("%s > 0", Tokenizer.createExternalName(countBandName)));

                Band countBand = product.addBand(countBandName, ProductData.TYPE_INT32);
                countBand.setDescription(String.format("Count of %s", outputVariable.getName()));
            }
            if (this.conditions != null) {
                for (Condition condition : this.conditions) {
                    if (condition.isOutput()) {
                        Band band = product.addBand(condition.getName(), ProductData.TYPE_INT32);
                        band.setDescription(condition.getExpression());
                    }
                }
            }
        }
    }

    private void createSourceImagesNativeResolution() throws TransformException {
        int productType = 0;
        if (this.reprojectedProducts[0].getProductType().contains("S2")) {
            productType = 1;
        }
        for (Band band : this.targetProduct.getBands()) {
            Band[] srcBands = new Band[this.reprojectedProducts.length];
            for (int index = 0; index < this.reprojectedProducts.length; index++){
                srcBands[index] = this.reprojectedProducts[index].getBand(getSourceBandName(band.getName()));
            }
            final Dimension tileSize = JAIUtils.computePreferredTileSize(band.getRasterWidth(), band.getRasterHeight(), 1);
            final int levels = srcBands[0].getSourceImage().getModel().getLevelCount();
            S2MosaicMultiLevelSource bandSource = new S2MosaicMultiLevelSource(srcBands, band.getRasterWidth(), band.getRasterHeight(),
                    tileSize.width, tileSize.height, levels,
                    band.getGeoCoding(),
                    this.overlappingMethod, productType);
            band.setSourceImage(new DefaultMultiLevelImage(bandSource));
        }
    }


    private String getCountBandName(Variable outputVariable) {
        return String.format("%s_count", outputVariable.getName());
    }

    private String getSourceBandName(String variable){
        String bandName = null;
        for(Band Band : this.reprojectedProducts[0].getBands()){
            if (variable.equals(Band.getName())) {
                bandName=Band.getName();
                break;
            } else if (variable.contains(Band.getName())) {
                bandName=Band.getName();
                break;
            }
        }
        return bandName;
    }

    private Product generateSelectedBandsProduct(final Product product) {
        Product localProduct;
        AbstractSet<String> bandFromExpresion = new HashSet<>();
        if (product == null) {
            String msg = "Source product is null.";
            getLogger().warning(msg);
            throw new OperatorException("received product is null");
        }
        for (String bandName : product.getBandNames()) {
            if (this.variables != null) {
                for (Variable variable : this.variables) {
                    String variableExpression = variable.getExpression();
                    if (variableExpression.equals(bandName)) {
                        bandFromExpresion.add(bandName);
                        break;
                    } else if (variableExpression.contains(bandName)) {
                        bandFromExpresion.add(bandName);
                        break;
                    }
                }
            }
            if (this.conditions != null){
                for (Condition condition : this.conditions) {
                    String conditionExpression = condition.getExpression();
                    if (conditionExpression.equals(bandName)) {
                        bandFromExpresion.add(bandName);
                        break;
                    } else if (conditionExpression.contains(bandName)) {
                        bandFromExpresion.add(bandName);
                        break;
                    }
                }
            }
        }

        String[] BandNamesArray = bandFromExpresion.toArray(new String[bandFromExpresion.size()]);
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

    private Product[] createReprojectedProductsNativeResolution() {
        List<Product> reprojProductList = new ArrayList<>(this.sourceProducts.length);
        CoordinateReferenceSystem targetCRS = null;
        try {
            targetCRS = CRS.parseWKT(this.crs);
        } catch (FactoryException ignored) {
        }
        for (Product sourceProduct : this.sourceProducts) {
            GeoCoding sceneGeoCoding = sourceProduct.getSceneGeoCoding();
            if (sceneGeoCoding == null) {
                String msg = "Source product: '" + sourceProduct.getName() + "' contains no geo-coding. Skipped for further processing.";
                getLogger().warning(msg);
                continue;
            }
            if (!CRS.equalsIgnoreMetadata(sceneGeoCoding.getMapCRS(), targetCRS)) {
                final HashMap<String, Object> projParameters = createProjectionParameters();
                HashMap<String, Product> projProducts = new HashMap<>();
                projProducts.put("source", sourceProduct);
                //projProducts.put("reprojectedFirstProduct", (index == 0) ? null : reprojProductList.get(0));
                reprojProductList.add(GPF.createProduct("S2tbx-Reproject", projParameters, projProducts));
            } else {
                reprojProductList.add(sourceProduct);
            }
        }
        return reprojProductList.toArray(new Product[reprojProductList.size()]);
    }

    private Product[] createReprojectedProducts() {
        List<Product> reprojProductList = new ArrayList<>(this.sourceProducts.length);
        final HashMap<String, Object> projParameters = createProjectionParameters();
        for (Product reprojectedProduct : this.sourceProducts) {
            GeoCoding geoCoding = reprojectedProduct.getSceneGeoCoding();
            if (geoCoding == null) {
                String msg = "Source product: '" + reprojectedProduct.getName() + "' contains no geo-coding. Skipped for further processing.";
                getLogger().warning(msg);
                continue;
            }
            if (!targetCRS.getName().getCode().equals(geoCoding.getMapCRS().getName().getCode())) {
                HashMap<String, Product> projProducts = new HashMap<>();
                projProducts.put("source", reprojectedProduct);
                //projProducts.put("collocateWith", this.targetProduct);
                reprojProductList.add(GPF.createProduct("S2tbx-Reproject", projParameters, projProducts));
            } else {
                reprojProductList.add(reprojectedProduct);
            }
        }
        return reprojProductList.toArray(new Product[reprojProductList.size()]);
    }

    private HashMap<String, Object> createProjectionParameters() {
        HashMap<String, Object> projParameters = new HashMap<>();
        projParameters.put("resamplingName", this.resamplingName);
        projParameters.put("includeTiePointGrids", true);  // ensure tie-points are reprojected
        projParameters.put("crs", this.crs);
        if (this.orthorectify) {
            projParameters.put("orthorectify", true);
            projParameters.put("elevationModelName", this.elevationModelName);
        }
        return projParameters;
    }

    private Product[] resample(Product[] source, int targetWidth, int targetHeight, String resampleType) {
        List<Product> resampledProductList = new ArrayList<>(source.length);
        for (Product sourceProduct : source) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("referenceBandName", null);
            parameters.put("targetWidth", targetWidth);
            parameters.put("targetHeight", targetHeight);
            parameters.put("targetResolution",null);
            if (resampleType.equals(LOWER_RESOLUTION)) {
                parameters.put("downsampling","First");
            } else if (resampleType.equals(HIGHER_RESOLUTION)) {
                parameters.put("upsampling", "Nearest");
            }
            resampledProductList.add(GPF.createProduct("Resample", parameters, sourceProduct));
        }
        return resampledProductList.toArray(new Product[resampledProductList.size()]);
    }

    public static class Variable {

        @Parameter(description = "The name of the variable.")
        String name;
        @Parameter(description = "The expression of the variable.")
        String expression;

        public Variable() {
        }

        public Variable(String name, String expression) {
            this.name = name;
            this.expression = expression;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getExpression() {
            return this.expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }
    }

    public static class Condition {

        @Parameter(description = "The name of the condition.")
        String name;
        @Parameter(description = "The expression of the condition.")
        String expression;
        @Parameter(description = "Whether the result of the condition shall be written.")
        boolean output;

        public Condition() {
        }

        public Condition(String name, String expression, boolean output) {
            this.name = name;
            this.expression = expression;
            this.output = output;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getExpression() {
            return this.expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public boolean isOutput() {
            return this.output;
        }

        public void setOutput(boolean output) {
            this.output = output;
        }
    }
    public static Map<String, Object> getOperatorParameters(Product product) throws OperatorException {
        final MetadataElement graphElement = product.getMetadataRoot().getElement("Processing_Graph");
        if (graphElement == null) {
            throw new OperatorException("Product has no metadata element named 'Processing_Graph'");
        }
        final String operatorAlias = "S2tbx-Mosaic";
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
