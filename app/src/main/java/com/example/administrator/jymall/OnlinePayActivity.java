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

    //-----------支付宝参数-------------
    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "";

    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "";

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static final String RSA2_PRIVATE = "";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;


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
                            }else {
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
        if(rb_alipay.isChecked()){
            selectAli=1;
        }
        else{
            selectAli=0;
        }
        if(selectAli==0 && selectWechat==0){
            CommonUtil.alter("请选择在线支付方式！");
            return ;
        }
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        if(selectAli==1) {
            CommonUtil.alter("敬请期待！");
            //payAli();
            /*maps.put("serverKey", super.serverKey);
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

            });*/
        }
        else if(selectWechat==1){

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

    /*@SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    *//**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     *//*
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(OnlinePayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(OnlinePayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(OnlinePayActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(OnlinePayActivity.this,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    *//**
     * 支付宝支付业务
     *
     *//*
    public void payAli() {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }

        *//**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         *//*
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(OnlinePayActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    *//**
     * 支付宝账户授权业务
     *
     * @param v
     *//*
    public void authV2(View v) {
        if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
                || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
                || TextUtils.isEmpty(TARGET_ID)) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER |APP_ID| RSA_PRIVATE| TARGET_ID")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    }).show();
            return;
        }

        *//**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * authInfo的获取必须来自服务端；
         *//*
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
        final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(OnlinePayActivity.this);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);

                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }

    *//**
     * get the sdk version. 获取SDK版本号
     *
     *//*
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }*/


}
