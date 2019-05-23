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

package org.esa.s2tbx.dataio.rapideye;

import org.esa.s2tbx.dataio.VirtualDirEx;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParser;
import org.esa.s2tbx.dataio.metadata.XmlMetadataParserFactory;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeConstants;
import org.esa.s2tbx.dataio.rapideye.metadata.RapidEyeMetadata;
import org.esa.snap.core.dataio.AbstractProductReader;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.FlagCoding;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.MetadataAttribute;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.util.TreeNode;
import org.esa.snap.dataio.FileImageInputStreamSpi;
import org.esa.snap.dataio.geotiff.GeoTiffProductReader;

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
 * @author Cosmin Cara
 */
public abstract class RapidEyeReader extends AbstractProductReader {
    static {
        XmlMetadataParserFactory.registerParser(RapidEyeMetadata.class, new XmlMetadataParser<>(RapidEyeMetadata.class));
    }

    final Logger logger;
    protected RapidEyeMetadata metadata;
    protected Product product;
    VirtualDirEx productDirectory;
    private ImageInputStreamSpi channelImageInputStreamSpi;

    RapidEyeReader(ProductReaderPlugIn readerPlugIn) {
        super(readerPlugIn);
        this.logger = Logger.getLogger(RapidEyeReader.class.getName());
        registerSpi();
    }

    @Override
    public void close() throws IOException {
        if (this.productDirectory != null) {
            this.productDirectory.close();
        }
        if (this.channelImageInputStreamSpi != null) {
            IIORegistry.getDefaultInstance().deregisterServiceProvider(this.channelImageInputStreamSpi);
        }
        super.close();
    }

    void readMasks() {
        File file;
        if (this.metadata != null) {
            try {
                String maskFileName = this.metadata.getMaskFileName();
                if (maskFileName != null) {
                    file = this.productDirectory.getFile(maskFileName);
                    if (file != null && file.exists()) {
                        GeoTiffProductReader reader = new GeoTiffProductReader(getReaderPlugIn());
                        Product udmProduct = reader.readProductNodes(file, null);
                        Band srcBand = udmProduct.getBandAt(0);
                        float scaleX = (float) this.metadata.getRasterWidth() / (float) udmProduct.getSceneRasterWidth();
                        float scaleY = (float) this.metadata.getRasterHeight() / (float) udmProduct.getSceneRasterHeight();
                        RenderedOp renderedOp = ScaleDescriptor.create(srcBand.getSourceImage(), scaleX, scaleY, 0.0f, 0.0f, Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);
                        Band targetBand = this.product.addBand("unusable_data", srcBand.getDataType());
                        targetBand.setSourceImage(renderedOp);
                        FlagCoding cloudsFlagCoding = createFlagCoding(this.product);
                        targetBand.setSampleCoding(cloudsFlagCoding);

                        List<Mask> cloudsMasks = createMasksFromFlagCodding(this.product, cloudsFlagCoding);
                        for (Mask mask : cloudsMasks) {
                            this.product.getMaskGroup().add(mask);
                        }
                    }
                }
            } catch (IOException e) {
                this.logger.warning(e.getMessage());
            }
        }
    }

    private FlagCoding createFlagCoding(Product product) {
        FlagCoding flagCoding = new FlagCoding("unusable_data");
        flagCoding.addFlag(RapidEyeConstants.FLAG_BLACK_FILL, 1, "area was not imaged by spacecraft");
        flagCoding.addFlag(RapidEyeConstants.FLAG_CLOUDS, 2, "cloud covered");
        flagCoding.addFlag(RapidEyeConstants.FLAG_MISSING_BLUE_DATA, 4, "missing/suspect data in blue band");
        flagCoding.addFlag(RapidEyeConstants.FLAG_MISSING_GREEN_DATA, 8, "missing/suspect data in green band");
        flagCoding.addFlag(RapidEyeConstants.FLAG_MISSING_RED_DATA, 16, "missing/suspect data in red band");
        flagCoding.addFlag(RapidEyeConstants.FLAG_MISSING_RED_EDGE_DATA, 32, "missing/suspect data in red edge band");
        flagCoding.addFlag(RapidEyeConstants.FLAG_MISSING_NIR_DATA, 64, "missing/suspect data in nir band");
        product.getFlagCodingGroup().add(flagCoding);
        return flagCoding;
    }

    private List<Mask> createMasksFromFlagCodding(Product product, FlagCoding flagCoding) {
        String flagCodingName = flagCoding.getName();
        ArrayList<Mask> masks = new ArrayList<>();
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

    Dimension getPreferredTileSize() {
        Dimension tileSize = null;
        if (this.product != null) {
            tileSize = this.product.getPreferredTileSize();
            if (tileSize == null) {
                Dimension suggestedTileSize = ImageManager.getPreferredTileSize(this.product);
                tileSize = new Dimension((int) suggestedTileSize.getWidth(), (int) suggestedTileSize.getHeight());
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
            this.channelImageInputStreamSpi = new FileImageInputStreamSpi();
            defaultInstance.registerServiceProvider(this.channelImageInputStreamSpi);
            if (toUnorder != null) {
                // Make the custom Spi to be the first one to be used.
                defaultInstance.setOrdering(ImageInputStreamSpi.class, this.channelImageInputStreamSpi, toUnorder);
            }
        }
    }

    void addProductComponentIfNotPresent(String componentId, File componentFile, TreeNode<File> currentComponents) {
        TreeNode<File> resultComponent = null;
        for (TreeNode node : currentComponents.getChildren()) {
            if (node.getId().equalsIgnoreCase(componentId.toLowerCase())) {
                //noinspection unchecked
                resultComponent = node;
                break;
            }
        }
        if (resultComponent == null) {
            resultComponent = new TreeNode<>(componentId, componentFile);
            currentComponents.addChild(resultComponent);
        }
    }

    protected static class ColorIterator {
        static final ArrayList<Color> colors;
        static Iterator<Color> colorIterator;

        static {
            colors = new ArrayList<>();
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

        private ColorIterator() {
        }

        static Color next() {
            if (!colorIterator.hasNext()) {
                colorIterator = colors.iterator();
            }
            return colorIterator.next();
        }
    }
}
