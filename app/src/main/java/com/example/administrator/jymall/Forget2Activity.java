package com.example.administrator.jymall;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.jymall.common.BaseActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

@ContentView(R.layout.activity_forget2)
public class Forget2Activity extends BaseActivity {

    @ViewInject(R.id.submitbtn)
    private Button submitbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        progressDialog.hide();
    }

    @Event(R.id.submitbtn)
    private void submitClick(View v){
        super.clearServerKey();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }
}

