package com.example.administrator.jymall;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.jymall.common.TopActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_intel_market)
public class IntelMarketActivity extends TopActivity {
    @ViewInject(R.id.supplybtn)
    private Button supplybtn;

    @ViewInject(R.id.iv_part1)
    private ImageView iv_part1;
    @ViewInject(R.id.iv_part2)
    private ImageView iv_part2;
    @ViewInject(R.id.iv_part3)
    private ImageView iv_part3;

    @ViewInject(R.id.ll_part1)
    private LinearLayout ll_part1;
    @ViewInject(R.id.ll_part2)
    private LinearLayout ll_part2;
    @ViewInject(R.id.ll_part3)
    private LinearLayout ll_part3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("智能超市");
        progressDialog.hide();
    }

    @Event(value={R.id.supplybtn})
    private void supplybtnClick(View v){
        Intent i = new Intent(getApplicationContext(), SupplyMarketActivity.class);
        startActivity(i);
    }

    @Event(value={R.id.iv_part1})
    private void part1Click(View v){
        if(ll_part1.getVisibility()==View.VISIBLE){
            ll_part1.setVisibility(View.GONE);
            iv_part1.setBackgroundResource(R.drawable.icon_arrow_up);
        }
        else{
            ll_part1.setVisibility(View.VISIBLE);
            iv_part1.setBackgroundResource(R.drawable.icon_arrow_down);
        }
    }

    @Event(value={R.id.iv_part2})
    private void part2Click(View v){
        if(ll_part2.getVisibility()==View.VISIBLE){
            ll_part2.setVisibility(View.GONE);
            iv_part2.setBackgroundResource(R.drawable.icon_arrow_up);
        }
        else{
            ll_part2.setVisibility(View.VISIBLE);
            iv_part2.setBackgroundResource(R.drawable.icon_arrow_down);
        }
    }

    @Event(value={R.id.iv_part3})
    private void part3Click(View v){
        if(ll_part3.getVisibility()==View.VISIBLE){
            ll_part3.setVisibility(View.GONE);
            iv_part3.setBackgroundResource(R.drawable.icon_arrow_up);
        }
        else{
            ll_part3.setVisibility(View.VISIBLE);
            iv_part3.setBackgroundResource(R.drawable.icon_arrow_down);
        }
    }
}
