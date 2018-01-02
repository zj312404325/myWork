package com.example.administrator.jymall;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;

import com.example.administrator.jymall.common.TopActivity;

import android.os.Bundle;

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
