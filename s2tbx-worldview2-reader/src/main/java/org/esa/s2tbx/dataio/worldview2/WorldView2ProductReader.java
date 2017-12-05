package org.esa.s2tbx.dataio.worldview2;

import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.esa.s2tbx.dataio.VirtualDirEx;
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

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
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
    private String productSelected ;
    private HashMap<Product, String> tilesMultispectral;
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
        this.tilesMultispectral = new HashMap<>();
        tilesPanchromatic = new HashMap<>();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        WorldView2ProductReaderPlugin readerPlugin = (WorldView2ProductReaderPlugin)getReaderPlugIn();
        final File inputFile = getInputFile();
        this.productDirectory = readerPlugin.getInput(getInput());
        this.metadata = WorldView2Metadata.create(inputFile.toPath());
        ModalDialog dialog = new ModalDialog(null, "Product Selector",ModalDialog.ID_OK_CANCEL, "" );
        String[] products = getProductsFromMetadata(this.metadata);
        dialog.setContent(createDialogContent(products));
        final int show = dialog.show();
        if (show == ModalDialog.ID_CANCEL) {
            return null;
        }
        if(productSelected == null) {
            throw new IOException("No product has been selected");
        }
        if(metadata != null) {
            Set<String> selectedProductFiles = new HashSet<>();
            for (String file : metadata.getAttributeValues(WorldView2Constants.PATH_FILE_LIST)) {
                if (file.contains(productSelected) && (file.contains(WorldView2Constants.METADATA_EXTENSION) ||
                        file.contains(WorldView2Constants.IMAGE_EXTENSION))) {
                    selectedProductFiles.add(file);
                }
            }
            List<TileMetadata> tileMetadataList = new ArrayList<>();
            for (String fileMetadata : selectedProductFiles) {
                if (fileMetadata.endsWith(WorldView2Constants.METADATA_EXTENSION)) {
                    TileMetadata tileMetadata = TileMetadata.create(productDirectory.getFile(fileMetadata).toPath());
                    tileMetadataList.add(tileMetadata);
                }
            }
            if (tileMetadataList.size() != 0) {
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
                this.product.setFileLocation(inputFile);
                for (TileMetadata tileMetadata: tileMetadataList) {
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
                generateProductLists(selectedProductFiles,tileMetadataList );
                int levels = getProductLevels();
                for (TileMetadata tileMetadata: tileMetadataList) {
                    String[] bandNames;
                    if(numMultiSpectralBands == 4){
                        bandNames = WorldView2Constants.BAND_NAMES_MULTISPECTRAL_4_BANDS;
                    } else {
                        bandNames = WorldView2Constants.BAND_NAMES_MULTISPECTRAL_8_BANDS;
                    }
                    if(tileMetadata.getTileComponent().getBandID().equals("MS1")) {
                        TileComponent tileComp = tileMetadata.getTileComponent();
                        for (int index = 0; index < this.numMultiSpectralBands; index++) {
                            Band targetBand = createTargetBand(levels,bandNames, index, this.tilesMultispectral, tileComp);
                            this.product.addBand(targetBand);
                        }
                    } else {
                        TileComponent tileComp = tileMetadata.getTileComponent();
                        Band targetBand = createTargetBand(levels,new String[] {bandNames[bandNames.length-1]}, 0, this.tilesPanchromatic, tileComp);
                        this.product.addBand(targetBand);
                    }
                }
            }
        }
        return this.product;
    }

    private Band createTargetBand(int levels,String[] bandNames, int index, HashMap<Product, String> tiles, TileComponent tileComp) {
        Band targetBand = new Band(bandNames[index], this.bandDataType,
                tileComp.getNumColumns(), tileComp.getNumRows());
        Band band = setInputSpecificationBand(tiles, index);
        final Dimension tileSize = JAIUtils.computePreferredTileSize(band.getRasterWidth(), band.getRasterHeight(), 1);
        setBandProperties(targetBand, band);
        initBandsGeoCoding(targetBand,tileComp);
        Map<Band, String> srcBands = getBandTiles(tiles, index);
        MosaicMultiLevelSource bandSource =
                new MosaicMultiLevelSource(srcBands,
                        targetBand.getRasterWidth(), targetBand.getRasterHeight(),
                        tileSize.width, tileSize.height,
                        levels,tileComp,
                        targetBand.getGeoCoding() != null ?
                                Product.findImageToModelTransform(targetBand.getGeoCoding()) :
                                Product.findImageToModelTransform(product.getSceneGeoCoding()));
        targetBand.setSourceImage(new DefaultMultiLevelImage(bandSource));
        return targetBand;
    }

    private Band setInputSpecificationBand(HashMap<Product, String> map, int index ) {
        Map.Entry<Product, String> entry = map.entrySet().iterator().next();
        Product p = entry.getKey();
        return p.getBandAt(index);
    }

    private Map<Band,String> getBandTiles(HashMap<Product, String> tiles, int index) {
        HashMap<Band, String> map = new HashMap<>();
        for(Map.Entry<Product,String> entry: tiles.entrySet() ){
            map.put(entry.getKey().getBandAt(index), entry.getValue());
        }
        return map;
    }

    private int  getProductLevels() {
        Map.Entry<Product, String> entryFirst = this.tilesMultispectral.entrySet().iterator().next();
        int levels = entryFirst.getKey().getBandAt(0).getSourceImage().getModel().getLevelCount();
        int levelsMultiSpectral = getLevel(this.tilesMultispectral, levels);
        int levelsPanchromatic = getLevel(this.tilesPanchromatic, levels);
        if(levelsMultiSpectral< levels){
            levels = levelsMultiSpectral;
        } else if (levelsPanchromatic < levels) {
            levels = levelsPanchromatic;
        }
        if (levels > product.getNumResolutionsMax()) {
            product.setNumResolutionsMax(levels);
        }
        return levels;
    }

    private int getLevel(HashMap<Product, String> tiles, int levels) {
        int level = levels;
        for( Map.Entry<Product, String> entry : tiles.entrySet()) {
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

    private void initBandsGeoCoding(Band targetBand, TileComponent tileComp) {
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
        } catch(Exception e) {
            logger.warning(e.getMessage());
        }
        targetBand.setGeoCoding(geoCoding);
    }

    private void setBandProperties(Band targetBand, Band band) {
        targetBand.setSpectralBandIndex(band.getSpectralBandIndex());
        targetBand.setSpectralWavelength(band.getSpectralWavelength());
        targetBand.setSpectralBandwidth(band.getSpectralBandwidth());
        targetBand.setSolarFlux(band.getSolarFlux());
        targetBand.setUnit(band.getUnit());
        targetBand.setNoDataValue(band.getNoDataValue());
        targetBand.setNoDataValueUsed(true);
        targetBand.setScalingFactor(band.getScalingFactor());
        targetBand.setScalingOffset(band.getScalingOffset());
        targetBand.setUnit(band.getUnit());
        targetBand.setDescription(band.getDescription());
    }

    private void generateProductLists( Set<String> selectedProductFiles, List<TileMetadata>tileMetadataList) throws IOException{
        for (TileMetadata tileMetadata : tileMetadataList) {
            TileComponent tileComponent = tileMetadata.getTileComponent();
            for(int filesIndex = 0; filesIndex < tileComponent.getNumOfTiles(); filesIndex++) {
                String filePath = null;
                for (String filePaths : selectedProductFiles) {
                    if (filePaths.contains(tileComponent.getTileNames()[filesIndex])) {
                        filePath = filePaths;
                    }
                }
                Product p = ProductIO.readProduct(Paths.get(productDirectory.getBasePath()).resolve(filePath).toFile());
                this.bandDataType = p.getBandAt(0).getDataType();
                if (tileComponent.getBandID().equals("P")) {
                    this.tilesPanchromatic.put(p, tileComponent.getTileNames()[filesIndex]);
                } else {
                    this.tilesMultispectral.put(p, tileComponent.getTileNames()[filesIndex]);
                    if (this.numMultiSpectralBands ==0) {
                        this.numMultiSpectralBands = p.getNumBands();
                    }
                }
            }
        }
    }

    private String[] getProductsFromMetadata(WorldView2Metadata metadata) {
        Set<String> products = new HashSet<>();
        String [] fileNames = metadata.getAttributeValues(WorldView2Constants.PATH_FILE_LIST);
        for(String file : fileNames) {
            if (file.contains(WorldView2Constants.METADATA_EXTENSION) &&
                    !file.contains(EXCLUSION_STRING)) {
                String filename = file.substring(file.lastIndexOf("/")+1,file.lastIndexOf(WorldView2Constants.METADATA_EXTENSION));
                String value = filename.substring(0,filename.indexOf("-"));
                products.add(value);
            }
        }
        return products.toArray(new String[0]);
    }

    private JPanel createDialogContent(String[] productNames) {
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
        content.add(new JLabel("Input file contains " + productNames.length +"  acquisition time products" ), constraints);
        constraints.gridy++;
        constraints.gridy++;
        content.add(new JLabel("Select product to be read "), constraints);
        ButtonGroup group = new ButtonGroup();
        for (String name: productNames) {
            constraints.gridy++;
            JRadioButton jButton = new JRadioButton(name);
            jButton.addActionListener(e -> productSelected = jButton.getText());
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

    private File getInputFile() throws FileNotFoundException {
        final File inputFile = new File(getInput().toString());
        if (!inputFile.exists()) {
            throw new FileNotFoundException(inputFile.getPath());
        }
        return inputFile;
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
        if(this.metadata != null) {
            this.metadata = null;
        }
        if(this.tilesPanchromatic != null) {
            this.tilesPanchromatic.clear();
            this.tilesPanchromatic = null;
        }
        if(this.tilesMultispectral != null) {
            this.tilesMultispectral.clear();
            this.tilesMultispectral = null;
        }

        super.close();
    }
}
