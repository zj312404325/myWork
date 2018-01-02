package com.example.administrator.jymall;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.administrator.jymall.common.TopActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_intel_market)
public class IntelMarketActivity extends TopActivity {
    @ViewInject(R.id.supplybtn)
    private Button supplybtn;

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
}
