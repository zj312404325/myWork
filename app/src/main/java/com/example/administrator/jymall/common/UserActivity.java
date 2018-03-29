package com.example.administrator.jymall.common;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.administrator.jymall.LoginActivity;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.TokenUtil;

import org.json.JSONObject;

public class UserActivity extends BaseActivity {
	public static boolean isLogin = true;
	public static boolean isRealName = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String sk=super.getServerKey();
		if(sk.equals("") || getUser().equals("")){
			//CommonUtil.alter("你还未登录，请登录。");
			MyApplication.backActivity = this.getClass();
			startActivity(new Intent(getApplicationContext(),LoginActivity.class));
			isLogin = false;
			isRealName = false;
		}
		else{
			if(TokenUtil.getUserid(sk).equals("0")){
				//CommonUtil.alter("你还未登录，请登录。");
				MyApplication.backActivity = this.getClass();
				startActivity(new Intent(getApplicationContext(),LoginActivity.class));
				isLogin = false;
				isRealName = false;
			}
			try {
				JSONObject user = new JSONObject(getUser());
				JSONObject comp=user.getJSONObject("company");
				// 1:公司审核通过 4:个人审核通过
				if (FormatUtil.toInt(comp.get("ischeck")) == 1 || FormatUtil.toInt(comp.get("ischeck")) == 4) {
					isRealName = true;
				}
				else{
					isRealName = false;
				}
			}
			catch (Exception e){
				Log.i("User", "realName state error!");
				isRealName = false;
			}
		}
		
		super.onCreate(savedInstanceState);
		
		
	}
	

	
	
	
}
