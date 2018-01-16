package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_refundmoney_one)
public class RefundMoneyOneActivity extends TopActivity {

    @ViewInject(R.id.tv_proName)
    private TextView tv_proName;

    @ViewInject(R.id.tv_salePrice)
    private TextView tv_salePrice;

    @ViewInject(R.id.tv_quantity)
    private TextView tv_quantity;

    @ViewInject(R.id.tv_info)
    private TextView tv_info;

    @ViewInject(R.id.btn_submit)
    private Button btn_submit;

    private String orderid;
    private String orderdtlid;
    private String reason;
    private String remark;
    private String money;
    private String dtlmoney;
    private String fileurl;
    private String isReceived;

    private JSONObject order;
    private JSONObject orderdtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("申请退款");
        super.progressDialog.hide();
        Intent i = this.getIntent();
        orderid = i.getStringExtra("orderId");
        orderdtlid = i.getStringExtra("orderDtlId");
        initData();
    }


    private void initData(){
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("orderId", orderid);
        maps.put("orderDtlId", orderdtlid);

        XUtilsHelper.getInstance().post("app/goRefundMoney.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                progressDialog.hide();
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    order = res.getJSONObject("order");
                    orderdtl= res.getJSONObject("orderdtl");
                    dtlmoney=orderdtl.getString("money");

                    String info = "";
                    String salePrice = orderdtl.getString("salePrice");
                    info = "品牌："+orderdtl.getString("brand")+"\n"+"材质："+orderdtl.getString("proQuality")+"\n" +"规格："+orderdtl.getString("proSpec");
                    tv_info.setText(info);
                    tv_proName.setText(orderdtl.getString("proName"));
                    if(salePrice.equals("0")){
                        salePrice = "面议";
                    }
                    else{
                        salePrice += "元/"+orderdtl.getString("unit");
                    }
                    tv_salePrice.setText(salePrice);
                    tv_quantity.setText("×"+orderdtl.getString("quantity")+orderdtl.getString("unit"));

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    @Event(R.id.btn_submit)
    private void submitClick(View v){
        if(!FormatUtil.isNoEmpty(money)){
            CommonUtil.alter("退款金额不能为空！");
            return ;
        }
        else{
            if(FormatUtil.toDouble(money)<=0){
                CommonUtil.alter("金额不能小于0！");
                return ;
            }
            if(FormatUtil.toDouble(money) > FormatUtil.toDouble(dtlmoney)){
                CommonUtil.alter("金额不能大于明细金额！");
                return ;
            }
        }
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("orderId", orderid);
        maps.put("orderDtlId", orderdtlid);
        maps.put("flag", "1");
        maps.put("refundmemo", remark);
        maps.put("reason", reason);
        maps.put("isreceived", isReceived);
        maps.put("fileurl", fileurl);
        maps.put("mymoney", money);
        XUtilsHelper.getInstance().post("app/submitRefund.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                progressDialog.hide();
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    String refundid=res.get("refundid").toString();
                    setServerKey(res.get("serverKey").toString());
                    if(res.get("d").equals("n")){
                        CommonUtil.alter(res.get("msg").toString());
                    }
                    else{
                        CommonUtil.alter(res.get("msg").toString());
                        finish();
                        Intent i =  new Intent(getApplicationContext(), RefundMoneyTwoActivity.class);
                        i.putExtra("refundId", refundid);
                        startActivity(i);

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }


}
