
package com.zony.multidownload.protocal;

import java.util.Map;

/**
 * Created by zony on 17-5-25.
 */

public interface Request {
    public interface Type {
        int GET = 1;

        int POST = 2;
    }

    public int getId();

    public int getRequestType();

    public String getUrl();

    public Map<String, String> getSpecialHeader();
}
