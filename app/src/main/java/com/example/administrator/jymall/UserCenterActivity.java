package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.jymall.common.ButtomTapActivity;
import com.example.administrator.jymall.common.MyApplication;
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

@ContentView(R.layout.activity_user_center)
public class UserCenterActivity extends ButtomTapActivity {
	@ViewInject(R.id.top_back)
	private ImageButton top_back;

	@ViewInject(R.id.uc_info)
	private RelativeLayout uc_info;

	@ViewInject(R.id.tv_xh1)
	private TextView tv_xh1;
	@ViewInject(R.id.tv_xh2)
	private TextView tv_xh2;
	@ViewInject(R.id.tv_xh3)
	private TextView tv_xh3;
	@ViewInject(R.id.tv_xh4)
	private TextView tv_xh4;

	@ViewInject(R.id.heador)
	private ImageView heador;
	@ViewInject(R.id.comp)
	private TextView comp;
	
	private JSONObject user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			XUtilsHelper.getInstance().post("app/initMallUserCenter.htm", maps,new XUtilsHelper.XCallBack(){
	
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
							if(res.getString("countAll").equals("0")){
								tv_xh1.setVisibility(View.GONE);
							}
							else{
								tv_xh1.setText(res.getString("countAll"));
							}
							if(res.getString("countFast").equals("0")){
								tv_xh2.setVisibility(View.GONE);
							}
							else{
								tv_xh2.setText(res.getString("countFast"));
							}
							if(res.getString("countOrder").equals("0")){
								tv_xh3.setVisibility(View.GONE);
							}
							else{
								tv_xh3.setText(res.getString("countOrder"));
							}
							if(res.getString("countCommon").equals("0")){
								tv_xh4.setVisibility(View.GONE);
							}
							else{
								tv_xh4.setText(res.getString("countCommon"));
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

	@Event(value={R.id.rl_allOrder,R.id.rl_fastMatch,R.id.rl_orderMatch,R.id.rl_commonOrder},type=View.OnTouchListener.class)
	private boolean orderxhTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(v.getId() == R.id.rl_allOrder){
				Intent i = new Intent(getApplicationContext(),MyOrderActivity.class);
				i.putExtra("orderType", "");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_fastMatch){
				Intent i = new Intent(getApplicationContext(),MyOrderActivity.class);
				i.putExtra("orderType", "fastMatch");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_orderMatch){
				Intent i = new Intent(getApplicationContext(),MyOrderActivity.class);
				i.putExtra("orderType", "orderMatch");
				startActivity(i);
			}
			else if(v.getId() == R.id.rl_commonOrder){
				Intent i = new Intent(getApplicationContext(),MyOrderActivity.class);
				i.putExtra("orderType", "product");
				startActivity(i);
			}
			return false;
		}
		return true;
	}
	

	@Event(value=R.id.rl_myMatch,type=View.OnTouchListener.class)
	private boolean myMatchTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			startActivity(new Intent(getApplicationContext(),MyMatchIndexActivity.class));
			return false;
		}
		return true;
	}

	@Event(value=R.id.rl_userCenter,type=View.OnTouchListener.class)
	private boolean userCenterTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			startActivity(new Intent(getApplicationContext(),UserInfoActivity.class));
			return false;
		}
		return true;
	}

	@Event(value=R.id.rl_myScore,type=View.OnTouchListener.class)
	private boolean myScoreTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			startActivity(new Intent(getApplicationContext(),MyScoreIndexActivity.class));
			return false;
		}
		return true;
	}

	@Event(value=R.id.rl_myCredit,type=View.OnTouchListener.class)
	private boolean myCreditTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			startActivity(new Intent(getApplicationContext(),MyCreditIndexActivity.class));
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
