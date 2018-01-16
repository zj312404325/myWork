package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.XUtilsHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_refundgoods_cancel)
public class RefundGoodsCancelActivity extends TopActivity {

    @ViewInject(R.id.tv_proName)
    private TextView tv_proName;

    @ViewInject(R.id.tv_salePrice)
    private TextView tv_salePrice;

    @ViewInject(R.id.tv_quantity)
    private TextView tv_quantity;

    @ViewInject(R.id.tv_info)
    private TextView tv_info;

    @ViewInject(R.id.tv_refundCancelDate)
    private TextView tv_refundCancelDate;

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

    private String cancelDate;
    private String refundid;

    private JSONObject order;
    private JSONObject orderdtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("申请退货");
        super.progressDialog.hide();
        Intent i = this.getIntent();
        cancelDate = i.getStringExtra("cancelDate");
        refundid = i.getStringExtra("refundId");
        initData();
    }

    private void initData(){
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("cancelDate", cancelDate);
        maps.put("refundid", refundid);

        XUtilsHelper.getInstance().post("app/refundMoneyCancel.htm", maps,new XUtilsHelper.XCallBack(){

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

                    tv_refundCancelDate.setText("退货取消时间："+cancelDate);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }
}
