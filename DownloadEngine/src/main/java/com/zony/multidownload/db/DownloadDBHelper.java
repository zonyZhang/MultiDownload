
package com.zony.multidownload.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.zony.multidownload.db.DbCoumnUtils.Column;

public class DownloadDBHelper extends SQLiteOpenHelper {

    private static DownloadDBHelper downloadDBHelper = null;

    private static final int DOWNLOAD_DB_VERSION = 1;

    private DownloadDBHelper(Context context) {
        super(context,DbCoumnUtils.DB_NAME, null, DOWNLOAD_DB_VERSION);
    }

    public synchronized static DownloadDBHelper getInstance(Context context) {
        if (downloadDBHelper == null) {
            downloadDBHelper = new DownloadDBHelper(context);
        }
        return downloadDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDownloadTable(db);
    }

    private void createDownloadTable(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(DbCoumnUtils.TABLE).append("(");
        sql.append(Column.COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(Column.COLUMN_NAME).append(" VARCHAR, ");
        sql.append(Column.COLUMN_URL).append(" VARCHAR, ");
        sql.append(Column.COLUMN_SAVEPATH).append(" VARCHAR, ");
        sql.append(Column.COLUMN_CREATETIME).append(" INTEGER, ");
        sql.append(Column.COLUMN_ISSUPPORTBREAKPOINTRESUME).append(" INTEGER, ");
        sql.append(Column.COLUMN_REQUESTTYPE).append(" INTEGER, ");
        sql.append(Column.COLUMN_POSTCONTENT).append(" VARCHAR, ");
        sql.append(Column.COLUMN_RESPONSESTATE).append(" INTEGER, ");
        sql.append(Column.COLUMN_TOTALSIZE).append(" INTEGER, ");
        sql.append(Column.COLUMN_DOWNLOADSIZE).append(" INTEGER, ");
        sql.append(Column.COLUMN_DOWNLOADSPEED).append(" INTEGER, ");
        sql.append(Column.COLUMN_REMAINTIME).append(" INTEGER, ");
        sql.append(Column.COLUMN_RESPCODE).append(" INTEGER, ");
        sql.append(Column.COLUMN_MIMETYPE).append(" VARCHAR, ");
        sql.append(Column.COLUMN_DOWNLOADSTATE).append(" INTEGER");
        sql.append(")");
        db.execSQL(sql.toString());
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    protected void closeDB(SQLiteDatabase db) {
        if (db != null) {
            db.close();
        }
    }

    public void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
}
