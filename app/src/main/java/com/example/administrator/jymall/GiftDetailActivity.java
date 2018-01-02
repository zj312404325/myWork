package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.jymall.common.MyApplication;
import com.example.administrator.jymall.common.UserActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.AmountView;
import com.example.administrator.jymall.view.ShufflingView;

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

@ContentView(R.layout.activity_gift_detail)
public class GiftDetailActivity extends UserActivity {

    @ViewInject(R.id.toinfo)
    private RelativeLayout toinfo;

    @ViewInject(R.id.top_back)
    private ImageButton top_back;

    private String id;

    @ViewInject(R.id.shufflingView)
    private ShufflingView shufflingView;//产品图片轮换控件
    private List<String> mImageIds = new ArrayList<String>();//产品图片数据

    private JSONObject gift;
    private String isLogin="";
    private String myScore="";
    private String flag="";

    @ViewInject(R.id.tv_giftName)
    private TextView tv_giftName;
    @ViewInject(R.id.tv_giftDesc)
    private TextView tv_giftDesc;
    @ViewInject(R.id.tv_giftNo)
    private TextView tv_giftNo;
    @ViewInject(R.id.tv_giftScore)
    private TextView tv_giftScore;
    @ViewInject(R.id.tv_myScore)
    private TextView tv_myScore;
    @ViewInject(R.id.tv_quantity)
    private TextView tv_quantity;
    @ViewInject(R.id.submitbtn)
    private Button submitbtn;
    @ViewInject(R.id.iv_showImg)
    private ImageView iv_showImg;

    @ViewInject(R.id.av_quantity)
    private AmountView av_quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        progressDialog.hide();
        Intent i = this.getIntent();
        id = i.getStringExtra("id");
        getDetailData();
    }

    private void getDetailData(){
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("id", id);
        XUtilsHelper.getInstance().post("app/mallGiftDtl.htm", maps,new XUtilsHelper.XCallBack(){

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
                        MyApplication.getInstance().finishActivity();
                    }
                    else{
                        gift = (JSONObject)res.get("data");
                        flag = res.get("flag").toString();
                        myScore = res.get("myScore").toString();
                        isLogin = res.get("isLogin").toString();

                        if(isLogin.equals("y")) {
                            tv_myScore.setText("可用积分："+myScore+"积分");
                            if (flag.equals("y")) {
                                submitbtn.setBackgroundColor(submitbtn.getResources().getColor(R.color.login_back_blue));
                                submitbtn.setTextColor(submitbtn.getResources().getColor(R.color.login_text_white));
                                submitbtn.setEnabled(true);
                            } else {
                                submitbtn.setBackgroundColor(submitbtn.getResources().getColor(R.color.login_back_gray));
                                submitbtn.setTextColor(submitbtn.getResources().getColor(R.color.login_text_gray));
                                submitbtn.setEnabled(false);
                            }
                        }
                        else{
                            tv_myScore.setText("可用积分：请登录查看");
                            submitbtn.setBackgroundColor(submitbtn.getResources().getColor(R.color.login_back_gray));
                            submitbtn.setTextColor(submitbtn.getResources().getColor(R.color.login_text_gray));
                            submitbtn.setEnabled(false);
                        }

                        XUtilsHelper.getInstance().bindCommonImage(iv_showImg, gift.getString("picurl"), true);

                        if(FormatUtil.isNoEmpty(gift.getString("gift"))){
                            tv_giftName.setText(gift.getString("gift"));
                        }
                        else{
                            tv_giftName.setText("未获取到礼品名称");
                        }
                        if(FormatUtil.isNoEmpty(gift.getString("giftNo"))) {
                            tv_giftNo.setText("礼品编号："+gift.getString("giftNo"));
                        }
                        else{
                            tv_giftNo.setText("礼品编号：暂无");
                        }
                        if(FormatUtil.isNoEmpty(gift.getString("giftIntegral")) && FormatUtil.toInteger(gift.getString("giftIntegral"))>0) {
                            tv_giftScore.setText("所需积分：" +gift.getString("giftIntegral")+"积分");
                        }
                        else{
                            tv_giftScore.setText("所需积分：9999积分");
                        }
                        if(FormatUtil.isNoEmpty(gift.getString("description"))){
                            tv_giftDesc.setText(gift.getString("description"));
                        }
                        else{
                            tv_giftDesc.setText("");
                        }
                        if(FormatUtil.isNoEmpty(gift.getString("quantity")) && FormatUtil.toDouble(gift.getString("quantity"))>0) {
                            tv_quantity.setText("库存：" + gift.getString("quantity")+gift.getString("unit"));
                            submitbtn.setBackgroundColor(submitbtn.getResources().getColor(R.color.login_back_blue));
                            submitbtn.setTextColor(submitbtn.getResources().getColor(R.color.login_text_white));
                            submitbtn.setEnabled(true);
                        }
                        else{
                            tv_quantity.setText("库存：0"+gift.getString("unit"));
                            submitbtn.setBackgroundColor(submitbtn.getResources().getColor(R.color.login_back_gray));
                            submitbtn.setTextColor(submitbtn.getResources().getColor(R.color.login_text_gray));
                            submitbtn.setEnabled(false);
                        }

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
        if(av_quantity.getAmount() <0 || !FormatUtil.isNoEmpty(av_quantity.getAmount())){
            CommonUtil.alter("请重新输入兑换数量！");return;
        }
        else {
            Intent i = new Intent(getApplicationContext(), GiftExchangeActivity.class);
            i.putExtra("id", id);
            i.putExtra("quantity", FormatUtil.toString(av_quantity.getAmount()));
            startActivity(i);
        }
    }

    @Event(R.id.top_back)
    private void top_backclick(View v){
        MyApplication.getInstance().finishActivity();
    }

}
