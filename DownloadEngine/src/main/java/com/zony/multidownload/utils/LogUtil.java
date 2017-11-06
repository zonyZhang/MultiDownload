package com.zony.multidownload.utils;

import android.util.Log;

/**
 * @author zhanggz
 * @time 2014-3-31
 */
public class LogUtil {

    private static boolean enableDefaultLog = true;

    private static final int RETURN_NOLOG = 99;

    private static final String RETURN_NOLOG_STRING = "RETURN_NOLOG";

    private static final String LOG_NORMAL = "zony_";

    public static int d(String tag, String msg, Throwable tr) {
        return enableDefaultLog ? Log.d(tag, LOG_NORMAL + msg, tr) : RETURN_NOLOG;
    }

    public static int d(String tag, String msg) {
        return enableDefaultLog ? Log.d(tag, LOG_NORMAL + msg) : RETURN_NOLOG;
    }

    public static int e(String tag, String msg) {
        return enableDefaultLog ? Log.e(tag, LOG_NORMAL + msg) : RETURN_NOLOG;
    }

    public static int e(String tag, String msg, Throwable tr) {
        return enableDefaultLog ? Log.e(tag, LOG_NORMAL + msg, tr) : RETURN_NOLOG;
    }

    public static String getStackTraceString(Throwable tr) {
        return enableDefaultLog ? Log.getStackTraceString(tr) : RETURN_NOLOG_STRING;
    }

    public static int i(String tag, String msg, Throwable tr) {
        return enableDefaultLog ? Log.i(tag, LOG_NORMAL + msg, tr) : RETURN_NOLOG;
    }

    public static int i(String tag, String msg) {
        return enableDefaultLog ? Log.i(tag, LOG_NORMAL + msg) : RETURN_NOLOG;
    }

    public static boolean isLoggable(String tag, int level) {
        return enableDefaultLog ? Log.isLoggable(tag, level) : false;
    }

    public static int println(int priority, String tag, String msg) {
        return enableDefaultLog ? Log.println(priority, tag, msg) : RETURN_NOLOG;
    }

    public static int v(String tag, String msg, Throwable tr) {
        return enableDefaultLog ? Log.v(tag, LOG_NORMAL + msg, tr) : RETURN_NOLOG;
    }

    public static int v(String tag, String msg) {
        return enableDefaultLog ? Log.v(tag, LOG_NORMAL + msg) : RETURN_NOLOG;
    }

    public static int w(String tag, String msg) {
        return enableDefaultLog ? Log.w(tag, LOG_NORMAL + msg) : RETURN_NOLOG;
    }

    public static int w(String tag, Throwable tr) {
        return enableDefaultLog ? Log.w(tag, tr) : RETURN_NOLOG;
    }

    public static int w(String tag, String msg, Throwable tr) {
        return enableDefaultLog ? Log.w(tag, LOG_NORMAL + msg, tr) : RETURN_NOLOG;
    }
}
