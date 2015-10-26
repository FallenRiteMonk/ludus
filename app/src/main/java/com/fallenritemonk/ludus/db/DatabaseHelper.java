package com.fallenritemonk.ludus.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by FallenRiteMonk on 9/22/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String TAG = "DB_HELPER";

    private static DatabaseHelper sInstance;

    // Database info
    private static final String DATABASE_NAME = "ludus.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_STATES = "states";

    // States Table Columns
    private static final String KEY_STATES_ID = "id"; //used as sequence number
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

    public void clearDB() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_STATES, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all states");
        } finally {
            db.endTransaction();
        }
    }

    public void saveState(int order, String fieldArray) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_STATES_ID, order);
            values.put(KEY_STATES_FIELD_ARRAY, fieldArray);

            db.insertOrThrow(TABLE_STATES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to save state");
        } finally {
            db.endTransaction();
        }
    }

    public int getLastStateOrder() {
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_STATES);
        int lastID = -1;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToLast()) {
                lastID = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_STATES_ID)));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get max id from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return lastID;
    }

    public String getLastState() {
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_STATES);
        String fieldArray = "";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToLast()) {
                fieldArray = cursor.getString(cursor.getColumnIndex(KEY_STATES_FIELD_ARRAY));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get max id from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return fieldArray;
    }

    public boolean undo() {
        int order = getLastStateOrder();
        if (order > 0) {
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete(TABLE_STATES, KEY_STATES_ID + "=" + order, null);
                db.setTransactionSuccessful();
                return true;
            } catch (Exception e) {
                Log.d(TAG, "Error while trying to undo");
            } finally {
                db.endTransaction();
            }
        }
        return false;
    }
}
