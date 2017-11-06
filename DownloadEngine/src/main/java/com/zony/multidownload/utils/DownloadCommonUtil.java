
package com.zony.multidownload.utils;

import android.text.TextUtils;
import android.util.SparseArray;

/**
 * 工具类
 *
 * @author zony
 * @time 2 Nov 2015 18:24:43
 */
public class DownloadCommonUtil {

    /**
     * 是否为纯数字
     *
     * @param str
     * @return
     * @author zony
     * @time 2014-9-5
     */
    public static boolean isPureNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        return str.matches("^[0-9]*$");
    }

    /**
     * long型byte值换算为 KB、MB、GB、TB、PB为单位的字符串
     *
     * @param fileS
     * @return
     * @author zony
     * @time 2014-9-25
     */
    public static String formatFileSize(long fileS) {
        String fileSizeString = "0B";
        if (fileS == 0) {
            return fileSizeString;
        }
        float dFileSize = (float) fileS;
        if (fileS < 1024f) {
            fileSizeString = String.format("%.2f", dFileSize) + "B";
        } else if (fileS < 1048576f) {
            fileSizeString = String.format("%.2f", dFileSize / 1024f) + "KB";
        } else if (fileS < 1073741824f) {
            fileSizeString = String.format("%.2f", dFileSize / 1048576f) + "MB";
        } else if (fileS < 1099511627776f) {
            fileSizeString = String.format("%.2f", dFileSize / 1073741824f) + "GB";
        } else if (fileS < 1.125899906842624E15) {
            fileSizeString = String.format("%.2f", dFileSize / 1099511627776f) + "TB";
        } else {
            fileSizeString = String.format("%.2f", dFileSize / 1.125899906842624E15) + "PB";
        }
        return fileSizeString;
    }

    /**
     * int型byte网速值换算为 KB、MB、GB为单位的字符串, 不到1MB时不显示小数点后两位
     *
     * @param speedSize
     * @return
     * @author zony
     * @time 2014-9-25
     */
    public static String formatSpeedSize(int speedSize) {
        String fileSizeString = "";
        if (speedSize < 1024) {
            fileSizeString = String.format("%d", speedSize) + "KB";
        } else if (speedSize < 1048576f) {
            fileSizeString = String.format("%.2f", speedSize / 1024f) + "MB";
        } else if (speedSize < 1073741824f) {
            fileSizeString = String.format("%.2f", speedSize / 1048576f) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 获取下载剩余时间
     * 00:05:03
     *
     * @return
     * @author zony
     * @time 2014-9-5
     */
    public static String getRemainTime(long remainSecond) {
        StringBuilder stringBuilder = new StringBuilder();
        long hours = remainSecond / 3600;
        long minutes = (remainSecond - hours * 3600) / 60;
        long seconds = remainSecond - hours * 3600 - minutes * 60;
        if (hours < 10) {
            stringBuilder.append("0").append(hours).append(":");
        } else {
            stringBuilder.append(hours).append(":");
        }

        if (minutes < 10) {
            stringBuilder.append("0").append(minutes).append(":");
        } else {
            stringBuilder.append(minutes).append(":");
        }

        if (seconds < 10) {
            stringBuilder.append("0").append(seconds);
        } else {
            stringBuilder.append(seconds);
        }
        return stringBuilder.toString();
    }

    /**
     * 判断SparseArray是否为空
     *
     * @param
     * @author zony
     * @time 17-6-15 下午2:48
     */
    public static boolean isSparseArrayEmpty(SparseArray sparseArray) {
        if (sparseArray != null && sparseArray.size() > 0) {
            return false;
        }
        return true;
    }
}
