package org.esa.s2tbx.fcc.chi.distribution;

/**
 * Created by jcoravu on 8/6/2017.
 */
public class ChiSquareDistribution {

    private ChiSquareDistribution() {
    }

    public static double computeChiSquare(int degreesOfFreedom, double criticalValue) {
        if (criticalValue < 0 || degreesOfFreedom < 1) {
            return 0.0;
        }
        double X = criticalValue * 0.5d;
        if (degreesOfFreedom == 2) {
            return Math.exp(-1.0d * X);
        }
        double K = degreesOfFreedom * 0.5d;
        double ln_PV = naturalLogarithmOfIncompleteGammaFunction(K, X);
        double gamma = approximateGamma(K);
        ln_PV -= gamma;
        return 1.0d - Math.exp(ln_PV);
    }

    private static double naturalLogarithmOfIncompleteGammaFunction(double S, double Z) {
        if (Z < 0.0d) {
            return 0.0d;
        }
        double Sc = (Math.log(Z) * S) - Z - Math.log(S);
        double K = KM(S, Z);
        return Math.log(K) + Sc;
    }

    private static double KM(double S, double Z) {
	    double Sum = 1.0d;
	    double Nom = 1.0d;
	    double Denom = 1.0d;

        for(int i = 0; i < 1000; i++) { // Loops for 1000 iterations
            Nom *= Z;
            S++;
            Denom *= S;
            Sum += (Nom / Denom);
        }
        return Sum;
    }

    private static double approximateGamma(double Z) {
        double RECIP_E = 0.36787944117144232159552377016147;  // RECIP_E = (E^-1) = (1.0 / E)
        double TWOPI = 6.283185307179586476925286766559;  // TWOPI = 2.0 * PI

        double D = 1.0d / (10.0d * Z);
        D = 1.0d / ((12.0d * Z) - D);
        D = (D + Z) * RECIP_E;
        D = Math.pow(D, Z);
        D *= Math.sqrt(TWOPI / Z);
        return D;
    }
}
