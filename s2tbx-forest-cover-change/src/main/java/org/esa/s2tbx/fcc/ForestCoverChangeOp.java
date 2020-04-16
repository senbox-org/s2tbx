package org.esa.s2tbx.fcc;

import com.bc.ceres.core.ProgressMonitor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.esa.s2tbx.fcc.annotation.ParameterGroup;
import org.esa.s2tbx.fcc.common.AbstractWriteMasksTilesComputing;
import org.esa.s2tbx.fcc.common.WriteCombinedMasksTilesComputing;
import org.esa.s2tbx.fcc.common.WriteMaskTilesComputing;
import org.esa.s2tbx.fcc.descriptor.FCCLandCoverModelDescriptor;
import org.esa.s2tbx.fcc.common.ForestCoverChangeConstants;
import org.esa.s2tbx.fcc.trimming.ColorFillerTilesComputing;
import org.esa.s2tbx.fcc.trimming.MovingWindowTileParallelComputing;
import org.esa.s2tbx.grm.segmentation.product.WriteProductBandsTilesComputing;
import org.esa.s2tbx.fcc.trimming.ObjectsSelectionTilesComputing;
import org.esa.s2tbx.fcc.trimming.PixelStatistic;
import org.esa.s2tbx.fcc.trimming.UnionMasksTilesComputing;
import org.esa.s2tbx.grm.GenericRegionMergingOp;
import org.esa.s2tbx.grm.RegionMergingInputParameters;
import org.esa.s2tbx.grm.RegionMergingProcessingParameters;
import org.esa.s2tbx.grm.segmentation.tiles.SegmentationSourceProductPair;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.GeoPos;
import org.esa.snap.core.datamodel.ImageInfo;
import org.esa.snap.core.datamodel.IndexCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.dataop.resamp.ResamplingFactory;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.StringUtils;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.landcover.dataio.LandCoverFactory;
import org.esa.snap.landcover.gpf.AddLandCoverOp;
import org.esa.snap.utils.ProductHelper;
import org.esa.snap.utils.StringHelper;
import org.esa.snap.utils.matrix.IntMatrix;

import javax.media.jai.JAI;
import java.awt.Dimension;
import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Razvan Dumitrascu
 * @author Jean Coravu
 * @since 5.0.6
 */
@OperatorMetadata(
        alias = "ForestCoverChangeOp",
        version="1.0",
        category = "Optical/Thematic Land Processing",
        description = "Creates forest change masks out of two source products",
        authors = "Jean Coravu, Razvan Dumitrascu",
        copyright = "Copyright (C) 2017 by CS ROMANIA")
public class ForestCoverChangeOp extends Operator {

    private static final Logger logger = Logger.getLogger(ForestCoverChangeOp.class.getName());

    private static final String[] nameList;

    static {
        nameList = LandCoverFactory.getNameList();
    }

    @SourceProduct(alias = "recentProduct", label = "Recent Date Product", description = "The source product to be modified.")
    private Product currentSourceProduct;

    @SourceProduct(alias = "previousProduct", label = "Previous Date Product", description = "The source product to be modified.")
    private Product previousSourceProduct;

    @TargetProduct(description = "The target product which represents the operator's output.")
    private Product targetProduct;

    @Parameter(defaultValue = "95.0", label = "Forest Cover Percentage", itemAlias = "percentage", description = "Specifies the percentage of forest cover per segment")
    private float forestCoverPercentage;

    @ParameterGroup(alias = "Land Cover")
    @Parameter(label = "Name", description = "", defaultValue = ForestCoverChangeConstants.CCI_LAND_COVER_NAME)
    private String landCoverName;

    @ParameterGroup(alias = "Land Cover")
    @Parameter(label = "Map Forest Indices", defaultValue = ForestCoverChangeConstants.CCI_LAND_COVER_MAP_INDICES, description = "The indices of forest color from the new added land cover map")
    private String landCoverMapIndices;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Merging Cost Criterion",
            defaultValue = GenericRegionMergingOp.SPRING_MERGING_COST_CRITERION,
            description = "The method to compute the region merging.",
            valueSet = {GenericRegionMergingOp.SPRING_MERGING_COST_CRITERION, GenericRegionMergingOp.BAATZ_SCHAPE_MERGING_COST_CRITERION, GenericRegionMergingOp.FULL_LANDA_SCHEDULE_MERGING_COST_CRITERION})
    private String mergingCostCriterion;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Region Merging Criterion",
            defaultValue = GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION,
            description = "The method to check the region merging.",
            valueSet = {GenericRegionMergingOp.BEST_FITTING_REGION_MERGING_CRITERION, GenericRegionMergingOp.LOCAL_MUTUAL_BEST_FITTING_REGION_MERGING_CRITERION})
    private String regionMergingCriterion;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Total Iterations", defaultValue = "10", description = "The total number of iterations.")
    private int totalIterationsForSecondSegmentation;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Threshold", defaultValue = "5.0", description = "The threshold.")
    private float threshold;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Spectral Weight", defaultValue = "0.5", description = "The spectral weight.")
    private float spectralWeight;

    @ParameterGroup(alias = "Segmentation")
    @Parameter(label = "Shape Weight", defaultValue = "0.5" , description = "The shape weight.")
    private float shapeWeight;

    @ParameterGroup(alias = "Trimming")
    @Parameter(label = "Degrees Of Freedom", defaultValue = "3.3" , description = "Degrees of freedom used for the Chi distribution trimming process")
    private double degreesOfFreedom;

    @ParameterGroup(alias = "Product Masks")
    @Parameter(label = "Recent Date Product Mask", description = "A binary raster file to be added as mask to the output product")
    private File currentProductSourceMaskFile;

    @ParameterGroup(alias = "Product Masks")
    @Parameter(label = "Previous Date Product Mask", description = "A binary raster file to be added as mask to the output product")
    private File previousProductSourceMaskFile;

    private String[] currentProductBandsNames;
    private String[] previousProductBandsNames;

    private int threadCount;
    private ExecutorService threadPool;

    public ForestCoverChangeOp() {
        super();
    }

    @Override
    public void initialize() {
        validateSourceProducts();

        if (StringHelper.isNullOrEmpty(this.landCoverName)) {
            throw new OperatorException("No land cover name specified.");
        }
        if (StringHelper.isNullOrEmpty(this.landCoverMapIndices)) {
            throw new OperatorException("No land cover map indices specified.");
        }
        this.currentProductBandsNames = findBandNames(this.currentSourceProduct);
        this.previousProductBandsNames = findBandNames(this.previousSourceProduct);

        int sceneWidth = this.currentSourceProduct.getSceneRasterWidth();
        int sceneHeight = this.currentSourceProduct.getSceneRasterHeight();
        Dimension tileSize = JAI.getDefaultTileSize();
        this.targetProduct = new Product("ForestCoverChange", this.currentSourceProduct.getProductType(), sceneWidth, sceneHeight);
        this.targetProduct.setPreferredTileSize(tileSize);
        ProductUtils.copyGeoCoding(this.currentSourceProduct, this.targetProduct);
        Band targetBand = new Band("band_1", ProductData.TYPE_INT32, sceneWidth, sceneHeight);
        this.targetProduct.addBand(targetBand);

        this.threadCount = Runtime.getRuntime().availableProcessors() - 1;
        this.threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void doExecute(ProgressMonitor pm) throws OperatorException {
        long startTime = System.currentTimeMillis();

        String folderPath = System.getProperty("fcc.temp.folder.path");
        if (folderPath == null) {
            folderPath = System.getProperty("java.io.tmpdir");
        }
        String temporaryFolderName = "forest-cover-change" + Long.toString(System.currentTimeMillis());
        Path temporaryFolder = Paths.get(folderPath, temporaryFolderName);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start Forest Cover Change: image width: "+this.targetProduct.getSceneRasterWidth()+", image height: "+this.targetProduct.getSceneRasterHeight() + ", start time: " + new Date(startTime));
            logger.log(Level.FINE, "Temporary folder path to store the binary files: '" + temporaryFolder.toFile().getAbsolutePath() + "'");
        }

        try {
            Product currentExternalMaskProduct = null;
            Product previousExternalMaskProduct = null;
            if (this.currentProductSourceMaskFile != null) {
                currentExternalMaskProduct = ProductIO.readProduct(this.currentProductSourceMaskFile);
                if (currentExternalMaskProduct.getNumBands() != 1) {
                    throw new IllegalArgumentException("The current mask product '"+currentExternalMaskProduct.getName()+"' must contain only one raster.");
                }
            }
            if (this.previousProductSourceMaskFile != null) {
                previousExternalMaskProduct = ProductIO.readProduct(this.previousProductSourceMaskFile);
                if (previousExternalMaskProduct.getNumBands() != 1) {
                    throw new IllegalArgumentException("The previous mask product '"+previousExternalMaskProduct.getName()+"' must contain only one raster.");
                }
            }

            // create the temporary folder
            Files.createDirectories(temporaryFolder);

            ProductData productData = computeFinalProductData(currentExternalMaskProduct, previousExternalMaskProduct, temporaryFolder);

            Band targetBand = this.targetProduct.getBandAt(0);

            // reset the source image of the target product
            targetBand.setSourceImage(null);
            targetBand.setData(productData);
            targetBand.getSourceImage();

            FCCLandCoverModelDescriptor descriptor = new FCCLandCoverModelDescriptor();
            IndexCoding indexCoding = descriptor.getIndexCoding();
            targetBand.setSampleCoding(indexCoding);
            this.targetProduct.getIndexCodingGroup().add(indexCoding);

            ImageInfo imageInfo = descriptor.getImageInfo();
            imageInfo.getColorPaletteDef().setNumColors(256);
            targetBand.setImageInfo(imageInfo);

            if (logger.isLoggable(Level.FINE)) {
                long finishTime = System.currentTimeMillis();
                long totalSeconds = (finishTime - startTime) / 1000;
                logger.log(Level.FINE, ""); // add an empty line
                logger.log(Level.FINE, "Finish Forest Cover Change: image width: "+this.targetProduct.getSceneRasterWidth()+", image height: "+this.targetProduct.getSceneRasterHeight()+", total seconds: "+totalSeconds+", finish time: "+new Date(finishTime));
            }
        } catch (Exception ex) {
            throw new OperatorException(ex);
        } finally {
            this.threadPool.shutdown();
            FileUtils.deleteTree(temporaryFolder.toFile());
        }
    }

    private ProductData computeFinalProductData(Product currentExternalMaskProduct, Product previousExternalMaskProduct, Path temporaryParentFolder) throws Exception {
        FolderPathsResults currentFolderPathsResult = extractBands(this.currentSourceProduct, this.currentProductBandsNames, currentExternalMaskProduct, temporaryParentFolder);

        FolderPathsResults previousFolderPathsResult = extractBands(this.previousSourceProduct, this.previousProductBandsNames, previousExternalMaskProduct, temporaryParentFolder);

        IntMatrix colorFillerMatrix = computeColorFillerMatrix(temporaryParentFolder, currentFolderPathsResult, previousFolderPathsResult);

        int[] sourceBandIndices = new int[] {0, 1, 2};
        Dimension tileSize = getPreferredTileSize();

        int movingWindowWidth = 1000;//tileSize.width;
        int movingWindowHeight = 1000;//tileSize.height;
        Dimension movingWindowSize = new Dimension(movingWindowWidth, movingWindowHeight);

        int movingStepWidth = 500;//tileSize.width / 2;
        int movingStepHeight = 500;//tileSize.height / 2;
        Dimension movingStepSize = new Dimension(movingStepWidth, movingStepHeight);

        Path currentSourceSegmentationTilesFolder = currentFolderPathsResult.getTemporaryBandsFolder();
        IntSet currentTrimmingRegionKeys = computeMovingTrimming(colorFillerMatrix, movingWindowSize, movingStepSize, tileSize, currentSourceSegmentationTilesFolder, sourceBandIndices);

        Path previousSourceSegmentationTilesFolder = previousFolderPathsResult.getTemporaryBandsFolder();
        IntSet previousTrimmingRegionKeys = computeMovingTrimming(colorFillerMatrix, movingWindowSize, movingStepSize, tileSize, previousSourceSegmentationTilesFolder, sourceBandIndices);

        // run union masks
        ProductData productData = computeUnionMask(currentTrimmingRegionKeys, previousTrimmingRegionKeys, colorFillerMatrix);

        // reset the references
        WeakReference<IntSet> referenceCurrentTrimmingRegionKeys = new WeakReference<IntSet>(currentTrimmingRegionKeys);
        referenceCurrentTrimmingRegionKeys.clear();
        WeakReference<IntSet> referencePreviousTrimmingRegionKeys = new WeakReference<IntSet>(previousTrimmingRegionKeys);
        referencePreviousTrimmingRegionKeys.clear();
        WeakReference<IntMatrix> referenceProductColorFill = new WeakReference<IntMatrix>(colorFillerMatrix);
        referenceProductColorFill.clear();

        return productData;
    }

    private IntSet computeMovingTrimming(IntMatrix colorFillerMatrix, Dimension movingWindowSize, Dimension movingStepSize, Dimension tileSize,
                                       Path temporarySourceSegmentationTilesFolder, int[] sourceBandIndices)
                                       throws Exception {

        MovingWindowTileParallelComputing movingWindowTiles = new MovingWindowTileParallelComputing(colorFillerMatrix, movingWindowSize, movingStepSize, tileSize,
                                                                                                temporarySourceSegmentationTilesFolder, sourceBandIndices, this.degreesOfFreedom);
        return movingWindowTiles.runTilesInParallel(this.threadCount, this.threadPool);
    }

    private IntMatrix computeColorFillerMatrix(Path temporaryParentFolder, FolderPathsResults currentFolderPathsResult, FolderPathsResults previousFolderPathsResult)
                                               throws Exception {

        Path currentSourceSegmentationTilesFolder = currentFolderPathsResult.getTemporaryBandsFolder();
        Path previousSourceSegmentationTilesFolder = previousFolderPathsResult.getTemporaryBandsFolder();

        Dimension tileSize = getPreferredTileSize();

        RegionMergingProcessingParameters processingParameters = new RegionMergingProcessingParameters(this.threadCount, this.threadPool, this.targetProduct.getSceneRasterWidth(),
                                                                                            this.targetProduct.getSceneRasterHeight(), tileSize.width, tileSize.height);

        RegionMergingInputParameters inputParameters = new RegionMergingInputParameters(mergingCostCriterion, regionMergingCriterion,
                                                                                totalIterationsForSecondSegmentation, threshold, spectralWeight, shapeWeight);

        SegmentationSourceProductPair segmentationSourcePairs = new SegmentationSourceProductPair(currentSourceSegmentationTilesFolder, previousSourceSegmentationTilesFolder);

        IntMatrix segmentationMatrix = GenericRegionMergingOp.computeSegmentation(processingParameters, inputParameters, segmentationSourcePairs, temporaryParentFolder);

        // reset the references
        WeakReference<RegionMergingInputParameters> referenceInputParameters = new WeakReference<RegionMergingInputParameters>(inputParameters);
        referenceInputParameters.clear();
        WeakReference<SegmentationSourceProductPair> referenceSourcePairs = new WeakReference<SegmentationSourceProductPair>(segmentationSourcePairs);
        referenceSourcePairs.clear();

        IntSet validRegionsWith95Percentage = computeObjectsSelection(segmentationMatrix, this.currentSourceProduct, this.forestCoverPercentage, tileSize);

        Path currentMaskTilesFolder = currentFolderPathsResult.getTemporaryMaskFolder();
        Path previousMaskTilesFolder = previousFolderPathsResult.getTemporaryMaskFolder();

        ColorFillerTilesComputing tilesComputing = new ColorFillerTilesComputing(segmentationMatrix, validRegionsWith95Percentage, currentMaskTilesFolder,
                                                                                 previousMaskTilesFolder, tileSize.width, tileSize.height);

        IntMatrix colorFillerMatrix = tilesComputing.runTilesInParallel(this.threadCount, this.threadPool);

        // reset the references
        WeakReference<IntSet> referencePercentage = new WeakReference<IntSet>(validRegionsWith95Percentage);
        referencePercentage.clear();
        WeakReference<IntMatrix> referenceSegmentation = new WeakReference<IntMatrix>(segmentationMatrix);
        referenceSegmentation.clear();

        return colorFillerMatrix;
    }

    private Dimension getPreferredTileSize() {
        return this.targetProduct.getPreferredTileSize();
    }

    private IntSet computeObjectsSelection(IntMatrix originalSegmentationMatrix, Product extractedBandsSourceProduct, float percentagePixels, Dimension tileSize)
                                           throws Exception {

        IntSet landCoverValidPixels = new IntOpenHashSet();
        StringTokenizer str = new StringTokenizer(this.landCoverMapIndices, ", ");
        while (str.hasMoreElements()) {
            int pixelValue = Integer.parseInt(str.nextToken().trim());
            landCoverValidPixels.add(pixelValue);
        }
        Product landCoverProduct = buildLandCoverProduct(extractedBandsSourceProduct, this.landCoverName);

        ObjectsSelectionTilesComputing tilesComputing = new ObjectsSelectionTilesComputing(originalSegmentationMatrix, landCoverProduct, landCoverValidPixels, tileSize.width, tileSize.height);
        Int2ObjectMap<PixelStatistic> statistics = tilesComputing.runTilesInParallel(this.threadCount, this.threadPool);

        // reset the reference
        WeakReference<Product> referenceLandCoverProduct = new WeakReference<Product>(landCoverProduct);
        referenceLandCoverProduct.clear();

        IntSet validRegions = new IntOpenHashSet();
        ObjectIterator<Int2ObjectMap.Entry<PixelStatistic>> it = statistics.int2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Int2ObjectMap.Entry<PixelStatistic> entry = it.next();
            PixelStatistic value = entry.getValue();
            if (value.computePixelsPercentage() >= percentagePixels) {
                validRegions.add(entry.getIntKey());
            }
        }
        return validRegions;
    }

    private static Product buildLandCoverProduct(Product sourceProduct, String landCoverName) throws Exception {
        Product landCoverProduct = new Product(sourceProduct.getName(), sourceProduct.getProductType(), sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());
        landCoverProduct.setStartTime(sourceProduct.getStartTime());
        landCoverProduct.setEndTime(sourceProduct.getEndTime());
        landCoverProduct.setNumResolutionsMax(sourceProduct.getNumResolutionsMax());

        ProductUtils.copyMetadata(sourceProduct, landCoverProduct);
        ProductUtils.copyGeoCoding(sourceProduct, landCoverProduct);
        ProductUtils.copyTiePointGrids(sourceProduct, landCoverProduct);
        ProductUtils.copyVectorData(sourceProduct, landCoverProduct);

        AddLandCoverOp.LandCoverParameters param = new AddLandCoverOp.LandCoverParameters(landCoverName, ResamplingFactory.NEAREST_NEIGHBOUR_NAME);
        AddLandCoverOp.AddLandCover(landCoverProduct, param);

        return landCoverProduct;
    }

    private FolderPathsResults extractBands(Product sourceProduct, String[] sourceBandNames, Product externalMaskProduct, Path temporaryParentFolder) throws Exception {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Extract "+sourceBandNames.length+" bands for source product '" + sourceProduct.getName()+"'");
        }

        Product resampledSourceProduct = resampleAllBands(sourceProduct, sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());

        Dimension tileSize = getPreferredTileSize();

        WriteProductBandsTilesComputing bandsTilesComputing = new WriteProductBandsTilesComputing(resampledSourceProduct, sourceBandNames, tileSize.width, tileSize.height, temporaryParentFolder);
        Path temporaryFolder = bandsTilesComputing.runTilesInParallel(this.threadCount, this.threadPool);

        Path temporaryMaskFolder = null;
        if (externalMaskProduct == null) {
            if (isSentinelProduct(sourceProduct)) {
                if(productContainsMasks(sourceProduct)) {
                    WriteCombinedMasksTilesComputing writeMaskTilesComputing = new WriteCombinedMasksTilesComputing(resampledSourceProduct, ForestCoverChangeConstants.SENTINEL_MASK_NAMES,
                            tileSize.width, tileSize.height, temporaryParentFolder);
                    temporaryMaskFolder = writeMaskTilesComputing.runTilesInParallel(this.threadCount, this.threadPool);
                }
            }

            // reset the reference
            WeakReference<Product> referenceResampleSourceProduct = new WeakReference<Product>(resampledSourceProduct);
            referenceResampleSourceProduct.clear();
        } else {
            // reset the reference
            WeakReference<Product> referenceResampleSourceProduct = new WeakReference<Product>(resampledSourceProduct);
            referenceResampleSourceProduct.clear();

            Product resampledMaskProduct = resampleAllBands(externalMaskProduct, sourceProduct.getSceneRasterWidth(), sourceProduct.getSceneRasterHeight());

            Band band = resampledMaskProduct.getBandGroup().get(0);
            WriteMaskTilesComputing writeMaskTilesComputing = new WriteMaskTilesComputing(band, tileSize.width, tileSize.height, temporaryParentFolder);
            temporaryMaskFolder = writeMaskTilesComputing.runTilesInParallel(this.threadCount, this.threadPool);

            // reset the references
            WeakReference<Product> referenceExternalMaskProduct = new WeakReference<Product>(externalMaskProduct);
            referenceExternalMaskProduct.clear();
            WeakReference<Product> referenceResampleMaskProduct = new WeakReference<Product>(resampledMaskProduct);
            referenceResampleMaskProduct.clear();
        }

        return new FolderPathsResults(temporaryFolder, temporaryMaskFolder);
    }

    private static boolean productContainsMasks(Product sourceProduct) {
        for(int k = 0; k < ForestCoverChangeConstants.SENTINEL_MASK_NAMES.length; k++) {
            Mask sourceMask = sourceProduct.getMaskGroup().get(ForestCoverChangeConstants.SENTINEL_MASK_NAMES[k]);
            if(sourceMask == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSentinelProduct(Product product) {
        return StringHelper.startsWithIgnoreCase(product.getProductType(), "S2_MSI_Level");
    }

    private static Product resampleAllBands(Product sourceProduct, int targetWidth, int targetHeight) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Resample the bands for source product '" + sourceProduct.getName()+"'");
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("targetWidth", targetWidth);
        parameters.put("targetHeight", targetHeight);
        Product targetProduct = GPF.createProduct("Resample", parameters, sourceProduct);
        targetProduct.setName(sourceProduct.getName());
        return targetProduct;
    }

    private ProductData computeUnionMask(IntSet currentSegmentationTrimmingRegionKeys, IntSet previousSegmentationTrimmingRegionKeys, IntMatrix colorFillerMatrix)
                                         throws Exception {

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Start running union mask");
        }
        Dimension tileSize = getPreferredTileSize();
        UnionMasksTilesComputing tilesComputing = new UnionMasksTilesComputing(colorFillerMatrix, currentSegmentationTrimmingRegionKeys,
                                                                               previousSegmentationTrimmingRegionKeys, tileSize.width, tileSize.height);
        return tilesComputing.runTilesInParallel(threadCount, threadPool);
    }

    private void validateSourceProducts() {
        if (this.currentSourceProduct.getNumBands() < 4) {
            String message = String.format("The current source product '%s' does not contain minimum number of source bands needed.",
                    this.currentSourceProduct.getName());
            throw new OperatorException(message);
        }
        if (this.previousSourceProduct.getNumBands() < 4) {
            String message = String.format("The previous source product '%s' does not contain minimum number of source bands needed.",
                    this.previousSourceProduct.getName());
            throw new OperatorException(message);
        }
        if (this.currentSourceProduct.getSceneRasterWidth() != this.previousSourceProduct.getSceneRasterWidth()
                || this.currentSourceProduct.getSceneRasterHeight() != this.previousSourceProduct.getSceneRasterHeight()) {

            String message = String.format("Source products '%s' and '%s' do not have the same raster sizes.",
                    this.currentSourceProduct.getName(),
                    this.previousSourceProduct.getName());
            throw new OperatorException(message);
        }
        final GeoCoding geoCodingCurrentProduct = currentSourceProduct.getSceneGeoCoding();
        GeoPos currentProductMinPoint = geoCodingCurrentProduct.getGeoPos(new PixelPos(0, 0), null);
        GeoPos currentProductMaxPoint = geoCodingCurrentProduct.getGeoPos(new PixelPos(currentSourceProduct.getSceneRasterWidth(),
                currentSourceProduct.getSceneRasterHeight()), null);
        final GeoCoding geoCodingPreviousProduct = previousSourceProduct.getSceneGeoCoding();
        GeoPos previousProductMinPoint = geoCodingPreviousProduct.getGeoPos(new PixelPos(0, 0), null);
        GeoPos previousProductMaxPoint = geoCodingPreviousProduct.getGeoPos(new PixelPos(previousSourceProduct.getSceneRasterWidth(),
                previousSourceProduct.getSceneRasterHeight()), null);
        if ((currentProductMinPoint.getLat() != previousProductMinPoint.getLat())||(currentProductMinPoint.getLon() != previousProductMinPoint.getLon())){
            String message = String.format("Source products '%s' and '%s' do not have the same geoCoding.",
                    this.currentSourceProduct.getName(),
                    this.previousSourceProduct.getName());
            throw new OperatorException(message);
        }
        if ((currentProductMaxPoint.getLat() != previousProductMaxPoint.getLat())||(currentProductMaxPoint.getLon() != previousProductMaxPoint.getLon())){
            String message = String.format("Source products '%s' and '%s' do not have the same geoCoding.",
                    this.currentSourceProduct.getName(),
                    this.previousSourceProduct.getName());
            throw new OperatorException(message);
        }
    }

    private static String[] findBandNames(Product product) {
        String red = findBand(ForestCoverChangeConstants.MINIMUM_SPECTRAL_WAVE_LENGTH_RED_BAND,
                ForestCoverChangeConstants.MAXIMUM_SPECTRAL_WAVE_LENGTH_RED_BAND,
                product);
        String nir = findBand(ForestCoverChangeConstants.MINIMUM_SPECTRAL_WAVE_LENGTH_NIR_BAND,
                ForestCoverChangeConstants.MAXIMUM_SPECTRAL_WAVE_LENGTH_NIR_BAND,
                product);
        String swir = findBand(ForestCoverChangeConstants.MINIMUM_SPECTRAL_WAVE_LENGTH_SWIR_BAND,
                ForestCoverChangeConstants.MAXIMUM_SPECTRAL_WAVE_LENGTH_SWIR_BAND,
                product);
        String swir2 = findBand(ForestCoverChangeConstants.MINIMUM_SPECTRAL_WAVE_LENGTH_SWIR2_BAND,
                ForestCoverChangeConstants.MAXIMUM_SPECTRAL_WAVE_LENGTH_SWIR2_BAND,
                product);
        return new String[] {red, nir, swir, swir2};
    }

    private static String findBand(float minWavelength, float maxWavelength, Product product) {
        String bestBand = null;
        float minDelta = Float.MAX_VALUE;
        float mean = (minWavelength + maxWavelength) / 2;
        for (Band band : product.getBands()) {
            float bandWavelength = band.getSpectralWavelength();
            if (bandWavelength != 0.0F) {
                float delta = Math.abs(bandWavelength - mean);
                if (delta < minDelta) {
                    bestBand = band.getName();
                    minDelta = delta;
                }
            }
        }
        if (bestBand == null) {
            String message = String.format("Source product '%s' does not contain a band that has a wave length between '%s' and '%s'.",
                    product.getName(),
                    minWavelength,
                    maxWavelength);
            throw new OperatorException(message);
        }
        return bestBand;
    }

    private static class FolderPathsResults {
        private final Path temporaryBandsFolder;
        private final Path temporaryMaskFolder;

        FolderPathsResults(Path temporaryBandsFolder, Path temporaryMaskFolder) {
            this.temporaryBandsFolder = temporaryBandsFolder;
            this.temporaryMaskFolder = temporaryMaskFolder;
        }

        public Path getTemporaryBandsFolder() {
            return temporaryBandsFolder;
        }

        public Path getTemporaryMaskFolder() {
            return temporaryMaskFolder;
        }
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(ForestCoverChangeOp.class);
        }
    }
}
