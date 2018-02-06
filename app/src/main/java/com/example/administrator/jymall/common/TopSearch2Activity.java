package com.example.administrator.jymall.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.administrator.jymall.R;
import com.example.administrator.jymall.SearchProductListActivity;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

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

	@Event(value={R.id.top_searchbar_input_txt},type=View.OnKeyListener.class )
	private  boolean searchonKey(View v, int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_ENTER){
			Intent i = new Intent(getApplicationContext(),SearchProductListActivity.class);
			i.putExtra("keyword", top_searchbar_input_txt.getText().toString());
			startActivity(i);
			return false;
		}
		return false;
	}
}
