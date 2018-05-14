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
		else if(orderType.equals("fastMatch") || orderType.equals("fabFastMatch")){
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
		else if(orderType.equals("orderMatch") || orderType.equals("fabOrderMatch")){
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
				else if(orderType.equals("fabFastMatch")){
					img_picUrl.setBackgroundResource(R.drawable.pro_fab_fast_match);
				}
				else if(orderType.equals("fabOrderMatch")){
					img_picUrl.setBackgroundResource(R.drawable.pro_fab_order_match);
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
				.setMessage("一、总则\n" +
						"1.1 为了规范平台交易，促进买卖双方交易的顺利进行，根据公平、公正、平等的原则，制定本规则。\n" +
						"\n" +
						"1.2 本规则适用于在平台里进行的商品交易活动，平台的工作人员、会员、仓库在进行货物交易、结算、货物交收时必须遵守本规则。\n" +
						"\n" +
						"1.3 金赢工业超市平台是苏州金赢网电子商务有限公司自营的网上工业品交易超市，域名：mall.jinying365.com,是区别于其他的金赢网交易网站，其所提供的商品均为苏州金赢网电子商务有限公司所有。\n" +
						"\n" +
						"1.4 您确认，在您完成注册程序或者使用平台服务时，您应当是具备完全民事权利能力和与所从事的民事行为相适应的行为能力的自然人、法人或其他组织。若您不具备前述主体资格，请勿使用平台的服务，否则您及您的监护人应承担因此而导致的一切后果，且平台有权注销（永久冻结）您的账户，并向您及您的监护人索偿。如您代表一家公司或其他法律主体在平台注册或在金赢网注册，则您声明和保证，您有权使该公司或该法律主体受本协议“条款”的约束。\n" +
						"\n" +
						"1.5 您明确同意，平台有权根据法律法规及运营需要，不时对本协议和条款进行修改并公布在平台，该等协议和条款已经公布，立即生效，平台无须另行通知。应主动不时查阅或注意平台的公告，若不同意平台的修改，应立即停止使用平台。若继续使用平台的服务，则视为接受平台对本协议和条款的修改。\n" +
						"\n" +
						"1.6 您在金赢网注册成为金赢网会员时，即可在平台里进行采购，也可在金赢网平台使用，且均认可其在平台上产生的订单是真实、有效且具有法律效力的。\n" +
						"\n" +
						"1.7 平台客服热线：0512-52686666，客服邮箱：kf@jinying365.com。\n" +
						"\n" +
						"二、须知\n" +
						"2.1 收货人\n" +
						"\n" +
						"您需填写真实有效的收货地址，如因您填写的收货地址有误而导致货物无法准确及时送达而产生的纠纷由该自行承担负责。\n" +
						"\n" +
						"2.2 发票信息\n" +
						"\n" +
						"您需填写真实有效的发票信息，如因您填写的发票信息有误而导致发票开错，由您自行承担责任。\n" +
						"\n" +
						"2.3 银行账户管理\n" +
						"\n" +
						"您需填写真实有效的银行账户信息，如因您填写的银行账户信息有误而导致发票开错，由您自行承担责任。\n" +
						"\n" +
						"2.4 合同模板\n" +
						"\n" +
						"苏州金赢网电子商务有限公司在合同上盖的电子印章都视同为与盖实体章相同的法律效力。\n" +
						"\n" +
						"2.5 合同扫描件\n" +
						"\n" +
						"您在平台里下载的合同模板填写您的信息后，打印出来签名并盖公章后扫描，经您签名盖章确认的扫描件视同原件。\n" +
						"\n" +
						"2.6 评价\n" +
						"\n" +
						"您应该在尊重事实的基础上，遵循公平、公正、平等的原则上理性发表评论，否则经平台调查该评论与事实严重不符，则平台有权要求您修改，并在平台上对您的行为进行公告，平台有权视情节严重的作出加入黑名单或注销您的账户。\n" +
						"\n" +
						"三、交易流程\n" +
						"3.1 订单\n" +
						"\n" +
						"3.1.1 平台发布商品信息，商品信息包括但不限于以下内容：品名、规格、价格、数量、重量、品牌等。\n" +
						"\n" +
						"3.1.2 买方须先下载模板填写采购需求，并上传采购需求，平台筛选商品生成订单，符合买方采购需求的，则买方同意生成的订单；若买方不同意，则订单取消，不视为违约。\n" +
						"\n" +
						"3.1.3 若买方需要定制化的商品，买方上传采购需求（含图纸），平台自收到采购需求之日起四个工作日内回复是否生成订单；若平台已生成订单，则由买方确认是否同意生成的订单；若平台未生成订单，则采购需求取消，不视为平台的违约。\n" +
						"\n" +
						"3.2 付款\n" +
						"\n" +
						"3.2.1 买方确认生成的订单后，买方须在规定时间内支付足额的订单货款（定制化的，则按照订单款的30%支付订金，剩余货款须在卖方催促付款时付清）。\n" +
						"\n" +
						"3.2.2 买方由两种付款方式选择；①现结，含线上网银支付和线下网银支付；②信用支付，可月结或者季度结算。详见如何支付。\n" +
						"\n" +
						"3.3 发货\n" +
						"\n" +
						"卖方收到订单款后（信用支付除外）按照双方约定或者承诺的时间送货。若库存有现货的，自收到订单款两日内发货；若库存没有现货，则发货时间以买卖双方约定为准。\n" +
						"\n" +
						"3.4 运输方式\n" +
						"\n" +
						"平台直送或者通过快递公司或物流公司送货。（详见配送方式）\n" +
						"\n" +
						"3.5 商品质量\n" +
						"\n" +
						"超市平台订单中所涉及的商品质量标准，若无特别规定，按商品出厂标准执行，若没有出厂标准，则按照国家标准执行。\n" +
						"\n" +
						"3.6 商品签收\n" +
						"\n" +
						"3.6.1 收货人必须是实名认证的买方或者经过买方指定的收货人。否则卖方有权拒绝交货，其所产生的法律后果由买方承担。\n" +
						"\n" +
						"3.6.2 收到商品时，请确认商品正确（包括商品名、品牌、型号、数量、包装、外观等）、无损坏后签收，如商品外箱贴有易碎标识。请务必当场开箱检查箱内商品是否有破损。如外包装有明显损坏迹象，可及时通知平台，并拒绝签收，平台会处理并承担由此而产生的运输费用。\n" +
						"\n" +
						"若买方已经完好签收，则平台不再受理关于此商品有误或有损坏的投诉。\n" +
						"\n" +
						"3.6.3 买方签收商品后，须在平台中确认收货状态。否则一般以订单对应的快递或物流公司的签收信息作为买方收货依据。若买方不确认收货，也不提出退换货要求的，订单发货7天后默认为已收货状态。\n" +
						"\n" +
						"3.7 价格\n" +
						"\n" +
						"平台里所有报价即成交价均为该商品的含税价格。\n" +
						"\n" +
						"四、售后服务/品质承诺\n" +
						"4.1 不适用无质量异议的退换货情形。\n" +
						"\n" +
						"4.1.1 定制件信息填写错误的。\n" +
						"\n" +
						"4.1.2 影响二次销售的。\n" +
						"\n" +
						"4.1.3 人为损害的商品。\n" +
						"\n" +
						"4.1.4 加工定制件。\n" +
						"\n" +
						"4.1.5 7天默认收货的。\n" +
						"\n" +
						"4.2 7天无理由退换货原则（标准件）\n" +
						"\n" +
						"（详见7天无理由退换货原则（标准件））\n" +
						"\n" +
						"4.3 有质量问题的退、换货原则\n" +
						"\n" +
						"被卖方确认或者经有资质的鉴定机构鉴定为性能故障的质量问题，则卖方根据买方的申请，进行退货或换货处理。\n" +
						"\n" +
						"五、违约责任\n" +
						"5.1 买方连续超过三次或三次以上提交订单而不支付货款或被平台认定是恶意提交订单而不支付货款的，平台将会暂停买方在平台的交易权限。须其以书面申请并经平台审核通过后方可重新开通。\n" +
						"\n" +
						"5.2 买方（定制化）逾期付款，逾期付款 15 日内，每日按照订单价款的千分之五计算滞纳金，超过15日未付款的，视为取消订单，已收取的订金不予退还，且有权要求买方支付滞纳金。\n" +
						"\n" +
						"5.3 （信用支付）买方付款逾期一天，则超市平台将冻结买方的账户，已生成订单未发货的，暂停发货，且卖方有权每日按照订单总额的千分之五收取滞纳金，若造成卖方损失的，包括但不限于滞纳金、赔偿金、诉讼费、律师费等均由买方承担。\n" +
						"\n" +
						"六、其他\n" +
						"6.1 若针对同一行为或情形，本规则与金赢网平台发布的其他规则约定不一致的，以本规则为准。\n" +
						"\n" +
						"6.2 如您就本规则或因执行本规则交易过程中产生争议，双方应协商解决，解决不成的，任一方均可向苏州金赢网电子商务有限公司注册地所在地法院起诉。\n" +
						"\n" +
						"6.3 本规则最终由苏州金赢网电子商务有限公司解释\n")
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
