package com.example.administrator.helloworld;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.helloworld.common.TopNoLoginActivity;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.DensityUtil;
import com.example.administrator.helloworld.util.XUtilsHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends TopNoLoginActivity {

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

    @ViewInject(R.id.btn_VerifyCode)
    private Button btn_VerifyCode;
    @ViewInject(R.id.tv_ts)
    private TextView tv_ts;

    @ViewInject(R.id.et_mobile)
    private EditText et_mobile;
    @ViewInject(R.id.et_mobileCode)
    private EditText et_mobileCode;
    @ViewInject(R.id.et_parentCode)
    private EditText et_parentCode;

    private int utype = 0; //用户类型 0：企业，1：个人

    Handler handler;
    private int time = 120;

    /**
     * EditText有内容的个数
     */
    private int mEditTextHaveInputCount = 0;
    /**
     * EditText总个数
     */
    private final int EDITTEXT_AMOUNT = 4;
    /**
     * 编辑框监听器
     */
    private TextWatcher mTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("注册");
        progressDialog.hide();
        init();
        handler = new Handler();

        btn_VerifyCode.setOnTouchListener(new vcotl());
    }

    @Event(R.id.nextbtn)
    private void nextClick(View v){
        if(et_mobile.getText().length() != 11){
            CommonUtil.alter("手机号不正确！！");return;
        }
        if(et_mobileCode.getText().length() != 4){
            CommonUtil.alter("验证码不正确！！");return;
        }
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("mobile", et_mobile.getText().toString());
        maps.put("mobileCode", et_mobileCode.getText().toString());
        XUtilsHelper.getInstance().post("app/verifyMobileCode.htm", maps,new XUtilsHelper.XCallBack(){

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
                        Intent i = new Intent(getApplicationContext(),Register1Activity.class);
                        i.putExtra("mobile", et_mobile.getText().toString());
                        i.putExtra("utype", utype);
                        i.putExtra("parentCode", et_parentCode.getText().toString());
                        startActivity(i);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    private void sendData(){
        if( et_mobile.getText().length() != 11){
            CommonUtil.alter("手机号码不正确！！！");
            return;
        }
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("mobile", et_mobile.getText().toString());
        XUtilsHelper.getInstance().post("app/sendVerifyCodeToMobile.htm", maps,new XUtilsHelper.XCallBack(){

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
                        tv_ts.setVisibility(View.VISIBLE);
                        handler.postDelayed(runnable, 1000);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    class vcotl implements OnTouchListener {
        //类型(分类)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                sendData();
                return false;
            }
            return true;
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                if(time == 0){
                    handler.removeCallbacks(this);
                    btn_VerifyCode.setText("获取验证码");
                    btn_VerifyCode.setOnTouchListener(new vcotl());
                    time = 120;
                }
                else{
                    btn_VerifyCode.setText(time+"秒");
                    btn_VerifyCode.setOnTouchListener(null);
                    time--;
                    handler.postDelayed(this, 1000);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("NewApi")
    @Event(value={R.id.tab1,R.id.tab2},type=View.OnTouchListener.class)
    private boolean tabTouch(View v, MotionEvent e){
        if(e.getAction() == MotionEvent.ACTION_UP){
            RelativeLayout.LayoutParams lp1 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(8));
            lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            RelativeLayout.LayoutParams lp2 =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(1));
            lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if(v.getId() ==R.id.tab1 ){
                tab_line1.setBackground(CommonUtil.getDrawable(R.drawable.line_select));
                tab_line1.setLayoutParams(lp1);
                tab_line2.setBackgroundColor(0xFFb5b6b9);
                tab_line2.setLayoutParams(lp2);
                utype = 0;
            }else if(v.getId() ==R.id.tab2 ){
                tab_line2.setBackground(CommonUtil.getDrawable(R.drawable.line_select));
                tab_line2.setLayoutParams(lp1);
                tab_line1.setBackgroundColor(0xFFb5b6b9);
                tab_line1.setLayoutParams(lp2);
                utype=1;
            }
            return false;
        }
        return true;
    }

    private void init() {
        mTextWatcher = new TextWatcher() {
            /** 改变前*/
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(et_mobile.getText())) {
                    btn_VerifyCode.setBackgroundColor(btn_VerifyCode.getResources().getColor(R.color.verify_orange));
                    btn_VerifyCode.setTextColor(btn_VerifyCode.getResources().getColor(R.color.login_text_white));
                    btn_VerifyCode.setEnabled(true);
                }

                if (!(TextUtils.isEmpty(et_mobile.getText()) || TextUtils.isEmpty(et_mobileCode.getText()))) {
                    nextbtn.setBackgroundColor(nextbtn.getResources().getColor(R.color.login_back_blue));
                    nextbtn.setTextColor(nextbtn.getResources().getColor(R.color.login_text_white));
                    nextbtn.setEnabled(true);
                }
            }

            /** 内容改变*/
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                /** EditText内容改变之后 内容为空时 个数减一 按钮改为不可以的背景*/
                if (TextUtils.isEmpty(et_mobile.getText())) {
                    btn_VerifyCode.setBackgroundColor(btn_VerifyCode.getResources().getColor(R.color.login_back_gray));
                    btn_VerifyCode.setTextColor(btn_VerifyCode.getResources().getColor(R.color.login_text_gray));
                    btn_VerifyCode.setEnabled(false);
                }

                if (TextUtils.isEmpty(et_mobile.getText()) || TextUtils.isEmpty(et_mobileCode.getText())){
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
        et_mobile.addTextChangedListener(mTextWatcher);
        et_mobileCode.addTextChangedListener(mTextWatcher);

    }
}

