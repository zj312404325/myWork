package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopNoLoginActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.DensityUtil;
import com.example.administrator.jymall.util.XUtilsHelper;

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

    @ViewInject(R.id.cb_agree_tip)
    private CheckBox cb_agree_tip;

    @ViewInject(R.id.cb_agree_rule)
    private CheckBox cb_agree_rule;

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
        showAgreement();
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
        if(!cb_agree_tip.isChecked()){
            CommonUtil.alter("请阅读并同意《温馨提示》！");return;
        }
        if(!cb_agree_rule.isChecked()){
            CommonUtil.alter("请阅读并同意《用户注册与账户管理协议》！");return;
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

    @Event(R.id.tv_agreement)
    private void agreementClick(View v){
        showAgreement();
    }

    @Event(R.id.tv_rule)
    private void ruleClick(View v){
        showRule();
    }

    private void showAgreement(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("（工业超市）温馨提示")
                .setMessage("您在申请注册流程中点击同意前，应当认真阅读以下协议。请您务必审慎阅读、充分理解协议中相关条款内容，其中包括：限制责任的条款、法律适用和管辖的条款、其他以粗体下划线标识的重要条款。\n" +
                        "\n" +
                        "如您对协议有任何疑问，可向平台客服咨询。\n" +
                        "\n" +
                        "【特别提示】当您按照注册页面提示填写信息、阅读并同意协议且完成全部注册程序后，即表示您已充分阅读、理解并接受协议的全部内容。如您因工平台服务与金赢工业超市网发生争议的，适用金赢工业超市网的平台规则处理。\n" +
                        "\n" +
                        "阅读协议的过程中，如果您不同意相关协议或其中任何条款约定，您应立即停止注册程序。\n"+
                        "\n" +
                        "《法律声明与隐私政策》\n" +
                        "\n" +
                        "您注册使用金赢工业超市网，为尽力保护您的个人信息安全。金赢工业超市网服务提供者（以下简称“我们”）制定本《法律声明与隐私权政策》（下称“本政策”）。\n" +
                        "\n" +
                        "在使用金赢工业超市网各项产品或服务前，请您务必仔细阅读并透彻理解本政策，在确认充分理解并同意后使用相关产品或服务。一旦您开始使用金赢工业超市网各项产品或服务，即表示您已充分理解并同意本政策。如对本政策内容有任何疑问、意见或建议，您可与我们联系。\n" +
                        "\n" +
                        "一、定义\n" +
                        "\n" +
                        "金赢工业超市网：指金赢工业超市网（域名为 mall.jinying365.com）网站及金赢工业超市网客户端。\n" +
                        "\n" +
                        "金赢工业超市网服务提供者：指苏州金赢网电子商务有限公司。\n" +
                        "\n" +
                        "个人信息：以电子或者其他方式记录的能够单独或者与其他信息结合识别特定自然人身份或者反映特定自然人活动情况的各种信息。\n" +
                        "\n" +
                        "二、法律声明\n" +
                        "\n" +
                        "2.1权利归属\n" +
                        "\n" +
                        "除非金赢工业超市网另行声明，金赢工业超市网内的所有产品、技术、软件、程序、数据及其他信息（包括文字、图标、图片、照片、音频、视频、图表、色彩组合、版面设计等）的所有权利（包括版权、商标权、专利权、商业秘密及其他相关权利）均归金赢工业超市网服务提供者所有。未经金赢工业超市网服务提供者许可，任何人不得以包括通过机器人、蜘蛛等程序或设备监视、复制、传播、展示、镜像、上载、下载等方式擅自使用金赢工业超市网内的任何内容。\n" +
                        "\n" +
                        "金赢工业超市网的 “金赢工业超市网”等文字及/或标识，以及金赢工业超市网的其他标识、徽记、产品和服务名称均为金赢工业超市网服务提供者所有，如有宣传、展示等任何使用需要，您必须取得金赢工业超市网服务提供者事先书面授权。\n" +
                        "\n" +
                        "2.2 责任限制\n" +
                        "\n" +
                        "2.2.1 您在金赢工业超市网页面看到商品的单价并非实际价格，因市场变化或网站滞后性，故您在金赢工业超市网购买商品时的单价以您结算时订单的价格为准。\n" +
                        "\n" +
                        "2.2.2 用户所持账号在平台上产生的订单都是由用户自行提供并创建，包括订单上的信息（包括但不仅限于商品信息、收货地址、发票信息、联系人等）都是由用户自行提供并创建，由用户对其提供并上传的信息承担相应法律责任。\n"+
                        "\n" +
                        "2.3知识产权\n" +
                        "\n" +
                        "2.3.1我们所提供的软件、客户端以及相应涉及的其他服务，均享有知识产权，受法律保护。任何人未经金赢工业超市网服务提供者书面同意，不得以任何理由或方式上述软件、服务、信息、文字的任何部分进行使用、复制、修改、传播，或以连接或传送、存储于信息检索系统或其他商业目的的使用，但对于非商业目的、个人使用的下载或打印（未作修改，且注明版权所有者的说明）除外。\n" +
                        "\n" +
                        "2.3.2 我们尊重知识产权，反对并打击侵犯知识产权的行为。知识产权权利人若认为金赢工业超市网内容（包括但不限于金赢工业超市网用户发布的商品信息）侵犯其合法权益的，可以通过0512-52686666进行投诉，我们将在收到知识产权权利人合格通知后依据相应的法律法规以及平台规则及时处理。\n" +
                        "\n" +
                        "三、隐私政策\n" +
                        "\n" +
                        "3.1信息的收集范围\n" +
                        "\n" +
                        "3.1.1您授权我们收集您的以下个人信息：\n" +
                        "\n" +
                        "身份识别信息，包括但不限于你的姓名、身份证明、联系地址、电话号码信息，或您的公司名称、地址、营业执照及组织代码证；\n" +
                        "\n" +
                        "支付信息，包括但不限于您的支付时间、支付金额、支付工具、银行账户及支付账户信息；\n" +
                        "\n" +
                        "交易信息，包括但不限于邮箱、订单信息、信用积分、评价内容；、\n" +
                        "\n" +
                        "其他根据金赢工业超市网具体产品及服务的需要而收集的您的个人信息，包括但不限于您对我们及我们产品或服务的意见、建议、您曾经使用或经常使用的记录和使用习惯等信息。\n" +
                        "\n" +
                        "3.2 信息的用途\n" +
                        "\n" +
                        "3.2.1 您授权我们处于以下用途使用您的个人信息：\n" +
                        "\n" +
                        "向您提供金赢工业超市网的产品及服务，并进行金赢工业超市网相关网站及客户端的管理及优化；\n" +
                        "\n" +
                        "提升和改善金赢工业超市网现有产品及服务的功能和质量，包括但不限于产品及服务内容的更新；\n" +
                        "\n" +
                        "开展金赢工业超市网产品及服务相关的市场活动，向您推送最新的市场活动信息及优惠方案；\n" +
                        "\n" +
                        "设计、开发、推广全新的产品及服务；\n" +
                        "\n" +
                        "提高金赢工业超市网产品及服务的安全性；\n" +
                        "\n" +
                        "其他与上述用途有关的用途。\n" +
                        "\n" +
                        "3.3 信息的安全及保护\n" +
                        "\n" +
                        "3.3.1我们努力采取各种合理的物理、电子和管理方面的安全措施来保护您的信息，使您的信息不会被泄漏、毁损或丢失，包括但不限于信息加密存储、数据中心的访问控制。我们对可能接触到您的信息的员工或外包人员也采取了严格管理，包括但不限于根据岗位的不同采取不同的权限控制，与他们签署保密协议，监控他们的操作情况等措施。我们会按现有技术提供相应的安全措施来保护您的信息，提供合理的安全保障，我们将尽力做到使您的信息不被泄漏、毁损或丢失。\n" +
                        "\n" +
                        "3.3.2您的账户均有安全保护功能，请妥善保管您的账户及密码信息。我们将通过向其它服务器备份、对用户密码进行加密等安全措施确保您的信息不丢失，不被滥用和变造。尽管有前述安全措施，但同时也请您理解在信息网络上不存在“完善的安全措施”。\n" +
                        "\n" +
                        "3.3.3 如果我们监测到您将金赢工业超市网的产品及服务以及相关信息用于欺诈或非法目的，我们将会采取相应措施，包括但不限于中止或终止向您提供任何产品或服务。\n" +
                        "\n" +
                        "四、变更\n" +
                        "\n" +
                        "随着网络平台信息服务的进一步提升，本政策的内容会随时更新。更新后的本政策一旦在平台上公告即有效代替原来的政策。我们鼓励您定期查看本政策的内容以及了解我们对隐私保护的最新措施\n"+
                        "\n" +
                        "《工业超市规则》\n" +
                                "当您点击“同意”按钮注册为金赢工业超市网会员或使用金赢工业超市网（以下简称“平台”）时，也代表你签署并认可了该份规则，您在后续的使用过程中必须遵守该规则。\n" +
                                "\n" +
                                "一、总则\n" +
                                "\n" +
                                "1.1.为了规范平台交易，促进买卖双方交易的顺利进行，根据公平、公正、平等的原则，制定本规则。\n" +
                                "\n" +
                                "1.2本规则适用于在平台里进行的商品交易活动，平台的工作人员、会员、仓库在进行货物交易、结算、货物交收时必须遵守本规则。\n" +
                                "\n" +
                                "1.3.金赢工业超市平台是苏州金赢网电子商务有限公司自营的网上工业品交易超市，域名：mall.jinying365.com,是区别与其他的金赢网交易网站，其所提供的商品均为苏州金赢网电子商务有限公司所有。\n" +
                                "\n" +
                                "1.4.您确认，在您完成注册程序或者使用平台服务时，您应当是具备完全民事权利能力和与所从事的民事行为相适应的行为能力的自然人、法人或其他组织。若您不具备前述主体资格，请勿使用平台的服务，否则您及您的监护人应承担因此而导致的一切后果，且平台有权注销（永久冻结）您的账户，并向您及您的监护人索偿。如您代表一家公司或其他法律主体在平台注册或在金赢网注册，则您声明和保证，您有权使该公司或该法律主体受本协议“条款”的约束。\n" +
                                "\n" +
                                "1.5您明确同意，平台有权根据法律法规及运营需要，不时对本协议和条款进行修改并公布在平台，该等协议和条款已经公布，立即生效，平台无须另行通知。应主动不时查阅或注意平台的公告，若不同意平台的修改，应立即停止使用平台。若继续使用平台的服务，则视为接受平台对本协议和条款的修改。\n" +
                                "\n" +
                                "1.6您在金赢网注册成为金赢网会员时，即可在平台里进行采购，也可在金赢网平台使用，且均认可其在平台上产生的订单是真实、有效且具有法律效力的。\n" +
                                "\n" +
                                "1.7 平台客服热线：0512-52686666，客服邮箱：kf@jinying365.com。\n" +
                                "\n" +
                                "二、须知\n" +
                                "\n" +
                                "2.1收货人\n" +
                                "\n" +
                                "您需填写真实有效的收货地址，如因您填写的收货地址有误而导致货物无法准确及时送达而产生的纠纷由该自行承担负责。\n" +
                                "\n" +
                                "2.2发票信息\n" +
                                "\n" +
                                "您需填写真实有效的发票信息，如因您填写的发票信息有误而导致发票开错，由您自行承担责任。\n" +
                                "\n" +
                                "2.3银行账户管理\n" +
                                "\n" +
                                "您需填写真实有效的银行账户信息，如因您填写的银行账户信息有误而导致发票开错，由您自行承担责任。\n" +
                                "\n" +
                                "2.4合同模板\n" +
                                "\n" +
                                "苏州金赢网电子商务有限公司在合同上盖的电子印章都视同为与盖实体章相同的法律效力。\n" +
                                "\n" +
                                "2.5 合同扫描件\n" +
                                "\n" +
                                "您在平台里下载的合同模板填写您的信息后，打印出来签名并盖公章后扫描，经您签名盖章确认的扫描件视同原件。\n" +
                                "\n" +
                                "2.6 评价\n" +
                                "\n" +
                                "您应该在尊重事实的基础上，遵循公平、公正、平等的原则上理性发表评论，否则经平台调查该评论与事实严重不符，则平台有权要求您修改，并在平台上对您的行为进行公告，平台有权视情节严重的作出加入黑名单或注销您的账户。\n" +
                                "\n" +
                                "三、交易流程\n" +
                                "\n" +
                                "3.1.订单\n" +
                                "\n" +
                                "3.1.1平台发布商品信息，商品信息包括但不限于以下内容：品名、规格、价格、数量、重量、品牌等。\n" +
                                "\n" +
                                "3.1.2买方须先下载模板填写采购需求，并上传采购需求，平台筛选商品生成订单，符合买方采购需求的，则买方同意生成的订单；若买方不同意，则订单取消，不视为违约。\n" +
                                "\n" +
                                "3.1.3若买方需要定制化的商品，买方上传采购需求（含图纸），平台自收到采购需求之日起四个工作日内回复是否生成订单；若平台已生成订单，则由买方确认是否同意生成的订单；若平台未生成订单，则采购需求取消，不视为平台的违约。\n" +
                                "\n" +
                                "3.2.付款\n" +
                                "\n" +
                                "3.2.1买方确认生成的订单后，买方须在规定时间内支付足额的订单货款（定制化的，则按照订单款的30%支付订金，剩余货款须在卖方催促付款时付清）。\n" +
                                "\n" +
                                "3.2.2买方由两种付款方式选择；①现结，含线上网银支付和线下网银支付；②信用支付，可月结或者季度结算。详见如何支付。\n" +
                                "\n" +
                                "3.3发货\n" +
                                "\n" +
                                "卖方收到订单款后（信用支付除外）按照双方约定或者承诺的时间送货。若库存有现货的，自收到订单款两日内发货；若库存没有现货，则发货时间以买卖双方约定为准。\n" +
                                "\n" +
                                "3.4.运输方式\n" +
                                "\n" +
                                "平台直送或者通过快递公司或物流公司送货。（详见配送方式）\n" +
                                "\n" +
                                "3.5 商品质量\n" +
                                "\n" +
                                "超市平台订单中所涉及的商品质量标准，若无特别规定，按商品出厂标准执行，若没有出厂标准，则按照国家标准执行。\n" +
                                "\n" +
                                "3.6. 商品签收\n" +
                                "\n" +
                                "3.6.1收货人必须是实名认证的买方或者经过买方指定的收货人。否则卖方有权拒绝交货，其所产生的法律后果由买方承担。\n" +
                                "\n" +
                                "3.6.2 收到商品时，请确认商品正确（包括商品名、品牌、型号、数量、包装、外观等）、无损坏后签收，如商品外箱贴有易碎标识。请务必当场开箱检查箱内商品是否有破损。如外包装有明显损坏迹象，可及时通知平台，并拒绝签收，平台会处理并承担由此而产生的运输费用。\n" +
                                "\n" +
                                "若买方已经完好签收，则平台不再受理关于此商品有误或有损坏的投诉。\n" +
                                "\n" +
                                "3.6.3买方签收商品后，须在平台中确认收货状态。否则一般以订单对应的快递或物流公司的签收信息作为买方收货依据。若买方不确认收货，也不提出退换货要求的，订单发货7天后默认为已收货状态。\n" +
                                "\n" +
                                "3.7 价格\n" +
                                "\n" +
                                "平台里所有报价即成交价均为该商品的含税价格。\n" +
                                "\n" +
                                "四、售后服务/品质承诺\n" +
                                "\n" +
                                "4.1不适用无质量异议的退换货情形\n" +
                                "\n" +
                                "4.1.1定制件信息填写错误的。\n" +
                                "\n" +
                                "4.1.2影响二次销售的。\n" +
                                "\n" +
                                "4.1.3人为损害的商品。\n" +
                                "\n" +
                                "4.1.4加工定制件。\n" +
                                "\n" +
                                "4.1.5 7天默认收货的。\n" +
                                "\n" +
                                "4.2. 7天无理由退换货原则（标准件）\n" +
                                "\n" +
                                "（详见7天无理由退换货原则（标准件））\n" +
                                "\n" +
                                "4.3 有质量问题的退、换货原则\n" +
                                "\n" +
                                "被卖方确认或者经有资质的鉴定机构鉴定为性能故障的质量问题，则卖方根据买方的申请，进行退货或换货处理。\n" +
                                "\n" +
                                "五、违约责任\n" +
                                "\n" +
                                "5.1 买方连续超过三次或三次以上提交订单而不支付货款或被平台认定是恶意提交订单而不支付货款的，平台将会暂停买方在平台的交易权限。须其以书面申请并经平台审核通过后方可重新开通。\n" +
                                "\n" +
                                "5.2 买方（定制化）逾期付款，逾期付款 15 日内，每日按照订单价款的千分之五计算滞纳金，超过15日未付款的，视为取消订单，已收取的订金不予退还，且有权要求买方支付滞纳金。\n" +
                                "\n" +
                                "5.3 （信用支付）买方付款逾期一天，则超市平台将冻结买方的账户，已生成订单未发货的，暂停发货，且卖方有权每日按照订单总额的千分之五收取滞纳金，若造成卖方损失的，包括但不限于滞纳金、赔偿金、诉讼费、律师费等均由买方承担。\n" +
                                "\n" +
                                "六、其他\n" +
                                "\n" +
                                "6.1 若针对同一行为或情形，本规则与金赢网平台发布的其他规则约定不一致的，以本规则为准。\n" +
                                "\n" +
                                "6.2如您就本规则或因执行本规则交易过程中产生争议，双方应协商解决，解决不成的，任一方均可向苏州金赢网电子商务有限公司注册地所在地法院起诉。\n" +
                                "\n" +
                                "6.3 本规则最终由苏州金赢网电子商务有限公司解释。")
                .setCancelable(false)
                .setPositiveButton("我已阅读", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        cb_agree_tip.setChecked(true);
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.login_back_blue));
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setText("我已阅读并同意");
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ffffff"));
    }

    private void showRule(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("用户注册与账户管理协议")
                .setMessage("当您点击“同意”按钮或注册为金赢网会员的同时，也代表你签署并认可了该份规则，您在后续的使用过程中必须遵守该规则。金赢网为保障金赢用户合法权益，维护金赢网正常经营秩序，根据国家相关法律、法规、政策，结合各行业交易的特性，制定金赢规则，所有会员在金赢网从事行业交易时视为已承诺同意接受金赢规则的所有条款，金赢网有权随时变更本规则并在网站上予以公告，若用户不同意相关变更，应立即停止金赢网提供的相关服务和产品，金赢网有权对用户行为及适用的规则进行单方认定。\n" +
                        "\n" +
                        "1 注册与账户\n" +
                        "\n" +
                        "2 平台使用规范\n" +
                        "\n" +
                        "3 协议终止\n" +
                        "\n" +
                        "4 附则\n" +
                        "\n" +
                        "本版《用户注册与账户管理协议》（以下简称“本协议”）是您与苏州金赢网电子商务有限公司就注册成为www.jinying365.com平台的用户所订立的契约。请用户仔细阅读本协议，对于协议中以加粗字体显示的内容，您应重点阅读。\n" +
                        "\n" +
                        "1 注册与账户\n" +
                        "\n" +
                        "1.1 金赢网平台（域名jinying365.com ）（以下简称“平台”）为苏州金赢网电子商务有限公司运营的、采用B2B形式的、为上下游企业提供工业产品交易服务的电子商务平台，平台的各项电子服务的所有权和运作权归苏州金赢网电子商务有限公司所有。\n" +
                        "\n" +
                        "1.2 非平台注册用户可以游客身份登录平台网址，仅能浏览权限允许内的信息，不能使用平台功能；平台注册用户可以浏览平台全部信息和使用部分平台功能，实名认证后能够使用平台全部功能。\n" +
                        "\n" +
                        "1.3 平台注册用户同时拥有买家资格和卖家资格。\n" +
                        "\n" +
                        "1.3.1 一个手机号和邮箱只能注册一个用户，用户可用注册手机号或者注册时留下的邮箱找回密码。\n" +
                        "\n" +
                        "1.3.2 注册企业账号时，请填写正确的公司名称，一旦确认，不能变更，公司名称需与营业执照上的名称一致，如果不一致，则无法通过实名认证。\n" +
                        "\n" +
                        "1.4 用户应当在使用平台服务之前认真阅读本协议全部内容，对于协议中以加粗字体显示的内容，用户应重点阅读。如用户对协议有任何疑问的，应向苏州金赢网电子商务有限公司咨询。但无论用户事实上是否在使用平台服务之前认真阅读了本协议内容，只要用户使用平台服务，则本协议即对用户产生约束，届时用户不应以未阅读本协议的内容或者未获得平台对用户问询的解答等理由，主张本协议无效，或要求撤销本协议。\n" +
                        "\n" +
                        "1.5 平台拥有平台网站各项电子服务的所有权和运作权。用户同意所有注册协议条款并完成注册程序，才能成为平台的正式用户。用户应自行诚信向平台提供注册资料，确保提供的注册资料真实、准确、完整、合法有效，用户注册资料如有变动的，应及时更新其注册资料。如果用户提供的注册资料不合法、不真实、不准确、不详尽的，用户需承担因此引起的相应责任及后果，并且平台保留终止用户使用平台各项服务的权利。\n" +
                        "\n" +
                        "1.6 用户点击“同意并继续”注册用户，登录的，即视为用户确认自己具有享受平台服务、从事平台产品交易等相应的权利能力和行为能力，能够独立承担法律责任。如果用户不同意本协议的约定，用户应立即停止注册程序或停止使用平台服务。\n" +
                        "\n" +
                        "1.7 用户注册成功后，您可以直接使用您设置或确认的金赢网账户名称和金赢网账户密码登陆金赢网网站。由于您的金赢网账户关联您的相关信息（包括企业信息、商业信息、交易信息），您的金赢网账户仅限您使用。未经本公司同意，您直接或间接授权第三方使用您金赢网账户或获取您账户项下信息的行为无效。如本公司判断您金赢网账户的使用可能危及您的账户安全和/或金赢网网站信息安全的，则可以拒绝提供相应服务。我们任何时候均不会主动要求您提供您的账户，因此，建议您务必保管好您的账户，保管好您的金赢网账户密码和登陆信息，并确保您在每个上网时段结束时退出登录并以正确步骤离开金赢网网站。账户因您主动泄露或遭受他人攻击、诈骗等行为导致的损失及后果，均由您自行承担。除金赢网存在过错外，您应对您账户项下的所有行为（包括但不限于在线签署各类协议、发布信息、披露信息等）负责。如果发现任何未经授权使用您账户登录金赢网网站或其他可能导致您账户遭窃、遗失的情况，建议您立即通知我们。您理解我们对您的任何请求采取行动均需要合理时间，除我们存在过错外，我们对因任何未经授权使用您账户登录金赢网网站或其他可能导致您账户遭窃、遗失的情况而产生的后果不承担任何责任。\n" +
                        "\n" +
                        "1.7.1 用户在平台上所上传的所有与本企业或本人相关的资料或信息都是由用户亲自提供并上传，由用户对其提供并上传的资料或信息承担相应法律责任。\n" +
                        "\n" +
                        "1.7.2 如需更改注册手机号、注册邮箱或账号、更改注册公司名称等，如是企业用户还需提供公司营业执照（盖公章扫描件，更换公司抬头的，前后公司营业执照都需要提供）、情况说明（盖公章扫描件）、账号、密码；个人用户提供身份证扫描件及情况说明、账号、密码，通过邮件发送到kf@jinying365.com。客服收到邮件后将审核处理。\n" +
                        "\n" +
                        "1.7.3 用户如遗失用户名或密码，请及时按照上述1.7.1条提供相应的信息进行提交平台进行修改申请。\n" +
                        "\n" +
                        "1.8 平台有权根据需要不时地制订、修改本协议及平台各项规则，制订、修改完成后，用户第一次登陆平台/第一次使用平台服务时，制订、修改的协议和规则将自动弹出并提示用户。如用户不同意相关变更，应当立即停止使用平台服务。用户继续使用平台服务的，即表示用户接受经修订的协议和规则。\n" +
                        "\n" +
                        "2 平台使用规范\n" +
                        "2.1.在苏州金赢网电子商务有限公司平台上使用上服务过程中，您承诺遵守以下约定：\n" +
                        "\n" +
                        "2.1.1 在使用苏州金赢网电子商务有限公司平台服务过程中实施的所有行为均遵守国家法律、法规等规范性文件及苏州金赢网电子商务有限公司平台各项规则的规定和要求，不违背社会公共利益或公共道德，不损害他人的合法权益，不违反本协议及相关规则。您如果违反前述承诺，产生任何法律后果的，您应以自己的名义独立承担所有的法律责任，并确保苏州金赢网电子商务有限公司免于因此产生任何损失。\n" +
                        "\n" +
                        "2.1.2 在交易过程中，遵守诚实信用原则，不采取不正当竞争行为，不扰乱电商交易的正常秩序，不从事与电商交易无关的行为。\n" +
                        "\n" +
                        "2.1.3 不对苏州金赢网电子商务有限公司平台上的任何数据作商业性利用，包括但不限于在未经苏州金赢网电子商务有限公司事先书面同意的情况下，以复制、传播等任何方式使用苏州金赢网电子商务有限公司平台各网站上展示的资料。\n" +
                        "\n" +
                        "2.1.4不使用任何装置、软件或例行程序干预或试图干预苏州金赢网电子商务有限公司平台的正常运作或正在苏州金赢网电子商务有限公司平台上进行的任何交易、活动。您不得采取任何将导致不合理的庞大数据负载加诸苏州金赢网电子商务有限公司平台网络设备的行动。 \n" +
                        "\n" +
                        "2.2.您了解并同意：\n" +
                        "\n" +
                        "2.2.1 苏州金赢网电子商务有限公司有权对您是否违反上述承诺做出单方认定，并根据单方认定结果适用规则予以处理或终止向您提供服务，且无须征得您的同意或提前通知予您。\n" +
                        "\n" +
                        "2.2.2 基于维护苏州金赢网电子商务有限公司平台交易秩序及交易安全的需要，苏州金赢网电子商务有限公司有权在发生恶意购买等扰乱市场正常交易秩序的情形下，执行关闭相应交易订单等操作。\n" +
                        "\n" +
                        "2.2.3 经国家行政或司法机关的生效法律文书确认您存在违法或侵权行为，或者苏州金赢网电子商务有限公司根据自身的判断，认为您的行为涉嫌违反本协议和/或规则的条款或涉嫌违反法律法规的规定的，则苏州金赢网电子商务有限公司有权在苏州金赢网电子商务有限公司平台上公示您的该等涉嫌违法或违约行为及苏州金赢网电子商务有限公司已对您采取的措施。\n" +
                        "\n" +
                        "2.2.4对于您在苏州金赢网电子商务有限公司平台上实施的行为，包括您未在苏州金赢网电子商务有限公司平台上实施但已经对苏州金赢网电子商务有限公司平台及其用户产生影响的行为，苏州金赢网电子商务有限公司有权单方认定您行为的性质及是否构成对本协议和/或规则的违反，并据此作出相应处罚。您应自行保存与您行为有关的全部证据，并应对无法提供充要证据而承担的不利后果。\n" +
                        "\n" +
                        "2.2.5 对于您涉嫌违反承诺的行为对任意第三方造成损害的，您均应当以自己的名义独立承担所有的法律责任，并应确保苏州金赢网电子商务有限公司免于因此产生损失或增加费用。\n" +
                        "\n" +
                        "2.2.6 如您涉嫌违反有关法律或者本协议之规定，使苏州金赢网电子商务有限公司遭受任何损失，或受到任何第三方的索赔，或受到任何行政管理部门的处罚，您应当赔偿苏州金赢网电子商务有限公司因此造成的损失及（或）发生的费用，包括合理的律师费用。\n" +
                        "\n" +
                        "2.2.7 本站上的用户发布的商品价格、数量、是否有货等商品信息随时都有可能发生变动，请与信息发布所属用户直接联系确认，本站不作特别通知。由于网站上商品信息的数量极其庞大，虽然本站会尽最大努力保证您所浏览商品信息的准确性，但由于众所周知的互联网技术因素等客观原因存在，本站网页显示的信息可能会有一定的滞后性或差错，对此情形您知悉并理解。\n" +
                        "\n" +
                        "3 协议终止\n" +
                        "\n" +
                        "3.1您同意，苏州金赢网电子商务有限公司有权自行全权决定以任何理由中止、终止向您提供部分或全部苏州金赢网电子商务有限公司平台服务，暂时冻结或永久冻结（注销）您的账户，且无须为此向您承担任何责任。\n" +
                        "\n" +
                        "3.2出现以下情况时，苏州金赢网电子商务有限公司有权直接以注销账户的方式终止本协议:\n" +
                        "\n" +
                        "3.2.1苏州金赢网电子商务有限公司终止向您提供服务后，您涉嫌再一次直接或间接或以他人名义注册为苏州金赢网电子商务有限公司用户的；\n" +
                        "\n" +
                        "3.2.2您注册信息中的主要内容不真实或不准确或不及时或不完整；\n" +
                        "\n" +
                        "3.2.3本协议变更时，您明示并通知苏州金赢网电子商务有限公司不愿接受新的服务协议的；\n" +
                        "\n" +
                        "3.2.4其它苏州金赢网电子商务有限公司认为应当终止服务的情况。\n" +
                        "\n" +
                        "3.3您有权向苏州金赢网电子商务有限公司要求注销您的账户，经苏州金赢网电子商务有限公司审核同意的，苏州金赢网电子商务有限公司注销（永久冻结）您的账户，届时，您与苏州金赢网电子商务有限公司基于本协议的合同关系即终止。您的账户被注销（永久冻结）后，苏州金赢网电子商务有限公司没有义务为您保留或向您披露您账户中的任何信息。\n" +
                        "\n" +
                        "3.4 您同意，您与苏州金赢网电子商务有限公司的合同关系终止后，苏州金赢网电子商务有限公司仍享有下列权利：\n" +
                        "\n" +
                        "3.4.1 继续保存您的注册信息及您使用苏州金赢网电子商务有限公司平台服务期间的所有交易信息。\n" +
                        "\n" +
                        "3.4.2 您在使用苏州金赢网电子商务有限公司平台服务期间存在违法行为或违反本协议和/或规则的行为的，苏州金赢网电子商务有限公司仍可依据本协议向您主张权利。\n" +
                        "\n" +
                        "3.5苏州金赢网电子商务有限公司中止或终止向您提供苏州金赢网电子商务有限公司平台服务后，对于您在服务中止或终止之前的交易行为依下列原则处理，您应独力处理并完全承担进行以下处理所产生的任何争议、损失或增加的任何费用，并应确保苏州金赢网电子商务有限公司免于因此产生任何损失或承担任何费用：\n" +
                        "\n" +
                        "3.5.1您在服务中止或终止之前已经上传至苏州金赢网电子商务有限公司平台的物品尚未交易的，苏州金赢网电子商务有限公司有权在中止或终止服务的同时删除此项物品的相关信息；\n" +
                        "\n" +
                        "3.5.2 您在服务中止或终止之前已经达成买卖合同，但合同尚未实际履行的，苏州金赢网电子商务有限公司有权删除该买卖合同及其交易物品的相关信息。\n" +
                        "\n" +
                        "3.6 如需注销账号或关闭店铺，个人用户通过邮件，并提供身份证扫描件、账号、密码及情况说明，发送到kf@jinying365.com；企业用户通过邮件，并提供公司营业执照（盖公章扫描件）、情况说明（盖公章扫描件）、账号、密码发送到kf@jinying365.com。客服收到邮件后将审核处理。\n" +
                        "\n" +
                        "4 其他约定\n" +
                        "\n" +
                        "4.1 本规则的订立、解释、履行及效力均受中华人民共和国大陆地区法律管辖。如发生本规则条款与适用之法律相抵触时，则这些条款将完全按法律规定重新解释，而其他有效条款继续有效。\n" +
                        "\n" +
                        "4.2 遵守的规则\n" +
                        "\n" +
                        "平台规则：指平台根据法律法规等强制性规定、市场行情、平台整体规划及相关要求、平台现有功能等因素制订的交易规范，包括《金赢物流规则》、《金赢供应链规则》、《工业超市规则》、《平台交易规则》、《加工交易规则》、《商家店铺规则》、《我的金赢后台管理规则》、《采购招标服务规则》、《金赢合伙人规则》等以上规则均需遵守。\n" +
                        "\n" +
                        "4.3规则冲突：\n" +
                        "\n" +
                        "同一时间节点，平台规则、平台公告、合同内容不一致，按下列顺序由高到低确定效力：\n" +
                        "\n" +
                        "1、平台公告；\n" +
                        "\n" +
                        "2、合同；\n" +
                        "\n" +
                        "3、平台规则。\n" +
                        "\n" +
                        "4.4如用户就本协议内容或其执行发生任何争议，双方应尽力友好协商解决；协商不成时，任何一方均可向苏州金赢网电子商务有限公司工商注册地法院提起诉讼。\n" +
                        "\n" +
                        "4.5 本规则自用户点击“提交”即生效，本协议最终由苏州金赢网电子商务有限公司解释。" )
                .setCancelable(false)
                .setPositiveButton("我已阅读", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        cb_agree_rule.setChecked(true);
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.login_back_blue));
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setText("我已阅读并同意");
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ffffff"));
    }
}

