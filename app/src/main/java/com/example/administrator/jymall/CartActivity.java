package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.ButtomTapActivity;
import com.example.administrator.jymall.util.BigDecimalUtil;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.DensityUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.CountView;
import com.example.administrator.jymall.view.IChangeCoutCallback;
import com.example.administrator.jymall.view.XListView;
import com.example.administrator.jymall.view.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_cart)
public class CartActivity extends  ButtomTapActivity implements IXListViewListener{
	
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
	private Handler mHandler;
	private int start = 1;
	private String goodsMoney;
	private int goodsCount = 0;

	@ViewInject(R.id.xListView)
	public XListView listViewAll = null ;
	@ViewInject(R.id.listtv)
	private TextView listtv;
	@ViewInject(R.id.ck_all)
	private CheckBox ck_all;
	@ViewInject(R.id.tv_allmoney)
	private TextView tv_allmoney;
	
	public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
	
	private SimpleAdapter sap;
	private String skey ="";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.progressDialog.hide();
		skey=super.serverKey;
		sap = new InfoSimpleAdapter(CartActivity.this, dateMaps,
				R.layout.listview_cartinfo,
				new String[]{"proName"},
				new int[]{R.id.tv_proName});
		listViewAll.setAdapter(sap);
		listViewAll.setPullLoadEnable(true);
		listViewAll.setXListViewListener(this);

		parentControl();
		changeDiv();

		ck_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TOakDO Auto-generated method stub
				allCheck(arg1);
			}
		});
		mHandler = new Handler();
	}

	private void getData(){
		progressDialog.show();
		listtv.setVisibility(View.GONE);
		listViewAll.setPullLoadEnable(false);
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
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
					if(buyCartList.length()==0 ) {
						listtv.setVisibility(View.VISIBLE);
					}
					else if(buyCartList.length() ==  5 ) {
						listViewAll.setPullLoadEnable(true);
					}

					for(int i=0;i<buyCartList.length();i++){
						Map<String, Object> dateMap = new HashMap<String, Object>();
						dateMap.put("id", buyCartList.getJSONObject(i).get("id"));
						dateMap.put("proName", buyCartList.getJSONObject(i).get("proName"));
						dateMap.put("proImgPath", buyCartList.getJSONObject(i).get("proImgPath"));
						dateMap.put("money", buyCartList.getJSONObject(i).get("money"));
						dateMap.put("salePrice", buyCartList.getJSONObject(i).get("salePrice"));
						dateMap.put("quantity", buyCartList.getJSONObject(i).get("quantity"));
						dateMap.put("isCheck", "0");
						dateMap.put("index", i);
						dateMap.put("createDate", buyCartList.getJSONObject(i).get("createDate"));
						dateMap.put("proID", buyCartList.getJSONObject(i).get("proID"));
						dateMap.put("propID", buyCartList.getJSONObject(i).get("propID"));
						dateMap.put("proSpec", buyCartList.getJSONObject(i).get("proSpec"));
						dateMaps.add(dateMap);
					}
					sap.notifyDataSetChanged();
					getAllMoney();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
	}


	public class InfoSimpleAdapter  extends SimpleAdapter {
		public ViewHolder holder;
		private LayoutInflater mInflater;
		private List<Map<String, Object>> myMaps;

		public InfoSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			myMaps = (List<Map<String, Object>>) data;
		}
		
		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			holder=null;
			if(convertView==null){
				convertView=mInflater.inflate(R.layout.listview_cartinfo, null);
				holder=new ViewHolder();
				x.view().inject(holder,convertView);
				convertView.setTag(holder);
			}
			else{
				holder=(ViewHolder) convertView.getTag();
			}

			if(myMaps.get(position).get("isCheck").toString().equals("1")){
				holder.ck_checkinfo.setChecked(true);
			}
			else {
				holder.ck_checkinfo.setChecked(false);
			}

			XUtilsHelper.getInstance().bindCommonImage(holder.img_proImgpath, myMaps.get(position).get("proImgPath").toString(), true);
			holder.tv_proName.setText(myMaps.get(position).get("proName").toString());

			double salePricetemp = FormatUtil.toDoubleSmp(myMaps.get(position).get("salePrice"));
			if(salePricetemp == 0) {
				holder.tv_salePrice.setText("面议");
			}
			else {
				holder.tv_salePrice.setText("￥" + FormatUtil.toString(salePricetemp)+"元");
			}

			//规格
			String spec=myMaps.get(position).get("proSpec").toString();
			if(FormatUtil.isNoEmpty(spec)){
				holder.tv_spec.setText("规格："+spec);
			}
			else{
				holder.tv_spec.setText("规格：暂无");
			}


			//final JSONArray productPrices = FormatUtil.toJSONArray(myMaps.get(position).get("productPrices").toString());

			/*holder.av_quantity.setGoods_storage(999999999);
			holder.av_quantity.setGoods_min(1);
			holder.av_quantity.setAmount(FormatUtil.toDouble( myMaps.get(position).get("quantity").toString()));*/

			holder.av_quantity.setCallback(new IChangeCoutCallback() {
				@Override
				public void change(int count) {            //总价变化
					myMaps.get(position).put("quantity", count);

					//改变数量
					Map<String, String> maps= new HashMap<String, String>();
					maps.put("serverKey", skey);
					maps.put("quantity", FormatUtil.toString(count));
					maps.put("id", myMaps.get(position).get("id").toString());
					XUtilsHelper.getInstance().post("app/updateMallBuycart.htm", maps,new XUtilsHelper.XCallBack(){

						@SuppressLint("NewApi")
						@Override
						public void onResponse(String result)  {

							JSONObject res;
							try {
								res = new JSONObject(result);
								setServerKey(res.get("serverKey").toString());
								if(res.get("d").equals("n")){
									CommonUtil.alter(res.get("msg").toString(),getApplicationContext());
								}
								else{
									myMaps.get(position).put("salePrice", res.get("salePrice").toString());
									double salePricetemp = FormatUtil.toDoubleSmp(res.get("salePrice").toString());
									holder.tv_salePrice.setText("￥" + FormatUtil.toString(salePricetemp)+"元");
									getAllMoney();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					});
					getAllMoney();
				}
			});
			holder.av_quantity.setMaxValue(99999);
			holder.av_quantity.setAmount(FormatUtil.toInteger( myMaps.get(position).get("quantity").toString()));

			String isCheck=myMaps.get(position).get("isCheck").toString();
			if(isCheck.equals("1")){
				holder.ck_checkinfo.setChecked(true);
			}
			else{
				holder.ck_checkinfo.setChecked(false);
			}
			holder.ck_checkinfo.setTag(position);
			/*holder.ck_checkinfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					if(arg0.getId() == R.id.ck_checkinfo){
						String isCheck = arg1?"1":"0";
						myMaps.get(position).put("isCheck", isCheck);
						sap.notifyDataSetChanged();
						getAllMoney();
					}
				}
			});*/

			holder.ck_checkinfo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox)v;
					String isCheck=myMaps.get(position).get("isCheck").toString();
					if(isCheck.equals("0")){
						myMaps.get(position).put("isCheck", "1");
					}else{
						myMaps.get(position).put("isCheck", "0");
					}
					sap.notifyDataSetChanged();
					getAllMoney();
				}
			});



			//vip
			/*holder.av_quantity.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
				@Override
				public void onAmountChange(View view, double amount) {
					myMaps.get(position).put("quantity", amount);
					//double salePrice = PricesUtil.getMinPrice(amount, productPrices);
					//myMaps.get(position).put("salePrice", salePrice);
					getAllMoney();
				}

				@Override
				public void onAmountChange1(View view, double amount) {
					// TODO Auto-generated method stub
					myMaps.get(position).put("quantity", amount);
					//double salePrice = PricesUtil.getMinPrice(amount, productPrices);
					//myMaps.get(position).put("salePrice", salePrice);
					getAllMoney();
				}
			});*/
			//

			holder.img_proImgpath.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getApplicationContext(),ProductInfoActivity.class);
					i.putExtra("id", myMaps.get(position).get("proID").toString());
					startActivity(i);
				}
			});
			return super.getView(position, convertView, parent);
		}

		@Override
		public int getCount() {
			return this.myMaps.size();
		}

	}
	
	private void getAllMoney(){
		try{
			double tem = 0;
			int count = 0;
			for(int i=0;i<dateMaps.size();i++){
				if(dateMaps.get(i).get("isCheck").toString().equals("1")){
					tem = BigDecimalUtil.add(tem,
							BigDecimalUtil.mul(
									FormatUtil.toDouble(dateMaps.get(i).get("salePrice").toString()),
									FormatUtil.toDouble(dateMaps.get(i).get("quantity").toString())
							));
					count++;
				}
			}
			tv_allmoney.setText("￥"+FormatUtil.toString(tem));
			btn_js.setText("去结算（"+count+"）");
			goodsMoney=FormatUtil.toString(tem);
			goodsCount=count;
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
		getData();

	}
	
	@Event(R.id.btn_js)
	private void btnjsClick(View v){
		if(super.isRealName) {
			Intent i = new Intent(getApplicationContext(), AddOrderActivity.class);
			i.putExtra("data", (Serializable) dateMaps);
			i.putExtra("goodsMoney", goodsMoney);
			i.putExtra("goodsCount", FormatUtil.toString(goodsCount));
			i.putExtra("orderType", "order");
			startActivity(i);
		}
		else{
			CommonUtil.alter("请先进行实名认证！");
			return;
		}
	}
	
	@Event(R.id.top_del)
	private void top_del(View v){
		String ids = "";
		for(int i=0;i<dateMaps.size();i++){
			if(dateMaps.get(i).get("isCheck").toString().equals("1")){
				ids += dateMaps.get(i).get("id").toString()+",";
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
		new  Builder(this).setTitle("金赢工业超市提醒您：" )
		.setMessage("确定要删除吗？" )
		.setPositiveButton("是" ,  new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				progressDialog.show();
				Map<String, String> maps= new HashMap<String, String>();
				maps.put("serverKey", serverKey1);
				maps.put("ids", ids1);
				XUtilsHelper.getInstance().post("app/deleteMallBuyCart.htm", maps,new XUtilsHelper.XCallBack(){

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

	class ViewHolder{
		@ViewInject(R.id.ck_checkinfo)
		private CheckBox ck_checkinfo;
		@ViewInject(R.id.img_proImgpath)
		private ImageView img_proImgpath;
		@ViewInject(R.id.tv_proName)
		private TextView tv_proName;
		@ViewInject(R.id.av_quantity)
		private CountView av_quantity;
		@ViewInject(R.id.tv_salePrice)
		private TextView tv_salePrice;
		@ViewInject(R.id.tv_spec)
		private TextView tv_spec;
	}

	private void onLoad() {
		listViewAll.stopRefresh();
		listViewAll.stopLoadMore();
		listViewAll.setRefreshTime(DateUtil.DateToString(new Date(), DateStyle.HH_MM_SS));
	}

	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				start = 1;
				getData();
				onLoad();
			}
		}, 1);

	}

	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				start++;
				getData();
				onLoad();
			}
		}, 1);

	}

	/**
	 * 初始化控件
	 */
	private void parentControl(){
		super.imageView4.setImageResource(R.drawable.buttom_tap_4s);
	}
}
