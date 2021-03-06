package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

@ContentView(R.layout.activity_offline_pay)
public class OfflinePayActivity extends TopActivity {

    @ViewInject(R.id.et_password)
    private EditText et_password;

    @ViewInject(R.id.btn_offlinePay)
    private Button btn_offlinePay;

    String orderNo = "";
    String id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("线下支付");
        super.progressDialog.hide();

        Intent i = this.getIntent();
        orderNo = i.getStringExtra("orderNo");
        id = i.getStringExtra("id");
    }


    @Event(R.id.btn_offlinePay)
    private void offlinePay(View v){
        if(et_password.getText().length()<1){
            CommonUtil.alter("密码不能为空！");
            return ;
        }
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("orderId", id);
        maps.put("orderNo", orderNo);
        maps.put("orderType", "mall");
        maps.put("password", et_password.getText().toString());
        XUtilsHelper.getInstance().post("app/doOfflinePay.htm", maps,new XUtilsHelper.XCallBack(){

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
