package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.annotations.TargetProduct;
import org.esa.snap.core.gpf.internal.OperatorExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * todo: add comment
 */
@OperatorMetadata(alias = "CCICloudShadow",
        category = "Optical",
        authors = "Grit Kirches, Michael Paperin, Olaf Danne, Tonio Fincke, Dagmar Mueller",
        copyright = "(c) Brockmann Consult GmbH",
        version = "1.0",
        description = "Algorithm detecting cloud shadow...")
public class S2IdepixCloudShadowOp extends Operator {

    @SourceProduct(description = "The classification product.")
    private Product s2ClassifProduct;

    @SourceProduct(alias = "s2CloudBuffer", optional = true)
    private Product s2CloudBufferProduct;      // has only classifFlagBand with buffer added

    @SourceProduct(alias = "sourceProduct", optional = true)
    private Product preProcessedProduct;

    @TargetProduct
    private Product targetProduct;

    @Parameter(description = "The mode by which clouds are detected. There are three options: Land/Water, Multiple Bands" +
            "or Single Band", valueSet = {"LandWater", "MultiBand", "SingleBand"}, defaultValue = "LandWater")
    private String mode;

    @Parameter(description = "Whether to also compute mountain shadow", defaultValue = "true")
    private boolean computeMountainShadow;

    public final static String BAND_NAME_CLOUD_SHADOW = "FlagBand";

    //private Map<Integer, double[]> meanReflPerTile = new HashMap<>();
    private Map<Integer, double[][]> meanReflPerTile = new HashMap<>();
    private Map<Integer, Integer> NCloudOverLand = new HashMap<>();
    private Map<Integer, Integer> NCloudOverWater = new HashMap<>();
    private Map<Integer, Integer> NValidPixelTile = new HashMap<>();

    @Override
    public void initialize() throws OperatorException {
        HashMap<String, Product> preInput = new HashMap<>();
        preInput.put("s2ClassifProduct", s2ClassifProduct);
        preInput.put("s2CloudBufferProduct", s2CloudBufferProduct);
        Map<String, Object> preParams = new HashMap<>();
        preParams.put("computeMountainShadow", computeMountainShadow);
        preParams.put("mode", mode);

        //Preprocessing
        final String operatorAlias = OperatorSpi.getOperatorAlias(S2IdepixPreCloudShadowOp.class);
        final S2IdepixPreCloudShadowOp cloudShadowPreProcessingOperator =
                (S2IdepixPreCloudShadowOp) GPF.getDefaultInstance().createOperator(operatorAlias, preParams, preInput, null);

        //trigger computation of all tiles
        final OperatorExecutor operatorExecutor = OperatorExecutor.create(cloudShadowPreProcessingOperator);
        operatorExecutor.execute(ProgressMonitor.NULL);

        NCloudOverLand = cloudShadowPreProcessingOperator.getNCloudOverLandPerTile();
        NCloudOverWater = cloudShadowPreProcessingOperator.getNCloudOverWaterPerTile();
        meanReflPerTile= cloudShadowPreProcessingOperator.getMeanReflPerTile();
        NValidPixelTile = cloudShadowPreProcessingOperator.getNValidPixelTile();
        //writingMeanReflAlongPath(); // for development of minimum analysis.


        /*for(int key : NCloudOverWater.keySet()){
            System.out.println("valid pixel: "+NValidPixelTile.get(key));
            System.out.println((float)(NCloudOverLand.get(key) +NCloudOverWater.get(key))/ (float) NValidPixelTile.get(key));
        }*/


        int[] bestOffset = findOverallMinimumReflectance();

        int a = chooseBestOffset(bestOffset);
        System.out.println("bestOffset all "+ bestOffset[0]);
        System.out.println("bestOffset land "+bestOffset[1]);
        System.out.println("bestOffset water "+bestOffset[2]);

        System.out.println("chosen Offset "+a);

        //writingMeanReflAlongPath();

        //Write target product
        //setTargetProduct(preProcessedProduct);


        HashMap<String, Product> postInput = new HashMap<>();
        postInput.put("s2ClassifProduct", s2ClassifProduct);
        postInput.put("s2CloudBufferProduct", s2CloudBufferProduct);
        //put in here the input products that are required by the post-processing operator
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("bestOffset", a);
        postParams.put("mode", mode);
        //put in here any parameters that might be requested by the post-processing operator

        //Postprocessing

        final String operatorAliasPost = OperatorSpi.getOperatorAlias(S2IdepixPostCloudShadowOp.class);
        final S2IdepixPostCloudShadowOp cloudShadowPostProcessingOperator =
                (S2IdepixPostCloudShadowOp) GPF.getDefaultInstance().createOperator(operatorAliasPost, postParams, postInput, null);

        //trigger computation of all tiles
        final OperatorExecutor operatorExecutorPost = OperatorExecutor.create(cloudShadowPostProcessingOperator);
        operatorExecutorPost.execute(ProgressMonitor.NULL);

        targetProduct = cloudShadowPostProcessingOperator.getTargetProduct();

        setTargetProduct(targetProduct);

    }

    public void writingMeanReflAlongPath(){
        for(int j=0; j<3; j++){
            int N=0;
            for(int key : meanReflPerTile.keySet()){
                N=meanReflPerTile.get(key)[j].length;
                break;
            }

            for(int key: meanReflPerTile.keySet()){
                if(j==0) System.out.print((float) (NCloudOverLand.get(key)+NCloudOverWater.get(key))/NValidPixelTile.get(key) + "\t");
                if(j==1) System.out.print((float) NCloudOverLand.get(key)/NValidPixelTile.get(key)+ "\t");
                if(j==2) System.out.print((float) NCloudOverWater.get(key)/NValidPixelTile.get(key)+ "\t");
            }
            System.out.println();

            for(int i=0; i<N; i++){

                for(int key : meanReflPerTile.keySet()){

                    System.out.print(meanReflPerTile.get(key)[j][i] + "\t");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public int chooseBestOffset(int[] bestOffset){
        int NCloudWater=0;
        int NCloudLand=0;

        int out;

        if(NCloudOverWater.size()>0){
            for(int index : NCloudOverWater.keySet()){
                NCloudWater += NCloudOverWater.get(index);
            }
        }
        if(NCloudOverLand.size()>0){
            for(int index : NCloudOverLand.keySet()){
                NCloudLand += NCloudOverLand.get(index);
            }
        }

        int Nall= NCloudLand + NCloudWater;

        float relCloudLand = (float) NCloudLand/Nall;
        float relCloudWater = (float) NCloudWater/Nall;


        if(relCloudLand> 2*relCloudWater){
            out = bestOffset[1];
        }
        else if(relCloudWater > 2*relCloudLand){
            out = bestOffset[2];
        }
        else out = bestOffset[0];


        return out;
    }

    /*
    public int findOverallMinimumReflectance(){
        double[] scaledTotalReflectance = new double[meanReflPerTile.get(0).length];

        for(int key : meanReflPerTile.keySet()){
            double[] meanValues = meanReflPerTile.get(key);

            double maxValue = 0.;
            for(int i=0; i<meanValues.length; i++){
                if(meanValues[i]>maxValue){
                    maxValue = meanValues[i];
                }
            }

            for(int i=0; i<meanValues.length; i++){
                scaledTotalReflectance[i] += meanValues[i]/maxValue;
            }
        }

        double minValue = 10.;
        int offset = 0;
        for(int i=1; i<scaledTotalReflectance.length; i++){
            if(scaledTotalReflectance[i]<minValue){
                minValue = scaledTotalReflectance[i];
                offset = i;
            }
        }
        return offset;
    }*/

    public int[] findOverallMinimumReflectance(){

        double[][] scaledTotalReflectance = new double[3][meanReflPerTile.get(0)[0].length];

        String[] cases = {"all", "land", "water"};

        for (int j=0; j < 3; j++){

            /*Checking the meanReflPerTile:
                - if it has no relative minimum other than the first or the last value, it is excluded.
                - if it contains NaNs, it is excluded.
                Exclusion works by setting values to NaN.
            */
            for(int key : meanReflPerTile.keySet()){
                //float part = (float)(NCloudOverLand.get(key) +NCloudOverWater.get(key))/ (float) NValidPixelTile.get(key);

                double[][] meanValues = meanReflPerTile.get(key);
                boolean exclude = false;

                List<Integer> relativeMinimum = indecesRelativMaxInArray(meanValues[j], false);
                //System.out.println(relativeMinimum.toArray());
                if (relativeMinimum.contains(0)) relativeMinimum.remove(relativeMinimum.indexOf(0));
                if (relativeMinimum.contains(meanValues[j].length-1)) relativeMinimum.remove(relativeMinimum.indexOf(meanValues[j].length-1));

                //smallest relative minimum is in second part of the path -> exclude
                if (relativeMinimum.indexOf(0) > meanValues[j].length/2.) exclude = true;

                if (relativeMinimum.size()==0) exclude = true;

                if (exclude){
                   // System.out.println("exclude:"+ cases[j] + " cloud per valid: "+ part);
                    for(int i=0; i<meanValues[j].length; i++){
                        meanValues[j][i] = Double.NaN;
                    }
                }
            }

            //Finding the minimum in brightness in the scaled mean function.
            for(int key : meanReflPerTile.keySet()){
                double[][] meanValues = meanReflPerTile.get(key);

                double[] maxValue = new double[3];

                for(int i=0; i<meanValues[j].length; i++){
                    if (!Double.isNaN(meanValues[j][i])) {
                        if(meanValues[j][i] > maxValue[j]){
                            maxValue[j] = meanValues[j][i];
                        }
                    }
                }
                for(int i=0; i<meanValues[j].length; i++){
                    if (!Double.isNaN(meanValues[j][i]) && maxValue[j]>0) {
                        //System.out.println(meanValues[j][i]/maxValue[j]);
                        scaledTotalReflectance[j][i] += meanValues[j][i]/maxValue[j];
                    }
                }
                //System.out.println();
            }
        }

        int[] offset = new int[3];

        for (int j=0; j<3; j++){

            List<Integer> test = indecesRelativMaxInArray(scaledTotalReflectance[j], false);
            if (test.contains(0)) test.remove(test.indexOf(0));
            if (test.contains(scaledTotalReflectance[j].length-1)) test.remove(test.indexOf(scaledTotalReflectance[j].length-1));

            if (test.size()>0){
                offset[j] = test.get(0);
            }

            /*
            double minValue = 10.;
            for(int i=1; i<scaledTotalReflectance[0].length; i++){
                //System.out.println(scaledTotalReflectance[j][i]);
                if(scaledTotalReflectance[j][i] < minValue){
                    minValue = scaledTotalReflectance[j][i];
                    offset[j] = i;
                }
            }
            */
            //System.out.println();

        }
        return offset;
    }

    public List<Integer> indecesRelativMaxInArray(double[] x, boolean findMax){
        int lx = x.length;

        List<Integer> ID = new ArrayList<>();

        boolean valid = true;
        int i =0;
        while (i<lx && valid){
            if (Double.isNaN(x[i])) valid = false;
            i++;
        }

        if(valid){
            double fac=1.;
            if (!findMax) fac = -1.;

            if (fac*x[0]> fac*x[1]) ID.add(0);
            if (fac*x[lx - 1] > fac*x[lx - 2]) ID.add(lx-1);

            for ( i=1; i< lx - 1; i++){
                if(fac*x[i] > fac*x[i - 1] && fac*x[i] > fac*x[i + 1]) ID.add(i);
            }
        }
        else{
            ID.add(0);
            ID.add(lx-1);
        }

        return ID;
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(S2IdepixCloudShadowOp.class);
        }
    }

}
