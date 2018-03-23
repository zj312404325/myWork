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

    private void showAgreement(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("（工业超市）温馨提示")
                .setMessage("您在申请注册流程中点击同意前，应当认真阅读以下协议。请您务必审慎阅读、充分理解协议中相关条款内容，其中包括：限制责任的条款、法律适用和管辖的条款、其他以粗体下划线标识的重要条款。\n" +
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
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.login_back_blue));
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setText("我已阅读");
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ffffff"));
    }
}

