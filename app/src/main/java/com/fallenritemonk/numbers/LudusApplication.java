package com.fallenritemonk.numbers;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;

/**
 * Created by FallenRiteMonk on 9/24/15.
 */
public class LudusApplication extends Application {
    private static String APP_ID = BuildConfig.APPLICATION_ID;
    private static String APP_VERSION = BuildConfig.VERSION_NAME;
    private static boolean DEBUG_MODE = BuildConfig.DEBUG;
    private static String CLIENT_ID;
    private static String CLIENT_LANG;
    private static String CLIENT_LOCATION;

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
                mTracker = analytics.newTracker(R.string.analytics_debug_api_key);
            } else {
                mTracker = analytics.newTracker(R.string.analytics_api_key);
            }

            CLIENT_LANG = Locale.getDefault().getDisplayLanguage();
            CLIENT_LOCATION = Locale.getDefault().getCountry();
            CLIENT_ID = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            mTracker.setAppId(APP_ID);
            mTracker.setAppVersion(APP_VERSION);
            mTracker.enableAutoActivityTracking(true);
            mTracker.enableExceptionReporting(true);
            mTracker.setClientId(CLIENT_ID);
            mTracker.setLanguage(CLIENT_LANG);
            mTracker.setLocation(CLIENT_LOCATION);
        }
        return mTracker;
    }
}
