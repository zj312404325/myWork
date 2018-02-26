package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.CommonDialog;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.BigDecimalUtil;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
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

@ContentView(R.layout.activity_fast_match_detail)
public class FastMatchDetailActivity extends TopActivity implements IXListViewListener {

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;
    @ViewInject(R.id.listtv)
    private TextView listtv;
    @ViewInject(R.id.tv_totalMoney)
    private TextView tv_totalMoney;
    @ViewInject(R.id.tv_delSelect)
    private TextView tv_delSelect;
    @ViewInject(R.id.cb_selectAll)
    private CheckBox cb_selectAll;
    @ViewInject(R.id.btn_submit)
    private Button btn_submit;

    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
    private SimpleAdapter sap;
    private Handler mHandler;
    private int start = 1;
    private String myScore="";
    private String skey;
    private String matchid;
    private String selectIds="";
    private String customid;
    private String orderType;
    private String goodsMoney;
    private int selectCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_match_detail);
        x.view().inject(this);
        super.title.setText("标准配明细");
        progressDialog.hide();
        skey=super.serverKey;

        Intent i = this.getIntent();
        matchid = i.getStringExtra("matchid");

        sap = new ProSimpleAdapter(FastMatchDetailActivity.this, dateMaps,
                R.layout.listview_fastmatch_detail,
                new String[]{"proName"},
                new int[]{R.id.tv_proName});
        listViewAll.setAdapter(sap);
        listViewAll.setPullLoadEnable(true);
        listViewAll.setXListViewListener(this);
        cb_selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TOakDO Auto-generated method stub
                allCheck(arg1);
            }
        });
        getData(true,true);
        mHandler = new Handler();
    }

    private void getData(final boolean isShow,final boolean flag){
        if(isShow){
            progressDialog.show();
        }
        if(flag){
            start = 1;
        }
        listtv.setVisibility(View.GONE);
        listViewAll.setPullLoadEnable(false);
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("currentPage", ""+start);
        maps.put("matchid", matchid);

        XUtilsHelper.getInstance().post("app/fastMatchDetail.htm", maps,new XUtilsHelper.XCallBack(){

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
                    JSONArray detailList = (JSONArray)res.get("detailList");
                    customid=res.get("customid").toString();

                    if(detailList.length()==0 && start == 1) {
                        listtv.setVisibility(View.VISIBLE);
                    }
                    else if(detailList.length() ==  10 ) {
                        listViewAll.setPullLoadEnable(true);
                    }

                    for(int i=0;i<detailList.length();i++){
                        Map<String, Object> dateMap = new HashMap<String, Object>();
                        dateMap.put("id", detailList.getJSONObject(i).get("iD"));
                        dateMap.put("proName", detailList.getJSONObject(i).get("proName"));
                        dateMap.put("brand", detailList.getJSONObject(i).get("brand"));
                        dateMap.put("proSpec", detailList.getJSONObject(i).get("proSpec"));
                        dateMap.put("quantity", detailList.getJSONObject(i).get("quantity"));
                        dateMap.put("unit", detailList.getJSONObject(i).get("unit"));
                        dateMap.put("hasGoods", detailList.getJSONObject(i).get("hasGoods"));
                        dateMap.put("salePrice", detailList.getJSONObject(i).get("salePrice"));
                        dateMap.put("money", detailList.getJSONObject(i).get("money"));
                        dateMap.put("hasOrder", detailList.getJSONObject(i).get("hasOrder"));
                        dateMap.put("hasDeal", detailList.getJSONObject(i).get("hasDeal"));
                        dateMap.put("isCheck", "0");
                        dateMap.put("hasCheck", "0");
                        dateMap.put("index", i);
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

    public class ProSimpleAdapter  extends SimpleAdapter {

        public ViewHolder holder;
        private LayoutInflater mInflater;
        private List<Map<String, Object>> myMaps;

        public ProSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myMaps = (List<Map<String, Object>>) data;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            holder=null;
            if(convertView==null){
                convertView=mInflater.inflate(R.layout.listview_fastmatch_detail, null);
                holder=new ViewHolder();
                x.view().inject(holder,convertView);
                convertView.setTag(holder);
            }
            else{
                holder=(ViewHolder) convertView.getTag();
            }

            final String id=myMaps.get(position).get("id").toString();
            final String proName=myMaps.get(position).get("proName").toString();
            final String brand=myMaps.get(position).get("brand").toString();
            final String proSpec=myMaps.get(position).get("proSpec").toString();
            final String quantity=myMaps.get(position).get("quantity").toString();
            final String unit=myMaps.get(position).get("unit").toString();
            final String hasGoods=myMaps.get(position).get("hasGoods").toString();
            final String salePrice=myMaps.get(position).get("salePrice").toString();
            final String money=myMaps.get(position).get("money").toString();
            final String hasOrder=myMaps.get(position).get("hasOrder").toString();
            final String hasDeal=myMaps.get(position).get("hasDeal").toString();

            holder.tv_proName.setText(proName);
            holder.tv_brand.setText(brand);
            holder.tv_spec.setText(proSpec);
            holder.tv_quantity.setText(quantity+unit);
            holder.tv_unitPrice.setText(salePrice);
            holder.tv_money.setText(money);

            if(hasGoods.equals("1")){
                holder.tv_unitPrice.setText("无货");
                holder.tv_money.setText("-");
            }
            else{
                if(salePrice.equals("0")){
                    holder.tv_unitPrice.setText("");
                }
                else {
                    holder.tv_unitPrice.setText(salePrice);
                }
                if(money.equals("0")){
                    holder.tv_money.setText("");
                }
                else {
                    holder.tv_money.setText(money);
                }

            }

            if(hasOrder.equals("1")) {
                holder.tv_orderStatus.setText("已生成订单");
            }
            else{
                holder.tv_orderStatus.setText("未生成订单");
            }

            if(!hasOrder.equals("1") && !hasGoods.equals("1") && (!FormatUtil.isNoEmpty(hasDeal) || hasDeal.equals("1"))){
                myMaps.get(position).put("hasCheck", "1");
                holder.cb_selectDtl.setVisibility(View.VISIBLE);
                holder.cb_selectDtl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                        String isCheck = arg1?"1":"0";
                        if(isCheck.equals("0")){
                            cb_selectAll.setChecked(false);
                        }
                        myMaps.get(position).put("isCheck", isCheck);
                        sap.notifyDataSetChanged();
                        getAllMoney();
                    }
                });
            }
            else{
                myMaps.get(position).put("hasCheck", "0");
                holder.cb_selectDtl.setVisibility(View.INVISIBLE);
            }

            if(myMaps.get(position).get("hasCheck").toString().equals("1")) {
                if (myMaps.get(position).get("isCheck").toString().equals("1")) {
                    holder.cb_selectDtl.setChecked(true);
                } else {
                    holder.cb_selectDtl.setChecked(false);
                }
            }

            return super.getView(position, convertView, parent);
        }
    }

    @Event(R.id.btn_submit)
    private void submitClick(View v){
        if(!FormatUtil.isNoEmpty(selectIds)){
            CommonUtil.alter("请选择需要生成订单的明细!");
            return;
        }
        else {
            new CommonDialog(FastMatchDetailActivity.this, R.style.dialog, "确定生成订单？", new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        orderType = "fastMatch";
                        progressDialog.show();
                        Map<String, String> maps = new HashMap<String, String>();
                        maps.put("serverKey", skey);
                        maps.put("ids", selectIds);
                        maps.put("orderType", orderType);
                        maps.put("customid", customid);
                        XUtilsHelper.getInstance().post("app/prepareMatchOrder.htm", maps, new XUtilsHelper.XCallBack() {

                            @SuppressLint("NewApi")
                            @Override
                            public void onResponse(String result) {
                                progressDialog.hide();
                                JSONObject res;
                                try {
                                    res = new JSONObject(result);
                                    setServerKey(res.get("serverKey").toString());
                                    if (res.get("d").equals("n")) {
                                        CommonUtil.alter(res.get("msg").toString());
                                    } else {
                                        Intent i = new Intent(getApplicationContext(), AddOrderActivity.class);
                                        i.putExtra("data", (Serializable) dateMaps);
                                        i.putExtra("goodsMoney", goodsMoney);
                                        i.putExtra("goodsCount", FormatUtil.toString(selectCount));
                                        i.putExtra("orderType", "fastMatch");
                                        i.putExtra("customid", customid);
                                        startActivity(i);
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                }
            }).setTitle("提示").show();
        }
    }


    @Event(R.id.tv_delSelect)
    private void delSelectClick(View v){
        if(!FormatUtil.isNoEmpty(selectIds)){
            CommonUtil.alter("请选择需要删除的明细!");
            return;
        }
        else {
            new CommonDialog(FastMatchDetailActivity.this, R.style.dialog, "确定要删除这些商品？", new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        progressDialog.show();
                        Map<String, String> maps = new HashMap<String, String>();
                        maps.put("serverKey", skey);
                        maps.put("ids", selectIds);

                        XUtilsHelper.getInstance().post("app/matchDetailDel.htm", maps, new XUtilsHelper.XCallBack() {

                            @SuppressLint("NewApi")
                            @Override
                            public void onResponse(String result) {
                                progressDialog.hide();
                                JSONObject res;
                                try {
                                    res = new JSONObject(result);
                                    setServerKey(res.get("serverKey").toString());
                                    if (res.get("d").equals("n")) {
                                        CommonUtil.alter(res.get("msg").toString());
                                    } else {
                                        sap.notifyDataSetChanged();
                                        getData(true, true);
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                }
            }).setTitle("提示").show();
        }
    }

    class ViewHolder{
        @ViewInject(R.id.cb_selectDtl)
        private CheckBox cb_selectDtl;
        @ViewInject(R.id.tv_proName)
        private TextView tv_proName;
        @ViewInject(R.id.tv_brand)
        private TextView tv_brand;
        @ViewInject(R.id.tv_spec)
        private TextView tv_spec;
        @ViewInject(R.id.tv_quantity)
        private TextView tv_quantity;
        @ViewInject(R.id.tv_unitPrice)
        private TextView tv_unitPrice;
        @ViewInject(R.id.tv_money)
        private TextView tv_money;
        @ViewInject(R.id.tv_orderStatus)
        private TextView tv_orderStatus;
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

    private void allCheck(boolean f){
        String isCheck = f?"1":"0";
        for(int i=0;i<dateMaps.size();i++){
            if(dateMaps.get(i).get("hasCheck").toString().equals("1")) {
                dateMaps.get(i).put("isCheck", isCheck);
            }
        }
        sap.notifyDataSetChanged();
        getAllMoney();
    }

    private void getAllMoney(){
        try{
            double tem = 0;
            int count=0;
            String ids="";
            for(int i=0;i<dateMaps.size();i++){
                if(dateMaps.get(i).get("hasCheck").toString().equals("1")) {
                    if (dateMaps.get(i).get("isCheck").toString().equals("1")) {
                        tem = BigDecimalUtil.add(tem, BigDecimalUtil.mul(FormatUtil.toDouble(dateMaps.get(i).get("salePrice").toString()), FormatUtil.toDouble(dateMaps.get(i).get("quantity").toString())));
                        ids = ids + dateMaps.get(i).get("id").toString() + ",";
                        count++;
                    }
                }
            }

            selectCount=count;
            //tv_totalMoney.setText("¥"+FormatUtil.toString(tem));

            if(FormatUtil.isNoEmpty(ids)) {
                selectIds = ids.substring(0, ids.length() - 1);
                progressDialog.show();
                Map<String, String> maps= new HashMap<String, String>();
                maps.put("serverKey", skey);
                maps.put("ids", selectIds);
                XUtilsHelper.getInstance().post("app/fastMatchSum.htm", maps,new XUtilsHelper.XCallBack(){

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
                                goodsMoney=FormatUtil.toString(res.get("allMoney"));
                                tv_totalMoney.setText("¥"+FormatUtil.toString(res.get("allMoney")));
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
            else{
                selectIds = ids;
                goodsMoney="0";
                tv_totalMoney.setText("¥0.00");
            }

            if(selectCount>0){
                btn_submit.setBackgroundColor(btn_submit.getResources().getColor(R.color.login_back_blue));
                btn_submit.setTextColor(btn_submit.getResources().getColor(R.color.login_text_white));
                btn_submit.setEnabled(true);
            }
            else{
                btn_submit.setBackgroundColor(btn_submit.getResources().getColor(R.color.login_back_gray));
                btn_submit.setTextColor(btn_submit.getResources().getColor(R.color.login_text_gray));
                btn_submit.setEnabled(false);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
