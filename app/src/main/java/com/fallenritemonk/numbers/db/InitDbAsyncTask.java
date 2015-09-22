package com.fallenritemonk.numbers.db;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by FallenRiteMonk on 9/22/15.
 */
public class InitDbAsyncTask extends AsyncTask<Context, Void, Void> {

    @Override
    protected Void doInBackground(Context... params) {
        DatabaseHelper helper = DatabaseHelper.getInstance(params[0]);
        helper.getWritableDatabase();
        return null;
    }
}
