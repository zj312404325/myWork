package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.jymall.common.MyApplication;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.ImageFactory;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyConfirmDialog;
import com.example.administrator.jymall.view.MyListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_add_order)
public class AddOrderActivity extends TopActivity {
	
	@ViewInject(R.id.btn_tj)
	private Button btn_tj;
	
	private List<Map<String, Object>> dateMaps;
	
	private String ids="";
	private List<Map<String,String>> proNum =  new ArrayList<Map<String, String>>();
	
	private List<Map<String, Object>> resMaps = new ArrayList<Map<String, Object>>();
	
	private JSONObject resdata;
	
	private JSONObject address = null; //默认收获地址
	@ViewInject(R.id.tv_ad_contact)
	private TextView tv_ad_contact;
	@ViewInject(R.id.tv_ad_mobilephone)
	private TextView tv_ad_mobilephone;
	@ViewInject(R.id.tv_ad_addinfo)
	private TextView tv_ad_addinfo;
	@ViewInject(R.id.tv_agreement)
	private TextView tv_agreement;
	@ViewInject(R.id.ll_address)
	private LinearLayout ll_address;
	@ViewInject(R.id.ll_address1)
	private LinearLayout ll_address1;
	
	private JSONObject Invoice = null; //发票
	@ViewInject(R.id.ll_Invoice)
	private LinearLayout ll_Invoice;
	@ViewInject(R.id.ll_Invoice1)
	private LinearLayout ll_Invoice1;
	@ViewInject(R.id.tv_in_invoiceType)
	private TextView tv_in_invoiceType;
	@ViewInject(R.id.tv_in_invoiceInfo)
	private TextView tv_in_invoiceInfo;
	@ViewInject(R.id.tv_in_invoiceContent)
	private TextView tv_in_invoiceContent;
	
	private JSONObject bankAccount = null; //银行
	@ViewInject(R.id.ll_bank)
	private LinearLayout ll_bank;
	@ViewInject(R.id.ll_bank1)
	private LinearLayout ll_bank1;
	@ViewInject(R.id.tv_bk_bankName)
	private TextView tv_bk_bankName;
	@ViewInject(R.id.tv_bk_bankAdd)
	private TextView tv_bk_bankAdd;
	@ViewInject(R.id.tv_bk_bankNo)
	private TextView tv_bk_bankNo;

	@ViewInject(R.id.tv_goodsCount)
	private TextView tv_goodsCount;
	@ViewInject(R.id.tv_goodsMoney)
	private TextView tv_goodsMoney;
	@ViewInject(R.id.tv_transFee)
	private TextView tv_transFee;

	@ViewInject(R.id.index_address)
	private Button index_address;
	@ViewInject(R.id.index_invoice)
	private Button index_invoice;
	@ViewInject(R.id.index_bank)
	private Button index_bank;
	
	
	private List<Map<String,Object>> prolist = new ArrayList<Map<String,Object>>();
	private SimpleAdapter sap;
	@ViewInject(R.id.mlv_prolist)
	private MyListView mlv_prolist;
	
	@ViewInject(R.id.tv_totalPrice)
	private TextView tv_totalPrice;

	@ViewInject(R.id.et_buyMemo)
	private EditText et_buyMemo; //买家备注

	@ViewInject(R.id.cb_agree)
	private CheckBox cb_agree;
	
	private String annex;
	private String goodsMoney;
	private String goodsCount;

	private String orderType;
	private String customid;

	private String TEMP_IMAGE_PATH;  	
	private String TEMP_IMAGE_PATH1= Environment.getExternalStorageDirectory().getPath()+"/temp1.png"; 
	private Bitmap bitmap1 = null;
	private MyConfirmDialog mcd1 = null;

	private double totalCount=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		progressDialog.hide();
		super.title.setText("确认订单");

		Intent intent = this.getIntent();
		dateMaps = (List<Map<String, Object>>) intent.getSerializableExtra("data");
		goodsMoney =intent.getStringExtra("goodsMoney");
		goodsCount =intent.getStringExtra("goodsCount");
		orderType =intent.getStringExtra("orderType");

		if(orderType.equals("order")) {
			for (int i = 0; i < dateMaps.size(); i++) {
				if (dateMaps.get(i).get("isCheck").toString().equals("1")) {
					ids += dateMaps.get(i).get("id").toString() + ",";
					Map<String, String> temp = new HashMap<String, String>();
					temp.put("k", "pro_Num" + dateMaps.get(i).get("id").toString());
					temp.put("v", dateMaps.get(i).get("quantity").toString());
					proNum.add(temp);
					totalCount+=FormatUtil.toDouble(dateMaps.get(i).get("quantity").toString());
				}
			}
		}
		else if(orderType.equals("fastMatch")){
			customid=intent.getStringExtra("customid");
			for (int i = 0; i < dateMaps.size(); i++) {
				if(dateMaps.get(i).get("hasCheck").toString().equals("1")){
					if (dateMaps.get(i).get("isCheck").toString().equals("1")) {
						ids += dateMaps.get(i).get("id").toString() + ",";
						Map<String, String> temp = new HashMap<String, String>();
						temp.put("k", "pro_Num" + dateMaps.get(i).get("id").toString());
						temp.put("v", dateMaps.get(i).get("quantity").toString());
						proNum.add(temp);
						totalCount+=FormatUtil.toDouble(dateMaps.get(i).get("quantity").toString());
					}
				}
			}
		}
		else if(orderType.equals("orderMatch")){
			customid=intent.getStringExtra("customid");
			for (int i = 0; i < dateMaps.size(); i++) {
				if(dateMaps.get(i).get("hasCheck").toString().equals("1")){
					if (dateMaps.get(i).get("isCheck").toString().equals("1")) {
						ids += dateMaps.get(i).get("id").toString() + ",";
						Map<String, String> temp = new HashMap<String, String>();
						temp.put("k", "pro_Num" + dateMaps.get(i).get("id").toString());
						temp.put("v", dateMaps.get(i).get("quantity").toString());
						proNum.add(temp);
						totalCount+=FormatUtil.toDouble(dateMaps.get(i).get("quantity").toString());
					}
				}
			}
		}

		if(ids.equals("")){
			CommonUtil.alter("请选择要购买的产品！");
			finish();
		}
		else {
			ids = ids.substring(0, ids.length() - 1);
		}
		sap = new ProSimpleAdapter(this, prolist, R.layout.listview_doorderinfo, 
				new String[]{"proName"}, 
				new int[]{R.id.tv_proName});
		mlv_prolist.setAdapter(sap);

		if(orderType.equals("order")) {
			getOrderData();
		}
		else{
			getMatchData();
		}
	}
	
	@Event(R.id.btn_tj)
	private void btn_tjclick(View v){
		try{
			String bankid="";
			String invoiceid="";
			if(bankAccount== null){
				/*Toast.makeText(getApplicationContext(), "请先设置银行账号！",Toast.LENGTH_LONG*10000).show();
				return;*/
			}
			else{
				bankid=bankAccount.getString("id");
			}
			if(address== null){
				Toast.makeText(getApplicationContext(), "请先设置收货地址！",Toast.LENGTH_LONG*10000).show();
				return;
			}
			if(Invoice== null){
				/*Toast.makeText(getApplicationContext(), "请先设置开票资料！",Toast.LENGTH_LONG*10000).show();
				return;*/
			}
			else{
				invoiceid=Invoice.getString("id");
			}
			if(!cb_agree.isChecked()){
				Toast.makeText(getApplicationContext(), "请阅读并同意金赢网交易条款!",Toast.LENGTH_LONG*10000).show();
				return;
			}

			progressDialog.show();
			Map<String, String> maps= new HashMap<String, String>();
			maps.put("serverKey", super.serverKey);
			maps.put("ids", ids);
			maps.put("orderType", orderType);
			maps.put("customid", customid);
			maps.put("subBankId", bankid);
			maps.put("subAddressId", address.getString("id"));
			maps.put("subInvoiceId", invoiceid);
			maps.put("buyMemo", et_buyMemo.getText().toString());
			XUtilsHelper.getInstance().post("app/doMallOrder.htm", maps,new XUtilsHelper.XCallBack(){

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
							CommonUtil.alter(res.get("msg").toString());
							startActivity(new Intent(AddOrderActivity.this,UserCenterActivity.class));							
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			});
		}
		catch(Exception e){e.printStackTrace();}
		
	}
	
	
	private void getOrderData(){
		progressDialog.show();
		resMaps.clear();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("ids", ids);
		for(Map m:proNum){
			maps.put(m.get("k").toString(), m.get("v").toString());
		}
		dateMaps.clear();
		XUtilsHelper.getInstance().post("app/prepareMallOrder.htm", maps,new XUtilsHelper.XCallBack(){

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
						address = FormatUtil.toJSONObject(res.getString("address"));
						setAddress();
						Invoice =  FormatUtil.toJSONObject(res.getString("invoice"));
						setInvoice();
						bankAccount =  FormatUtil.toJSONObject(res.getString("bankaccount"));
						setBank();
						
						JSONArray prolistjson = res.getJSONArray("productList");
						tv_goodsCount.setText(FormatUtil.toString(totalCount)+"件");
						tv_goodsMoney.setText(goodsMoney+"元");
						tv_totalPrice.setText(FormatUtil.toString(res.getDouble("totalPrice")));
						for(int i=0;i<prolistjson.length();i++){
							Map<String,Object> maptemp = new HashMap<String, Object>();
							JSONObject product = prolistjson.getJSONObject(i);
							JSONObject prop = product.getJSONObject("mallProductProp");
							JSONObject attr = product.getJSONObject("mallProductAttr");
							maptemp.put("proName", product.get("proname"));
							maptemp.put("specifno", "规格："+prop.getString("var1")+" "+" 品牌："+attr.getString("var1"));
							maptemp.put("salePrice",FormatUtil.toString( product.getDouble("saleprice")));
							maptemp.put("picUrl", product.get("picUrl"));
							maptemp.put("stockQty",FormatUtil.toString( product.getDouble("stockqty")));
							maptemp.put("unit", product.get("unit"));
							prolist.add(maptemp);
						}
						sap.notifyDataSetChanged();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		
	}

	private void getMatchData(){
		progressDialog.show();
		resMaps.clear();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("ids", ids);
		for(Map m:proNum){
			maps.put(m.get("k").toString(), m.get("v").toString());
		}
		dateMaps.clear();
		XUtilsHelper.getInstance().post("app/prepareMatchOrder.htm", maps,new XUtilsHelper.XCallBack(){

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
						address = FormatUtil.toJSONObject(res.getString("address"));
						setAddress();
						Invoice =  FormatUtil.toJSONObject(res.getString("invoice"));
						setInvoice();
						bankAccount =  FormatUtil.toJSONObject(res.getString("bankaccount"));
						setBank();

						JSONArray detailList = res.getJSONArray("detailList");

						tv_goodsCount.setText(goodsCount);
						tv_goodsMoney.setText(goodsMoney);
						tv_totalPrice.setText(FormatUtil.toString(res.getDouble("totalPrice")));
						for(int i=0;i<detailList.length();i++){
							Map<String,Object> maptemp = new HashMap<String, Object>();
							maptemp.put("proName", detailList.getJSONObject(i).get("proName"));
							maptemp.put("specifno", "规格："+detailList.getJSONObject(i).get("proSpec")+" "+" 品牌："+detailList.getJSONObject(i).get("brand"));
							maptemp.put("proSpec", detailList.getJSONObject(i).get("proSpec"));
							maptemp.put("brand",detailList.getJSONObject(i).get("brand"));
							maptemp.put("salePrice", detailList.getJSONObject(i).get("salePrice"));
							maptemp.put("stockQty",detailList.getJSONObject(i).get("quantity"));
							maptemp.put("unit", detailList.getJSONObject(i).get("unit"));
							maptemp.put("money", detailList.getJSONObject(i).get("money"));
							prolist.add(maptemp);
						}
						sap.notifyDataSetChanged();
					}

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
					convertView=mInflater.inflate(R.layout.listview_doorderinfo, null); 
				} 
				ImageView img_picUrl = (ImageView)convertView.findViewById(R.id.img_picUrl);
				TextView tv_proName = (TextView)convertView.findViewById(R.id.tv_proName);
				TextView pro_specifno = (TextView)convertView.findViewById(R.id.pro_specifno);
				TextView pro_salePrice = (TextView)convertView.findViewById(R.id.pro_salePrice);
				TextView pro_stockQty = (TextView)convertView.findViewById(R.id.pro_stockQty);
				if(orderType.equals("order")) {
					XUtilsHelper.getInstance().bindCommonImage(img_picUrl, prolist.get(position).get("picUrl").toString(), true);
				}
				else if(orderType.equals("fastMatch")){
					img_picUrl.setBackgroundResource(R.drawable.pro_fast_match);
				}
				else if(orderType.equals("orderMatch")){
					img_picUrl.setBackgroundResource(R.drawable.pro_order_match);
				}
				tv_proName.setText(prolist.get(position).get("proName").toString());
				pro_specifno.setText(prolist.get(position).get("specifno").toString());
				if(FormatUtil.toDouble( prolist.get(position).get("salePrice").toString())==0){
					pro_salePrice.setText("面议");
				}
				else{
					pro_salePrice.setText(Html.fromHtml("￥" + prolist.get(position).get("salePrice").toString() + "<font color=\"#b1b1b1\">/" + prolist.get(position).get("unit").toString()+"</font>"));
				}
				pro_stockQty.setText("X "+prolist.get(position).get("stockQty").toString());				
			}
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}
	}
	
	/**
	 * 设置地区信息
	 */
	private void setAddress(){
		try{
			if(address != null){
				tv_ad_contact.setText(address.getString("contact"));
				tv_ad_addinfo.setText(address.getString("province")+"  "
						+address.getString("city")+"  "
						+address.getString("district")+"  "
						+address.getString("addr")+"  ");
				tv_ad_mobilephone.setText(address.getString("mobilephone"));
				ll_address1.setVisibility(View.GONE);
				ll_address.setVisibility(View.VISIBLE);
				if(address.getBoolean("addrdefault")){
					index_address.setVisibility(View.VISIBLE);
				}
				else{
					index_address.setVisibility(View.GONE);
				}
			}
			else{
				ll_address1.setVisibility(View.VISIBLE);
				ll_address.setVisibility(View.GONE);
			}
		}
		catch(Exception ep){ep.printStackTrace();}
	}
	
	/**
	 * 设置银行信息
	 */
	private void setBank(){
		try{
			if(bankAccount != null){
				tv_bk_bankName.setText(bankAccount.getString("bankName"));
				tv_bk_bankAdd.setText(bankAccount.getString("bankAdd"));
				tv_bk_bankNo.setText(bankAccount.getString("bankNo"));
				ll_bank1.setVisibility(View.GONE);
				ll_bank.setVisibility(View.VISIBLE);
				if(bankAccount.getBoolean("bankdefault")){
					index_bank.setVisibility(View.VISIBLE);
				}
				else{
					index_bank.setVisibility(View.GONE);
				}
			}
			else{
				ll_bank1.setVisibility(View.VISIBLE);
				ll_bank.setVisibility(View.GONE);
			}
		}
		catch(Exception ep){ep.printStackTrace();}
	}
	
	private void setInvoice(){
		try{
			if(Invoice != null){
				if(Invoice.getString("invoiceType").equals("VAT")){//
					tv_in_invoiceType.setText("增值税发票");
					tv_in_invoiceInfo.setText("单位名称："+Invoice.getString("companyName")+"\n"
							+"纳税人识别号："+Invoice.getString("taxNo")+"\n"
							+"注册地址："+Invoice.getString("registerAddress")+"\n"
							+"注册电话："+Invoice.getString("registerPhone")+"\n"
							+"开户银行："+Invoice.getString("bankName")+"\n"
							+"银行账户："+Invoice.getString("bankNo"));
				}
				else{
					tv_in_invoiceType.setText("普通发票");
					tv_in_invoiceInfo.setText(Invoice.getString("title"));
				}
				if(Invoice.getString("invoiceContent").equals("1")){
					tv_in_invoiceContent.setText("明细");
				}
				else{
					tv_in_invoiceContent.setText(Invoice.getString("invoiceContent"));
				}
				ll_Invoice.setVisibility(View.VISIBLE);
				ll_Invoice1.setVisibility(View.GONE);

				if(Invoice.getString("invoiceDefault").equals("1")){
					index_invoice.setVisibility(View.VISIBLE);
				}
				else{
					index_invoice.setVisibility(View.GONE);
				}
			}
			else{
				ll_Invoice.setVisibility(View.GONE);
				ll_Invoice1.setVisibility(View.VISIBLE);
			}
		}
		catch(Exception ep){ep.printStackTrace();}
	}
	
	
	private void tjClick(View v){
		startActivity(new Intent(getApplicationContext(), Order_Xh_Info_Activity.class));
	}
	
	@Event(value={R.id.ll_address1,R.id.ll_address},type=View.OnTouchListener.class)
	private boolean editAddress(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			Intent i = new Intent(getApplicationContext(),AddressOrderActivty.class);
			startActivityForResult(i, 11);
			return false;
		}
		return true;
	}
	
	@Event(value={R.id.ll_Invoice,R.id.ll_Invoice1},type=View.OnTouchListener.class)
	private boolean editInvoice(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			Intent i = new Intent(getApplicationContext(),InvoiceOrderActivity.class);
			startActivityForResult(i, 22);
			return false;
		}
		return true;
	}
	
	@Event(value={R.id.ll_bank,R.id.ll_bank1},type=View.OnTouchListener.class)
	private boolean editbank(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			Intent i = new Intent(getApplicationContext(),BankOrderActivity.class);
			startActivityForResult(i, 33);
			return false;
		}
		return true;
	}

	@Event(R.id.tv_agreement)
	private void agreementClick(View v){
		new AlertDialog.Builder(this)
				.setTitle("金赢网交易条款")
				.setMessage("第一章 总则\n" +
						"\n" +
						"第一条 为了保障金赢网用户的合法权益，维护金赢网的正常经营秩序，特制定本交易条款。\n" +
						"\n" +
						"第二条 金赢网用户在提供或者接受商品订购与配送服务时，应当遵守本交易条款。\n" +
						"\n" +
						"第三条 金赢网用户应当遵守法律、法规和规章等规定。对于任何违反法律、法规和规章等规定的行为，本交易条款已有规定的，适用本交易条款的相关规定；本交易条款尚无规定的，金赢网有权酌情予以处理。但金赢网对用户的处理并不免除其应承担的法律责任。\n" +
						"\n" +
						"第四条 金赢网有权随时修订本交易条款并在金赢网上进行公布。若金赢网用户不同意遵守本交易条款或者相关修订的，应立即停止使用金赢网的有关产品或者服务；若金赢网用户选择使用或者继续使用金赢网的有关产品或者服务的，视为同意遵守本交易条款或者相关修订。\n" +
						"\n" +
						"第五条 金赢网用户的各类交易行为，发生在金赢网交易条款修订之前的，适用当时的金赢网交易条款进行处理；发生在金赢网交易条款修订之后的，适用现行的金赢网交易条款进行处理。\n" +
						"\n" +
						"第二章 术语\n" +
						"\n" +
						"第六条 金赢网，指苏州金赢网电子商务有限公司，域名为jinying365.com。\n" +
						"\n" +
						"第七条 订单，指金赢网用户成交金赢网入驻商家的商品或者服务所形成的契约。\n" +
						"\n" +
						"第三章 交易流程\n" +
						"\n" +
						"第一节 注册金赢网用户\n" +
						"\n" +
						"第八条 互联网用户需要按照金赢网系统设置的注册流程完成注册，成为金赢网用户后，才能使用金赢网所提供的各项服务。\n" +
						"\n" +
						"第二节 要约\n" +
						"\n" +
						"第九条 入驻商家所发布的商品广告、价目表和声明等均不属于要约，在入驻商家处理了金赢网用户的订单之前，双方之间不存在任何契约关系。\n" +
						"\n" +
						"第十条 在下订单的同时，金赢网用户应确认自身的身份信息及收货地址、邮编和电话等资料准确无误，并对订单中提供的所有信息的真实性、准确性负责。\n" +
						"\n" +
						"第三节 生成订单\n" +
						"\n" +
						"第十一条 金赢网上商品的定价和可获性都在金赢网上予以明确注明。同时，入驻商家有权针对商品和订购数量进行限制。 每一项商品所标明的价格均不包含送货费，送货费用根据金赢网用户选择的送货方式的不同而异。")
				.setCancelable(true)
				.create().show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		   case 11: //地址
			   Bundle b=data.getExtras(); //data为B中回传的Intent
			   try{
				   address= FormatUtil.toJSONObject( b.getString("address"));//str即为回传的值
				   setAddress();
			   }
			   catch(Exception ep){ep.printStackTrace();}
		   break;
		   case 22: //发票
			   Bundle b1=data.getExtras(); //data为B中回传的Intent
			   try{
				   Invoice= FormatUtil.toJSONObject( b1.getString("Invoice"));//str即为回传的值
				   setInvoice();
			   }
			   catch(Exception ep){ep.printStackTrace();}
			break;
		   case 33: //发票
			   Bundle b3=data.getExtras(); //data为B中回传的Intent
			   try{
				   bankAccount= FormatUtil.toJSONObject( b3.getString("bank"));//str即为回传的值
				   setBank();
			   }
			   catch(Exception ep){ep.printStackTrace();}
			break;
		   default:
		   break;
		}
		if(resultCode==RESULT_OK){ 
			//营业执照
	           if(requestCode==101&&data!=null){  
	        	   progressDialog.show();
	        	   mcd1.dismiss();
	        	   Uri uri = data.getData();  
	               if(bitmap1 != null)//如果不释放的话，不断取图片，将会内存不够  
                	   bitmap1.recycle();  
	               TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
	               ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap1 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
	               //iv_annex.setImageBitmap(bitmap1);

	               Map<String, String> maps = new HashMap<String, String>();
	               maps.put("fileUploadeFileName", TEMP_IMAGE_PATH1.substring(TEMP_IMAGE_PATH1.lastIndexOf("/")+1));
	               maps.put("pathType","company");
	               Map<String, File> file = new HashMap<String, File>();
	               file.put("fileUploade",new File(TEMP_IMAGE_PATH1));
	               XUtilsHelper.getInstance().upLoadFile("fileUploadOkJson.htm", maps, file, new XUtilsHelper.XCallBack() {					
						@Override
						public void onResponse(String result) {
							progressDialog.hide();
							try{
								JSONObject res = FormatUtil.toJSONObject(result);
								if(res != null){
									if(res.get("d").equals("n")){
										CommonUtil.alter("图片上传失败");
									}
									else{
										annex=res.getString("fileUrl");	
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }
	           else if(requestCode==201){  
	        	   progressDialog.show();
	        	   mcd1.dismiss();
	        	   if(bitmap1 != null)//如果不释放的话，不断取图片，将会内存不够  
                	   bitmap1.recycle(); 
	        	   ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap1 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);  
	               //iv_annex.setImageBitmap(bitmap1);
	               Map<String, String> maps = new HashMap<String, String>();
	               maps.put("fileUploadeFileName", TEMP_IMAGE_PATH1.substring(TEMP_IMAGE_PATH1.lastIndexOf("/")+1));
	               maps.put("pathType","company");
	               Map<String, File> file = new HashMap<String, File>();
	               file.put("fileUploade",new File(TEMP_IMAGE_PATH1));
	               XUtilsHelper.getInstance().upLoadFile("fileUploadOkJson.htm", maps, file, new XUtilsHelper.XCallBack() {					
						@Override
						public void onResponse(String result) {
							progressDialog.hide();
							try{
								JSONObject res = FormatUtil.toJSONObject(result);
								if(res != null){
									if(res.get("d").equals("n")){
										CommonUtil.alter("图片上传失败");
									}
									else{
										annex=res.getString("fileUrl");
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }
		}
	}
	
	/*@Event(value=R.id.iv_annex,type=View.OnTouchListener.class)
	private boolean businesslicenseclick(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(mcd1==null){			
				mcd1=new MyConfirmDialog(AddOrderActivity.this, "上传充值凭证", "拍照上传", "本地上传");
				mcd1.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {					
					@Override
					public void doConfirm() {					   
					    TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";  
					    Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
					    Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));  
					    intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);  
					    startActivityForResult(intent1,201);  
					}					
					@Override
					public void doCancel() {  
	
					    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
					    intent.addCategory(Intent.CATEGORY_OPENABLE);
					    intent.setType("image*//*");
					    startActivityForResult(intent, 101);
					}
				});
			}
			mcd1.show();			
			return false;
		}
		return true;
	}*/
	


}
