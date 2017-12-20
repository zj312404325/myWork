package com.example.administrator.helloworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.helloworld.common.MyApplication;
import com.example.administrator.helloworld.common.TopActivity;
import com.example.administrator.helloworld.common.UserActivity;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.ComputeCallBack;
import com.example.administrator.helloworld.util.FormatUtil;
import com.example.administrator.helloworld.util.XUtilsHelper;
import com.example.administrator.helloworld.view.WheelCascade;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;


import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

@ContentView(R.layout.activity_user_info_edit)
public class UserInfoEditActivity extends TopActivity implements ValidationListener {

	@ViewInject(R.id.savebtn)
	private Button savebtn;
	@TextRule(order = 1, minLength = 2, maxLength = 11, message = "请输入正确姓名")
	@ViewInject(R.id.et_realname)
	private EditText et_realname;
	
	@TextRule(order = 2, minLength = 11, maxLength = 11, message = "请输入正确的手机号码")
	@ViewInject(R.id.et_mobile)
	private EditText et_mobile;
	
	@Required(order = 3,message="邮箱必填")
	@Email(order = 4,message="邮箱格式不正确")
	@ViewInject(R.id.et_email)
	private EditText et_email;
	
	@Required(order = 5,message="固定电话必填")
	@ViewInject(R.id.et_phone)
	private EditText et_phone;
	
	@ViewInject(R.id.rg_sex)
	private RadioGroup rg_sex;
	@ViewInject(R.id.rg_sex1)
	private RadioButton rg_sex1;
	@ViewInject(R.id.rg_sex2)
	private RadioButton rg_sex2;
	
	
	Validator validator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("编辑账户信息");		
		JSONObject user =FormatUtil.toJSONObject(super.getUser());
		if(user != null){
			try {
				et_realname.setText(user.getString("realname"));
				et_mobile.setText(user.getString("mobile"));
				et_phone.setText(user.getJSONObject("company").getString("phone"));
				et_email.setText(user.getString("email"));
				if(user.getString("sex").equals("女")){
					rg_sex2.setChecked(true);
				}
				else{
					rg_sex1.setChecked(true);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		validator = new Validator(this);
	    validator.setValidationListener(this);
		progressDialog.hide();
		
	}
	
	private void sendData(){
		RadioButton rb =  (RadioButton)findViewById(rg_sex.getCheckedRadioButtonId());
		progressDialog.show();;
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("realname",et_realname.getText().toString() );
		maps.put("mobile",et_mobile.getText().toString() );
		maps.put("phone",et_phone.getText().toString() );
		maps.put("email",et_email.getText().toString() );
		maps.put("sex",rb.getText().toString() );
		
		XUtilsHelper.getInstance().post("app/modifyContactInfo.htm", maps,new XUtilsHelper.XCallBack(){

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
						MyApplication.getInstance().finishActivity();
					}
					else{
						setUser(res.get("data").toString());
						startActivity(new Intent(getApplicationContext(),UserInfoActivity.class));
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		
	}
	
	@Event(value=R.id.savebtn,type=View.OnTouchListener.class)
	private boolean ucinfoTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			validator.validate();
			
			return false;
		}
		return true;
	}

	@Override
	public void preValidation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuccess() {
		sendData();
		
	}

	@Override
	public void onFailure(View failedView, Rule<?> failedRule) {
		String message = failedRule.getFailureMessage(); 
		if (failedView instanceof EditText) { 
			failedView.requestFocus(); 
			((EditText) failedView).setError(message); 
		} 
		else { CommonUtil.alter(message); }
		
	}

	@Override
	public void onValidationCancelled() {
		// TODO Auto-generated method stub
		
	}
	
	

}
