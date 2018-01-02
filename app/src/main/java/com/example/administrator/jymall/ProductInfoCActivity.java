package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.administrator.jymall.util.DensityUtil;
import com.example.administrator.jymall.util.URLImageParser;
import com.example.administrator.jymall.util.XUtilsHelper;
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

@ContentView(R.layout.activity_product_info_c)
public class ProductInfoCActivity extends TopActivity {
	
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
	@ViewInject(R.id.gv)
	private MyGridView gv;
	
	

	public List<Map<String, Object>> data_list= new ArrayList<Map<String, Object>>();
	private SimpleAdapter sap;
	
	private JSONObject info;
	@ViewInject(R.id.tv_proDesc)
	private TextView tv_proDesc;
	@ViewInject(R.id.tv_proArea)
	private TextView tv_proArea;
	@ViewInject(R.id.tv_brand)
	private TextView tv_brand;
	@ViewInject(R.id.tv_categoryName)
	private TextView tv_categoryName;
	@ViewInject(R.id.tv_storename)
	private TextView tv_storename;
	@ViewInject(R.id.tv_storekw)
	private TextView tv_storekw;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		//super.title.setText("产品详情");
		Intent i = this.getIntent();
		try {
			info = new JSONObject(i.getStringExtra("info"));
			URLImageParser p = new URLImageParser(tv_proDesc, ProductInfoCActivity.this);
			String html = info.getString("prodesc");
			Spanned htmlSpan = Html.fromHtml(html, p, null);
			tv_proDesc.setText(htmlSpan.toString());
			tv_brand.setText(info.getJSONObject("mallProductAttr").get("var1").toString());
			tv_categoryName.setText(info.getString("categorymame"));

			String [] from ={"proName"};
			int [] to = {R.id.protitle};
			sap = new ProSimpleAdapter(this, data_list, R.layout.list_indexprolist, from, to);
			gv.setAdapter(sap);
			getDate();
		} catch (JSONException e) {
			e.printStackTrace();
			progressDialog.hide();
		}
	}

	private void getDate() throws JSONException{
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("comId", info.getString("ownerID"));
		XUtilsHelper.getInstance().post("app/getProductListTJ.htm", maps,new XUtilsHelper.XCallBack(){

			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				JSONObject res;
				try {
					res = new JSONObject(result);
					setServerKey(res.get("serverKey").toString());
					JSONArray resjarr = (JSONArray)res.get("data");
					for(int i=0;i<resjarr.length();i++){
						Map<String, Object> dateMap = new HashMap<String, Object>();
						dateMap.put("isfutures", resjarr.getJSONObject(i).get("isfuture"));
						dateMap.put("picUrl", resjarr.getJSONObject(i).get("proImage"));
						dateMap.put("proName", resjarr.getJSONObject(i).get("proName"));
						dateMap.put("salePrice", resjarr.getJSONObject(i).get("salePrice"));
						dateMap.put("iD", resjarr.getJSONObject(i).get("id"));
						data_list.add(dateMap);
						
					}
					sap.notifyDataSetChanged();
					progressDialog.hide();
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
				holder=null;
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.list_indexprolist, null); 
					holder=new ViewHolder(); 
					x.view().inject(holder,convertView); 
					convertView.setTag(holder);
				} 
				else{
					holder=(ViewHolder) convertView.getTag(); 
				} 
				holder.protitle.setText(data_list.get(position).get("proName").toString());
				holder.promoney.setText("￥"+data_list.get(position).get("salePrice").toString());
				XUtilsHelper.getInstance().bindCommonImage(holder.proimg, data_list.get(position).get("picUrl").toString(), true);

				LinearLayout.LayoutParams lp1 =new LinearLayout.
		        		LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		        lp1.setMargins(0, 0, DensityUtil.dip2px( 2), DensityUtil.dip2px( 4));
		        LinearLayout.LayoutParams lp2 =new LinearLayout.
		        		LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		        lp2.setMargins(DensityUtil.dip2px( 2), 0, 0, DensityUtil.dip2px( 4));
		        if(position%2 == 0){
		        	holder.pro.setLayoutParams(lp1);
		        }
		        else
		        {
		        	holder.pro.setLayoutParams(lp2);
		        }
				
				
				holder.pro.setOnTouchListener(new View.OnTouchListener() {
						
						@Override
						public boolean onTouch(View arg0, MotionEvent e) {
							if(e.getAction() == MotionEvent.ACTION_UP){
								Intent i = new Intent(getApplicationContext(),ProductInfoActivity.class);
								i.putExtra("id", data_list.get(position).get("iD").toString());
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

	class ViewHolder{ 
		@ViewInject(R.id.proimg)
		private ImageView proimg;
		@ViewInject(R.id.protitle)
		private TextView protitle;
		@ViewInject(R.id.promoney)
		private TextView promoney;
		@ViewInject(R.id.pro)
		private RelativeLayout pro;
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
				tab_txt1.setTextColor(Color.parseColor("#0083c8"));
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
				tab_txt2.setTextColor(Color.parseColor("#0083c8"));
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
				tab_txt3.setTextColor(Color.parseColor("#0083c8"));
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

	
}
