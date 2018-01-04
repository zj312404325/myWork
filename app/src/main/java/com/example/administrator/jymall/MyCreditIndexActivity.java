package com.example.administrator.jymall;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.administrator.jymall.common.TopActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_credit_index)
public class MyCreditIndexActivity extends TopActivity {
    @ViewInject(R.id.rl_creditState)
    private RelativeLayout rl_creditState;

    @ViewInject(R.id.rl_creditStream)
    private RelativeLayout rl_creditStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("信用支付");
        progressDialog.hide();
    }

    @Event(value=R.id.rl_creditState,type=View.OnTouchListener.class)
    private boolean creditStateTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),MyCreditActivity.class));
            return false;
        }
        return true;
    }

    @Event(value=R.id.rl_creditStream,type=View.OnTouchListener.class)
    private boolean creditStreamTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),CreditStreamActivity.class));
            return false;
        }
        return true;
    }
}