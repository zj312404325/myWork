package com.example.administrator.helloworld;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.helloworld.common.BaseActivity;
import com.example.administrator.helloworld.util.DensityUtil;
import com.example.administrator.helloworld.util.FormatUtil;
import com.example.administrator.helloworld.util.XUtilsHelper;
import com.example.administrator.helloworld.view.AmountView;
import com.example.administrator.helloworld.view.MyGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * 彩涂模块
 * @author Administrator
 *
 */
@SuppressLint("ResourceAsColor")
@ContentView(R.layout.activity_product_info_ch)
public class ProductInfoChActivity2 extends BaseActivity {
	
	
	@ViewInject(R.id.ll_chinfo)
	private LinearLayout ll_chinfo;
	@ViewInject(R.id.ll_prices)
	private LinearLayout ll_prices;
	@ViewInject(R.id.ll_info)
	private LinearLayout ll_info;

	@ViewInject(R.id.ll_count)
	private LinearLayout ll_count;
	@ViewInject(R.id.ll_quantity)
	private LinearLayout ll_quantity;

	@ViewInject(R.id.tv_quantity)
	private TextView tv_quantity;
	
	@ViewInject(R.id.close)
	private Button close;
	@ViewInject(R.id.btn_close)
	private Button btn_close;
	@ViewInject(R.id.btn_ok)
	private Button btn_ok;
	
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
	
	
	JSONObject selectPro;
	
	private String pId = "";
	
	private double count;	
	
	
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
		
		//ll_proquality.setVisibility(View.GONE);//隐藏材质
		ll_count.setVisibility(View.GONE);//隐藏数量选择
		ll_quantity.setVisibility(View.GONE);//隐藏库存
		
		Intent intent = this.getIntent();
		String [] from ={"text"};
	    int [] to = {R.id.text};
	    data_list1 = new ArrayList<Map<String, Object>>();
	    data_list2 = new ArrayList<Map<String, Object>>();
	    data_list3 = new ArrayList<Map<String, Object>>();
	    data_list4 = new ArrayList<Map<String, Object>>();
		try {
			resdata =new JSONObject( intent.getStringExtra("data"));
			//count = intent.getDoubleExtra("count", 1);
			selectPro = new JSONObject(intent.getStringExtra("selectPro"));
			sellProductProps = resdata.getJSONArray("propsArr");
			
			JSONArray guigeLstArr = resdata.getJSONArray("guigeLst");
			for(int i=0;i<guigeLstArr.length();i++){
				 Map<String, Object> map = new HashMap<String, Object>();
		         map.put("text",guigeLstArr.getString(i));
		         if(selectPro.getString("var1").equals( guigeLstArr.getString(i))){
		        	 map.put("s", 2);
		         }
		         else{			         
		        	 map.put("s", 1);
		         }
		         data_list1.add(map);
			}
			sim_adapter1 = new TestSimpleAdapter1(this, data_list1, R.layout.select_value, from, to);
			createDiv("规格",sim_adapter1);
			
			JSONArray colorLstArr = resdata.getJSONArray("colorLst");
			for(int i=0;i<colorLstArr.length();i++){
				 Map<String, Object> map = new HashMap<String, Object>();
		         map.put("text", colorLstArr.getString(i));
		         if(selectPro.getString("var4").equals( colorLstArr.getString(i))){
		        	 map.put("s", 2);
		         }
		         else{			         
		        	 map.put("s", 0);
		         }
		         data_list2.add(map);
			}
			sim_adapter2 = new TestSimpleAdapter2(this, data_list2, R.layout.select_value, from, to);
			createDiv("颜色",sim_adapter2);
			
			JSONArray thickLstArr = resdata.getJSONArray("thickLst");
			for(int i=0;i<thickLstArr.length();i++){
				 Map<String, Object> map = new HashMap<String, Object>();
		         map.put("text", thickLstArr.getString(i));
		         if(selectPro.getString("var5").equals( thickLstArr.getString(i))){
		        	 map.put("s", 2);
		         }
		         else{			         
		        	 map.put("s", 0);
		         }
		         data_list3.add(map);
			}
			sim_adapter3 = new TestSimpleAdapter3(this, data_list3, R.layout.select_value, from, to);
			createDiv("漆膜厚度",sim_adapter3);
			
			JSONArray zhongliangLstArr = resdata.getJSONArray("zhongliangLst");
			for(int i=0;i<zhongliangLstArr.length();i++){
						 Map<String, Object> map = new HashMap<String, Object>();
				         map.put("text", zhongliangLstArr.getString(i));
				         if(FormatUtil.dequals(  FormatUtil.toDouble(selectPro.getString("quantity"))
				        		 , FormatUtil.toDouble( zhongliangLstArr.getString(i)))){
				        	 map.put("s", 2);
				         }
				         else{			         
				        	 map.put("s", 0);
				         }
				         data_list4.add(map);
			}
			sim_adapter4 = new TestSimpleAdapter4(this, data_list4, R.layout.select_value, from, to);
			createDiv("重量",sim_adapter4);

			getPriceInfo(selectPro.getString("id").toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	    progressDialog.hide();
	}
	
	private void getPriceInfo(String id) throws JSONException{
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("id", id);
		XUtilsHelper.getInstance().post("app/getPriceJson.htm", maps,new XUtilsHelper.XCallBack(){
			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				progressDialog.hide();
				JSONObject res1;
				try {
					res1 = new JSONObject(result);
					setServerKey(res1.get("serverKey").toString());
					JSONArray   parr = (JSONArray)res1.get("data");
					chanageDiv(parr);
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}			
		});
	}
	
	private void chanageDiv(JSONArray parr) throws JSONException{
		ll_prices.removeAllViews();
		for(int i=0;i<parr.length();i++){
			LinearLayout.LayoutParams lp =new LinearLayout.
	        		LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			TextView t1 = new TextView(this);
			t1.setLayoutParams(lp);
			t1.setText("价格：");
			t1.setTextColor(Color.parseColor("#939393"));
			
			TextView t2 = new TextView(this);
			t2.setLayoutParams(lp);
			if(parr.getJSONObject(i).getString("price").equals("0"))
				t2.setText("面议");
			else
				t2.setText("￥"+parr.getJSONObject(i).getString("price")+"/"+resdata.getJSONObject("info").getString("unit"));
			t2.setTextColor(Color.parseColor("#FFFF0000"));
			
			LinearLayout.LayoutParams lp1 =new LinearLayout.
	        		LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			LinearLayout ll = new LinearLayout(this);			
			lp1.setMargins(0, DensityUtil.dip2px(10), 0, 0);  
			ll.setLayoutParams(lp1);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			
			ll.addView(t1);
			ll.addView(t2);
			ll_prices.addView(ll);
		}
		setInfo(selectPro);
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
	
	private void setInfo(JSONObject selectPro) throws JSONException{
			//tv_procode.setText(selectPro.get("procode").toString());
			//tv_remark.setText(selectPro.get("remark").toString());
			count = FormatUtil.toDoubleSmp(selectPro.get("quantity").toString());
			pId = selectPro.get("id").toString();
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
									
							
									arrTem.clear();
									for(int i=0;i<sellProductProps.length();i++){
											if(sellProductProps.getJSONObject(i).getString("var1")
													.equals(data_list1.get(position1).get("text").toString())){
												arrTem.add(sellProductProps.getJSONObject(i).getString("var4"));
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

									//点击选中
									for(int i=0;i<data_list1.size();i++){
										int s = FormatUtil.toInt( data_list1.get(i).get("s").toString());
										if(i == position1 && s != 0){
											data_list1.get(i).put("s", 2);
											var1 = data_list1.get(i).get("text").toString();
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

									arrTem.clear();
									for(int i=0;i<sellProductProps.length();i++){
											if(sellProductProps.getJSONObject(i).getString("var4")
													.equals(data_list2.get(position1).get("text").toString()) &&
												sellProductProps.getJSONObject(i).getString("var1")
													.equals(var1)
													){
												arrTem.add(sellProductProps.getJSONObject(i).getString("var5"));
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

									
									//点击选中
									for(int i=0;i<data_list2.size();i++){
										int s = FormatUtil.toInt( data_list2.get(i).get("s").toString());
										if(i == position1 && s != 0){
											data_list2.get(i).put("s", 2);
											var2 = data_list2.get(i).get("text").toString();
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
									
				
										arrTem.clear();
										for(int i=0;i<sellProductProps.length();i++){
												if(sellProductProps.getJSONObject(i).getString("var5")
														.equals(data_list3.get(position1).get("text").toString()) &&
													sellProductProps.getJSONObject(i).getString("var1")
															.equals(var1) &&
													sellProductProps.getJSONObject(i).getString("var4")
																.equals(var2)
														){
													arrTem.add(sellProductProps.getJSONObject(i).getString("quantity"));
												}											
										}	
										//显示第二个属性的是否可以选择状态
										for(int i=0;i<data_list4.size();i++){
												data_list4.get(i).put("s", 0);
												for(String str:arrTem){
													if(FormatUtil.dequals(FormatUtil.toDouble(data_list4.get(i).get("text").toString())
													        		,FormatUtil.toDouble( str))){
														data_list4.get(i).put("s", 1);
													}
												}
												
										}
										sim_adapter4.notifyDataSetChanged();
																		
									for(int i=0;i<data_list3.size();i++){
										int s = FormatUtil.toInt( data_list3.get(i).get("s").toString());
										if(i == position1 && s != 0){
											data_list3.get(i).put("s", 2);
											var3 = data_list3.get(i).get("text").toString();
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
							try{
							if(e.getAction() == MotionEvent.ACTION_UP){
								TextView tem = (TextView)arg0;
								int position1 = FormatUtil.toInt(tem.getTag());
								for(int i=0;i<data_list4.size();i++){
									int s = FormatUtil.toInt( data_list4.get(i).get("s").toString());
									if(i == position1 && s != 0){
										data_list4.get(i).put("s", 2);
										var4 = data_list4.get(i).get("text").toString();
										
										for(int j=0;j<sellProductProps.length();j++){
											JSONObject objtem = sellProductProps.getJSONObject(j);
											if(objtem.getString("var1").equals(var1) &&
													objtem.getString("var4").equals(var2)&&
													objtem.getString("var5").equals(var3)&&													
													FormatUtil.dequals(FormatUtil.toDouble(objtem.getString("quantity"))
									        		,FormatUtil.toDouble( var4))){											
												
												selectPro = objtem;
												getPriceInfo(objtem.getString("id"));
											}
										}									
										
									}
									else if(s!= 0){
										data_list4.get(i).put("s", 1);
									}
								}
								sim_adapter4.notifyDataSetChanged();
								return false;
							}
							}
							catch(Exception ep){ep.printStackTrace();}
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
				chanagelist(data_list4);
				sim_adapter4.notifyDataSetChanged();
		}
		else if(f==2){
			chanagelist(data_list4);
			sim_adapter4.notifyDataSetChanged();
		}
	}
	
	private void chanagelist(List<Map<String, Object>> data_listtem){
		for(int i=0;i<data_listtem.size();i++){
			data_listtem.get(i).put("s", 0);			
		}
	}
	
	
	@Event(value={R.id.close,R.id.btn_close,R.id.btn_ok})
	private void closeclick(View v){
		Intent mIntent = new Intent();
		if(v.getId()==R.id.btn_ok){
			mIntent.putExtra("pId", pId); 
			mIntent.putExtra("count", count); 
			setResult(RESULT_OK, mIntent);
			this.finish();
		}
		else{
			setResult(1, mIntent);
			this.finish();
		}
	}
}
