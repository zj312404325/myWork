package com.example.administrator.helloworld;

import java.io.File;
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

import com.example.administrator.helloworld.CartActivity.InfoSimpleAdapter;
import com.example.administrator.helloworld.common.MyApplication;
import com.example.administrator.helloworld.common.TopActivity;
import com.example.administrator.helloworld.util.BigDecimalUtil;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.FormatUtil;
import com.example.administrator.helloworld.util.ImageFactory;
import com.example.administrator.helloworld.util.XUtilsHelper;
import com.example.administrator.helloworld.view.MyConfirmDialog;
import com.example.administrator.helloworld.view.MyListView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
	
	
	private List<Map<String,Object>> prolist = new ArrayList<Map<String,Object>>();
	private SimpleAdapter sap;
	@ViewInject(R.id.mlv_prolist)
	private MyListView mlv_prolist;
	
	@ViewInject(R.id.tv_totalPrice)
	private TextView tv_totalPrice;
	@ViewInject(R.id.tv_totalPrice1)
	private TextView tv_totalPrice1;
	
	@ViewInject(R.id.et_outType)
	private EditText et_outType;  //运输方式
	@ViewInject(R.id.et_buyMemo)
	private EditText et_buyMemo; //买家备注
	
	private String annex;
	
	@ViewInject(R.id.iv_annex)
	private ImageView iv_annex;
	private String TEMP_IMAGE_PATH;  	
	private String TEMP_IMAGE_PATH1= Environment.getExternalStorageDirectory().getPath()+"/temp1.png"; 
	private Bitmap bitmap1 = null;
	private MyConfirmDialog mcd1 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("这尼玛", "Create基老陈 ");

		super.onCreate(savedInstanceState);
		x.view().inject(this);
		progressDialog.hide();
		super.title.setText("确认订单");
		
		dateMaps = (List<Map<String, Object>>) getIntent().getSerializableExtra("data"); 
		for(int i=0;i<dateMaps.size();i++){
			List<Map<String, Object>> dateMapinfo= (List<Map<String, Object>>) dateMaps.get(i).get("buyCartList");
			for(int j=0;j<dateMapinfo.size();j++){
				if(dateMapinfo.get(j).get("isCheck").toString().equals("1")){
					ids += dateMapinfo.get(j).get("ID").toString()+",";
					Map<String,String> temp = new HashMap<String, String>();
					temp.put("k", "pro_Num"+dateMapinfo.get(j).get("ID").toString());
					temp.put("v", dateMapinfo.get(j).get("quantity").toString());
					proNum.add(temp);
				}
			}
		}
		if(ids.equals("")){
			CommonUtil.alter("请选择购买产品！");
			finish();
		}
		else
			ids = ids.substring(0, ids.length()-1);
		sap = new ProSimpleAdapter(this, prolist, R.layout.listview_doorderinfo, 
				new String[]{"proName"}, 
				new int[]{R.id.tv_proName});
		mlv_prolist.setAdapter(sap);		
		getDate();
		et_outType.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(AddOrderActivity.this);
				final String[] units ={"委托金赢物流","自提","卖家代运"};
				final String[] units1 ={"2","1","3"};
                builder.setItems(units, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    	et_outType.setText(units[which].toString());
                    	et_outType.setTag(units1[which].toString());
                    }
                });
                builder.show();	
			}
		});
	}
	
	@Event(R.id.btn_tj)
	private void btn_tjclick(View v){
		try{
			if(bankAccount== null){
				Toast.makeText(getApplicationContext(), "请先设置银行账号！！",Toast.LENGTH_LONG*10000).show();
				return;
			}
			if(address== null){
				Toast.makeText(getApplicationContext(), "请先设置收货地址！！",Toast.LENGTH_LONG*10000).show();
				return;
			}
			if(Invoice== null){
				Toast.makeText(getApplicationContext(), "请先设置开票资料！！",Toast.LENGTH_LONG*10000).show();
				return;
			}

			progressDialog.show();
			Map<String, String> maps= new HashMap<String, String>();
			maps.put("serverKey", super.serverKey);
			maps.put("ids", ids);
			maps.put("pickbankid", bankAccount.getString("id"));
			maps.put("addressId", address.getString("id"));
			maps.put("invoiceId", Invoice.getString("id"));
			maps.put("outType", et_outType.getTag().toString());
			maps.put("buyMemo", et_buyMemo.getText().toString());
			maps.put("annex", annex);
			XUtilsHelper.getInstance().post("app/doOrder.htm", maps,new XUtilsHelper.XCallBack(){

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
	
	
	private void getDate(){
		progressDialog.show();
		resMaps.clear();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("ids", ids);
		for(Map m:proNum){
			maps.put(m.get("k").toString(), m.get("v").toString());
		}
		dateMaps.clear();
		XUtilsHelper.getInstance().post("app/prepareDoOrder.htm", maps,new XUtilsHelper.XCallBack(){

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
						Invoice =  FormatUtil.toJSONObject(res.getString("Invoice"));
						setInvoice();
						bankAccount =  FormatUtil.toJSONObject(res.getString("bankAccount"));
						setBank();
						
						JSONArray prolistjson = res.getJSONArray("productList");
						tv_totalPrice.setText(FormatUtil.toString(res.getDouble("totalPrice")));
						tv_totalPrice1.setText(FormatUtil.toString(res.getDouble("totalPrice")));
						for(int i=0;i<prolistjson.length();i++){
							Map<String,Object> maptemp = new HashMap<String, Object>();
							JSONObject jsontemp1 = prolistjson.getJSONObject(i);
							JSONObject jsontemp = jsontemp1.getJSONObject("product");							
							maptemp.put("proName", jsontemp.get("proName"));
							maptemp.put("isFutures", jsontemp.get("isFutures"));
							if(jsontemp.getString("templateid").equals("0")){
								maptemp.put("specifno", "规格："+jsontemp1.getString("var1")+" "
										+" 材质："+jsontemp1.getString("proquality"));
							}
							else if(jsontemp.getString("templateid").equals("1")){
								maptemp.put("specifno", "规格："+jsontemp1.getString("var1")+" "
										+" 锌层："+jsontemp1.getString("var2")
										+" 包装方式："+jsontemp1.getString("var3"));
							}
							else if(jsontemp.getString("templateid").equals("2")){
								maptemp.put("specifno", "规格："+jsontemp1.getString("var1")+" "
										+" 颜色："+jsontemp1.getString("var4")
										+" 漆膜厚度："+jsontemp1.getString("var5"));
							}
							else if(jsontemp.getString("templateid").equals("3")){
								maptemp.put("specifno", "厚度："+jsontemp1.getString("var6")+" "
										+" 口径："+jsontemp1.getString("var7"));
							}
							else if(jsontemp.getString("templateid").equals("4") ||
									jsontemp.getString("templateid").equals("5")){
								maptemp.put("specifno", "规格："+jsontemp1.getString("var1")+" "
										+" 包装方式："+jsontemp1.getString("var3"));
							}
							maptemp.put("salePrice",FormatUtil.toString( jsontemp1.getDouble("saleprice")));
							maptemp.put("picUrl", jsontemp.get("picUrl"));
							maptemp.put("stockQty",FormatUtil.toString( jsontemp1.getDouble("quantity")));
							maptemp.put("unit", jsontemp.get("unit"));
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
				
				XUtilsHelper.getInstance().bindCommonImage(img_picUrl, 
						prolist.get(position).get("picUrl").toString(), true);
				tv_proName.setText(prolist.get(position).get("proName").toString());
				pro_specifno.setText(prolist.get(position).get("specifno").toString());
				if(FormatUtil.toDouble( prolist.get(position).get("salePrice").toString())==0){
					pro_salePrice.setText("面议");
				}
				else
					pro_salePrice.setText("￥"+prolist.get(position).get("salePrice").toString()+"/"
						+prolist.get(position).get("unit").toString());
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
				tv_ad_contact.setText("收货人："+address.getString("contact"));
				tv_ad_addinfo.setText(address.getString("province")+"  "
						+address.getString("city")+"  "
						+address.getString("district")+"  "
						+address.getString("addr")+"  ");
				tv_ad_mobilephone.setText(address.getString("mobilephone"));
				ll_address1.setVisibility(View.GONE);
				ll_address.setVisibility(View.VISIBLE);
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
	               iv_annex.setImageBitmap(bitmap1);

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
	               iv_annex.setImageBitmap(bitmap1);
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
	
	@Event(value=R.id.iv_annex,type=View.OnTouchListener.class)
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
					    intent.setType("image/*");
					    startActivityForResult(intent, 101);
					}
				});
			}
			mcd1.show();			
			return false;
		}
		return true;
	}
	


}
