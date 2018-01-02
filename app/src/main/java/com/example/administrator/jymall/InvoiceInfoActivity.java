package com.example.administrator.jymall;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.jymall.common.MyApplication;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.mobsandgeeks.saripaar.Validator;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

@ContentView(R.layout.activity_invoice_info)
public class InvoiceInfoActivity extends TopActivity {

	@ViewInject(R.id.savebtn)
	private Button savebtn;
	
	private String id="";
	private String invoiceType = "COMMON";
	private String invoiceContent = "1";
	
	Validator validator;
	@ViewInject(R.id.ll_i1)
	private LinearLayout ll_i1;
	@ViewInject(R.id.ll_i2)
	private LinearLayout ll_i2;
	
	@ViewInject(R.id.rg_invoiceType)
	private RadioGroup rg_invoiceType;
	@ViewInject(R.id.rg_invoiceType1)
	private RadioButton rg_invoiceType1;
	@ViewInject(R.id.rg_invoiceType2)
	private RadioButton rg_invoiceType2;
	
	@ViewInject(R.id.rg_invoiceContentType)
	private RadioGroup rg_invoiceContentType;
	@ViewInject(R.id.rg_invoiceContentType1)
	private RadioButton rg_invoiceContentType1;
	@ViewInject(R.id.rg_invoiceContentType2)
	private RadioButton rg_invoiceContentType2;
	
	@ViewInject(R.id.et_title)
	private EditText et_title;
	
	@ViewInject(R.id.et_companyName)
	private EditText et_companyName;
	
	@ViewInject(R.id.et_taxNo)
	private EditText et_taxNo;
	
	@ViewInject(R.id.et_registerAddress)
	private EditText et_registerAddress;
	
	@ViewInject(R.id.et_registerPhone)
	private EditText et_registerPhone;
	
	@ViewInject(R.id.et_bankName)
	private EditText et_bankName;
	
	@ViewInject(R.id.et_bankNo)
	private EditText et_bankNo;
	
	@ViewInject(R.id.et_invoiceContent)
	private EditText et_invoiceContent;
	

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("编辑发票");		
		progressDialog.hide();
		Intent i = getIntent();
		String tempString = i.getStringExtra("invoice");
		
		if(FormatUtil.isNoEmpty(tempString)){
			try{
				JSONObject cJobj = FormatUtil.toJSONObject(tempString);
				if(cJobj != null){
					id = cJobj.getString("id");
					invoiceType =  cJobj.getString("invoiceType");
					if(invoiceType.equals("COMMON")){
						ll_i1.setVisibility(View.VISIBLE);
						ll_i2.setVisibility(View.GONE);
						rg_invoiceType1.setChecked(true);
					}
					else{
						ll_i2.setVisibility(View.VISIBLE);
						ll_i1.setVisibility(View.GONE);
						rg_invoiceType2.setChecked(true);
					}
					et_title.setText(cJobj.getString("title"));
					et_companyName.setText(cJobj.getString("companyName"));
					et_taxNo.setText(cJobj.getString("taxNo"));
					et_registerAddress.setText(cJobj.getString("registerAddress"));
					et_registerPhone.setText(cJobj.getString("registerPhone"));
					et_bankName.setText(cJobj.getString("bankName"));
					et_bankNo.setText(cJobj.getString("bankNo"));
					invoiceContent = cJobj.getString("invoiceContent");
					if(invoiceContent.equals("1")){
						et_invoiceContent.setVisibility(View.GONE);
						rg_invoiceContentType1.setChecked(true);
					}
					else{
						rg_invoiceContentType2.setChecked(true);
						et_invoiceContent.setVisibility(View.VISIBLE);
						et_invoiceContent.setText(cJobj.getString("invoiceContent"));
					}
					
					
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		rg_invoiceType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if(arg1 == rg_invoiceType1.getId()){
					invoiceType = "COMMON";
					ll_i1.setVisibility(View.VISIBLE);
					ll_i2.setVisibility(View.GONE);
				}
				else{
					invoiceType = "VAT";
					ll_i2.setVisibility(View.VISIBLE);
					ll_i1.setVisibility(View.GONE);
				}
				
			}
		});
		rg_invoiceContentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				RadioButton radioButton = (RadioButton)findViewById(arg0.getCheckedRadioButtonId());
				if(radioButton.getTag().equals("1")){
					invoiceContent = "1";
					
					et_invoiceContent.setVisibility(View.INVISIBLE);
				}
				else{
					invoiceContent = "0";
					et_invoiceContent.setVisibility(View.VISIBLE);
				}
			}
		});
		
		/*validator = new Validator(this);
	    validator.setValidationListener(this);*/
	}
	
	private void sendData(){
		
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("id", id);
		maps.put("invoiceType", invoiceType);
		if(invoiceType.equals("COMMON")){
			if(et_title.getText().toString().equals("")){
				CommonUtil.alter("发票抬头必须填写");
				return;
			}
		}
		else{
			if(et_companyName.getText().toString().equals("")){
				CommonUtil.alter("公司名称必须填写");return;
			}
			if(et_taxNo.getText().toString().equals("")){
				CommonUtil.alter("纳税人识别号必须填写");return;
			}
			if(et_registerAddress.getText().toString().equals("")){
				CommonUtil.alter("注册地址必须填写");return;
			}
			if(et_registerPhone.getText().toString().equals("")){
				CommonUtil.alter("注册电话必须填写");return;
			}
			if(et_bankName.getText().toString().equals("")){
				CommonUtil.alter("开户银行必须填写");return;
			}
			if(et_bankNo.getText().toString().equals("")){
				CommonUtil.alter("银行账户必须填写");return;
			}
		}		
		if(!invoiceContent.equals("1") && et_invoiceContent.getText().toString().equals(""))
		{
			CommonUtil.alter("请输入发票内容");return;
		}
		progressDialog.show();
		maps.put("title", et_title.getText().toString());
		maps.put("companyName", et_companyName.getText().toString());
		maps.put("taxNo", et_taxNo.getText().toString());
		maps.put("registerAddress", et_registerAddress.getText().toString());
		maps.put("registerPhone", et_registerPhone.getText().toString());
		maps.put("bankName", et_bankName.getText().toString());
		maps.put("bankNo", et_bankNo.getText().toString());
		if(invoiceContent.equals("1"))
			maps.put("invoiceContent", "1");
		else
			maps.put("invoiceContent", et_invoiceContent.getText().toString());
		XUtilsHelper.getInstance().post("app/invoiceSave.htm", maps,new XUtilsHelper.XCallBack(){

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
	
	@Event(R.id.savebtn)
	private void saveClick(View v){
		sendData();
	}
}
