package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.jymall.common.CommonDialog;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.ImageFactory;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyConfirmDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_refundgoods_two)
public class RefundGoodsTwoActivity extends TopActivity {

    @ViewInject(R.id.tv_proName)
    private TextView tv_proName;

    @ViewInject(R.id.tv_salePrice)
    private TextView tv_salePrice;

    @ViewInject(R.id.tv_quantity)
    private TextView tv_quantity;

    @ViewInject(R.id.tv_info)
    private TextView tv_info;

    @ViewInject(R.id.ll_refundWait)
    private LinearLayout ll_refundWait;

    @ViewInject(R.id.ll_refundRefuse)
    private LinearLayout ll_refundRefuse;

    @ViewInject(R.id.ll_refundCommitLogistic)
    private LinearLayout ll_refundCommitLogistic;

    @ViewInject(R.id.ll_refundSendGoods)
    private LinearLayout ll_refundSendGoods;

    @ViewInject(R.id.ll_refundOk)
    private LinearLayout ll_refundOk;

    @ViewInject(R.id.tv_refuseReason)
    private TextView tv_refuseReason;

    @ViewInject(R.id.tv_refundOkDate)
    private TextView tv_refundOkDate;

    @ViewInject(R.id.tv_refundMoney)
    private TextView tv_refundMoney;

    @ViewInject(R.id.tv_showComp)
    private TextView tv_showComp;

    @ViewInject(R.id.tv_showAddress)
    private TextView tv_showAddress;

    @ViewInject(R.id.serviceInfo)
    private TextView serviceInfo;

    @ViewInject(R.id.btn_submit)
    private Button btn_submit;

    @ViewInject(R.id.img_proImgPath)
    private ImageView img_proImgPath;

    @ViewInject(R.id.et_refundLogisticNo)
    private EditText et_refundLogisticNo;

    @ViewInject(R.id.et_refundFeeRemark)
    private EditText et_refundFeeRemark;

    @ViewInject(R.id.rl_selectLogistic)
    private RelativeLayout rl_selectLogistic;

    @ViewInject(R.id.rl_selectAddress)
    private RelativeLayout rl_selectAddress;

    @ViewInject(R.id.iv_uploadImg)
    private ImageView iv_uploadImg;

    private String skey;
    private String orderid;
    private String orderdtlid;
    private String reason;
    private String remark;
    private String money;
    private String dtlmoney;
    private String isReceived;
    private String refundStatus;

    private String refundid;
    private String fileurl;
    private String serviceid;
    private String logistic;
    private String logisticno;
    private String logisticremark;

    private JSONObject order;
    private JSONObject orderdtl;
    private JSONObject refund;
    private JSONArray servicelist;
    ArrayList<String> serviceNameList = new ArrayList<String>();
    ArrayList<String> serviceIdList = new ArrayList<String>();
    ArrayList<String> addressList = new ArrayList<String>();
    public List<Map<String, Object>> dataMaps= new ArrayList<Map<String, Object>>();

    private String TEMP_IMAGE_PATH;
    private String TEMP_IMAGE_PATH1= Environment.getExternalStorageDirectory().getPath()+"/temp1.png";
    private Bitmap bitmap = null;
    private MyConfirmDialog mcd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refundgoods_two);
        x.view().inject(this);
        super.title.setText("申请退货");
        super.progressDialog.hide();
        Intent i = this.getIntent();
        refundid = i.getStringExtra("refundId");
        orderid = i.getStringExtra("orderId");
        orderdtlid = i.getStringExtra("orderDtlId");
        initData();
    }


    private void initData(){
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("refundId", refundid);
        maps.put("orderId", orderid);
        maps.put("orderDtlId", orderdtlid);

        XUtilsHelper.getInstance().post("app/refundGoodsTwo.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                progressDialog.hide();
                JSONObject res;
                dataMaps.clear();
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    skey = res.get("serverKey").toString();
                    order = res.getJSONObject("order");
                    orderdtl= res.getJSONObject("orderdtl");
                    refund = res.getJSONObject("refund");
                    servicelist=res.getJSONArray("servicelist");
                    refundid=refund.getString("id");
                    dtlmoney=orderdtl.getString("money");
                    refundStatus=orderdtl.getString("refundStatus");

                    for (int i= 0;i<servicelist.length();i++){
                        Map<String, Object> dataMap = new HashMap<String, Object>();
                        JSONObject jsonObject=servicelist.getJSONObject(i);
                        String id=jsonObject.getString("id");
                        String name=jsonObject.getString("name");
                        String address=jsonObject.getString("province")+jsonObject.getString("city")+jsonObject.getString("district")+jsonObject.getString("address");
                        String postcode=jsonObject.getString("postcode");
                        String contact=jsonObject.getString("contact");
                        String mobile=jsonObject.getString("mobile");
                        dataMaps.add(dataMap);
                        serviceNameList.add(name);
                        serviceIdList.add(id);
                        addressList.add(address+"（邮编："+postcode+"  收件人："+contact+" 联系电话："+mobile+")");
                    }

                    String info = "";
                    String salePrice = orderdtl.getString("salePrice");
                    info = "品牌："+orderdtl.getString("brand")+"\n"+"材质："+orderdtl.getString("proQuality")+"\n" +"规格："+orderdtl.getString("proSpec");
                    tv_info.setText(info);
                    tv_proName.setText(orderdtl.getString("proName"));
                    img_proImgPath.setBackgroundResource(0);
                    XUtilsHelper.getInstance().bindCommonImage(img_proImgPath, orderdtl.getString("proImgPath"), true);
                    if(salePrice.equals("0")){
                        salePrice = "面议";
                    }
                    else{
                        salePrice += "元/"+orderdtl.getString("unit");
                    }
                    tv_salePrice.setText(salePrice);
                    tv_quantity.setText("×"+orderdtl.getString("quantity")+orderdtl.getString("unit"));

                    if(refundStatus.equals("1")){
                        showRefundWait();
                    }
                    else if(refundStatus.equals("-1")){
                        tv_refuseReason.setText("- 拒绝原因："+refund.getString("msg"));
                        showRefundRefuse();
                    }
                    else if(refundStatus.equals("2")){
                        showRefundCommitLogistic();
                    }
                    else if(refundStatus.equals("3")){
                        showRefundSendGoods();
                    }
                    else if(refundStatus.equals("4")){
                        tv_refundOkDate.setText("- 退款时间："+orderdtl.getString("backEndDate"));
                        tv_refundMoney.setText("- 退款金额："+refund.getString("money")+"元");
                        showRefundOk();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });

        //设置监听
        rl_selectAddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int count=0;
                AlertDialog.Builder builder = new AlertDialog.Builder(RefundGoodsTwoActivity.this);
                builder.setIcon(R.drawable.icon_logo);
                builder.setTitle("区域服务中心");
                final String[] list = (String[])serviceNameList.toArray(new String[serviceNameList.size()]);
                //    设置一个单项选择下拉框
                /**
                 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
                 * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
                 * 第三个参数给每一个单选项绑定一个监听器
                 */
                String info=serviceInfo.getText().toString();
                if(FormatUtil.isNoEmpty(info)){
                    for(int i=0;i<list.length;i++){
                        if(list[i].equals(info)){
                            count=i;
                            break;
                        }
                    }
                }
                builder.setSingleChoiceItems(list, count, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(FormatUtil.isNoEmpty(which)){
                            if(which>-1) {
                                serviceInfo.setText(serviceNameList.get(which));
                                serviceid=serviceIdList.get(which);
                                tv_showAddress.setText(addressList.get(which));
                            }
                        }
                        //Toast.makeText(RefundMoneyOneActivity.this, "状态为：" + refundReason[which], Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(FormatUtil.isNoEmpty(which)){
                            if(which>-1) {
                                serviceInfo.setText(serviceNameList.get(which));
                                serviceid=serviceIdList.get(which);
                                tv_showAddress.setText(addressList.get(which));
                            }
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        serviceInfo.setText("请选择区域服务中心");
                        serviceid="";
                        tv_showAddress.setText("请选择收货地址");
                    }
                });
                builder.show();
            }
        });

        //设置监听
        rl_selectLogistic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int count=0;
                AlertDialog.Builder builder = new AlertDialog.Builder(RefundGoodsTwoActivity.this);
                builder.setIcon(R.drawable.icon_logo);
                builder.setTitle("物流公司");
                final String[] compList = {"顺丰", "中通", "圆通", "韵达"};
                //    设置一个单项选择下拉框
                /**
                 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
                 * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
                 * 第三个参数给每一个单选项绑定一个监听器
                 */
                if(FormatUtil.isNoEmpty(logistic)){
                    for(int i=0;i<compList.length;i++){
                        if(compList[i].equals(logistic)){
                            count=i;
                            break;
                        }
                    }
                }
                builder.setSingleChoiceItems(compList, count, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(FormatUtil.isNoEmpty(which)){
                            if(which>-1) {
                                tv_showComp.setText(compList[which]);
                                logistic = compList[which];
                            }
                        }
                        //Toast.makeText(RefundMoneyOneActivity.this, "状态为：" + refundReason[which], Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(FormatUtil.isNoEmpty(which)){
                            if(which>-1) {
                                tv_showComp.setText(compList[which]);
                                logistic = compList[which];
                            }
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        tv_showComp.setText("");
                        logistic="";
                    }
                });
                builder.show();
            }
        });
    }

    @Event(R.id.btn_cancelRefund_wait)
    private void cancelWaitClick(View v){
        new CommonDialog(RefundGoodsTwoActivity.this, R.style.dialog, "取消退货申请后，本次退款将关闭，您还可以再次发起退款/退货申请，确认取消？", new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    progressDialog.show();
                    Map<String, String> maps= new HashMap<String, String>();
                    maps.put("serverKey", skey);
                    maps.put("id", refundid);
                    maps.put("type", "0");
                    XUtilsHelper.getInstance().post("app/cancelRefund.htm", maps,new XUtilsHelper.XCallBack(){

                        @SuppressLint("NewApi")
                        @Override
                        public void onResponse(String result)  {
                            progressDialog.hide();
                            JSONObject res;
                            try {
                                res = new JSONObject(result);
                                setServerKey(res.get("serverKey").toString());
                                skey = res.get("serverKey").toString();
                                if(res.get("d").equals("n")){
                                    CommonUtil.alter(res.get("msg").toString());
                                }
                                else{
                                    CommonUtil.alter("取消成功！");
                                    finish();
                                    Intent i =  new Intent(getApplicationContext(), RefundGoodsCancelActivity.class);
                                    i.putExtra("refundId", refundid);
                                    i.putExtra("cancelDate", res.get("canceldate").toString());
                                    startActivity(i);
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }

    @Event(R.id.btn_cancelRefund_refuse)
    private void cancelRefuseClick(View v){
        new CommonDialog(RefundGoodsTwoActivity.this, R.style.dialog, "取消退货申请后，本次退款将关闭，您还可以再次发起退款/退货申请，确认取消？", new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    progressDialog.show();
                    Map<String, String> maps= new HashMap<String, String>();
                    maps.put("serverKey", skey);
                    maps.put("id", refundid);
                    maps.put("type", "0");
                    XUtilsHelper.getInstance().post("app/cancelRefund.htm", maps,new XUtilsHelper.XCallBack(){

                        @SuppressLint("NewApi")
                        @Override
                        public void onResponse(String result)  {
                            progressDialog.hide();
                            JSONObject res;
                            try {
                                res = new JSONObject(result);
                                setServerKey(res.get("serverKey").toString());
                                skey = res.get("serverKey").toString();
                                if(res.get("d").equals("n")){
                                    CommonUtil.alter(res.get("msg").toString());
                                }
                                else{
                                    CommonUtil.alter("取消成功！");
                                    finish();
                                    Intent i =  new Intent(getApplicationContext(), RefundGoodsCancelActivity.class);
                                    i.putExtra("refundId", refundid);
                                    i.putExtra("cancelDate", res.get("canceldate").toString());
                                    startActivity(i);
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }

    @Event(R.id.btn_submit)
    private void submitClick(View v){
        logisticno=et_refundLogisticNo.getText().toString();
        logisticremark=et_refundFeeRemark.getText().toString();
        if(!FormatUtil.isNoEmpty(serviceid)){
            CommonUtil.alter("请选择收货地址！");
            return ;
        }
        else if(!FormatUtil.isNoEmpty(logisticno)){
            CommonUtil.alter("运单号码不能为空！");
            return ;
        }
        else if(!FormatUtil.isNoEmpty(logistic)){
            CommonUtil.alter("请选择物流公司！");
            return ;
        }

        if(FormatUtil.getStringLength(logisticno)>20){
            CommonUtil.alter("运单号填写不正确！");
            return ;
        }
        if(FormatUtil.getStringLength(logisticremark)>40){
            CommonUtil.alter("运费说明填写不正确！");
            return ;
        }

        new CommonDialog(RefundGoodsTwoActivity.this, R.style.dialog, "确定发货？", new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    progressDialog.show();
                    Map<String, String> maps= new HashMap<String, String>();
                    maps.put("serverKey", skey);
                    maps.put("refundid", refundid);
                    maps.put("logisticno", logisticno);
                    maps.put("logistic", logistic);
                    maps.put("logisticremark", logisticremark);
                    maps.put("fileurl", fileurl);
                    maps.put("serviceid", serviceid);
                    XUtilsHelper.getInstance().post("app/addRefundLogistic.htm", maps,new XUtilsHelper.XCallBack(){

                        @SuppressLint("NewApi")
                        @Override
                        public void onResponse(String result)  {
                            progressDialog.hide();
                            JSONObject res;
                            try {
                                res = new JSONObject(result);
                                String refundid=res.get("refundid").toString();
                                setServerKey(res.get("serverKey").toString());
                                if(res.get("d").equals("n")){
                                    CommonUtil.alter(res.get("msg").toString());
                                }
                                else{
                                    CommonUtil.alter(res.get("msg").toString());
                                    Intent i =  new Intent(getApplicationContext(), RefundGoodsTwoActivity.class);
                                    i.putExtra("refundId", refundid);
                                    startActivity(i);
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();
    }

    @Event(R.id.btn_editRefund_wait)
    private void editRefundWaitClick(View v){
        Intent i =  new Intent(getApplicationContext(), RefundGoodsOneEditActivity.class);
        i.putExtra("orderId", orderid);
        i.putExtra("orderDtlId", orderdtlid);
        startActivity(i);
    }

    @Event(R.id.btn_editRefund_refuse)
    private void editRefundRefuseClick(View v){
        Intent i =  new Intent(getApplicationContext(), RefundGoodsOneEditActivity.class);
        i.putExtra("orderId", orderid);
        i.putExtra("orderDtlId", orderdtlid);
        startActivity(i);
    }

    @Event(value=R.id.iv_uploadImg,type=View.OnTouchListener.class)
    private boolean uploadImg(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            if(mcd==null){
                mcd=new MyConfirmDialog(RefundGoodsTwoActivity.this, "上传图片", "拍照上传", "本地上传");
                mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm() {
                        //设置一个临时路径，保存所拍的照片
                        //获取该路径
                        TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";
                        //传入ACTION_IMAGE_CAPTURE:该action指向一个照相机app
                        Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //创建File并获取它的URI值
                        Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));
                        //MediaStore.EXTRA_OUTPUT为字符串"output"，即将该键值对放进intent中
                        intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                        startActivityForResult(intent1,200);
                    }

                    @Override
                    public void doCancel() {
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, 100);
                    }
                });
            }
            mcd.show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==100&&data!=null){
                progressDialog.show();
                mcd.dismiss();
                //选择图片
                Uri uri = data.getData();
                if(bitmap != null)//如果不释放的话，不断取图片，将会内存不够
                    bitmap.recycle();
                TEMP_IMAGE_PATH = ImageFactory.getPath(getApplicationContext(), uri);
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap = BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
                iv_uploadImg.setImageBitmap(bitmap);
                Map<String, String> maps = new HashMap<String, String>();
                maps.put("fileUploadeFileName", TEMP_IMAGE_PATH1.substring(TEMP_IMAGE_PATH1.lastIndexOf("/")+1));
                maps.put("pathType","company");
                Map<String, File> file = new HashMap<String, File>();
                file.put("fileUploade",new File(TEMP_IMAGE_PATH1));
                XUtilsHelper.getInstance().upLoadFile("fileUploadOkJson.htm", maps, file, new XUtilsHelper.XCallBack() {
                    @Override
                    public void onResponse(String result) {
                        try{
                            JSONObject res = FormatUtil.toJSONObject(result);
                            if(res != null){
                                if(res.get("d").equals("n")){
                                    CommonUtil.alter("图片上传失败");
                                    progressDialog.hide();
                                }
                                else{
                                    fileurl=res.getString("fileUrl");
                                }
                            }
                        }
                        catch(Exception e){e.printStackTrace();}
                    }
                });
            }else if(requestCode==200){
                progressDialog.show();
                mcd.dismiss();
                if(bitmap != null)//如果不释放的话，不断取图片，将会内存不够
                    bitmap.recycle();
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap = BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
                iv_uploadImg.setImageBitmap(bitmap);
                Map<String, String> maps = new HashMap<String, String>();
                maps.put("fileUploadeFileName", TEMP_IMAGE_PATH1.substring(TEMP_IMAGE_PATH1.lastIndexOf("/")+1));
                maps.put("pathType","company");
                Map<String, File> file = new HashMap<String, File>();
                file.put("fileUploade",new File(TEMP_IMAGE_PATH1));
                XUtilsHelper.getInstance().upLoadFile("fileUploadOkJson.htm", maps, file, new XUtilsHelper.XCallBack() {

                    @Override
                    public void onResponse(String result) {
                        try{
                            JSONObject res = FormatUtil.toJSONObject(result);
                            if(res != null){
                                if(res.get("d").equals("n")){
                                    CommonUtil.alter("图片上传失败");
                                    progressDialog.hide();
                                }
                                else{
                                    progressDialog.hide();
                                    fileurl=res.getString("fileUrl");
                                }
                            }
                        }
                        catch(Exception e){e.printStackTrace();}
                    }
                });
            }
        }
    }

    private void showRefundWait(){
        hideAll();
        ll_refundWait.setVisibility(View.VISIBLE);
    }

    private void showRefundRefuse(){
        hideAll();
        ll_refundRefuse.setVisibility(View.VISIBLE);
    }

    private void showRefundCommitLogistic(){
        hideAll();
        ll_refundCommitLogistic.setVisibility(View.VISIBLE);
    }

    private void showRefundSendGoods(){
        hideAll();
        ll_refundSendGoods.setVisibility(View.VISIBLE);
    }

    private void showRefundOk(){
        hideAll();
        ll_refundOk.setVisibility(View.VISIBLE);
    }

    private void hideAll(){
        ll_refundWait.setVisibility(View.GONE);
        ll_refundRefuse.setVisibility(View.GONE);
        ll_refundCommitLogistic.setVisibility(View.GONE);
        ll_refundSendGoods.setVisibility(View.GONE);
        ll_refundOk.setVisibility(View.GONE);
    }
}
