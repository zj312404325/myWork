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

@ContentView(R.layout.activity_myscore_index)
public class MyScoreIndexActivity extends TopActivity {
    @ViewInject(R.id.rl_myScore)
    private RelativeLayout rl_myScore;

    @ViewInject(R.id.rl_myExchange)
    private RelativeLayout rl_myExchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("我的积分");
        progressDialog.hide();
    }

    @Event(value=R.id.rl_myScore,type=View.OnTouchListener.class)
    private boolean myScoreTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),MyScoreActivity.class));
            return false;
        }
        return true;
    }

    @Event(value=R.id.rl_myExchange,type=View.OnTouchListener.class)
    private boolean myExchangeTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),MyExchangeActivity.class));
            return false;
        }
        return true;
    }
}
