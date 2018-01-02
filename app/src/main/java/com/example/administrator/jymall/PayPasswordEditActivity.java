package com.example.administrator.jymall;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.ValidationUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyEditText;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;


@ContentView(R.layout.activity_pay_password_edit)
public class PayPasswordEditActivity extends TopActivity {
	
	
	@ViewInject(R.id.submitbtn)
	private Button submitbtn;
	
	@ViewInject(R.id.et_newpass)
	private MyEditText et_newpass;
	@ViewInject(R.id.et_repass)
	private MyEditText et_repass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("支付密码设置");	
		super.progressDialog.hide();
		
	}

	@Event(R.id.submitbtn)
	private void submitclick(View v){
		if(et_newpass.getText().length() <6){
    		CommonUtil.alter("新密码必须大于6位！");return;
    	}
		String ts = ValidationUtil.PasswordValidation(et_newpass.getText());
		if(!ts.equals("")){
			CommonUtil.alter(ts);return;
		}
    	if(!et_newpass.getText().equals(et_repass.getText())){
    		CommonUtil.alter("确认密码不相同");return;
    	}
    	Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("password", et_newpass.getText());
		XUtilsHelper.getInstance().post("app/updatePaypwd.htm", maps,new XUtilsHelper.XCallBack(){

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
						CommonUtil.alter("支付密码修改成功！！！！");
						setResult(1);
						finish();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		
	}


}
