package com.example.administrator.helloworld;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.helloworld.common.ButtomTapActivity;
import com.example.administrator.helloworld.common.MyApplication;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.FormatUtil;
import com.example.administrator.helloworld.util.XUtilsHelper;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@ContentView(R.layout.activity_user_center)
public class UserCenterActivity extends ButtomTapActivity {
	@ViewInject(R.id.top_back)
	private ImageButton top_back;

	@ViewInject(R.id.uc_info)
	private RelativeLayout uc_info;
	
	@ViewInject(R.id.orderxh)
	private RelativeLayout orderxh;
	@ViewInject(R.id.orderqh)
	private RelativeLayout orderqh;
	@ViewInject(R.id.orderjg)
	private RelativeLayout orderjg;
	
	@ViewInject(R.id.zhgl)
	private Button zhgl;
	
	@ViewInject(R.id.tv_xh1)
	private TextView tv_xh1;
	@ViewInject(R.id.tv_xh2)
	private TextView tv_xh2;
	@ViewInject(R.id.tv_xh3)
	private TextView tv_xh3;
	@ViewInject(R.id.tv_xh4)
	private TextView tv_xh4;
	
	@ViewInject(R.id.tv_qh1)
	private TextView tv_qh1;
	@ViewInject(R.id.tv_qh2)
	private TextView tv_qh2;
	@ViewInject(R.id.tv_qh3)
	private TextView tv_qh3;
	@ViewInject(R.id.tv_qh4)
	private TextView tv_qh4;
	
	@ViewInject(R.id.tv_jg1)
	private TextView tv_jg1;
	@ViewInject(R.id.tv_jg2)
	private TextView tv_jg2;
	@ViewInject(R.id.tv_jg3)
	private TextView tv_jg3;
	@ViewInject(R.id.tv_jg4)
	private TextView tv_jg4;
	
	@ViewInject(R.id.rl_xh1)
	private RelativeLayout rl_xh1;
	@ViewInject(R.id.rl_xh2)
	private RelativeLayout rl_xh2;
	@ViewInject(R.id.rl_xh3)
	private RelativeLayout rl_xh3;
	@ViewInject(R.id.rl_xh4)
	private RelativeLayout rl_xh4;
	
	@ViewInject(R.id.rl_qh1)
	private RelativeLayout rl_qh1;
	@ViewInject(R.id.rl_qh2)
	private RelativeLayout rl_qh2;
	@ViewInject(R.id.rl_qh3)
	private RelativeLayout rl_qh3;
	@ViewInject(R.id.rl_qh4)
	private RelativeLayout rl_qh4;
	
	@ViewInject(R.id.rl_jg1)
	private RelativeLayout rl_jg1;
	@ViewInject(R.id.rl_jg2)
	private RelativeLayout rl_jg2;
	@ViewInject(R.id.rl_jg3)
	private RelativeLayout rl_jg3;
	@ViewInject(R.id.rl_jg4)
	private RelativeLayout rl_jg4;
	
	@ViewInject(R.id.heador)
	private ImageView heador;
	@ViewInject(R.id.comp)
	private TextView comp;
	
	private JSONObject user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("这尼玛", "基老陈成型了");
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		parentControl();
		getDate();
		user = FormatUtil.toJSONObject(super.getUser());
		setUsr();
	}
	
	/**
	 * 设置用户信息
	 * @throws JSONException 
	 */
	private void setUsr(){
		if(user != null){
			try {
				XUtilsHelper.getInstance().bindCommonImage(heador, user.getString("heador"), true);
				comp.setText(user.getString("username"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private void getDate(){
		try{
			Map<String, String> maps= new HashMap<String, String>();
			maps.put("serverKey", super.serverKey);
			XUtilsHelper.getInstance().post("app/initUserCenter.htm", maps,new XUtilsHelper.XCallBack(){
	
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
							if(res.getString("buyPayNum").equals("0")){
								tv_xh1.setVisibility(View.GONE);
							}
							else{
								tv_xh1.setText(res.getString("buyPayNum"));
							}
							if(res.getString("buyRecNum").equals("0")){
								tv_xh2.setVisibility(View.GONE);
							}
							else{
								tv_xh2.setText(res.getString("buyRecNum"));
							}
							if(res.getString("buyEndNum").equals("0")){
								tv_xh3.setVisibility(View.GONE);
							}
							else{
								tv_xh3.setText(res.getString("buyEndNum"));
							}
							if(res.getString("buyPermitNum").equals("0")){
								tv_xh4.setVisibility(View.GONE);
							}
							else{
								tv_xh4.setText(res.getString("buyPermitNum"));
							}
							//期货
							if(res.getString("qhbuyPayNum").equals("0")){
								tv_qh1.setVisibility(View.GONE);
							}
							else{
								tv_qh1.setText(res.getString("qhbuyPayNum"));
							}
							if(res.getString("qhbuyRecNum").equals("0")){
								tv_qh2.setVisibility(View.GONE);
							}
							else{
								tv_qh2.setText(res.getString("qhbuyRecNum"));
							}
							if(res.getString("qhbuyEndNum").equals("0")){
								tv_qh3.setVisibility(View.GONE);
							}
							else{
								tv_qh3.setText(res.getString("qhbuyEndNum"));
							}
							if(res.getString("qhbuyPermitNum").equals("0")){
								tv_qh4.setVisibility(View.GONE);
							}
							else{
								tv_qh4.setText(res.getString("qhbuyPermitNum"));
							}
							//加工
							if(res.getString("jgbuyPayNum").equals("0")){
								tv_jg1.setVisibility(View.GONE);
							}
							else{
								tv_jg1.setText(res.getString("jgbuyPayNum"));
							}
							if(res.getString("jgbuyRecNum").equals("0")){
								tv_jg2.setVisibility(View.GONE);
							}
							else{
								tv_jg2.setText(res.getString("jgbuyRecNum"));
							}
							if(res.getString("jgbuyEndNum").equals("0")){
								tv_jg3.setVisibility(View.GONE);
							}
							else{
								tv_jg3.setText(res.getString("jgbuyEndNum"));
							}
							if(res.getString("jgbuyPermitNum").equals("0")){
								tv_jg4.setVisibility(View.GONE);
							}
							else{
								tv_jg4.setText(res.getString("jgbuyPermitNum"));
							}
							
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			});
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	
	@Event(value={R.id.uc_info,R.id.zhgl},type=View.OnTouchListener.class)
	private boolean ucinfoTouch(View v, MotionEvent event){
		Log.i("这尼玛", "基老陈正在启动: ");
		if (event.getAction() == event.ACTION_UP) {
			Intent i = new Intent(getApplicationContext(),UserInfoActivity.class);
			startActivity(i);
			return false;
		}
		return true;
	}
	
	@Event(value={R.id.orderxh,R.id.rl_xh1,R.id.rl_xh2,R.id.rl_xh3,R.id.rl_xh4},type=View.OnTouchListener.class)
	private boolean orderxhTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(v.getId() == R.id.orderxh){
				startActivity(new Intent(getApplicationContext(),Order_Product_Activity.class));
			}
			else if(v.getId() == R.id.rl_xh1){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "1");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_xh2){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "3");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_xh3){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "4");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_xh4){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "6");
				startActivity(i);
			}			
			return false;
		}
		return true;
	}
	
	@Event(value={R.id.orderqh,R.id.rl_qh1,R.id.rl_qh2,R.id.rl_qh3,R.id.rl_qh4},type=View.OnTouchListener.class)
	private boolean orderqhTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(v.getId() == R.id.orderqh){
				startActivity(new Intent(getApplicationContext(),Order_Product_Activity.class));
			}
			else if(v.getId() == R.id.rl_qh1){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "1");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_qh2){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "3");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_qh3){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "4");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_qh4){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "6");
				startActivity(i);
			}			
			return false;
		}
		return true;
	}
	
	@Event(value={R.id.orderjg,R.id.rl_jg1,R.id.rl_jg2,R.id.rl_jg3,R.id.rl_jg4},type=View.OnTouchListener.class)
	private boolean orderjgTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(v.getId() == R.id.orderjg){
				startActivity(new Intent(getApplicationContext(),Order_Product_Activity.class));
			}
			else if(v.getId() == R.id.rl_jg1){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "1");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_jg2){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "3");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_jg3){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "4");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_jg4){
				Intent i = new Intent(getApplicationContext(),Order_Product_Activity.class);
				i.putExtra("orderStatus", "6");
				startActivity(i);
			}			
			return false;
		}
		return true;
	}
	

	/**
	 * 初始化控件
	 */
	private void parentControl(){
		super.imageView5.setImageResource(R.drawable.buttom_tap_5s);
	}
	
	@Event(R.id.top_back)
	private void toSetting(View v){
		startActivity(new Intent(getApplicationContext(),SettingActivity.class));
	}

}
