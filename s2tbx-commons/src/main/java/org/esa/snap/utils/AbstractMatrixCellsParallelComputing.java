package org.esa.snap.utils;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public abstract class AbstractMatrixCellsParallelComputing extends AbstractParallelComputing {

    private final int columnCount;
    private final int rowCount;

    private int currentRowIndex;
    private int currentColumnIndex;

    protected AbstractMatrixCellsParallelComputing(int columnCount, int rowCount) {
        super();

        this.columnCount = columnCount;
        this.rowCount = rowCount;

        this.currentRowIndex = 0;
        this.currentColumnIndex = 0;
    }

    protected abstract void runTile(int localRowIndex, int localColumnIndex) throws IOException, IllegalAccessException, InterruptedException;

    @Override
    protected final void execute() throws Exception {
        int localRowIndex = -1;
        int localColumnIndex = -1;
        do {
            localRowIndex = -1;
            localColumnIndex = -1;
            synchronized (this) {
                if (this.threadException != null) {
                    return;
                }
                if (this.currentRowIndex < this.rowCount) {
                    if (this.currentColumnIndex < this.columnCount) {
                        localColumnIndex = this.currentColumnIndex;
                        localRowIndex = this.currentRowIndex;
                    } else {
                        this.currentColumnIndex = 0; // reset the column index
                        localColumnIndex = this.currentColumnIndex;

                        this.currentRowIndex++; // increment the row index
                        if (this.currentRowIndex < this.rowCount) {
                            localRowIndex = this.currentRowIndex;
                        }
                    }
                    this.currentColumnIndex++;
                }
            }
            if (localRowIndex >= 0 && localColumnIndex >= 0) {
                runTile(localRowIndex, localColumnIndex);
            }
        } while (localRowIndex >= 0 && localColumnIndex >= 0);
    }
}
