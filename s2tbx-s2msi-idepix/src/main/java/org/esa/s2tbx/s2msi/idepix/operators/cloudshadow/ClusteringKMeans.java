package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * todo: add comment
 *
 */
public class ClusteringKMeans {

    // input: array of 1-dimensional image arrays
    public double[][] computedKMeansCluster(double[]... images) {
        AdaptedIsoClusterer clusterer = new AdaptedIsoClusterer(AnalyzeCloudShadowIDAreas.clusterCount,
                                                                AnalyzeCloudShadowIDAreas.maxIterCount);
        List<Clusterable> list = new ArrayList<>();
        for (int xyPos = 0; xyPos < images[0].length; xyPos++) {
            double[] values = new double[images.length];
            for (int imageIndex = 0; imageIndex < images.length; imageIndex++) {
                values[imageIndex] = images[imageIndex][xyPos];
            }
            list.add(new DoublePoint(values));
        }
        List<CentroidCluster<Clusterable>> cluster = clusterer.cluster(list);
        double[][] clusterCentroidArray = new double[AnalyzeCloudShadowIDAreas.clusterCount][S2IdepixCloudShadowOp.SENSOR_BAND_CLUSTERING];


//        double[][] clusterArray = new double[AnalyzeCloudShadowIDAreas.clusterCount][counter+2];

//        for (int dd = 0; dd < clusterCount; dd++) {
//            for (int ee = 0; ee <counter+2; ee++) {
//                clusterArray[dd][ee] = Double.NaN;
//            }
//        }


        int countClusterNumber = 0;
        for (CentroidCluster<Clusterable> centroidCluster : cluster) {
            for (int ii = 0; ii < S2IdepixCloudShadowOp.SENSOR_BAND_CLUSTERING; ii++) {
                clusterCentroidArray[countClusterNumber][ii] = centroidCluster.getCenter().getPoint()[ii];
                //System.out.printf("Centroid in cluster:  %f %n", centroidCluster.getCenter().getPoint()[0]);

                //System.out.printf("Centroid in %d cluster:  %.2f %n", countClusterNumber, clusterCentroidArray[countClusterNumber]);
                //System.out.printf("Count of all points in %d cluster: %d \n",countClusterNumber, centroidCluster.getPoints().size());
            }


//            System.out.println("Array of all cluster points in this cluster:");
//            Clusterable[] clusterableArray = centroidCluster.getPoints().toArray(new Clusterable[centroidCluster.getPoints().size()]);
//            for (int i = 0; i < clusterableArray.length; i++) {
//                Clusterable clusterable = clusterableArray[i];
//                clusterArray[countClusterNumber][i] = clusterable.getPoint()[0];
//                System.out.printf("clusterable[%d]= %.2f  %d %.2f%n", i, clusterable.getPoint()[0], countClusterNumber, clusterArray[countClusterNumber][i]);
//            }
//            System.out.println("Count of all points in this cluster : " + centroidCluster.getPoints().size());
            countClusterNumber++;
        }
        // done
        return clusterCentroidArray;
    }
}

