package com.fallenritemonk.ludus;

import android.app.Application;

/**
 * Created by FallenRiteMonk on 9/24/15.
 */
public class LudusApplication extends Application {
    private final static String APP_VERSION = BuildConfig.VERSION_NAME;

    public String getAppVersion() {
        return APP_VERSION;
    }
}
