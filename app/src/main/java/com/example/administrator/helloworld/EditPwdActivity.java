package com.example.administrator.helloworld;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.helloworld.common.TopActivity;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.XUtilsHelper;
import com.example.administrator.helloworld.view.MyEditText;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

@ContentView(R.layout.activity_edit_pwd)
public class EditPwdActivity extends TopActivity {

	@ViewInject(R.id.submitbtn)
	private Button submitbtn;
	
	@ViewInject(R.id.et_newpass)
	private MyEditText et_newpass;
	@ViewInject(R.id.et_repass)
	private MyEditText et_repass;
	@ViewInject(R.id.et_oldpass)
	private MyEditText et_oldpass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("修改密码");
		
		progressDialog.hide();
	}

	@Event(R.id.submitbtn)
	private void submitclick(View v){
		if(et_oldpass.getText().length() <1){
    		CommonUtil.alter("老密码不能为空！");return;
    	}
		if(et_newpass.getText().length() <6){
    		CommonUtil.alter("新密码必须大于6位！");return;
    	}
    	if(!et_newpass.getText().equals(et_repass.getText())){
    		CommonUtil.alter("确认密码不相同");return;
    	}
    	Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("newpass", et_newpass.getText());
		maps.put("repass", et_repass.getText());
		maps.put("oldpass", et_oldpass.getText());
		XUtilsHelper.getInstance().post("app/updatePass.htm", maps,new XUtilsHelper.XCallBack(){

			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				progressDialog.hide();
				JSONObject res;
				try {
					res = new JSONObject(result);
					setServerKey(res.get("serverKey").toString());
					if(res.get("d").equals("n")){
						CommonUtil.alter(res.get("msg").toString());
					}
					else{
						startActivity(new Intent(getApplicationContext(),EditPwdResActivity.class));
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		
	}

}
