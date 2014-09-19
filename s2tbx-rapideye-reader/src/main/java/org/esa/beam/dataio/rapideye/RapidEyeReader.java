package org.esa.beam.dataio.rapideye;

import org.esa.beam.dataio.FileImageInputStreamSpi;
import org.esa.beam.dataio.ZipVirtualDir;
import org.esa.beam.dataio.geotiff.GeoTiffProductReader;
import org.esa.beam.dataio.metadata.XmlMetadataParser;
import org.esa.beam.dataio.metadata.XmlMetadataParserFactory;
import org.esa.beam.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.jai.ImageManager;
import org.esa.beam.util.StringUtils;
import org.esa.beam.util.TreeNode;
import org.esa.beam.util.io.FileUtils;
import org.esa.beam.util.logging.BeamLogManager;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.media.jai.Interpolation;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Base class for RapidEye readers.
 *
 * @author  Cosmin Cara
 */
public abstract class RapidEyeReader extends AbstractProductReader {
    public static final int WIDTH_THRESHOLD = 8192;
    protected RapidEyeMetadata metadata;
    protected Product product;
    protected final Logger logger;
    protected ZipVirtualDir productDirectory;
    private ImageInputStreamSpi channelImageInputStreamSpi;

    static {
        XmlMetadataParserFactory.registerParser(RapidEyeMetadata.class, new XmlMetadataParser<RapidEyeMetadata>(RapidEyeMetadata.class));
    }

    public RapidEyeReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        logger = BeamLogManager.getSystemLogger();
        registerSpi();
    }

    @Override
    public void close() throws IOException {
        if (productDirectory != null) {
            productDirectory.close();
        }
        if (channelImageInputStreamSpi != null) {
            IIORegistry.getDefaultInstance().deregisterServiceProvider(channelImageInputStreamSpi);
        }
        super.close();
    }

    protected void readMasks(ZipVirtualDir folder) {
        File file;
        if (metadata != null) {
            try {
                String maskFileName = metadata.getMaskFileName();
                if (maskFileName != null) {
                    file = folder.getFile(maskFileName);
                    if (file != null && file.exists()) {
                        GeoTiffProductReader reader = new GeoTiffProductReader(getReaderPlugIn());
                        Product udmProduct = reader.readProductNodes(file, null);
                        Band srcBand = udmProduct.getBandAt(0);
                        float scaleX = metadata.getRasterWidth() / udmProduct.getSceneRasterWidth();
                        float scaleY = metadata.getRasterHeight() / udmProduct.getSceneRasterHeight();
                        RenderedOp renderedOp = ScaleDescriptor.create(srcBand.getSourceImage(), scaleX, scaleY, 0.0f, 0.0f, Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);
                        Band targetBand = product.addBand("unusable_data", srcBand.getDataType());
                        targetBand.setSourceImage(renderedOp);
                        FlagCoding cloudsFlagCoding = createFlagCoding(product);
                        targetBand.setSampleCoding(cloudsFlagCoding);

                        List<Mask> cloudsMasks = createMasksFromFlagCodding(product, cloudsFlagCoding);
                        for (Mask mask : cloudsMasks) {
                            product.getMaskGroup().add(mask);
                        }
                    }
                }
            } catch (IOException e) {
                logger.warning(e.getMessage());
            }
        }
    }

    protected FlagCoding createFlagCoding(Product product) {
        FlagCoding flagCoding = new FlagCoding("unusable_data");
        flagCoding.addFlag("black_fill", 1, "area was not imaged by spacecraft");
        flagCoding.addFlag("clouds", 2, "cloud covered");
        flagCoding.addFlag("missing_blue_data", 4, "missing/suspect data in blue band");
        flagCoding.addFlag("missing_green_data", 8, "missing/suspect data in green band");
        flagCoding.addFlag("missing_red_data", 16, "missing/suspect data in red band");
        flagCoding.addFlag("missing_red_edge_data", 32, "missing/suspect data in red edge band");
        flagCoding.addFlag("missing_nir_data", 64, "missing/suspect data in nir band");
        product.getFlagCodingGroup().add(flagCoding);
        return flagCoding;
    }

    protected List<Mask> createMasksFromFlagCodding(Product product, FlagCoding flagCoding) {
        String flagCodingName = flagCoding.getName();
        ArrayList<Mask> masks = new ArrayList<Mask>();
        final int width = product.getSceneRasterWidth();
        final int height = product.getSceneRasterHeight();

        for (String flagName : flagCoding.getFlagNames()) {
            MetadataAttribute flag = flagCoding.getFlag(flagName);
            masks.add(Mask.BandMathsType.create(flagName,
                    flag.getDescription(),
                    width, height,
                    flagCodingName + "." + flagName,
                    ColorIterator.next(),
                    0.5));
        }
        return masks;
    }

    protected Dimension getPreferredTileSize() {
        Dimension tileSize = null;
        if (product != null) {
            tileSize = product.getPreferredTileSize();
            if (tileSize == null) {
                Dimension suggestedTileSize = ImageManager.getPreferredTileSize(product);
                tileSize = new Dimension((int)suggestedTileSize.getWidth(), (int)suggestedTileSize.getHeight());
            }
        }
        return tileSize;
    }

    private void registerSpi() {
        // We will register a new Spi for creating NIO-based ImageInputStreams.
        final IIORegistry defaultInstance = IIORegistry.getDefaultInstance();
        Iterator<ImageInputStreamSpi> serviceProviders = defaultInstance.getServiceProviders(ImageInputStreamSpi.class, true);
        ImageInputStreamSpi toUnorder = null;
        if (defaultInstance.getServiceProviderByClass(FileImageInputStreamSpi.class) == null) {
            // register only if not already registered
            while (serviceProviders.hasNext()) {
                ImageInputStreamSpi current = serviceProviders.next();
                if (current.getInputClass() == File.class) {
                    toUnorder = current;
                    break;
                }
            }
            channelImageInputStreamSpi = new FileImageInputStreamSpi();
            defaultInstance.registerServiceProvider(channelImageInputStreamSpi);
            if (toUnorder != null) {
                // Make the custom Spi to be the first one to be used.
                defaultInstance.setOrdering(ImageInputStreamSpi.class, channelImageInputStreamSpi, toUnorder);
            }
        }
    }

    protected void addProductComponentIfNotPresent(String componentId, File componentFile, TreeNode<File> currentComponents) {
        TreeNode<File> resultComponent = null;
        for (TreeNode node : currentComponents.getChildren()) {
            if (node.getId().toLowerCase().equals(componentId.toLowerCase())) {
                //noinspection unchecked
                resultComponent = node;
                break;
            }
        }
        if (resultComponent == null) {
            resultComponent = new TreeNode<File>(componentId, componentFile);
            currentComponents.addChild(resultComponent);
        }
    }

    protected static class ColorIterator {

        static final ArrayList<Color> colors;
        static Iterator<Color> colorIterator;

        static {
            colors = new ArrayList<Color>();
            colors.add(Color.red);
            colors.add(Color.red.darker());
            colors.add(Color.blue);
            colors.add(Color.blue.darker());
            colors.add(Color.green);
            colors.add(Color.green.darker());
            colors.add(Color.yellow);
            colors.add(Color.yellow.darker());
            colors.add(Color.magenta);
            colors.add(Color.magenta.darker());
            colors.add(Color.pink);
            colors.add(Color.pink.darker());
            colors.add(Color.cyan);
            colors.add(Color.cyan.darker());
            colors.add(Color.orange);
            colors.add(Color.orange.darker());
            colors.add(Color.blue.darker().darker());
            colors.add(Color.green.darker().darker());
            colors.add(Color.yellow.darker().darker());
            colors.add(Color.magenta.darker().darker());
            colors.add(Color.pink.darker().darker());
            colorIterator = colors.iterator();
        }

        static Color next() {
            if (!colorIterator.hasNext()) {
                colorIterator = colors.iterator();
            }
            return colorIterator.next();
        }
    }

    static File getFileInput(Object input) {
        if (input instanceof String) {
            return new File((String) input);
        } else if (input instanceof File) {
            return (File) input;
        }
        return null;
    }

    static ZipVirtualDir getInput(Object input) throws IOException {
        File inputFile = getFileInput(input);

        if (inputFile.isFile() && !ZipVirtualDir.isCompressedFile(inputFile)) {
            final File absoluteFile = inputFile.getAbsoluteFile();
            inputFile = absoluteFile.getParentFile();
            if (inputFile == null) {
                throw new IOException(String.format("Unable to retrieve parent to file %s.", absoluteFile.getAbsolutePath()));
            }
        }

        return new ZipVirtualDir(inputFile);
    }
}
