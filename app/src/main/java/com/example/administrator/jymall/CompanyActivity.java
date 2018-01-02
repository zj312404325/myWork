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

import com.example.administrator.jymall.common.ButtomTapActivity;
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.XListView;
import com.example.administrator.jymall.view.XListView.IXListViewListener;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@ContentView(R.layout.activity_company)
public class CompanyActivity extends ButtomTapActivity implements IXListViewListener {
	
	
	@ViewInject(R.id.xListView)
	public XListView listViewAll = null ;
	@ViewInject(R.id.listtv)
	private TextView listtv;
	
	public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
	private SimpleAdapter sap;
	private Handler mHandler;
	private int start = 1;
	
	private String keyword = "";
	
	@ViewInject(R.id.search_btn)
	private Button search_btn;
	@ViewInject(R.id.txt_keyword)
	private TextView txt_keyword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		progressDialog.hide();
		
		sap = new ProSimpleAdapter(CompanyActivity.this, dateMaps, 
				R.layout.list_company, 
				new String[]{"comp"}, 
				new int[]{R.id.txt_comp});
		listViewAll.setAdapter(sap);
		listViewAll.setPullLoadEnable(true);
		listViewAll.setXListViewListener(this);
		getDate(true,true);
		mHandler = new Handler();
		
		parentControl();
	}
	
	@Event(value=R.id.search_btn)
	private void search_click(View v){
		keyword = txt_keyword.getText().toString();
		getDate(true,true);
	}
	
	
	private void getDate(final boolean isShow,final boolean flag){
		if(isShow){progressDialog.show();};
		if(flag){start = 1;}
		listtv.setVisibility(View.GONE);
		listViewAll.setPullLoadEnable(false);
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("currentPage", ""+start);		
		maps.put("keyword",keyword);	
		
		XUtilsHelper.getInstance().post("app/getCompanyJson.htm", maps,new XUtilsHelper.XCallBack(){

			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				listtv.setVisibility(View.GONE);
				if(flag){ dateMaps.clear();}
				if(isShow){progressDialog.hide();};
				JSONObject res;
				try {
					res = new JSONObject(result);
					setServerKey(res.get("serverKey").toString());
					JSONArray resjarr = (JSONArray)res.get("data");
					if(resjarr.length()==0 && start == 1)
						listtv.setVisibility(View.VISIBLE);
					else if(resjarr.length() ==  10 )
						listViewAll.setPullLoadEnable(true);
					
					for(int i=0;i<resjarr.length();i++){
						Map<String, Object> dateMap = new HashMap<String, Object>();
						dateMap.put("proNums", resjarr.getJSONObject(i).get("proNums"));
						dateMap.put("integral", resjarr.getJSONObject(i).get("integral"));
						dateMap.put("comp", resjarr.getJSONObject(i).get("comp"));
						dateMap.put("zone", resjarr.getJSONObject(i).get("zone"));
						dateMap.put("addr", resjarr.getJSONObject(i).get("addr"));
						dateMap.put("industryclass", resjarr.getJSONObject(i).get("industryclass"));
						dateMap.put("roleName", resjarr.getJSONObject(i).get("roleName"));
						dateMap.put("ispartner", resjarr.getJSONObject(i).get("ispartner"));
						dateMap.put("comptype", resjarr.getJSONObject(i).get("comptype"));
						dateMap.put("mobile", resjarr.getJSONObject(i).get("mobile"));
						dateMap.put("qq", resjarr.getJSONObject(i).get("qq"));
						dateMap.put("id", resjarr.getJSONObject(i).get("id"));
						dateMap.put("rank", resjarr.getJSONObject(i).get("rank"));
						dateMap.put("top1", resjarr.getJSONObject(i).get("top1"));
						dateMap.put("top3", resjarr.getJSONObject(i).get("top3"));
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
		
		public ViewHolder holder; 
		private LayoutInflater mInflater;
		
		private List<Map<String, Object>> myMaps;
		
		public ProSimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			myMaps = (List<Map<String, Object>>) data;
		}
		
		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
				holder=null;
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.list_company, null); 
					holder=new ViewHolder(); 
					x.view().inject(holder,convertView); 
					convertView.setTag(holder); 
				} 
				else{ 
					holder=(ViewHolder) convertView.getTag(); 
				} 
				holder.txt_proNums.setText(dateMaps.get(position).get("proNums").toString());

				//Log.v("PRO",dateMaps.get(position).get("pic").toString());
				String roleName = myMaps.get(position).get("roleName").toString();
				final String mobile = myMaps.get(position).get("mobile").toString();
				final String qq = myMaps.get(position).get("qq").toString();
				final String id = myMaps.get(position).get("id").toString();
				//推广
				int rank = FormatUtil.toInt(myMaps.get(position).get("rank"));
				//置顶
				int top1 = FormatUtil.toInt(myMaps.get(position).get("top1"));
				//推荐
				int top3 = FormatUtil.toInt(myMaps.get(position).get("top3"));
				
				
				if(top3>0){
					holder.img_top3.setVisibility(View.VISIBLE);
				}
				else{
					holder.img_top3.setVisibility(View.GONE);
				}
				if(top1>0){
					holder.img_top2.setVisibility(View.VISIBLE);
					holder.img_top3.setVisibility(View.GONE);
				}
				else{
					holder.img_top2.setVisibility(View.GONE);
				}
				if(rank>0){
					holder.img_top1.setVisibility(View.VISIBLE);
					holder.img_top3.setVisibility(View.GONE);
					holder.img_top2.setVisibility(View.GONE);
				}
				else{
					holder.img_top1.setVisibility(View.GONE);
				}
				
				
				if(FormatUtil.isNoEmpty(mobile)){
					holder.img_mobile.setVisibility(View.VISIBLE);
					holder.img_mobile.setOnTouchListener(new View.OnTouchListener() {
						
						@Override
						public boolean onTouch(View arg0, MotionEvent me) {
							if(me.getAction() == MotionEvent.ACTION_UP){
								Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+mobile));  
				                startActivity(intent);
				                return false;
							}
							return true;
						}
					});
				}
				else{
					holder.img_mobile.setVisibility(View.GONE);
					holder.img_mobile.setOnTouchListener(null);
				}
				if(FormatUtil.isNoEmpty(qq)){
					holder.img_qq.setVisibility(View.VISIBLE);
					holder.img_qq.setOnTouchListener(new View.OnTouchListener() {						
						@Override
						public boolean onTouch(View arg0, MotionEvent me) {
							if(me.getAction() == MotionEvent.ACTION_UP){
								String url="mqqwpa://im/chat?chat_type=wpa&uin="+qq;
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); 
								return false;
							}
							return true;
						}
					});
					
				}
				else{
					holder.img_qq.setVisibility(View.GONE);
					holder.img_qq.setOnTouchListener(null);
				}
				holder.ll_company.setOnTouchListener(new View.OnTouchListener() {
					
					@Override
					public boolean onTouch(View arg0, MotionEvent me) {
						if(me.getAction() == MotionEvent.ACTION_UP){
							Intent i = new Intent(getApplicationContext(),CompanyInfoActivity.class);
							i.putExtra("id",id );
							startActivity(i);
							return false;
						}
						return true;
					}
				});
				int  integral =FormatUtil.toInt( dateMaps.get(position).get("integral").toString());
				if(roleName.equals("付费VIP会员")){
					holder.txt_roleName.setText("钻石会员");
					holder.img_roleName.setBackgroundResource(R.drawable.xz6);
				}
				else if(integral>=10000){
					holder.txt_roleName.setText("铂金会员");
					holder.img_roleName.setBackgroundResource(R.drawable.xz5);
				}
				else if(integral>=5000){
					holder.txt_roleName.setText("金牌会员");
					holder.img_roleName.setBackgroundResource(R.drawable.xz4);
				}
				else if(integral>=2000){
					holder.txt_roleName.setText("银牌会员");
					holder.img_roleName.setBackgroundResource(R.drawable.xz3);
				}
				else if(integral>=500){
					holder.txt_roleName.setText("铜牌会员");
					holder.img_roleName.setBackgroundResource(R.drawable.xz2);
				}
				else{
					holder.txt_roleName.setText("基础会员");
					holder.img_roleName.setBackgroundResource(R.drawable.xz1);
				}
				
				holder.txt_comp.setText(myMaps.get(position).get("comp").toString());
				holder.txt_comptype.setText(myMaps.get(position).get("comptype").toString());
				holder.txt_industryclass.setText(myMaps.get(position).get("industryclass").toString());
				
				
			
			return super.getView(position, convertView, parent);
		}

		
	}
	
	class ViewHolder{ 
		@ViewInject(R.id.img_roleName)
		private ImageView img_roleName;		
		@ViewInject(R.id.ll_company)
		private LinearLayout ll_company;
		@ViewInject(R.id.txt_comp)
		private TextView txt_comp;
		@ViewInject(R.id.txt_roleName)
		private TextView txt_roleName;
		@ViewInject(R.id.txt_comptype)
		private TextView txt_comptype;
		@ViewInject(R.id.txt_industryclass)
		private TextView txt_industryclass;
		@ViewInject(R.id.txt_proNums)
		private TextView txt_proNums;
		@ViewInject(R.id.img_mobile)
		private ImageView img_mobile;
		@ViewInject(R.id.img_qq)
		private ImageView img_qq;
		@ViewInject(R.id.img_top1)
		private ImageView img_top1;
		@ViewInject(R.id.img_top2)
		private ImageView img_top2;
		@ViewInject(R.id.img_top3)
		private ImageView img_top3;
	}
	
	
	/**
	 * 初始化控件
	 */
	private void parentControl(){
		super.imageView3.setImageResource(R.drawable.buttom_tap_3s);
	}


	private void onLoad() {
		listViewAll.stopRefresh();
		listViewAll.stopLoadMore();
		listViewAll.setRefreshTime(DateUtil.DateToString(new Date(), DateStyle.HH_MM_SS));
	}
	@Override
	public void onRefresh() {
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
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				start++;
				getDate(true,false);
				onLoad();
			}
		}, 1);
		
	}

	
}
