
package com.zony.multidownload.protocal;

/**
 * Created by zony on 17-5-25.
 */

public interface Response {

    public void setResponseState(int state);

    public int getResponseState();

    public void setResponseCode(int code);

    public int getResponseCode();

    public void setMimeType(String mime);

    public String getMimeType();

}
