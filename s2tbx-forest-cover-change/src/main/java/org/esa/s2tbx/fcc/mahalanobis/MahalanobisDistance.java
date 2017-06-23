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

    public static Int2ObjectMap<PixelSourceBands> filterValidRegionsUsingMahalanobisDistance(Int2ObjectMap<PixelSourceBands> validRegionStatictics, double cumulativeProbability)
                                                                                 throws InterruptedException {

        int threadCount = Runtime.getRuntime().availableProcessors() + 1;

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "Compute the Mahalanobis distance for " + validRegionStatictics.size() + " valid regions and " + threadCount + " threads.");
        }

        TrimmingStatisticsMatrix trimmingStatisticsMatrix = new TrimmingStatisticsMatrix(validRegionStatictics);

        Matrix inverseMatrix = computeInverseMatrix(trimmingStatisticsMatrix, threadCount);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, ""); // add an empty line
            logger.log(Level.FINE, "computeMahalanobisSquareMatrix matrix="+trimmingStatisticsMatrix+", inverseMatrix="+inverseMatrix);
        }

        if (inverseMatrix != null) {
            MahalanobisDistanceHelper mahalanobisDistanceHelper = new MahalanobisDistanceHelper(trimmingStatisticsMatrix, inverseMatrix, cumulativeProbability);

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

    private static Matrix computeInverseMatrix(Matrix matrix, int threadCount) throws InterruptedException {
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
        Matrix result = new MultiplyByConstantMatrix(transposeCofactorMatrix, constant);

        StorageMatrixHelper storageMatrixHelper = new StorageMatrixHelper(result);
        for (int i=0; i<threadCount; i++) {
            StorageMatrixRunnable storageMatrixRunnable = new StorageMatrixRunnable(storageMatrixHelper);
            Thread thread = new Thread(storageMatrixRunnable);
            thread.start(); // start the thread
        }

        storageMatrixHelper.computeMatrixCells();

        return storageMatrixHelper.waitToFinish();
    }

    private static class StorageMatrixRunnable implements Runnable {
        private final StorageMatrixHelper storageMatrixHelper;

        StorageMatrixRunnable(StorageMatrixHelper storageMatrixHelper) {
            this.storageMatrixHelper = storageMatrixHelper;
            this.storageMatrixHelper.incrementThreadCounter();
        }

        @Override
        public void run() {
            try {
                this.storageMatrixHelper.computeMatrixCells();
            } catch (Exception exception) {
                logger.log(Level.SEVERE, "Failed to compute the matrix.", exception);
            } finally {
                this.storageMatrixHelper.decrementThreadCounter();
            }
        }
    }

    private static class StorageMatrixHelper {
        private final Matrix inputMatrix;
        private final StorageMatrix storageMatrix;
        private int rowIndex;
        private int columnIndex;
        private int threadCounter;

        StorageMatrixHelper(Matrix inputMatrix) {
            this.inputMatrix = inputMatrix;
            this.storageMatrix = new StorageMatrix(this.inputMatrix.getRowCount(), this.inputMatrix.getColumnCount());
            this.rowIndex = 0;
            this.columnIndex = 0;
            this.threadCounter = 0;
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

        synchronized StorageMatrix waitToFinish() throws InterruptedException {
            if (this.threadCounter > 0) {
                wait();
            }
            return this.storageMatrix;
        }

        void computeMatrixCells() {
            int localRowIndex = -1;
            int localColumnIndex = -1;
            do {
                synchronized (this.storageMatrix) {
                    if (this.rowIndex < this.storageMatrix.getRowCount()) {
                        localRowIndex = this.rowIndex;
                        if (this.columnIndex < this.storageMatrix.getColumnCount()) {
                            localColumnIndex = this.columnIndex;
                            this.columnIndex++;
                        } else {
                            this.columnIndex = 0; // reset the column index
                            localColumnIndex = this.columnIndex;
                            this.rowIndex++; // increment the row index
                        }
                    } else {
                        localRowIndex = -1;
                        localColumnIndex = -1;
                    }
                }
                if (localRowIndex >= 0 && localColumnIndex >= 0) {
                    float cellValue = this.inputMatrix.getValueAt(localRowIndex, localColumnIndex);
                    this.storageMatrix.setValueAt(localRowIndex, localColumnIndex, cellValue);
                }
            } while (localRowIndex >= 0 && localColumnIndex >= 0);
        }
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

        MahalanobisDistanceHelper(TrimmingStatisticsMatrix trimmingStatisticsMatrix, Matrix inverseMatrix, double cumulativeProbability) {
            MultiplyMatrix resultMatrix = new MultiplyMatrix(trimmingStatisticsMatrix, inverseMatrix);
            TransposeMatrix transposeMatrix = new TransposeMatrix(trimmingStatisticsMatrix);
            this.squaredMahalanobisMatrix = new MultiplyMatrix(resultMatrix, transposeMatrix);
            if (this.squaredMahalanobisMatrix.getRowCount() != trimmingStatisticsMatrix.getRowCount()) {
                throw new IllegalArgumentException("Wrong size");
            }
            if (!this.squaredMahalanobisMatrix.isSquare()) {
                throw new IllegalArgumentException("The Mahalanobis matrix is not a squared matrix.");
            }

            this.trimmingStatisticsMatrix = trimmingStatisticsMatrix;
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
            return this.validStatistics;
        }

        void computeDistances() {
            int index = -1;
            do {
                synchronized (this.squaredMahalanobisMatrix) {
                    if (this.matrixCounter < this.trimmingStatisticsMatrix.getRowCount()) {
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

                    if (index % 1000 == 0 && logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, ""); // add an empty line
                        logger.log(Level.FINE, "Mahalanobis distance computation: index: "+index+", total regions: "+this.trimmingStatisticsMatrix.getRowCount()+", distance: " +Math.sqrt(squareDistance)+", chi distribution: "+this.cumulativeProbability);
                    }
                }
            } while (index >= 0);
        }
    }
}
