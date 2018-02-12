package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.MyApplication;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_bank)
public class BankOrderActivity extends TopActivity {

	@ViewInject(R.id.addbtn)
	private Button addbtn;
	
	private List<Map<String, Object>> resMaps = new ArrayList<Map<String, Object>>();
	
	private SimpleAdapter sap;
	@ViewInject(R.id.mlv_addrlist)
	private MyListView mlv_addrlist;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("银行管理");		
		progressDialog.hide();
		sap = new ProSimpleAdapter(this, resMaps, R.layout.listview_bank, 
				new String[]{"bankName"}, 
				new int[]{R.id.tv_b_bankName});
		mlv_addrlist.setAdapter(sap);		
		getDate();
	}

	private void getDate(){
		progressDialog.show();;
		resMaps.clear();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		XUtilsHelper.getInstance().post("app/getBankList.htm", maps,new XUtilsHelper.XCallBack(){

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
						JSONArray jarr = res.getJSONArray("data");
						for(int i=0;i<jarr.length();i++){
							Map<String,Object> maptemp = new HashMap<String, Object>();
							JSONObject jobj = jarr.getJSONObject(i);
							maptemp.put("bank", jobj.toString());
							maptemp.put("bankdefault", jobj.get("bankdefault"));
							maptemp.put("id", jobj.get("id"));
							maptemp.put("bankName", jobj.get("bankName"));
							resMaps.add(maptemp);
						}
						sap.notifyDataSetChanged();
						
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		
	}
	
	public class ProSimpleAdapter  extends SimpleAdapter {		
		private LayoutInflater mInflater;		
		public ProSimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			try{
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.listview_bank, null); 
				}
				TextView tv_b_bankName = (TextView)convertView.findViewById(R.id.tv_b_bankName);
				TextView tv_b_bankAdd = (TextView)convertView.findViewById(R.id.tv_b_bankAdd);
				TextView tv_b_bankNo = (TextView)convertView.findViewById(R.id.tv_b_bankNo);		
				
				RadioButton rb_bankdefault = (RadioButton)convertView.findViewById(R.id.rb_bankdefault);
				Button btn_select = (Button)convertView.findViewById(R.id.btn_select);
				Button btn_edit = (Button)convertView.findViewById(R.id.btn_edit);
				Button btn_del = (Button)convertView.findViewById(R.id.btn_del);


				JSONObject cJobj = new JSONObject( resMaps.get(position).get("bank").toString());
				
				tv_b_bankName.setText(cJobj.getString("bankName"));
				tv_b_bankAdd.setText(cJobj.getString("bankAdd"));
				tv_b_bankNo.setText(cJobj.getString("bankNo"));
				
				rb_bankdefault.setChecked(FormatUtil.toBoolean(resMaps.get(position).get("bankdefault").toString()));
				btn_select.setVisibility(View.VISIBLE);
				btn_select.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent mIntent = new Intent();
						mIntent.putExtra("bank", resMaps.get(position).get("bank").toString()); 
						setResult(33, mIntent);
						MyApplication.getInstance().finishActivity();
					}
				});
				rb_bankdefault.setOnClickListener(new View.OnClickListener() {					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						setDefaultAddress(resMaps.get(position).get("id").toString());
					}
				});
				btn_del.setOnClickListener(new View.OnClickListener() {					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						delAddress(resMaps.get(position).get("id").toString(),position);
					}
				});
				btn_edit.setOnClickListener(new View.OnClickListener() {					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(getApplicationContext(),BankInfoActivity.class);
						intent.putExtra("bank", resMaps.get(position).get("bank").toString());
						startActivityForResult(intent,11);
					}
				});

				
			}
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}
	}
	
	private void delAddress(final String id,final int i){
		progressDialog.show();;
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("id", id);
		XUtilsHelper.getInstance().post("app/delBankAccount.htm", maps,new XUtilsHelper.XCallBack(){

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
						resMaps.remove(i);						
						sap.notifyDataSetChanged();						
					}					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			
		});
	}
	
	private void setDefaultAddress(final String id){
		progressDialog.show();;
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("id", id);
		XUtilsHelper.getInstance().post("app/setDefaultBankAccount.htm", maps,new XUtilsHelper.XCallBack(){

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
						
						for(int i=0;i<resMaps.size();i++){
							if(resMaps.get(i).get("id").toString().equals(id)){
								resMaps.get(i).put("bankdefault", "1");							
							}
							else{
								resMaps.get(i).put("bankdefault", "0");
							}
						}
						sap.notifyDataSetChanged();						
					}					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			
		});
	}
	
	@Event(R.id.addbtn)
	private void addClick(View v){
		startActivityForResult(new Intent(getApplicationContext(),BankInfoActivity.class),11);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		   case 11: //地址
			   getDate();
		   break;
		   default:
		   break;
		}
	}

}
