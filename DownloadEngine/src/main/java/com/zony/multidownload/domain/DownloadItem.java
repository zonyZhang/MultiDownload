
package com.zony.multidownload.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.zony.multidownload.protocal.Request;
import com.zony.multidownload.protocal.Response;
import com.zony.multidownload.utils.DownLoadConstant;
import com.zony.multidownload.utils.LogUtil;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 下载信息（下载文件链接、大小、速度、剩余时间等） Created by zony on 17-5-25.
 */

public class DownloadItem extends BaseDownloadItem
        implements Request, Response, Parcelable, Serializable {
    private final String TAG = "DownloadItem";

    private boolean isSupportBreakpointResume = true;// 是否支持断点续传

    private int requestType;// 请求类型（get、post）

    private Map<String, String> specialHeaders = new HashMap<String, String>();// 头信息

    private String postContent;// post内容

    private int responseState = -1;// 请求返回状态

    private long totalSize;// 文件总大小

    private long downloadSize;// 已经下载文件大小

    private int downloadSpeed;// 下载速度

    private long remainTime; // 剩余时间

    private int respCode = -1;// 状态码

    private String mimeType;// 媒体类型

    private int downloadState = -1;// 下载状态

    public DownloadItem(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        // TODO Auto-generated method stub
        return id;
    }

    /**
     * 获取下载文件名，非空。 未设置时,如果httpUrl非空，则自动划分该地址最后一个“/”后字符串作为文件名
     *
     * @return
     */
    public String getName() {
        if (name == null) {
            if (url != null) {
                String[] splits = url.split("/");
                name = splits[splits.length - 1];
            } else {
                name = "";
            }
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 当用户未设置路径时，默认为DOWNLOAD_PATH
     *
     * @param
     * @author zony
     * @time 17-6-1 下午3:27
     */
    public String getSavePath() {
        if (TextUtils.isEmpty(savePath)) {
            savePath = DownLoadConstant.DOWNLOAD_PATH;
        }
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getFileSaveFullPath() {
        return new StringBuilder(getSavePath()).append(File.separator).append(name).toString();
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setSupportBreakpointResume(boolean supportBreakpointResume) {
        isSupportBreakpointResume = supportBreakpointResume;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setRequstType(int type) {
        requestType = type;
    }

    @Override
    public int getRequestType() {
        // TODO Auto-generated method stub
        return requestType;
    }

    @Override
    public String getUrl() {
        // TODO Auto-generated method stub
        return url;
    }

    public void setSpecialHeader(String key, String value) {
        if (key == null || value == null) {
            LogUtil.w(TAG, "addSpecialHeader: check key or value !");
            return;
        }
        specialHeaders.put(key, value);
    }

    @Override
    public Map<String, String> getSpecialHeader() {
        // TODO Auto-generated method stub
        return specialHeaders;
    }

    public void setIsSupportBreakpointResume(boolean isSupport) {
        isSupportBreakpointResume = isSupport;
    }

    public boolean isSupportBreakpointResume() {
        return isSupportBreakpointResume;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostContent() {
        return postContent;
    }

    @Override
    public void setResponseState(int state) {
        responseState = state;
    }

    @Override
    public int getResponseState() {
        return responseState;
    }

    public void setDownloadState(int state) {
        downloadState = state;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setTotalSize(long size) {
        totalSize = size;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setDownloadSize(long size) {
        downloadSize = size;
    }

    public long getDownLoadedSize() {
        return downloadSize;
    }

    /**
     * get download percent.
     *
     * @return present to 0 - 100 number.
     */
    public int getDownloadPercent() {
        if (totalSize == 0) {
            return 0;
        }
        float decimal = (float) (((double) downloadSize) / ((double) totalSize));
        return (int) (decimal * 100);
    }

    @Override
    public void setResponseCode(int code) {
        // TODO Auto-generated method stub
        respCode = code;
    }

    @Override
    public int getResponseCode() {
        // TODO Auto-generated method stub
        return respCode;
    }

    @Override
    public void setMimeType(String mime) {
        // TODO Auto-generated method stub
        mimeType = mime;
    }

    @Override
    public String getMimeType() {
        // TODO Auto-generated method stub
        return mimeType;
    }

    /**
     * @return the downloadSpeed
     */
    public int getDownloadSpeed() {
        return downloadSpeed;
    }

    /**
     * @param downloadSpeed the downloadSpeed to set
     */
    public void setDownloadSpeed(int downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    /**
     * @return the remainTime
     */
    public long getRemainTime() {
        return remainTime;
    }

    /**
     * @param remainTime the remainTime to set
     */
    public void setRemainTime(long remainTime) {
        this.remainTime = remainTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.savePath);
        dest.writeLong(this.createTime);
        dest.writeByte(this.isSupportBreakpointResume ? (byte) 1 : (byte) 0);
        dest.writeInt(this.requestType);
        dest.writeInt(this.specialHeaders.size());
        for (Map.Entry<String, String> entry : this.specialHeaders.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeString(this.postContent);
        dest.writeInt(this.responseState);
        dest.writeLong(this.totalSize);
        dest.writeLong(this.downloadSize);
        dest.writeInt(this.downloadSpeed);
        dest.writeLong(this.remainTime);
        dest.writeInt(this.respCode);
        dest.writeString(this.mimeType);
        dest.writeInt(this.downloadState);
    }

    protected DownloadItem(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.url = in.readString();
        this.savePath = in.readString();
        this.createTime = in.readLong();
        this.isSupportBreakpointResume = in.readByte() != 0;
        this.requestType = in.readInt();
        int specialHeadersSize = in.readInt();
        this.specialHeaders = new HashMap<String, String>(specialHeadersSize);
        for (int i = 0; i < specialHeadersSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.specialHeaders.put(key, value);
        }
        this.postContent = in.readString();
        this.responseState = in.readInt();
        this.totalSize = in.readLong();
        this.downloadSize = in.readLong();
        this.downloadSpeed = in.readInt();
        this.remainTime = in.readLong();
        this.respCode = in.readInt();
        this.mimeType = in.readString();
        this.downloadState = in.readInt();
    }

    public static final Creator<DownloadItem> CREATOR = new Creator<DownloadItem>() {
        @Override
        public DownloadItem createFromParcel(Parcel source) {
            return new DownloadItem(source);
        }

        @Override
        public DownloadItem[] newArray(int size) {
            return new DownloadItem[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DownloadItem)) {
            return false;
        }
        DownloadItem p = (DownloadItem) obj;
        if (this.name.equals(p.getName()) && this.id == p.getId()) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "DownloadItem{" + "id=" + id + ", name='" + name + '\'' + ", url='" + url + '\''
                + ", savePath='" + savePath + '\'' + ", createTime=" + createTime
                + ", isSupportBreakpointResume=" + isSupportBreakpointResume + ", requestType="
                + requestType + ", specialHeaders=" + specialHeaders.toString() + ", postContent='"
                + postContent + '\'' + ", responseState=" + responseState + ", totalSize="
                + totalSize + ", downloadSize=" + downloadSize + ", downloadSpeed=" + downloadSpeed
                + ", remainTime=" + remainTime + ", respCode=" + respCode + ", mimeType='"
                + mimeType + '\'' + ", downloadState=" + downloadState + '}';
    }
}
