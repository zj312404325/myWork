package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.jymall.common.MyApplication;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.ComputeCallBack;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.WheelCascade;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.TextRule;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_address_info)
public class AddressInfoActivity extends TopActivity implements ValidationListener {

	@ViewInject(R.id.savebtn)
	private Button savebtn;
	
	private String id="";
	private String addrdefault="false";
	@TextRule(order = 1, minLength = 2, maxLength = 10, message = "收 货 人填写不正确")
	@ViewInject(R.id.et_contact)
	private EditText et_contact;
	@TextRule(order = 3, minLength = 11, maxLength = 11, message = "请输入正确的手机号码")
	@ViewInject(R.id.et_mobilephone)
	private EditText et_mobilephone;
	@ViewInject(R.id.et_phone)
	private EditText et_phone;
	@ViewInject(R.id.et_zone)
	private EditText et_zone;
	@ViewInject(R.id.et_addr)
	private EditText et_addr;
	@ViewInject(R.id.et_postcode)
	private EditText et_postcode;
	
	
	Validator validator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("编辑收货地址");		
		progressDialog.hide();
		
		Intent i = getIntent();
		String tempString = i.getStringExtra("address");
		if(FormatUtil.isNoEmpty(tempString)){
			try{
			JSONObject cJobj = FormatUtil.toJSONObject(tempString);
			if(cJobj != null){
				id = cJobj.getString("id");
				addrdefault = cJobj.getString("addrdefault");
				et_contact.setText(cJobj.getString("contact"));
				et_mobilephone.setText(cJobj.getString("mobilephone"));
				et_phone.setText(cJobj.getString("phone"));
				et_zone.setText(cJobj.getString("province")
						+"-"+cJobj.getString("city")
						+"-"+cJobj.getString("district"));
				et_addr.setText(cJobj.getString("addr"));
				et_postcode.setText(cJobj.getString("postcode"));
			}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		et_zone.setOnTouchListener(new div1());	
		
		validator = new Validator(this);
	    validator.setValidationListener(this);
	}
	
	private void sendData(){
		if(!FormatUtil.isNoEmpty(et_contact.getText().toString())){
			CommonUtil.alter("请填写收货人信息！");
			return;
		}
		else{
			if(FormatUtil.getStringLength(et_contact.getText().toString())>10 || FormatUtil.getStringLength(et_contact.getText().toString())<=1){
				CommonUtil.alter("收货人信息不正确！");
				return;
			}
		}
		if(!FormatUtil.isNoEmpty(et_mobilephone.getText().toString())){
			CommonUtil.alter("请填写手机号码！");
			return;
		}
		else{
			if(FormatUtil.getStringLength(et_mobilephone.getText().toString())!=11){
				CommonUtil.alter("手机号码不正确！");
				return;
			}
		}
		if(!FormatUtil.isNoEmpty(et_zone.getText().toString())){
			CommonUtil.alter("请选择所在区域！");
			return;
		}
		else{
			if(FormatUtil.getStringLength(et_zone.getText().toString())>30 || FormatUtil.getStringLength(et_zone.getText().toString())<=1){
				CommonUtil.alter("区域信息不正确！");
				return;
			}
		}
		if(!FormatUtil.isNoEmpty(et_addr.getText().toString())){
			CommonUtil.alter("请填写详细地址！");
			return;
		}
		else{
			if(FormatUtil.getStringLength(et_addr.getText().toString())>100 || FormatUtil.getStringLength(et_addr.getText().toString())<=1){
				CommonUtil.alter("详细地址不正确！");
				return;
			}
		}
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("contact", et_contact.getText().toString());
		maps.put("id", id);
		maps.put("addrdefault", addrdefault);
		maps.put("mobilephone", et_mobilephone.getText().toString());
		maps.put("phone", et_phone.getText().toString());
		maps.put("zone", et_zone.getText().toString());
		maps.put("addr", et_addr.getText().toString());
		maps.put("postcode", et_postcode.getText().toString());
		XUtilsHelper.getInstance().post("app/addAddress.htm", maps,new XUtilsHelper.XCallBack(){

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
						setResult(11);
						MyApplication.getInstance().finishActivity();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		
	}
	
	class div1 implements OnTouchListener {
		
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction() == KeyEvent.ACTION_UP){
				final AlertDialog alertDialog = new AlertDialog.Builder(AddressInfoActivity.this).create();  
				alertDialog.show(); 
				Window window = alertDialog.getWindow();  
				window.setContentView(R.layout.activity_cascade);
				et_zone.getText().toString();
				new WheelCascade(window,et_zone.getText().toString(),new ComputeCallBack(){
					@Override
					public void onComputeEnd(String str) {
						alertDialog.cancel();
						et_zone.setText(str);
					}
					
				});
				
				return false;
			}
			return true;
		}
	};
	

	@Event(R.id.savebtn)
	private void saveClick(View v){
		//validator.validate();
		sendData();
	}


	@Override
	public void preValidation() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		sendData();
	}


	@Override
	public void onFailure(View failedView, Rule<?> failedRule) {
		// TODO Auto-generated method stub
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
