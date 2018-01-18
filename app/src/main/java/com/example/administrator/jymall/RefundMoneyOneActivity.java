package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.jymall.common.CommonDialog;
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

    @ViewInject(R.id.tv_totalMoney)
    private TextView tv_totalMoney;

    @ViewInject(R.id.tv_refundMoney)
    private EditText tv_refundMoney;

    @ViewInject(R.id.btn_submit)
    private Button btn_submit;

    @ViewInject(R.id.img_proImgPath)
    private ImageView img_proImgPath;

    private String orderid;
    private String orderdtlid;
    private String reason;
    private String remark;
    private String money;
    private String dtlmoney;
    private String fileurl;
    private String isReceived;

    private String skey;
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
        skey=super.serverKey;
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
                    img_proImgPath.setBackgroundResource(0);
                    XUtilsHelper.getInstance().bindCommonImage(img_proImgPath, orderdtl.getString("proImgPath"), true);
                    if(salePrice.equals("0")){
                        salePrice = "面议";
                    }
                    else{
                        salePrice += "元/"+orderdtl.getString("unit");
                    }
                    tv_salePrice.setText(salePrice);
                    tv_quantity.setText("×"+orderdtl.getString("quantity")+orderdtl.getString("unit"));

                    tv_refundMoney.setText(dtlmoney);
                    tv_totalMoney.setText("最多"+dtlmoney+"元");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    @Event(R.id.btn_submit)
    private void submitClick(View v){
        money=tv_refundMoney.getText().toString();
        isReceived="0";
        reason="做工问题";
        if(!FormatUtil.isNoEmpty(money)){
            CommonUtil.alter("退款金额不能为空！");
            return ;
        }
        else if(!FormatUtil.isNoEmpty(isReceived)){
            CommonUtil.alter("请选择收货状态！");
            return ;
        }
        else if(!FormatUtil.isNoEmpty(reason)){
            CommonUtil.alter("退款原因不能为空！");
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

        new CommonDialog(RefundMoneyOneActivity.this, R.style.dialog, "确定提交？", new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    progressDialog.show();
                    Map<String, String> maps= new HashMap<String, String>();
                    maps.put("serverKey", skey);
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
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();


    }


}
