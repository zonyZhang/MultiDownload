
package com.zony.multidownload.manager;

import com.zony.multidownload.protocal.Controller;

/**
 * Created by zony on 17-5-25.
 */

public class ContorllerImpl implements Controller {

    private boolean isStop = false;

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        isStop = true;
    }

    @Override
    public boolean isStoped() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }
}
