package com.example.administrator.helloworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.FormatUtil;
import com.example.administrator.helloworld.util.XUtilsHelper;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

@ContentView(R.layout.activity_bank_info)
public class BankInfoActivity extends TopActivity implements ValidationListener {

	@ViewInject(R.id.savebtn)
	private Button savebtn;
	
	private String id="";
	
	@Required(order = 1,  message = "银行名称必须填写")
	@ViewInject(R.id.et_bankname)
	private EditText et_bankname;
	@Required(order = 2, message = "开户行必须填写")
	@ViewInject(R.id.et_bankadd)
	private EditText et_bankadd;
	@Required(order = 3, message = "银行账号必须填写")
	@ViewInject(R.id.et_bankno)
	private EditText et_bankno;
	
	Validator validator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("编辑银行信息");		
		progressDialog.hide();
		
		Intent i = getIntent();
		String tempString = i.getStringExtra("bank");
		if(FormatUtil.isNoEmpty(tempString)){
			try{
			JSONObject cJobj = FormatUtil.toJSONObject(tempString);
			if(cJobj != null){
				id = cJobj.getString("id");
				et_bankname.setText(cJobj.getString("bankName"));
				et_bankadd.setText(cJobj.getString("bankAdd"));
				et_bankno.setText(cJobj.getString("bankNo"));
				
			}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}		
		validator = new Validator(this);
	    validator.setValidationListener(this);
	    
	    et_bankname.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(BankInfoActivity.this);
				final String[] units ={"汉口银行","重庆农村商业银行","微商银行","重庆银行",
						"大连银行","南京银行","盛京银行","威海市商业银行","浙江稠州商业银行",
						"北京农商银行","天津银行","赣州银行","莱商银行","南洋商业银行",
						"广州银行","上海银行","宁波银行","包商银行","江苏银行","哈尔滨银行",
						"北京银行","交通银行","农业银行","工商银行","中信银行","杭州银行","中国银行",
						"银联","温州银行","建设银行","上海农商银行","平安银行","广发银行","广东南粤银行",
						"台州银行","湖州银行","九江银行","东亚银行","锦州银行","华夏银行","深圳发展银行",
						"民生银行","浦发银行","citibank","中国邮政储蓄银行","兴业银行","招商银行",
						"大新银行","恒生银行","泉州银行","青岛银行",
						"顺德农商银行","南昌银行","兰州银行","常熟农商银行","青海银行","宁夏银行",
						"广州农商银行","富滇银行","河北银行","成都农商银行","中国光大银行","上饶银行",
						"天津农商银行","潍坊银行","齐鲁银行","华润银行"};

                builder.setItems(units, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    	et_bankname.setText(units[which].toString());
                    }
                });
                builder.show();	
			}
		});
	}
	
	private void sendData(){
		progressDialog.show();;
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("id", id);
		maps.put("bankname", et_bankname.getText().toString());
		maps.put("bankadd", et_bankadd.getText().toString());
		maps.put("bankno", et_bankno.getText().toString());
		XUtilsHelper.getInstance().post("app/moneyAddBank.htm", maps,new XUtilsHelper.XCallBack(){

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

	@Event(R.id.savebtn)
	private void saveClick(View v){
		validator.validate();
	}

}
