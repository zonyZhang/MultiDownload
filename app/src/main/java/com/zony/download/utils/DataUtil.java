package com.zony.download.utils;

import com.zony.multidownload.domain.DownloadItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zony on 17-6-5.
 */

public class DataUtil {

    public static final String downloadUrls[] = {
            "http://192.168.31.55/videos/%E4%B8%A5%E8%89%BA%E4%B8%B9-%E4%B8%89%E5%AF%B8%E5%A4%A9%E5%A0%82.avi",
            "http://123.125.46.174/imtt.dd.qq.com/16891/08BF56522F97BA0B72C9D70505D5509A.apk?mkey=589aaaf19a6aec9a&f=6d17&c=0&fsname=com.tencent.tmgp.sgame_1.17.1.11_17011101.apk&csr=2097&_track_d99957f7=22e0dbb4-86fd-419a-b0de-d1709f0e4c41&p=.apk",
            "http://111.202.99.14/imtt.dd.qq.com/16891/E00D69D73077FC37744E7856331D071A.apk?mkey=589ae6ca9a6aec9a&f=3480&c=0&fsname=com.jingdong.app.mall_5.7.0_42153.apk&csr=2097&_track_d99957f7=d7bc3318-c50f-4a46-8752-536a408bbae0&p=.apk",
            "http://123.125.110.15/imtt.dd.qq.com/16891/148B053CFB8F8242D4F1A0A09431C5D3.apk?mkey=58815af29a6aec9a&f=d287&c=0&fsname=com.tencent.qqlive_5.4.0.11652_11652.apk&csr=2097&_track_d99957f7=ac201979-cc9c-4eda-8a1a-7d2f29856096&p=.apk",
            "http://img.yingyonghui.com/apk/16457/com.rovio.angrybirdsspace.ads.1332528395706.apk",
            "http://img.yingyonghui.com/apk/15951/com.galapagossoft.trialx2_winter.1328012793227.apk",
            "http://125.39.134.47/r/a.gdown.baidu.com/data/wisegame/5d1b508cd8872f3f/wuxiafushengji_22.apk?from=a1101",
            "http://125.39.134.47/r/a.gdown.baidu.com/data/wisegame/40a38c03b56ae428/dagongwuyouwang_1.apk?from=a1101",
            "http://125.39.134.47/r/a.gdown.baidu.com/data/wisegame/512e31c60857f3a4/kutitiku_1.apk?from=a1101",
            "http://125.39.134.47/r/a.gdown.baidu.com/data/wisegame/5c625f0f7616eccc/zhonghuaren_117.apk?from=a1101",
            "http://125.39.134.47/r/a.gdown.baidu.com/data/wisegame/512e31c60857f3a4/kutitiku_1.apk?from=a1101",
            "http://125.39.134.47/r/a.gdown.baidu.com/data/wisegame/b666ef49a82232c9/maopushipin_8.apk?from=a1101",
            "http://125.39.134.47/r/a.gdown.baidu.com/data/wisegame/9133c923a2f6c2d1/haotushipin_9.apk?from=a1101",
            "http://125.39.134.47/r/a.gdown.baidu.com/data/wisegame/d62f123612f42502/gushixiaozhen_636.apk?from=a1101",
            "http://fast.yingyonghui.com/9239e4ee7fbcc24d9de246fc261a5e2a/59f92e90/apk/5229658/6ac1d045f458d08f6b5cd83b15d6d2ff",
            "http://ws.yingyonghui.com/cfaa92a5200e44e6a976f1090050d2c6/59f926a7/apk/5133200/f0249c38df60cb789ed5914655889825",
            "http://cdn1.down.apk.gfan.com/asdf/Pfiles/2012/3/26/181157_0502c0c3-f9d1-460b-ba1d-a3bad959b1fa.apk",
            "http://static.nduoa.com/apk/258/258681/com.gameloft.android.GAND.GloftAsp6.asphalt6.apk",
            "http://cdn1.down.apk.gfan.com/asdf/Pfiles/2011/12/5/100522_b73bb8d2-2c92-4399-89c7-07a9238392be.apk",
    };

    public static List<DownloadItem> getDownloadItemList() {
        List<DownloadItem> sparseArray = new ArrayList<>();
        for (int i = 1; i < downloadUrls.length; i++) {
            DownloadItem downloadItem = new DownloadItem(i, downloadUrls[i]);
            downloadItem.setIsSupportBreakpointResume(true);
            downloadItem.setName("multidownload_breakpoint_@.apk".replace("@", String.valueOf(i)));
            sparseArray.add(downloadItem);
        }
        return sparseArray;
    }

    public static DownloadItem getDownloadItem() {
        int id = 18;
        DownloadItem downloadItem = new DownloadItem(id, downloadUrls[id]);
        downloadItem.setIsSupportBreakpointResume(true);
        downloadItem.setName("multidownload_breakpoint_@.apk".replace("@", String.valueOf(id)));
        return downloadItem;
    }
}
