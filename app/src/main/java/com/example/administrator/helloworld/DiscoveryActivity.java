package com.example.administrator.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.administrator.helloworld.common.TopActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_discovery)
public class DiscoveryActivity extends TopActivity {
    @ViewInject(R.id.rl_mallScore)
    private RelativeLayout rl_mallScore;

    @ViewInject(R.id.rl_serviceCenter)
    private RelativeLayout rl_serviceCenter;

    @ViewInject(R.id.rl_intelMarket)
    private RelativeLayout rl_intelMarket;

    @ViewInject(R.id.rl_aboutus)
    private RelativeLayout rl_aboutus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("发现");
        progressDialog.hide();
    }

    @Event(value=R.id.rl_mallScore,type=View.OnTouchListener.class)
    private boolean mallScoreTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),AddressActivity.class));
            return false;
        }
        return true;
    }

    @Event(value=R.id.rl_serviceCenter,type=View.OnTouchListener.class)
    private boolean serviceCenterTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),InvoiceActivity.class));
            return false;
        }
        return true;
    }

    @Event(value=R.id.rl_intelMarket,type=View.OnTouchListener.class)
    private boolean intelMarketTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),BankActivity.class));
            return false;
        }
        return true;
    }

    @Event(value=R.id.rl_aboutus,type=View.OnTouchListener.class)
    private boolean aboutusTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),AboutusActivity.class));
            return false;
        }
        return true;
    }
}
