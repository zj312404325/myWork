package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.AmountView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/28.
 */

@ContentView(R.layout.activity_gift_exchange)
public class GiftExchangeActivity extends TopActivity{
    @ViewInject(R.id.submitbtn)
    private Button submitbtn;
    @ViewInject(R.id.et_contact)
    private EditText et_contact;
    @ViewInject(R.id.et_mobile)
    private EditText et_mobile;
    @ViewInject(R.id.et_address)
    private EditText et_address;
    @ViewInject(R.id.av_quantity)
    private AmountView av_quantity;

    private String id;
    private String quantity;

    /**
     * EditText有内容的个数
     */
    private int mEditTextHaveInputCount = 0;
    /**
     * EditText总个数
     */
    private final int EDITTEXT_AMOUNT = 3;
    /**
     * 编辑框监听器
     */
    private TextWatcher mTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("填写收货信息");
        progressDialog.hide();
        Intent i = this.getIntent();
        id = i.getStringExtra("id");
        quantity = i.getStringExtra("quantity");
        init();
    }

    @Event(R.id.submitbtn)
    private void submitclick(View v){
        if(et_contact.getText().toString().length() <1){
            CommonUtil.alter("联系人不能为空！");return;
        }
        if(et_mobile.getText().toString().length() <1){
            CommonUtil.alter("联系电话不能为空！");return;
        }
        if(et_address.getText().toString().length() <1){
            CommonUtil.alter("收件地址不能为空！");return;
        }
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("contact", et_contact.getText().toString());
        maps.put("mobile", et_mobile.getText().toString());
        maps.put("addr", et_address.getText().toString());
        maps.put("giftid", id);
        maps.put("quantity", quantity);
        XUtilsHelper.getInstance().post("app/editMallIntegral.htm", maps,new XUtilsHelper.XCallBack(){

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
                        CommonUtil.alter(res.get("msg").toString());
                        Intent i = new Intent(getApplicationContext(), MallIntegralActivity.class);
                        startActivity(i);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }

    private void init() {
        mTextWatcher = new TextWatcher() {
            /** 改变前*/
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                /** EditText最初内容为空 改变EditText内容时 个数加一*/
                if (TextUtils.isEmpty(charSequence)) {
                    mEditTextHaveInputCount++;
                    /** 判断个数是否到达要求*/
                    if (mEditTextHaveInputCount == EDITTEXT_AMOUNT) {
                        submitbtn.setBackgroundColor(submitbtn.getResources().getColor(R.color.login_back_blue));
                        submitbtn.setTextColor(submitbtn.getResources().getColor(R.color.login_text_white));
                        submitbtn.setEnabled(true);
                    }
                }
            }

            /** 内容改变*/
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                /** EditText内容改变之后 内容为空时 个数减一 按钮改为不可以的背景*/
                if (TextUtils.isEmpty(charSequence)) {
                    mEditTextHaveInputCount--;
                    submitbtn.setBackgroundColor(submitbtn.getResources().getColor(R.color.login_back_gray));
                    submitbtn.setTextColor(submitbtn.getResources().getColor(R.color.login_text_gray));
                    submitbtn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        /** 需要监听的EditText add*/
        et_contact.addTextChangedListener(mTextWatcher);
        et_mobile.addTextChangedListener(mTextWatcher);
        et_address.addTextChangedListener(mTextWatcher);

    }
}
