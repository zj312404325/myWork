package com.example.administrator.jymall.common;


import com.example.administrator.jymall.util.TokenUtil;

import com.example.administrator.jymall.LoginActivity;

import android.content.Intent;
import android.os.Bundle;

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
