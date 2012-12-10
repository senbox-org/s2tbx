package org.esa.beam.dataio.s3.synergy;

import org.esa.beam.util.math.ArcDistanceCalculator;
import org.esa.beam.util.math.DistanceCalculator;

class ArcDistanceCalculatorFactory implements DistanceCalculatorFactory {

    @Override
    public DistanceCalculator create(double lon, double lat) {
        return new ArcDistanceCalculator(lon, lat);
    }
}
