package org.esa.snap.utils;

import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public abstract class AbstractArrayCellsParallelComputing extends AbstractParallelComputing {

    private final int cellCount;

    private int currentCellIndex;

    protected AbstractArrayCellsParallelComputing(int cellCount) {
        super();

        this.cellCount = cellCount;
        this.currentCellIndex = 0;
    }

    protected abstract void computeCell(int localIndex);

    @Override
    protected final void execute() {
        int index = -1;
        do {
            synchronized (this) {
                if (this.threadException != null) {
                    return;
                }
                if (this.currentCellIndex < this.cellCount) {
                    index = this.currentCellIndex;
                    this.currentCellIndex++;
                } else {
                    index = -1;
                }
            }
            if (index >= 0) {
                computeCell(index);
            }
        } while (index >= 0);
    }
}
