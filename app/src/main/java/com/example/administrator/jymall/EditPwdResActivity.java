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
