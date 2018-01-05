package com.example.administrator.jymall;

import android.os.Bundle;
import com.example.administrator.jymall.common.TopActivity;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_about_us)
public class AboutusActivity extends TopActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("关于我们");
        progressDialog.hide();
    }
}
