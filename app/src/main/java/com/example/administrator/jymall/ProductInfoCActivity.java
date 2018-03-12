package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.DensityUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.URLImageParser;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyListView;
import com.example.administrator.jymall.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_product_info_c)
public class ProductInfoCActivity extends TopActivity implements XListView.IXListViewListener{
	
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
	@ViewInject(R.id.proinfo1)
	private LinearLayout proinfo1;
	@ViewInject(R.id.proinfo2)
	private LinearLayout proinfo2;
	@ViewInject(R.id.proinfo3)
	private LinearLayout proinfo3;
	@ViewInject(R.id.xListView)
	private XListView listViewAll=null;
	@ViewInject(R.id.lv_list)
	private MyListView lv_list;

	private int start = 1;
	private Handler mHandler;

	public List<Map<String, Object>> data_list= new ArrayList<Map<String, Object>>();
	private SimpleAdapter sap;
	
	private JSONObject info;
	private JSONObject defaultProp;
	private JSONArray appraiseList;

	@ViewInject(R.id.tv_proDesc)
	private TextView tv_proDesc;
	@ViewInject(R.id.tv_brand)
	private TextView tv_brand;
	@ViewInject(R.id.tv_quality)
	private TextView tv_quality;
	@ViewInject(R.id.tv_size)
	private TextView tv_size;
	@ViewInject(R.id.tv_model)
	private TextView tv_model;
	@ViewInject(R.id.tv_color)
	private TextView tv_color;
	@ViewInject(R.id.tv_weight)
	private TextView tv_weight;
	@ViewInject(R.id.tv_noData)
	private TextView tv_noData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_info_c);
		x.view().inject(this);
		super.title.setText("产品详情");
		Intent i = this.getIntent();
		try {
			info = new JSONObject(i.getStringExtra("info"));
			defaultProp = new JSONObject(i.getStringExtra("defaultProp"));
			appraiseList = new JSONArray(i.getStringExtra("appraiseList"));
			sap = new ProSimpleAdapter(ProductInfoCActivity.this, data_list,
					R.layout.listview_appraise,
					new String[]{},
					new int[]{});
			//lv_list.setAdapter(sap);
			listViewAll.setAdapter(sap);
			listViewAll.setPullLoadEnable(true);
			listViewAll.setXListViewListener(this);
			getData(true,true);
			mHandler = new Handler();

		} catch (JSONException e) {
			e.printStackTrace();
			progressDialog.hide();
		}
	}

	private void getData(final boolean isShow,final boolean flag){
		int pageSize=10;

		if(isShow){
			progressDialog.show();
		}
		if(flag){
			start = 1;
		}

		listViewAll.setPullLoadEnable(false);
		tv_noData.setVisibility(View.GONE);
		try {
			if(FormatUtil.isNoEmpty(appraiseList)) {
				int count=appraiseList.length();
				int totalPage=pageSize*start;
				int startPage=pageSize*(start-1);
				if(totalPage<=count){
					count=totalPage;
				}
				if(flag){
					data_list.clear();
				}
				if(isShow){
					progressDialog.hide();
				}
				if(count==0 && start == 1) {
					tv_noData.setVisibility(View.VISIBLE);
				}
				else if(count ==  10 ) {
					listViewAll.setPullLoadEnable(true);
				}
				for (int i=startPage; i < count; i++) {
					Map<String, Object> dateMap = new HashMap<String, Object>();
					dateMap.put("createuser", appraiseList.getJSONObject(i).get("createuser"));
					dateMap.put("createdate", appraiseList.getJSONObject(i).get("createdate"));
					dateMap.put("orderno", appraiseList.getJSONObject(i).get("orderno"));
					dateMap.put("productLevel", appraiseList.getJSONObject(i).get("productLevel"));
					dateMap.put("remark", appraiseList.getJSONObject(i).get("remark"));
					dateMap.put("heador", appraiseList.getJSONObject(i).get("heador"));
					dateMap.put("pic1", appraiseList.getJSONObject(i).get("pic1"));
					dateMap.put("pic2", appraiseList.getJSONObject(i).get("pic2"));
					dateMap.put("pic3", appraiseList.getJSONObject(i).get("pic3"));
					dateMap.put("pic4", appraiseList.getJSONObject(i).get("pic4"));
					dateMap.put("pic5", appraiseList.getJSONObject(i).get("pic5"));
					dateMap.put("id", appraiseList.getJSONObject(i).get("id"));
					data_list.add(dateMap);
				}
				if(appraiseList.length()>0) {
					tv_noData.setVisibility(View.GONE);
				}
			}
			else{
				tv_noData.setVisibility(View.VISIBLE);
			}

			URLImageParser p = new URLImageParser(tv_proDesc, ProductInfoCActivity.this);
			String html = info.getString("prodesc");
			Spanned htmlSpan = Html.fromHtml(html, p, null);
			tv_proDesc.setText(htmlSpan.toString());
			tv_brand.setText(info.getJSONObject("mallProductAttr").get("var1").toString());
			tv_quality.setText(defaultProp.get("proquality").toString());
			tv_size.setText(info.getJSONObject("mallProductAttr").get("var4").toString());
			tv_model.setText(info.getJSONObject("mallProductAttr").get("var5").toString());
			tv_color.setText(info.getJSONObject("mallProductAttr").get("var6").toString());
			tv_weight.setText(info.getJSONObject("mallProductAttr").get("var7").toString());

			progressDialog.hide();
			sap.notifyDataSetChanged();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class ProSimpleAdapter  extends SimpleAdapter {
		
		public ViewHolder holder; 
		private LayoutInflater mInflater;
		
		public ProSimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			try{
				holder=null;
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.listview_appraise, null);
					holder=new ViewHolder(); 
					x.view().inject(holder,convertView); 
					convertView.setTag(holder);
				} 
				else{
					holder=(ViewHolder) convertView.getTag(); 
				}

				holder.tv_userName.setText(data_list.get(position).get("createuser").toString());
				holder.tv_orderTime.setText(data_list.get(position).get("createdate").toString());
				holder.tv_remark.setText(data_list.get(position).get("remark").toString());
				XUtilsHelper.getInstance().bindCommonImage(holder.iv_heador, data_list.get(position).get("heador").toString(), true);

				holder.iv_pic1.setVisibility(View.GONE);
				holder.iv_pic2.setVisibility(View.GONE);
				holder.iv_pic3.setVisibility(View.GONE);

				if(FormatUtil.isNoEmpty(data_list.get(position).get("pic1").toString())) {
					XUtilsHelper.getInstance().bindCommonImage(holder.iv_pic1, data_list.get(position).get("pic1").toString(), true);
					holder.iv_pic1.setVisibility(View.VISIBLE);
				}
				else{
					holder.iv_pic1.setVisibility(View.GONE);
				}
				if(FormatUtil.isNoEmpty(data_list.get(position).get("pic2").toString())) {
					XUtilsHelper.getInstance().bindCommonImage(holder.iv_pic2, data_list.get(position).get("pic2").toString(), true);
					holder.iv_pic2.setVisibility(View.VISIBLE);
				}
				else{
					holder.iv_pic2.setVisibility(View.GONE);
				}
				if(FormatUtil.isNoEmpty(data_list.get(position).get("pic3").toString())) {
					XUtilsHelper.getInstance().bindCommonImage(holder.iv_pic3, data_list.get(position).get("pic3").toString(), true);
					holder.iv_pic3.setVisibility(View.VISIBLE);
				}
				else{
					holder.iv_pic3.setVisibility(View.GONE);
				}

				if( holder.iv_pic1.getVisibility() == View.GONE &&   holder.iv_pic2.getVisibility() == View.GONE && holder.iv_pic3.getVisibility() == View.GONE){
					holder.ll_imageArea.setVisibility(View.GONE);
				}
				else{
					holder.ll_imageArea.setVisibility(View.VISIBLE);
				}

				String level=data_list.get(position).get("productLevel").toString();
				if(level.equals("1")){
					holder.iv_orderStar.setBackgroundResource(R.drawable.fivestar_red_1);
				}
				else if(level.equals("2")){
					holder.iv_orderStar.setBackgroundResource(R.drawable.fivestar_red_2);
				}
				else if(level.equals("3")){
					holder.iv_orderStar.setBackgroundResource(R.drawable.fivestar_red_3);
				}
				else if(level.equals("4")){
					holder.iv_orderStar.setBackgroundResource(R.drawable.fivestar_red_4);
				}
				else{
					holder.iv_orderStar.setBackgroundResource(R.drawable.fivestar_red_5);
				}

			}
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}

		
	}

	class ViewHolder{
		@ViewInject(R.id.iv_heador)
		private ImageView iv_heador;
		@ViewInject(R.id.iv_orderStar)
		private ImageView iv_orderStar;
		@ViewInject(R.id.tv_userName)
		private TextView tv_userName;
		@ViewInject(R.id.tv_orderTime)
		private TextView tv_orderTime;
		@ViewInject(R.id.tv_remark)
		private TextView tv_remark;
		@ViewInject(R.id.iv_pic1)
		private ImageView iv_pic1;
		@ViewInject(R.id.iv_pic2)
		private ImageView iv_pic2;
		@ViewInject(R.id.iv_pic3)
		private ImageView iv_pic3;
		@ViewInject(R.id.ll_imageArea)
		private LinearLayout ll_imageArea;
	}
	
	@SuppressLint("NewApi")
	@Event(value={R.id.tab1,R.id.tab2,R.id.tab3},type=View.OnTouchListener.class)
	private boolean tabTouch(View v, MotionEvent e){
		if(e.getAction() == MotionEvent.ACTION_UP){
			RelativeLayout.LayoutParams lp1 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(2));
			lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			RelativeLayout.LayoutParams lp2 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(1));
			lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);	
			if(v.getId() ==R.id.tab1 ){
				tab_txt1.setTextColor(Color.parseColor("#1a3688"));
				tab_line1.setBackground(CommonUtil.getDrawable(R.drawable.tab_s3));
				tab_line1.setLayoutParams(lp1);
				tab_txt2.setTextColor(Color.parseColor("#000000"));
				tab_line2.setBackgroundColor(0xFFb5b6b9);				
				tab_line2.setLayoutParams(lp2);
				tab_txt3.setTextColor(Color.parseColor("#000000"));
				tab_line3.setBackgroundColor(0xFFb5b6b9);
				tab_line3.setLayoutParams(lp2);
				proinfo1.setVisibility(View.VISIBLE);
				proinfo2.setVisibility(View.GONE);
				proinfo3.setVisibility(View.GONE);
			}else if(v.getId() ==R.id.tab2 ){
				tab_txt2.setTextColor(Color.parseColor("#1a3688"));
				tab_line2.setBackground(CommonUtil.getDrawable(R.drawable.tab_s3));
				tab_line2.setLayoutParams(lp1);
				tab_txt1.setTextColor(Color.parseColor("#000000"));
				tab_line1.setBackgroundColor(0xFFb5b6b9);
				tab_line1.setLayoutParams(lp2);
				tab_txt3.setTextColor(Color.parseColor("#000000"));
				tab_line3.setBackgroundColor(0xFFb5b6b9);
				tab_line3.setLayoutParams(lp2);
				proinfo2.setVisibility(View.VISIBLE);
				proinfo1.setVisibility(View.GONE);
				proinfo3.setVisibility(View.GONE);
			}else if(v.getId() ==R.id.tab3 ){
				tab_txt3.setTextColor(Color.parseColor("#1a3688"));
				tab_line3.setBackground(CommonUtil.getDrawable(R.drawable.tab_s3));
				tab_line3.setLayoutParams(lp1);
				tab_txt2.setTextColor(Color.parseColor("#000000"));
				tab_line2.setBackgroundColor(0xFFb5b6b9);
				tab_line2.setLayoutParams(lp2);
				tab_txt1.setTextColor(Color.parseColor("#000000"));
				tab_line1.setBackgroundColor(0xFFb5b6b9);
				tab_line1.setLayoutParams(lp2);
				proinfo3.setVisibility(View.VISIBLE);
				proinfo2.setVisibility(View.GONE);
				proinfo1.setVisibility(View.GONE);
			}
			return false;
		}
		return true;
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
				getData(true,true);
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
				getData(true,false);
				onLoad();
			}
		}, 1);

	}
}
