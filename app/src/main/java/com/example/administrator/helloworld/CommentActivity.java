package com.example.administrator.helloworld;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;

import com.example.administrator.helloworld.common.TopActivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

@ContentView(R.layout.activity_comment)
public class CommentActivity extends TopActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("评论");
		
		progressDialog.hide();
	}

}
