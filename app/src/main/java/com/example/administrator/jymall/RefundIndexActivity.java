package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.XUtilsHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_refund_index)
public class RefundIndexActivity extends TopActivity {
    @ViewInject(R.id.tv_proName)
    private TextView tv_proName;

    @ViewInject(R.id.tv_salePrice)
    private TextView tv_salePrice;

    @ViewInject(R.id.tv_quantity)
    private TextView tv_quantity;

    @ViewInject(R.id.tv_info)
    private TextView tv_info;

    @ViewInject(R.id.img_proImgPath)
    private ImageView img_proImgPath;

    private String orderid;
    private String orderdtlid;
    private JSONObject order;
    private JSONObject orderdtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("选择服务类型");
        progressDialog.hide();
        Intent i = this.getIntent();
        orderid = i.getStringExtra("orderId");
        orderdtlid = i.getStringExtra("orderDtlId");
        initData();
    }

    @Event(value=R.id.rl_refundMoney,type=View.OnTouchListener.class)
    private boolean refundMoneyTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            Intent i =  new Intent(getApplicationContext(), RefundMoneyOneActivity.class);
            i.putExtra("orderId", orderid);
            i.putExtra("orderDtlId", orderdtlid);
            startActivity(i);
            return false;
        }
        return true;
    }

    @Event(value=R.id.rl_refundGoods,type=View.OnTouchListener.class)
    private boolean refundGoodsTouch(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            Intent i =  new Intent(getApplicationContext(), RefundGoodsOneActivity.class);
            i.putExtra("orderId", orderid);
            i.putExtra("orderDtlId", orderdtlid);
            startActivity(i);
            return false;
        }
        return true;
    }

    private void initData(){
        progressDialog.show();

        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("orderId", orderid);
        maps.put("orderDtlId", orderdtlid);
        XUtilsHelper.getInstance().post("app/refundIndex.htm", maps,new XUtilsHelper.XCallBack(){

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

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }
}
