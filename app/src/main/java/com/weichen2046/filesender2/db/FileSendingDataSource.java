package com.weichen2046.filesender2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * Created by chenwei on 2017/1/29.
 */

public class FileSendingDataSource {

    private static final String TAG = "FileSendingDataSource";

    public static final int INVALID_DB_ID = -1;

    private SQLiteDatabase mDb;
    private SQLiteOpenHelper mDbHelper;

    public FileSendingDataSource(Context context) {
        mDbHelper = new FileSender2SQLiteOpenHelper(context);
    }

    public void open() throws SQLException {
        if (mDb == null) {
            mDb = mDbHelper.getWritableDatabase();
        } else {
            Log.w(TAG, "Database already opened");
        }
    }

    public void close() {
        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }

    public long add(String uuid, Uri uri, String host, int port) {
        return add(uuid, uri.toString(), host, port);
    }

    public long add(String uuid, String uri, String host, int port) {
        if (!isDbOpened()) {
            return INVALID_DB_ID;
        }
        ContentValues values = new ContentValues();
        values.put(FileSender2SQLiteOpenHelper.FileSendingTable.COLUMN_UUID, uuid);
        values.put(FileSender2SQLiteOpenHelper.FileSendingTable.COLUMN_URI, uri);
        values.put(FileSender2SQLiteOpenHelper.FileSendingTable.COLUMN_HOST, host);
        values.put(FileSender2SQLiteOpenHelper.FileSendingTable.COLUMN_PORT, port);
        values.put(FileSender2SQLiteOpenHelper.FileSendingTable.COLUMN_CREATETIME,
                System.currentTimeMillis());
        return mDb.insert(FileSender2SQLiteOpenHelper.FileSendingTable.TABLE_NAME, null, values);
    }

    public FileSendingObj retrieve(long id) {
        if (!isDbOpened()) {
            return null;
        }
        Cursor cursor = mDb.query(FileSender2SQLiteOpenHelper.FileSendingTable.TABLE_NAME, null,
                "_id=?", new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            FileSendingObj obj = cursorToFileSendingObj(cursor);
            cursor.close();
            return obj;
        }
        return null;
    }

    public int delete(long id) {
        if (!isDbOpened()) {
            return -1;
        }
        return mDb.delete(FileSender2SQLiteOpenHelper.FileSendingTable.TABLE_NAME, "_id=?",
                new String[]{ String.valueOf(id) });
    }

    public int delete(FileSendingObj fsObj) {
        if (fsObj == null) {
            return -1;
        }
        return delete(fsObj.id);
    }

    private boolean isDbOpened() {
        return mDb != null;
    }

    private FileSendingObj cursorToFileSendingObj(Cursor cursor) {
        FileSendingObj fsObj = new FileSendingObj();
        fsObj.id = cursor.getLong(
                cursor.getColumnIndex(FileSender2SQLiteOpenHelper.FileSendingTable._ID));
        fsObj.uuid = cursor.getString(
                cursor.getColumnIndex(FileSender2SQLiteOpenHelper.FileSendingTable.COLUMN_UUID));
        fsObj.uri = cursor.getString(
                cursor.getColumnIndex(FileSender2SQLiteOpenHelper.FileSendingTable.COLUMN_URI));
        fsObj.host = cursor.getString(
                cursor.getColumnIndex(FileSender2SQLiteOpenHelper.FileSendingTable.COLUMN_HOST));
        fsObj.port = cursor.getInt(
                cursor.getColumnIndex(FileSender2SQLiteOpenHelper.FileSendingTable.COLUMN_PORT));
        fsObj.createtimestamp = cursor.getLong(
                cursor.getColumnIndex(FileSender2SQLiteOpenHelper.FileSendingTable.COLUMN_CREATETIME));
        return fsObj;
    }
}
