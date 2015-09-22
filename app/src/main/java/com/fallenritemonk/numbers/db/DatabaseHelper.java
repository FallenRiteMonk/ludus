package com.fallenritemonk.numbers.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by FallenRiteMonk on 9/22/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper sInstance;

    // Database info
    private static final String DATABASE_NAME = "ludus.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_STATES = "states";

    // States Table Columns
    private static final String KEY_STATES_ID = "id"; //used as sequence number
    private static final String KEY_STATES_LAST_ACTION = "last_action";
    private static final String KEY_STATES_FIELD_ARRAY = "field_array";

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STATES_TABLE = "CREATE TABLE " + TABLE_STATES +
                "(" +
                KEY_STATES_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_STATES_LAST_ACTION + " TEXT" +
                KEY_STATES_FIELD_ARRAY + " TEXT" +
                ")";

        db.execSQL(CREATE_STATES_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATES);
            onCreate(db);
        }
    }
}
