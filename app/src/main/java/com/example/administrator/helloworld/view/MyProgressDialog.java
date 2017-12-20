package com.example.administrator.helloworld.view;

import com.example.administrator.helloworld.R;
import com.example.administrator.helloworld.common.MyApplication;
import com.example.administrator.helloworld.util.CommonUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public class MyProgressDialog{

	private static ProgressDialog m;
	
	public static ProgressDialog getInstance(Context context) {
		m =  new ProgressDialog(context);
		//m.setTitle(CommonUtil.getStrings(R.string.progressDialog_title));
		m.setMessage(CommonUtil.getStrings(R.string.progressDialog_msg));
        return m;
    }
	
	public static void setMsg(String msg){
		m.setMessage(msg);
	}
	
	public static void setTitle(String title){
		m.setTitle(title);
	}

}
