package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.example.administrator.jymall.view.MyConfirmDialog;
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

@ContentView(R.layout.activity_order_match_detail_edit)
public class EditOrderMatchDetailActivity extends TopActivity implements IXListViewListener {

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
    private String factoryId="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_match_detail_edit);
        x.view().inject(this);
        super.title.setText("定制配明细");
        progressDialog.hide();
        skey=super.serverKey;

        Intent i = this.getIntent();
        matchid = i.getStringExtra("matchid");

        sap = new ProSimpleAdapter(EditOrderMatchDetailActivity.this, dateMaps,
                R.layout.listview_ordermatch_detail_edit,
                new String[]{"proName"},
                new int[]{R.id.tv_proName});
        listViewAll.setAdapter(sap);
        listViewAll.setPullLoadEnable(true);
        listViewAll.setXListViewListener(this);

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

        XUtilsHelper.getInstance().post("app/orderMatchDetail.htm", maps,new XUtilsHelper.XCallBack(){

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
                    factoryId=res.getJSONObject("match").getString("factoryid");

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
                        dateMap.put("proQuality", detailList.getJSONObject(i).get("proQuality"));
                        dateMap.put("model", detailList.getJSONObject(i).get("model"));
                        dateMap.put("proSpec", detailList.getJSONObject(i).get("proSpec"));
                        dateMap.put("quantity", detailList.getJSONObject(i).get("quantity"));
                        dateMap.put("unit", detailList.getJSONObject(i).get("unit"));
                        dateMap.put("proDesc", detailList.getJSONObject(i).get("proDesc"));
                        dateMap.put("specialReq", detailList.getJSONObject(i).get("specialReq"));
                        dateMap.put("functionReq", detailList.getJSONObject(i).get("functionReq"));
                        dateMap.put("parameter", detailList.getJSONObject(i).get("parameter"));
                        dateMap.put("remark", detailList.getJSONObject(i).get("remark"));
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
                convertView=mInflater.inflate(R.layout.listview_ordermatch_detail_edit, null);
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
            final String proQuality=myMaps.get(position).get("proQuality").toString();
            final String proSpec=myMaps.get(position).get("proSpec").toString();
            final String quantity=myMaps.get(position).get("quantity").toString();
            final String unit=myMaps.get(position).get("unit").toString();
            final String functionReq=myMaps.get(position).get("functionReq").toString();
            final String parameter=myMaps.get(position).get("parameter").toString();
            final String remark=myMaps.get(position).get("remark").toString();
            final String hasGoods=myMaps.get(position).get("hasGoods").toString();
            final String salePrice=myMaps.get(position).get("salePrice").toString();
            final String money=myMaps.get(position).get("money").toString();
            final String hasOrder=myMaps.get(position).get("hasOrder").toString();
            final String hasDeal=myMaps.get(position).get("hasDeal").toString();

            Button btn_save = (Button)convertView.findViewById(R.id.savebtn);
            final EditText et_proName = (EditText)convertView.findViewById(R.id.et_proName);
            final EditText et_brand = (EditText)convertView.findViewById(R.id.et_brand);
            final EditText et_proQuality = (EditText)convertView.findViewById(R.id.et_proQuality);
            final EditText et_proSpec = (EditText)convertView.findViewById(R.id.et_spec);
            final EditText et_quantity = (EditText)convertView.findViewById(R.id.et_quantity);
            final EditText et_unit = (EditText)convertView.findViewById(R.id.et_unit);
            final EditText et_functionReq = (EditText)convertView.findViewById(R.id.et_functionReq);
            final EditText et_parameter = (EditText)convertView.findViewById(R.id.et_parameter);
            final EditText et_remark = (EditText)convertView.findViewById(R.id.et_remark);

            //把Bean与输入框进行绑定-START
            et_proName.setTag(myMaps.get(position));
            //清除焦点
            et_proName.clearFocus();
            et_proName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //获得Edittext所在position里面的Bean，并设置数据
                    Map<String, Object> data = (Map<String, Object>) et_proName.getTag();
                    data.put("proName",s+"");
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            if(FormatUtil.isNoEmpty(myMaps.get(position).get("proName").toString())){
                et_proName.setText(myMaps.get(position).get("proName").toString());
            }else{
                et_proName.setText("");
            }
            et_proName.setSelection(et_proName.getText().length());
            //把Bean绑定END

            //把Bean与输入框进行绑定-START
            et_brand.setTag(myMaps.get(position));
            //清除焦点
            et_brand.clearFocus();
            et_brand.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //获得Edittext所在position里面的Bean，并设置数据
                    Map<String, Object> data = (Map<String, Object>) et_brand.getTag();
                    data.put("brand",s+"");
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            if(FormatUtil.isNoEmpty(myMaps.get(position).get("brand").toString())){
                et_brand.setText(myMaps.get(position).get("brand").toString());
            }else{
                et_brand.setText("");
            }
            //把Bean绑定END

            //把Bean与输入框进行绑定-START
            et_proQuality.setTag(myMaps.get(position));
            //清除焦点
            et_proQuality.clearFocus();
            et_proQuality.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //获得Edittext所在position里面的Bean，并设置数据
                    Map<String, Object> data = (Map<String, Object>) et_proQuality.getTag();
                    data.put("proQuality",s+"");
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            if(FormatUtil.isNoEmpty(myMaps.get(position).get("proQuality").toString())){
                et_proQuality.setText(myMaps.get(position).get("proQuality").toString());
            }else{
                et_proQuality.setText("");
            }
            //把Bean绑定END

            //把Bean与输入框进行绑定-START
            et_proSpec.setTag(myMaps.get(position));
            //清除焦点
            et_proSpec.clearFocus();
            et_proSpec.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //获得Edittext所在position里面的Bean，并设置数据
                    Map<String, Object> data = (Map<String, Object>) et_proSpec.getTag();
                    data.put("proSpec",s+"");
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            if(FormatUtil.isNoEmpty(myMaps.get(position).get("proSpec").toString())){
                et_proSpec.setText(myMaps.get(position).get("proSpec").toString());
            }else{
                et_proSpec.setText("");
            }
            //把Bean绑定END


            //把Bean与输入框进行绑定-START
            et_quantity.setTag(myMaps.get(position));
            //清除焦点
            et_quantity.clearFocus();
            et_quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //获得Edittext所在position里面的Bean，并设置数据
                    Map<String, Object> data = (Map<String, Object>) et_quantity.getTag();
                    data.put("quantity",s+"");
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            if(FormatUtil.isNoEmpty(myMaps.get(position).get("quantity").toString())){
                et_quantity.setText(myMaps.get(position).get("quantity").toString());
            }else{
                et_quantity.setText("");
            }
            //把Bean绑定END

            //把Bean与输入框进行绑定-START
            et_unit.setTag(myMaps.get(position));
            //清除焦点
            et_unit.clearFocus();
            et_unit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //获得Edittext所在position里面的Bean，并设置数据
                    Map<String, Object> data = (Map<String, Object>) et_unit.getTag();
                    data.put("unit",s+"");
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            if(FormatUtil.isNoEmpty(myMaps.get(position).get("unit").toString())){
                et_unit.setText(myMaps.get(position).get("unit").toString());
            }else{
                et_unit.setText("");
            }
            //把Bean绑定END

            //把Bean与输入框进行绑定-START
            et_functionReq.setTag(myMaps.get(position));
            //清除焦点
            et_functionReq.clearFocus();
            et_functionReq.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //获得Edittext所在position里面的Bean，并设置数据
                    Map<String, Object> data = (Map<String, Object>) et_functionReq.getTag();
                    data.put("functionReq",s+"");
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            if(FormatUtil.isNoEmpty(myMaps.get(position).get("functionReq").toString())){
                et_functionReq.setText(myMaps.get(position).get("functionReq").toString());
            }else{
                et_functionReq.setText("");
            }
            //把Bean绑定END

            //把Bean与输入框进行绑定-START
            et_parameter.setTag(myMaps.get(position));
            //清除焦点
            et_parameter.clearFocus();
            et_parameter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //获得Edittext所在position里面的Bean，并设置数据
                    Map<String, Object> data = (Map<String, Object>) et_parameter.getTag();
                    data.put("parameter",s+"");
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            if(FormatUtil.isNoEmpty(myMaps.get(position).get("parameter").toString())){
                et_parameter.setText(myMaps.get(position).get("parameter").toString());
            }else{
                et_parameter.setText("");
            }
            //把Bean绑定END

            //把Bean与输入框进行绑定-START
            et_remark.setTag(myMaps.get(position));
            //清除焦点
            et_remark.clearFocus();
            et_remark.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //获得Edittext所在position里面的Bean，并设置数据
                    Map<String, Object> data = (Map<String, Object>) et_remark.getTag();
                    data.put("remark",s+"");
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            if(FormatUtil.isNoEmpty(myMaps.get(position).get("remark").toString())){
                et_remark.setText(myMaps.get(position).get("remark").toString());
            }else{
                et_remark.setText("");
            }
            //把Bean绑定END

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    //非空校验
                    if(!FormatUtil.isNoEmpty(FormatUtil.toString(et_proName.getText().toString()))){
                        CommonUtil.alter("商品名称不能为空！");
                        return;
                    }
                    else if(FormatUtil.getStringLength(FormatUtil.toString(et_proName.getText().toString()))>25){
                        CommonUtil.alter("商品名称过长！");
                        return;
                    }
                    if(!FormatUtil.isNoEmpty(FormatUtil.toString(et_proQuality.getText().toString()))){
                        CommonUtil.alter("材质不能为空！");
                        return;
                    }
                    else if(FormatUtil.getStringLength(FormatUtil.toString(et_proQuality.getText().toString()))>25){
                        CommonUtil.alter("材质过长！");
                        return;
                    }
                    if(!FormatUtil.isNoEmpty(FormatUtil.toString(et_proSpec.getText().toString()))){
                        CommonUtil.alter("规格不能为空！");
                        return;
                    }
                    else if(FormatUtil.getStringLength(FormatUtil.toString(et_proSpec.getText().toString()))>25){
                        CommonUtil.alter("规格过长！");
                        return;
                    }
                    if(!FormatUtil.isNoEmpty(FormatUtil.toString(et_quantity.getText().toString()))){
                        CommonUtil.alter("数量不能为空！");
                        return;
                    }
                    if(!FormatUtil.isNoEmpty(FormatUtil.toString(et_unit.getText().toString()))){
                        CommonUtil.alter("单位不能为空！");
                        return;
                    }
                    else if(FormatUtil.getStringLength(FormatUtil.toString(et_unit.getText().toString()))>25){
                        CommonUtil.alter("单位过长！");
                        return;
                    }
                    if(!FormatUtil.isNoEmpty(FormatUtil.toString(et_parameter.getText().toString()))){
                        CommonUtil.alter("参数不能为空！");
                        return;
                    }
                    else if(FormatUtil.getStringLength(FormatUtil.toString(et_parameter.getText().toString()))>25){
                        CommonUtil.alter("参数过长！");
                        return;
                    }

                    //非必填项校验
                    if(FormatUtil.getStringLength(FormatUtil.toString(et_brand.getText().toString()))>25){
                        CommonUtil.alter("品牌过长！");
                        return;
                    }
                    if(FormatUtil.getStringLength(FormatUtil.toString(et_functionReq.getText().toString()))>25){
                        CommonUtil.alter("性能要求过长！");
                        return;
                    }
                    if(FormatUtil.getStringLength(FormatUtil.toString(et_remark.getText().toString()))>50){
                        CommonUtil.alter("备注过长！");
                        return;
                    }
                    //
                    // 编辑明细开始
                    final MyConfirmDialog mcd = new MyConfirmDialog(EditOrderMatchDetailActivity.this, "您确认修改明细？", "修改", "取消");
                    mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                        @Override
                        public void doConfirm() {
                            mcd.dismiss();
                            progressDialog.show();
                            Map<String, String> maps= new HashMap<String, String>();
                            maps.put("serverKey", skey);
                            maps.put("id", id);
                            maps.put("matchid", matchid);
                            maps.put("proName", et_proName.getText().toString());
                            maps.put("brand", et_brand.getText().toString());
                            maps.put("proQuality", et_proQuality.getText().toString());
                            maps.put("proSpec", et_proSpec.getText().toString());
                            maps.put("quantity", et_quantity.getText().toString());
                            maps.put("unit", et_unit.getText().toString());
                            maps.put("functionReq", et_functionReq.getText().toString());
                            maps.put("parameter", et_parameter.getText().toString());
                            maps.put("remark", et_remark.getText().toString());
                            XUtilsHelper.getInstance().post("app/editOrderMatchDtl.htm", maps,new XUtilsHelper.XCallBack(){

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
                                            CommonUtil.alter("修改成功！");
                                            getData(true,true);
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
            new CommonDialog(EditOrderMatchDetailActivity.this, R.style.dialog, "确定生成订单？", new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        if(FormatUtil.isNoEmpty(factoryId)){
                            orderType = "fabFastMatch";
                        }
                        else {
                            orderType = "fastMatch";
                        }
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
                                        i.putExtra("orderType", orderType);
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
            new CommonDialog(EditOrderMatchDetailActivity.this, R.style.dialog, "确定要删除这些商品？", new CommonDialog.OnCloseListener() {
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

        @ViewInject(R.id.et_proName)
        private TextView et_proName;
        @ViewInject(R.id.et_brand)
        private TextView et_brand;
        @ViewInject(R.id.et_proQuality)
        private TextView et_proQuality;
        @ViewInject(R.id.et_spec)
        private TextView et_spec;
        @ViewInject(R.id.et_quantity)
        private TextView et_quantity;
        @ViewInject(R.id.et_unit)
        private TextView et_unit;
        @ViewInject(R.id.et_functionReq)
        private TextView et_functionReq;
        @ViewInject(R.id.et_parameter)
        private TextView et_parameter;
        @ViewInject(R.id.et_remark)
        private TextView et_remark;
        @ViewInject(R.id.savebtn)
        private Button savebtn;

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
