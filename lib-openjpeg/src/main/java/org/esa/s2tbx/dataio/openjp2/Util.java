package org.esa.s2tbx.dataio.openjp2;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.esa.snap.core.util.SystemUtils;

import java.awt.image.DataBuffer;
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

    public static int[][] read(Path path, int dataType) throws IOException {
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
        int[][] out = new int[2][];
        out[0] = new int[2];
        out[1] = new int[(size - 8) / pixSize];
        try (RandomAccessFile in = new RandomAccessFile(path.toFile(), "r")) {
            try (FileChannel file = in.getChannel()) {
                buf = file.map(FileChannel.MapMode.READ_ONLY, 0, size);
                out[0][0] = buf.getInt();
                out[0][1] = buf.getInt();
                if (pixSize == 4) {
                    for (int i = 0; i < out[1].length; i++) {
                        out[1][i] = buf.getInt();
                    }
                } else if (pixSize == 2) {
                    for (int i = 0; i < out[1].length; i++) {
                        out[1][i] = buf.getShort();
                    }
                } else {
                    for (int i = 0; i < out[1].length; i++) {
                        out[1][i] = buf.get();
                    }
                }
            }
        }
        return out;
    }

    public static Path write(int width, int height, int[] values, int dataType, Path toFile, Function<Path, Void> completionCallBack) {
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

}