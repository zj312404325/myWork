package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.jymall.common.CommonDialog;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.XUtilsHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_refundmoney_two)
public class RefundMoneyTwoActivity extends TopActivity {

    @ViewInject(R.id.tv_proName)
    private TextView tv_proName;

    @ViewInject(R.id.tv_salePrice)
    private TextView tv_salePrice;

    @ViewInject(R.id.tv_quantity)
    private TextView tv_quantity;

    @ViewInject(R.id.tv_info)
    private TextView tv_info;

    @ViewInject(R.id.ll_refundWait)
    private LinearLayout ll_refundWait;

    @ViewInject(R.id.ll_refundRefuse)
    private LinearLayout ll_refundRefuse;

    @ViewInject(R.id.ll_refundOk)
    private LinearLayout ll_refundOk;

    @ViewInject(R.id.tv_refuseReason)
    private TextView tv_refuseReason;

    @ViewInject(R.id.tv_refundOkDate)
    private TextView tv_refundOkDate;

    @ViewInject(R.id.tv_refundMoney)
    private TextView tv_refundMoney;

    @ViewInject(R.id.btn_submit)
    private Button btn_submit;

    @ViewInject(R.id.btn_cancelRefund_refuse)
    private Button btn_cancelRefund_refuse;

    @ViewInject(R.id.img_proImgPath)
    private ImageView img_proImgPath;

    private String skey;
    private String refundid;
    private String orderid;
    private String orderdtlid;
    private String reason;
    private String remark;
    private String money;
    private String dtlmoney;
    private String fileurl;
    private String isReceived;
    private String refundStatus;

    private JSONObject order;
    private JSONObject orderdtl;
    private JSONObject refund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refundmoney_two);
        x.view().inject(this);
        super.title.setText("申请退款");
        super.progressDialog.hide();
        Intent i = this.getIntent();
        refundid = i.getStringExtra("refundId");
        orderid = i.getStringExtra("orderId");
        orderdtlid = i.getStringExtra("orderDtlId");
        initData();
    }


    private void initData(){
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("refundId", refundid);
        maps.put("orderId", orderid);
        maps.put("orderDtlId", orderdtlid);

        XUtilsHelper.getInstance().post("app/refundMoneyTwo.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                progressDialog.hide();
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    skey = res.get("serverKey").toString();
                    order = res.getJSONObject("order");
                    orderdtl= res.getJSONObject("orderdtl");
                    refund = res.getJSONObject("refund");
                    refundid=refund.getString("id");
                    dtlmoney=orderdtl.getString("money");
                    refundStatus=orderdtl.getString("refundStatus");

                    String info = "";
                    String salePrice = orderdtl.getString("salePrice");
                    info = "品牌："+orderdtl.getString("brand")+"\n"+"材质："+orderdtl.getString("proQuality")+"\n" +"规格："+orderdtl.getString("proSpec");
                    tv_info.setText(info);
                    tv_proName.setText(orderdtl.getString("proName"));
                    img_proImgPath.setBackgroundResource(0);

                    String orderType=order.get("orderType").toString();
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

                    if(salePrice.equals("0")){
                        salePrice = "面议";
                    }
                    else{
                        salePrice += "元/"+orderdtl.getString("unit");
                    }
                    tv_salePrice.setText(salePrice);
                    tv_quantity.setText("×"+orderdtl.getString("quantity")+orderdtl.getString("unit"));

                    if(refundStatus.equals("1")){
                        showRefundWait();
                    }
                    else if(refundStatus.equals("-1")){
                        tv_refuseReason.setText("- 拒绝原因："+refund.getString("msg"));
                        showRefundRefuse();
                    }
                    else if(refundStatus.equals("2")){
                        tv_refundOkDate.setText("- 退款时间："+orderdtl.getString("backEndDate"));
                        tv_refundMoney.setText(Html.fromHtml("- 退款金额：<font color=\"#e60012\">"+refund.getString("money")+"</font>元"));
                        showRefundOk();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    @Event(R.id.btn_cancelRefund_wait)
    private void cancelWaitClick(View v){
        new CommonDialog(RefundMoneyTwoActivity.this, R.style.dialog, "取消退款申请后，本次退款将关闭，您还可以再次发起退款/退货申请，确认取消？", new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    progressDialog.show();
                    Map<String, String> maps= new HashMap<String, String>();
                    maps.put("serverKey", skey);
                    maps.put("id", refundid);
                    maps.put("type", "1");
                    XUtilsHelper.getInstance().post("app/cancelRefund.htm", maps,new XUtilsHelper.XCallBack(){

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
                                    finish();
                                    Intent i =  new Intent(getApplicationContext(), RefundMoneyCancelActivity.class);
                                    i.putExtra("refundId", refundid);
                                    i.putExtra("cancelDate", res.get("canceldate").toString());
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

    @Event(R.id.btn_cancelRefund_refuse)
    private void cancelRefuseClick(View v){
        new CommonDialog(RefundMoneyTwoActivity.this, R.style.dialog, "取消退款申请后，本次退款将关闭，您还可以再次发起退款/退货申请，确认取消？", new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    progressDialog.show();
                    Map<String, String> maps= new HashMap<String, String>();
                    maps.put("serverKey", skey);
                    maps.put("id", refundid);
                    maps.put("type", "1");
                    XUtilsHelper.getInstance().post("app/cancelRefund.htm", maps,new XUtilsHelper.XCallBack(){

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
                                    finish();
                                    Intent i =  new Intent(getApplicationContext(), RefundMoneyCancelActivity.class);
                                    i.putExtra("refundId", refundid);
                                    i.putExtra("cancelDate", res.get("canceldate").toString());
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

    @Event(R.id.btn_editRefund_wait)
    private void editRefundWaitClick(View v){
        Intent i =  new Intent(getApplicationContext(), RefundMoneyOneEditActivity.class);
        i.putExtra("orderId", orderid);
        i.putExtra("orderDtlId", orderdtlid);
        startActivity(i);
    }

    @Event(R.id.btn_editRefund_refuse)
    private void editRefundRefuseClick(View v){
        Intent i =  new Intent(getApplicationContext(), RefundMoneyOneEditActivity.class);
        i.putExtra("orderId", orderid);
        i.putExtra("orderDtlId", orderdtlid);
        startActivity(i);
    }


    private void showRefundWait(){
        hideAll();
        ll_refundWait.setVisibility(View.VISIBLE);
    }

    private void showRefundRefuse(){
        hideAll();
        ll_refundRefuse.setVisibility(View.VISIBLE);
    }

    private void showRefundOk(){
        hideAll();
        ll_refundOk.setVisibility(View.VISIBLE);
    }

    private void hideAll(){
        ll_refundWait.setVisibility(View.GONE);
        ll_refundRefuse.setVisibility(View.GONE);
        ll_refundOk.setVisibility(View.GONE);
    }
}
