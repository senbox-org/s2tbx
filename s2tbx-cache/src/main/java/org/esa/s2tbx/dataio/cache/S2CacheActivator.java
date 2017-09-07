package org.esa.s2tbx.dataio.cache;

import org.esa.snap.runtime.Activator;
import org.esa.snap.runtime.Config;
import org.esa.snap.runtime.Engine;

import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by obarrile on 09/06/2016.
 */
public class S2CacheActivator implements Activator {

    @Override
    public void start() {
        final Preferences preferences = Config.instance("s2tbx").load().preferences();
        String cachePolicy = preferences.get(S2CacheUtils.SENTINEL_2_CACHE_MAX_TIME, null);
        if(cachePolicy == null) {
            cachePolicy = S2CacheUtils.SENTINEL_2_CACHE_OPTION_WEEK;
            preferences.put(S2CacheUtils.SENTINEL_2_CACHE_MAX_TIME, cachePolicy);
            try {
                preferences.flush();
            } catch (BackingStoreException e) {
                Logger logger = Engine.getInstance().getLogger();
                logger.severe(e.getMessage());
            }
        }

        if (cachePolicy.equals(S2CacheUtils.SENTINEL_2_CACHE_OPTION_NEVER_DELETE)) {
            return;
        }
        if (cachePolicy.equals(S2CacheUtils.SENTINEL_2_CACHE_OPTION_DAY)) {
            S2CacheUtils.delete1DayCache();
            return;
        }
        if (cachePolicy.equals(S2CacheUtils.SENTINEL_2_CACHE_OPTION_WEEK)) {
            S2CacheUtils.delete1WeekCache();
            return;
        }
        if (cachePolicy.equals(S2CacheUtils.SENTINEL_2_CACHE_OPTION_MONTH)) {
            S2CacheUtils.delete1MonthCache();
            return;
        }
        if (cachePolicy.equals(S2CacheUtils.SENTINEL_2_CACHE_OPTION_EACH_START_UP)) {
            S2CacheUtils.deleteCache();
            return;
        }
    }

    @Override
    public void stop() {
        final Preferences preferences = Config.instance("s2tbx").load().preferences();
        String cachePolicy = preferences.get(S2CacheUtils.SENTINEL_2_CACHE_MAX_TIME, null);
        if (cachePolicy == null) {
            return;
        }
        if (cachePolicy.equals(S2CacheUtils.SENTINEL_2_CACHE_OPTION_EACH_START_UP)) {
            S2CacheUtils.deleteCache();
        }
    }

}
