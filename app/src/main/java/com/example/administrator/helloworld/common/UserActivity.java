package com.example.administrator.helloworld.common;


import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.helloworld.common.MyApplication;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.TokenUtil;
import com.example.administrator.helloworld.view.MyProgressDialog;
import com.example.administrator.helloworld.view.XListView;

import com.example.administrator.helloworld.LoginActivity;
import com.example.administrator.helloworld.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserActivity extends BaseActivity {
	public boolean isLogin = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String sk=super.getServerKey();
		if(sk.equals("") || getUser().equals("")){
			//CommonUtil.alter("你还未登录，请登录。");
			MyApplication.backActivity = this.getClass();
			startActivity(new Intent(getApplicationContext(),LoginActivity.class));
			isLogin = false;
		}
		else{
			if(TokenUtil.getUserid(sk).equals("0")){
				//CommonUtil.alter("你还未登录，请登录。");
				MyApplication.backActivity = this.getClass();
				startActivity(new Intent(getApplicationContext(),LoginActivity.class));
				isLogin = false;
			}
		}
		
		super.onCreate(savedInstanceState);
		
		
	}
	

	
	
	
}
