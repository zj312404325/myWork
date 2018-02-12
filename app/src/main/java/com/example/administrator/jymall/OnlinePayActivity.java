package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

@ContentView(R.layout.activity_online_pay)
public class OnlinePayActivity extends TopActivity {

    private String id;
    private String orderNo;
    private String totalMoney;
    private String adminmobile;
    private String hascredit;
    private String orderdtlProcId;
    private int selectAli=0;
    private int selectWechat=0;

    private double umoney=0; //余额
    private double umoneysy=0; //剩余余额
    private double money=0; //非定制配支付金额
    private double orderPayMoney=0; //定制配支付金额
    private double paidMoney=0; //已付金额

    @ViewInject(R.id.tv_haspwd)
    private TextView tv_haspwd;

    @ViewInject(R.id.tv_money)
    private TextView tv_money;

    @ViewInject(R.id.tv_orderNo)
    private TextView tv_orderNo;

    @ViewInject(R.id.tv_orderMoney)
    private TextView tv_orderMoney;

    @ViewInject(R.id.tv_paidMoney)
    private TextView tv_paidMoney;

    @ViewInject(R.id.et_password)
    private EditText et_password;

    @ViewInject(R.id.btn_umoneyPay)
    private Button btn_umoneyPay;

    @ViewInject(R.id.ll_umoneyPwd)
    private LinearLayout ll_umoneyPwd;

    @ViewInject(R.id.ll_payMethod)
    private LinearLayout ll_payMethod;

    @ViewInject(R.id.rl_payPwd)
    private RelativeLayout rl_payPwd;

    @ViewInject(R.id.rg_payMethod)
    private RadioGroup rg_payMethod;

    @ViewInject(R.id.rb_alipay)
    private RadioButton rb_alipay;

    @ViewInject(R.id.rb_wechatpay)
    private RadioButton rb_wechatpay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("在线支付");
        super.progressDialog.hide();

        Intent i =this.getIntent();
        id = i.getStringExtra("id");
        orderNo = i.getStringExtra("orderNo");
        totalMoney = i.getStringExtra("totalMoney");
        orderdtlProcId= i.getStringExtra("orderdtlProcId");
        adminmobile= i.getStringExtra("adminmobile");

    }

    private void initData(){
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("id", id);
        XUtilsHelper.getInstance().post("app/checkPaypwd.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                progressDialog.hide();
                final JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    if(res.get("d").equals("n")){
                        CommonUtil.alter(res.get("msg").toString());
                    }
                    else{
                        String haspwd = res.getString("haspwd");
                        if(haspwd.equals("1")){
                            tv_haspwd.setText("如需修改密码,点击此处设置");
                            rl_payPwd.setVisibility(View.VISIBLE);
                        }
                        else{
                            tv_haspwd.setText("你还没有支付密码,点击此处设置");
                            rl_payPwd.setVisibility(View.GONE);
                        }
                    }
                    //为RadioGroup设置监听事件
                    rg_payMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            // TODO Auto-generated method stub
                            if(checkedId == rb_alipay.getId()){
                                selectAli=1;
                                selectWechat=0;
                            }else if(checkedId == rb_wechatpay.getId()){
                                selectWechat=1;
                                selectAli=0;
                            }
                        }
                    });
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Event(R.id.btn_onlinePay)
    private void onlinePay(View v){
        if(et_password.getText().length()<1){
            CommonUtil.alter("密码不能为空！");
            return ;
        }
        if(selectAli==0 && selectWechat==0){
            CommonUtil.alter("请选择在线支付方式！");
            return ;
        }
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        if(selectAli==1) {
            maps.put("serverKey", super.serverKey);
            maps.put("orderId",id);
            maps.put("WIDout_trade_no", orderNo);
            maps.put("WIDsubject", "金赢网订单支付");
            maps.put("WIDtotal_fee", totalMoney);
            maps.put("zfbpaymethod", "0");
            maps.put("zfbPayOrderid", id);
            maps.put("zfbPayorderdtlProcId", orderdtlProcId);
            maps.put("orderType", "mall");
            maps.put("password", et_password.getText().toString());
            XUtilsHelper.getInstance().post("app/alipayIndex.htm", maps, new XUtilsHelper.XCallBack() {

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
                            CommonUtil.alter("支付成功");
                            setResult(RESULT_OK);
                            finish();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            });
        }
        else if(selectWechat==1){
            XUtilsHelper.getInstance().post("app/doOfflinePay.htm", maps, new XUtilsHelper.XCallBack() {

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
                            CommonUtil.alter("支付成功");
                            setResult(RESULT_OK);
                            finish();
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            });
        }
    }


    @Event(R.id.rl_paypwdedit)
    private void paypwdedit(View v){
        Intent i =  new Intent(getApplicationContext(), PayPasswordActivity.class);
        i.putExtra("adminmobile", adminmobile);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode == CommonUtil.getInt(R.string.RECODE_RECHARGE) ){

            }
            else if(requestCode == CommonUtil.getInt(R.string.RECODE_DOWNPAY)){
                Intent intent = new Intent(getApplicationContext(),MyOrderInfoActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
            }
        }
    }

}
