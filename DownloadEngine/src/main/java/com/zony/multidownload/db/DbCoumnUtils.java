
package com.zony.multidownload.db;

import android.content.ContentProviderOperation.Builder;
import android.content.ContentValues;
import android.database.Cursor;

import com.zony.multidownload.domain.DownloadItem;

public class DbCoumnUtils {

    public static final String DB_NAME = "download.db";

    public static final String TABLE = "downloadtable";

    public interface Column {
        String COLUMN_ID = "id";
        String COLUMN_NAME = "name";
        String COLUMN_URL = "url";
        String COLUMN_SAVEPATH = "savePath";
        String COLUMN_CREATETIME = "createTime";
        String COLUMN_ISSUPPORTBREAKPOINTRESUME = "isSupportBreakpointResume";
        String COLUMN_REQUESTTYPE = "requestType";
        String COLUMN_POSTCONTENT = "postContent";
        String COLUMN_RESPONSESTATE = "responseState";
        String COLUMN_TOTALSIZE = "totalSize";
        String COLUMN_DOWNLOADSIZE = "downloadSize";
        String COLUMN_DOWNLOADSPEED = "downloadSpeed";
        String COLUMN_REMAINTIME = "remainTime";
        String COLUMN_RESPCODE = "respCode";
        String COLUMN_MIMETYPE = "mimeType";
        String COLUMN_DOWNLOADSTATE = "downloadState";
    }

    public static DownloadItem cursor2DownloadItem(Cursor c) {
        DownloadItem item = new DownloadItem(
                c.getInt(c.getColumnIndex(Column.COLUMN_ID)),
                c.getString(c.getColumnIndex(Column.COLUMN_URL)));
        item.setName(c.getString(c.getColumnIndex(Column.COLUMN_NAME)));
        item.setSavePath(c.getString(c.getColumnIndex(Column.COLUMN_SAVEPATH)));
        item.setCreateTime(c.getLong(c.getColumnIndex(Column.COLUMN_CREATETIME)));
        int supportBreakpointResume = c.getInt(c.getColumnIndex(Column.COLUMN_ISSUPPORTBREAKPOINTRESUME));
        item.setIsSupportBreakpointResume(supportBreakpointResume == 0);// 0表示支持
        item.setRequestType(c.getInt(c.getColumnIndex(Column.COLUMN_REQUESTTYPE)));
        item.setPostContent(c.getString(c.getColumnIndex(Column.COLUMN_POSTCONTENT)));
        item.setResponseState(c.getInt(c.getColumnIndex(Column.COLUMN_RESPONSESTATE)));
        item.setTotalSize(c.getLong(c.getColumnIndex(Column.COLUMN_TOTALSIZE)));
        item.setDownloadSize(c.getLong(c.getColumnIndex(Column.COLUMN_DOWNLOADSIZE)));
        item.setDownloadSpeed(c.getInt(c.getColumnIndex(Column.COLUMN_DOWNLOADSPEED)));
        item.setRemainTime(c.getLong(c.getColumnIndex(Column.COLUMN_REMAINTIME)));
        item.setResponseCode(c.getInt(c.getColumnIndex(Column.COLUMN_RESPCODE)));
        item.setMimeType(c.getString(c.getColumnIndex(Column.COLUMN_MIMETYPE)));
        item.setDownloadState(c.getInt(c.getColumnIndex(Column.COLUMN_DOWNLOADSTATE)));
        return item;
    }

    public static void downloadItem2Builder(Builder builder, DownloadItem item) {
        builder.withValue(Column.COLUMN_ID, item.getId());
        builder.withValue(Column.COLUMN_NAME, item.getName());
        builder.withValue(Column.COLUMN_URL, item.getUrl());
        builder.withValue(Column.COLUMN_SAVEPATH, item.getSavePath());
        builder.withValue(Column.COLUMN_CREATETIME, item.getCreateTime());
        builder.withValue(Column.COLUMN_ISSUPPORTBREAKPOINTRESUME, item.isSupportBreakpointResume() ? 0 : 1);
        builder.withValue(Column.COLUMN_REQUESTTYPE, item.getRequestType());
        builder.withValue(Column.COLUMN_POSTCONTENT, item.getPostContent());
        builder.withValue(Column.COLUMN_RESPONSESTATE, item.getResponseState());
        builder.withValue(Column.COLUMN_TOTALSIZE, item.getTotalSize());
        builder.withValue(Column.COLUMN_DOWNLOADSIZE, item.getDownLoadedSize());
        builder.withValue(Column.COLUMN_DOWNLOADSPEED, item.getDownloadSpeed());
        builder.withValue(Column.COLUMN_REMAINTIME, item.getRemainTime());
        builder.withValue(Column.COLUMN_RESPCODE, item.getResponseCode());
        builder.withValue(Column.COLUMN_MIMETYPE, item.getMimeType());
        builder.withValue(Column.COLUMN_DOWNLOADSTATE, item.getDownloadState());
    }

    public static ContentValues downloadItem2ContentValues(DownloadItem item) {
        ContentValues cv = new ContentValues();
        cv.put(Column.COLUMN_ID, item.getId());
        cv.put(Column.COLUMN_NAME, item.getName());
        cv.put(Column.COLUMN_URL, item.getUrl());
        cv.put(Column.COLUMN_SAVEPATH, item.getSavePath());
        cv.put(Column.COLUMN_CREATETIME, item.getCreateTime());
        cv.put(Column.COLUMN_ISSUPPORTBREAKPOINTRESUME, item.isSupportBreakpointResume() ? 0 : 1);
        cv.put(Column.COLUMN_REQUESTTYPE, item.getRequestType());
        cv.put(Column.COLUMN_POSTCONTENT, item.getPostContent());
        cv.put(Column.COLUMN_RESPONSESTATE, item.getResponseState());
        cv.put(Column.COLUMN_TOTALSIZE, item.getTotalSize());
        cv.put(Column.COLUMN_DOWNLOADSIZE, item.getDownLoadedSize());
        cv.put(Column.COLUMN_DOWNLOADSPEED, item.getDownloadSpeed());
        cv.put(Column.COLUMN_REMAINTIME, item.getRemainTime());
        cv.put(Column.COLUMN_RESPCODE, item.getResponseCode());
        cv.put(Column.COLUMN_MIMETYPE, item.getMimeType());
        cv.put(Column.COLUMN_DOWNLOADSTATE, item.getDownloadState());
        return cv;
    }
}
