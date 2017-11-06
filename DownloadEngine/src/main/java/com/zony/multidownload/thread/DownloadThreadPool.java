package com.zony.multidownload.thread;

import android.os.Build;
import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zony.multidownload.utils.DownLoadConstant.CORE_POOL_SIZE;
import static com.zony.multidownload.utils.DownLoadConstant.KEEP_ALIVE;
import static com.zony.multidownload.utils.DownLoadConstant.MAX_POOL_SIZE;

/**
 * 下载线程池
 *
 * @author zony
 * @time 17-5-26 下午4:57
 */
public class DownloadThreadPool {

    private ThreadPoolExecutor THREAD_POOL_EXECUTOR;

    private ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger();

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable, "download_task#" + mCount.getAndIncrement());
        }
    };

    private DownloadThreadPool() {
    }

    public static DownloadThreadPool getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final DownloadThreadPool instance = new DownloadThreadPool();
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        if (THREAD_POOL_EXECUTOR == null) {
            THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
                    CORE_POOL_SIZE, MAX_POOL_SIZE,
                    KEEP_ALIVE, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<Runnable>(),
                    sThreadFactory);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                /**
                 * 核心线程在allowCoreThreadTimeout被设置为true时会超时退出，默认情况下不会退出,
                 * 是否允许核心线程空闲退出，默认值为false。
                 */
                if (THREAD_POOL_EXECUTOR instanceof ThreadPoolExecutor)
                    THREAD_POOL_EXECUTOR.allowCoreThreadTimeOut(true);
            }
        }
        return THREAD_POOL_EXECUTOR;
    }
}
