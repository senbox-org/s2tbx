package org.esa.s2tbx.coregistration.operators;

import com.bc.ceres.core.VirtualDir;
import com.sun.media.jai.codec.TIFFEncodeParam;
import org.esa.s2tbx.coregistration.MatrixUtils;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Class utility for saving/loading/computing images from pyramid, and also for saving and deleting other temporary images
 */
public class ImagePyramidCache {
    private Map<Integer, String> imagesLocation;
    private int levels;
    private boolean computed = false;
    private static final float[] burt1D = new float[]{0.05f, 0.25f, 0.4f, 0.25f, 0.05f};
    private static File tmpDir;

    static {
        try {
            tmpDir = VirtualDir.createUniqueTempDir();
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger("ImagePyramidCache").warning("Error on  creating temporary folder " + ex.getMessage());
            //TODO other dir
        }
    }

    public ImagePyramidCache(BufferedImage image, int levels) {
        this.levels = levels;
        try {
            String levelTmpFile = "L0img" + System.currentTimeMillis();
            writeImage(image, levelTmpFile);
            imagesLocation = new HashMap<>();
            imagesLocation.put(0, levelTmpFile);
            BufferedImage previousImage = image;
            for (int k = 0; k < levels; k++) {
                BufferedImage currentImage = pyramBurt(previousImage);
                levelTmpFile = "L" + (k + 1) + "img" + System.currentTimeMillis();
                writeImage(currentImage, levelTmpFile);
                imagesLocation.put((k + 1), levelTmpFile);
                previousImage = currentImage;
            }
            Runtime.getRuntime().gc();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void compute() {
        /*if(computed == false){
            levelImages = new ArrayList<>(levels + 1);
            levelImages.add(sourceImage);
            for (int k = 0; k < levels; k++) {
                levelImages.add(pyramBurt(levelImages.get(k)));
            }
            computed = true;
        }*/
    }

    public static BufferedImage pyramBurt(BufferedImage inputImage) {
        BufferedImage convolvedImg = ImageOperations.doubleConvolve(inputImage, burt1D);

        //resize with 1/2 factor:
        /*RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        RenderedOp resizeOp = SubsampleAverageDescriptor.create(convolvedImg,
                (Double) 0.5, (Double) 0.5, hints);

        BufferedImage bufferedResizedImage = resizeOp.getAsBufferedImage();
        */
        float[][] resized = MatrixUtils.subsample(ImageOperations.matrixFromImage(convolvedImg));
        return ImageOperations.imageFromMatrix(resized);
    }

    //TODO only for tests
    public static BufferedImage[] pyramid2(String prefix, int level) {
        BufferedImage[] imagePyramid = new BufferedImage[level + 1];

        for (int k = 0; k <= level; k++) {
//            try {
//                GeoTiffProductReader reader = new GeoTiffProductReader(new GeoTiffProductReaderPlugIn());
//                Product prod;
//                prod = reader.readProductNodes(new File(prefix + (k + 1) + ".tif"), null);
//                imagePyramid[k] = convertBufferedImage(prod.getBandAt(0).getSourceImage().getImage(0));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return imagePyramid;
    }

    public BufferedImage getImage(int level) {
        /*if(!computed) {
            compute();
        }
        return levelImages.get(level);
        */
        return readImage(imagesLocation.get(level)).getAsBufferedImage();
    }

    public static void writeImage(RenderedImage image, String fileName) {
        String tmpFileName = new File(tmpDir, fileName).getAbsolutePath();
        TIFFEncodeParam param = new TIFFEncodeParam();
        //param.setCompression(TIFFEncodeParam.COMPRESSION_JPEG_TTN2);
        JAI.create("filestore", image, tmpFileName, "TIFF");//, param);
    }

    public static RenderedOp readImage(String fileName) {
        String tmpFileName = new File(tmpDir, fileName).getAbsolutePath();
        return JAI.create("fileload", tmpFileName);
    }

    public void cleanPyramid() {
        for (int key : imagesLocation.keySet()) {
            new File(tmpDir, imagesLocation.get(key)).delete();
        }
    }

    public static void cleanLocation() throws IOException {
        for (String file : tmpDir.list()) {
            try {
                File f = new File(file);
                f.delete();
            } catch (Exception ex) {
            }
        }
        tmpDir.delete();
    }

}
