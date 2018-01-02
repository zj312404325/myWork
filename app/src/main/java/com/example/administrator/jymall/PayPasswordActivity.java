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
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyEditText;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@ContentView(R.layout.activity_pay_password)
public class PayPasswordActivity extends TopActivity {

	
	


	@ViewInject(R.id.nextbtn)
	private Button nextbtn;
	
	@ViewInject(R.id.btn_VerifyCode)
	private Button btn_VerifyCode;
	@ViewInject(R.id.tv_ts)
	private TextView tv_ts;
	
	@ViewInject(R.id.et_mobile)
	private TextView et_mobile;
	@ViewInject(R.id.et_mobileCode)
	private EditText et_mobileCode;
	@ViewInject(R.id.et_parentCode)
	private MyEditText et_parentCode;
	
	private String adminmobile;
	
	Handler handler;  
	private int time = 120;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("安全验证");		
		progressDialog.hide();
		Intent i = this.getIntent();
		adminmobile = i.getStringExtra("adminmobile");
		et_mobile.setText(adminmobile);
		handler = new Handler();
	    
	    btn_VerifyCode.setOnTouchListener(new vcotl());
	}
	
	@Event(R.id.nextbtn)
	private void nextClick(View v){
		if(et_mobile.getText().length() != 11){
    		CommonUtil.alter("手机号不正确！！");return;
    	}
    	if(et_mobileCode.getText().length() != 4){
    		CommonUtil.alter("验证码不正确！！");return;
    	}
    	Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("mobile", adminmobile);
		maps.put("mobileCode", et_mobileCode.getText().toString());
		XUtilsHelper.getInstance().post("app/verifyMobileCode.htm", maps,new XUtilsHelper.XCallBack(){

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
						Intent i = new Intent(getApplicationContext(),PayPasswordEditActivity.class);
						startActivityForResult(i,22);
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==22 && resultCode==1){
			finish();
		}
	}
		
	private void sendData(){
		if( et_mobile.getText().length() != 11){
			CommonUtil.alter("手机号码不正确！！！");
			return;
		}
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("mobile", adminmobile);
		maps.put("f", "1");
		XUtilsHelper.getInstance().post("app/sendVerifyCodeToMobile.htm", maps,new XUtilsHelper.XCallBack(){

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
						tv_ts.setVisibility(View.VISIBLE);
						handler.postDelayed(runnable, 1000);
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
	}
	
	class vcotl implements OnTouchListener {
	       //类型(分类)
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					sendData();
					return false;
				}
				return true;
			}
	}
	
	
	Runnable runnable = new Runnable() {
        @Override  
        public void run() {  
            // handler自带方法实现定时器  
            try {  
            	if(time == 0){
            		handler.removeCallbacks(this);
            		btn_VerifyCode.setText("获取验证码");
            		btn_VerifyCode.setOnTouchListener(new vcotl());
            		time = 120;
            	}
            	else{
            		btn_VerifyCode.setText(time+"秒");
            		btn_VerifyCode.setOnTouchListener(null);
            		time--;
	                handler.postDelayed(this, 1000);                 
	                
            	}
                
            } catch (Exception e) {  
                e.printStackTrace();   
            }  
        }  
    };


}
