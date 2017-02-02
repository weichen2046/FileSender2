package com.weichen2046.filesender2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by chenwei on 2017/1/29.
 */

public class FileSender2SQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "filesender2.db";
    private static final int DATABASE_VERSION = 1;

    public FileSender2SQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        FileSendingTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        FileSendingTable.onUpgrade(db, oldVersion, newVersion);
    }

    public static class FileSendingTable implements BaseColumns {
        public static final String TABLE_NAME           = "filesending";

        public static final String COLUMN_CREATETIME    = "createtime";
        public static final String COLUMN_URI           = "uri";
        public static final String COLUMN_UUID          = "uuid";
        public static final String COLUMN_HOST          = "host";
        public static final String COLUMN_PORT          = "port";

        private static final String SQL_CREATE_DATABASE = "create table "
                + TABLE_NAME
                + "("
                + FileSendingTable._ID + " integer primary key autoincrement, "
                + FileSendingTable.COLUMN_UUID + " text not null,"
                + FileSendingTable.COLUMN_URI + " text not null,"
                + FileSendingTable.COLUMN_HOST + " text not null,"
                + FileSendingTable.COLUMN_PORT + " integer not null,"
                + FileSendingTable.COLUMN_CREATETIME + " integer not null"
                + ");";

        private static void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_DATABASE);
        }

        private static void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        }
    }
}
