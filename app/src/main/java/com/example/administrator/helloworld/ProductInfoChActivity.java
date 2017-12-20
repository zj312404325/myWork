package com.example.administrator.helloworld;

import java.text.Format;
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

import com.baidu.mapapi.map.Text;
import com.example.administrator.helloworld.common.BaseActivity;
import com.example.administrator.helloworld.common.MyApplication;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.DensityUtil;
import com.example.administrator.helloworld.util.FormatUtil;
import com.example.administrator.helloworld.util.XUtilsHelper;
import com.example.administrator.helloworld.view.AmountView;
import com.example.administrator.helloworld.view.MyGridView;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 通用模块
 * @author Administrator
 *
 */
@SuppressLint("ResourceAsColor")
@ContentView(R.layout.activity_product_info_ch)
public class ProductInfoChActivity extends BaseActivity {
	
	
	@ViewInject(R.id.ll_chinfo)
	private LinearLayout ll_chinfo;
	@ViewInject(R.id.ll_prices)
	private LinearLayout ll_prices;
	@ViewInject(R.id.ll_info)
	private LinearLayout ll_info;
	@ViewInject(R.id.tv_procode)
	private TextView tv_procode;
	@ViewInject(R.id.tv_remark)
	private TextView tv_remark;
	@ViewInject(R.id.tv_quantity)
	private TextView tv_quantity;
	@ViewInject(R.id.tv_proquality)
	private TextView tv_proquality;
	
	
	@ViewInject(R.id.close)
	private Button close;
	@ViewInject(R.id.btn_close)
	private Button btn_close;
	@ViewInject(R.id.btn_ok)
	private Button btn_ok;
	
	private List<Map<String, Object>> data_list1;
	
	private SimpleAdapter sim_adapter1;
	
	@ViewInject(R.id.amount_view)
	private AmountView mAmountView;
	
	private JSONObject resdata;	
	
	
	JSONArray sellProductProps;
	
	JSONObject selectPro;
	
	private String var1;
	
	private String pId = "";
	
	private double count;
	
	private double minMoq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		Intent intent = this.getIntent();
		String [] from ={"text"};
	    int [] to = {R.id.text};
	    data_list1 = new ArrayList<Map<String, Object>>();
		try {
			resdata =new JSONObject( intent.getStringExtra("data"));
			double count1 = intent.getDoubleExtra("count", 0);
			selectPro = new JSONObject(intent.getStringExtra("selectPro"));
			sellProductProps = resdata.getJSONArray("propsArr");
			for(int i=0;i<sellProductProps.length();i++){
					 Map<String, Object> map = new HashMap<String, Object>();
			         map.put("text", sellProductProps.getJSONObject(i).getString("var1"));
			         map.put("id", sellProductProps.getJSONObject(i).getString("id"));
			         map.put("proquality", sellProductProps.getJSONObject(i).getString("proquality"));
			         map.put("quantity", sellProductProps.getJSONObject(i).getString("quantity"));
			         map.put("remark", sellProductProps.getJSONObject(i).getString("remark"));
			         map.put("procode", sellProductProps.getJSONObject(i).getString("procode"));
			         map.put("selectPro", sellProductProps.getJSONObject(i).toString());
			         if(selectPro.getString("id").equals( sellProductProps.getJSONObject(i).getString("id"))){
			        	 map.put("s", 2);
			         }
			         else{			         
			        	 map.put("s", 1);
			         }
			         data_list1.add(map);
			}
			sim_adapter1 = new TestSimpleAdapter1(this, data_list1, R.layout.select_value, from, to);
			createDiv("规格",sim_adapter1);
			getPriceInfo(selectPro.getString("id").toString(),count1);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
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
	
	private void getPriceInfo(String id,final double count1) throws JSONException{
		ll_prices.removeAllViews();
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
					chanageDiv(parr,count1);
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}			
		});
	}
	
	private void chanageDiv(JSONArray a,double count1) throws JSONException{
		ll_prices.removeAllViews();
		for(int i=0;i<a.length();i++){
			if(i==0){
				minMoq = FormatUtil.toDouble(a.getJSONObject(i).getString("moq"));
				count = minMoq;
				if(count1 != 0){
					count = count1;
				}
			}
			
			LinearLayout.LayoutParams lp =new LinearLayout.
	        		LayoutParams(DensityUtil.dip2px(150), LinearLayout.LayoutParams.WRAP_CONTENT);
			TextView t1 = new TextView(this);
			t1.setLayoutParams(lp);
			t1.setText("起批量（吨）≥"+a.getJSONObject(i).getString("moq"));
			t1.setTextColor(Color.parseColor("#939393"));
			
			TextView t2 = new TextView(this);
			t2.setLayoutParams(lp);
			if(a.getJSONObject(i).getString("price").equals("0")){
				t2.setText("面议");
			}
			else
				t2.setText("￥"+a.getJSONObject(i).getString("price")+"/"+resdata.getJSONObject("info").getString("unit"));
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
	
	private void setInfo(JSONObject selectPro) throws JSONException{
		tv_quantity.setText(selectPro.get("quantity").toString());
		tv_procode.setText(selectPro.get("procode").toString());
		tv_proquality.setText(selectPro.get("proquality").toString());
		tv_remark.setText(selectPro.get("remark").toString());
		mAmountView.setGoods_storage(FormatUtil.toDouble(selectPro.get("quantity").toString()));
		mAmountView.setGoods_min(minMoq);
		mAmountView.setAmount(count);
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
									

									//点击选中
									for(int i=0;i<data_list1.size();i++){
										int s = FormatUtil.toInt( data_list1.get(i).get("s").toString());
										if(i == position1 && s != 0){
											data_list1.get(i).put("s", 2);
											selectPro = new JSONObject(data_list1.get(i).get("selectPro").toString());
											getPriceInfo(data_list1.get(i).get("id").toString(),0);
									
										}
										else if(s!= 0){
											data_list1.get(i).put("s", 1);
										}
									}
									sim_adapter1.notifyDataSetChanged();
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
	
	
	
	
	@Event(value={R.id.close,R.id.btn_close,R.id.btn_ok})
	private void closeclick(View v){
		Intent mIntent = new Intent();
		if(v.getId()==R.id.btn_ok){
			mIntent.putExtra("pId", pId); 
			mIntent.putExtra("count", mAmountView.getAmount()); 
			setResult(RESULT_OK, mIntent);
			this.finish();
		}
		else{
			setResult(1, mIntent);
			this.finish();
		}
	}

}
