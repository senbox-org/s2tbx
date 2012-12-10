package org.esa.beam.dataio.s3.synergy;

import org.esa.beam.util.math.DistanceCalculator;

interface DistanceCalculatorFactory {

    DistanceCalculator create(double lon, double lat);
}
