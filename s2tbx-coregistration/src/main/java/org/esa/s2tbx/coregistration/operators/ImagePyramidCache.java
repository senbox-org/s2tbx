package org.esa.s2tbx.coregistration.operators;

import org.esa.s2tbx.coregistration.MatrixUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ImagePyramidCache {

    private BufferedImage sourceImage;
    private List<BufferedImage> levelImages;
    private int levels;
    private boolean computed = false;
    private static final float[] burt1D = new float[]{0.05f, 0.25f, 0.4f, 0.25f, 0.05f};

    public ImagePyramidCache(BufferedImage image, int levels){
        this.sourceImage = image;
        this.levels = levels;
    }

    public void compute(){
        if(computed == false){
            levelImages = new ArrayList<>(levels + 1);
            levelImages.add(sourceImage);
            for (int k = 0; k < levels; k++) {
                levelImages.add(pyramBurt(levelImages.get(k)));
            }
            computed = true;
        }
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

    public BufferedImage getImage(int level){
        if(!computed) {
            compute();
        }
        return levelImages.get(level);
    }

}
