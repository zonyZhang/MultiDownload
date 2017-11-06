
package com.zony.multidownload.db;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.zony.multidownload.db.DbCoumnUtils.Column;
import com.zony.multidownload.utils.DownLoadConstant;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadProvider extends ContentProvider {

    private static final int MATCH_DOWNLOAD = 1;

    // 这个是每个Provider的标识，在Manifest中使用
    public static final String AUTHORITY = DownLoadConstant.AUTHORITY;

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.zony.download";

    private static HashMap<String, String> dlMap;

    public static final Uri CONTENT_URI_DOWNLOAD = Uri.parse("content://" + AUTHORITY + "/"
            + DbCoumnUtils.TABLE);

    private static final UriMatcher sUriMatcher;

    private DownloadDBHelper dbHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, DbCoumnUtils.TABLE, MATCH_DOWNLOAD);

        // 保存所有表用到的字段
        dlMap = new HashMap<String, String>();
        dlMap.put(Column.COLUMN_ID, Column.COLUMN_ID);
        dlMap.put(Column.COLUMN_NAME, Column.COLUMN_NAME);
        dlMap.put(Column.COLUMN_URL, Column.COLUMN_URL);
        dlMap.put(Column.COLUMN_SAVEPATH, Column.COLUMN_SAVEPATH);
        dlMap.put(Column.COLUMN_CREATETIME, Column.COLUMN_CREATETIME);
        dlMap.put(Column.COLUMN_ISSUPPORTBREAKPOINTRESUME, Column.COLUMN_ISSUPPORTBREAKPOINTRESUME);
        dlMap.put(Column.COLUMN_REQUESTTYPE, Column.COLUMN_REQUESTTYPE);
        dlMap.put(Column.COLUMN_POSTCONTENT, Column.COLUMN_POSTCONTENT);
        dlMap.put(Column.COLUMN_RESPONSESTATE, Column.COLUMN_RESPONSESTATE);
        dlMap.put(Column.COLUMN_TOTALSIZE, Column.COLUMN_TOTALSIZE);
        dlMap.put(Column.COLUMN_DOWNLOADSIZE, Column.COLUMN_DOWNLOADSIZE);
        dlMap.put(Column.COLUMN_DOWNLOADSPEED, Column.COLUMN_DOWNLOADSPEED);
        dlMap.put(Column.COLUMN_REMAINTIME, Column.COLUMN_REMAINTIME);
        dlMap.put(Column.COLUMN_RESPCODE, Column.COLUMN_RESPCODE);
        dlMap.put(Column.COLUMN_MIMETYPE, Column.COLUMN_MIMETYPE);
        dlMap.put(Column.COLUMN_DOWNLOADSTATE, Column.COLUMN_DOWNLOADSTATE);
    }

    @Override
    public boolean onCreate() {
        if (dbHelper == null) {
            dbHelper = DownloadDBHelper.getInstance(getContext());
        }
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return CONTENT_TYPE;

    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection,
                                     String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        // 匹配对应的表
        switch (sUriMatcher.match(uri)) {
            case MATCH_DOWNLOAD:
                qb.setTables(DbCoumnUtils.TABLE);
                qb.setProjectionMap(dlMap);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {

        if (values == null) {
            throw new IllegalArgumentException("values is null");
        }
        String tableName = "";
        switch (sUriMatcher.match(uri)) {
            case MATCH_DOWNLOAD:
                tableName = DbCoumnUtils.TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(tableName, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        String tableName = "";
        switch (sUriMatcher.match(uri)) {
            case MATCH_DOWNLOAD:
                tableName = DbCoumnUtils.TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        count = db.delete(tableName, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection,
                                   String[] selectionArgs) {
        if (values == null) {
            throw new IllegalArgumentException("values is null");
        }
        int count = 0;
        String tableName = "";
        switch (sUriMatcher.match(uri)) {
            case MATCH_DOWNLOAD:
                tableName = DbCoumnUtils.TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        count = db.update(tableName, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public synchronized ContentProviderResult[] applyBatch(
            ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();// 开始事务
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();// 设置事务标记为successful
            return results;
        } finally {
            db.endTransaction();// 结束事务
        }
    }
}
