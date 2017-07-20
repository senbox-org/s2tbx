package org.esa.s2tbx.coregistration;


import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Mask;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.ProductNodeGroup;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.Tile;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.util.ProductUtils;
import org.netbeans.api.progress.aggregate.ProgressMonitor;
import org.esa.snap.utils.StringHelper;

import javax.imageio.ImageIO;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImagePyramid;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.ExtremaDescriptor;
import javax.media.jai.operator.SubsampleAverageDescriptor;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Coregistration
 *
 * @author  Ramona Manda
 * @since   6.0.0
 */
@OperatorMetadata(
        alias = "CoregistrationOp",
        version="1.0",
        category = "Optical",
        description = "The 'Coregistration Processor' operator ...",
        authors = "RamonaM",
        copyright = "Copyright (C) 2016 by CS ROMANIA")
public class CoregistrationOp //extends Operator
{

    public boolean contrast = false;
    public int levels = 6;
    public int rank = 4;
    private static final float[] burt1D = new float[]{0.05f, 0.25f, 0.4f, 0.25f, 0.05f};

    public static BufferedImage img = null;


    public CoregistrationOp() {
    }

    public void doExecute(Product sourceProduct, int bandIndex) {
        //[ToDO] if necessary, JAI dithering operation compresses the three bands of an RGB image to a single-banded byte image.

        int level = sourceProduct.getBandAt(bandIndex).getMultiLevelModel().getLevelCount();
        //RenderedImage sourceImage = sourceProduct.getBandAt(0).getSourceImage().getImage(level);
        RenderedImage sourceImage = img;
        RenderedImage outputImage = sourceImage;
        if(contrast) {
            outputImage = applyContrast(sourceImage);
        }

        RenderedImage[] pyramid = pyramid(outputImage, levels);

        for(int k=pyramid.length-1;k>=0;k++) {
            RenderedImage levelImage = pyramid[k];
            if (contrast) {
                //clahe should be applied directly on RenderedImage...
                levelImage = equalize(convertRenderedImage(levelImage));
            }
            //TODO init meshgrid and u/v if necessary...
            if(rank != 0){
                //rank apply from MatrixUtils? Or find JAI method for it
            }
            //............................
        }

    }

    private RenderedImage applyContrast(RenderedImage inputImage) {
        final RenderedImage extrema = ExtremaDescriptor.create(inputImage, null, 1, 1, false, 1, null);
        double[][] minMax = (double[][]) extrema.getProperty("Extrema");
        double min = minMax[0][0];
        double max = minMax[1][0];
        double dif = max-min;

        double[] multiplyByThis = new double[1];
        multiplyByThis[0] = 1.0/(max-min);
        double[] addThis = new double[1];
        addThis[0] = min/(max-min);
        ParameterBlock pbRescale = new ParameterBlock();
        pbRescale.add(multiplyByThis);
        pbRescale.add(addThis);
        pbRescale.addSource(inputImage);
        PlanarImage outImage = (PlanarImage)JAI.create("rescale", pbRescale);
        return outImage;
    }

    private RenderedImage[] pyramid(RenderedImage inputImage, int level){
        RenderedImage[] imagePyramid = new RenderedImage[level+1];
        imagePyramid[0] = inputImage;
        for (int k=0;k<=level;k++){
            imagePyramid[k+1] = pyramBurt(imagePyramid[k]);
        }
        return imagePyramid;
    }


    private RenderedImage pyramBurt(RenderedImage inputImage){
        int rad = 2;
        RenderedImage borderedImage = addBorder(inputImage, rad, rad, rad, rad);
        int kernelSize = burt1D.length;
        KernelJAI kernel = new KernelJAI(kernelSize,kernelSize,burt1D);
        PlanarImage conv1 = JAI.create("convolve", borderedImage, kernel);
        PlanarImage conv2 = JAI.create("convolve", conv1, kernel);

        //resize with 1/2 factor:
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        RenderedOp resizeOp = SubsampleAverageDescriptor.create(conv2,
                0.5, 0.5, hints);

        BufferedImage bufferedResizedImage = resizeOp.getAsBufferedImage();

        return bufferedResizedImage;
    }

    private RenderedImage addBorder(RenderedImage inputImage, int left,int right, int top, int bottom) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(inputImage);
        pb.add(left);
        pb.add(right);
        pb.add(top);
        pb.add(bottom);
        pb.add(BorderExtender.BORDER_ZERO);
        return JAI.create("border", pb);
    }

    /*public static class Spi extends OperatorSpi {

        public Spi() {
            super(CoregistrationOp.class);
        }
    }*/

    BufferedImage equalize(BufferedImage src){
        BufferedImage nImg = new BufferedImage(src.getWidth(), src.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = src.getRaster();
        WritableRaster er = nImg.getRaster();
        int totpix= wr.getWidth()*wr.getHeight();
        int[] histogram = new int[256];

        for (int x = 0; x < wr.getWidth(); x++) {
            for (int y = 0; y < wr.getHeight(); y++) {
                histogram[wr.getSample(x, y, 0)]++;
            }
        }

        int[] chistogram = new int[256];
        chistogram[0] = histogram[0];
        for(int i=1;i<256;i++){
            chistogram[i] = chistogram[i-1] + histogram[i];
        }

        float[] arr = new float[256];
        for(int i=0;i<256;i++){
            arr[i] =  (float)((chistogram[i]*255.0)/(float)totpix);
        }

        for (int x = 0; x < wr.getWidth(); x++) {
            for (int y = 0; y < wr.getHeight(); y++) {
                int nVal = (int) arr[wr.getSample(x, y, 0)];
                er.setSample(x, y, 0, nVal);
            }
        }
        nImg.setData(er);
        return nImg;
        /* [TODO] solution to access pixel values to avoid RenderedImage transformation ?????
        1 int width = pi.getWidth();
2 int height = pi.getHeight();
3 SampleModel sm = pi.getSampleModel();
4 int nbands = sm.getNumBands();
5 int[] pixel = new int[nbands];
6 RandomIter iterator = RandomIterFactory.create(pi, null);
7 for(int h=0;h<height;h++)
8 for(int w=0;w<width;w++)
9 {
10 iterator.getPixel(w,h,pixel);
11 System.out.print("at ("+w+","+h+"): ");
12 for(int band=0;band<nbands;band++)
13 System.out.print(pixel[band]+" ");
14 System.out.println();
15 }
         */
    }

    public static BufferedImage convertRenderedImage(RenderedImage img) {
        if (img instanceof BufferedImage) {
        			return (BufferedImage) img;
        		}
        ColorModel cm = img.getColorModel();
        int width = img.getWidth();
        int height = img.getHeight();
        WritableRaster raster = cm
        		.createCompatibleWritableRaster(width, height);
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        Hashtable properties = new Hashtable<>();
        String[] keys = img.getPropertyNames();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                properties.put(keys[i], img.getProperty(keys[i]));
            }
        }
        BufferedImage result = new BufferedImage(cm, raster,
        			isAlphaPremultiplied, properties);
        img.copyData(raster);
        return result;
    }

    public static void main(String args[]){
        try {
            img = ImageIO.read(new File("strawberry.jpg"));
        } catch (IOException e) {
            //[TODO]
        }

        CoregistrationOp op = new CoregistrationOp();
        op.doExecute(null, 0);
    }

}
