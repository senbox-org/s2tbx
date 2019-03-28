/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.spot;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.FileImageInputStreamSpi;
import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.readers.BaseProductReaderPlugIn;
import org.esa.s2tbx.dataio.spot.dimap.SpotConstants;
import org.esa.s2tbx.dataio.spot.dimap.SpotDimapMetadata;
import org.esa.s2tbx.dataio.spot.dimap.SpotSceneMetadata;
import org.esa.s2tbx.dataio.spot.dimap.VolumeComponent;
import org.esa.s2tbx.dataio.spot.dimap.VolumeMetadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.util.TreeNode;
import org.geotools.metadata.InvalidMetadataException;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * This product reader is intended for reading SPOT-1 to SPOT-5 scene files
 * from compressed archive files or from file system.
 *
 * @author Cosmin Cara
 */
public class SpotDimapProductReader extends AbstractProductReader {
    private static final Logger logger = Logger.getLogger(SpotDimapProductReader.class.getName());

    private ImageInputStreamSpi channelImageInputStreamSpi;
    private VirtualDirEx productDirectory;
    private SpotSceneMetadata metadata;
    private SpotProductReader internalReader;

    static {
        XmlMetadataParserFactory.registerParser(SpotDimapMetadata.class, new XmlMetadataParser<SpotDimapMetadata>(SpotDimapMetadata.class));
    }

    protected SpotDimapProductReader(SpotDimapProductReaderPlugin readerPlugIn) {
        super(readerPlugIn);

        registerSpi();
    }

    @Override
    protected Product readProductNodesImpl() throws IOException {
        productDirectory = ((BaseProductReaderPlugIn)getReaderPlugIn()).getInput(getInput());
        metadata = SpotSceneMetadata.create(productDirectory, this.logger);
        VolumeMetadata volumeMetadata = metadata.getVolumeMetadata();
        SpotDimapProductReaderPlugin readerPlugIn = (SpotDimapProductReaderPlugin)getReaderPlugIn();
        Path colorPaletteFilePath = readerPlugIn.getColorPaletteFilePath();
        if (volumeMetadata != null) {
            if (SpotConstants.PROFILE_MULTI_VOLUME.equals(volumeMetadata.getProfileName())) {
                internalReader = new SpotDimapVolumeProductReader(readerPlugIn, colorPaletteFilePath);
                logger.info("Multi-volume product detected.");
            } else {
                if (!SpotConstants.PROFILE_VOLUME.equals(volumeMetadata.getProfileName())) {
                    logger.warning("Metadata profile unknown, will use SPOTScene reader.");
                } else {
                    logger.info("Single volume product detected.");
                }
                internalReader = new SpotDimapSimpleProductReader(readerPlugIn, colorPaletteFilePath);
            }
        } else {
            logger.warning("No volume metadata found. Will assume single volume product.");
            internalReader = new SpotDimapSimpleProductReader(readerPlugIn, colorPaletteFilePath);
        }
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
        if (productDirectory.isCompressed()) {
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
            for (SpotDimapMetadata componentMetadata : metadata.getComponentsMetadata()) {
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
