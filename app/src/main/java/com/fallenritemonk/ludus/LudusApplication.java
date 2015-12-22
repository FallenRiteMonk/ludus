package com.fallenritemonk.ludus;

import android.app.Application;

import com.fallenritemonk.ludus.services.AnalyticsTrackers;

/**
 * Created by FallenRiteMonk on 9/24/15.
 */
public class LudusApplication extends Application {
    private final static String APP_VERSION = BuildConfig.VERSION_NAME;

    @Override
    public void onCreate() {
        super.onCreate();

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().getTracker();
    }

    public String getAppVersion() {
        return APP_VERSION;
    }
}
