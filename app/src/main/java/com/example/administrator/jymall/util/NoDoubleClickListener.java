package com.example.administrator.jymall.util;


import android.view.View;
import android.view.View.OnClickListener;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/12/13.
 */

public class NoDoubleClickListener implements OnClickListener {
        public static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                //onNoDoubleClick(v);
            }
        }
}
