package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.jymall.common.MyApplication;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.BigDecimalUtil;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

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
	
	@ViewInject(R.id.tv_leftUmoney)
	private TextView tv_leftUmoney;
	
	@ViewInject(R.id.et_password)
	private EditText et_password;
	
	@ViewInject(R.id.btn_offlinePay)
	private Button btn_offlinePay;

	@ViewInject(R.id.btn_umoneyPay)
	private Button btn_umoneyPay;
	
	@ViewInject(R.id.bt_recharge)
	private Button bt_recharge;

	@ViewInject(R.id.cb_selectUmoney)
	private CheckBox cb_selectUmoney;

	@ViewInject(R.id.ll_leftUmoney)
	private LinearLayout ll_leftUmoney;

	@ViewInject(R.id.ll_umoneyPwd)
	private LinearLayout ll_umoneyPwd;

	@ViewInject(R.id.ll_payMethod)
	private LinearLayout ll_payMethod;

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
		XUtilsHelper.getInstance().post("app/prepareMallPay.htm", maps,new XUtilsHelper.XCallBack(){

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
							tv_haspwd.setText("如需修改密码,点击此处设置");
						}
						else{
							tv_haspwd.setText("你还没有支付密码,点击此处设置");
						}
						umoney = FormatUtil.toDouble(res.getString("umoney"));
						money = FormatUtil.toDouble(res.getJSONObject("order").getString("money"));
						umoneysy = BigDecimalUtil.sub(umoney, money,2);
						if(umoneysy<0){
							umoneysy=0d;
						}
						
						tv_money.setText(FormatUtil.toStringWithDecimal(money));
						tv_umoney.setText(FormatUtil.toStringWithDecimal(umoney));
						tv_leftUmoney.setText(FormatUtil.toStringWithDecimal(umoneysy));
					}

					//给CheckBox设置事件监听
					cb_selectUmoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// TODO Auto-generated method stub
							if(isChecked){//选中
								ll_leftUmoney.setVisibility(View.VISIBLE);
								ll_umoneyPwd.setVisibility(View.VISIBLE);
								ll_payMethod.setVisibility(View.GONE);
							}else{//取消选中
								ll_leftUmoney.setVisibility(View.GONE);
								ll_umoneyPwd.setVisibility(View.GONE);
								ll_payMethod.setVisibility(View.VISIBLE);
							}
						}
					});
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Event(R.id.btn_umoneyPay)
	private void umoneyPay(View v){
		if(umoneysy <= 0){
			CommonUtil.alter("你的余额不足，请先充值！");return ;
		}
		if(et_password.getText().length()<1){
			CommonUtil.alter("支付密码不能为空！");return ;
		}

		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("orderNo", orderNo);
		maps.put("orderType", "mall");
		maps.put("orderId", id);
		maps.put("password", et_password.getText().toString());
		XUtilsHelper.getInstance().post("app/doUmoneyPay.htm", maps,new XUtilsHelper.XCallBack(){

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
						Intent intent = new Intent(getApplicationContext(),MyOrderInfoActivity.class);
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
