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
import com.example.administrator.jymall.util.BigDecimalUtil;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

@ContentView(R.layout.activity_prepare_pay)
public class PreparePayActivity extends TopActivity {
	
	@ViewInject(R.id.rl_paypwdedit)
	private RelativeLayout rl_paypwdedit;
	
	private String id;
	private String adminmobile;
	private String orderNo;
	
	@ViewInject(R.id.tv_haspwd)
	private TextView tv_haspwd;
	
	private double umoney=0; //余额
	private double money=0; //支付金额
	private double umoneysy=0; //剩余金额
	
	@ViewInject(R.id.tv_money)
	private TextView tv_money;
	
	@ViewInject(R.id.tv_umoney)
	private TextView tv_umoney;
	
	@ViewInject(R.id.tv_umoneysy)
	private TextView tv_umoneysy;
	
	@ViewInject(R.id.rb_isfreezone)
	private CheckBox rb_isfreezone;
	
	@ViewInject(R.id.et_password)
	private EditText et_password;
	
	@ViewInject(R.id.btn_pay)
	private Button btn_pay;
	
	@ViewInject(R.id.rl_downpay)
	private RelativeLayout rl_downpay;
	
	@ViewInject(R.id.rl_recharge)
	private RelativeLayout rl_recharge;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("订单支付");
		Intent i =this.getIntent();
		id = i.getStringExtra("id");
		initData();
	}
	
	private void initData(){
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("id", id);
		XUtilsHelper.getInstance().post("app/preparePay.htm", maps,new XUtilsHelper.XCallBack(){

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
						adminmobile = res.getString("adminmobile");
						orderNo = res.getJSONObject("order").getString("orderNo");
						String haspwd = res.getString("haspwd");
						if(haspwd.equals("1")){
							tv_haspwd.setText("如需修改密码？点击此处设置");
						}
						else{
							tv_haspwd.setText("你还没有支付密码？点击此处设置");
						}
						umoney = res.getDouble("umoney");
						money = res.getJSONObject("order").getDouble("money");
						umoneysy = BigDecimalUtil.sub(umoney, money);
						
						tv_money.setText(FormatUtil.toStringWithDecimal(money));
						tv_umoney.setText(FormatUtil.toStringWithDecimal(umoney));
						tv_umoneysy.setText(FormatUtil.toStringWithDecimal(umoneysy));
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
	}

	@Event(R.id.btn_pay)
	private void btntopay(View v){
		if(umoneysy <= 0){
			CommonUtil.alter("你的余额不足，请先充值！");return ;
		}
		if(et_password.getText().length()<1){
			CommonUtil.alter("支付密码不能为空！");return ;
		}
		String isfreezone = rb_isfreezone.isChecked()?"1":"0";
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("isfreezone",isfreezone);
		maps.put("orderNo", orderNo);
		maps.put("id", id);
		maps.put("password", et_password.getText().toString());
		XUtilsHelper.getInstance().post("app/doPayCurrent.htm", maps,new XUtilsHelper.XCallBack(){

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
						CommonUtil.alter("支付成功");
						Intent intent = new Intent(getApplicationContext(),Order_Xh_Info_Activity.class);
						intent.putExtra("id", id);
						startActivity(intent);
						finish();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
	}
	
	
	@Event(R.id.rl_paypwdedit)
	private void paypwdedit(View v){
		Intent i =  new Intent(getApplicationContext(), PayPasswordActivity.class);
		i.putExtra("adminmobile", adminmobile);
		startActivity(i);
	}
	
	@Event(R.id.rl_downpay)
	private void downpayClick(View v){
		Intent i =  new Intent(getApplicationContext(), PayXxActivity.class);
		i.putExtra("orderNo", orderNo);
		i.putExtra("id", id);
		startActivityForResult(i,CommonUtil.getInt(R.string.RECODE_DOWNPAY));
	}

	@Event(R.id.rl_recharge)
	private void rechargeClick(View v){
		Intent i =  new Intent(getApplicationContext(), RechargeActivity.class);
		startActivityForResult(i, CommonUtil.getInt(R.string.RECODE_RECHARGE));
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    
		if(resultCode==RESULT_OK){ 
			
			if(requestCode == CommonUtil.getInt(R.string.RECODE_RECHARGE) ){
				
			}
			else if(requestCode == CommonUtil.getInt(R.string.RECODE_DOWNPAY)){
				Intent intent = new Intent(getApplicationContext(),Order_Xh_Info_Activity.class);
				intent.putExtra("id", id);
				startActivity(intent);
				finish();
			}
		}
	}
	
}
