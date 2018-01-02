package com.example.administrator.jymall.view;

import com.example.administrator.jymall.R;
import com.example.administrator.jymall.util.CommonUtil;

import android.app.ProgressDialog;
import android.content.Context;

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
