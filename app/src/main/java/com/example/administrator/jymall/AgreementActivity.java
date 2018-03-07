package com.example.administrator.jymall;

import android.os.Bundle;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_agreement)
public class AgreementActivity extends TopActivity {
    @ViewInject(R.id.tv_content)
    private TextView tv_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("平台协议");
        progressDialog.hide();

        tv_content.setText("当您点击“同意”按钮注册为金赢工业超市网会员或使用金赢工业超市网（以下简称“平台”）时，也代表你签署并认可了该份规则，您在后续的使用过程中必须遵守该规则。\n" +
                "\n" +
                "一、总则\n" +
                "1.1 为了规范平台交易，促进买卖双方交易的顺利进行，根据公平、公正、平等的原则，制定本规则。\n" +
                "\n" +
                "1.2 本规则适用于在平台里进行的商品交易活动，平台的工作人员、会员、仓库在进行货物交易、结算、货物交收时必须遵守本规则。\n" +
                "\n" +
                "1.3 金赢工业超市平台是苏州金赢网电子商务有限公司自营的网上工业品交易超市，域名：mall.jinying365.com,是区别于其他的金赢网交易网站，其所提供的商品均为苏州金赢网电子商务有限公司所有。\n" +
                "\n" +
                "1.4 您确认，在您完成注册程序或者使用平台服务时，您应当是具备完全民事权利能力和与所从事的民事行为相适应的行为能力的自然人、法人或其他组织。若您不具备前述主体资格，请勿使用平台的服务，否则您及您的监护人应承担因此而导致的一切后果，且平台有权注销（永久冻结）您的账户，并向您及您的监护人索偿。如您代表一家公司或其他法律主体在平台注册或在金赢网注册，则您声明和保证，您有权使该公司或该法律主体受本协议“条款”的约束。\n" +
                "\n" +
                "1.5 您明确同意，平台有权根据法律法规及运营需要，不时对本协议和条款进行修改并公布在平台，该等协议和条款已经公布，立即生效，平台无须另行通知。应主动不时查阅或注意平台的公告，若不同意平台的修改，应立即停止使用平台。若继续使用平台的服务，则视为接受平台对本协议和条款的修改。\n" +
                "\n" +
                "1.6 您在金赢网注册成为金赢网会员时，即可在平台里进行采购，也可在金赢网平台使用，且均认可其在平台上产生的订单是真实、有效且具有法律效力的。\n" +
                "\n" +
                "1.7 平台客服热线：0512-52686666，客服邮箱：kf@jinying365.com。\n" +
                "\n" +
                "二、须知\n" +
                "2.1 收货人\n" +
                "\n" +
                "您需填写真实有效的收货地址，如因您填写的收货地址有误而导致货物无法准确及时送达而产生的纠纷由该自行承担负责。\n" +
                "\n" +
                "2.2 发票信息\n" +
                "\n" +
                "您需填写真实有效的发票信息，如因您填写的发票信息有误而导致发票开错，由您自行承担责任。\n" +
                "\n" +
                "2.3 银行账户管理\n" +
                "\n" +
                "您需填写真实有效的银行账户信息，如因您填写的银行账户信息有误而导致发票开错，由您自行承担责任。\n" +
                "\n" +
                "2.4 合同模板\n" +
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
                "3.1 订单\n" +
                "\n" +
                "3.1.1 平台发布商品信息，商品信息包括但不限于以下内容：品名、规格、价格、数量、重量、品牌等。\n" +
                "\n" +
                "3.1.2 买方须先下载模板填写采购需求，并上传采购需求，平台筛选商品生成订单，符合买方采购需求的，则买方同意生成的订单；若买方不同意，则订单取消，不视为违约。\n" +
                "\n" +
                "3.1.3 若买方需要定制化的商品，买方上传采购需求（含图纸），平台自收到采购需求之日起四个工作日内回复是否生成订单；若平台已生成订单，则由买方确认是否同意生成的订单；若平台未生成订单，则采购需求取消，不视为平台的违约。\n" +
                "\n" +
                "3.2 付款\n" +
                "\n" +
                "3.2.1 买方确认生成的订单后，买方须在规定时间内支付足额的订单货款（定制化的，则按照订单款的30%支付订金，剩余货款须在卖方催促付款时付清）。\n" +
                "\n" +
                "3.2.2 买方由两种付款方式选择；①现结，含线上网银支付和线下网银支付；②信用支付，可月结或者季度结算。详见如何支付。\n" +
                "\n" +
                "3.3 发货\n" +
                "\n" +
                "卖方收到订单款后（信用支付除外）按照双方约定或者承诺的时间送货。若库存有现货的，自收到订单款两日内发货；若库存没有现货，则发货时间以买卖双方约定为准。\n" +
                "\n" +
                "3.4 运输方式\n" +
                "\n" +
                "平台直送或者通过快递公司或物流公司送货。（详见配送方式）\n" +
                "\n" +
                "3.5 商品质量\n" +
                "\n" +
                "超市平台订单中所涉及的商品质量标准，若无特别规定，按商品出厂标准执行，若没有出厂标准，则按照国家标准执行。\n" +
                "\n" +
                "3.6 商品签收\n" +
                "\n" +
                "3.6.1 收货人必须是实名认证的买方或者经过买方指定的收货人。否则卖方有权拒绝交货，其所产生的法律后果由买方承担。\n" +
                "\n" +
                "3.6.2 收到商品时，请确认商品正确（包括商品名、品牌、型号、数量、包装、外观等）、无损坏后签收，如商品外箱贴有易碎标识。请务必当场开箱检查箱内商品是否有破损。如外包装有明显损坏迹象，可及时通知平台，并拒绝签收，平台会处理并承担由此而产生的运输费用。\n" +
                "\n" +
                "若买方已经完好签收，则平台不再受理关于此商品有误或有损坏的投诉。\n" +
                "\n" +
                "3.6.3 买方签收商品后，须在平台中确认收货状态。否则一般以订单对应的快递或物流公司的签收信息作为买方收货依据。若买方不确认收货，也不提出退换货要求的，订单发货7天后默认为已收货状态。\n" +
                "\n" +
                "3.7 价格\n" +
                "\n" +
                "平台里所有报价即成交价均为该商品的含税价格。\n" +
                "\n" +
                "四、售后服务/品质承诺\n" +
                "4.1 不适用无质量异议的退换货情形。\n" +
                "\n" +
                "4.1.1 定制件信息填写错误的。\n" +
                "\n" +
                "4.1.2 影响二次销售的。\n" +
                "\n" +
                "4.1.3 人为损害的商品。\n" +
                "\n" +
                "4.1.4 加工定制件。\n" +
                "\n" +
                "4.1.5 7天默认收货的。\n" +
                "\n" +
                "4.2 7天无理由退换货原则（标准件）\n" +
                "\n" +
                "（详见7天无理由退换货原则（标准件））\n" +
                "\n" +
                "4.3 有质量问题的退、换货原则\n" +
                "\n" +
                "被卖方确认或者经有资质的鉴定机构鉴定为性能故障的质量问题，则卖方根据买方的申请，进行退货或换货处理。\n" +
                "\n" +
                "五、违约责任\n" +
                "5.1 买方连续超过三次或三次以上提交订单而不支付货款或被平台认定是恶意提交订单而不支付货款的，平台将会暂停买方在平台的交易权限。须其以书面申请并经平台审核通过后方可重新开通。\n" +
                "\n" +
                "5.2 买方（定制化）逾期付款，逾期付款 15 日内，每日按照订单价款的千分之五计算滞纳金，超过15日未付款的，视为取消订单，已收取的订金不予退还，且有权要求买方支付滞纳金。\n" +
                "\n" +
                "5.3 （信用支付）买方付款逾期一天，则超市平台将冻结买方的账户，已生成订单未发货的，暂停发货，且卖方有权每日按照订单总额的千分之五收取滞纳金，若造成卖方损失的，包括但不限于滞纳金、赔偿金、诉讼费、律师费等均由买方承担。\n" +
                "\n" +
                "六、其他\n" +
                "6.1 若针对同一行为或情形，本规则与金赢网平台发布的其他规则约定不一致的，以本规则为准。\n" +
                "\n" +
                "6.2 如您就本规则或因执行本规则交易过程中产生争议，双方应协商解决，解决不成的，任一方均可向苏州金赢网电子商务有限公司注册地所在地法院起诉。\n" +
                "\n" +
                "6.3 本规则最终由苏州金赢网电子商务有限公司解释");
    }
}