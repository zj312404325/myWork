package com.example.administrator.jymall;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

@ContentView(R.layout.activity_setting)
public class SettingActivity extends TopActivity {

	@ViewInject(R.id.btn_exit)
	private Button btn_exit;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("设置");
		progressDialog.hide();
	}
	
	@Event(R.id.btn_exit)
	private void exit(View v){
		super.clearServerKey();
		CommonUtil.alter("成功退出！！！");
		startActivity(new Intent(getApplicationContext(),LoginActivity.class));
	}
	
}
