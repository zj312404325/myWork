package com.example.administrator.jymall.common;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

//import com.example.administrator.helloworld.CategoryActivity;
//import com.example.administrator.helloworld.ProductActivity;
import com.example.administrator.jymall.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class TopNoLoginActivity  extends BaseActivity{

	@ViewInject(R.id.top_back)
	public ImageButton top_back;
	
	@ViewInject(R.id.title)
	public TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Event(value=R.id.top_back)
	private void Click1(View v){
		MyApplication.getInstance().finishActivity();
	}
}
