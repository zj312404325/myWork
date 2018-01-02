package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.jymall.common.BaseActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.DensityUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_register2)
public class Register2Activity extends BaseActivity {


    @ViewInject(R.id.submitbtn)
    private Button submitbtn;

    private int utype;

    @ViewInject(R.id.tab1)
    private RelativeLayout tab1;
    @ViewInject(R.id.tab2)
    private RelativeLayout tab2;

    @ViewInject(R.id.tab_txt1)
    private TextView tab_txt1;
    @ViewInject(R.id.tab_txt2)
    private TextView tab_txt2;

    @ViewInject(R.id.tab_line1)
    private LinearLayout tab_line1;
    @ViewInject(R.id.tab_line2)
    private LinearLayout tab_line2;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        progressDialog.hide();
        Intent i = this.getIntent();
        utype = i.getIntExtra("utype", 0);

        RelativeLayout.LayoutParams lp1 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(8));
        lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        RelativeLayout.LayoutParams lp2 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(1));
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if(utype == 0 ){
            tab_line1.setBackground(CommonUtil.getDrawable(R.drawable.line_select));
            tab_line1.setLayoutParams(lp1);
            tab_line2.setBackgroundColor(0xFFb5b6b9);
            tab_line2.setLayoutParams(lp2);


        }else {
            tab_line2.setBackground(CommonUtil.getDrawable(R.drawable.line_select));
            tab_line2.setLayoutParams(lp1);
            tab_line1.setBackgroundColor(0xFFb5b6b9);
            tab_line1.setLayoutParams(lp2);

        }
    }

    @Event(R.id.submitbtn)
    private void submitClick(View v){
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }


}
