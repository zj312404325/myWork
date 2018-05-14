package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
	private String hascredit;
	private String orderdtlProcId;
	
	@ViewInject(R.id.tv_haspwd)
	private TextView tv_haspwd;
	
	private double umoney=0; //余额
	private double umoneysy=0; //剩余余额
	private double money=0; //非定制配支付金额
	private double orderPayMoney=0; //定制配支付金额
	private double paidMoney=0; //已付金额

	@ViewInject(R.id.tv_money)
	private TextView tv_money;
	
	@ViewInject(R.id.tv_umoney)
	private TextView tv_umoney;
	
	@ViewInject(R.id.tv_leftUmoney)
	private TextView tv_leftUmoney;

	@ViewInject(R.id.tv_orderNo)
	private TextView tv_orderNo;

	@ViewInject(R.id.tv_orderMoney)
	private TextView tv_orderMoney;

	@ViewInject(R.id.tv_paidMoney)
	private TextView tv_paidMoney;
	
	@ViewInject(R.id.et_password)
	private EditText et_password;

	@ViewInject(R.id.btn_umoneyPay)
	private Button btn_umoneyPay;
	
	@ViewInject(R.id.btn_recharge)
	private Button btn_recharge;

	@ViewInject(R.id.cb_selectUmoney)
	private CheckBox cb_selectUmoney;

	@ViewInject(R.id.ll_leftUmoney)
	private LinearLayout ll_leftUmoney;

	@ViewInject(R.id.ll_umoneyPwd)
	private LinearLayout ll_umoneyPwd;

	@ViewInject(R.id.ll_payMethod)
	private LinearLayout ll_payMethod;

	@ViewInject(R.id.rl_payPwd)
	private RelativeLayout rl_payPwd;


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
				final JSONObject res;
				try {
					res = new JSONObject(result);
					setServerKey(res.get("serverKey").toString());
					if(res.get("d").equals("n")){
						CommonUtil.alter(res.get("msg").toString());
					}
					else{
						adminmobile = res.getString("adminmobile");
						hascredit= res.getString("hascredit");
						orderdtlProcId= res.getString("orderdtlProcId");
						orderNo = res.getJSONObject("order").getString("orderNo");
						id = res.getJSONObject("order").getString("iD");
						String haspwd = res.getString("haspwd");
						if(haspwd.equals("1")){
							tv_haspwd.setText("如需修改密码,点击此处设置");
							rl_payPwd.setVisibility(View.VISIBLE);
						}
						else{
							tv_haspwd.setText("你还没有支付密码,点击此处设置");
							rl_payPwd.setVisibility(View.GONE);
						}
						umoney = FormatUtil.toDouble(res.getString("umoney"));
						money = FormatUtil.toDouble(res.getJSONObject("order").getString("money"));
						orderPayMoney = FormatUtil.toDouble(res.getJSONObject("order").getString("orderPayMoney"));
						paidMoney=FormatUtil.toDouble(res.getJSONObject("order").getString("getMoney"));
						umoneysy = BigDecimalUtil.sub(umoney, orderPayMoney,2);
						if(umoneysy<0){
							umoneysy=0d;
						}
						tv_orderNo.setText(FormatUtil.toString(orderNo));
						tv_umoney.setText(FormatUtil.toString(umoney)+"元");
						tv_leftUmoney.setText(FormatUtil.toString(umoneysy)+"元");
						tv_paidMoney.setText(Html.fromHtml("<font color=\"#333333\">已付金额：</font>¥"+FormatUtil.toString(paidMoney)));

						if(res.getJSONObject("order").getString("orderType").equals("orderMatch") || res.getJSONObject("order").getString("orderType").equals("fabOrderMatch")){
							tv_orderMoney.setText(Html.fromHtml("<font color=\"#333333\">交易金额：</font>¥"+FormatUtil.toString(orderPayMoney)));
							tv_money.setText(FormatUtil.toString(orderPayMoney));
						}
						else{
							tv_orderMoney.setText(Html.fromHtml("<font color=\"#333333\">交易金额：</font>¥"+FormatUtil.toString(money)));
							tv_money.setText(FormatUtil.toString(money));
						}
						hideUmoneyInfo();
					}

					//给CheckBox设置事件监听
					cb_selectUmoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// TODO Auto-generated method stub
							if(isChecked){//选中
								try{
									showUmoneyInfo();
									if(res.getJSONObject("order").getString("orderType").equals("orderMatch") || res.getJSONObject("order").getString("orderType").equals("fabOrderMatch")){
										if(umoney>=orderPayMoney){
											tv_money.setText("0.00");
										}
										else{
											tv_money.setText(FormatUtil.toString(BigDecimalUtil.sub(orderPayMoney,umoney,2)));
											tv_leftUmoney.setText("0.00"+"元");
											hideAll();
										}
									}
									else{
										if(umoney>=money){
											tv_money.setText("0.00");
										}
										else{
											tv_money.setText(FormatUtil.toString(BigDecimalUtil.sub(money,umoney,2)));
											tv_leftUmoney.setText("0.00"+"元");
											hideAll();
										}
									}
								}
								catch (JSONException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else{//取消选中
								try {
									if (res.getJSONObject("order").getString("orderType").equals("orderMatch") || res.getJSONObject("order").getString("orderType").equals("fabOrderMatch")) {
										tv_money.setText(FormatUtil.toString(orderPayMoney));
									} else {
										tv_money.setText(FormatUtil.toString(money));
									}
									hideUmoneyInfo();
								}
								catch (JSONException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
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
		if(umoneysy <0){
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
						return;
						//MyApplication.getInstance().finishActivity();
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

	@Event(R.id.rl_onlinePay)
	private void onlinePayClick(View v){
		Intent i =  new Intent(getApplicationContext(), OnlinePayActivity.class);
		i.putExtra("orderNo", orderNo);
		i.putExtra("id", id);
		i.putExtra("totalMoney",tv_money.getText().toString());
		i.putExtra("orderdtlProcId",orderdtlProcId);
		i.putExtra("adminmobile",adminmobile);
		startActivityForResult(i,CommonUtil.getInt(R.string.RECODE_DOWNPAY));
	}

	@Event(R.id.rl_offlinePay)
	private void offlinePayClick(View v){
		Intent i =  new Intent(getApplicationContext(), OfflinePayActivity.class);
		i.putExtra("orderNo", orderNo);
		i.putExtra("id", id);
		startActivityForResult(i,CommonUtil.getInt(R.string.RECODE_DOWNPAY));
	}

	@Event(R.id.rl_creditPay)
	private void creditPayClick(View v){
		Intent i =  new Intent(getApplicationContext(), CreditPayActivity.class);
		i.putExtra("orderNo", orderNo);
		i.putExtra("hascredit", hascredit);
		i.putExtra("id", id);
		startActivityForResult(i,CommonUtil.getInt(R.string.RECODE_DOWNPAY));
	}

	@Event(R.id.btn_recharge)
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
				Intent intent = new Intent(getApplicationContext(),MyOrderInfoActivity.class);
				intent.putExtra("id", id);
				startActivity(intent);
				finish();
			}
		}
	}

	private void showUmoneyInfo(){
		hideAll();
		ll_leftUmoney.setVisibility(View.VISIBLE);
		ll_umoneyPwd.setVisibility(View.VISIBLE);
		btn_umoneyPay.setVisibility(View.VISIBLE);
	}

	private void hideUmoneyInfo(){
		hideAll();
		ll_payMethod.setVisibility(View.VISIBLE);
	}

	private void hideAll(){
		ll_leftUmoney.setVisibility(View.GONE);
		ll_umoneyPwd.setVisibility(View.GONE);
		ll_payMethod.setVisibility(View.GONE);
		btn_umoneyPay.setVisibility(View.GONE);
	}
}
