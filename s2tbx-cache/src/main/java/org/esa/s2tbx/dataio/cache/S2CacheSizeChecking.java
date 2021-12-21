package org.esa.s2tbx.dataio.cache;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class S2CacheSizeChecking {

    public static final S2CacheSizeChecking INSTANCE = new S2CacheSizeChecking();
    private static final Logger logger = Logger.getLogger(S2CacheSizeChecking.class.getName());
    private boolean checkingEnable;
    private long limitSizeCache;
    private final ScheduledExecutorService executor;
    private static final double GB_TO_BYTES = 100000000;

    private S2CacheSizeChecking() {
        executor = Executors.newSingleThreadScheduledExecutor();
    }


    public static S2CacheSizeChecking getInstance() {
        return INSTANCE;
    }

    /**
     * set the parameters of the cache size checking
     */
    public synchronized void setParameters(boolean checkingEnable, double limitSizeCache) {
        this.checkingEnable = checkingEnable;
        this.limitSizeCache = (long)(limitSizeCache * GB_TO_BYTES);
    }

    /**
     * Method check the S2cache size over time. The method is executed in thread.
     * Each period time, the cache size is checked.
     * The x% of the oldest files should be deleted in case of cache oversize.
     *
     * @param releasedSpacePercent The space percentage of the oldest files should be deleted
     * @param period               The period between two size checking in minute
     */
    public synchronized void launchCacheSizeChecking(double releasedSpacePercent, int period) {
        Runnable runnable = () -> {
            if (checkingEnable) {  // would be better if the executor is stopped when the parameter changes
                long currentCacheSize = S2CacheUtils.getCacheSize();
                //compute the limit of cache size should be keep after the checking
                long circularLimitSizeCache = (long)(limitSizeCache * (1 - releasedSpacePercent));
                while (currentCacheSize > circularLimitSizeCache) {
                    File oldestFolder = S2CacheUtils.getOldestFolder();
                    long folderSize = S2CacheUtils.getFilesSize(oldestFolder);
                    if (oldestFolder != null) {
                        S2CacheUtils.deleteFiles(oldestFolder); // nb. this might fail
                    }
                    currentCacheSize = Math.max(currentCacheSize - folderSize, 0) ;
                }
            }
        };
        try {
            executor.scheduleAtFixedRate(runnable, 0, period, TimeUnit.MINUTES);
        } catch (RejectedExecutionException e) {
            logger.log(Level.WARNING, "Failed to start observer of S2 Data Cache: ", e);
        }

    }

    public void complete() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.log(Level.SEVERE, "Failed to stop observer of S2 Data Cache.");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to stop observer of S2 Data Cache: ", e);
        }
    }

}
