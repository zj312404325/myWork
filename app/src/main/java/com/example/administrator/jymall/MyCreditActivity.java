package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.XUtilsHelper;

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

@ContentView(R.layout.activity_credit_state)
public class MyCreditActivity extends TopActivity {
    @ViewInject(R.id.ll_step1)
    private LinearLayout ll_step1;
    @ViewInject(R.id.ll_step2)
    private LinearLayout ll_step2;
    @ViewInject(R.id.ll_step3)
    private LinearLayout ll_step3;
    @ViewInject(R.id.ll_step4)
    private LinearLayout ll_step4;
    @ViewInject(R.id.ll_step_ok)
    private LinearLayout ll_step_ok;
    @ViewInject(R.id.ll_step_refuse)
    private LinearLayout ll_step_refuse;

    private String id="";
    private String pic1="";
    private String pic2="";
    private String pic3="";
    private String compType="";
    private String personNumber="";

    private String sealurl="";
    private String paytype="";
    private String address="";
    private String products="";

    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("信用支付状态");
        progressDialog.hide();
        getState();
    }

    private void getState(){
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);

        disableInit();
        XUtilsHelper.getInstance().post("app/mallCreditSupply.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    JSONArray servicelist = (JSONArray)res.get("servicelist");

                    if(res.get("isEmpty").toString().equals("n")) {
                        JSONObject credit = (JSONObject)res.get("credit");
                        id=credit.getString("id");
                        if(credit.getString("status").equals("0")){
                            ll_step3.setVisibility(View.VISIBLE);
                        }
                        if(credit.getString("status").equals("2")){
                            ll_step_refuse.setVisibility(View.VISIBLE);
                        }
                        if(credit.getString("status").equals("1") && !credit.getString("ischecked").equals("1")){
                            ll_step4.setVisibility(View.VISIBLE);
                        }
                        if(credit.getString("status").equals("1") && credit.getString("ischecked").equals("1")){
                            ll_step_ok.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        ll_step1.setVisibility(View.VISIBLE);
                    }

                    for(int i=0;i<servicelist.length();i++){
                        Map<String, Object> dateMap = new HashMap<String, Object>();
                        dateMap.put("id", servicelist.getJSONObject(i).get("id"));
                        dateMap.put("fax", servicelist.getJSONObject(i).get("fax"));
                        dateMap.put("name", servicelist.getJSONObject(i).get("name"));
                        dateMaps.add(dateMap);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }

    @Event(R.id.submitbtn)
    private void submitclick(View v){
        subClick(v);
    }

    @Event(R.id.resubmitbtn)
    private void resubmitclick(View v){
        subClick(v);
    }

    @Event(R.id.submit)
    private void dosubmitclick(View v){
        pic1="";
        pic1="";
        pic1="";
        compType="";
        personNumber="";
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("id", id);
        maps.put("pic1", pic1);
        maps.put("pic2", pic2);
        maps.put("pic3", pic3);
        maps.put("compType", compType);
        maps.put("personNumber", personNumber);
        XUtilsHelper.getInstance().post("app/supplyMallCredit.htm", maps,new XUtilsHelper.XCallBack(){

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
                        getState();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    @Event(R.id.sub)
    private void subclick(View v){
        sealurl="";
        address="";
        products="";
        paytype="";
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("id", id);
        maps.put("sealurl", sealurl);
        maps.put("address", address);
        maps.put("products", products);
        maps.put("paytype", paytype);
        XUtilsHelper.getInstance().post("app/editeMallCredit.htm", maps,new XUtilsHelper.XCallBack(){

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
                        getState();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    private void disableInit() {
        ll_step1.setVisibility(View.GONE);
        ll_step2.setVisibility(View.GONE);
        ll_step3.setVisibility(View.GONE);
        ll_step4.setVisibility(View.GONE);
        ll_step_ok.setVisibility(View.GONE);
        ll_step_refuse.setVisibility(View.GONE);
    }

    private void subClick(View v){
        disableInit();
        ll_step2.setVisibility(View.VISIBLE);
    }
}