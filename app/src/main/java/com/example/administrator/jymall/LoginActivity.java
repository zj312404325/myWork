package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.administrator.jymall.common.BaseActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.UpdateApp;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyEditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.input_user)
    private MyEditText input_user;

    @ViewInject(R.id.input_pwd)
    private EditText input_pwd;

    @ViewInject(R.id.input_pwd_b)
    private LinearLayout input_pwd_b;

    @ViewInject(R.id.loginbtn)
    private Button loginbtn;

    @ViewInject(R.id.registerbtn)
    private Button registerbtn;

    @ViewInject(R.id.forgetBtn)
    private Button forgetBtn;

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
        new UpdateApp("app/versionCheck.htm",this);
        progressDialog.hide();
        init();
    }

    @SuppressLint("NewApi")
    @Event(value=R.id.input_pwd,type=View.OnFocusChangeListener.class)
    private void onFocusChangePwd(View arg0, boolean arg1){
        if(arg1){
            //input_pwd_b.setBackground(CommonUtil.getDrawable(R.drawable.input_b));
        }
        else{
            //input_pwd_b.setBackground(CommonUtil.getDrawable(R.drawable.input_g));
        }
    }

    @Event(value=R.id.loginbtn)
    private void LogbtnClick(View v){
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("login_name", input_user.getText());
        maps.put("password", input_pwd.getText().toString());
        XUtilsHelper.getInstance().post("app/loing.htm", maps,new XUtilsHelper.XCallBack(){

            @Override
            public void onResponse(String result)  {
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    if(res.get("d").equals("y")){
                        setUser(res.get("data").toString());
                        CommonUtil.alter(res.get("msg").toString());
                        //原跳转
                        startActivity(new Intent(getApplicationContext(),IndexActivity.class));

                        //新跳转
                        /*LoginCarrier invoker = (LoginCarrier) getIntent().getParcelableExtra("CONFIG_INVOKER");
                        invoker.invoke(LoginActivity.this);
                        finish();*/

                        //startActivity();
                    }
                    else{
                        CommonUtil.alter(res.get("msg").toString());
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    @Event(value=R.id.registerbtn)
    private void RegClick(View v){
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
    }

    @Event(value=R.id.forgetBtn)
    private void forgetBtnClick(View v){
        startActivity(new Intent(getApplicationContext(),ForgetActivity.class));
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
                    if (mEditTextHaveInputCount == EDITTEXT_AMOUNT)
                        //registerbtn.setBackgroundResource(R.drawable.login_btn_longOn);
                        loginbtn.setBackgroundColor(loginbtn.getResources().getColor(R.color.login_back_blue));
                        loginbtn.setTextColor(loginbtn.getResources().getColor(R.color.login_text_white));
                        loginbtn.setEnabled(true);

                }
            }

            /** 内容改变*/
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                /** EditText内容改变之后 内容为空时 个数减一 按钮改为不可以的背景*/
                if (TextUtils.isEmpty(charSequence)) {
                    mEditTextHaveInputCount--;
                    //registerbtn.setBackgroundResource(R.drawable.login_btn_longOn);
                    loginbtn.setBackgroundColor(loginbtn.getResources().getColor(R.color.login_back_gray));
                    loginbtn.setTextColor(loginbtn.getResources().getColor(R.color.login_text_gray));
                    loginbtn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        /** 需要监听的EditText add*/
        input_user.getEditeText().addTextChangedListener(mTextWatcher);
        input_pwd.addTextChangedListener(mTextWatcher);

    }

    private void startActivity() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("className") != null) {
            String className = getIntent().getExtras().getString("className");
            getIntent().removeExtra("className");
            if (className != null && !className.equals(getApplicationContext().getClass().getName())) {
                try {
                    ComponentName componentName = new ComponentName(getApplicationContext(), Class.forName(className));
                    startActivity(getIntent().setComponent(componentName));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            startActivity(new Intent(getApplicationContext(),IndexActivity.class));
        }
    }

}
