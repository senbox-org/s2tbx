package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

/**
 * @author Tonio Fincke
 */
public class AdaptedKMeansPlusPlusClusterer<T extends Clusterable> extends KMeansPlusPlusClusterer {

    public AdaptedKMeansPlusPlusClusterer(int k, int maxIterations) {
        super(k, maxIterations);
    }

}
