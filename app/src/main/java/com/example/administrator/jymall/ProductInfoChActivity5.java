package com.example.administrator.jymall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.jymall.common.BaseActivity;
import com.example.administrator.jymall.util.DensityUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.view.AmountView;
import com.example.administrator.jymall.view.MyGridView;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 轧硬模块
 * @author Administrator
 *
 */
@SuppressLint("ResourceAsColor")
@ContentView(R.layout.activity_product_info_ch)
public class ProductInfoChActivity5 extends BaseActivity {
	
	
	@ViewInject(R.id.ll_chinfo)
	private LinearLayout ll_chinfo;
	
	@ViewInject(R.id.close)
	private Button close;
	
	private List<Map<String, Object>> data_list1;
	private List<Map<String, Object>> data_list2;
	private List<Map<String, Object>> data_list3;
	private List<Map<String, Object>> data_list4;
	
	private SimpleAdapter sim_adapter1;
	private SimpleAdapter sim_adapter2;
	private SimpleAdapter sim_adapter3;
	private SimpleAdapter sim_adapter4;
	
	@ViewInject(R.id.amount_view)
	private AmountView mAmountView;
	
	private JSONObject resdata;
	
	
	
	int flag = 1; //当前点击层级
	String templateid;//模板
	Set<String>  arrTem=new HashSet<String>();
	JSONArray sellProductProps;
	
	private String var1;
	private String var2;
	private String var3;
	private String var4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		Intent intent = this.getIntent();
		String [] from ={"text"};
	    int [] to = {R.id.text};
	    data_list1 = new ArrayList<Map<String, Object>>();
	    data_list2 = new ArrayList<Map<String, Object>>();
	    data_list3 = new ArrayList<Map<String, Object>>();
	    data_list4 = new ArrayList<Map<String, Object>>();
		try {
			resdata =new JSONObject( intent.getStringExtra("data"));
			templateid  = resdata.getJSONObject("info").getString("templateid");
			sellProductProps = resdata.getJSONObject("info").getJSONArray("sellProductProps");
			//默认通用分类
			if(templateid.equals("0")){
				JSONArray defaultPropArr = resdata.getJSONArray("resdata");
				for(int i=0;i<defaultPropArr.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", defaultPropArr.getString(i));
			         map.put("s", 1);
			         data_list1.add(map);
				}
				sim_adapter1 = new TestSimpleAdapter1(this, data_list1, R.layout.select_value, from, to);
				createDiv("规格",sim_adapter1);
			}
			else if(templateid.equals("1")){
				
				JSONArray guigeLstArr = resdata.getJSONArray("guigeLst");
				for(int i=0;i<guigeLstArr.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", guigeLstArr.getString(i));
			         map.put("s", 1);
			         data_list1.add(map);
				}
				sim_adapter1 = new TestSimpleAdapter1(this, data_list1, R.layout.select_value, from, to);
				createDiv("规格",sim_adapter1);
				
				JSONArray xincengLstArr = resdata.getJSONArray("xincengLst");
				for(int i=0;i<xincengLstArr.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", xincengLstArr.getString(i));
			         map.put("s", 0);
			         data_list2.add(map);
				}
				sim_adapter2 = new TestSimpleAdapter2(this, data_list2, R.layout.select_value, from, to);
				createDiv("锌层",sim_adapter2);
				
				JSONArray baozhuangLstArr = resdata.getJSONArray("baozhuangLst");
				for(int i=0;i<baozhuangLstArr.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", baozhuangLstArr.getString(i));
			         map.put("s", 0);
			         data_list3.add(map);
				}
				sim_adapter3 = new TestSimpleAdapter3(this, data_list3, R.layout.select_value, from, to);
				createDiv("包装",sim_adapter3);
				
				JSONArray zhongliangLstArr = resdata.getJSONArray("zhongliangLst");
				for(int i=0;i<zhongliangLstArr.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", zhongliangLstArr.getString(i));
			         map.put("s", 0);
			         data_list4.add(map);
				}
				sim_adapter4 = new TestSimpleAdapter4(this, data_list4, R.layout.select_value, from, to);
				createDiv("重量",sim_adapter4);
			}
			else if(templateid.equals("2")){
				JSONArray guigeLstArr = resdata.getJSONArray("guigeLst");
				for(int i=0;i<guigeLstArr.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text",guigeLstArr.getString(i));
			         map.put("s", 1);
			         data_list1.add(map);
				}
				sim_adapter1 = new TestSimpleAdapter1(this, data_list1, R.layout.select_value, from, to);
				createDiv("规格",sim_adapter1);
				
				JSONArray colorLstArr = resdata.getJSONArray("colorLst");
				for(int i=0;i<colorLstArr.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", colorLstArr.getString(i));
			         map.put("s", 0);
			         data_list2.add(map);
				}
				sim_adapter2 = new TestSimpleAdapter2(this, data_list2, R.layout.select_value, from, to);
				createDiv("颜色",sim_adapter2);
				
				JSONArray thickLstArr = resdata.getJSONArray("thickLst");
				for(int i=0;i<thickLstArr.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", thickLstArr.getString(i));
			         map.put("s", 0);
			         data_list3.add(map);
				}
				sim_adapter3 = new TestSimpleAdapter3(this, data_list3, R.layout.select_value, from, to);
				createDiv("漆膜厚度",sim_adapter3);
				
				JSONArray zhongliangLstArr = resdata.getJSONArray("zhongliangLst");
				for(int i=0;i<zhongliangLstArr.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", zhongliangLstArr.getString(i));
			         map.put("s", 0);
			         data_list4.add(map);
				}
				sim_adapter4 = new TestSimpleAdapter4(this, data_list4, R.layout.select_value, from, to);
				createDiv("重量",sim_adapter4);
			}
			else if(templateid.equals("3")){
				JSONArray thickLst = resdata.getJSONArray("thickLst");
				for(int i=0;i<thickLst.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text",thickLst.getString(i));
			         map.put("s", 1);
			         data_list1.add(map);
				}
				sim_adapter1 = new TestSimpleAdapter1(this, data_list1, R.layout.select_value, from, to);
				createDiv("厚度",sim_adapter1);
				
				JSONArray koujingLst = resdata.getJSONArray("koujingLst");
				for(int i=0;i<koujingLst.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", koujingLst.getString(i));
			         map.put("s", 0);
			         data_list2.add(map);
				}
				sim_adapter2 = new TestSimpleAdapter2(this, data_list2, R.layout.select_value, from, to);
				createDiv("口径",sim_adapter2);
				
				JSONArray zhongliangLst = resdata.getJSONArray("zhongliangLst");
				for(int i=0;i<zhongliangLst.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", zhongliangLst.getString(i));
			         map.put("s", 0);
			         data_list3.add(map);
				}
				sim_adapter3 = new TestSimpleAdapter3(this, data_list3, R.layout.select_value, from, to);
				createDiv("重量",sim_adapter3);
				
			}
			else if(templateid.equals("4")){
				JSONArray guigeLst = resdata.getJSONArray("guigeLst");
				for(int i=0;i<guigeLst.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text",guigeLst.getString(i));
			         map.put("s", 1);
			         data_list1.add(map);
				}
				sim_adapter1 = new TestSimpleAdapter1(this, data_list1, R.layout.select_value, from, to);
				createDiv("规格",sim_adapter1);
				
				JSONArray baozhuangLst = resdata.getJSONArray("baozhuangLst");
				for(int i=0;i<baozhuangLst.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", baozhuangLst.getString(i));
			         map.put("s", 0);
			         data_list2.add(map);
				}
				sim_adapter2 = new TestSimpleAdapter2(this, data_list2, R.layout.select_value, from, to);
				createDiv("包装",sim_adapter2);
				
				JSONArray zhongliang = resdata.getJSONArray("zhongliang");
				for(int i=0;i<zhongliang.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", zhongliang.getString(i));
			         map.put("s", 0);
			         data_list3.add(map);
				}
				sim_adapter3 = new TestSimpleAdapter3(this, data_list3, R.layout.select_value, from, to);
				createDiv("重量",sim_adapter3);
			}
			else if(templateid.equals("5")){
				JSONArray guigeLst = resdata.getJSONArray("guigeLst");
				for(int i=0;i<guigeLst.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text",guigeLst.getString(i));
			         map.put("s", 1);
			         data_list1.add(map);
				}
				sim_adapter1 = new TestSimpleAdapter1(this, data_list1, R.layout.select_value, from, to);
				createDiv("规格",sim_adapter1);
				
				JSONArray baozhuangLst = resdata.getJSONArray("baozhuangLst");
				for(int i=0;i<baozhuangLst.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", baozhuangLst.getString(i));
			         map.put("s", 0);
			         data_list2.add(map);
				}
				sim_adapter2 = new TestSimpleAdapter2(this, data_list2, R.layout.select_value, from, to);
				createDiv("包装",sim_adapter2);
				
				JSONArray zhongliang = resdata.getJSONArray("zhongliang");
				for(int i=0;i<zhongliang.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", zhongliang.getString(i));
			         map.put("s", 0);
			         data_list3.add(map);
				}
				sim_adapter3 = new TestSimpleAdapter3(this, data_list3, R.layout.select_value, from, to);
				createDiv("重量",sim_adapter3);
			}
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	        
	    mAmountView.setGoods_storage(50);
	    mAmountView.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
	            @Override
	            public void onAmountChange(View view, double amount) {
	               // Toast.makeText(getApplicationContext(), "Amount=>  " + amount, Toast.LENGTH_SHORT).show();
	            }

				@Override
				public void onAmountChange1(View view, double amount) {
					// TODO Auto-generated method stub
					
				}
	        });
	    progressDialog.hide();
	}
	
	
	private void createDiv(String title,SimpleAdapter aa){
		TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextColor(Color.parseColor("#000000"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
        lp.setMargins(0, DensityUtil.dip2px(10), 0, 0);  
        tv.setLayoutParams(lp);
        
        MyGridView gview = new MyGridView(this);
        gview.setNumColumns(3);
        LinearLayout.LayoutParams lp1 =new LinearLayout.
        		LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(0, DensityUtil.dip2px( 5), 0, 0);
        gview.setLayoutParams(lp1);
        
        LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams lp2 =new LinearLayout.
        		LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px( 0.5f));
        lp2.setMargins(0, DensityUtil.dip2px(10), 0, 0);
        ll.setLayoutParams(lp2);
        ll.setBackgroundColor(0xFFd0d0d0);
        //配置适配器
        gview.setAdapter(aa);	        
        
        ll_chinfo.addView(tv);
        ll_chinfo.addView(gview);
        ll_chinfo.addView(ll);
	}
	
	
	@SuppressLint("ResourceAsColor")
	public class TestSimpleAdapter1  extends SimpleAdapter {
		private LayoutInflater mInflater;
		
		public TestSimpleAdapter1(Context context,
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
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.select_value, null); 
				}
				int s = FormatUtil.toInt( data_list1.get(position).get("s").toString());
				TextView text = (TextView)convertView.findViewById(R.id.text);
				if(s == 1){
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvalue);
					text.setBackgroundResource(R.color.bg_selectvlaue);
					text.setTextColor(Color.parseColor("#666666"));
				}
				else if(s == 0){
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvaluegray);
					text.setBackgroundResource(R.color.bg_selectvlaue_gary);
					text.setTextColor(Color.parseColor("#bcbcbc"));
				}
				else{
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvalueblue);
					text.setBackgroundResource(R.color.bg_selectvlaue_blue);
					text.setTextColor(Color.parseColor("#000000"));
				}
				text.setTag(position);
				if(s != 0){
					text.setOnTouchListener(new View.OnTouchListener() {						
						@Override
						public boolean onTouch(View arg0, MotionEvent e) {
							try{
								if(e.getAction() == MotionEvent.ACTION_UP){
									TextView tem = (TextView)arg0;
									int position1 = FormatUtil.toInt(tem.getTag());
									
									if(!templateid.equals("0")){
										//根据产品明细，点击第一个属性，取出相应的第二个属性
										arrTem.clear();
										for(int i=0;i<sellProductProps.length();i++){
											if(sellProductProps.getJSONObject(i).getString("var1")
													.equals(data_list1.get(position1).get("text").toString())){
												arrTem.add(sellProductProps.getJSONObject(i).getString("var2"));
											}											
										}	
										//显示第二个属性的是否可以选择状态
										for(int i=0;i<data_list2.size();i++){
											data_list2.get(i).put("s", 0);
											for(String str:arrTem){
												if(str.equals(data_list2.get(i).get("text").toString())){
													data_list2.get(i).put("s", 1);
												}
											}
											
										}
										sim_adapter2.notifyDataSetChanged();
										checkno(1);
									}
									

									//点击选中
									for(int i=0;i<data_list1.size();i++){
										int s = FormatUtil.toInt( data_list1.get(i).get("s").toString());
										if(i == position1 && s != 0){
											data_list1.get(i).put("s", 2);
											var1 = data_list1.get(i).get("text").toString();
											flag = 1;
										}
										else if(s!= 0){
											data_list1.get(i).put("s", 1);
										}
									}
									sim_adapter1.notifyDataSetChanged();
									return false;
								}
							}
							catch(Exception ep){}
							return true;
						}
					});
				}
				
			}
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}
	}
	
	@SuppressLint("ResourceAsColor")
	public class TestSimpleAdapter2  extends SimpleAdapter {
		private LayoutInflater mInflater;
		
		public TestSimpleAdapter2(Context context,
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
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.select_value, null); 
				}
				int s = FormatUtil.toInt( data_list2.get(position).get("s").toString());
				TextView text = (TextView)convertView.findViewById(R.id.text);
				if(s == 1){
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvalue);
					text.setBackgroundResource(R.color.bg_selectvlaue);
					text.setTextColor(Color.parseColor("#666666"));
				}
				else if(s == 0){
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvaluegray);
					text.setBackgroundResource(R.color.bg_selectvlaue_gary);
					text.setTextColor(Color.parseColor("#bcbcbc"));
				}
				else{
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvalueblue);
					text.setBackgroundResource(R.color.bg_selectvlaue_blue);
					text.setTextColor(Color.parseColor("#000000"));
				}
				text.setTag(position);
				if(s != 0){
					text.setOnTouchListener(new View.OnTouchListener() {						
						@Override
						public boolean onTouch(View arg0, MotionEvent e) {
							try{
								if(e.getAction() == MotionEvent.ACTION_UP){
									TextView tem = (TextView)arg0;
									int position1 = FormatUtil.toInt(tem.getTag());
									if(!templateid.equals("0")){
										//根据产品明细，点击第一个属性，取出相应的第二个属性
										arrTem.clear();
										for(int i=0;i<sellProductProps.length();i++){
											if(sellProductProps.getJSONObject(i).getString("var2")
													.equals(data_list2.get(position1).get("text").toString())){
												arrTem.add(sellProductProps.getJSONObject(i).getString("var3"));
											}											
										}	
										//显示第二个属性的是否可以选择状态
										for(int i=0;i<data_list3.size();i++){
											data_list3.get(i).put("s", 0);
											for(String str:arrTem){
												if(str.equals(data_list3.get(i).get("text").toString())){
													data_list3.get(i).put("s", 1);
												}
											}
											
										}
										sim_adapter3.notifyDataSetChanged();
										checkno(2);
									}
									
									//点击选中
									for(int i=0;i<data_list2.size();i++){
										int s = FormatUtil.toInt( data_list2.get(i).get("s").toString());
										if(i == position1 && s != 0){
											data_list2.get(i).put("s", 2);
											var2 = data_list2.get(i).get("text").toString();
											flag = 2;
										}
										else if(s!= 0){
											data_list2.get(i).put("s", 1);
										}
									}
									sim_adapter2.notifyDataSetChanged();
									return false;
								}
							}
							catch(Exception ep){}
							return true;
						}
					});
				}
				
			}
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}
	}
	
	@SuppressLint("ResourceAsColor")
	public class TestSimpleAdapter3  extends SimpleAdapter {
		private LayoutInflater mInflater;
		
		public TestSimpleAdapter3(Context context,
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
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.select_value, null); 
				}
				int s = FormatUtil.toInt( data_list3.get(position).get("s").toString());
				TextView text = (TextView)convertView.findViewById(R.id.text);
				if(s == 1){
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvalue);
					text.setBackgroundResource(R.color.bg_selectvlaue);
					text.setTextColor(Color.parseColor("#666666"));
				}
				else if(s == 0){
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvaluegray);
					text.setBackgroundResource(R.color.bg_selectvlaue_gary);
					text.setTextColor(Color.parseColor("#bcbcbc"));
				}
				else{
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvalueblue);
					text.setBackgroundResource(R.color.bg_selectvlaue_blue);
					text.setTextColor(Color.parseColor("#000000"));
				}
				text.setTag(position);
				if(s != 0){
					text.setOnTouchListener(new View.OnTouchListener() {						
						@Override
						public boolean onTouch(View arg0, MotionEvent e) {
							try{
								if(e.getAction() == MotionEvent.ACTION_UP){
									TextView tem = (TextView)arg0;
									int position1 = FormatUtil.toInt(tem.getTag());
									
									if(templateid.equals("1") || templateid.equals("2")){
										//根据产品明细，点击第一个属性，取出相应的第二个属性
										arrTem.clear();
										for(int i=0;i<sellProductProps.length();i++){
											if(sellProductProps.getJSONObject(i).getString("var3")
													.equals(data_list3.get(position1).get("text").toString())){
												arrTem.add(sellProductProps.getJSONObject(i).getString("var4"));
											}											
										}	
										//显示第二个属性的是否可以选择状态
										for(int i=0;i<data_list4.size();i++){
											data_list4.get(i).put("s", 0);
											for(String str:arrTem){
												if(str.equals(data_list4.get(i).get("text").toString())){
													data_list4.get(i).put("s", 1);
												}
											}
											
										}
										sim_adapter3.notifyDataSetChanged();
									}
									
									
									
									for(int i=0;i<data_list3.size();i++){
										int s = FormatUtil.toInt( data_list1.get(i).get("s").toString());
										if(i == position1 && s != 0){
											data_list3.get(i).put("s", 2);
											var3 = data_list3.get(i).get("text").toString();
											flag = 3;
										}
										else if(s!= 0){
											data_list3.get(i).put("s", 1);
										}
									}
									sim_adapter3.notifyDataSetChanged();
									return false;
								}
							}
							catch(Exception ep){}
							return true;
						}
					});
				}
				
			}
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}
	}
	
	@SuppressLint("ResourceAsColor")
	public class TestSimpleAdapter4  extends SimpleAdapter {
		private LayoutInflater mInflater;
		
		public TestSimpleAdapter4(Context context,
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
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.select_value, null); 
				}
				int s = FormatUtil.toInt( data_list4.get(position).get("s").toString());
				TextView text = (TextView)convertView.findViewById(R.id.text);
				if(s == 1){
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvalue);
					text.setBackgroundResource(R.color.bg_selectvlaue);
					text.setTextColor(Color.parseColor("#666666"));
				}
				else if(s == 0){
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvaluegray);
					text.setBackgroundResource(R.color.bg_selectvlaue_gary);
					text.setTextColor(Color.parseColor("#bcbcbc"));
				}
				else{
					//holder.text.setTextAppearance(getApplicationContext(), R.style.selectvalueblue);
					text.setBackgroundResource(R.color.bg_selectvlaue_blue);
					text.setTextColor(Color.parseColor("#000000"));
				}
				text.setTag(position);
				if(s != 0){
					text.setOnTouchListener(new View.OnTouchListener() {						
						@Override
						public boolean onTouch(View arg0, MotionEvent e) {
							if(e.getAction() == MotionEvent.ACTION_UP){
								TextView tem = (TextView)arg0;
								int position1 = FormatUtil.toInt(tem.getTag());
								for(int i=0;i<data_list4.size();i++){
									int s = FormatUtil.toInt( data_list1.get(i).get("s").toString());
									if(i == position1 && s != 0){
										data_list4.get(i).put("s", 2);
										var4 = data_list4.get(i).get("text").toString();
										flag = 4;
									}
									else if(s!= 0){
										data_list4.get(i).put("s", 1);
									}
								}
								sim_adapter4.notifyDataSetChanged();
								return false;
							}
							return true;
						}
					});
				}
				
			}
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}
	}
	
	
	private void checkno(int f){
		if(f==1){
			chanagelist(data_list3);			
			sim_adapter3.notifyDataSetChanged();
			if(templateid.equals(1) || templateid.equals("2")){
				chanagelist(data_list4);
				sim_adapter4.notifyDataSetChanged();
			}
		}
		else if(f==2 && (templateid.equals(1) || templateid.equals("2"))){
			chanagelist(data_list4);
			sim_adapter4.notifyDataSetChanged();
		}
	}
	
	private void chanagelist(List<Map<String, Object>> data_listtem){
		for(int i=0;i<data_listtem.size();i++){
			data_list3.get(i).put("s", 0);			
		}
	}
	
	
	@Event(R.id.close)
	private void closeclick(View v){
		this.finish();
	}

}
