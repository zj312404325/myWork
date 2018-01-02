package com.example.administrator.jymall;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.XListView;
import com.example.administrator.jymall.view.XListView.IXListViewListener;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@ContentView(R.layout.activity_user_money)
public class UserMoneyActivity  extends TopActivity  implements IXListViewListener {

	
	

	@ViewInject(R.id.xListView)
	public XListView listViewAll = null ;
	
	public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
	private SimpleAdapter sap;
	private Handler mHandler;
	@ViewInject(R.id.listtv)
	private TextView listtv;
	
	private String s_date = "1";
	
	private int start = 1;
	
	@ViewInject(R.id.tv_umoney)
	private TextView tv_umoney;
	@ViewInject(R.id.tv_frozenmoney)
	private TextView tv_frozenmoney;
	@ViewInject(R.id.tv_inMoney)
	private TextView tv_inMoney;
	@ViewInject(R.id.tv_outMoney)
	private TextView tv_outMoney;
	@ViewInject(R.id.btn_s1)
	private Button btn_s1;
	@ViewInject(R.id.btn_s2)
	private Button btn_s2;
	@ViewInject(R.id.btn_s3)
	private Button btn_s3;
	@ViewInject(R.id.btn_cz)
	private Button btn_cz;
	@ViewInject(R.id.btn_tx)
	private Button btn_tx;
	
	private String adminmobile;
	private String banklist;
	private double ktxje;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("资金账户中心");
		
		sap = new ProSimpleAdapter(UserMoneyActivity.this, dateMaps, 
				R.layout.product_listview, 
				new String[]{"inmoney"}, 
				new int[]{R.id.tv_inmoney});
		listViewAll.setAdapter(sap);
		listViewAll.setPullLoadEnable(true);
		listViewAll.setXListViewListener(this);
		getDate(true,true);
		mHandler = new Handler();
	}
	
	@Event(R.id.btn_cz)
	private void rechargeClick(View v){
		Intent i =  new Intent(getApplicationContext(), RechargeActivity.class);	
		startActivityForResult(i, CommonUtil.getInt(R.string.RECODE_RECHARGE));
	}
	
	@Event(R.id.btn_tx)
	private void txClick(View v){
		Intent i =  new Intent(getApplicationContext(), TXActivity.class);
		i.putExtra("ktxje",ktxje);
		i.putExtra("adminmobile", adminmobile);
		i.putExtra("banklist", banklist);
		startActivityForResult(i, CommonUtil.getInt(R.string.RECODE_TX));
	}
	
	@Event(value={R.id.btn_s1,R.id.btn_s2,R.id.btn_s3})
	private void btnclick(View v){
		if(v.getId() == R.id.btn_s1){
			s_date = "1";
			btn_s1.setTextAppearance(getApplicationContext(), R.style.btnumlefts);
			btn_s1.setBackgroundResource(R.color.bg_btn_usermoneylefts);
			btn_s2.setTextAppearance(getApplicationContext(), R.style.btnummibble);
			btn_s2.setBackgroundResource(R.color.bg_btn_usermoneymibble);
			btn_s3.setTextAppearance(getApplicationContext(), R.style.btnumright);
			btn_s3.setBackgroundResource(R.color.bg_btn_usermoneyright);
		}
		else if(v.getId() == R.id.btn_s2){
			s_date = "2";
			btn_s1.setTextAppearance(getApplicationContext(), R.style.btnumleft);
			btn_s1.setBackgroundResource(R.color.bg_btn_usermoneyleft);
			btn_s2.setTextAppearance(getApplicationContext(), R.style.btnummibbles);
			btn_s2.setBackgroundResource(R.color.bg_btn_usermoneymibbles);
			btn_s3.setTextAppearance(getApplicationContext(), R.style.btnumright);
			btn_s3.setBackgroundResource(R.color.bg_btn_usermoneyright);
		}
		else if(v.getId() == R.id.btn_s3){
			s_date = "3";
			btn_s1.setTextAppearance(getApplicationContext(), R.style.btnumleft);
			btn_s1.setBackgroundResource(R.color.bg_btn_usermoneyleft);
			btn_s2.setTextAppearance(getApplicationContext(), R.style.btnummibble);
			btn_s2.setBackgroundResource(R.color.bg_btn_usermoneymibble);
			btn_s3.setTextAppearance(getApplicationContext(), R.style.btnumrights);
			btn_s3.setBackgroundResource(R.color.bg_btn_usermoneyrights);
		}
		getDate(true,true);
	}
	
	private void getDate(final boolean isShow,final boolean flag){
		if(isShow){progressDialog.show();};
		if(flag){ dateMaps.clear();start = 1;}
		listtv.setVisibility(View.GONE);
		listViewAll.setPullLoadEnable(false);
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("currentPage", ""+start);
		maps.put("s_date", s_date);		
		
		XUtilsHelper.getInstance().post("app/bankAccountIndex.htm", maps,new XUtilsHelper.XCallBack(){

			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				listtv.setVisibility(View.GONE);
				if(isShow){progressDialog.hide();};
				JSONObject res;
				try {
					res = new JSONObject(result);
					setServerKey(res.get("serverKey").toString());
					if(res.get("d").equals("n")){
						CommonUtil.alter(res.get("msg").toString());
						finish();
						return;
					}
					JSONArray resjarr = (JSONArray)res.get("list");
					if(resjarr.length()==0 && start == 1)
						listtv.setVisibility(View.VISIBLE);
					else if(resjarr.length() == 8 )
						listViewAll.setPullLoadEnable(true);
					tv_umoney.setText(FormatUtil.toString( res.getDouble("umoney")));
					tv_frozenmoney.setText(res.getString("frozenmoney"));
					tv_inMoney.setText(FormatUtil.toString( res.getDouble("inMoney")));
					tv_outMoney.setText(FormatUtil.toString( res.getDouble("outMoney")));
					adminmobile = res.getString("adminmobile");
					banklist = res.getString("banklist");
					ktxje = res.getDouble("ktxje");
					for(int i=0;i<resjarr.length();i++){
						Map<String, Object> dateMap = new HashMap<String, Object>();						
						dateMap.put("createdate", resjarr.getJSONObject(i).get("createdate"));
						dateMap.put("inmoney", resjarr.getJSONObject(i).get("inmoney"));
						dateMap.put("mtype", resjarr.getJSONObject(i).get("mtype"));
						dateMap.put("ystatus", resjarr.getJSONObject(i).get("ystatus"));
						dateMap.put("ystatusName", resjarr.getJSONObject(i).get("ystatusName"));
						dateMap.put("id", resjarr.getJSONObject(i).get("id"));
						dateMaps.add(dateMap);						
					}
					sap.notifyDataSetChanged();
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
					convertView=mInflater.inflate(R.layout.listview_usermoney, null); 
				} 
				TextView tv_inmoney = (TextView)convertView.findViewById(R.id.tv_inmoney);
				TextView tv_createdate = (TextView)convertView.findViewById(R.id.tv_createdate);
				TextView tv_mtype = (TextView)convertView.findViewById(R.id.tv_mtype);
				TextView tv_ystatus = (TextView)convertView.findViewById(R.id.tv_ystatus);
				String inmoney = dateMaps.get(position).get("inmoney").toString();
				String ystatus = dateMaps.get(position).get("ystatus").toString();
				final String id =  dateMaps.get(position).get("id").toString();
				tv_inmoney.setText(inmoney);
				if(inmoney.indexOf("+")>-1){
					tv_inmoney.setTextColor(Color.parseColor("#42bd67"));
					tv_mtype.setTextColor(Color.parseColor("#42bd67"));
				}
				else{
					tv_inmoney.setTextColor(Color.parseColor("#e16262"));
					tv_mtype.setTextColor(Color.parseColor("#e16262"));
				}
				
				tv_createdate.setText(dateMaps.get(position).get("createdate").toString());
				tv_mtype.setText(dateMaps.get(position).get("mtype").toString());	
				if(ystatus.equals("2")){
					tv_ystatus.setText(dateMaps.get(position).get("ystatusName").toString()+" 关闭");
					tv_ystatus.setOnTouchListener(new View.OnTouchListener() {
						
						@Override
						public boolean onTouch(View arg0, MotionEvent event) {
							if (event.getAction() == event.ACTION_UP) {
										moneytixianclose(id);	
										return false;
							}
							return true;
						}
					});
				}
				else{
					tv_ystatus.setText(dateMaps.get(position).get("ystatusName").toString());
				}
				
			}
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}

		
	}
	
	private void onLoad() {
		listViewAll.stopRefresh();
		listViewAll.stopLoadMore();
		listViewAll.setRefreshTime(DateUtil.DateToString(new Date(), DateStyle.HH_MM_SS));
	}
	
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				start = 1;
				getDate(true,true);
				onLoad();
			}
		}, 1);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				start++;
				getDate(true,false);
				onLoad();
			}
		}, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode ==  CommonUtil.getInt(R.string.RECODE_TX)){
			getDate(true, true);
		}
		
	}
	
	private void moneytixianclose(final String id){							
		new  AlertDialog.Builder(this).setTitle("金赢网提醒" )  
				.setMessage("你确定要关闭提现吗？" )  
				.setPositiveButton("是" ,  new OnClickListener() {														
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						progressDialog.show();;
						Map<String, String> maps= new HashMap<String, String>();
						maps.put("serverKey", serverKey);
						maps.put("id", id);
						XUtilsHelper.getInstance().post("app/moneytixianclose.htm", maps,new XUtilsHelper.XCallBack(){

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
										CommonUtil.alter("提现关闭成功");
										getDate(true,true);
									}
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
							
						});
						
					}
				} )  
				.setNegativeButton("否" , new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
					}
				} )  
				.show(); 
	}

}
