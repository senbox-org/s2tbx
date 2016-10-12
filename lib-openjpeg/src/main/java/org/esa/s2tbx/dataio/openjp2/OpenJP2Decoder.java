package org.esa.s2tbx.dataio.openjp2;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.esa.s2tbx.dataio.openjp2.library.Callbacks;
import org.esa.s2tbx.dataio.openjp2.library.Constants;
import org.esa.s2tbx.dataio.openjp2.library.Enums;
import org.esa.s2tbx.dataio.openjp2.library.OpenJp2;
import org.esa.s2tbx.dataio.openjp2.struct.DecompressParams;
import org.esa.s2tbx.dataio.openjp2.struct.DecompressionCodec;
import org.esa.s2tbx.dataio.openjp2.struct.Image;
import org.esa.s2tbx.dataio.openjp2.struct.ImageComponent;
import org.esa.snap.core.util.SystemUtils;
import sun.awt.image.SunWritableRaster;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple wrapper over OpenJP2 library that extracts a Raster
 * from a given .jp2 file
 */
public class OpenJP2Decoder implements AutoCloseable {
    private static final ExecutorService executor;

    private PointerByReference pStream;
    private DecompressParams parameters;
    private DecompressionCodec pCodec;
    private PointerByReference pImage;
    private int width;
    private int height;
    private final Path tileFile;
    private int resolution;
    private int layer;
    private int dataType;
    private int tileIndex;
    private int bandIndex;
    private Logger logger;
    private final Set<Path> pendingWrites;
    private Function<Path, Void> writeCompletedCallback;

    static {
        executor = Executors.newFixedThreadPool(Math.min(Runtime.getRuntime().availableProcessors() / 2, 4));
    }

    public OpenJP2Decoder(Path cacheDir, Path file, int bandIndex, int dataType, int resolution, int layer, int tileIndex) {
        this.logger = SystemUtils.LOG;
        this.dataType = dataType;
        this.resolution = resolution;
        this.layer = layer;
        this.tileIndex = tileIndex;
        this.bandIndex = bandIndex == -1 ? 0 : bandIndex;
        this.tileFile = cacheDir.resolve(file.getFileName().toString().replace(".", "_").toLowerCase()
                + "_" + String.valueOf(tileIndex)
                + "_" + String.valueOf(resolution)
                + "_" + String.valueOf(this.bandIndex) + ".raw");
        pStream = OpenJp2.opj_stream_create_default_file_stream(file.toAbsolutePath().toString(), Constants.OPJ_STREAM_READ);
        if (pStream == null || pStream.getValue() == null)
            throw new RuntimeException("Failed to create the stream from the file");
        this.parameters = initDecodeParams(file);
        pCodec = setupDecoder(parameters);
        pImage = new PointerByReference();
        OpenJp2.opj_read_header(pStream, pCodec, pImage);
        Image jImage = Util.dereference(Image.class, pImage.getValue());
        ImageComponent component = ((ImageComponent[]) jImage.comps.toArray(jImage.numcomps))[this.bandIndex];
        width = component.w;
        height = component.h;
        this.pendingWrites = new HashSet<>();
        this.writeCompletedCallback = value -> {
            synchronized (pendingWrites) {
                if (value != null) {
                    pendingWrites.remove(value);
                }
            }
            return null;
        };
    }

    public int[] getImageDimensions() throws IOException {
        return new int[] { width, height };
    }

    public Raster read() throws IOException {
        return decompress(null);
    }

    public Raster read(Rectangle rectangle) throws IOException {
        return decompress(rectangle);
    }

    public void close() {
        try {
            if (pImage != null && pImage.getValue() != null) {
                OpenJp2.opj_image_destroy(pImage.getValue());
            }
            if (pCodec != null) {
                OpenJp2.opj_destroy_codec(pCodec.getPointer());
            }
            if (pStream != null && pStream.getValue() != null) {
                OpenJp2.opj_stream_destroy(pStream);
            }
        } catch (Exception ex) {
            logger.warning(ex.getMessage());
        }
    }

    private ImageComponent[] decode() {
        Image jImage = Util.dereference(Image.class, pImage.getValue());

        if (parameters.nb_tile_to_decode == 0) {
            if (OpenJp2.opj_set_decode_area(pCodec, jImage, parameters.DA_x0, parameters.DA_y0, parameters.DA_x1, parameters.DA_y1) == 0) {
                throw new RuntimeException("Failed to set the decoded area");
            }
            if (OpenJp2.opj_decode(pCodec, pStream, jImage) == 0 &&
                    OpenJp2.opj_end_decompress(pCodec, pStream) != 0) {
                throw new RuntimeException("Failed to decode image");
            }
        } else {
            if (OpenJp2.opj_get_decoded_tile(pCodec, pStream, jImage, parameters.tile_index) == 0) {
                throw new RuntimeException("Failed to decode tile");
            }
        }

        jImage = Util.dereference(Image.class, pImage.getValue());
        ImageComponent[] comps = (ImageComponent[]) jImage.comps.toArray(jImage.numcomps);
        if (jImage.color_space != Enums.ColorSpace.OPJ_CLRSPC_SYCC &&
                jImage.numcomps == 3 && comps[0].dx == comps[0].dy &&
                comps[1].dx != 1) {
            jImage.color_space = Enums.ColorSpace.OPJ_CLRSPC_SYCC;
        } else if (jImage.numcomps <= 2) {
            jImage.color_space = Enums.ColorSpace.OPJ_CLRSPC_GRAY;
        }
        if (jImage.color_space == Enums.ColorSpace.OPJ_CLRSPC_SYCC) {
            // color_sycc_to_rgb(image);
        } else if (jImage.color_space == Enums.ColorSpace.OPJ_CLRSPC_CMYK
                && parameters.cod_format != 14 /* TIF_DFMT */) {
            // color_cmyk_to_rgb(image);
        } else if (jImage.color_space == Enums.ColorSpace.OPJ_CLRSPC_EYCC) {
            // color_esycc_to_rgb(image);
        }
        return comps; //toRaster(comps, roi);
    }

    private DecompressParams initDecodeParams(Path inputFile) {
        DecompressParams params = new DecompressParams();
        params.decod_format = -1;
        params.cod_format = -1;
        OpenJp2.opj_set_default_decoder_parameters(params.core);
        params.decod_format = Util.getFileFormat(inputFile);
        params.cod_format = Util.getFormat("jp2");
        params.core.cp_reduce = this.resolution;
        params.core.cp_layer = this.layer;
        params.tile_index = this.tileIndex;
        params.nb_tile_to_decode = params.tile_index >= 0 ? 1 : 0;
        return params;
    }

    private DecompressionCodec setupDecoder(DecompressParams params) {
        DecompressionCodec codec;
        switch (params.decod_format) {
            case 0: // JPEG-2000 codestream
                codec = OpenJp2.opj_create_decompress(Enums.CodecFormat.OPJ_CODEC_J2K);
                break;
            case 1: // JPEG-2000 compressed data
                codec = OpenJp2.opj_create_decompress(Enums.CodecFormat.OPJ_CODEC_JP2);
                break;
            case 2:
                codec = OpenJp2.opj_create_decompress(Enums.CodecFormat.OPJ_CODEC_JPT);
                break;
            default:
                throw new RuntimeException("File is not coded with JPEG-2000");
        }
        if (SystemUtils.LOG.getLevel().intValue() <= Level.FINE.intValue()) {
            Callbacks.MessageFunction callback = new Callbacks.MessageFunction() {
                @Override
                public void invoke(Pointer msg, Pointer client_data) {
                    System.out.println("[INFO]" + msg.getString(0));
                }

            };
            OpenJp2.opj_set_info_handler(codec, callback, null);
        }
        //OpenJp2.opj_set_warning_handler(codec, warningCallback, null);
        //OpenJp2.opj_set_error_handler(codec, errorCallback, null);

        int setupDecoder = OpenJp2.opj_setup_decoder(codec, params.core);
        if (setupDecoder == 0) {
            throw new RuntimeException("Failed to setup decoder");
        }
        return codec;
    }

    private Raster decompress(Rectangle roi) throws IOException {
        int width;
        int height;
        int[] pixels;
        if (this.pendingWrites.contains(this.tileFile)) {
            Thread.yield();
        }
        if (!Files.exists(this.tileFile)) {
            ImageComponent[] components = decode();
            ImageComponent component = components[this.bandIndex];
            width = component.w;
            height = component.h;
            pixels = component.data.getPointer().getIntArray(0, component.w * component.h);
            executor.submit(() -> {
                try {
                    this.pendingWrites.add(this.tileFile);
                    Util.write(width, height, pixels, this.dataType, this.tileFile, this.writeCompletedCallback);
                } catch (Exception ex) {
                    logger.warning(ex.getMessage());
                }
            });
            if (components.length > 1) {
                for (int i = 1; i < components.length; i++) {
                    final int index = i;
                    executor.submit(() -> {
                        try {
                            String fName = this.tileFile.getFileName().toString();
                            fName = fName.substring(0, fName.lastIndexOf("_")) + "_" + String.valueOf(index + 1) + ".raw";
                            Path otherBandFile = Paths.get(fName);
                            this.pendingWrites.add(otherBandFile);
                            Util.write(width, height,
                                       components[index].data.getPointer().getIntArray(0, components[index].w * components[index].h),
                                       this.dataType, otherBandFile, this.writeCompletedCallback);
                        } catch (Exception ex) {
                            logger.warning(ex.getMessage());
                        }
                    });
                }
            }
        } else {
            int[][] readBytes = Util.read(this.tileFile, this.dataType);
            pixels = readBytes[1];
            width = readBytes[0][0];
            height = readBytes[0][1];
        }
        int bands = 1;
        int[] bandOffsets = new int[bands];
        for (int i = 0; i < bands; i++)
            bandOffsets[i] = i;
        SampleModel sampleModel;
        DataBuffer buffer;
        int[] values;
        if (roi != null) {
            values = new int[roi.width * roi.height];
            sampleModel = new PixelInterleavedSampleModel(this.dataType, roi.width, roi.height, bands, roi.width * bands, bandOffsets);
            int srcPos;
            int dstPos;
            int maxVal = Math.min(roi.y + roi.height, height);
            for (int col = roi.y; col < maxVal; col++) {
                try {
                    srcPos = roi.x + col * width;
                    dstPos = (col - roi.y) * roi.width;
                    if (srcPos < pixels.length && dstPos < values.length)
                        System.arraycopy(pixels, srcPos, values, dstPos, Math.min(roi.width, pixels.length - srcPos));
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        } else {
            sampleModel = new PixelInterleavedSampleModel(this.dataType, width, height, bands, width * bands, bandOffsets);
            values = pixels;
        }
        switch (this.dataType) {
            case DataBuffer.TYPE_BYTE:
                byte[] bytes = new byte[values.length];
                for (int i = 0; i < values.length; i++) {
                    bytes[i] = (byte) (values[i] >> 3);
                }
                buffer = new DataBufferByte(bytes, width * height * bands);
                break;
            case DataBuffer.TYPE_SHORT:
                short[] shorts = new short[values.length];
                for (int i = 0; i < values.length; i++) {
                    shorts[i] = (short) values[i];
                }
                buffer = new DataBufferShort(shorts, width * height * bands);
                break;
            case DataBuffer.TYPE_USHORT:
                short[] ushorts = new short[values.length];
                for (int i = 0; i < values.length; i++) {
                    ushorts[i] = (short) values[i];
                }
                buffer = new DataBufferUShort(ushorts, width * height * bands);
                break;
            case DataBuffer.TYPE_INT:
                buffer = new DataBufferInt(values, width * height * bands);
                break;
            default:
                throw new UnsupportedOperationException("Source buffer type not supported");
        }
        WritableRaster raster = null;
        try {
            raster = new SunWritableRaster(sampleModel, buffer, new Point(0, 0));
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        return raster;
    }
}
