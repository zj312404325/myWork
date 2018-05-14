package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.MyApplication;
import com.example.administrator.jymall.common.TopSearchOrderActivity;
import com.example.administrator.jymall.util.BaseConst;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.DensityUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyConfirmDialog;
import com.example.administrator.jymall.view.MyListView;
import com.example.administrator.jymall.view.XListView;
import com.example.administrator.jymall.view.XListView.IXListViewListener;

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

/**
 * Created by Administrator on 2018-01-09.
 */

@ContentView(R.layout.activity_order_search)
public class MyOrderSearchActivity  extends TopSearchOrderActivity implements IXListViewListener{

    @ViewInject(R.id.tab)
    private LinearLayout ll_tab;
    @ViewInject(R.id.tab_two)
    private LinearLayout ll_tab_two;
    @ViewInject(R.id.tab1)
    private RelativeLayout tab1;
    @ViewInject(R.id.tab2)
    private RelativeLayout tab2;
    @ViewInject(R.id.tab3)
    private RelativeLayout tab3;
    @ViewInject(R.id.tab4)
    private RelativeLayout tab4;
    @ViewInject(R.id.tab5)
    private RelativeLayout tab5;
    @ViewInject(R.id.tab_txt1)
    private TextView tab_txt1;
    @ViewInject(R.id.tab_txt2)
    private TextView tab_txt2;
    @ViewInject(R.id.tab_txt3)
    private TextView tab_txt3;
    @ViewInject(R.id.tab_txt4)
    private TextView tab_txt4;
    @ViewInject(R.id.tab_txt5)
    private TextView tab_txt5;
    @ViewInject(R.id.tab_line1)
    private LinearLayout tab_line1;
    @ViewInject(R.id.tab_line2)
    private LinearLayout tab_line2;
    @ViewInject(R.id.tab_line3)
    private LinearLayout tab_line3;
    @ViewInject(R.id.tab_line4)
    private LinearLayout tab_line4;
    @ViewInject(R.id.tab_line5)
    private LinearLayout tab_line5;

    @ViewInject(R.id.tab_two_txt1)
    private TextView tab_two_txt1;
    @ViewInject(R.id.tab_two_txt2)
    private TextView tab_two_txt2;
    @ViewInject(R.id.tab_two_txt3)
    private TextView tab_two_txt3;
    @ViewInject(R.id.tab_two_txt4)
    private TextView tab_two_txt4;
    @ViewInject(R.id.tab_two_txt5)
    private TextView tab_two_txt5;
    @ViewInject(R.id.tab_two_txt6)
    private TextView tab_two_txt6;
    @ViewInject(R.id.tab_two_line1)
    private LinearLayout tab_two_line1;
    @ViewInject(R.id.tab_two_line2)
    private LinearLayout tab_two_line2;
    @ViewInject(R.id.tab_two_line3)
    private LinearLayout tab_two_line3;
    @ViewInject(R.id.tab_two_line4)
    private LinearLayout tab_two_line4;
    @ViewInject(R.id.tab_two_line5)
    private LinearLayout tab_two_line5;
    @ViewInject(R.id.tab_two_line6)
    private LinearLayout tab_two_line6;

    @ViewInject(R.id.rl_showData)
    private RelativeLayout rl_showData;

    @ViewInject(R.id.ll_allOrder)
    private LinearLayout ll_allOrder;
    @ViewInject(R.id.ll_commonOrder)
    private LinearLayout ll_commonOrder;
    @ViewInject(R.id.ll_fastMatch)
    private LinearLayout ll_fastMatch;
    @ViewInject(R.id.ll_orderMatch)
    private LinearLayout ll_orderMatch;

    @ViewInject(R.id.iv_allOrder)
    private ImageView iv_allOrder;
    @ViewInject(R.id.iv_commonOrder)
    private ImageView iv_commonOrder;
    @ViewInject(R.id.iv_fastMatch)
    private ImageView iv_fastMatch;
    @ViewInject(R.id.iv_orderMatch)
    private ImageView iv_orderMatch;

    @ViewInject(R.id.tv_allOrder)
    private TextView tv_allOrder;
    @ViewInject(R.id.tv_commonOrder)
    private TextView tv_commonOrder;
    @ViewInject(R.id.tv_fastMatch)
    private TextView tv_fastMatch;
    @ViewInject(R.id.tv_orderMatch)
    private TextView tv_orderMatch;

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;

    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
    private SimpleAdapter sap;
    private Handler mHandler;
    private int start = 1;

    @ViewInject(R.id.listtv)
    private TextView listtv;
    @ViewInject(R.id.et_searchvar)
    private EditText et_searchvar;

    String orderStatus="";
    String orderType="";
    String orderNo="";
    @ViewInject(R.id.btn_search)
    private Button btn_search;

    private String skey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_search);
        x.view().inject(this);
        progressDialog.hide();
        //super.title.setText("订单搜索");
        skey = super.serverKey;

        Intent i = this.getIntent();
        orderNo = i.getStringExtra("keyword");
        if(FormatUtil.isNoEmpty(orderNo)){
            top_searchbar_input_txt.setText(orderNo);
        }

        sap = new ProSimpleAdapter(MyOrderSearchActivity.this, dateMaps,
                R.layout.listview_mallorder,
                new String[]{"id"},
                new int[]{R.id.tv_id});
        listViewAll.setAdapter(sap);
        listViewAll.setPullLoadEnable(true);
        listViewAll.setXListViewListener(this);

        getDate(true,true);
        mHandler = new Handler();

    }

    private void getDate(final boolean isShow,final boolean flag){
        if(isShow){
            progressDialog.show();
        }
        if(flag){
            start=1;
        }
        listtv.setVisibility(View.GONE);
        listViewAll.setPullLoadEnable(false);
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", skey);
        maps.put("currentPage", ""+start);
        //maps.put("searchvar", et_searchvar.getText().toString());
        maps.put("orderNo", orderNo);
        maps.put("orderStatus", orderStatus);
        maps.put("orderType", orderType);
        XUtilsHelper.getInstance().post("app/mallOrderList.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                listtv.setVisibility(View.GONE);
                if(flag){
                    dateMaps.clear();
                }
                if(isShow){
                    progressDialog.hide();
                }
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    skey = res.get("serverKey").toString();
                    if(res.get("d").equals("n")){
                        CommonUtil.alter(res.get("msg").toString());
                        MyApplication.getInstance().finishActivity();
                    }
                    else{
                        JSONArray resjarr = (JSONArray)res.get("orderList");
                        if(resjarr.length()==0 && start == 1) {
                            listtv.setVisibility(View.VISIBLE);
                        }
                        else if(resjarr.length() ==  15 ) {
                            listViewAll.setPullLoadEnable(true);
                        }
                        for(int i=0;i<resjarr.length();i++){
                            Map<String, Object> dateMap = new HashMap<String, Object>();
                            dateMap.put("id", resjarr.getJSONObject(i).get("iD"));
                            dateMap.put("orderNo", resjarr.getJSONObject(i).get("orderNo"));
                            dateMap.put("orderStatus", resjarr.getJSONObject(i).get("orderStatus"));
                            dateMap.put("order", resjarr.getJSONObject(i));

                            dateMap.put("feeMoney", resjarr.getJSONObject(i).get("feeMoney"));
                            dateMap.put("firstMoney", resjarr.getJSONObject(i).get("firstMoney"));
                            dateMap.put("initMoney", resjarr.getJSONObject(i).get("initMoney"));
                            dateMap.put("money", resjarr.getJSONObject(i).get("money"));
                            dateMap.put("isAppraised", resjarr.getJSONObject(i).get("isAppraised"));

                            List<Map<String, Object>> dateMapinfo= new ArrayList<Map<String, Object>>();
                            double count = 0;

                            JSONArray orderDtls = resjarr.getJSONObject(i).getJSONArray("orderDtls");
                            for(int j=0;j<orderDtls.length();j++){
                                Map<String, Object> dateMap1 = new HashMap<String, Object>();
                                dateMap1.put("id", orderDtls.getJSONObject(j).get("iD"));
                                dateMap1.put("orderType", resjarr.getJSONObject(i).get("orderType"));
                                dateMap1.put("orderDtl", orderDtls.getJSONObject(j).toString());
                                dateMap1.put("proName", orderDtls.getJSONObject(j).get("proName"));
                                dateMap1.put("salePrice", orderDtls.getJSONObject(j).get("salePrice"));
                                count += FormatUtil.toDouble(orderDtls.getJSONObject(j).get("quantity"));
                                dateMap1.put("brand", orderDtls.getJSONObject(j).get("brand"));
                                dateMap1.put("proQuality", orderDtls.getJSONObject(j).get("proQuality"));
                                dateMap1.put("proSpec", orderDtls.getJSONObject(j).get("proSpec"));
                                dateMap1.put("refundStatus", orderDtls.getJSONObject(j).get("refundStatus"));
                                dateMap1.put("refundType", orderDtls.getJSONObject(j).get("refundType"));
                                dateMap1.put("proImgPath", orderDtls.getJSONObject(j).get("proImgPath").toString());
                                dateMapinfo.add(dateMap1);
                            }
                            dateMap.put("orderDtls", dateMapinfo);
                            dateMap.put("count",FormatUtil.toString(count));
                            dateMaps.add(dateMap);
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

    /**
     * 产品list
     * @author Administrator
     *
     */

    private void onLoad() {
        listViewAll.stopRefresh();
        listViewAll.stopLoadMore();
        listViewAll.setRefreshTime(DateUtil.DateToString(new Date(), DateStyle.HH_MM_SS));
    }


    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start = 1;
                getDate(true,true);
                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start++;
                getDate(true,false);
                onLoad();
            }
        }, 2000);
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
                    convertView=mInflater.inflate(R.layout.listview_mallorder, null);
                }

                JSONObject order =FormatUtil.toJSONObject(dateMaps.get(position).get("order").toString());
                final String id = dateMaps.get(position).get("id").toString();
                final String count = dateMaps.get(position).get("count").toString();
                final String money = dateMaps.get(position).get("money").toString();
                final String initMoney = dateMaps.get(position).get("initMoney").toString();
                final String feeMoney = dateMaps.get(position).get("feeMoney").toString();
                final String firstMoney = dateMaps.get(position).get("firstMoney").toString();

                TextView tv_orderNo = (TextView)convertView.findViewById(R.id.tv_orderNo);
                TextView tv_orderStatus = (TextView)convertView.findViewById(R.id.tv_orderStatus);
                TextView tv_count = (TextView)convertView.findViewById(R.id.tv_count);
                TextView tv_money = (TextView)convertView.findViewById(R.id.tv_money);
                TextView tv_transFee = (TextView)convertView.findViewById(R.id.tv_transFee);
                TextView tv_firstMoney = (TextView)convertView.findViewById(R.id.tv_firstMoney);

                LinearLayout orderinfo = (LinearLayout)convertView.findViewById(R.id.orderinfo);
                LinearLayout ll_firstMoney = (LinearLayout)convertView.findViewById(R.id.ll_firstMoney);

                orderinfo.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View arg0, MotionEvent arg1) {
                        if (arg1.getAction() == arg1.ACTION_UP) {
                            Intent intent = new Intent(getApplicationContext(),MyOrderInfoActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                            return false;
                        }
                        return true;
                    }
                });

                MyListView list_orderinfo = (MyListView)convertView.findViewById(R.id.list_orderinfo);

                Button btn_delete = (Button)convertView.findViewById(R.id.btn_delete);
                Button btn_cancel = (Button)convertView.findViewById(R.id.btn_cancel);
                Button btn_pay = (Button)convertView.findViewById(R.id.btn_pay);
                Button btn_payFirst = (Button)convertView.findViewById(R.id.btn_payFirst);
                Button btn_payLast = (Button)convertView.findViewById(R.id.btn_payLast);
                Button btn_confirmProduct = (Button)convertView.findViewById(R.id.btn_confirmProduct);
                Button btn_appraise = (Button)convertView.findViewById(R.id.btn_appraise);
                TextView tv_isAppraised = (TextView)convertView.findViewById(R.id.tv_isAppraised);
                ImageView iv_orderTypeIcon = (ImageView)convertView.findViewById(R.id.iv_orderTypeIcon);

                final int orderStatus = order.getInt("orderStatus");
                final String orderType = order.getString("orderType");
                final int isAppraised = order.getInt("isAppraised");

                final String ownerID = order.getString("ownerID");
                final int isOnline = order.getInt("isOnline");
                final int refundstatus = order.getInt("refundstatus");
                final int refundtype = order.getInt("refundtype");

                tv_orderNo.setText(order.getString("orderNo"));
                tv_count.setText("共"+count+"件商品");
                tv_money.setText("合计：¥"+money+"元");
                tv_transFee.setText("（含运费：¥"+feeMoney+"元）");

                if(orderType.equals("fastMatch")){
                    iv_orderTypeIcon.setBackgroundResource(R.drawable.ordertype_fast_match);
                }
                else if(orderType.equals("fabFastMatch")){
                    iv_orderTypeIcon.setBackgroundResource(R.drawable.ordertype_fab_fast_match);
                }
                else if(orderType.equals("orderMatch")){
                    iv_orderTypeIcon.setBackgroundResource(R.drawable.ordertype_order_match);
                    if(orderStatus>0){
                        tv_firstMoney.setText("¥"+firstMoney+"元");
                    }
                }
                else if(orderType.equals("fabOrderMatch")){
                    iv_orderTypeIcon.setBackgroundResource(R.drawable.ordertype_fab_order_match);
                    if(orderStatus>0){
                        tv_firstMoney.setText("¥"+firstMoney+"元");
                    }
                }
                if(orderType.equals("product")){
                    iv_orderTypeIcon.setBackgroundResource(R.drawable.logo_jinjin);
                }

                btn_delete.setVisibility(View.GONE);

                if(!orderType.equals("orderMatch") && !orderType.equals("fabOrderMatch")) {
                    ll_firstMoney.setVisibility(View.GONE);
                    btn_payFirst.setVisibility(View.GONE);
                    btn_payLast.setVisibility(View.GONE);
                    if (orderStatus == 0) {
                        tv_orderStatus.setText("未支付");
                        btn_cancel.setVisibility(View.GONE);
                        btn_confirmProduct.setVisibility(View.GONE);
                        btn_pay.setVisibility(View.VISIBLE);
                        btn_pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                Intent i = new Intent(getApplicationContext(),PreparePayActivity.class);
                                i.putExtra("id", id);
                                startActivity(i);
                            }
                        });
                        btn_cancel.setVisibility(View.VISIBLE);
                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                // 交易取消开始
                                final MyConfirmDialog mcd = new MyConfirmDialog(MyOrderSearchActivity.this, "您确认取消此订单吗?", "确定取消", "否");
                                mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {
                                        mcd.dismiss();
                                        progressDialog.show();
                                        Map<String, String> maps= new HashMap<String, String>();
                                        maps.put("serverKey", skey);
                                        maps.put("id", id);
                                        XUtilsHelper.getInstance().post("app/cancelMallOrder.htm", maps,new XUtilsHelper.XCallBack(){

                                            @SuppressLint("NewApi")
                                            @Override
                                            public void onResponse(String result)  {
                                                progressDialog.hide();
                                                JSONObject res;
                                                try {
                                                    res = new JSONObject(result);
                                                    setServerKey(res.get("serverKey").toString());
                                                    skey = res.get("serverKey").toString();
                                                    if(res.get("d").equals("n")){
                                                        CommonUtil.alter(res.get("msg").toString());
                                                    }
                                                    else{
                                                        CommonUtil.alter("取消成功！");
                                                        getDate(true,true);
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
                        });
                    } else if (orderStatus == 1) {
                        tv_orderStatus.setText("等待收款");
                        btn_pay.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                        btn_confirmProduct.setVisibility(View.GONE);
                    } else if (orderStatus == 2 ) {
                        tv_orderStatus.setText("等待发货");
                        btn_pay.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                        btn_confirmProduct.setVisibility(View.GONE);
                    } else if (orderStatus == 3) {
                        tv_orderStatus.setText("等待收货");
                        btn_pay.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                        btn_confirmProduct.setVisibility(View.VISIBLE);
                        btn_confirmProduct.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                // 确认收货开始
                                final MyConfirmDialog mcd = new MyConfirmDialog(MyOrderSearchActivity.this, "您确认已经收到货物?", "确定收货", "否");
                                mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {
                                        mcd.dismiss();
                                        progressDialog.show();
                                        Map<String, String> maps= new HashMap<String, String>();
                                        maps.put("serverKey", skey);
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
                                                    skey = res.get("serverKey").toString();
                                                    if(res.get("d").equals("n")){
                                                        CommonUtil.alter(res.get("msg").toString());
                                                    }
                                                    else{
                                                        CommonUtil.alter("收货成功！");
                                                        getDate(true,true);
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
                        });
                    } else if (orderStatus == 4) {
                        tv_orderStatus.setText("订单完成");
                        btn_pay.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                        btn_confirmProduct.setVisibility(View.GONE);
                    } else if (orderStatus == 5) {
                        tv_orderStatus.setText("订单取消");
                        btn_pay.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                        btn_confirmProduct.setVisibility(View.GONE);
                        btn_delete.setVisibility(View.VISIBLE);
                        btn_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                // 删除订单开始
                                final MyConfirmDialog mcd = new MyConfirmDialog(MyOrderSearchActivity.this, "您确认要删除订单？", "确定删除", "否");
                                mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {
                                        mcd.dismiss();
                                        progressDialog.show();
                                        Map<String, String> maps= new HashMap<String, String>();
                                        maps.put("serverKey", skey);
                                        maps.put("id", id);
                                        XUtilsHelper.getInstance().post("app/delMallOrder.htm", maps,new XUtilsHelper.XCallBack(){

                                            @SuppressLint("NewApi")
                                            @Override
                                            public void onResponse(String result)  {
                                                progressDialog.hide();
                                                JSONObject res;
                                                try {
                                                    res = new JSONObject(result);
                                                    setServerKey(res.get("serverKey").toString());
                                                    skey = res.get("serverKey").toString();
                                                    if(res.get("d").equals("n")){
                                                        CommonUtil.alter(res.get("msg").toString());
                                                    }
                                                    else{
                                                        CommonUtil.alter("删除成功！");
                                                        getDate(true,true);
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
                        });
                    } else if (orderStatus == 6) {
                        tv_orderStatus.setText("订单结束");
                        btn_pay.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                        btn_confirmProduct.setVisibility(View.GONE);
                    }
                }
                else{
                    btn_pay.setVisibility(View.GONE);
                    if (orderStatus == 0) {
                        ll_firstMoney.setVisibility(View.GONE);
                        tv_orderStatus.setText("待设置定金");
                        btn_cancel.setVisibility(View.VISIBLE);
                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                // 交易取消开始
                                final MyConfirmDialog mcd = new MyConfirmDialog(MyOrderSearchActivity.this, "您确认取消此订单吗?", "确定取消", "否");
                                mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {
                                        mcd.dismiss();
                                        progressDialog.show();
                                        Map<String, String> maps= new HashMap<String, String>();
                                        maps.put("serverKey", skey);
                                        maps.put("id", id);
                                        XUtilsHelper.getInstance().post("app/cancelMallOrder.htm", maps,new XUtilsHelper.XCallBack(){

                                            @SuppressLint("NewApi")
                                            @Override
                                            public void onResponse(String result)  {
                                                progressDialog.hide();
                                                JSONObject res;
                                                try {
                                                    res = new JSONObject(result);
                                                    setServerKey(res.get("serverKey").toString());
                                                    skey = res.get("serverKey").toString();
                                                    if(res.get("d").equals("n")){
                                                        CommonUtil.alter(res.get("msg").toString());
                                                    }
                                                    else{
                                                        CommonUtil.alter("取消成功！");
                                                        getDate(true,true);
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
                        });
                    } else if (orderStatus == 1) {
                        ll_firstMoney.setVisibility(View.VISIBLE);
                        tv_orderStatus.setText("待支付定金");
                        btn_cancel.setVisibility(View.VISIBLE);
                        btn_payFirst.setVisibility(View.VISIBLE);
                        btn_payFirst.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                Intent i = new Intent(getApplicationContext(),PreparePayActivity.class);
                                i.putExtra("id", id);
                                startActivity(i);
                            }
                        });
                        btn_cancel.setVisibility(View.VISIBLE);
                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                // 交易取消开始
                                final MyConfirmDialog mcd = new MyConfirmDialog(MyOrderSearchActivity.this, "您确认取消此订单吗?", "确定取消", "否");
                                mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {
                                        mcd.dismiss();
                                        progressDialog.show();
                                        Map<String, String> maps= new HashMap<String, String>();
                                        maps.put("serverKey", skey);
                                        maps.put("id", id);
                                        XUtilsHelper.getInstance().post("app/cancelMallOrder.htm", maps,new XUtilsHelper.XCallBack(){

                                            @SuppressLint("NewApi")
                                            @Override
                                            public void onResponse(String result)  {
                                                progressDialog.hide();
                                                JSONObject res;
                                                try {
                                                    res = new JSONObject(result);
                                                    setServerKey(res.get("serverKey").toString());
                                                    skey = res.get("serverKey").toString();
                                                    if(res.get("d").equals("n")){
                                                        CommonUtil.alter(res.get("msg").toString());
                                                    }
                                                    else{
                                                        CommonUtil.alter("取消成功！");
                                                        getDate(true,true);
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
                        });
                    } else if (orderStatus == 2 ) {
                        ll_firstMoney.setVisibility(View.VISIBLE);
                        tv_orderStatus.setText("待确认定金");
                        btn_payFirst.setVisibility(View.GONE);
                        btn_payLast.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                    } else if (orderStatus == 3) {
                        ll_firstMoney.setVisibility(View.VISIBLE);
                        tv_orderStatus.setText("待生产完成");
                        btn_payFirst.setVisibility(View.GONE);
                        btn_payLast.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                    } else if (orderStatus == 4) {
                        ll_firstMoney.setVisibility(View.VISIBLE);
                        tv_orderStatus.setText("待付尾款");
                        btn_payFirst.setVisibility(View.GONE);
                        btn_payLast.setVisibility(View.VISIBLE);
                        btn_cancel.setVisibility(View.GONE);
                        btn_payLast.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                Intent i = new Intent(getApplicationContext(),PreparePayActivity.class);
                                i.putExtra("id", id);
                                startActivity(i);
                            }
                        });
                    } else if (orderStatus == 5) {
                        ll_firstMoney.setVisibility(View.VISIBLE);
                        tv_orderStatus.setText("待确认尾款");
                        btn_payFirst.setVisibility(View.GONE);
                        btn_payLast.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                    } else if (orderStatus == 6) {
                        ll_firstMoney.setVisibility(View.VISIBLE);
                        tv_orderStatus.setText("待发货");
                        btn_payFirst.setVisibility(View.GONE);
                        btn_payLast.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                    } else if (orderStatus == 7) {
                        ll_firstMoney.setVisibility(View.VISIBLE);
                        tv_orderStatus.setText("等待收货");
                        btn_payFirst.setVisibility(View.GONE);
                        btn_payLast.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                        btn_confirmProduct.setVisibility(View.VISIBLE);
                        btn_confirmProduct.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                // 确认收货开始
                                final MyConfirmDialog mcd = new MyConfirmDialog(MyOrderSearchActivity.this, "您确认已经收到货物?", "确定收货", "否");
                                mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {
                                        mcd.dismiss();
                                        progressDialog.show();
                                        Map<String, String> maps= new HashMap<String, String>();
                                        maps.put("serverKey", skey);
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
                                                    skey = res.get("serverKey").toString();
                                                    if(res.get("d").equals("n")){
                                                        CommonUtil.alter(res.get("msg").toString());
                                                    }
                                                    else{
                                                        CommonUtil.alter("收货成功！");
                                                        getDate(true,true);
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
                        });
                    } else if (orderStatus == 8) {
                        ll_firstMoney.setVisibility(View.VISIBLE);
                        btn_payFirst.setVisibility(View.GONE);
                        btn_payLast.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                        tv_orderStatus.setText("订单完成");
                    } else if (orderStatus == 9) {
                        ll_firstMoney.setVisibility(View.GONE);
                        btn_payFirst.setVisibility(View.GONE);
                        btn_payLast.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);
                        tv_orderStatus.setText("订单取消");
                        btn_delete.setVisibility(View.VISIBLE);
                        btn_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                // 删除订单开始
                                final MyConfirmDialog mcd = new MyConfirmDialog(MyOrderSearchActivity.this, "您确认要删除订单？", "确定删除", "否");
                                mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {
                                        mcd.dismiss();
                                        progressDialog.show();
                                        Map<String, String> maps= new HashMap<String, String>();
                                        maps.put("serverKey", skey);
                                        maps.put("id", id);
                                        XUtilsHelper.getInstance().post("app/delMallOrder.htm", maps,new XUtilsHelper.XCallBack(){

                                            @SuppressLint("NewApi")
                                            @Override
                                            public void onResponse(String result)  {
                                                progressDialog.hide();
                                                JSONObject res;
                                                try {
                                                    res = new JSONObject(result);
                                                    setServerKey(res.get("serverKey").toString());
                                                    skey = res.get("serverKey").toString();
                                                    if(res.get("d").equals("n")){
                                                        CommonUtil.alter(res.get("msg").toString());
                                                    }
                                                    else{
                                                        CommonUtil.alter("删除成功！");
                                                        getDate(true,true);
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
                        });
                    }
                }

                //评价状态
                if(orderType.equals("product")) {
                    if (isAppraised != 1) {
                        if (orderStatus == 4 ) {
                            tv_isAppraised.setVisibility(View.GONE);
                            btn_appraise.setVisibility(View.VISIBLE);
                            btn_appraise.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                // 评价开始
                                Intent intent = new Intent(getApplicationContext(), OrderAppraiseActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                                }
                            });
                        }
                        else{
                            tv_isAppraised.setVisibility(View.GONE);
                            btn_appraise.setVisibility(View.GONE);
                        }
                    } else if(isAppraised ==1){
                        btn_appraise.setVisibility(View.GONE);
                        tv_isAppraised.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    tv_isAppraised.setVisibility(View.GONE);
                    btn_appraise.setVisibility(View.GONE);
                }

                final List<Map<String, Object>> dateMapinfo= (List<Map<String, Object>>) dateMaps.get(position).get("orderDtls");
                final SimpleAdapter sapinfo = new InfoSimpleAdapter(MyOrderSearchActivity.this, dateMapinfo,
                        R.layout.listview_myorderinfo,
                        new String[]{"proName"},
                        new int[]{R.id.tv_proName});
                list_orderinfo.setAdapter(sapinfo);
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
                ImageView img_tui = (ImageView)convertView.findViewById(R.id.img_tui);

                JSONObject orderdtl  =FormatUtil.toJSONObject( mdata.get(position).get("orderDtl").toString());
                String orderType=mdata.get(position).get("orderType").toString();
                if(orderType.equals("product")) {
                    XUtilsHelper.getInstance().bindCommonImage(img_proImgPath, orderdtl.getString("proImgPath"), true);
                }
                else if(orderType.equals("fastMatch")){
                    img_proImgPath.setBackgroundResource(R.drawable.pro_fast_match);
                }
                else if(orderType.equals("orderMatch")){
                    img_proImgPath.setBackgroundResource(R.drawable.pro_order_match);
                }
                else if(orderType.equals("fabFastMatch")){
                    img_proImgPath.setBackgroundResource(R.drawable.pro_fab_fast_match);
                }
                else if(orderType.equals("fabOrderMatch")){
                    img_proImgPath.setBackgroundResource(R.drawable.pro_fab_order_match);
                }
                else{
                    XUtilsHelper.getInstance().bindCommonImage(img_proImgPath, orderdtl.getString("proImgPath"), true);
                }
                String salePrice = orderdtl.getString("salePrice");
                String proName = orderdtl.getString("proName");

                String info ;
                info = "品牌："+orderdtl.getString("brand")+"\n"+"材质："+orderdtl.getString("proQuality")+"\n" +"规格："+orderdtl.getString("proSpec");

                tv_info.setText(info);
                tv_proName.setText(proName);

                if(salePrice.equals("0")){
                    salePrice = "面议";
                }
                else{
                    salePrice ="¥"+salePrice+"元/"+orderdtl.getString("unit");
                }
                tv_salePrice.setText(salePrice);
                tv_quantity.setText("×"+orderdtl.getString("quantity")+orderdtl.getString("unit"));

            }
            catch(Exception e){
                Log.v("PRO", e.getMessage());
            }
            return super.getView(position, convertView, parent);
        }
    }

    @Event(R.id.btn_search)
    private void btn_searchclick(View v){
        getDate(true,true);
    }

    @SuppressLint("NewApi")
    private void setTab(){
        RelativeLayout.LayoutParams lp1 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(5));
        lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        RelativeLayout.LayoutParams lp2 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(1));
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tab_txt1.setTextColor(Color.parseColor("#000000"));
        tab_line1.setBackgroundColor(0xFFb5b6b9);
        tab_line1.setLayoutParams(lp2);
        tab_txt2.setTextColor(Color.parseColor("#000000"));
        tab_line2.setBackgroundColor(0xFFb5b6b9);
        tab_line2.setLayoutParams(lp2);
        tab_txt3.setTextColor(Color.parseColor("#000000"));
        tab_line3.setBackgroundColor(0xFFb5b6b9);
        tab_line3.setLayoutParams(lp2);
        tab_txt4.setTextColor(Color.parseColor("#000000"));
        tab_line4.setBackgroundColor(0xFFb5b6b9);
        tab_line4.setLayoutParams(lp2);
        tab_txt5.setTextColor(Color.parseColor("#000000"));
        tab_line5.setBackgroundColor(0xFFb5b6b9);
        tab_line5.setLayoutParams(lp2);
        if(orderStatus.equals("")){
            tab_txt1.setTextColor(Color.parseColor("#0083c8"));
            tab_line1.setBackground(CommonUtil.getDrawable(R.drawable.icon_blue_bottom));
            tab_line1.setLayoutParams(lp1);
        }else if(orderStatus.equals("0") ){
            tab_txt2.setTextColor(Color.parseColor("#0083c8"));
            tab_line2.setBackground(CommonUtil.getDrawable(R.drawable.icon_blue_bottom));
            tab_line2.setLayoutParams(lp1);
        }else if(orderStatus.equals("3") ){
            tab_txt3.setTextColor(Color.parseColor("#0083c8"));
            //tab_line3.setBackground(CommonUtil.getDrawable(R.drawable.tab_s));
            tab_line3.setBackground(CommonUtil.getDrawable(R.drawable.icon_blue_bottom));
            tab_line3.setLayoutParams(lp1);
        }else if(orderStatus.equals("4") ){
            tab_txt4.setTextColor(Color.parseColor("#0083c8"));
            tab_line4.setBackground(CommonUtil.getDrawable(R.drawable.icon_blue_bottom));
            tab_line4.setLayoutParams(lp1);
        }else if(orderStatus.equals("5") ){
            tab_txt5.setTextColor(Color.parseColor("#0083c8"));
            tab_line5.setBackground(CommonUtil.getDrawable(R.drawable.icon_blue_bottom));
            tab_line5.setLayoutParams(lp1);
        }
    }

    @SuppressLint("NewApi")
    private void setTabTwo(){
        RelativeLayout.LayoutParams lp1 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(5));
        lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        RelativeLayout.LayoutParams lp2 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(1));
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tab_two_txt1.setTextColor(Color.parseColor("#000000"));
        tab_two_line1.setBackgroundColor(0xFFb5b6b9);
        tab_two_line1.setLayoutParams(lp2);
        tab_two_txt2.setTextColor(Color.parseColor("#000000"));
        tab_two_line2.setBackgroundColor(0xFFb5b6b9);
        tab_two_line2.setLayoutParams(lp2);
        tab_two_txt3.setTextColor(Color.parseColor("#000000"));
        tab_two_line3.setBackgroundColor(0xFFb5b6b9);
        tab_two_line3.setLayoutParams(lp2);
        tab_two_txt4.setTextColor(Color.parseColor("#000000"));
        tab_two_line4.setBackgroundColor(0xFFb5b6b9);
        tab_two_line4.setLayoutParams(lp2);
        tab_two_txt5.setTextColor(Color.parseColor("#000000"));
        tab_two_line5.setBackgroundColor(0xFFb5b6b9);
        tab_two_line5.setLayoutParams(lp2);
        tab_two_txt6.setTextColor(Color.parseColor("#000000"));
        tab_two_line6.setBackgroundColor(0xFFb5b6b9);
        tab_two_line6.setLayoutParams(lp2);
        if(orderStatus.equals("")){
            tab_two_txt1.setTextColor(Color.parseColor("#0083c8"));
            tab_two_line1.setBackground(CommonUtil.getDrawable(R.drawable.icon_blue_bottom));
            tab_two_line1.setLayoutParams(lp1);
        }else if(orderStatus.equals("1") ){
            tab_two_txt2.setTextColor(Color.parseColor("#0083c8"));
            tab_two_line2.setBackground(CommonUtil.getDrawable(R.drawable.icon_blue_bottom));
            tab_two_line2.setLayoutParams(lp1);
        }else if(orderStatus.equals("4") ){
            tab_two_txt3.setTextColor(Color.parseColor("#0083c8"));
            tab_two_line3.setBackground(CommonUtil.getDrawable(R.drawable.icon_blue_bottom));
            tab_two_line3.setLayoutParams(lp1);
        }else if(orderStatus.equals("7") ){
            tab_two_txt4.setTextColor(Color.parseColor("#0083c8"));
            tab_two_line4.setBackground(CommonUtil.getDrawable(R.drawable.icon_blue_bottom));
            tab_two_line4.setLayoutParams(lp1);
        }else if(orderStatus.equals("8") ){
            tab_two_txt5.setTextColor(Color.parseColor("#0083c8"));
            tab_two_line5.setBackground(CommonUtil.getDrawable(R.drawable.icon_blue_bottom));
            tab_two_line5.setLayoutParams(lp1);
        }else if(orderStatus.equals("9") ){
            tab_two_txt6.setTextColor(Color.parseColor("#0083c8"));
            tab_two_line6.setBackground(CommonUtil.getDrawable(R.drawable.icon_blue_bottom));
            tab_two_line6.setLayoutParams(lp1);
        }
    }

    @SuppressLint("NewApi")
    @Event(value={R.id.tab1,R.id.tab2,R.id.tab3,R.id.tab4,R.id.tab5},type=View.OnTouchListener.class)
    private boolean tabTouch(View v, MotionEvent e){
        if(e.getAction() == MotionEvent.ACTION_UP){

            if(v.getId() ==R.id.tab1 ){
                orderStatus="";
            }else if(v.getId() ==R.id.tab2 ){
                orderStatus="0";
            }else if(v.getId() ==R.id.tab3 ){
                orderStatus="3";
            }else if(v.getId() ==R.id.tab4 ){
                orderStatus="4";
            }else if(v.getId() ==R.id.tab5 ){
                orderStatus="5";
            }
            setTab();
            getDate(true,true);
            return false;
        }
        return true;
    }

    @SuppressLint("NewApi")
    @Event(value={R.id.tab_two_1,R.id.tab_two_2,R.id.tab_two_3,R.id.tab_two_4,R.id.tab_two_5,R.id.tab_two_6},type=View.OnTouchListener.class)
    private boolean tabTwoTouch(View v, MotionEvent e){
        if(e.getAction() == MotionEvent.ACTION_UP){
            if(v.getId() ==R.id.tab_two_1 ){
                orderStatus="";
            }else if(v.getId() ==R.id.tab_two_2 ){
                orderStatus="1";
            }else if(v.getId() ==R.id.tab_two_3 ){
                orderStatus="4";
            }else if(v.getId() ==R.id.tab_two_4 ){
                orderStatus="7";
            }else if(v.getId() ==R.id.tab_two_5 ){
                orderStatus="8";
            }else if(v.getId() ==R.id.tab_two_6 ){
                orderStatus="9";
            }
            setTabTwo();
            getDate(true,true);
            return false;
        }
        return true;
    }

    @SuppressLint("NewApi")
    @Event(value={R.id.ll_allOrder,R.id.ll_commonOrder,R.id.ll_fastMatch,R.id.ll_orderMatch},type=View.OnTouchListener.class)
    private boolean selectTouch(View v, MotionEvent e){
        if(e.getAction() == MotionEvent.ACTION_UP){
            if(v.getId() ==R.id.ll_allOrder ){
                orderType="";
            }else if(v.getId() ==R.id.ll_commonOrder ){
                orderType="product";
            }else if(v.getId() ==R.id.ll_fastMatch ){
                orderType="fastMatch";
            }else if(v.getId() ==R.id.ll_orderMatch ){
                orderType="orderMatch";
            }
            setTab();
            setTabTwo();
            orderStatus="";
            cssInit(orderType);
            orderTypeInit(orderType);
            getDate(true,true);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BaseConst.RCODE_REFOUND && resultCode == BaseConst.RCODE_REFOUND){
            getDate(true,true);
        }
    }

    private void orderTypeInit(String type){
        if(FormatUtil.isNoEmpty(type)){
            if(type.equals("orderMatch")){
                ll_tab.setVisibility(View.GONE);
                ll_tab_two.setVisibility(View.VISIBLE);
                //创建RelativeLayout.LayoutParams
                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.BELOW,R.id.tab_two);
                rl_showData.setLayoutParams(params);
            }
            else{
                ll_tab.setVisibility(View.VISIBLE);
                ll_tab_two.setVisibility(View.GONE);
                //创建RelativeLayout.LayoutParams
                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.BELOW,R.id.tab);
                rl_showData.setLayoutParams(params);
            }
        }
        else{
            ll_tab.setVisibility(View.GONE);
            ll_tab_two.setVisibility(View.GONE);
        }
    }

    private void cssInit(String type){
        if(FormatUtil.isNoEmpty(type)){
            if(type.equals("orderMatch")){
                showCss(iv_orderMatch.getId(),tv_orderMatch.getId());
            }
            else if(type.equals("fastMatch")){
                showCss(iv_fastMatch.getId(),tv_fastMatch.getId());
            }
            else if(type.equals("product")){
                showCss(iv_commonOrder.getId(),tv_commonOrder.getId());
            }
            else{

            }
        }
        else{
            showCss(iv_allOrder.getId(),tv_allOrder.getId());
        }
    }

    private void showCss(int ivId,int tvId){
        hideAllImageView();
        hideAllTextView();
        if(ivId == iv_allOrder.getId()){
            iv_allOrder.setBackgroundResource(R.drawable.icon_radio_selected);
        }
        else if(ivId == iv_commonOrder.getId()){
            iv_commonOrder.setBackgroundResource(R.drawable.icon_radio_selected);
        }
        else if(ivId == iv_fastMatch.getId()){
            iv_fastMatch.setBackgroundResource(R.drawable.icon_radio_selected);
        }
        else if(ivId == iv_orderMatch.getId()){
            iv_orderMatch.setBackgroundResource(R.drawable.icon_radio_selected);
        }
        else{

        }

        if(tvId == tv_allOrder.getId()){
            //tv_allOrder.setTextColor(this.getBaseContext().getResources().getColor(R.color.bg_btn_blue));
            tv_allOrder.setTextColor(Color.BLUE);
        }
        else if(tvId == tv_commonOrder.getId()){
            tv_commonOrder.setTextColor(Color.BLUE);
        }
        else if(tvId == tv_fastMatch.getId()){
            tv_fastMatch.setTextColor(Color.BLUE);
        }
        else if(tvId == tv_orderMatch.getId()){
            tv_orderMatch.setTextColor(Color.BLUE);
        }
        else{

        }
    }

    private void hideAllImageView(){
        iv_orderMatch.setBackgroundResource(R.drawable.icon_radio_unselected);
        iv_fastMatch.setBackgroundResource(R.drawable.icon_radio_unselected);
        iv_commonOrder.setBackgroundResource(R.drawable.icon_radio_unselected);
        iv_allOrder.setBackgroundResource(R.drawable.icon_radio_unselected);
    }

    private void hideAllTextView(){
        tv_orderMatch.setTextColor(Color.GRAY);
        tv_fastMatch.setTextColor(Color.GRAY);
        tv_commonOrder.setTextColor(Color.GRAY);
        tv_allOrder.setTextColor(Color.GRAY);
    }
}
