package org.esa.s2tbx.dataio.cache;

import org.esa.snap.core.util.SystemUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by obarrile on 09/06/2016.
 */
public class S2CacheUtils {

    public final static String SENTINEL_2_CACHE_MAX_TIME = "s2tbx.dataio.maxTime";
    public final static String SENTINEL_2_CACHE_MAX_SIZE_OPTION = "s2tbx.dataio.maxSizeActive";
    public final static String SENTINEL_2_CACHE_MAX_SIZE = "s2tbx.dataio.maxSize";
    public final static boolean SENTINEL_2_CACHE_MAX_SIZE_OPTION_DEFAULT = false;
    public final static double SENTINEL_2_CACHE_MAX_SIZE_DEFAULT = 20.0;

    public final static long ONE_DAY_MILLISECONDS = 24 * 60 * 60 * 1000;
    public final static long ONE_WEEK_MILLISECONDS = 7 * 24 * 60 * 60 * 1000;
    public final static long ONE_MONTH_MILLISECONDS = 30 * 24 * 60 * 60 * 1000;

    public final static String SENTINEL_2_CACHE_OPTION_DAY = "One day";
    public final static String SENTINEL_2_CACHE_OPTION_WEEK = "One week";
    public final static String SENTINEL_2_CACHE_OPTION_MONTH = "One month";
    public final static String SENTINEL_2_CACHE_OPTION_NEVER_DELETE = "Do not delete automatically";
    public final static String SENTINEL_2_CACHE_OPTION_EACH_START_UP = "Delete cache at each start up";

    /**
     * Method for obtaining the cache folder.
     *
     * @return cache File or null if an exception occurs
     */
    public static File getSentinel2CacheFolder() {
        try {
            File s2CacheFolder = new File(SystemUtils.getCacheDir(), "s2tbx");
            return s2CacheFolder;
        } catch (Exception e) {
            return null;
        }
    }

    public static void deleteFiles(final File file) {
        if (file.isDirectory()) {
            for (String aChild : file.list()) {
                deleteFiles(new File(file, aChild));
            }
        }
        try {
            file.delete();
        } catch (Exception e) {
            // do not report anything
        }
    }

    public static void deleteFiles(final ArrayList<File> files) {
        if (files == null) return;
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            if (file == null) continue;
            deleteFiles(file);
        }
    }

    public static void deleteCache() {
        try {
            deleteFiles(getSentinel2CacheFolder());
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Method for obtaining the modification time of a file
     *
     * @param file
     * @return OL if it is not possible to compute, in other case time in milliseconds
     */
    public static long getModificationTime(File file) {
        final long time;
        try {
            time = file.lastModified();
        } catch (Exception e) {
            return 0L;
        }
        return time;
    }

    public static long getCacheModificationTime() {
        try {
            return getModificationTime(getSentinel2CacheFolder());
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * Method for computing the size of a file or folder
     *
     * @param file
     * @return OL if it is not possible to compute, in other case size in bytes
     */
    public static long getFilesSize(File file) {
        if (file == null) return 0L;
        if (file.isFile()) return file.length();
        if (!file.isDirectory()) return 0L;

        File[] subFiles = file.listFiles();
        if (subFiles == null) return 0L;

        long size = 0L;
        for (File subFile : subFiles) {
            if (subFile == null) continue;
            if (subFile.isFile())
                size += subFile.length();
            else
                size += getFilesSize(subFile);
        }

        return size;
    }

    public static long getCacheSize() {
        return getFilesSize(getSentinel2CacheFolder());
    }

    /**
     * Method for obtaining the largest product folder in s2tbx cache folder.
     * The structure of the cache should be: {s2tbxCacheFolder}/{productLevel}/{version}/{products}
     *
     * @return OL if it is not possible to compute, in other case time1-time2
     */
    public static File getLargestFolder() {

        File s2CacheFolder = getSentinel2CacheFolder();
        if (s2CacheFolder == null || !s2CacheFolder.isDirectory()) return null;

        File[] levelFolders = s2CacheFolder.listFiles();
        if (levelFolders == null) return null;

        File largestFile = null;
        long size = 0;
        long aux;
        for (File levelFolder : levelFolders) {
            if (levelFolder == null || !levelFolder.isDirectory()) continue;


            File[] versions = levelFolder.listFiles();
            if (versions == null) continue;
            for (File version : versions) {
                if (version == null || !version.isDirectory()) continue;
                File[] products = version.listFiles();
                if (products == null) continue;
                for (File product : products) {
                    if (product == null) continue;
                    aux = getFilesSize(product);
                    if (aux > size) {
                        size = aux;
                        largestFile = product;
                    }
                }
            }
        }

        return largestFile;
    }


    /**
     * Method for obtaining the files larger than a given size.
     *
     * @param megaBytes: the minimum size of the folder to be returned
     * @return null if cache is empty or an error occurs, in other case an ArrayList of files
     */
    public static ArrayList<File> getLargestFolders(long megaBytes) {

        long bytes = megaBytes * 1024 * 1024;
        File s2CacheFolder = getSentinel2CacheFolder();
        if (s2CacheFolder == null || !s2CacheFolder.isDirectory()) return null;

        File[] levelFolders = s2CacheFolder.listFiles();
        if (levelFolders == null) return null;

        ArrayList<File> largestFile = new ArrayList<>();
        for (File levelFolder : levelFolders) {
            if (levelFolder == null || !levelFolder.isDirectory()) continue;


            File[] versions = levelFolder.listFiles();
            if (versions == null) continue;
            for (File version : versions) {
                if (version == null || !version.isDirectory()) continue;
                File[] products = version.listFiles();
                if (products == null) continue;
                for (File product : products) {
                    if (product == null) continue;
                    if (getFilesSize(product) > bytes) {
                        largestFile.add(product);
                    }
                }
            }
        }

        return largestFile;
    }

    /**
     * Method for obtaining the oldest product in cache
     *
     * @return null if cache is empty or an error occurs, in other case the oldest product file in cache
     */
    public static File getOldestFolder() {
        File s2CacheFolder = getSentinel2CacheFolder();
        if (s2CacheFolder == null || !s2CacheFolder.isDirectory()) return null;

        File[] levelFolders = s2CacheFolder.listFiles();
        if (levelFolders == null) return null;

        File oldestFile = null;
        long lastModifiedTime = Long.MAX_VALUE;
        long aux;
        for (File levelFolder : levelFolders) {
            if (levelFolder == null || !levelFolder.isDirectory()) continue;

            File[] versions = levelFolder.listFiles();
            if (versions == null) continue;
            for (File version : versions) {
                if (version == null || !version.isDirectory()) continue;
                File[] products = version.listFiles();
                if (products == null) continue;
                for (File product : products) {
                    if (product == null) continue;
                    aux = getModificationTime(product);
                    if (aux < lastModifiedTime) {
                        lastModifiedTime = aux;
                        oldestFile = product;
                    }
                }
            }
        }

        return oldestFile;
    }

    /**
     * Method for obtaining the products in cache that were cached more than x milliseconds ago.
     *
     * @param millisecondsSinceModification: the minimum time passed since the product folder was cached
     * @return null if cache is empty or an error occurs, in other case an arraylist containing the oldest products
     */
    public static ArrayList<File> getOlderFolders(long millisecondsSinceModification) {
        long currentTime = System.currentTimeMillis();

        File s2CacheFolder = getSentinel2CacheFolder();
        if (s2CacheFolder == null || !s2CacheFolder.isDirectory()) return null;

        File[] levelFolders = s2CacheFolder.listFiles();
        if (levelFolders == null) return null;

        ArrayList<File> oldestFiles = new ArrayList<>();

        for (File levelFolder : levelFolders) {
            if (levelFolder == null || !levelFolder.isDirectory()) continue;

            File[] versions = levelFolder.listFiles();
            if (versions == null) continue;
            for (File version : versions) {
                if (version == null || !version.isDirectory()) continue;
                File[] products = version.listFiles();
                if (products == null) continue;
                for (File product : products) {
                    if (product == null) continue;
                    if ((currentTime - getModificationTime(product)) > millisecondsSinceModification) {
                        oldestFiles.add(product);
                    }
                }
            }
        }
        return oldestFiles;
    }

    public static void delete1DayCache() {
        deleteFiles(getOlderFolders(ONE_DAY_MILLISECONDS));
    }

    public static void delete1WeekCache() {
        deleteFiles(getOlderFolders(ONE_WEEK_MILLISECONDS));
    }

    public static void delete1MonthCache() {
        deleteFiles(getOlderFolders(ONE_MONTH_MILLISECONDS));
    }

    public static int getNumberOfProductsInCache() {

        int count = 0;
        File s2CacheFolder = getSentinel2CacheFolder();
        if (s2CacheFolder == null || !s2CacheFolder.isDirectory()) return count;

        File[] levelFolders = s2CacheFolder.listFiles();
        if (levelFolders == null) return count;

        ArrayList<File> oldestFiles = new ArrayList<>();

        for (File levelFolder : levelFolders) {
            if (levelFolder == null || !levelFolder.isDirectory()) continue;

            File[] versions = levelFolder.listFiles();
            if (versions == null) continue;
            for (File version : versions) {
                if (version == null || !version.isDirectory()) continue;
                File[] products = version.listFiles();
                if (products == null) continue;
                for (File product : products) {
                    if (product == null) continue;
                    count++;
                }
            }
        }
        return count;
    }


}
