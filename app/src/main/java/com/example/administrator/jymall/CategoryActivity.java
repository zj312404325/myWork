package com.example.administrator.jymall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.jymall.common.TopSearchActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@ContentView(R.layout.activity_category)
public class CategoryActivity extends TopSearchActivity {

	private List<Map<String, Object>> data_list= new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> data_list1= new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> data_list2= new ArrayList<Map<String, Object>>();
	
	private SimpleAdapter sadapter;
	private SimpleAdapter sadapter1;
	private SimpleAdapter sadapter2;
	
	@ViewInject(R.id.c)
	private ListView c;
	@ViewInject(R.id.c1)
	private ListView c1;
	@ViewInject(R.id.c2)
	private ListView c2;

	//@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		String [] from ={"name"};
        int [] to = {R.id.name};
		sadapter = new Cadapter(this, data_list, R.layout.list_category, from, to);
		sadapter1 = new Cadapter1(this, data_list1, R.layout.list_category, from, to);
		sadapter2 = new Cadapter2(this, data_list2, R.layout.list_category, from, to);
		c.setAdapter(sadapter);
		
		c1.setAdapter(sadapter1);
		c2.setAdapter(sadapter2);
		c.setDivider(null);
		c1.setDivider(null);
		c2.setDivider(null);
		getDate();
		parentControl();
		
		progressDialog.hide();
	}

	
	public void getDate(){
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		XUtilsHelper.getInstance().post("app/getCategory.htm", maps,new XUtilsHelper.XCallBack(){

			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				JSONObject res;
				try {
					res = new JSONObject(result);
					setServerKey(res.get("serverKey").toString());
					JSONArray resjarr = new JSONArray(res.get("data").toString());
					for(int i=0;i<resjarr.length();i++){
						Map<String, Object> dateMap = new HashMap<String, Object>();
						dateMap.put("name", resjarr.getJSONObject(i).get("name"));
						dateMap.put("list", FormatUtil.getJSONObject(resjarr.getJSONObject(i), "list"));
						dateMap.put("id", resjarr.getJSONObject(i).get("id"));
						dateMap.put("s", "0");
						data_list.add(dateMap);
					}
					sadapter.notifyDataSetChanged();
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
	}
	
	public class Cadapter  extends SimpleAdapter {
		public ViewHolder holder; 
		private LayoutInflater mInflater;
		
		public Cadapter(Context context,
				List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@SuppressLint({ "NewApi", "ResourceAsColor" })
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			try{
				holder=null;
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.list_category, null); 
					holder=new ViewHolder(); 
					x.view().inject(holder,convertView); 
					convertView.setTag(holder);
				} 
				else{
					holder=(ViewHolder) convertView.getTag(); 
				} 
				holder.name.setText(data_list.get(position).get("name").toString());
				String s = data_list.get(position).get("s").toString();
				if(s.equals("1")){
					holder.line1.setVisibility(View.VISIBLE);
					holder.line.setVisibility(View.GONE);
				}
				else{
					holder.line1.setVisibility(View.GONE);
					holder.line.setVisibility(View.VISIBLE);
				}
				
				holder.c.setOnLongClickListener(new View.OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View arg0) {
						CommonUtil.alter("ID="+data_list.get(position).get("name").toString());
						return false;
					}
				});
				holder.c.setOnTouchListener(new View.OnTouchListener() {
						
						@Override
						public boolean onTouch(View arg0, MotionEvent e) {
							if(e.getAction() == MotionEvent.ACTION_UP){
								String s = data_list.get(position).get("s").toString();
								if(s.equals("1")){
									Intent i = new Intent(getApplicationContext(), ProductActivity.class);
									i.putExtra("cId", data_list.get(position).get("id").toString());
									startActivity(i);
								}
								else{
									data_list1.clear();
									data_list2.clear();
									for(int i=0;i<data_list.size();i++){
										if(i==position){
											data_list.get(i).put("s", "1");
										}else{
											data_list.get(i).put("s", "0");
										}
									}
									sadapter.notifyDataSetChanged();
									
									JSONArray resjarr = (JSONArray)data_list.get(position).get("list");
									if(resjarr!= null){
										try{
											
										for(int i=0;i<resjarr.length();i++){
											Map<String, Object> dateMap = new HashMap<String, Object>();
											dateMap.put("name", resjarr.getJSONObject(i).get("name"));
											dateMap.put("list", FormatUtil.getJSONObject(resjarr.getJSONObject(i), "list"));
											dateMap.put("id", resjarr.getJSONObject(i).get("id"));
											dateMap.put("s", "0");
											data_list1.add(dateMap);
										}
										
										}
										catch(Exception ex){}
									}
									sadapter1.notifyDataSetChanged();
									sadapter2.notifyDataSetChanged();
								}
								return false;
							}
							return true;
						}
				});
				
				
			}
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}
	}
	
	class ViewHolder{ 

		@ViewInject(R.id.name)
		private TextView name;
		@ViewInject(R.id.line)
		private LinearLayout line;
		@ViewInject(R.id.line1)
		private LinearLayout line1;
		@ViewInject(R.id.c)
		private RelativeLayout c;
		
	}
	
	
	
	public class Cadapter1  extends SimpleAdapter {
		public ViewHolder1 holder; 
		private LayoutInflater mInflater;
		
		public Cadapter1(Context context,
				List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@SuppressLint({ "NewApi", "ResourceAsColor" })
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			try{
				holder=null;
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.list_category, null); 
					holder=new ViewHolder1(); 
					x.view().inject(holder,convertView); 
					convertView.setTag(holder);
				} 
				else{
					holder=(ViewHolder1) convertView.getTag(); 
				} 
				holder.name.setText(data_list1.get(position).get("name").toString());
				String s = data_list1.get(position).get("s").toString();
				if(s.equals("1")){
					holder.line1.setVisibility(View.VISIBLE);
					holder.line.setVisibility(View.GONE);
				}
				else{
					holder.line1.setVisibility(View.GONE);
					holder.line.setVisibility(View.VISIBLE);
				}
				
				
				holder.c.setOnTouchListener(new View.OnTouchListener() {
						
						@Override
						public boolean onTouch(View arg0, MotionEvent e) {
							if(e.getAction() == MotionEvent.ACTION_UP){
								String s = data_list1.get(position).get("s").toString();
								if(s.equals("1")){
									Intent i = new Intent(getApplicationContext(), ProductActivity.class);
									i.putExtra("cId", data_list1.get(position).get("id").toString());
									startActivity(i);
								}
								else{						
									for(int i=0;i<data_list1.size();i++){
										if(i==position){
											data_list1.get(i).put("s", "1");
										}else{
											data_list1.get(i).put("s", "0");
										}
									}
									sadapter1.notifyDataSetChanged();
									
									data_list2.clear();
									JSONArray resjarr = (JSONArray)data_list1.get(position).get("list");
									if(resjarr!= null){
										try{
											
										for(int i=0;i<resjarr.length();i++){
											Map<String, Object> dateMap = new HashMap<String, Object>();
											dateMap.put("name", resjarr.getJSONObject(i).get("name"));
											dateMap.put("list", FormatUtil.getJSONObject(resjarr.getJSONObject(i), "list"));
											dateMap.put("id", resjarr.getJSONObject(i).get("id"));
											dateMap.put("s", "0");
											data_list2.add(dateMap);
										}
										
										}
										catch(Exception ex){}
									}
									sadapter2.notifyDataSetChanged();
								}
								return false;
							}
							return true;
						}
				});
				
				
			}
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}
	}
	
	class ViewHolder1{ 

		@ViewInject(R.id.name)
		private TextView name;
		@ViewInject(R.id.line)
		private LinearLayout line;
		@ViewInject(R.id.line1)
		private LinearLayout line1;
		@ViewInject(R.id.c)
		private RelativeLayout c;
		
	}
	
	
	
	public class Cadapter2  extends SimpleAdapter {
		public ViewHolder2 holder; 
		private LayoutInflater mInflater;
		
		public Cadapter2(Context context,
				List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@SuppressLint({ "NewApi", "ResourceAsColor" })
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			try{
				holder=null;
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.list_category, null); 
					holder=new ViewHolder2(); 
					x.view().inject(holder,convertView); 
					convertView.setTag(holder);
				} 
				else{
					holder=(ViewHolder2) convertView.getTag(); 
				} 
				holder.name.setText(data_list2.get(position).get("name").toString());
				String s = data_list2.get(position).get("s").toString();
				if(s.equals("1")){
					holder.line1.setVisibility(View.VISIBLE);
					holder.line.setVisibility(View.GONE);
				}
				else{
					holder.line1.setVisibility(View.GONE);
					holder.line.setVisibility(View.VISIBLE);
				}
				
				
				holder.c.setOnTouchListener(new View.OnTouchListener() {
						
						@Override
						public boolean onTouch(View arg0, MotionEvent e) {
							if(e.getAction() == MotionEvent.ACTION_UP){
								Intent i = new Intent(getApplicationContext(), ProductActivity.class);
								i.putExtra("cId", data_list2.get(position).get("id").toString());
								startActivity(i);
								return false;
							}
							return true;
						}
				});
				
				
			}
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}
	}
	
	class ViewHolder2{ 

		@ViewInject(R.id.name)
		private TextView name;
		@ViewInject(R.id.line)
		private LinearLayout line;
		@ViewInject(R.id.line1)
		private LinearLayout line1;
		@ViewInject(R.id.c)
		private RelativeLayout c;
		
	}
	
	
	/**
	 * 初始化控件
	 */
	private void parentControl(){
		super.imageView2.setImageResource(R.drawable.buttom_tap_1s);
	}

}
