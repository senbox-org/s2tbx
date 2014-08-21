package org.esa.beam.dataio.spot;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.dataio.FileImageInputStreamSpi;
import org.esa.beam.dataio.metadata.XmlMetadataParserFactory;
import org.esa.beam.dataio.spot.dimap.*;
import org.esa.beam.dataio.spot.internal.SpotVirtualDir;
import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.TreeNode;
import org.esa.beam.util.logging.BeamLogManager;
import org.geotools.metadata.InvalidMetadataException;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * This rootProduct reader is intended for reading SPOT-1 to SPOT-5 scene files
 * from compressed archive files or from file system.
 * @author Cosmin Cara
 */
public class SpotDimapProductReader extends AbstractProductReader {

    private ImageInputStreamSpi channelImageInputStreamSpi;
    private SpotVirtualDir productDirectory;
    private SpotSceneMetadata metadata;
    private SpotProductReader internalReader;
    private final Logger logger;

    static {
        XmlMetadataParserFactory.registerParser(SpotDimapMetadata.class, new SpotDimapMetadata.SpotDimapMetadataParser(SpotDimapMetadata.class));
    }

    protected SpotDimapProductReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        logger = BeamLogManager.getSystemLogger();
        registerSpi();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        productDirectory = SpotDimapProductReaderPlugin.getInput(getInput());
        metadata = SpotSceneMetadata.create(productDirectory, this.logger);
        VolumeMetadata volumeMetadata = metadata.getVolumeMetadata();
        if (volumeMetadata != null) {
            if (SpotConstants.PROFILE_MULTI_VOLUME.equals(volumeMetadata.getProfileName())) {
                internalReader = new SpotDimapVolumeProductReader(getReaderPlugIn());
                logger.info("Multi-volume product detected.");
            } else {
                if (!SpotConstants.PROFILE_VOLUME.equals(volumeMetadata.getProfileName())) {
                    logger.warning("Metadata profile unknown, will use SPOTScene reader.");
                } else {
                    logger.info("Single volume product detected.");
                }
                internalReader = new SpotDimapSimpleProductReader(getReaderPlugIn());
            }
        } else {
            logger.warning("No volume metadata found. Will assume single volume product.");
            internalReader = new SpotDimapSimpleProductReader(getReaderPlugIn());
        }
        internalReader.setLogger(logger);
        internalReader.setMetadata(metadata);
        internalReader.setProductDirectory(productDirectory);
        return internalReader.readProductNodes(getInput(), null);
    }

    @Override
    protected void readBandRasterDataImpl(int sourceOffsetX, int sourceOffsetY,
                                          int sourceWidth, int sourceHeight,
                                          int sourceStepX, int sourceStepY,
                                          Band destBand,
                                          int destOffsetX, int destOffsetY,
                                          int destWidth, int destHeight,
                                          ProductData destBuffer, ProgressMonitor pm) throws IOException {
        internalReader.readBandRasterData(destBand, destOffsetX, destOffsetY, destWidth, destHeight, destBuffer, pm);
    }

    @Override
    public void close() throws IOException {
        if (internalReader != null) {
            internalReader.close();
        }
        super.close();
        if (channelImageInputStreamSpi != null) {
            IIORegistry.getDefaultInstance().deregisterServiceProvider(channelImageInputStreamSpi);
        }
    }

    @Override
    public TreeNode<File> getProductComponents() {
        if (productDirectory.isThisZipFile()) {
            return super.getProductComponents();
        } else {
            TreeNode<File> result = super.getProductComponents();
            //if the volume metadata file is present, but it is not in the list, add it!
            try {
                File volumeMetadataPhysicalFile = productDirectory.getFile(SpotConstants.DIMAP_VOLUME_FILE);
                if (metadata.getVolumeMetadata() != null) {
                    addProductComponentIfNotPresent(SpotConstants.DIMAP_VOLUME_FILE, volumeMetadataPhysicalFile, result);
                    //add components of the volumes (like SCENE01 folders)
                    for (VolumeComponent component : metadata.getVolumeMetadata().getDimapComponents()) {
                        try {
                            //add thumb file of the component
                            addProductComponentIfNotPresent(component.getThumbnailPath(), productDirectory.getFile(component.getThumbnailPath()), result);
                        } catch (IOException ex) {
                            logger.warning(ex.getMessage());
                        }
                        try {
                            //add path file of the component
                            if (component.getType().equals(SpotConstants.DIMAP)) {
                                addProductComponentIfNotPresent(component.getPath(), productDirectory.getFile(component.getPath()), result);
                            }
                        } catch (IOException ex) {
                            logger.warning(ex.getMessage());
                        }
                    }
                }
            } catch (IOException ex) {
                logger.warning(ex.getMessage());
            }
            //add components of the metadatas
            for (SpotDimapMetadata componentMetadata: metadata.getComponentsMetadata()) {
                try {
                    String[] fileNames = componentMetadata.getRasterFileNames();
                    if (fileNames == null || fileNames.length == 0)
                        throw new InvalidMetadataException("No raster file found in metadata");
                    String fileId = componentMetadata.getPath().toLowerCase().replace(componentMetadata.getFileName().toLowerCase(),
                                                                                      fileNames[0].toLowerCase());
                    addProductComponentIfNotPresent(fileId, productDirectory.getFile(fileId), result);
                } catch (IOException e) {
                    logger.warning(e.getMessage());
                }
            }
            return result;
        }
    }

    private void addProductComponentIfNotPresent(String componentId, File componentFile, TreeNode<File> currentComponents) {
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
}
