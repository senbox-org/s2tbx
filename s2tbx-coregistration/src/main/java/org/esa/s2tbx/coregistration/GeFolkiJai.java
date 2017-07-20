package org.esa.s2tbx.coregistration;

import com.bc.ceres.glevel.MultiLevelImage;
import com.bc.ceres.glevel.support.AbstractMultiLevelSource;
import com.bc.ceres.glevel.support.DefaultMultiLevelImage;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.esa.s2tbx.coregistration.operators.ContrastOperator;

import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.BorderDescriptor;
import javax.media.jai.operator.ConvolveDescriptor;
import javax.media.jai.registry.RenderedRegistryMode;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by ramonag on 5/4/2017.
 */
@Deprecated
public class GeFolkiJai {
    //init
    int levels = 6;
    int iter = 2;
    boolean contrast = false;
    int rank = 4;

    public GeFolkiJai(){

    }

    public void process(RenderedImage I0, RenderedImage I1){
        RenderedImage renderedImage=null;
        try {
            renderedImage=javax.imageio.ImageIO.read(new File("C:\\Users\\ramonag\\Desktop\\4b0d9331c827e7b2cffa8608d6fa4622.jpg"));
        }
        catch (  IOException e) {
            e.printStackTrace();
        }


        I1 = applyContrast(renderedImage);


    }

    private RenderedImage pyramBurt(RenderedImage image){
        float a = 0.4f;
        float aComp = 0.25f-a/2.0f;
        float[] burt1D = {aComp,0.25f,a,0.25f,aComp};
        int row = image.getHeight();
        float rad = (burt1D.length-1f)/2f;
        int radI = (int) rad;// partea intreaga!!!!!!
        RenderedImage newImg = setConstantBorder(image, new Dimension(radI, row), 0);

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(newImg);
        pb.add(burt1D);
        RenderedImage convImg = JAI.create("Convolve", pb);
        //again conv
        pb = new ParameterBlock();
        pb.addSource(convImg);
        pb.add(burt1D);
        RenderedImage convImg2 = JAI.create("Convolve", pb);

return null;

    }

    private RenderedImage applyContrast(RenderedImage image1){
        //final ParameterBlock parameterBlock = new ParameterBlock();
            //parameterBlock.setSource(image1, 0);
        ParameterBlockJAI pb = new ParameterBlockJAI("Extrema",
                RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", image1);

        //pb.setParameter("roi", null);
        //pb.setParameter("xPeriod", 1);
        //pb.setParameter("yPeriod", 1);
        //pb.setParameter("saveLocations", null);
        //pb.setParameter("maxRuns", null);

        RenderedOp extremaOp = JAI.create("Extrema", pb, null);
        double[][] extrema = (double[][]) extremaOp.getProperty("extrema");
System.out.println(extrema);
        RenderedOp contrastOp = JAI.create("Contrast", pb, null);
        return new ContrastOperator(image1, null, (int) extrema[0][0], (int) extrema[1][0]);

    }

    public static RenderedOp setConstantBorder(RenderedImage img,
                                               Dimension border, double constVal) {
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(img);
        pb.add(border.width);
        pb.add(border.height);
        pb.add(border.width);
        pb.add(border.height);
        pb.add(BorderExtender.BORDER_ZERO);
        int numbands = img.getSampleModel().getNumBands();
        double[] fillValue = new double[numbands];
        for (int i=0;i<numbands;i++) {
            fillValue[i] = constVal;
        }
        pb.add( fillValue);
        return JAI.create("border", pb);
    }

    public static void main(String args[]){
        GeFolkiJai jai = new GeFolkiJai();
        jai.process(null, null);
    }
}
