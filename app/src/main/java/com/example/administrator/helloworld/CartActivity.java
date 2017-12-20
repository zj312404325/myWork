package com.example.administrator.helloworld;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.example.administrator.helloworld.common.ButtomTapActivity;
import com.example.administrator.helloworld.common.MyApplication;
import com.example.administrator.helloworld.util.BigDecimalUtil;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.DensityUtil;
import com.example.administrator.helloworld.util.FormatUtil;
import com.example.administrator.helloworld.util.PricesUtil;
import com.example.administrator.helloworld.util.XUtilsHelper;
import com.example.administrator.helloworld.view.AmountView;
import com.example.administrator.helloworld.view.MyListView;
import com.example.administrator.helloworld.view.XListView;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@ContentView(R.layout.activity_cart)
public class CartActivity extends  ButtomTapActivity{
	
	@ViewInject(R.id.tab1)
	private RelativeLayout tab1;
	@ViewInject(R.id.tab2)
	private RelativeLayout tab2;
	@ViewInject(R.id.tab3)
	private RelativeLayout tab3;
	@ViewInject(R.id.tab_txt1)
	private TextView tab_txt1;
	@ViewInject(R.id.tab_txt2)
	private TextView tab_txt2;
	@ViewInject(R.id.tab_txt3)
	private TextView tab_txt3;
	@ViewInject(R.id.tab_line1)
	private LinearLayout tab_line1;
	@ViewInject(R.id.tab_line2)
	private LinearLayout tab_line2;
	@ViewInject(R.id.tab_line3)
	private LinearLayout tab_line3;
	
	@ViewInject(R.id.btn_js)
	private Button btn_js;
	@ViewInject(R.id.top_del)
	private TextView top_del;
	
	private String isfuture = "0";
	
	@ViewInject(R.id.xListView)
	public MyListView listViewAll = null ;
	@ViewInject(R.id.listtv)
	private TextView listtv;
	@ViewInject(R.id.ck_all)
	private CheckBox ck_all;
	@ViewInject(R.id.tv_allmoney)
	private TextView tv_allmoney;
	
	public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
	
	private SimpleAdapter sap;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		Intent intent = this.getIntent();
		isfuture = intent.getIntExtra("type", 0)+"";
		super.progressDialog.hide();
		parentControl();
		sap = new ProSimpleAdapter(CartActivity.this, dateMaps, 
				R.layout.listview_cart, 
				new String[]{"owner"}, 
				new int[]{R.id.tv_owner});
		listViewAll.setAdapter(sap);
		changeDiv();	
		ck_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TOakDO Auto-generated method stub

				allCheck(arg1);
			
			}
		});
	}
	
	
	
	
	
	private void getDate(){
		progressDialog.show();;
		listtv.setVisibility(View.GONE);
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("isfuture", isfuture);
		dateMaps.clear();
		XUtilsHelper.getInstance().post("app/showMallBuyCartList.htm", maps,new XUtilsHelper.XCallBack(){

			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				listtv.setVisibility(View.GONE);
				progressDialog.hide();
				JSONObject res;
				try {
					res = new JSONObject(result);
					setServerKey(res.get("serverKey").toString());
					JSONArray buyCartList = (JSONArray)res.get("data");
					Log.i("这尼玛", buyCartList.length()+"");
					Log.i("这尼玛", "老陈"+buyCartList.toString());
					if(buyCartList.length()==0 ) {
						listtv.setVisibility(View.VISIBLE);
					}

					//JSONArray buyCartList = resjarr.getJSONObject(i).getJSONArray("buyCartList");
					Map<String, Object> dateMap = new HashMap<String, Object>();
					//dateMap.put("owner", resjarr.getJSONObject(i).get("owner"));
					//dateMap.put("ownerId", resjarr.getJSONObject(i).get("ownerId"));
					//dateMap.put("saleId", resjarr.getJSONObject(i).get("saleId"));
					//dateMap.put("saleName", resjarr.getJSONObject(i).get("saleName"));
					dateMap.put("isCheck", "0");
					List<Map<String, Object>> dateMapinfo= new ArrayList<Map<String, Object>>();
					for(int i=0;i<buyCartList.length();i++){
						Map<String, Object> dateMap1 = new HashMap<String, Object>();
						dateMap1.put("ID", buyCartList.getJSONObject(i).get("id"));
						dateMap1.put("proName", buyCartList.getJSONObject(i).get("proName"));
						dateMap1.put("proImgPaht", buyCartList.getJSONObject(i).get("proImgPath"));
						dateMap1.put("money", buyCartList.getJSONObject(i).get("money"));
						dateMap1.put("salePrice", buyCartList.getJSONObject(i).get("salePrice"));
						dateMap1.put("quantity", buyCartList.getJSONObject(i).get("quantity"));
						dateMap1.put("proUnitWerghtSuffix", buyCartList.getJSONObject(i).get("proUnit"));
						dateMap1.put("isCheck", "0");
						dateMap1.put("index", i);
						dateMap1.put("proID", buyCartList.getJSONObject(i).get("proID"));
						dateMap1.put("propID", buyCartList.getJSONObject(i).get("propID"));
						//dateMap1.put("productPrices", buyCartList.getJSONObject(i).getJSONArray("productPrices").toString());
						dateMapinfo.add(dateMap1);
						dateMap.put("buyCartList", dateMapinfo);
					}
					dateMaps.add(dateMap);
					Log.i("这尼玛", "跪了"+dateMaps.toString());
					sap.notifyDataSetChanged();
					getAllMoney();
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
					convertView=mInflater.inflate(R.layout.listview_cart, null); 
				} 
				LinearLayout ll_company = (LinearLayout)convertView.findViewById(R.id.ll_company);
				TextView tv_owner = (TextView)convertView.findViewById(R.id.tv_owner);
				CheckBox ck_check = (CheckBox)convertView.findViewById(R.id.ck_check);
				MyListView lv_infolist = (MyListView)convertView.findViewById(R.id.lv_infolist);
				if(dateMaps.get(position).get("isCheck").toString().equals("1")){
					ck_check.setChecked(true);
				}
				else
					ck_check.setChecked(false);
				
				
				final List<Map<String, Object>> dateMapinfo= (List<Map<String, Object>>) dateMaps.get(position).get("buyCartList");
				final SimpleAdapter sapinfo = new InfoSimpleAdapter(CartActivity.this, dateMapinfo, 
						R.layout.listview_cartinfo, 
						new String[]{"proName"}, 
						new int[]{R.id.tv_proName});
				lv_infolist.setAdapter(sapinfo);	
				
				ck_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {	
						try{
							allCheckInfo(dateMapinfo,arg1);
							sapinfo.notifyDataSetChanged();
						}
						catch(Exception ep){ep.printStackTrace();}
					}
				});
				ll_company.setOnTouchListener(new View.OnTouchListener() {
					
					@Override
					public boolean onTouch(View arg0, MotionEvent event) {
						// TODO Auto-generated method stub
						if (event.getAction() == event.ACTION_UP) {
							Intent i = new Intent(getApplicationContext(),CompanyInfoActivity.class);
							i.putExtra("id", dateMaps.get(position).get("ownerId").toString());
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
	
	public class InfoSimpleAdapter  extends SimpleAdapter {
		private LayoutInflater mInflater;
		private List<Map<String, Object>> mdata;
		
		public InfoSimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mdata = (List<Map<String, Object>>) data;
		}
		
		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			try{
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.listview_cartinfo, null); 
				} 
				ImageView img_proImgPaht = (ImageView)convertView.findViewById(R.id.img_proImgPaht);
				TextView tv_proName = (TextView)convertView.findViewById(R.id.tv_proName);
				TextView tv_salePrice = (TextView)convertView.findViewById(R.id.tv_salePrice);
				TextView tv_quantity = (TextView)convertView.findViewById(R.id.tv_quantity);
				AmountView av_quantity = (AmountView)convertView.findViewById(R.id.av_quantity);
				CheckBox ck_checkinfo = (CheckBox)convertView.findViewById(R.id.ck_checkinfo);
				
				if(mdata.get(position).get("isCheck").toString().equals("1")){
					ck_checkinfo.setChecked(true);
				}
				else
					ck_checkinfo.setChecked(false);
				
				XUtilsHelper.getInstance().bindCommonImage(img_proImgPaht, mdata.get(position).get("proImgPaht").toString(), true);
				tv_proName.setText(mdata.get(position).get("proName").toString());
				
				double salePricetemp = FormatUtil.toDoubleSmp(mdata.get(position).get("salePrice"));
				if(salePricetemp == 0)
					tv_salePrice.setText("面议");
				else
					tv_salePrice.setText("￥"+FormatUtil.toString(salePricetemp));
				
				
				final JSONArray productPrices = FormatUtil.toJSONArray(mdata.get(position).get("productPrices").toString());
				
				av_quantity.setGoods_storage(999999999);
				av_quantity.setGoods_min(PricesUtil.getMinQuantity(productPrices));
				av_quantity.setAmount(FormatUtil.toDouble( mdata.get(position).get("quantity").toString()));
				
				ck_checkinfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						if(arg0.getId() == R.id.ck_checkinfo){
							String isCheck = arg1?"1":"0";
							mdata.get(position).put("isCheck", isCheck);
							getAllMoney();
						}
					}
				});
				av_quantity.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
		            @Override
		            public void onAmountChange(View view, double amount) {	
		            	mdata.get(position).put("quantity", amount);
		            	double salePrice = PricesUtil.getMinPrice(amount, productPrices);
		            	//tv_salePrice.setText("￥"+FormatUtil.toString(salePrice));
		            	mdata.get(position).put("salePrice", salePrice);		            	
		            	getAllMoney();
		            }

					@Override
					public void onAmountChange1(View view, double amount) {
						// TODO Auto-generated method stub
		            	mdata.get(position).put("quantity", amount);
		            	double salePrice = PricesUtil.getMinPrice(amount, productPrices);
		            	//tv_salePrice.setText("￥"+FormatUtil.toString(salePrice));
		            	mdata.get(position).put("salePrice", salePrice);
		            	getAllMoney();
					}
		        });
				img_proImgPaht.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent i = new Intent(getApplicationContext(),ProductInfoActivity.class);
						i.putExtra("id", mdata.get(position).get("proID").toString());
						startActivity(i);						
					}
				});
			}
			catch(Exception e){
				//Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}		
	}
	
	private void getAllMoney(){
		try{
			double tem = 0;
			int count = 0;
			Log.i("这尼玛", "交钱"+dateMaps.toString());
			for(int i=0;i<dateMaps.size();i++){
				List<Map<String, Object>> dateMapinfo= (List<Map<String, Object>>) dateMaps.get(i).get("buyCartList"); 
				
				for(int j=0;j< dateMaps.size();j++){
					if(dateMapinfo.get(j).get("isCheck").toString().equals("1")){
						tem = BigDecimalUtil.add(tem,
								BigDecimalUtil.mul(
										FormatUtil.toDouble(dateMapinfo.get(j).get("salePrice").toString()),
										FormatUtil.toDouble(dateMapinfo.get(j).get("quantity").toString())	
								));
						count++;
					}
				}
			}
			tv_allmoney.setText("￥"+FormatUtil.toString(tem));
			btn_js.setText("去结算（"+count+"）");
		}
		catch(Exception ep){ep.printStackTrace();}
	}
	
	private void allCheckInfo(List<Map<String, Object>> m,boolean f) {
		String isCheck = f?"1":"0";
		for(int i=0;i<m.size();i++){
			m.get(i).put("isCheck", isCheck);
		}
		getAllMoney();
	}
	
	private void allCheck(boolean f){
		String isCheck = f?"1":"0";
		for(int i=0;i<dateMaps.size();i++){
			dateMaps.get(i).put("isCheck", isCheck);
			List<Map<String, Object>> dateMapinfo= (List<Map<String, Object>>) dateMaps.get(i).get("buyCartList");
			for(int j=0;j<dateMapinfo.size();j++){
				dateMapinfo.get(j).put("isCheck", isCheck);
			}
		}
		sap.notifyDataSetChanged();
		getAllMoney();
	}
	
	
	
	@SuppressLint("NewApi")
	@Event(value={R.id.tab1,R.id.tab2,R.id.tab3},type=View.OnTouchListener.class)
	private boolean tabTouch(View v, MotionEvent e){
		if(e.getAction() == MotionEvent.ACTION_UP){
			if(v.getId() ==R.id.tab1 ){				
				isfuture = "0";				
			}else if(v.getId() ==R.id.tab2 ){				
				isfuture = "1";				
			}else if(v.getId() ==R.id.tab3 ){				
				isfuture = "2";				
			}
			changeDiv();
			return false;
		}
		return true;
	}
	
	@SuppressLint("NewApi")
	private void changeDiv(){
		RelativeLayout.LayoutParams lp1 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(2));
		lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		RelativeLayout.LayoutParams lp2 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(1));
		lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);	
		if(isfuture.equals("0") ){
			/*tab_txt1.setTextColor(Color.parseColor("#0083c8"));
			tab_line1.setBackground(CommonUtil.getDrawable(R.drawable.tab_s3));
			tab_line1.setLayoutParams(lp1);
			tab_txt2.setTextColor(Color.parseColor("#000000"));
			tab_line2.setBackgroundColor(0xFFb5b6b9);				
			tab_line2.setLayoutParams(lp2);
			tab_txt3.setTextColor(Color.parseColor("#000000"));
			tab_line3.setBackgroundColor(0xFFb5b6b9);
			tab_line3.setLayoutParams(lp2);*/
			
		}else if(isfuture.equals("1") ){
			tab_txt2.setTextColor(Color.parseColor("#0083c8"));
			tab_line2.setBackground(CommonUtil.getDrawable(R.drawable.tab_s3));
			tab_line2.setLayoutParams(lp1);
			tab_txt1.setTextColor(Color.parseColor("#000000"));
			tab_line1.setBackgroundColor(0xFFb5b6b9);
			tab_line1.setLayoutParams(lp2);
			tab_txt3.setTextColor(Color.parseColor("#000000"));
			tab_line3.setBackgroundColor(0xFFb5b6b9);
			tab_line3.setLayoutParams(lp2);
			isfuture = "1";
			
		}else if(isfuture.equals("2") ){
			tab_txt3.setTextColor(Color.parseColor("#0083c8"));
			tab_line3.setBackground(CommonUtil.getDrawable(R.drawable.tab_s3));
			tab_line3.setLayoutParams(lp1);
			tab_txt2.setTextColor(Color.parseColor("#000000"));
			tab_line2.setBackgroundColor(0xFFb5b6b9);
			tab_line2.setLayoutParams(lp2);
			tab_txt1.setTextColor(Color.parseColor("#000000"));
			tab_line1.setBackgroundColor(0xFFb5b6b9);
			tab_line1.setLayoutParams(lp2);
			isfuture = "2";
		}
		getDate();

	}
	
	@Event(R.id.btn_js)
	private void btnjsClick(View v){
		Intent i = new Intent(getApplicationContext(), AddOrderActivity.class);
		i.putExtra("data", (Serializable)dateMaps);
		startActivity(i);
	}
	
	@Event(R.id.top_del)
	private void top_del(View v){
		String ids = "";
		for(int i=0;i<dateMaps.size();i++){
			List<Map<String, Object>> dateMapinfo= (List<Map<String, Object>>) dateMaps.get(i).get("buyCartList");
			for(int j=0;j<dateMapinfo.size();j++){
				if(dateMapinfo.get(j).get("isCheck").toString().equals("1")){
					ids += dateMapinfo.get(j).get("ID").toString()+",";
				}
			}
		}
		if(ids.equals("")){
			CommonUtil.alter("请选择需要删除的商品！");
			return;
		}
		else{
			ids = ids.substring(0, ids.length()-1);
		}
		final String serverKey1 = super.serverKey;
		final String ids1  = ids;
		new  Builder(this).setTitle("金赢网提醒" )
		.setMessage("你确定要删除吗？" )  
		.setPositiveButton("是" ,  new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				progressDialog.show();;
				Map<String, String> maps= new HashMap<String, String>();
				maps.put("serverKey", serverKey1);
				maps.put("ids", ids1);
				XUtilsHelper.getInstance().post("app/deleteBuyCart.htm", maps,new XUtilsHelper.XCallBack(){

					@SuppressLint("NewApi")
					@Override
					public void onResponse(String result)  {
						progressDialog.hide();
						JSONObject res;
						try {
							res = new JSONObject(result);
							setServerKey(res.get("serverKey").toString());
							if(res.get("d").equals("n")){
								CommonUtil.alter(res.get("msg").toString(),getApplicationContext());
							}
							else{
								CommonUtil.alter("删除成功",getApplicationContext());
								changeDiv();
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


	/**
	 * 初始化控件
	 */
	private void parentControl(){
		super.imageView4.setImageResource(R.drawable.buttom_tap_4s);
	}
}
