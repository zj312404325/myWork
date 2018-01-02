package com.example.administrator.jymall.common;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.jymall.R;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class TopSearch2Activity  extends BaseActivity{

	@ViewInject(R.id.top_back)
	public ImageButton top_back;
	
	@ViewInject(R.id.top_searchbar_input_txt)
	public EditText top_searchbar_input_txt;
	
	@ViewInject(R.id.top_searchbar_input_img)
	public ImageView top_searchbar_input_img;
	
	@ViewInject(R.id.top_fx)
	public ImageButton top_fx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Event(value=R.id.top_back)
	private void Click1(View v){
		MyApplication.getInstance().finishActivity();
	}	
	
	
	@Event(value=R.id.top_fx)
	private void Click3(View v){
		//CommonUtil.alter("我点击了分享");
	}
}
