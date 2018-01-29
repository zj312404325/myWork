package com.example.administrator.jymall;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.administrator.jymall.common.TopActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

/**
 * Created by Administrator on 2018-01-25.
 */

@ContentView(R.layout.activity_commit_ordermatch_ok)
public class CommitOrderMatchOkActivity extends TopActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("提交定制配");
        progressDialog.hide();
    }

    @Event(value=R.id.btn_submit)
    private void myMatchClick(View v){
        Intent i = new Intent(getApplicationContext(), OrderMatchListActivity.class);
        startActivity(i);
    }
}