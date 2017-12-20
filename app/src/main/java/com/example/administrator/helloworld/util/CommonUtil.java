package com.example.administrator.helloworld.util;

import java.util.Map;

import com.example.administrator.helloworld.R;
import com.example.administrator.helloworld.common.MyApplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CommonUtil {

	/**
	 * 弹出提示框
	 * @param msg
	 * @param msg
	 */
	public static void alter(String msg){
		Toast toast =Toast.makeText(MyApplication.getInstance().getBaseContext(), msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);// 最上方显示
		//LinearLayout toastLayout = (LinearLayout) toast.getView();
		//ImageView imageView = new ImageView(MyApplication.getInstance());
		//imageView.setImageResource(R.drawable.ic_launcher);
		//toastLayout.addView(imageView, 0);// 0 图片在文字的上方 ， 1 图片在文字的下方
		toast.show();
	}
	
	public static void alter(String msg,Context contxt){
		Toast toast =Toast.makeText(contxt, msg,Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);// 最上方显示
		//LinearLayout toastLayout = (LinearLayout) toast.getView();
		//ImageView imageView = new ImageView(MyApplication.getInstance());
		//imageView.setImageResource(R.drawable.ic_launcher);
		//toastLayout.addView(imageView, 0);// 0 图片在文字的上方 ， 1 图片在文字的下方
		toast.show();
	}
	
	/**
	 * 得到全局文本定义
	 * @param key
	 */
	public static String getStrings(int key){
		return MyApplication.getInstance().getResources().getString(key);
	}
	
	public static int getInt(int key){
		return FormatUtil.toInt( MyApplication.getInstance().getResources().getString(key));
	}
	
	public static Drawable getDrawable(int key){
		return MyApplication.getInstance().getResources().getDrawable(key);
	}
	
	
	public static void openQiutDialog() {
		new AlertDialog.Builder(MyApplication.getInstance().currentActivity())
		.setTitle("金赢网")
		.setMessage("是否退出金赢网？")
				.setPositiveButton("残忍退出", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						// System.exit(0);
						// finish();
						MyApplication.getInstance().AppExit();
					}
				}).setNegativeButton("我再看看",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,int which) {

							}
				}).show();
	}
	
	
	
	
}
