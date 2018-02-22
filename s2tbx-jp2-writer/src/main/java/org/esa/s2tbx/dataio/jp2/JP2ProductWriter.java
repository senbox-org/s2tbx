package org.esa.s2tbx.dataio.jp2;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.s2tbx.dataio.jp2.internal.GmlEnvelope;
import org.esa.s2tbx.dataio.jp2.internal.GmlFeatureCollection;
import org.esa.s2tbx.dataio.jp2.internal.GmlRectifiedGrid;
import org.esa.s2tbx.dataio.jp2.internal.JP2Constants;
import org.esa.s2tbx.dataio.jp2.internal.JP2ImageWriter;
import org.esa.s2tbx.dataio.jp2.metadata.JP2Metadata;
import org.esa.snap.core.dataio.AbstractProductWriter;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.Stx;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.util.io.FileUtils;
import org.esa.snap.core.util.jai.SingleBandedSampleModel;
import org.geotools.referencing.CRS;

import javax.imageio.IIOImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.RescaleDescriptor;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A product writer implementation for the JPEG-2000 format.
 *
 *  @author  Razvan Dumitrascu
 *  @since 5.0.2
 */
public class JP2ProductWriter extends AbstractProductWriter {

    private File outputFile;
    private GmlFeatureCollection metadata;
    private AtomicInteger processedCount = new AtomicInteger(0);
    private Map<Band, RenderedImage> processedBands = new HashMap<>();

    /**
     * Constructs a <code>ProductWriter</code>. Since no output destination is set, the <code>setOutput</code>
     * method must be called before data can be written.
     *
     * @param writerPlugIn the plug-in which created this writer, must not be <code>null</code>
     * @throws IllegalArgumentException
     * @see #writeProductNodes
     */
    JP2ProductWriter(ProductWriterPlugIn writerPlugIn) {
        super(writerPlugIn);
    }

    @Override
    protected void writeProductNodesImpl() throws IOException {
        this.outputFile = null;
        Product sourceProduct = getSourceProduct();
        if (sourceProduct.getNumBands() > 4) {
            String message = "Source product " + sourceProduct.getName() + " has more than 4 bands. The product can not be exported due to OpenJpeg library limitations";
            throw new IOException(message);
        }
        final File file;
        if (getOutput() instanceof String) {
            file = new File((String) getOutput());
        } else {
            file = (File) getOutput();
        }
        this.outputFile = FileUtils.ensureExtension(file, JP2Constants.FILE_EXTENSIONS[0]);
        deleteOutput();
        ensureNamingConvention();
        this.metadata = new GmlFeatureCollection();
        this.metadata.setNumBands(getSourceProduct().getNumBands());
    }

    private void ensureNamingConvention() {
        if (this.outputFile != null) {
            getSourceProduct().setName(FileUtils.getFilenameWithoutExtension(this.outputFile) + JP2Constants.FILE_EXTENSIONS[0]);
        }
    }

    @Override
    public void writeBandRasterData(Band sourceBand, int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, ProductData sourceBuffer, ProgressMonitor pm) throws IOException {
        final Product sourceProduct = sourceBand.getProduct();
        final int numBands = sourceProduct.getNumBands();
        boolean shouldFinalize = numBands == processedCount.get();
        if (!shouldFinalize) {
            if (!this.processedBands.containsKey(sourceBand)) {
                this.processedBands.put(sourceBand, getScaledImage(sourceBand));
                processedCount.incrementAndGet();
                shouldFinalize = numBands == processedCount.get();
            }
            if (shouldFinalize) {
                finalizeWrite();
            }
        }
    }

    private void finalizeWrite() throws IOException {
        Product sourceProduct = getSourceProduct();
        final int numBands = sourceProduct.getNumBands();
        RenderedImage writeImage;
        if (numBands > 1) {
            final ParameterBlock parameterBlock = new ParameterBlock();
            for (int i = 0; i < numBands; i++) {
                parameterBlock.setSource(processedBands.get(sourceProduct.getBandAt(i)), i);
            }
            writeImage = JAI.create("bandmerge", parameterBlock, null);
        } else {
            writeImage = processedBands.get(sourceProduct.getBandAt(0));
        }
        IIOImage outputImage = new IIOImage(writeImage, null, null);

        final GeoCoding geoCoding = sourceProduct.getSceneGeoCoding();
        final int width = sourceProduct.getSceneRasterWidth();
        final int height = sourceProduct.getSceneRasterHeight();
        JP2ImageWriter imageWriter = new JP2ImageWriter();
        try {
            imageWriter.setOutput(outputFile);
            imageWriter.setNumberResolution(width <= 2048 && height <= 2048 ? 1 : (int) (Math.log(Math.max(width, height) / 512) / Math.log(2)) + 2);
            if (geoCoding != null) {
                if (geoCoding instanceof CrsGeoCoding) {
                    try {
                        final Integer epsgCode = CRS.lookupEpsgCode(geoCoding.getMapCRS(), true);
                        GmlRectifiedGrid rectifiedGrid = new GmlRectifiedGrid();
                        rectifiedGrid.setEpsgNumber(epsgCode);
                        final AffineTransform transform = sourceProduct.getBandAt(0).getSourceImage().getModel().getImageToModelTransform(0);
                        rectifiedGrid.setOrigin(new Point2D.Double(transform.getTranslateX(),
                                                                   transform.getTranslateY()));
                        rectifiedGrid.setGridEnvelope(new GmlEnvelope<>(0, 0, width, height, "GridEnvelope"));
                        rectifiedGrid.setOffsetVectorX(new Point2D.Double(transform.getScaleX(), 0));
                        rectifiedGrid.setOffsetVectorY(new Point2D.Double(0, transform.getScaleY()));
                        this.metadata.setRectifiedGrid(rectifiedGrid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    GmlEnvelope<Double> gmlEnvelope = new GmlEnvelope<>(geoCoding.getGeoPos(new PixelPos(0, 0), null).getLat(),
                            geoCoding.getGeoPos(new PixelPos(0, 0), null).getLon(),
                            geoCoding.getGeoPos(new PixelPos(width - 1, height - 1), null).getLat(),
                            geoCoding.getGeoPos(new PixelPos(width - 1, height - 1), null).getLon(),
                            "Envelope");
                    gmlEnvelope.setPolygonCorners(geoCoding.getGeoPos(new PixelPos(0, 0), null).getLat(),
                            geoCoding.getGeoPos(new PixelPos(0, 0), null).getLon(),
                            geoCoding.getGeoPos(new PixelPos(width, 0), null).getLat(),
                            geoCoding.getGeoPos(new PixelPos(width, 0), null).getLon(),
                            geoCoding.getGeoPos(new PixelPos(width, height), null).getLat(),
                            geoCoding.getGeoPos(new PixelPos(width, height), null).getLon(),
                            geoCoding.getGeoPos(new PixelPos(0, height), null).getLat(),
                            geoCoding.getGeoPos(new PixelPos(0, height), null).getLon());
                    gmlEnvelope.setPolygonUse(true);
                    this.metadata.setEnvelope(gmlEnvelope);
                }
                final JP2Metadata jp2Metadata = new JP2Metadata(null, this.metadata);
                imageWriter.write(jp2Metadata, outputImage, null);
            } else {
                imageWriter.write(null, outputImage, null);
            }
        } finally {
            processedCount.set(0);
            processedBands.clear();
            imageWriter.dispose();
        }
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void deleteOutput() throws IOException {
        if (this.outputFile != null && this.outputFile.isFile()) {
            this.outputFile.delete();
        }
    }

    private RenderedImage getScaledImage(Band srcBand) throws IOException {
        RenderedImage sourceImage = srcBand.getSourceImage();
        /* This would be more rigorous, but may take a lot of time
        final RenderedOp minMaxOp = ExtremaDescriptor.create(sourceImage, null, 1, 1, false, 1, null);
        double[][] minMax = (double[][]) minMaxOp.getProperty("extrema");
        double min = minMax[0][0];
        double max = minMax[1][0];*/
        final Stx stx = srcBand.getStx();
        final double min = stx.getMinimum();
        final double max = stx.getMaximum();
        final double newMin = min < 0.0 ? Short.MIN_VALUE : 0;
        final double newMax = min < 0.0 ? Short.MAX_VALUE : 65535;
        final double offset = (newMin - min * (newMax - newMin) / (max - min));
        final double scale = (newMax - newMin) / (max - min);
        final int sourceDataType = srcBand.getDataType();
        int targetDataType;
        switch (sourceDataType) {
            case ProductData.TYPE_INT8:
            case ProductData.TYPE_UINT8:
            case ProductData.TYPE_INT16:
            case ProductData.TYPE_UINT16:
                targetDataType = sourceDataType;
                break;
            default:
                targetDataType = min < 0.0 ? ProductData.TYPE_INT16 : ProductData.TYPE_UINT16;
                break;
        }
        if (sourceDataType != targetDataType) {
            ImageLayout imageLayout = new ImageLayout(sourceImage.getMinX(), sourceImage.getMinY(),
                                                      sourceImage.getWidth(), sourceImage.getHeight());
            imageLayout.setSampleModel(
                    new SingleBandedSampleModel(ImageManager.getDataBufferType(targetDataType), sourceImage.getWidth(), sourceImage.getHeight()));
            Map<RenderingHints.Key, Object> map = new HashMap<>();
            map.put(JAI.KEY_IMAGE_LAYOUT, imageLayout);
            RenderingHints hints = new RenderingHints(map);
            RenderedOp renderedOp = RescaleDescriptor.create(sourceImage, new double[] { scale }, new double[] { offset }, hints);
            sourceImage = renderedOp.getRendering();
            this.metadata.setBandInfo(getSourceProduct().getBandIndex(srcBand.getName()),
                                      srcBand.getName(),
                                      scale,
                                      offset);
        } else {
            srcBand.readRasterDataFully();
            this.metadata.setBandInfo(getSourceProduct().getBandIndex(srcBand.getName()),
                                      srcBand.getName(), 1, 0);
        }
        return sourceImage;
    }

}
