package org.esa.s2tbx.dataio.openjp2;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.esa.snap.core.util.SystemUtils;

import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for various operations
 */
public class Util {

    private static final Map<String, Integer> extensions;

    static {
        extensions = new HashMap<String, Integer>() {{
            put("pgx", 11);
            put("pnm", 10);
            put("pgm", 10);
            put("ppm", 10);
            put("bmp", 12);
            put("tif", 14);
            put("raw", 15);
            put("rawl", 18);
            put("tga", 16);
            put("png", 17);
            put("j2k", 0);
            put("jp2", 1);
            put("jpt", 2);
            put("j2c", 0);
            put("jpc", 0);
        }};
    }

    /*public static Raster read(Path path, int dataType) throws IOException {
        int size = (int) Files.size(path);
        ByteBuffer buf = null;
        try (RandomAccessFile in = new RandomAccessFile(path.toFile(), "r")) {
            try (FileChannel file = in.getChannel()) {
                buf = ByteBuffer.allocate(size);
                file.read(buf);
            }
        }
        Raster raster = null;

        buf.position(0);
        int width = buf.getInt();
        int height = buf.getInt();
        int numBands = buf.getInt();
        int length = width * height * numBands;
        DataBuffer buffer = null;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                buffer = new DataBufferByte(buf.array(), width * height * numBands);
                break;
            case DataBuffer.TYPE_SHORT:
                short[] shorts = new short[length];
                buf.asShortBuffer().get(shorts, 0, shorts.length);
                buffer = new DataBufferShort(shorts, width * height * numBands);
                break;
            case DataBuffer.TYPE_USHORT:
                short[] ushorts = new short[length];
                buf.asShortBuffer().get(ushorts, 0, ushorts.length);
                buffer = new DataBufferUShort(ushorts, width * height * numBands);
                break;
            case DataBuffer.TYPE_INT:
                int[] ints = new int[length];
                buf.asIntBuffer().get(ints, 0, ints.length);
                buffer = new DataBufferInt(ints, width * height * numBands);
                break;
            case DataBuffer.TYPE_FLOAT:
                float[] floats = new float[length];
                buf.asFloatBuffer().get(floats, 0, floats.length);
                buffer = new DataBufferFloat(floats, width * height * numBands);
                break;
            case DataBuffer.TYPE_DOUBLE:
                double[] doubles = new double[length];
                buffer = new DataBufferDouble(doubles, width * height * numBands);
                break;
        }

        try {
            SampleModel sm = new PixelInterleavedSampleModel(dataType, width, height, numBands, width * numBands, new int[] { 0 });
            raster = WritableRaster.createRaster(sm, buffer, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return raster;
    }*/

    public static int[] read(Path path, int dataType) throws IOException {
        int size = (int) Files.size(path);
        int pixSize;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                pixSize = 1;
                break;
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_SHORT:
                pixSize = 2;
                break;
            case DataBuffer.TYPE_INT:
            default:
                pixSize = 4;
                break;
        }
        ByteBuffer buf;
        int[] out = new int[2 + (size - 8) / pixSize];
        try (RandomAccessFile in = new RandomAccessFile(path.toFile(), "r")) {
            try (FileChannel file = in.getChannel()) {
                buf = file.map(FileChannel.MapMode.READ_ONLY, 0, size);
                out[0] = buf.getInt();
                out[1] = buf.getInt();
                if (pixSize == 4) {
                    for (int i = 2; i < out.length; i++) {
                        out[i] = buf.getInt();
                    }
                } else if (pixSize == 2) {
                    for (int i = 2; i < out.length; i++) {
                        out[i] = buf.getShort();
                    }
                } else {
                    for (int i = 2; i < out.length; i++) {
                        out[i] = buf.get();
                    }
                }
            }
        }
        return out;
    }

    public static Path write(int width, int height, int[] values, int dataType, Path toFile, Function<Path, Void> completionCallBack) throws IOException {
        int pixSize;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                pixSize = 1;
                break;
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_SHORT:
                pixSize = 2;
                break;
            case DataBuffer.TYPE_INT:
            default:
                pixSize = 4;
                break;
        }
        try (RandomAccessFile raf = new RandomAccessFile(toFile.toFile(), "rw")) {
            try (FileChannel file = raf.getChannel()) {
                ByteBuffer buffer = file.map(FileChannel.MapMode.READ_WRITE, 0, pixSize * values.length + 8);
                buffer.putInt(width);
                buffer.putInt(height);
                if (pixSize == 4) {
                    for (int v : values) {
                        buffer.putInt(v);
                    }
                } else if (pixSize == 2) {
                    for (int v : values) {
                        buffer.putShort((short) v);
                    }
                } else {
                    for (int v : values) {
                        buffer.put((byte) v);
                    }
                }
                file.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (completionCallBack != null) {
                completionCallBack.apply(toFile);
            }
        }
        return toFile;
    }

    public static Path write(Raster raster, Path toFile) throws IOException {
        try (FileOutputStream out = new FileOutputStream(toFile.toFile())) {
            try (FileChannel file = out.getChannel()) {
                System.out.println(file.write(extractBuffer(raster)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return toFile;
    }

    public static int clampInt(final int value, final int prec, final int sgnd) {
        if (sgnd > 0) {
            if (prec <= 8)
                return clamp(value, Byte.MIN_VALUE, Byte.MAX_VALUE);
            else if (prec <= 16)
                return clamp(value, Short.MIN_VALUE, Short.MAX_VALUE);
            else
                return clamp(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
        } else {
            if (prec <= 8)
                return clamp(value, 0, 255);
            else if (prec <= 16)
                return clamp(value, 0, 65535);
            else
                return value;
        }
    }

    static <T extends Structure> T dereference(Class<T> tClass, Pointer pointer) {
        T ref = null;
        if (tClass != null && pointer != null) {
            try {
                Constructor<T> ctor = tClass.getDeclaredConstructor(Pointer.class);
                ref = ctor.newInstance(pointer);
                ref.read();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                SystemUtils.LOG.severe(e.getMessage());
            }
        }
        return ref;
    }

    static int getFileFormat(Path file) {
        if (file == null) {
            return -1;
        }
        String fileName = file.getFileName().toString();
        String ext = fileName.substring(fileName.lastIndexOf("."));
        if (ext.isEmpty()) {
            return -1;
        }
        ext = ext.toLowerCase().replace(".", "");
        Integer code = extensions.get(ext);
        return code != null ? code : -1;
    }

    static int getFormat(String extension) {
        extension = extension.toLowerCase().replace(".", "");
        Integer code = extensions.get(extension);
        return code != null ? code : -1;
    }

    private static int clamp(final int x, final int a, final int b) {
        return x < a ? a : (x > b ? b : x);
    }

    private static ByteBuffer extractBuffer(Raster raster) {
        ByteBuffer buffer = null;
        int width = raster.getWidth();
        int height = raster.getHeight();
        int numBands = raster.getNumBands();
        int pixels = width * height * numBands;
        switch (raster.getTransferType()) {
            case DataBuffer.TYPE_BYTE:
                buffer = ByteBuffer.allocate(pixels + 12);
                buffer.putInt(width);
                buffer.putInt(height);
                buffer.putInt(numBands);
                buffer.put((byte[]) raster.getDataElements(0, 0, raster.getWidth(), raster.getHeight(), null));
                break;
            case DataBuffer.TYPE_USHORT:
            case DataBuffer.TYPE_SHORT:
                buffer = ByteBuffer.allocate(2 * pixels + 12);
                buffer.putInt(width);
                buffer.putInt(height);
                buffer.putInt(numBands);
                buffer.asShortBuffer().put((short[]) raster.getDataElements(0, 0, raster.getWidth(), raster.getHeight(), null));
                break;
            case DataBuffer.TYPE_INT:
                buffer = ByteBuffer.allocate(4 * pixels + 12);
                buffer.putInt(width);
                buffer.putInt(height);
                buffer.putInt(numBands);
                buffer.asIntBuffer().put((int[]) raster.getDataElements(0, 0, raster.getWidth(), raster.getHeight(), null));
                break;
            case DataBuffer.TYPE_FLOAT:
                buffer = ByteBuffer.allocate(4 * pixels + 12);
                buffer.putInt(width);
                buffer.putInt(height);
                buffer.putInt(numBands);
                buffer.asFloatBuffer().put((float[]) raster.getDataElements(0, 0, raster.getWidth(), raster.getHeight(), null));
                break;
            case DataBuffer.TYPE_DOUBLE:
                buffer = ByteBuffer.allocate(8 * pixels + 12);
                buffer.putInt(width);
                buffer.putInt(height);
                buffer.putInt(numBands);
                buffer.asDoubleBuffer().put((double[]) raster.getDataElements(0, 0, raster.getWidth(), raster.getHeight(), null));
                break;
        }
        buffer.position(0);
        return buffer;
    }
}