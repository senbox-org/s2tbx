package org.esa.s2tbx.dataio.worldview2;

import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.worldview2.common.WorldView2Constants;
import org.esa.s2tbx.dataio.worldview2.internal.MosaicMultiLevelSource;
import org.esa.s2tbx.dataio.worldview2.metadata.TileComponent;
import org.esa.s2tbx.dataio.worldview2.metadata.TileMetadata;
import org.esa.s2tbx.dataio.worldview2.metadata.WorldView2Metadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.jai.JAIUtils;
import org.esa.snap.ui.ModalDialog;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Basic reader for WorldView 2 products.
 *
 * @author Razvan Dumitrascu
 */

class WorldView2ProductReader extends AbstractProductReader {

    private static final Logger logger = Logger.getLogger(WorldView2ProductReader.class.getName());

    private static final String EXCLUSION_STRING = "README";
    private VirtualDirEx productDirectory;
    private WorldView2Metadata metadata;
    private Product product;
    private String productSelected;
    private HashMap<Product, String> tilesMultiSpectral;
    private HashMap<Product, String> tilesPanchromatic;
    private int bandDataType;
    private int numMultiSpectralBands;

    /**
     * Constructs a new abstract product reader.
     *
     * @param readerPlugIn the reader plug-in which created this reader, can be {@code null} for internal reader
     *                     implementations
     */
    WorldView2ProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        this.tilesMultiSpectral = new HashMap<>();
        tilesPanchromatic = new HashMap<>();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        final Path inputPath = BaseProductReaderPlugIn.convertInputToPath(super.getInput());
        this.productDirectory = VirtualDirEx.build(inputPath);
        final String inputFile = productDirectory.findFirst(WorldView2Constants.METADATA_FILE_SUFFIX);
        final File filePath = productDirectory.getFile(inputFile);
        this.metadata = WorldView2Metadata.create(filePath.toPath());
        final String[] products = getProductsFromMetadata(this.metadata);
        if (products.length > 1) {
            ModalDialog dialog = new ModalDialog(null, "Product Selector", ModalDialog.ID_OK_CANCEL, "");
            dialog.setContent(createDialogContent(products));
            final int show = dialog.show();
            if (show == ModalDialog.ID_CANCEL) {
                productSelected = null;
            }
        } else {
            this.productSelected = products[0];
        }
        if (productSelected == null) {
            throw new IOException("No product has been selected");
        }
        if (metadata != null) {
            final List<String> selectedProductFiles = new ArrayList<>();
            for (String file : metadata.getAttributeValues(WorldView2Constants.PATH_FILE_LIST)) {
                if (file.contains(productSelected)) {
                    if (file.contains(WorldView2Constants.IMAGE_EXTENSION)) {
                        if (!selectedProductFiles.contains(file)) {
                            selectedProductFiles.add(productDirectory.getFile(file).toString());
                        }
                    }
                    if (file.contains(WorldView2Constants.METADATA_EXTENSION)) {
                        if (!selectedProductFiles.contains(file)) {
                            selectedProductFiles.add(file);
                        }
                    }
                }
            }
            final List<TileMetadata> tileMetadataList = new ArrayList<>();
            for (String fileMetadata : selectedProductFiles) {
                if (fileMetadata.endsWith(WorldView2Constants.METADATA_EXTENSION)) {
                    this.metadata = WorldView2Metadata.create(filePath.toPath());
                    TileMetadata tileMetadata;
                    tileMetadata = TileMetadata.create(productDirectory.getFile(fileMetadata).toPath());
                    tileMetadataList.add(tileMetadata);
                }
            }
            if (!tileMetadataList.isEmpty()) {
                int width = 0;
                int height = 0;
                double stepSize = 0.0;
                double originX = 0.0;
                double originY = 0.0;
                String crsCode = null;
                for (TileMetadata tileMetadata : tileMetadataList) {
                    TileComponent tileComponent = tileMetadata.getTileComponent();
                    if (tileComponent.getBandID().equals("P")) {
                        width = tileComponent.getNumColumns();
                        height = tileComponent.getNumRows();
                        stepSize = tileComponent.getStepSize();
                        originX = tileComponent.getOriginX();
                        originY = tileComponent.getOriginY();
                        crsCode = tileComponent.computeCRSCode();
                    }

                }
                this.product = new Product(productSelected, WorldView2Constants.PRODUCT_TYPE, width, height);
                this.product.setStartTime(this.metadata.getProductStartTime());
                this.product.setEndTime(this.metadata.getProductEndTime());
                this.product.setDescription(this.metadata.getProductDescription());
                this.product.setProductReader(this);
                this.product.setFileLocation(this.metadata.getPath().toFile());
                for (TileMetadata tileMetadata : tileMetadataList) {
                    this.product.getMetadataRoot().addElement(tileMetadata.getRootElement());
                }
                try {
                    assert crsCode != null;
                    GeoCoding geoCoding = new CrsGeoCoding(CRS.decode(crsCode),
                                                           width, height,
                                                           originX, originY,
                                                           stepSize, stepSize);
                    product.setSceneGeoCoding(geoCoding);
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }
                generateProductLists(selectedProductFiles, tileMetadataList);
                final int levels = getProductLevels();
                for (TileMetadata tileMetadata : tileMetadataList) {
                    String[] bandNames;
                    if (numMultiSpectralBands < 4) {
                        bandNames = WorldView2Constants.NATURAL_COLORS;
                    }
                    if (numMultiSpectralBands == 4) {
                        bandNames = WorldView2Constants.BAND_NAMES_MULTISPECTRAL_4_BANDS;
                    } else {
                        bandNames = WorldView2Constants.BAND_NAMES_MULTISPECTRAL_8_BANDS;
                    }
                    if (tileMetadata.getTileComponent().getBandID().equals("MS1") ||
                            tileMetadata.getTileComponent().getBandID().equals("Multi")) {
                        TileComponent tileComp = tileMetadata.getTileComponent();
                        for (int index = 0; index < this.numMultiSpectralBands; index++) {
                            Band targetBand = createTargetBand(levels, bandNames, index, this.tilesMultiSpectral, tileComp);
                            this.product.addBand(targetBand);
                        }
                    } else {
                        TileComponent tileComp = tileMetadata.getTileComponent();
                        Band targetBand = createTargetBand(levels, new String[]{bandNames[bandNames.length - 1]}, 0, this.tilesPanchromatic, tileComp);
                        this.product.addBand(targetBand);
                    }
                }
            }
        }
        return this.product;
    }

    private Band createTargetBand(final int levels, final String[] bandNames, final int index, final HashMap<Product, String> tiles, final TileComponent tileComp) {
        final Band targetBand = new Band(bandNames[index], this.bandDataType,
                                         tileComp.getNumColumns(), tileComp.getNumRows());
        final Band band = setInputSpecificationBand(tiles, index);
        final Dimension tileSize = JAIUtils.computePreferredTileSize(band.getRasterWidth(), band.getRasterHeight(), 1);
        setBandProperties(targetBand, band);
        initBandsGeoCoding(targetBand, tileComp);
        final Map<Band, String> srcBands = getBandTiles(tiles, index);
        final MosaicMultiLevelSource bandSource =
                new MosaicMultiLevelSource(srcBands,
                                           targetBand.getRasterWidth(), targetBand.getRasterHeight(),
                                           tileSize.width, tileSize.height,
                                           levels, tileComp,
                                           targetBand.getGeoCoding() != null ?
                                                   Product.findImageToModelTransform(targetBand.getGeoCoding()) :
                                                   Product.findImageToModelTransform(product.getSceneGeoCoding()));
        targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));
        targetBand.setScalingFactor(tileComp.getScalingFactor(targetBand.getName()));
        return targetBand;
    }

    private Band setInputSpecificationBand(final HashMap<Product, String> map, final int index) {
        Map.Entry<Product, String> entry = map.entrySet().iterator().next();
        Product p = entry.getKey();
        return p.getBandAt(index);
    }

    private Map<Band, String> getBandTiles(final HashMap<Product, String> tiles, final int index) {
        HashMap<Band, String> map = new HashMap<>();
        for (Map.Entry<Product, String> entry : tiles.entrySet()) {
            map.put(entry.getKey().getBandAt(index), entry.getValue());
        }
        return map;
    }

    private int getProductLevels() {
        Map.Entry<Product, String> entryFirst = this.tilesMultiSpectral.entrySet().iterator().next();
        int levels = entryFirst.getKey().getBandAt(0).getSourceImage().getModel().getLevelCount();
        int levelsMultiSpectral = getLevel(this.tilesMultiSpectral, levels);
        int levelsPanchromatic = getLevel(this.tilesPanchromatic, levels);
        if (levelsMultiSpectral < levels) {
            levels = levelsMultiSpectral;
        } else if (levelsPanchromatic < levels) {
            levels = levelsPanchromatic;
        }
        if (levels > product.getNumResolutionsMax()) {
            product.setNumResolutionsMax(levels);
        }
        return levels;
    }

    private int getLevel(final HashMap<Product, String> tiles, final int levels) {
        int level = levels;
        for (Map.Entry<Product, String> entry : tiles.entrySet()) {
            Product p = entry.getKey();
            for (Band band : p.getBands()) {
                int bandLevel = band.getSourceImage().getModel().getLevelCount();
                if (bandLevel < level) {
                    level = bandLevel;
                }
            }
        }
        return level;
    }

    private void initBandsGeoCoding(final Band targetBand, final TileComponent tileComp) {
        GeoCoding geoCoding = null;
        int width = tileComp.getNumColumns();
        int height = tileComp.getNumRows();
        double stepSize = tileComp.getStepSize();
        double originX = tileComp.getOriginX();
        double originY = tileComp.getOriginY();
        String crsCode = tileComp.computeCRSCode();
        try {
            assert crsCode != null;
            CoordinateReferenceSystem crs = CRS.decode(crsCode);
            geoCoding = new CrsGeoCoding(crs,
                                         width, height,
                                         originX, originY,
                                         stepSize, stepSize, 0.0, 0.0);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        targetBand.setGeoCoding(geoCoding);
    }

    private void setBandProperties(final Band targetBand, final Band band) {
        targetBand.setSpectralBandIndex(band.getSpectralBandIndex());
        targetBand.setSpectralWavelength(band.getSpectralWavelength());
        targetBand.setSpectralBandwidth(band.getSpectralBandwidth());
        targetBand.setSolarFlux(band.getSolarFlux());
        targetBand.setUnit(band.getUnit());
        targetBand.setNoDataValue(band.getNoDataValue());
        targetBand.setNoDataValueUsed(true);
        targetBand.setScalingOffset(band.getScalingOffset());
        targetBand.setDescription(band.getDescription());
    }

    private void generateProductLists(final List<String> selectedProductFiles, final List<TileMetadata> tileMetadataList) throws IOException {
        for (TileMetadata tileMetadata : tileMetadataList) {
            final TileComponent tileComponent = tileMetadata.getTileComponent();
            for (int filesIndex = 0; filesIndex < tileComponent.getNumOfTiles(); filesIndex++) {
                String filePath = null;
                for (String filePaths : selectedProductFiles) {
                    if (filePaths.contains(tileComponent.getTileNames()[filesIndex])) {
                        filePath = filePaths;
                    }
                }
                if (filePath != null) {
                    final Path path = metadata.getPath().getParent().resolve(filePath);
                    final Product p = ProductIO.readProduct(path.toFile());
                    this.bandDataType = p.getBandAt(0).getDataType();
                    if (tileComponent.getBandID().equals("P")) {
                        this.tilesPanchromatic.put(p, tileComponent.getTileNames()[filesIndex]);
                    } else {
                        this.tilesMultiSpectral.put(p, tileComponent.getTileNames()[filesIndex]);
                        if (this.numMultiSpectralBands == 0) {
                            this.numMultiSpectralBands = p.getNumBands();
                        }
                    }
                } else {
                    logger.warning(tileComponent.getTileNames()[filesIndex] + " is missing");

                }
            }
        }
    }

    private String[] getProductsFromMetadata(final WorldView2Metadata metadata) {
        Set<String> products = new HashSet<>();
        final String[] fileNames = metadata.getAttributeValues(WorldView2Constants.PATH_FILE_LIST);
        for (String file : fileNames) {
            if (file.contains(WorldView2Constants.METADATA_EXTENSION) &&
                    !file.contains(EXCLUSION_STRING)) {
                String filename = file.substring(file.lastIndexOf("/") + 1, file.lastIndexOf(WorldView2Constants.METADATA_EXTENSION));
                String value = filename.substring(0, filename.indexOf("-"));
                if (metadata.getImageDirectoryPath() == null) {
                    metadata.setImageDirectoryPath(productDirectory.getBasePath());
                }
                products.add(value);
            }
        }
        return products.toArray(new String[0]);
    }

    private JPanel createDialogContent(final String[] productNames) {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(6, 6, 6, 6));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets.top = 2;
        constraints.insets.bottom = 2;
        constraints.insets.left = 2;
        constraints.insets.right = 2;
        constraints.gridx = 0;
        constraints.gridy = -1;
        constraints.gridy++;
        content.add(new JLabel("Input file contains " + productNames.length + "  acquisition time products"), constraints);
        constraints.gridy++;
        constraints.gridy++;
        content.add(new JLabel("Select product to be read "), constraints);
        ButtonGroup group = new ButtonGroup();
        for (String name : productNames) {
            constraints.gridy++;
            JRadioButton jButton = new JRadioButton(name);
            jButton.addActionListener(e -> setProductSelected(jButton.getText()));
            group.add(jButton);
            content.add(jButton, constraints);
        }
        return content;
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY,
                                          int sourceWidth, int sourceHeight,
                                          int sourceStepX, int sourceStepY,
                                          Band destBand,
                                          int destOffsetX, int destOffsetY,
                                          int destWidth, int destHeight,
                                          ProductData destBuffer, com.bc.ceres.core.ProgressMonitor pm) throws IOException {
    }

    @Override
    public void close() throws IOException {
        System.gc();
        if (product != null) {
            for (Band band : product.getBands()) {
                MultiLevelImage sourceImage = band.getSourceImage();
                if (sourceImage != null) {
                    sourceImage.reset();
                    sourceImage.dispose();
                }
            }
        }
        if (this.productDirectory != null) {
            this.productDirectory.close();
            this.productDirectory = null;
        }
        if (this.metadata != null) {
            this.metadata = null;
        }
        if (this.tilesPanchromatic != null) {
            this.tilesPanchromatic.clear();
            this.tilesPanchromatic = null;
        }
        if (this.tilesMultiSpectral != null) {
            this.tilesMultiSpectral.clear();
            this.tilesMultiSpectral = null;
        }
        super.close();
    }

    private void setProductSelected(final String productSelected) {
        this.productSelected = productSelected;
    }
}