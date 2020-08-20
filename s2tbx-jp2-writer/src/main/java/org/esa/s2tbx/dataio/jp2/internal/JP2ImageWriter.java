package org.esa.s2tbx.dataio.jp2.internal;

import org.esa.s2tbx.dataio.jp2.metadata.JP2Metadata;
import org.esa.snap.lib.openjpeg.dataio.OpenJP2Encoder;
import org.esa.snap.lib.openjpeg.jp2.Box;
import org.esa.snap.lib.openjpeg.jp2.BoxReader;

import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Class that generates the contents of the JP2 file and writes the metadata
 *
 *  @author  Razvan Dumitrascu
 *  @since 5.0.2
 */
public class JP2ImageWriter extends ImageWriter {

    private static final int DEFAULT_NUMBER_OF_RESOLUTIONS = 1;
    private static final int XML_BOX_HEADER_TYPE = 0x786D6C20;
    private static final String CONTIGUOUS_CODESTREAM = "jp2c";
    private static final Logger logger = Logger.getLogger(JP2ImageWriter.class.getName());

    private File fileOutput;
    private IIOImage sourceImage;
    private RenderedImage renderedImage;
    private int numbResolution = DEFAULT_NUMBER_OF_RESOLUTIONS;
    private BoxReader boxReader;
    private int headerSize;
    private JP2Metadata createdStreamMetadata;

    /**
     * Constructs a new instance of the JP2ImageWriter
     */
    public JP2ImageWriter() {
        super(null);
    }

    @Override
    public void setOutput(Object output) {
        super.setOutput(output);
        if (output != null) {
            if (!(output instanceof File)) {
                throw new IllegalArgumentException
                        ("output not a File!");
            }
            this.fileOutput = (File) output;
        } else {
            this.fileOutput = null;
        }
    }

    @Override
    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return new JP2Metadata(param, null);
    }

    @Override
    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
        return null;
    }

    @Override
    public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
        if (inData instanceof JP2Metadata) {
            return inData;
        }
        return null;
    }

    @Override
    public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();
        this.renderedImage = null;
    }

    @Override
    public ImageWriteParam getDefaultWriteParam() {
        return new ImageWriteParam(getLocale());
    }

    @Override
    public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
        //verify that an image has been received
        if (image == null) {
            logger.info("No image has been received");
            throw new IllegalArgumentException("input image is null!");
        }
        //verify if the image output stream has been set
        if (this.fileOutput == null) {
            logger.info("Output has not been set");
            throw new IllegalStateException("Output has not been set!");
        }
        if (this.fileOutput.exists()) {
            try (RandomAccessFile file = new RandomAccessFile(this.fileOutput, "rws")) {
                file.setLength(0);
            } catch (IOException e) {
                logger.warning(e.getMessage());
            }
        }
        if (streamMetadata != null) {
            this.createdStreamMetadata = (JP2Metadata)convertStreamMetadata(streamMetadata, null);
        }
        this.sourceImage = image;
        this.renderedImage = this.sourceImage.getRenderedImage();
        try (OpenJP2Encoder jp2Encoder = new OpenJP2Encoder(this.renderedImage)) {
            Path outputStreamPath = FileSystems.getDefault().getPath(this.fileOutput.getPath());
            jp2Encoder.write(outputStreamPath, getNumberResolutions());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(e.getMessage());
        }
        //if the streamMetadata is not null a gml geoCoding XML will be introduced before the continuous code stream
        if (this.createdStreamMetadata != null) {
            this.headerSize = computeHeaderSize();
            if (this.fileOutput.exists()) {
                try (RandomAccessFile file = new RandomAccessFile(this.fileOutput, "rws")) {
                    int fileLength = (int) file.length();
                    byte[] headerStream = new byte[this.headerSize];
                    file.read(headerStream, 0, this.headerSize);
                    file.seek(0);
                    byte[] ccStream = new byte[(int) file.length() - this.headerSize];
                    file.seek(this.headerSize);
                    file.read(ccStream, 0, fileLength - this.headerSize);
                    file.setLength(0);
                    try (FileOutputStream fop = new FileOutputStream(this.fileOutput, true)) {
                        fop.write(this.createdStreamMetadata.toString().getBytes());
                        fop.write(0);
                    } catch (IOException e) {
                        logger.warning(e.getMessage());
                    }
                    byte[] xmlStream = new byte[(int) file.length()];
                    file.read(xmlStream, 0, (int) file.length());
                    file.setLength(0);
                    file.write(headerStream, 0, this.headerSize);
                    file.writeInt(8 + xmlStream.length);
                    file.writeInt(XML_BOX_HEADER_TYPE);
                    file.write(xmlStream);
                    file.write(ccStream);
                } catch (IOException e) {
                    logger.warning(e.getMessage());
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }
            }
        }
    }

    /**
     * sets the number of resolutions for the image to be encoded
     * @param numResolutions the number of resolutions
     */
    public void setNumberResolution(final int numResolutions) {
        this.numbResolution = numResolutions;
    }

    /**
     *
     * @return the number of resolutions that the image has to be encoded with
     */
    public int getNumberResolutions() {
        return this.numbResolution;
    }

    private static class BoxListener implements BoxReader.Listener {
        @Override
        public void knownBoxSeen(Box box) {}
        @Override
        public void unknownBoxSeen(Box box) {}
    }

    /**
     * Searches for the location of the code continuous stream in the encoded image
     *
     * @return returns the size of the header from the original encoded image
     */
    private int computeHeaderSize(){
        int headerSize = 0;
        try (FileImageInputStream file = new FileImageInputStream(this.fileOutput)) {
            this.boxReader = new BoxReader(file, file.length(), new BoxListener());
            this.boxReader.getFileLength();
            Box box;
            do {
                box = this.boxReader.readBox();
                if (box.getSymbol().equals(CONTIGUOUS_CODESTREAM)) {
                    headerSize = (int) box.getPosition();
                }
            }
            while (!box.getSymbol().equals(CONTIGUOUS_CODESTREAM));
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
        return headerSize;
    }

}
