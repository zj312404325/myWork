package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyConfirmDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_refundgoods_two)
public class RefundGoodsTwoActivity extends TopActivity {

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

    @ViewInject(R.id.ll_refundCommitLogistic)
    private LinearLayout ll_refundCommitLogistic;

    @ViewInject(R.id.ll_refundSendGoods)
    private LinearLayout ll_refundSendGoods;

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

    private String skey;
    private String orderid;
    private String orderdtlid;
    private String reason;
    private String remark;
    private String money;
    private String dtlmoney;
    private String isReceived;
    private String refundStatus;

    private String refundid;
    private String fileurl;
    private String serviceid;
    private String logistic;
    private String logisticno;
    private String logisticremark;

    private JSONObject order;
    private JSONObject orderdtl;
    private JSONObject refund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("申请退货");
        super.progressDialog.hide();
        Intent i = this.getIntent();
        refundid = i.getStringExtra("refundId");
        initData();
    }


    private void initData(){
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("refundId", refundid);

        XUtilsHelper.getInstance().post("app/refundGoodsTwo.htm", maps,new XUtilsHelper.XCallBack(){

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
                    dtlmoney=orderdtl.getString("money");
                    refundStatus=orderdtl.getString("refundStatus");

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

                    if(refundStatus.equals("1")){
                        showRefundWait();
                    }
                    else if(refundStatus.equals("-1")){
                        tv_refuseReason.setText("- 拒绝原因："+refund.getString("msg"));
                        showRefundRefuse();
                    }
                    else if(refundStatus.equals("2")){
                        showRefundCommitLogistic();
                    }
                    else if(refundStatus.equals("3")){
                        showRefundSendGoods();
                    }
                    else if(refundStatus.equals("4")){
                        tv_refundOkDate.setText("- 退款时间："+orderdtl.getString("backEndDate"));
                        tv_refundMoney.setText("- 退款金额："+refund.getString("money"));
                        showRefundOk();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    @Event(R.id.btn_cancelRefund_refuse)
    private void cancelRefuseClick(View v){
        progressDialog.show();
        // 交易取消开始
        final MyConfirmDialog mcd = new MyConfirmDialog(RefundGoodsTwoActivity.this, "取消退货申请后，本次退款将关闭，您还可以再次发起退款/退货申请。确认取消?", "确认取消", "否");
        mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                mcd.dismiss();
                progressDialog.show();
                Map<String, String> maps= new HashMap<String, String>();
                maps.put("serverKey", skey);
                maps.put("id", refundid);
                maps.put("type", "0");
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
            }
            @Override
            public void doCancel() {
                mcd.dismiss();
            }
        });
        mcd.show();
    }

    @Event(R.id.btn_submit)
    private void submitClick(View v){
        progressDialog.show();
        final MyConfirmDialog mcd = new MyConfirmDialog(RefundGoodsTwoActivity.this, "取消退货申请后，本次退款将关闭，您还可以再次发起退款/退货申请。确认取消?", "确认取消", "否");
        mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                mcd.dismiss();
                progressDialog.show();
                Map<String, String> maps= new HashMap<String, String>();
                maps.put("serverKey", skey);
                maps.put("refundid", refundid);
                maps.put("logisticno", logisticno);
                maps.put("logistic", logistic);
                maps.put("logisticremark", logisticremark);
                maps.put("fileurl", fileurl);
                maps.put("serviceid", serviceid);

                XUtilsHelper.getInstance().post("app/addRefundLogistic.htm", maps,new XUtilsHelper.XCallBack(){

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
            }
            @Override
            public void doCancel() {
                mcd.dismiss();
            }
        });
        mcd.show();
    }

    private void showRefundWait(){
        hideAll();
        ll_refundWait.setVisibility(View.VISIBLE);
    }

    private void showRefundRefuse(){
        hideAll();
        ll_refundRefuse.setVisibility(View.VISIBLE);
    }

    private void showRefundCommitLogistic(){
        hideAll();
        ll_refundCommitLogistic.setVisibility(View.VISIBLE);
    }

    private void showRefundSendGoods(){
        hideAll();
        ll_refundSendGoods.setVisibility(View.VISIBLE);
    }

    private void showRefundOk(){
        hideAll();
        ll_refundRefuse.setVisibility(View.VISIBLE);
    }

    private void hideAll(){
        ll_refundWait.setVisibility(View.GONE);
        ll_refundRefuse.setVisibility(View.GONE);
        ll_refundCommitLogistic.setVisibility(View.GONE);
        ll_refundSendGoods.setVisibility(View.GONE);
        ll_refundOk.setVisibility(View.GONE);
    }
}
