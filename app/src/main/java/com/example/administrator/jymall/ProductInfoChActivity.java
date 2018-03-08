package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.BaseActivity;
import com.example.administrator.jymall.util.DensityUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.CountView;
import com.example.administrator.jymall.view.IChangeCoutCallback;
import com.example.administrator.jymall.view.MyGridView;

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
	@ViewInject(R.id.ll_propInfo)
	private LinearLayout ll_propInfo;
	@ViewInject(R.id.ll_prices)
	private LinearLayout ll_prices;
	@ViewInject(R.id.ll_info)
	private LinearLayout ll_info;

	@ViewInject(R.id.tv_quantity)
	private TextView tv_quantity;

	@ViewInject(R.id.tv_showPrice)
	private TextView tv_showPrice;

	@ViewInject(R.id.tv_showProname)
	private TextView tv_showProname;

	@ViewInject(R.id.iv_showImage)
	private ImageView iv_showImage;
	
	
	@ViewInject(R.id.close)
	private Button close;
	@ViewInject(R.id.btn_close)
	private Button btn_close;
	@ViewInject(R.id.btn_ok)
	private Button btn_ok;
	
	private List<Map<String, Object>> data_list1;
	private List<Map<String, Object>> price_list;
	
	private SimpleAdapter sim_adapter1;
	private SimpleAdapter sim_adapter_price;
	
	@ViewInject(R.id.amount_view)
	private CountView mAmountView;
	
	private JSONObject resdata;
	private JSONObject info;
	
	JSONArray sellProductProps;
	
	JSONObject selectPro;

	JSONObject defaultProp;

	JSONArray productPrices;
	
	private String var1;
	
	private String pId = "";
	
	private double count;

	private double minMoq;

	private double selectPrice=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		Intent intent = this.getIntent();
		String [] from ={"text"};
	    int [] to = {R.id.text};

		String [] from1 ={""};
		int [] to1 = {};

	    data_list1 = new ArrayList<Map<String, Object>>();
		price_list = new ArrayList<Map<String, Object>>();
		try {
			resdata =new JSONObject( intent.getStringExtra("data"));
			info = resdata.getJSONObject("info");
			double count1 = intent.getDoubleExtra("count", 0);
			selectPro = new JSONObject(intent.getStringExtra("selectPro"));
			defaultProp = new JSONObject(intent.getStringExtra("defaultProp"));
			productPrices= defaultProp.getJSONArray("productPrices");
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

			//获取起批量价格数据
			for(int i=0;i<productPrices.length();i++){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("moq", productPrices.getJSONObject(i).getString("moq"));
				map.put("isDiscount", productPrices.getJSONObject(i).getString("isDiscount"));
				map.put("price", productPrices.getJSONObject(i).getString("price"));
				map.put("discountPrice", productPrices.getJSONObject(i).getString("discountPrice"));
				price_list.add(map);
			}

			sim_adapter1 = new TestSimpleAdapter1(this, data_list1, R.layout.select_value, from, to);
			createDiv("规格",sim_adapter1);

			//填充起批量view
			sim_adapter_price = new PriceSimpleAdapter(this, price_list, R.layout.select_price, from1, to1);
			createPriceDiv("起批量",sim_adapter_price);

			getPriceInfo(selectPro.getString("id").toString(),count1);
			pId=selectPro.getString("id").toString();
			if(info.getString("isDiscount").equals("1") && info.getString("isOnDiscount").equals("1")) {
				tv_showPrice.setText(Html.fromHtml("¥<font color='#ff0000'><big><big>"+defaultProp.getString("mynewprice")+"</big></big></font>/"+info.getString("unit")));
			}
			else{
				tv_showPrice.setText(Html.fromHtml("¥<font color='#ff0000'><big><big>"+defaultProp.getString("myprice")+"</big></big></font>/"+info.getString("unit")));
			}
			//绑定图片
			XUtilsHelper.getInstance().bindCommonImage(iv_showImage, info.get("picUrl").toString(), true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
        LinearLayout.LayoutParams lp1 =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(0, DensityUtil.dip2px( 5), 0, 0);
        gview.setLayoutParams(lp1);
        
        LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams lp2 =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px( 0.5f));
        lp2.setMargins(0, DensityUtil.dip2px(10), 0, 0);
        ll.setLayoutParams(lp2);
        ll.setBackgroundColor(0xFFd0d0d0);
        //配置适配器
        gview.setAdapter(aa);	        
        
        ll_chinfo.addView(tv);
        ll_chinfo.addView(gview);
        ll_chinfo.addView(ll);
	}

	private void createPriceDiv(String title,SimpleAdapter aa){
		TextView tv = new TextView(this);
		tv.setText(title);
		tv.setTextColor(Color.parseColor("#000000"));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, DensityUtil.dip2px(10), 0, 0);
		tv.setLayoutParams(lp);

		MyGridView gview = new MyGridView(this);
		gview.setNumColumns(1);
		LinearLayout.LayoutParams lp1 =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp1.setMargins(0, DensityUtil.dip2px( 5), 0, 0);
		gview.setLayoutParams(lp1);

		LinearLayout ll = new LinearLayout(this);
		LinearLayout.LayoutParams lp2 =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px( 0.5f));
		lp2.setMargins(0, DensityUtil.dip2px(10), 0, 0);
		ll.setLayoutParams(lp2);
		ll.setBackgroundColor(0xFFd0d0d0);
		//配置适配器
		gview.setAdapter(aa);

		ll_propInfo.addView(tv);
		ll_propInfo.addView(gview);
		ll_propInfo.addView(ll);
	}
	
	private void getPriceInfo(String id,final double count1) throws JSONException{
		ll_prices.removeAllViews();
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("id", id);
		XUtilsHelper.getInstance().post("app/getMallPriceJson.htm", maps,new XUtilsHelper.XCallBack(){
			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				progressDialog.hide();
				JSONObject res1;
				try {
					res1 = new JSONObject(result);
					setServerKey(res1.get("serverKey").toString());
					JSONArray   parr = (JSONArray)res1.get("data");
					JSONObject   prop = (JSONObject)res1.get("prop");
					changeDiv(parr,count1);

					productPrices= prop.getJSONArray("productPrices");
					price_list.clear();
					//获取起批量价格数据
					for(int i=0;i<productPrices.length();i++){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("moq", productPrices.getJSONObject(i).getString("moq"));
						map.put("isDiscount", productPrices.getJSONObject(i).getString("isDiscount"));
						map.put("price", productPrices.getJSONObject(i).getString("price"));
						map.put("discountPrice", productPrices.getJSONObject(i).getString("discountPrice"));
						price_list.add(map);
					}
					sim_adapter_price.notifyDataSetChanged();

					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}			
		});
	}
	
	private void changeDiv(JSONArray a,double count1) throws JSONException{
		ll_prices.removeAllViews();
		for(int i=0;i<a.length();i++) {
			if (i == 0) {
				minMoq = FormatUtil.toDouble(a.getJSONObject(i).getString("moq"));
				count = minMoq;
				if (count1 != 0) {
					count = count1;
				}
			}

			if (FormatUtil.toDouble(a.getJSONObject(i).getString("price")) == 0) {
				tv_showPrice.setText("面议");
			} else {
				if(info.getString("isDiscount").equals("1") && info.getString("isOnDiscount").equals("1")) {
					tv_showPrice.setText(Html.fromHtml("¥<font color='#ff0000'><big><big>"+a.getJSONObject(i).getString("discountPrice")+"</big></big></font>/"+info.getString("unit")));
					selectPrice=FormatUtil.toDouble(a.getJSONObject(i).getString("discountPrice"));
				}
				else{
					tv_showPrice.setText(Html.fromHtml("¥<font color='#ff0000'><big><big>"+a.getJSONObject(i).getString("price")+"</big></big></font>/"+info.getString("unit")));
					selectPrice=FormatUtil.toDouble(a.getJSONObject(i).getString("price"));
				}
			}
			setInfo(selectPro);
		}
	}
	
	private void setInfo(JSONObject selectPro) throws JSONException{
		tv_quantity.setText(selectPro.get("quantity").toString()+info.get("unit").toString());
		tv_showProname.setText(info.get("proname").toString());

		mAmountView.setCallback(new IChangeCoutCallback() {
			@Override
			public void change(int count) {            //总价变化

			}
		});
		mAmountView.setMaxValue(99999);
		mAmountView.setMinValue(FormatUtil.toInteger(minMoq));
		mAmountView.setAmount(FormatUtil.toInteger(count));

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


	@SuppressLint("ResourceAsColor")
	public class PriceSimpleAdapter  extends SimpleAdapter {
		private LayoutInflater mInflater;
		public PriceSimpleAdapter(Context context,
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
					convertView=mInflater.inflate(R.layout.select_price, null);
				}
				String isDiscount =  price_list.get(position).get("isDiscount").toString();
				String isOnDiscount = info.getString("isOnDiscount");
				String moq = price_list.get(position).get("moq").toString();
				String discountPrice = price_list.get(position).get("discountPrice").toString();
				String price = price_list.get(position).get("price").toString();

				TextView tv_quantity = (TextView)convertView.findViewById(R.id.tv_quantity);
				TextView tv_price = (TextView)convertView.findViewById(R.id.tv_price);

				tv_quantity.setText(moq+info.getString("unit"));
				tv_quantity.setTextColor(Color.parseColor("#000000"));
				if(!isDiscount.equals("1") || !isOnDiscount.equals("1")){
					tv_price.setText("¥"+price+"元/"+info.getString("unit"));
					tv_price.setTextColor(Color.parseColor("#000000"));
				}
				if(isDiscount.equals("1") && isOnDiscount.equals("1")){
					tv_price.setText("¥"+discountPrice+"元/"+info.getString("unit"));
					tv_price.setTextColor(Color.parseColor("#000000"));
				}
				tv_quantity.setTag(position);
				tv_price.setTag(position);
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
			mIntent.putExtra("selectPrice", selectPrice);
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
