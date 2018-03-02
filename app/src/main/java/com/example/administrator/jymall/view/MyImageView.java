package com.example.administrator.jymall.view;

/**
 * Created by Administrator on 2018-02-28.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * 重写ImageView，避免引用已回收的bitmap异常
 *
 * @author zwn
 *
 */
public class MyImageView extends AppCompatImageView {

    public MyImageView (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            System.out.println("MyImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
        }
    }

}
