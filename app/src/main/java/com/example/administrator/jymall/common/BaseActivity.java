package com.example.administrator.jymall.common;




import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

import com.example.administrator.jymall.util.Installation;
import com.example.administrator.jymall.util.TokenUtil;
import com.example.administrator.jymall.view.MyProgressDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.Date;

public class BaseActivity extends Activity {
	
	public String serverKey= "";
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	public ProgressDialog progressDialog = null;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDialog = MyProgressDialog.getInstance(this);
		progressDialog.show();
		SharedPreferences read = getSharedPreferences("config", MODE_WORLD_READABLE);
		serverKey =  read.getString("serverKey", "");
		if(serverKey.equals("")){
			 SharedPreferences.Editor editor = getSharedPreferences("config", MODE_WORLD_WRITEABLE).edit();  
			 serverKey = TokenUtil.setServerkey("0", Installation.id(getApplicationContext()));
             editor.putString("serverKey", serverKey);
             editor.commit();
		}
	}
	
	public void setIndexData(String obj){
		SharedPreferences.Editor editor = getSharedPreferences("data", MODE_WORLD_WRITEABLE).edit();  
        editor.putString("indexData", obj);
        editor.putLong("indexDataTime", new Date().getTime());
        editor.commit();
	}
	
	public String getIndexData(){
		int time = 1000*60*60*3; //3个小时
		SharedPreferences read = getSharedPreferences("data", MODE_WORLD_READABLE);
		String indexData =  read.getString("indexData", "");
		long oldTime = read.getLong("indexDataTime", 0);
		if(oldTime+time <new Date().getTime() ){
			return "";
		}
		return indexData;
	}

	public void setIndexHotData(String obj){
		SharedPreferences.Editor editor = getSharedPreferences("data", MODE_WORLD_WRITEABLE).edit();
		editor.putString("hotData", obj);
		editor.putLong("hotDataTime", new Date().getTime());
		editor.commit();
	}

	public String getIndexHotData(){
		int time = 1000*60*60*3; //3个小时
		SharedPreferences read = getSharedPreferences("data", MODE_WORLD_READABLE);
		String hotData =  read.getString("hotData", "");
		long oldTime = read.getLong("hotDataTime", 0);
		if(oldTime+time <new Date().getTime() ){
			return "";
		}
		return hotData;
	}
	
	public void setServerKey(String sk){
		serverKey = sk;
		SharedPreferences.Editor editor = getSharedPreferences("config", MODE_WORLD_WRITEABLE).edit();  
        editor.putString("serverKey", serverKey);
        editor.commit();
	}
	
	public String getServerKey(){
		SharedPreferences read = getSharedPreferences("config", MODE_WORLD_READABLE);
		return  read.getString("serverKey", "");
	}
	
	public void clearServerKey(){
		SharedPreferences.Editor editor = getSharedPreferences("config", MODE_WORLD_WRITEABLE).edit();  
		editor.remove("serverKey");
		editor.remove("user");
		editor.commit();
	}
	
	public void setUser(String user){
		SharedPreferences.Editor editor = getSharedPreferences("config", MODE_WORLD_WRITEABLE).edit();  
        editor.putString("user", user);
        editor.commit();
	}
	
	public String getUser(){
		SharedPreferences read = getSharedPreferences("config", MODE_WORLD_READABLE);
		return read.getString("user", "");
	}
	
	@Override
	protected void onDestroy(){
		progressDialog.dismiss();
	    super.onDestroy();
	    MyApplication.getInstance().finishActivity(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		 MobclickAgent.onPause(this);
	}
	
	
}
