package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.administrator.jymall.common.MyApplication;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.ValidationUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

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

	@ViewInject(R.id.et_qq)
	private EditText et_qq;
	@ViewInject(R.id.et_alipay)
	private EditText et_alipay;
	@ViewInject(R.id.et_fax)
	private EditText et_fax;
	
	
	Validator validator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("编辑账户信息");		
		JSONObject user =FormatUtil.toJSONObject(super.getUser());

		if(user != null){
			try {
				JSONObject company =user.getJSONObject("company");
				et_realname.setText(user.getString("realname"));
				et_mobile.setText(user.getString("mobile"));
				et_phone.setText(user.getJSONObject("company").getString("phone"));
				et_email.setText(user.getString("email"));
				et_qq.setText(user.getString("qq"));
				et_alipay.setText(user.getString("alipayno"));
				et_fax.setText(company.getString("fax"));
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
		if(!FormatUtil.isNoEmpty(et_realname.getText().toString())) {
			CommonUtil.alter("真实姓名不能为空！");
			return;
		}
		if (FormatUtil.getStringLength(et_realname.getText().toString()) > 20) {
			CommonUtil.alter("真实姓名过长！");
			return;
		}

		if(!FormatUtil.isNoEmpty(et_mobile.getText().toString())) {
			CommonUtil.alter("手机号码不能为空！");
			return;
		}

		if (!FormatUtil.isPhoneLegal(et_mobile.getText().toString())) {
			CommonUtil.alter("请输入正确的手机号码！");
			return;
		}

		if(!FormatUtil.isNoEmpty(et_phone.getText().toString())) {
			CommonUtil.alter("固定电话号码不能为空！");
			return;
		}

		if (!FormatUtil.isFixedPhone(et_phone.getText().toString())) {
			CommonUtil.alter("请输入正确的固定电话号码！");
			return;
		}

		if(!FormatUtil.isNoEmpty(et_email.getText().toString())) {
			CommonUtil.alter("邮箱不能为空！");
			return;
		}
		if(!ValidationUtil.emailValidation(et_email.getText().toString())){
			CommonUtil.alter("邮箱格式不正确！");
			return;
		}

		progressDialog.show();

		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("realname",et_realname.getText().toString() );
		maps.put("mobile",et_mobile.getText().toString() );
		maps.put("phone",et_phone.getText().toString() );
		maps.put("email",et_email.getText().toString() );
		maps.put("qq",et_qq.getText().toString() );
		maps.put("alipayno",et_alipay.getText().toString() );
		maps.put("fax",et_fax.getText().toString() );
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
