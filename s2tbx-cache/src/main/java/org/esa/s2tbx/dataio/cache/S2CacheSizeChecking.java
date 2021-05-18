package org.esa.s2tbx.dataio.cache;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.esa.snap.core.util.ThreadExecutor;
import org.esa.snap.core.util.ThreadRunnable;

public class S2CacheSizeChecking {

    public static final S2CacheSizeChecking INSTANCE = new S2CacheSizeChecking();
    private static final Logger logger = Logger.getLogger(S2CacheSizeChecking.class.getName());
    private boolean checkingEnable;
    private double limitSizeCache;
    private ThreadExecutor executor;

    private S2CacheSizeChecking(){
        //nothing to init
    }
    
    public static S2CacheSizeChecking getInstance() {
        return INSTANCE;
    }

    /**
     * set the parameters of the cache size checking
     *
     */
    public synchronized void setParameters(boolean checkingEnable,double limitSizeCache ) {
        this.checkingEnable = checkingEnable;
        this.limitSizeCache = limitSizeCache;
    }

    /**
     * Method check the S2cache size over time. The method is executed in thread.
     * Each period time, the cache size is checked.
     * The x% of the oldest files should be deleted in case of cache oversize.
     * @param releasedSpacePurcent The space purcentage of the oldest files should be deleted
     * @param period The period between two size checking in minute
     */
    public synchronized void launchCacheSizeChecking(double releasedSpacePurcent, int period) {
        executor = new ThreadExecutor();
            try {
                ThreadRunnable runnable = new ThreadRunnable() {
                    @Override
                    public void process() throws Exception {
                        double byteToGigaByte = 0.000000001;
                        while(!Thread.currentThread().isInterrupted()){
                            if(checkingEnable){
                                double currentCacheSize = S2CacheUtils.getCacheSize()*byteToGigaByte;
                                //compute the limit of cache size should be keep after the checking
                                double circularLimitSizeCache = limitSizeCache * (1-releasedSpacePurcent);
                                if(currentCacheSize>limitSizeCache){
                                    while(currentCacheSize>circularLimitSizeCache)
                                    {
                                        File oldestFile = S2CacheUtils.getOldestFolder();
                                        S2CacheUtils.deleteFile(oldestFile);
                                        currentCacheSize = S2CacheUtils.getCacheSize() * byteToGigaByte;
                                    }
                                }
                            }
                            Thread.sleep((long)period*20000);
                        }
                    }
                };
                executor.execute(runnable);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Failed to initialize S2Cache options.",ex);
            }
    }

    public void complete() {
        try {
            executor.complete();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to close the sizeCacheChecking: ",e);
        }
    }

}