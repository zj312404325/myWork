package com.example.administrator.helloworld;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.helloworld.common.TopActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

@ContentView(R.layout.activity_edit_pwd_res)
public class EditPwdResActivity extends TopActivity {

	@ViewInject(R.id.loginout)
	private Button loginout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("修改密码成功");
		super.top_back.setVisibility(View.GONE);
		
		progressDialog.hide();
	}
	
	@Event(R.id.loginout)
	private void loginoutclick(View v){
		startActivity(new Intent(getApplicationContext(),LoginActivity.class));
	}

}
