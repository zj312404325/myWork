package com.example.administrator.jymall;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@ContentView(R.layout.activity_tx_zx)
public class TxZxActivity  extends TopActivity {

	private String banklist;
	private double ktxje;
	
	private String bankselect="";
	private JSONArray banklistarr;
	
	@ViewInject(R.id.tv_ktxje)
	private TextView tv_ktxje;
	@ViewInject(R.id.rl_bankselect)
	private RelativeLayout rl_bankselect;
	@ViewInject(R.id.tv_bankselect)
	private TextView tv_bankselect;
	@ViewInject(R.id.et_tixianMoney)
	private EditText et_tixianMoney;
	@ViewInject(R.id.savebtn)
	private Button savebtn;
	
	private SimpleAdapter sapd;
	
	private JSONObject bankAccount = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("在线提现申请");	
		Intent i = this.getIntent();
		banklist = i.getStringExtra("banklist");
		banklistarr = FormatUtil.toJSONArray(banklist);
		ktxje = i.getDoubleExtra("ktxje", 0);
		tv_ktxje.setText("最多可提现金额："+ktxje+"元");
		super.progressDialog.hide();
		
		
		rl_bankselect.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction() == KeyEvent.ACTION_UP){
					Intent i = new Intent(getApplicationContext(),BankOrderActivity.class);
					startActivityForResult(i, 33);
					return false;
				}
				return true;
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		   case 33: //发票
			   Bundle b3=data.getExtras(); //data为B中回传的Intent
			   try{
				   bankAccount= FormatUtil.toJSONObject( b3.getString("bank"));//str即为回传的值
				   try{
						if(bankAccount != null){
							tv_bankselect.setText(bankAccount.getString("bankName")+":"+bankAccount.getString("bankNo"));
							bankselect = bankAccount.getString("id");
						}
					}
					catch(Exception ep){ep.printStackTrace();}
			   }
			   catch(Exception ep){ep.printStackTrace();}
			break;
		   default:
		   break;
		}
	}
	
	@Event(R.id.savebtn)
	private void txclick(View v){
		if(bankselect.equals("")){
			CommonUtil.alter("请选择提现的银行账户！");return;
		}
		double tixianMoney = FormatUtil.toDouble(et_tixianMoney.getText());
		if(tixianMoney == 0){
			CommonUtil.alter("提现金额不能为零！");return;
		}
		if(tixianMoney>ktxje){
			CommonUtil.alter("提现金额不能超过"+ktxje+"！");return;
		}
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("bankselect", bankselect);
		maps.put("tixianMoney", et_tixianMoney.getText().toString());
		XUtilsHelper.getInstance().post("app/moneytixian.htm", maps,new XUtilsHelper.XCallBack(){

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
						setResult(CommonUtil.getInt(R.string.RECODE_TX));
						CommonUtil.alter("提现成功，请耐心等待");
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
