package com.fallenritemonk.ludus;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by FallenRiteMonk on 9/24/15.
 */
public class LudusApplication extends Application {
    private final static String APP_VERSION = BuildConfig.VERSION_NAME;
    private final static boolean DEBUG_MODE = BuildConfig.DEBUG;

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getAnalyticsTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            if (DEBUG_MODE) {
                mTracker = analytics.newTracker(getString(R.string.analytics_debug_api_key));
            } else {
                mTracker = analytics.newTracker(getString(R.string.analytics_api_key));
            }

            mTracker.enableAutoActivityTracking(true);
            mTracker.enableExceptionReporting(true);
        }

        return mTracker;
    }

    public String getAppVersion() {
        return APP_VERSION;
    }
}
