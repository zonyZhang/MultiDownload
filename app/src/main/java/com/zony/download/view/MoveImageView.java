package com.zony.download.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.widget.ImageView;

/**
 * 自定义MoveImageView仅仅是增加了一个set方法方便属性动画 update时调用
 *
 * @param
 * @author zony
 * @time 17-11-3 下午3:51
 */
@SuppressLint("AppCompatCustomView")
public class MoveImageView extends ImageView {

    public MoveImageView(Context context) {
        super(context);
    }

    public void setMPointF(PointF pointF) {
        setX(pointF.x);
        setY(pointF.y);
    }
}
