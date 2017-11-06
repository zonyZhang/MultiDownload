package com.zony.multidownload.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;

import com.zony.multidownload.db.DbCoumnUtils.Column;
import com.zony.multidownload.domain.DownloadItem;
import com.zony.multidownload.utils.DownLoadConstant;
import com.zony.multidownload.utils.LogUtil;
import com.zony.multidownload.utils.SortUtil;

import java.util.ArrayList;
import java.util.List;

public class DownloadDao {

    private static final String TAG = "DownloadDao";

    private static DownloadDao instance;

    private ContentResolver mResolver;

    protected Context mContext;

    private DownloadDao(Context context) {
        mResolver = context.getContentResolver();
        mContext = context;
    }

    public synchronized static DownloadDao getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadDao(context);
        }

        return instance;
    }

    private void closeCursor(Cursor c) {
        if (c != null) {
            c.close();
        }
    }

    /**
     * 增加或更新下载数据库项
     *
     * @param item
     */
    public synchronized int insertDownloadItem(DownloadItem item) {
        int queryCount = -1;
        if (item == null) {
            LogUtil.w(TAG, "DownloadDao insertDownloadItem DownloadItem is null !");
            return queryCount;
        }
        try {
            item.setCreateTime(SystemClock.uptimeMillis());
            ContentValues values = DbCoumnUtils.downloadItem2ContentValues(item);
            String where = Column.COLUMN_ID + "=? and " + Column.COLUMN_NAME + "=?";
            String[] selectionArgs = new String[] {
                    item.getId() + "", item.getName()
            };

            Cursor c = mResolver.query(DownloadProvider.CONTENT_URI_DOWNLOAD, null, where,
                    selectionArgs, null);
            queryCount = c.getCount();
            if (queryCount > 0) {
                while (c.moveToNext()) {
                    DownloadItem itemQuery = DbCoumnUtils.cursor2DownloadItem(c);

                    // 已经下载完成的不再更新
                    if (itemQuery.getDownloadState() != DownLoadConstant.DownloadState.COMPLETE) {
                        LogUtil.i(TAG, "DownloadDao update");
                        mResolver.update(DownloadProvider.CONTENT_URI_DOWNLOAD, values, where,
                                selectionArgs);
                    }
                }
            } else {
                LogUtil.i(TAG, "DownloadDao insert");
                mResolver.insert(DownloadProvider.CONTENT_URI_DOWNLOAD, values);
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "DownloadDao insertDownloadItem exception: " + item.getName());
            e.printStackTrace();
        }
        LogUtil.i(TAG, "DownloadDao insertDownloadItem queryCount: " + queryCount);
        return queryCount;
    }

    /**
     * 获得所有下载信息
     */
    public synchronized List<DownloadItem> getAllDownloadItems() {
        Cursor c = null;
        List<DownloadItem> itemList = new ArrayList<>();
        try {
            c = mResolver.query(DownloadProvider.CONTENT_URI_DOWNLOAD, null, null, null,
                    Column.COLUMN_ID + " asc");
            while (c.moveToNext()) {
                DownloadItem item = DbCoumnUtils.cursor2DownloadItem(c);
                itemList.add(item);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "DownloadDao getAllDownloadItems error :" + e);
        } finally {
            closeCursor(c);
        }
        LogUtil.i(TAG, "DownloadDao getAllDownloadItems itemList =" + itemList);
        return itemList;
    }

    /**
     * 获取未成功的下载,名字升序
     *
     * @param
     * @author zony
     * @time 17-10-20 上午11:56
     */
    public synchronized List<DownloadItem> getDownloadingItemsByName() {
        List<DownloadItem> downloadItemList = getDownloadingItems(null);
        SortUtil.sortByName(downloadItemList, true);
        return downloadItemList;
    }

    /**
     * 获取未成功的下载,时间升序
     *
     * @param
     * @author zony
     * @time 17-10-20 上午11:56
     */
    public synchronized List<DownloadItem> getDownloadingItemsByCreatTime() {
        return getDownloadingItems(Column.COLUMN_CREATETIME + " asc");
    }

    private synchronized List<DownloadItem> getDownloadingItems(String sortOrder) {
        Cursor c = null;
        List<DownloadItem> itemList = new ArrayList<>();
        try {
            String selection = Column.COLUMN_DOWNLOADSTATE + "!=? ";
            String[] selectionArgs = new String[] {
                    DownLoadConstant.DownloadState.COMPLETE + ""
            };

            c = mResolver.query(DownloadProvider.CONTENT_URI_DOWNLOAD, null, selection,
                    selectionArgs, sortOrder);
            while (c.moveToNext()) {
                DownloadItem item = DbCoumnUtils.cursor2DownloadItem(c);
                itemList.add(item);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "DownloadDao getDownloadingItems error :" + e);
        } finally {
            closeCursor(c);
        }
        return itemList;
    }

    /**
     * 获取成功下载项
     *
     * @param
     * @author zony
     * @time 17-10-24 上午11:56
     */
    public synchronized List<DownloadItem> getDownloadedItems() {
        Cursor c = null;
        List<DownloadItem> itemList = new ArrayList<>();
        try {
            String selection = Column.COLUMN_DOWNLOADSTATE + "=? ";
            String[] selectionArgs = new String[] {
                    DownLoadConstant.DownloadState.COMPLETE + ""
            };

            c = mResolver.query(DownloadProvider.CONTENT_URI_DOWNLOAD, null, selection,
                    selectionArgs, Column.COLUMN_ID + " asc");
            while (c.moveToNext()) {
                DownloadItem item = DbCoumnUtils.cursor2DownloadItem(c);
                itemList.add(item);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "DownloadDao getDownloadedItems error :" + e);
        } finally {
            closeCursor(c);
        }
        return itemList;
    }

    /**
     * 更新数据库下载项数据
     *
     * @param item
     */
    public synchronized void updateDownloadItem(DownloadItem item) {
        if (item == null) {
            return;
        }
        try {
            String where;
            String[] selectionArgs;

            where = Column.COLUMN_ID + "=?";
            selectionArgs = new String[] {
                    item.getId() + ""
            };
            ContentValues values = DbCoumnUtils.downloadItem2ContentValues(item);
            mResolver.update(DownloadProvider.CONTENT_URI_DOWNLOAD, values, where, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 删除某数据库下载项
     *
     * @param item
     */
    public synchronized void deleteDownloadItem(DownloadItem item) {
        LogUtil.i(TAG, "DownloadDao deleteDownloadItem: " + item.getName());
        if (item == null) {
            return;
        }

        try {
            String where = Column.COLUMN_ID + "=?";
            String[] selectionArgs = new String[] {
                    item.getId() + ""
            };

            mResolver.delete(DownloadProvider.CONTENT_URI_DOWNLOAD, where, selectionArgs);
        } catch (Exception e) {
            LogUtil.w(TAG, "DownloadDao deleteDownloadItem Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 清空下载数据库所有数据
     */
    public synchronized void deleteAllDownloadItems() {
        try {
            mResolver.delete(DownloadProvider.CONTENT_URI_DOWNLOAD, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public synchronized DownloadItem getApkDownloadItemByPackageName(
    // String packageName) {
    //
    // String selection = Column.COLUMN_APP_PACKAGE_NAME + " = ?";
    // String[] selectionArgs = new String[] { packageName };
    // DownloadItem item = null;
    // Cursor c = null;
    // try {
    // c = mResolver.query(DownloadProvider.CONTENT_URI_DOWNLOAD, null,
    // selection, selectionArgs, null);
    // if (c.moveToFirst()) {
    // item = DbCoumnUtils.cursor2DownloadItem(c);
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // } finally {
    // if (c != null) {
    // c.close();
    // }
    // }
    // return item;
    // }
}
