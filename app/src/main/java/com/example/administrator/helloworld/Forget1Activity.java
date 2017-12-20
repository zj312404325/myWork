package com.example.administrator.helloworld;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.helloworld.common.TopNoLoginActivity;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.XUtilsHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_forget1)
public class Forget1Activity extends TopNoLoginActivity {

    @ViewInject(R.id.submitbtn)
    private Button submitbtn;

    private String username;

    @ViewInject(R.id.et_newpass)
    private EditText et_newpass;
    @ViewInject(R.id.et_repass)
    private EditText et_repass;

    /**
     * EditText有内容的个数
     */
    private int mEditTextHaveInputCount = 0;
    /**
     * EditText总个数
     */
    private final int EDITTEXT_AMOUNT = 2;
    /**
     * 编辑框监听器
     */
    private TextWatcher mTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("重置密码");
        progressDialog.hide();
        init();
        Intent i = this.getIntent();
        username = i.getStringExtra("mobile");
    }

    @Event(R.id.submitbtn)
    private void submitClick(View v){

        if(et_newpass.getText().length() <6){
            CommonUtil.alter("密码必须大于6位！");return;
        }
        if(!et_newpass.getText().toString().equals(et_repass.getText().toString())){
            CommonUtil.alter("两次密码不相同");return;
        }
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("username", username);
        maps.put("newpass", et_newpass.getText().toString());
        maps.put("repass", et_repass.getText().toString());
        XUtilsHelper.getInstance().post("app/fogetPass.htm", maps,new XUtilsHelper.XCallBack(){

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
                        startActivity(new Intent(getApplicationContext(),Forget2Activity.class));
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
        et_newpass.addTextChangedListener(mTextWatcher);
        et_repass.addTextChangedListener(mTextWatcher);

    }

}

