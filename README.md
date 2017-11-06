>>### ***功能说明：***
多任务断点续传下载,目前并行下载数量为1个，
如需并行多个，可在DownLoadConstant.java中修改CORE_POOL_SIZE即可


# 集成说明：

一、主工程添加DownloadEngine module(compile project(':DownloadEngine'))

二、主工程AndroidManifest.xml中添加如下代码：


          <!-- download start -->
          <service
              android:name="com.zony.multidownload.manager.DownloadManager"
              android:process="com.zony.multidownload.dl" />

          <provider
              android:name="com.zony.multidownload.db.DownloadProvider"
              android:authorities="xxx.provider"
              android:multiprocess="true" />
          <!-- download end -->

<font color=#FF0000 size=4>注意：以上代码中xxx请替换为自己的包名并在DownLoadConstant 中修改AUTHORITY的值</font>


# 使用相关说明：

## 一、单个文件下载

### 1、开始下载

        DownloadUtil.startDownload(this, downloadItem, new DownloadListObserver() {
            @Override
            public void onDownloadListObserver(
                final List<DownloadItem> downloadItemList) {

            }

            @Override
            public void delDownloadFileSuc(boolean isDelAll) {
                LogUtil.i(TAG, "MainActivity delDownloadFileSuc");
            }
        });

### 2、暂停下载

        DownloadUtil.pauseDownload(this, getDownloadItem());

### 3、取消下载

        DownloadUtil.deleteDownload(this, getDownloadItem());

## 二、单个文件下载

### 1、开始下载
直接调用DownloadUtil.startDownload(mContext, downloadItem);

下载监听则需要在下载界面new binder,可参考DownloadListLocalFragment.java类中，如下代码：

        mDownloadBind = new DownloadBind(getActivity(), new DownloadListObserver() {
            @Override
            public void onDownloadListObserver(final List<DownloadItem> list) {
                if (mDownloadListLocalingFragment != null) {
                    mDownloadListLocalingFragment.updateUi(list);
                }
            }

            @Override
            public void delDownloadFileSuc(boolean isDelAll) {
                LogUtil.i(TAG, "DownloadListLocalFragment delDownloadFileSuc: " + isDelAll);

                if (mTablayout.getSelectedTabPosition() == 0) {
                    if (mDownloadListLocalingFragment != null) {
                        mDownloadListLocalingFragment.updateUi(
                                DownloadDao.getInstance(getActivity()).getDownloadingItemsByName());
                    }
                } else {
                    if (mDownloadListLocalCompleteFragment != null) {
                        mDownloadListLocalCompleteFragment.updateUi();
                    }
                }
            }
        });
        mDownloadBind.bind();

<font color=#FF0000 size=4>注意：在绑定界面销毁时调用 unBind()函数</font>

### 2、暂停下载

        DownloadUtil.pauseDownload(this, downloadItem());

### 3、取消下载

        DownloadUtil.deleteDownload(this, downloadItem());

### 4、开始全部下载

        DownloadUtil.startAllDownload(this);

### 5、暂停全部下载

        DownloadUtil.pauseAllDownload(this);

### 6、取消全部下载

        DownloadUtil.deleteDownload(this);

# 代码修改相关说明：

1、DownLoadConstant.java 修改下载相关配置

2、查看下载总耗时可过滤“uTime”

3、全部log可过滤zony_