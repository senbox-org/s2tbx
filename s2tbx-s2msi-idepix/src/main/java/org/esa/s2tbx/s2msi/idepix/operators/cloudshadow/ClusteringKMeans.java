package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tonio Fincke
 * @author Michael Paperin
 */
class ClusteringKMeans {

    static double[][] computedKMeansCluster(double[]... images) {
        AdaptedIsoClustering clusterer = new AdaptedIsoClustering(CloudShadowIDAnalyzer.clusterCount,
                                                                  CloudShadowIDAnalyzer.maxIterCount);
        List<Clusterable> list = new ArrayList<>();
        for (int xyPos = 0; xyPos < images[0].length; xyPos++) {
            double[] values = new double[images.length];
            for (int imageIndex = 0; imageIndex < images.length; imageIndex++) {
                values[imageIndex] = images[imageIndex][xyPos];
            }
            list.add(new DoublePoint(values));
        }
        List<CentroidCluster<Clusterable>> clusters = clusterer.cluster(list);
        double[][] clusterCentroidArray = new double[CloudShadowIDAnalyzer.clusterCount][images.length];

        int countClusterNumber = 0;
        for (CentroidCluster<Clusterable> centroidCluster : clusters) {
            for (int i = 0; i < images.length; i++) {
                clusterCentroidArray[countClusterNumber][i] = centroidCluster.getCenter().getPoint()[i];
            }
            countClusterNumber++;
        }
        return clusterCentroidArray;
    }
}

