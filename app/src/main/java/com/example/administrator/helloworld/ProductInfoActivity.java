package com.example.administrator.helloworld;

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

import com.example.administrator.helloworld.common.MyApplication;
import com.example.administrator.helloworld.common.UserActivity;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.FormatUtil;
import com.example.administrator.helloworld.util.XUtilsHelper;
import com.example.administrator.helloworld.view.ShufflingView;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@ContentView(R.layout.activity_product_info)
public class ProductInfoActivity extends UserActivity {
	
	
	@ViewInject(R.id.rl_userinfo)
	private RelativeLayout rl_userinfo;
	@ViewInject(R.id.rl_fl)
	private RelativeLayout rl_fl;
	@ViewInject(R.id.rl_index)
	private RelativeLayout rl_index;
	@ViewInject(R.id.rl_owner)
	private RelativeLayout rl_owner;
	

	@ViewInject(R.id.rl_ch_pro)
	private RelativeLayout rl_ch_pro;
	
	@ViewInject(R.id.allcomment)
	private Button allcomment;
	@ViewInject(R.id.addBuyCart)
	private Button addBuyCart;
	@ViewInject(R.id.addBuy)
	private Button addBuy;
	
	@ViewInject(R.id.toinfo)
	private RelativeLayout toinfo;
	
	@ViewInject(R.id.top_back)
	private ImageButton top_back;
	
	private String id;
	
	@ViewInject(R.id.shufflingView)
	private ShufflingView shufflingView;//产品图片轮换控件
	private List<String> mImageIds = new ArrayList<String>();//产品图片数据
	
	private JSONObject resdata;
	private JSONObject info;
	private JSONArray sellProductProps; //所有产品
	private JSONObject selectPro; //选中的产品
	
	@ViewInject(R.id.img_isFuture)
	private ImageView img_isFuture; //现货期货图标
	@ViewInject(R.id.tv_proName)
	private TextView tv_proName;//产品标题
	@ViewInject(R.id.tv_proCode)
	private TextView tv_proCode;
	@ViewInject(R.id.tv_brand)
	private TextView tv_brand;
	@ViewInject(R.id.tv_owner)
	private TextView tv_owner;
	@ViewInject(R.id.tv_ch_pro)
	private TextView tv_ch_pro;
	
	private String pId="";
	private double count=0;
	private String templateid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		progressDialog.hide();
		Intent i = this.getIntent();
		id = i.getStringExtra("id");
		getInfoData();
	}
	
	private void getInfoData(){
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("id", id);
		XUtilsHelper.getInstance().post("app/getProductInfo.htm", maps,new XUtilsHelper.XCallBack(){

			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				progressDialog.hide();
				JSONObject res;
				try {
					res = new JSONObject(result);
					setServerKey(res.get("serverKey").toString());
					
					if(res.get("d").equals("n")){
						CommonUtil.alter(res.get("msg").toString());
						MyApplication.getInstance().finishActivity();
					}
					else{
						resdata = (JSONObject)res.get("data");
						info = resdata.getJSONObject("info");
						sellProductProps = resdata.getJSONArray("propsArr");
						templateid  = resdata.getJSONObject("info").getString("templateid");
						if(sellProductProps.length()>0){
							selectPro = sellProductProps.getJSONObject(0);
						}
						else{
							tv_ch_pro.setText("此产品已经下架");
						}
						//count =FormatUtil.toDouble(resdata.getString("minNum"));
						
						String isFutures = info.getString("isFutures");
						getImgData(info.getJSONArray("showProductImg"));
						if(isFutures.equals("0")){
							img_isFuture.setBackgroundResource(R.drawable.xianhuo);
						}
						else if(isFutures.equals("1")){
							img_isFuture.setBackgroundResource(R.drawable.qihuo);
						}
						else if(isFutures.equals("2")){
							img_isFuture.setBackgroundResource(R.drawable.jiagong);
						}
						tv_proName.setText(info.getString("proName"));
						tv_proCode.setText("所属分类："+info.getString("categoryName"));
						tv_brand.setText("品牌："+info.getString("brand"));
						tv_owner.setText("公司名称："+info.getString("owner"));
						rl_owner.setTag(info.getString("ownerID"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
	}
	
	/**
	 * 得到产品图片信息
	 * @throws JSONException 
	 */
	public void getImgData(JSONArray imglist) throws JSONException{
		mImageIds.clear();
		for(int i=0;i<imglist.length();i++){
			mImageIds.add(CommonUtil.getStrings(R.string.img_url)+imglist.getJSONObject(i).get("imgPath").toString());						
		}
        shufflingView.setImagers(mImageIds);
        shufflingView.setOnitenClicklistener(new ShufflingView.OnitemClicklistener(){

			@Override
			public void setOnitemClicklistener(int position) {

			}
        	
        });
	}
	
	

	@Event(value={R.id.rl_ch_pro,R.id.allcomment,R.id.toinfo},type=View.OnTouchListener.class)
	private boolean ch_pro(View arg0, MotionEvent me){
		if(me.getAction() == MotionEvent.ACTION_UP){
			if(arg0.getId() == R.id.rl_ch_pro){
				gotoch();
			}
			else if(arg0.getId() == R.id.allcomment){
				Intent i = new Intent(getApplicationContext(),CommentActivity.class);
				startActivity(i);
			}
			else if(arg0.getId() == R.id.toinfo){
				Intent i = new Intent(getApplicationContext(),ProductInfoCActivity.class);
				i.putExtra("info",info.toString());
				startActivity(i);
			}
			return false;
		}
		return true;
	}
	
	
	private void gotoch(){
		try{
			if(sellProductProps.length()>0){
			boolean isFutures = resdata.getJSONObject("info").getString("isFutures").equals("1")||
					resdata.getJSONObject("info").getString("isFutures").equals("2");
			if(templateid.equals("0")){
				Intent i = new Intent(getApplicationContext(),ProductInfoChActivity.class);
				i.putExtra("data", resdata.toString());
				i.putExtra("selectPro", selectPro.toString());
				i.putExtra("count", count);
				startActivityForResult(i, 0);
			}
			else if(templateid.equals("1")){
				if(isFutures){
					Intent i = new Intent(getApplicationContext(),ProductInfoChActivity11.class);
					i.putExtra("data", resdata.toString());
					i.putExtra("selectPro", selectPro.toString());
					i.putExtra("count", count);
					startActivityForResult(i, 0);
				}
				else{
					Intent i = new Intent(getApplicationContext(),ProductInfoChActivity1.class);
					i.putExtra("data", resdata.toString());
					i.putExtra("selectPro", selectPro.toString());
					i.putExtra("count", count);
					startActivityForResult(i, 0);
				}
			}
			else if(templateid.equals("2")){
				if(isFutures){
					Intent i = new Intent(getApplicationContext(),ProductInfoChActivity22.class);
					i.putExtra("data", resdata.toString());
					i.putExtra("selectPro", selectPro.toString());
					i.putExtra("count", count);
					startActivityForResult(i, 0);
				}
				else{
					Intent i = new Intent(getApplicationContext(),ProductInfoChActivity2.class);
					i.putExtra("data", resdata.toString());
					i.putExtra("selectPro", selectPro.toString());
					i.putExtra("count", count);
					startActivityForResult(i, 0);
				}
			}
			else if(templateid.equals("3")){
				if(isFutures){
					Intent i = new Intent(getApplicationContext(),ProductInfoChActivity33.class);
					i.putExtra("data", resdata.toString());
					i.putExtra("selectPro", selectPro.toString());
					i.putExtra("count", count);
					startActivityForResult(i, 0);
				}
				else{
					Intent i = new Intent(getApplicationContext(),ProductInfoChActivity3.class);
					i.putExtra("data", resdata.toString());
					i.putExtra("selectPro", selectPro.toString());
					i.putExtra("count", count);
					startActivityForResult(i, 0);
				}
			}
			else if(templateid.equals("4") ||templateid.equals("5") ){
				if(isFutures){
					Intent i = new Intent(getApplicationContext(),ProductInfoChActivity44.class);
					i.putExtra("data", resdata.toString());
					i.putExtra("selectPro", selectPro.toString());
					i.putExtra("count", count);
					startActivityForResult(i, 0);
				}
				else{
					Intent i = new Intent(getApplicationContext(),ProductInfoChActivity4.class);
					i.putExtra("data", resdata.toString());
					i.putExtra("selectPro", selectPro.toString());
					i.putExtra("count", count);
					startActivityForResult(i, 0);
				}
			}
			}
			else{
				CommonUtil.alter("此产品暂无库存！！！");
			}
		}
		catch(Exception e){}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		   case RESULT_OK:
			   try{
				    Bundle b=data.getExtras(); //data为B中回传的Intent
				    pId=b.getString("pId");//str即为回传的值
				    count = b.getDouble("count");
				    for(int i=0;i<sellProductProps.length();i++){
				    	if(sellProductProps.getJSONObject(i).get("id").toString().equals(pId)){
				    		selectPro = sellProductProps.getJSONObject(i);
				    	}
				    }
				    if(templateid.equals("3")){
				    	tv_ch_pro.setText("选择："+selectPro.getString("var6")+"   共"+FormatUtil.toString(count)+info.getString("unit"));
				    }
				    else
				    	tv_ch_pro.setText("选择："+selectPro.getString("var1")+"   共"+FormatUtil.toString(count)+info.getString("unit"));
			   }
			   catch(Exception ep){ep.printStackTrace();}
		    break;
		default:
		    break;
		}
	}
	
	@Event(R.id.addBuyCart)
	private void addBuyCartClick(View v) throws JSONException{
		if(pId.equals("")){
			//CommonUtil.alter("你还没有选择产品");
			gotoch();
			return;
		}
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("proId", info.getString("ID"));
		maps.put("propId", pId);
		maps.put("quantity", ""+count);
		XUtilsHelper.getInstance().post("app/addBuyCart.htm", maps,new XUtilsHelper.XCallBack(){

			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				progressDialog.hide();
				JSONObject res;
				try {
					res = new JSONObject(result);
					setServerKey(res.get("serverKey").toString());
					
					if(res.get("d").equals("n")){
						CommonUtil.alter(res.get("msg").toString());
					}
					else{
						new AlertDialog.Builder(MyApplication.getInstance().currentActivity())
						.setTitle("金赢网")
						.setMessage("加入成功，是否继续购物？")
								.setPositiveButton("去结算", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent(getApplicationContext(),CartActivity.class);
										try {
											intent.putExtra("type",FormatUtil.toInt(resdata.getJSONObject("info").getString("isFutures")));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										startActivity(intent);
									}
								}).setNegativeButton("再看看",new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
													int which) {

									}
								}).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
	}
	
	@Event(R.id.addBuy)
	private void addBuyClick(View v) throws JSONException{
		if(pId.equals("")){
			//CommonUtil.alter("你还没有选择产品");
			gotoch();
			return;
		}
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("proId", info.getString("ID"));
		maps.put("propId", pId);
		maps.put("quantity", ""+count);
		XUtilsHelper.getInstance().post("app/addBuyCart.htm", maps,new XUtilsHelper.XCallBack(){

			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				progressDialog.hide();
				JSONObject res;
				try {
					res = new JSONObject(result);
					setServerKey(res.get("serverKey").toString());
					
					if(res.get("d").equals("n")){
						CommonUtil.alter(res.get("msg").toString());
					}
					else{		
						String bId = res.getString("id");
						Intent intent = new Intent(getApplicationContext(),AddOrder1Activity.class);					
						intent.putExtra("bId",bId);	
						intent.putExtra("pId",pId);	
						intent.putExtra("count",""+count);	
						startActivity(intent);						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			
		});
	}
	
	
	
	@Event(R.id.top_back)
	private void top_backclick(View v){
		MyApplication.getInstance().finishActivity();
	}
	
	@Event(value={R.id.rl_fl,R.id.rl_index,R.id.rl_userinfo,R.id.rl_owner},type=View.OnTouchListener.class)
	private boolean rlTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(v.getId() == R.id.rl_index){
				startActivity(new Intent(getApplicationContext(),IndexActivity.class));
			}
			else if(v.getId() == R.id.rl_fl){
				startActivity(new Intent(getApplicationContext(),CategoryActivity.class));
			}
			else if(v.getId() == R.id.rl_userinfo){
				startActivity(new Intent(getApplicationContext(),UserCenterActivity.class));
			}
			else if(v.getId() == R.id.rl_owner){
				Intent i = new Intent(getApplicationContext(),CompanyInfoActivity.class);
				i.putExtra("id",v.getTag().toString() );
				startActivity(i);
			}
			return false;
		}
		return true;
	}

}
