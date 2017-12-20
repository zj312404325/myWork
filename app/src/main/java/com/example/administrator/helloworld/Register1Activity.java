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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.helloworld.common.TopNoLoginActivity;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.DensityUtil;
import com.example.administrator.helloworld.util.ValidationUtil;
import com.example.administrator.helloworld.util.XUtilsHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_register1)
public class Register1Activity extends TopNoLoginActivity {

    @ViewInject(R.id.tab1)
    private RelativeLayout tab1;
    @ViewInject(R.id.tab2)
    private RelativeLayout tab2;

    @ViewInject(R.id.tab_txt1)
    private TextView tab_txt1;
    @ViewInject(R.id.tab_txt2)
    private TextView tab_txt2;

    @ViewInject(R.id.tab_line1)
    private LinearLayout tab_line1;
    @ViewInject(R.id.tab_line2)
    private LinearLayout tab_line2;


    @ViewInject(R.id.nextbtn)
    private Button nextbtn;

    private String mobile;
    private int utype;
    private String parentCode;

    @ViewInject(R.id.tv_username)
    private TextView tv_username;
    @ViewInject(R.id.et_password)
    private EditText et_password;
    @ViewInject(R.id.et_repassword)
    private EditText et_repassword;
    @ViewInject(R.id.et_realname)
    private EditText et_realname;
    @ViewInject(R.id.et_comp)
    private EditText et_comp;
    @ViewInject(R.id.tv_comp)
    private TextView tv_comp;
    @ViewInject(R.id.et_mobile)
    private EditText et_mobile;
    @ViewInject(R.id.et_email)
    private EditText et_email;

    /**
     * EditText有内容的个数
     */
    private int mEditTextHaveInputCount = 0;
    /**
     * EditText总个数
     */
    private final int EDITTEXT_AMOUNT = 6;
    /**
     * 编辑框监听器
     */
    private TextWatcher mTextWatcher;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        progressDialog.hide();
        init();
        Intent i = this.getIntent();
        mobile = i.getStringExtra("mobile");
        utype = i.getIntExtra("utype", 0);
        parentCode = i.getStringExtra("parentCode");

        tv_username.setText("用户名"+mobile);
        et_mobile.setText(mobile);


        RelativeLayout.LayoutParams lp1 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(8));
        lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        RelativeLayout.LayoutParams lp2 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(1));
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if(utype == 0 ){
            tab_line1.setBackground(CommonUtil.getDrawable(R.drawable.line_select));
            tab_line1.setLayoutParams(lp1);
            tab_line2.setBackgroundColor(0xFFb5b6b9);
            tab_line2.setLayoutParams(lp2);
        }else {
            tab_line2.setBackground(CommonUtil.getDrawable(R.drawable.line_select));
            tab_line2.setLayoutParams(lp1);
            tab_line1.setBackgroundColor(0xFFb5b6b9);
            tab_line1.setLayoutParams(lp2);
            et_comp.setText(mobile);
            et_comp.setVisibility(View.GONE);
            tv_comp.setVisibility(View.GONE);

        }

    }



    @Event(R.id.nextbtn)
    private void nextClick(View v){
        if(et_password.getText().toString().length() <6){
            CommonUtil.alter("密码必须大于6位！");return;
        }
        if(!et_password.getText().toString().equals(et_repassword.getText().toString())){
            CommonUtil.alter("两个密码不相同");return;
        }
        if(et_realname.getText().toString().length() <1){
            CommonUtil.alter("真实姓名不能为空！");return;
        }
        if( ValidationUtil.textValidation(et_realname.getText().toString())){
            CommonUtil.alter("真实姓名不允许输入特殊符号！");return;
        }
        if(et_comp.getText().length() <1){
            CommonUtil.alter("公司名称不能为空！");return;
        }
        if(et_mobile.getText().toString().length() != 11){
            CommonUtil.alter("手机号不正确！！");return;
        }
        if(et_email.getText().toString().length() <1){
            CommonUtil.alter("邮箱不能为空！");return;
        }
        if(!ValidationUtil.emailValidation(et_email.getText().toString())){
            CommonUtil.alter("邮箱格式不正确！");return;
        }
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("mobile", et_mobile.getText().toString());
        maps.put("username", mobile);
        maps.put("comp", et_comp.getText().toString());
        maps.put("mobile", et_mobile.getText().toString());
        maps.put("password", et_password.getText().toString());
        maps.put("realname", et_realname.getText().toString());
        maps.put("email", et_email.getText().toString());
        maps.put("utype",""+ utype);
        maps.put("parentCode", parentCode);

        XUtilsHelper.getInstance().post("app/saveUser.htm", maps,new XUtilsHelper.XCallBack(){

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
                        Intent i = new Intent(getApplicationContext(),Register2Activity.class);
                        i.putExtra("mobile", et_mobile.getText().toString());
                        i.putExtra("utype", utype);
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
                        nextbtn.setBackgroundColor(nextbtn.getResources().getColor(R.color.login_back_blue));
                        nextbtn.setTextColor(nextbtn.getResources().getColor(R.color.login_text_white));
                        nextbtn.setEnabled(true);
                    }
                }
            }

            /** 内容改变*/
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                /** EditText内容改变之后 内容为空时 个数减一 按钮改为不可以的背景*/
                if (TextUtils.isEmpty(charSequence)) {
                    mEditTextHaveInputCount--;
                    nextbtn.setBackgroundColor(nextbtn.getResources().getColor(R.color.login_back_gray));
                    nextbtn.setTextColor(nextbtn.getResources().getColor(R.color.login_text_gray));
                    nextbtn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        /** 需要监听的EditText add*/
        et_password.addTextChangedListener(mTextWatcher);
        et_repassword.addTextChangedListener(mTextWatcher);
        et_realname.addTextChangedListener(mTextWatcher);
        et_comp.addTextChangedListener(mTextWatcher);
        et_mobile.addTextChangedListener(mTextWatcher);
        et_email.addTextChangedListener(mTextWatcher);

    }
}
