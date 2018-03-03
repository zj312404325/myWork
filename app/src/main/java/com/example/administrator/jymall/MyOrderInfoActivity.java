package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.BigDecimalUtil;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_order_info)
public class MyOrderInfoActivity extends TopActivity {

    private String id; //订单ID
    @ViewInject(R.id.btn_pay)
    private Button btn_pay;
    @ViewInject(R.id.btn_payFirst)
    private Button btn_payFirst;
    @ViewInject(R.id.btn_payLast)
    private Button btn_payLast;
    @ViewInject(R.id.btn_confirmProduct)
    private Button btn_confirmProduct;

    private JSONObject order;
    private int orderStatus;
    private String orderType;

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

    SimpleAdapter sapinfo;

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
        hideAll();

        dateMaps.clear();
        sapinfo = new InfoSimpleAdapter(MyOrderInfoActivity.this, dateMaps,
                R.layout.listview_myorderinfo,
                new String[]{"proName"},
                new int[]{R.id.tv_proName});
        sapinfo.notifyDataSetChanged();

        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("id", id);
        XUtilsHelper.getInstance().post("app/mallOrderDetail.htm", maps,new XUtilsHelper.XCallBack(){

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
                    orderType = order.getString("orderType");
                    int refundstatus = order.getInt("refundstatus");
                    int refundtype = order.getInt("refundtype");

                    if(!orderType.equals("orderMatch")) {
                        if (orderStatus == 0) {
                            tv_orderStatus.setText("未支付");
                            btn_pay.setVisibility(View.VISIBLE);
                            btn_pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    Intent i = new Intent(getApplicationContext(),PreparePayActivity.class);
                                    i.putExtra("id", id);
                                    startActivity(i);
                                }
                            });
                        } else if (orderStatus == 1) {
                            tv_orderStatus.setText("等待收款");
                        } else if (orderStatus == 2 ) {
                            tv_orderStatus.setText("等待发货");
                        } else if (orderStatus == 3) {
                            tv_orderStatus.setText("等待收货");
                            btn_confirmProduct.setVisibility(View.VISIBLE);
                        } else if (orderStatus == 4) {
                            tv_orderStatus.setText("订单完成");
                        } else if (orderStatus == 5) {
                            tv_orderStatus.setText("订单取消");
                        } else if (orderStatus == 6) {
                            tv_orderStatus.setText("订单结束");
                        }
                    }
                    else{
                        if (orderStatus == 0) {
                            tv_orderStatus.setText("待设置定金");
                        } else if (orderStatus == 1) {
                            tv_orderStatus.setText("待支付定金");
                            btn_payFirst.setVisibility(View.VISIBLE);
                        } else if (orderStatus == 2 ) {
                            tv_orderStatus.setText("待确认定金");
                        } else if (orderStatus == 3) {
                            tv_orderStatus.setText("待生产完成");
                        } else if (orderStatus == 4) {
                            tv_orderStatus.setText("待付尾款");
                            btn_payLast.setVisibility(View.VISIBLE);
                        } else if (orderStatus == 5) {
                            tv_orderStatus.setText("待确认尾款");
                        } else if (orderStatus == 6) {
                            tv_orderStatus.setText("待发货");
                        } else if (orderStatus == 7) {
                            tv_orderStatus.setText("等待收货");
                            btn_confirmProduct.setVisibility(View.VISIBLE);
                        } else if (orderStatus == 8) {
                            tv_orderStatus.setText("订单完成");
                        } else if (orderStatus == 9) {
                            tv_orderStatus.setText("订单取消");
                        }
                    }

                    tv_orderNo.setText(order.getString("orderNo"));
                    tv_createDate.setText(order.getString("createDate"));
                    tv_contact.setText(order.getString("contact"));
                    tv_mobilePhone.setText(order.getString("mobilePhone"));
                    tv_buyAddr.setText(order.getString("buyAddr"));

                    String invoiceType = order.getString("invoiceType");
                    String invoiceContent = invoiceType.equals("VAT")?order.getString("invoiceContent"):"明细";
                    if(invoiceType.equals("COMMON")){
                        tv_invoiceType.setText("普通发票");
                        tv_title.setText(order.getString("title"));
                    }
                    else{
                        tv_invoiceType.setText("增值税发票");
                        tv_title.setText(order.getString("companyName"));
                    }
                    tv_productMoney.setText("￥"+FormatUtil.toString(BigDecimalUtil.sub(FormatUtil.toDouble(order.getString("money")),FormatUtil.toDouble(order.getString("feeMoney")),2)));
                    tv_feeMoney.setText("￥"+order.getString("feeMoney"));
                    tv_money.setText("￥"+FormatUtil.toString( order.getDouble("money")));


                    JSONArray orderDtls = order.getJSONArray("orderDtls");
                    for(int j=0;j<orderDtls.length();j++){
                        Map<String, Object> dateMap1 = new HashMap<String, Object>();
                        dateMap1.put("id", orderDtls.getJSONObject(j).get("ID"));
                        dateMap1.put("proName", orderDtls.getJSONObject(j).get("proName"));
                        dateMap1.put("orderInfo", orderDtls.getJSONObject(j).toString());
                        dateMaps.add(dateMap1);
                    }
                    sapinfo = new InfoSimpleAdapter(MyOrderInfoActivity.this, dateMaps,
                            R.layout.listview_myorderinfo,
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
                    convertView=mInflater.inflate(R.layout.listview_myorderinfo, null);
                }
                ImageView img_proImgPath = (ImageView)convertView.findViewById(R.id.img_proImgPath);
                TextView tv_proName = (TextView)convertView.findViewById(R.id.tv_proName);
                TextView tv_info = (TextView)convertView.findViewById(R.id.tv_info);
                TextView tv_salePrice = (TextView)convertView.findViewById(R.id.tv_salePrice);
                TextView tv_quantity = (TextView)convertView.findViewById(R.id.tv_quantity);
                Button btn_refundIndex = (Button)convertView.findViewById(R.id.btn_refundIndex);
                TextView tv_refundMoneySupply = (TextView)convertView.findViewById(R.id.tv_refundMoneySupply);
                TextView tv_refundMoneyOk = (TextView)convertView.findViewById(R.id.tv_refundMoneyOk);
                TextView tv_refundGoodsSupply = (TextView)convertView.findViewById(R.id.tv_refundGoodsSupply);
                TextView tv_refundGoodsBuyerSend = (TextView)convertView.findViewById(R.id.tv_refundGoodsBuyerSend);
                TextView tv_refundGoodsSellerSend = (TextView)convertView.findViewById(R.id.tv_refundGoodsSellerSend);
                TextView tv_refundGoodsOk = (TextView)convertView.findViewById(R.id.tv_refundGoodsOk);


                JSONObject temp  =FormatUtil.toJSONObject( mdata.get(position).get("orderInfo").toString());
                img_proImgPath.setBackgroundResource(0);
                XUtilsHelper.getInstance().bindCommonImage(img_proImgPath, temp.getString("proImgPath"), true);

                String salePrice = temp.getString("salePrice");
                final String orderdtlid = temp.getString("iD");

                String info = "";
                info = "品牌："+temp.getString("brand")+"\n"+"材质："+temp.getString("proQuality")+"\n" +"规格："+temp.getString("proSpec");
                tv_info.setText(info);
                if(salePrice.equals("0")){
                    salePrice = "面议";
                }
                else{
                    salePrice += "元/"+temp.getString("unit");
                }
                tv_salePrice.setText(salePrice);
                tv_quantity.setText("×"+temp.getString("quantity")+temp.getString("unit"));

                orderStatus=FormatUtil.toInteger(order.getString("orderStatus"));
                int refundStatus=FormatUtil.toInteger(temp.getString("refundStatus"));
                int refundType=FormatUtil.toInteger(temp.getString("refundType"));

                if(!orderType.equals("orderMatch")) {
                    if (orderStatus >= 1 && orderStatus <= 3 && refundStatus == 0) {
                        tv_refundMoneySupply.setVisibility(View.GONE);
                        tv_refundMoneyOk.setVisibility(View.GONE);
                        tv_refundGoodsSupply.setVisibility(View.GONE);
                        tv_refundGoodsBuyerSend.setVisibility(View.GONE);
                        tv_refundGoodsSellerSend.setVisibility(View.GONE);
                        tv_refundGoodsOk.setVisibility(View.GONE);
                        btn_refundIndex.setVisibility(View.VISIBLE);
                        btn_refundIndex.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                Intent i = new Intent(getApplicationContext(), RefundIndexActivity.class);
                                i.putExtra("orderId", id);
                                i.putExtra("orderDtlId", orderdtlid);
                                startActivity(i);
                            }
                        });
                    }
                    if (orderStatus >= 1 && orderStatus <= 3 && (refundStatus == 1 || refundStatus == -1) && refundType == 1) {

                        tv_refundMoneyOk.setVisibility(View.GONE);
                        tv_refundGoodsSupply.setVisibility(View.GONE);
                        tv_refundGoodsBuyerSend.setVisibility(View.GONE);
                        tv_refundGoodsSellerSend.setVisibility(View.GONE);
                        tv_refundGoodsOk.setVisibility(View.GONE);
                        btn_refundIndex.setVisibility(View.GONE);
                        tv_refundMoneySupply.setVisibility(View.VISIBLE);
                        tv_refundMoneySupply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                Intent i = new Intent(getApplicationContext(), RefundMoneyTwoActivity.class);
                                i.putExtra("orderId", id);
                                i.putExtra("orderDtlId", orderdtlid);
                                startActivity(i);
                            }
                        });
                    }
                    if (orderStatus >= 1 && orderStatus <= 3 && (refundStatus == 1 || refundStatus == -1) && refundType == 0) {
                        tv_refundMoneyOk.setVisibility(View.GONE);
                        tv_refundMoneySupply.setVisibility(View.GONE);
                        tv_refundGoodsBuyerSend.setVisibility(View.GONE);
                        tv_refundGoodsSellerSend.setVisibility(View.GONE);
                        tv_refundGoodsOk.setVisibility(View.GONE);
                        btn_refundIndex.setVisibility(View.GONE);
                        tv_refundGoodsSupply.setVisibility(View.VISIBLE);
                        tv_refundGoodsSupply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                Intent i = new Intent(getApplicationContext(), RefundGoodsTwoActivity.class);
                                i.putExtra("orderId", id);
                                i.putExtra("orderDtlId", orderdtlid);
                                startActivity(i);
                            }
                        });
                    }
                    if (orderStatus >= 1 && (orderStatus <= 4 || orderStatus == 6) && refundStatus == 2 && refundType == 1) {
                        tv_refundMoneySupply.setVisibility(View.GONE);
                        tv_refundGoodsSupply.setVisibility(View.GONE);
                        tv_refundGoodsBuyerSend.setVisibility(View.GONE);
                        tv_refundGoodsSellerSend.setVisibility(View.GONE);
                        tv_refundGoodsOk.setVisibility(View.GONE);
                        btn_refundIndex.setVisibility(View.GONE);
                        tv_refundMoneyOk.setVisibility(View.VISIBLE);
                        tv_refundMoneyOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                Intent i = new Intent(getApplicationContext(), RefundMoneyTwoActivity.class);
                                i.putExtra("orderId", id);
                                i.putExtra("orderDtlId", orderdtlid);
                                startActivity(i);
                            }
                        });
                    }
                    if (orderStatus >= 1 && orderStatus <= 3 && refundStatus == 2 && refundType == 0) {
                        tv_refundMoneyOk.setVisibility(View.GONE);
                        tv_refundMoneySupply.setVisibility(View.GONE);
                        tv_refundGoodsSupply.setVisibility(View.GONE);
                        tv_refundGoodsSellerSend.setVisibility(View.GONE);
                        tv_refundGoodsOk.setVisibility(View.GONE);
                        btn_refundIndex.setVisibility(View.GONE);
                        tv_refundGoodsBuyerSend.setVisibility(View.VISIBLE);
                        tv_refundGoodsBuyerSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                Intent i = new Intent(getApplicationContext(), RefundGoodsTwoActivity.class);
                                i.putExtra("orderId", id);
                                i.putExtra("orderDtlId", orderdtlid);
                                startActivity(i);
                            }
                        });
                    }
                    if (orderStatus >= 1 && orderStatus <= 3 && refundStatus == 3 && refundType == 0) {
                        tv_refundMoneyOk.setVisibility(View.GONE);
                        tv_refundMoneySupply.setVisibility(View.GONE);
                        tv_refundGoodsSupply.setVisibility(View.GONE);
                        tv_refundGoodsBuyerSend.setVisibility(View.GONE);
                        tv_refundGoodsOk.setVisibility(View.GONE);
                        btn_refundIndex.setVisibility(View.GONE);
                        tv_refundGoodsSellerSend.setVisibility(View.VISIBLE);
                        tv_refundGoodsSellerSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                Intent i = new Intent(getApplicationContext(), RefundGoodsTwoActivity.class);
                                i.putExtra("orderId", id);
                                i.putExtra("orderDtlId", orderdtlid);
                                startActivity(i);
                            }
                        });
                    }
                    if (orderStatus >= 1 && (orderStatus <= 4 || orderStatus == 6) && refundStatus == 4 && refundType == 0) {
                        tv_refundMoneyOk.setVisibility(View.GONE);
                        tv_refundMoneySupply.setVisibility(View.GONE);
                        tv_refundGoodsSupply.setVisibility(View.GONE);
                        tv_refundGoodsBuyerSend.setVisibility(View.GONE);
                        tv_refundGoodsSellerSend.setVisibility(View.GONE);
                        btn_refundIndex.setVisibility(View.GONE);
                        tv_refundGoodsOk.setVisibility(View.VISIBLE);
                        tv_refundGoodsOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                Intent i = new Intent(getApplicationContext(), RefundGoodsTwoActivity.class);
                                i.putExtra("orderId", id);
                                i.putExtra("orderDtlId", orderdtlid);
                                startActivity(i);
                            }
                        });
                    }
                }
                else{
                    tv_refundMoneyOk.setVisibility(View.GONE);
                    tv_refundMoneySupply.setVisibility(View.GONE);
                    tv_refundGoodsSupply.setVisibility(View.GONE);
                    tv_refundGoodsBuyerSend.setVisibility(View.GONE);
                    tv_refundGoodsSellerSend.setVisibility(View.GONE);
                    btn_refundIndex.setVisibility(View.GONE);
                    tv_refundGoodsOk.setVisibility(View.GONE);
                }
            }
            catch(Exception e){
                Log.v("PRO", e.getMessage());
            }
            return super.getView(position, convertView, parent);
        }
    }

    @Event(value={R.id.btn_pay,R.id.btn_payFirst,R.id.btn_payLast,R.id.btn_confirmProduct})
    private void btnClick(View v){
        if(v.getId() == R.id.btn_pay || v.getId() == R.id.btn_payFirst  || v.getId() == R.id.btn_payLast ){
            Intent i = new Intent(getApplicationContext(),PreparePayActivity.class);
            i.putExtra("id", id);
            startActivity(i);
        }
        else if(v.getId() == R.id.btn_confirmProduct){
            final MyConfirmDialog mcd = new MyConfirmDialog(MyOrderInfoActivity.this, "您确认已经收到货物?", "确认收货", "取消");
            final String serverkey1 = super.serverKey;
            mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {

                @Override
                public void doConfirm() {
                    mcd.dismiss();
                    progressDialog.show();
                    Map<String, String> maps= new HashMap<String, String>();
                    maps.put("serverKey", serverkey1);
                    maps.put("id", id);
                    XUtilsHelper.getInstance().post("app/inMallOrder.htm", maps,new XUtilsHelper.XCallBack(){

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
                                    CommonUtil.alter("收货成功!");
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

    @Event(value=R.id.btn_copy)
    private void copyClick(View v){
        ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        //创建ClipData对象
        ClipData clipData = ClipData.newPlainText("orderNo", tv_orderNo.getText().toString());
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData);
        CommonUtil.alter("成功复制到剪贴板!");
    }

    private void hideAll(){
        btn_pay.setVisibility(View.GONE);
        btn_payFirst.setVisibility(View.GONE);
        btn_payLast.setVisibility(View.GONE);
        btn_confirmProduct.setVisibility(View.GONE);
    }

}
