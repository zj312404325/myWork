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

@ContentView(R.layout.activity_mymatch_index)
public class MyMatchIndexActivity extends TopActivity {
    @ViewInject(R.id.rl_fastMatch)
    private RelativeLayout rl_fastMatch;

    @ViewInject(R.id.rl_orderMatch)
    private RelativeLayout rl_orderMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("我要配管理");
        progressDialog.hide();
    }

    @Event(value=R.id.rl_fastMatch,type=View.OnTouchListener.class)
    private boolean myScoreTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),FastMatchListActivity.class));
            return false;
        }
        return true;
    }

    @Event(value=R.id.rl_orderMatch,type=View.OnTouchListener.class)
    private boolean myExchangeTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),OrderMatchListActivity.class));
            return false;
        }
        return true;
    }

    @Event(value=R.id.rl_fabFastMatch,type=View.OnTouchListener.class)
    private boolean fabFastTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),FabFastMatchListActivity.class));
            return false;
        }
        return true;
    }

    @Event(value=R.id.rl_fabOrderMatch,type=View.OnTouchListener.class)
    private boolean fabOrderTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            startActivity(new Intent(getApplicationContext(),FabOrderMatchListActivity.class));
            return false;
        }
        return true;
    }
}
