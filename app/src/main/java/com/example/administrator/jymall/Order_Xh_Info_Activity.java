package com.example.administrator.jymall;

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

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyConfirmDialog;
import com.example.administrator.jymall.view.MyListView;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@ContentView(R.layout.activity_order__xh__info)
public class Order_Xh_Info_Activity extends TopActivity {
	
	private String id; //订单ID
	@ViewInject(R.id.btn_topay)
	private Button btn_topay;
	@ViewInject(R.id.btn_tosh)
	private Button btn_tosh;
	
	private JSONObject order;
	
	@ViewInject(R.id.tv_orderStatus)
	private TextView tv_orderStatus;
	
	@ViewInject(R.id.tv_orderNo)
	private TextView tv_orderNo;
	
	@ViewInject(R.id.tv_createDate)
	private TextView tv_createDate;
	
	@ViewInject(R.id.tv_contact)
	private TextView tv_contact;
	
	@ViewInject(R.id.tv_mobilePhone)
	private TextView tv_mobilePhone;
	
	@ViewInject(R.id.tv_buyAddr)
	private TextView tv_buyAddr;
	
	@ViewInject(R.id.tv_postCode)
	private TextView tv_postCode;
	
	@ViewInject(R.id.tv_owner)
	private TextView tv_owner;
	
	@ViewInject(R.id.list_orderinfo)
	private MyListView list_orderinfo;
	public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
	
	
	@ViewInject(R.id.tv_invoiceType)
	private TextView tv_invoiceType;
	
	@ViewInject(R.id.ll_invoiceTypeCOMMON)
	private LinearLayout ll_invoiceTypeCOMMON;
	
	@ViewInject(R.id.ll_invoiceTypeVAT)
	private LinearLayout ll_invoiceTypeVAT;
	
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	
	@ViewInject(R.id.tv_invoiceContent)
	private TextView tv_invoiceContent;
	
	@ViewInject(R.id.tv_companyName)
	private TextView tv_companyName;
	
	@ViewInject(R.id.tv_taxNo)
	private TextView tv_taxNo;
	
	@ViewInject(R.id.tv_registerAddress)
	private TextView tv_registerAddress;
	
	@ViewInject(R.id.tv_registerPhone)
	private TextView tv_registerPhone;
	
	@ViewInject(R.id.tv_bankName)
	private TextView tv_bankName;
	
	@ViewInject(R.id.tv_bankNo)
	private TextView tv_bankNo;
	
	@ViewInject(R.id.tv_invoiceContent1)
	private TextView tv_invoiceContent1;
	
	@ViewInject(R.id.tv_productMoney)
	private TextView tv_productMoney;
	
	@ViewInject(R.id.tv_feeMoney)
	private TextView tv_feeMoney;
	
	@ViewInject(R.id.tv_machiningMoney)
	private TextView tv_machiningMoney;
	
	@ViewInject(R.id.tv_dealsMoney)
	private TextView tv_dealsMoney;
	
	@ViewInject(R.id.tv_money)
	private TextView tv_money;
	
	@ViewInject(R.id.rl_paytype)
	private RelativeLayout rl_paytype;
	@ViewInject(R.id.tv_isOnline)
	private TextView tv_isOnline;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("订单详情");
		progressDialog.hide();
		
		Intent i = this.getIntent();
		id = i.getStringExtra("id");
		datainit();
	}
	
	private void datainit(){
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("id", id);
		XUtilsHelper.getInstance().post("app/myBuyOrderDetail.htm", maps,new XUtilsHelper.XCallBack(){

			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				progressDialog.hide();
				JSONObject res;
				try {
					res = new JSONObject(result);					
					setServerKey(res.get("serverKey").toString());					
					order = res.getJSONObject("order");
					int orderStatus = order.getInt("orderStatus");
					int isOnline = order.getInt("isOnline");
					int refundstatus = order.getInt("refundstatus");
					int refundtype = order.getInt("refundtype");
					
					if(isOnline == 2){
						tv_isOnline.setText("线下支付");
					}
					else{
						tv_isOnline.setText("线上支付");
					}					
					if(orderStatus == 0){
						tv_orderStatus.setText("等待卖家审核");
						rl_paytype.setVisibility(View.GONE);
					}
					else if(orderStatus == 1){
						tv_orderStatus.setText("等待付款");
						btn_topay.setVisibility(View.VISIBLE);
						rl_paytype.setVisibility(View.GONE);
					}
					else if(orderStatus == 2 && isOnline ==2 && refundstatus == 0){
						tv_orderStatus.setText("已线下付款");
					}
					else if(orderStatus == 2 && isOnline !=2 && refundstatus == 0){
						tv_orderStatus.setText("买家已付款");
					}
					else if(orderStatus == 3){
						tv_orderStatus.setText("卖家已收款");
					}
					else if(orderStatus == 4){
						tv_orderStatus.setText("等待买家收货");
						btn_tosh.setVisibility(View.VISIBLE);
					}
					else if(orderStatus == 5){
						tv_orderStatus.setText("买家已收货");
					}
					else if(orderStatus == 6){
						tv_orderStatus.setText("交易成功");
					}
					else if(orderStatus == 7){
						tv_orderStatus.setText("交易取消");
					}
					if(refundstatus == 1){
						if(refundtype==0){
							tv_orderStatus.setText("退货确认中");
						}
						else if(refundtype==1){
							tv_orderStatus.setText("退款确认中");
						}
					}
					else if(refundstatus == 2){
						if(refundtype==0){
							tv_orderStatus.setText("卖家同意退货");
						}
						else if(refundtype==1){
							tv_orderStatus.setText("卖家同意退款");
						}
					}
					else if(refundstatus == 4){
						tv_orderStatus.setText("等待卖家收货");
					}
					tv_orderNo.setText(order.getString("orderNo"));
					tv_createDate.setText(order.getString("createDate"));
					tv_contact.setText(order.getString("contact"));
					tv_mobilePhone.setText(order.getString("mobilePhone"));
					tv_buyAddr.setText(order.getString("buyAddr"));
					tv_postCode.setText(order.getString("postCode"));
					tv_owner.setText(order.getString("owner"));
					
					String invoiceType = order.getString("invoiceType");
					String invoiceContent = invoiceType.equals("VAT")?order.getString("invoiceContent"):"明细";
					if(invoiceType.equals("COMMON")){
						tv_invoiceType.setText("普通发票");
						ll_invoiceTypeCOMMON.setVisibility(View.VISIBLE);
						ll_invoiceTypeVAT.setVisibility(View.GONE);
						tv_title.setText("抬头："+order.getString("title"));
						tv_invoiceContent.setText("发票内容："+invoiceContent);
					}
					else{
						tv_invoiceType.setText("增值税发票");
						ll_invoiceTypeCOMMON.setVisibility(View.GONE);
						ll_invoiceTypeVAT.setVisibility(View.VISIBLE);
						tv_companyName.setText("单位名称："+order.getString("companyName"));
						tv_taxNo.setText("纳税人识别号："+order.getString("taxNo"));
						tv_registerAddress.setText("注册地址："+order.getString("registerAddress"));
						tv_registerPhone.setText("注册电话："+order.getString("registerPhone"));
						tv_bankName.setText("开户银行："+order.getString("bankName"));
						tv_bankNo.setText("银行账户："+order.getString("bankNo"));
						tv_invoiceContent1.setText("发票内容："+invoiceContent);
					}
					tv_productMoney.setText("￥"+FormatUtil.toString(order.getDouble("productMoney")));
					tv_feeMoney.setText("￥"+order.getString("feeMoney"));
					tv_machiningMoney.setText("￥"+order.getString("machiningMoney"));
					tv_dealsMoney.setText("￥"+order.getString("dealsMoney"));
					tv_money.setText("￥"+FormatUtil.toString( order.getDouble("money")));
					
					
					JSONArray orderDtls = order.getJSONArray("orderDtls");
					for(int j=0;j<orderDtls.length();j++){
						Map<String, Object> dateMap1 = new HashMap<String, Object>();
						dateMap1.put("id", orderDtls.getJSONObject(j).get("ID"));
						dateMap1.put("proName", orderDtls.getJSONObject(j).get("proName"));
						dateMap1.put("orderInfo", orderDtls.getJSONObject(j).toString());
						dateMaps.add(dateMap1);							
					}
					SimpleAdapter sapinfo = new InfoSimpleAdapter(Order_Xh_Info_Activity.this, dateMaps, 
							R.layout.listview_orderxhinfo, 
							new String[]{"proName"}, 
							new int[]{R.id.tv_proName});
					list_orderinfo.setAdapter(sapinfo);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
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
		public View getView(int position, View convertView, ViewGroup parent) {
			try{
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.listview_orderxhinfo, null); 
				} 
				ImageView img_proImgPath = (ImageView)convertView.findViewById(R.id.img_proImgPath);
				TextView tv_proName = (TextView)convertView.findViewById(R.id.tv_proName);
				TextView tv_info = (TextView)convertView.findViewById(R.id.tv_info);
				TextView tv_salePrice = (TextView)convertView.findViewById(R.id.tv_salePrice);
				TextView tv_quantity = (TextView)convertView.findViewById(R.id.tv_quantity);
				
				JSONObject temp  =FormatUtil.toJSONObject( mdata.get(position).get("orderInfo").toString());
				XUtilsHelper.getInstance().bindCommonImage(img_proImgPath, temp.getString("proImgPath"), true);
				
				int templateid = temp.getInt("templateid");
				String salePrice = temp.getString("salePrice");
				int payMethod = temp.getInt("payMethod");
				String info = "";
				if(templateid == 0){
					info = "规格："+temp.getString("proSpec")+"   "
							+"材质："+temp.getString("proQuality")+"   "
							+"编号："+temp.getString("proCode");
				}
				else if(templateid == 1){
					info = "规格："+temp.getString("proSpec")+"   "
							+"锌层："+temp.getString("znlayer")+"   "
							+"包装方式："+temp.getString("packType")+"   "
							+"捆包号："+temp.getString("proCode");
				}
				else if(templateid == 2){
					info = "规格："+temp.getString("proSpec")+"   "
							+"颜色："+temp.getString("color")+"   "
							+"漆膜厚度："+temp.getString("film")+"   "
							+"捆包号："+temp.getString("proCode");
				}
				else if(templateid == 3){
					info = "厚度："+temp.getString("thick")+"   "
							+"口径："+temp.getString("borer")+"   "
							+"捆包号："+temp.getString("proCode");
				}
				else if(templateid == 4 || templateid == 5){
					info = "规格："+temp.getString("proSpec")+"   "
							+"包装方式："+temp.getString("packType")+"   "
							+"捆包号："+temp.getString("proCode");
				}
				tv_info.setText(info);		
				if(salePrice.equals("0")){
					salePrice = "面议";
				}
				else{
					if(payMethod == 0){
						salePrice += "元/"+temp.getString("unit");
					}
					else{
						salePrice += "积分/"+temp.getString("unit");
					}
				}
				tv_salePrice.setText(salePrice);
				tv_quantity.setText(temp.getString("quantity")+temp.getString("unit"));
				
			}			
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}		
	}
	
	@Event(value={R.id.btn_topay,R.id.btn_tosh})
	private void btnClick(View v){
		if(v.getId() == R.id.btn_topay){
			Intent i = new Intent(getApplicationContext(),PreparePayActivity.class);
			i.putExtra("id", id);
			startActivity(i);
		}
		else if(v.getId() == R.id.btn_tosh){
			final MyConfirmDialog mcd = new MyConfirmDialog(Order_Xh_Info_Activity.this, "你确认已经收到货物?", "确定收货", "否");
			final String serverkey1 = super.serverKey;
			mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
				
				@Override
				public void doConfirm() {
					mcd.dismiss();
					progressDialog.show();
					Map<String, String> maps= new HashMap<String, String>();
					maps.put("serverKey", serverkey1);
					maps.put("id", id);
					XUtilsHelper.getInstance().post("app/inOrder.htm", maps,new XUtilsHelper.XCallBack(){

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
									CommonUtil.alter("收货成功！！！！");
									datainit();
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						
					});
				}				
				@Override
				public void doCancel() {  
					mcd.dismiss();
				}
			});
			mcd.show();
		}

	}

}
