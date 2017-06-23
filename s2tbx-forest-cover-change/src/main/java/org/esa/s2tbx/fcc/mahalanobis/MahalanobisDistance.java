package org.esa.s2tbx.fcc.mahalanobis;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.esa.s2tbx.fcc.intern.PixelSourceBands;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jcoravu on 6/6/2017.
 */
public class MahalanobisDistance {
    private static final Logger logger = Logger.getLogger(MahalanobisDistance.class.getName());

    public static Int2ObjectMap<PixelSourceBands> computeMahalanobisSquareMatrix(Int2ObjectMap<PixelSourceBands> statistics, double cumulativeProbability) throws InterruptedException {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute the Mahalanobis distance for " + statistics.size() + " regions");
        }

        TrimmingStatisticsMatrix trimmingStatisticsMatrix = new TrimmingStatisticsMatrix(statistics);

        Matrix inverseMatrix = computeInverseMatrix(trimmingStatisticsMatrix);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "computeMahalanobisSquareMatrix matrix="+trimmingStatisticsMatrix+", inverseMatrix="+inverseMatrix);
        }

        if (inverseMatrix != null) {
            MultiplyMatrix resultMatrix = new MultiplyMatrix(trimmingStatisticsMatrix, inverseMatrix);
            TransposeMatrix transposeMatrix = new TransposeMatrix(trimmingStatisticsMatrix);
            MultiplyMatrix squaredMahalanobisMatrix = new MultiplyMatrix(resultMatrix, transposeMatrix);

            MahalanobisDistanceHelper mahalanobisDistanceHelper = new MahalanobisDistanceHelper(trimmingStatisticsMatrix, squaredMahalanobisMatrix, cumulativeProbability);

            int threadCount = Runtime.getRuntime().availableProcessors() + 1;
            for (int i=0; i<threadCount; i++) {
                MahalanobisDistanceRunnable mahalanobisDistanceRunnable = new MahalanobisDistanceRunnable(mahalanobisDistanceHelper);
                Thread thread = new Thread(mahalanobisDistanceRunnable);
                thread.start(); // start the thread
            }

            mahalanobisDistanceHelper.computeDistances();

            return mahalanobisDistanceHelper.waitToFinish();
        }
        return null;
    }

    private static Matrix computeInverseMatrix(Matrix matrix) {
        TransposeMatrix transposeMatrix = new TransposeMatrix(matrix);
        MultiplyMatrix quadraticMatrix = new MultiplyMatrix(transposeMatrix, matrix);
        float value = 1.0f / (float)(matrix.getRowCount() - 1);
        MultiplyByConstantMatrix covarianceMatrix = new MultiplyByConstantMatrix(quadraticMatrix, value);

        float matrixDeterminant = covarianceMatrix.computeDeterminant();
        if (matrixDeterminant == 0.0f) {
            return null;
        }
        CofactorMatrix cofactorMatrix = new CofactorMatrix(covarianceMatrix);
        TransposeMatrix transposeCofactorMatrix = new TransposeMatrix(cofactorMatrix);
        float constant = 1.0f / matrixDeterminant;
        return new MultiplyByConstantMatrix(transposeCofactorMatrix, constant);
    }

    private static class MahalanobisDistanceRunnable implements Runnable {
        private final MahalanobisDistanceHelper mahalanobisDistanceHelper;

        MahalanobisDistanceRunnable(MahalanobisDistanceHelper mahalanobisDistanceHelper) {
            this.mahalanobisDistanceHelper = mahalanobisDistanceHelper;
            this.mahalanobisDistanceHelper.incrementThreadCounter();
        }

        @Override
        public void run() {
            try {
                this.mahalanobisDistanceHelper.computeDistances();
            } catch (Exception exception) {
                logger.log(Level.SEVERE, "Failed to compute the Mahalanobis distance.", exception);
            } finally {
                this.mahalanobisDistanceHelper.decrementThreadCounter();
            }
        }
    }

    private static class MahalanobisDistanceHelper {
        private final TrimmingStatisticsMatrix trimmingStatisticsMatrix;
        private final MultiplyMatrix squaredMahalanobisMatrix;
        private final Int2ObjectMap<PixelSourceBands> validStatistics;
        private final double cumulativeProbability;

        private int threadCounter;
        private int matrixCounter;

        MahalanobisDistanceHelper(TrimmingStatisticsMatrix trimmingStatisticsMatrix, MultiplyMatrix squaredMahalanobisMatrix, double cumulativeProbability) {
            this.trimmingStatisticsMatrix = trimmingStatisticsMatrix;
            this.squaredMahalanobisMatrix = squaredMahalanobisMatrix;
            this.cumulativeProbability = cumulativeProbability;
            this.validStatistics = new Int2ObjectLinkedOpenHashMap<PixelSourceBands>();

            this.threadCounter = 0;
            this.matrixCounter = 0;
        }

        synchronized void incrementThreadCounter() {
            this.threadCounter++;
        }

        synchronized void decrementThreadCounter() {
            this.threadCounter--;
            if (this.threadCounter <= 0) {
                notifyAll();
            }
        }

        synchronized Int2ObjectMap<PixelSourceBands> waitToFinish() throws InterruptedException {
            if (this.threadCounter > 0) {
                wait();
            }
            return validStatistics;
        }

        void computeDistances() {
            int index = -1;
            do {
                synchronized (this) {
                    if (this.matrixCounter < this.squaredMahalanobisMatrix.getRowCount()) {
                        index = this.matrixCounter;
                        this.matrixCounter++;
                    } else {
                        index = -1;
                    }
                }
                if (index >= 0) {
                    float squareDistance = this.squaredMahalanobisMatrix.getValueAt(index, index);
                    if (Math.sqrt(squareDistance) <= this.cumulativeProbability) {
                        synchronized (this.validStatistics) {
                            this.validStatistics.put(this.trimmingStatisticsMatrix.getRegionKeyAt(index), this.trimmingStatisticsMatrix.getRegionAt(index));
                        }
                    }
                }
            } while (index >= 0);
        }
    }
}
