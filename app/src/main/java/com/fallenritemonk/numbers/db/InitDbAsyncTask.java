package com.fallenritemonk.numbers.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * Created by FallenRiteMonk on 9/22/15.
 */
public class InitDbAsyncTask extends AsyncTask<Context, Void, SQLiteDatabase> {
    Handler handler;

    public InitDbAsyncTask(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected SQLiteDatabase doInBackground(Context... params) {
        DatabaseHelper helper = DatabaseHelper.getInstance(params[0]);
        return helper.getWritableDatabase();
    }

    @Override
    protected void onPostExecute(SQLiteDatabase sqLiteDatabase) {
        super.onPostExecute(sqLiteDatabase);

        Message message = handler.obtainMessage(0, sqLiteDatabase);
        handler.sendMessage(message);
    }
}
