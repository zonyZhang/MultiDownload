
package com.zony.multidownload.utils;

import android.os.Environment;

import java.io.File;

/**
 * 文件工具类
 *
 * @author zony
 * @time 2 Nov 2015 18:24:43
 */
public class DownloadFileUtil {
    private static final String TAG = "DownloadFileUtil";

    // 下载目录
    private static final String DOWNLOAD_DIR = "ZONYDOWNLOAD";

    /**
     * 下载位置
     *
     * @param
     * @author zony
     * @time 17-5-31 下午3:32
     */
    public static String getDownLoadPath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path = path + File.separator + DOWNLOAD_DIR;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        LogUtil.i(TAG, "DownloadFileUtil getDownLoadPath: " + path);
        return path;
    }

    /**
     * 校验文件路径是否有效
     *
     * @param fileFullPath
     * @return
     * @author zony
     * @time 2014-9-5
     */
    public static boolean isFileFullPathValid(String fileFullPath) {
        if (fileFullPath == null || fileFullPath.length() == 0) {
            return false;
        }

        int i = fileFullPath.lastIndexOf(File.separator);
        if (i == 0 || i == -1) {
            return false;
        }

        if (i + 1 == fileFullPath.length()) {
            return false;
        }

        File f = new File(fileFullPath);
        if (f.isDirectory()) {
            return false;
        }

        String parentPath = fileFullPath.substring(0, i);
        File file = new File(parentPath);
        if (!file.isDirectory()) {
            return false;
        }

        return true;
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName test.mp4
     * @return mp4
     * @author zony
     * @time 2014-8-20
     */
    public static String getExtensionName(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int index = fileName.lastIndexOf('.');
            if ((index > -1) && (index < (fileName.length() - 1))) {
                return fileName.substring(index + 1);
            }
        }
        return fileName;
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param fileName test.mp4
     * @return test
     * @author zony
     * @time 2014-8-20
     */
    public static String getFileNameNoEx(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int index = fileName.lastIndexOf('.');
            if ((index > -1) && (index < (fileName.length()))) {
                return fileName.substring(0, index);
            }
        }
        return fileName;
    }

    /**
     * 获取文件名
     *
     * @param filePath /sdcard/test.mp4
     * @return test.mp4
     * @author zony
     * @time 2014-8-20
     */
    public static String getFileName(String filePath) {
        if ((filePath != null) && (filePath.length() > 0)) {
            int index = filePath.lastIndexOf('/');
            if ((index > -1) && (index < (filePath.length()))) {
                return filePath.substring(index).replace("/", "");
            }
        }
        return filePath;
    }

    /**
     * 删除文件夹
     *
     * @param
     * @author zony
     * @time 17-10-17 下午3:54
     */
    public static void deleteDir(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!file.exists()) {
                    LogUtil.i(TAG, "DownloadFileUtil deleteDir !file.exists(): " + file.getPath());
                    return;
                }

                if (file.isFile()) {
                    LogUtil.i(TAG, "DownloadFileUtil deleteDir file.delete: " + file.getPath());
                    file.delete();
                    return;
                }
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i]);
                }
                file.delete();
            }
        }).start();
    }
}
