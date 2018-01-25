package com.example.administrator.jymall;

import android.os.Bundle;
import com.example.administrator.jymall.common.TopActivity;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * Created by Administrator on 2018-01-25.
 */

@ContentView(R.layout.activity_about_us)
public class AppraiseOkActivity extends TopActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("评价完成");
        progressDialog.hide();
    }
}