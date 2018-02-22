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

@ContentView(R.layout.activity_address)
public class AddressActivity extends TopActivity {

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
		super.title.setText("收货地址管理");		
		progressDialog.hide();
		sap = new ProSimpleAdapter(this, resMaps, R.layout.listview_address, 
				new String[]{"contact"}, 
				new int[]{R.id.tv_Contact});
		mlv_addrlist.setAdapter(sap);		
		getDate();
	}
	
	private void getDate(){
		progressDialog.show();;
		resMaps.clear();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		XUtilsHelper.getInstance().post("app/getAddressList.htm", maps,new XUtilsHelper.XCallBack(){

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
							maptemp.put("address", jobj.toString());
							maptemp.put("addrdefault", jobj.getBoolean("addrdefault"));
							maptemp.put("contact", jobj.get("contact"));
							maptemp.put("id", jobj.get("id"));
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
					convertView=mInflater.inflate(R.layout.listview_address, null); 
				}
				TextView tv_Contact = (TextView)convertView.findViewById(R.id.tv_Contact);
				TextView tv_MobilePhone = (TextView)convertView.findViewById(R.id.tv_MobilePhone);
				TextView tv_Province = (TextView)convertView.findViewById(R.id.tv_Province);
				TextView tv_Addr = (TextView)convertView.findViewById(R.id.tv_Addr);
				TextView tv_Postcode = (TextView)convertView.findViewById(R.id.tv_Postcode);
				RadioButton rb_AddrDefault = (RadioButton)convertView.findViewById(R.id.rb_AddrDefault);
				Button btn_select = (Button)convertView.findViewById(R.id.btn_select);
				TextView tv_icon_edit = (TextView)convertView.findViewById(R.id.tv_icon_edit);
				TextView tv_icon_del = (TextView)convertView.findViewById(R.id.tv_icon_del);
				
				JSONObject cJobj = new JSONObject( resMaps.get(position).get("address").toString());
				
				tv_Contact.setText(cJobj.getString("contact"));
				tv_MobilePhone.setText(cJobj.getString("mobilephone"));
				tv_Province.setText(cJobj.getString("province")
						+cJobj.getString("city")
						+cJobj.getString("district"));
				tv_Addr.setText(cJobj.getString("addr"));
				tv_Postcode.setText(cJobj.getString("postcode"));
				rb_AddrDefault.setChecked(new Boolean(resMaps.get(position).get("addrdefault").toString()));
				
				btn_select.setVisibility(View.INVISIBLE);
				
				rb_AddrDefault.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						setDefaultAddress(resMaps.get(position).get("id").toString());
					}
				});
				tv_icon_del.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						delAddress(resMaps.get(position).get("id").toString(),position);
					}
				});
				tv_icon_edit.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(getApplicationContext(),AddressInfoActivity.class);
						intent.putExtra("address", resMaps.get(position).get("address").toString());
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
		XUtilsHelper.getInstance().post("app/delAddress.htm", maps,new XUtilsHelper.XCallBack(){

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
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("id", id);
		XUtilsHelper.getInstance().post("app/setDefaultAddress.htm", maps,new XUtilsHelper.XCallBack(){

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
								resMaps.get(i).put("addrdefault", true);							
							}
							else{
								resMaps.get(i).put("addrdefault", false);
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
		startActivityForResult(new Intent(getApplicationContext(),AddressInfoActivity.class),11);
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
