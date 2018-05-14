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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.jymall.common.CommonDialog;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.ImageFactory;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyConfirmDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_refundmoney_one)
public class RefundMoneyOneActivity extends TopActivity {

    @ViewInject(R.id.tv_proName)
    private TextView tv_proName;

    @ViewInject(R.id.tv_salePrice)
    private TextView tv_salePrice;

    @ViewInject(R.id.tv_quantity)
    private TextView tv_quantity;

    @ViewInject(R.id.tv_info)
    private TextView tv_info;

    @ViewInject(R.id.tv_totalMoney)
    private TextView tv_totalMoney;

    @ViewInject(R.id.tv_refundMoney)
    private EditText tv_refundMoney;

    @ViewInject(R.id.btn_submit)
    private Button btn_submit;

    @ViewInject(R.id.img_proImgPath)
    private ImageView img_proImgPath;

    @ViewInject(R.id.rl_goodsState)
    private RelativeLayout rl_goodsState;

    @ViewInject(R.id.rl_refundReason)
    private RelativeLayout rl_refundReason;

    @ViewInject(R.id.tv_showState)
    private TextView tv_showState;

    @ViewInject(R.id.tv_showReason)
    private TextView tv_showReason;

    @ViewInject(R.id.iv_uploadImg)
    private ImageView iv_uploadImg;

    @ViewInject(R.id.iv_close_uploadImg)
    private ImageView iv_close_uploadImg;

    @ViewInject(R.id.et_refundMark)
    private EditText et_refundMark;


    private String orderid;
    private String orderdtlid;
    private String reason;
    private String remark;
    private String money;
    private String dtlmoney;
    private String fileurl;
    private String isReceived;

    private String skey;
    private JSONObject order;
    private JSONObject orderdtl;

    private String TEMP_IMAGE_PATH;
    private String TEMP_IMAGE_PATH1= Environment.getExternalStorageDirectory().getPath()+"/temp1.png";
    private Bitmap bitmap = null;
    private MyConfirmDialog mcd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("申请退款");
        super.progressDialog.hide();
        Intent i = this.getIntent();
        orderid = i.getStringExtra("orderId");
        orderdtlid = i.getStringExtra("orderDtlId");
        skey=super.serverKey;
        initData();
        iv_close_uploadImg.setVisibility(View.INVISIBLE);
    }


    private void initData(){
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("orderId", orderid);
        maps.put("orderDtlId", orderdtlid);

        XUtilsHelper.getInstance().post("app/goRefundMoney.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                progressDialog.hide();
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    order = res.getJSONObject("order");
                    orderdtl= res.getJSONObject("orderdtl");
                    dtlmoney=orderdtl.getString("money");

                    String info = "";
                    String salePrice = orderdtl.getString("salePrice");
                    info = "品牌："+orderdtl.getString("brand")+"\n"+"材质："+orderdtl.getString("proQuality")+"\n" +"规格："+orderdtl.getString("proSpec");
                    tv_info.setText(info);
                    tv_proName.setText(orderdtl.getString("proName"));
                    img_proImgPath.setBackgroundResource(0);

                    String orderType=order.get("orderType").toString();
                    if(orderType.equals("product")) {
                        XUtilsHelper.getInstance().bindCommonImage(img_proImgPath, orderdtl.getString("proImgPath"), true);
                    }
                    else if(orderType.equals("fastMatch")){
                        img_proImgPath.setBackgroundResource(R.drawable.pro_fast_match);
                    }
                    else if(orderType.equals("orderMatch")){
                        img_proImgPath.setBackgroundResource(R.drawable.pro_order_match);
                    }
                    else if(orderType.equals("fabFastMatch")){
                        img_proImgPath.setBackgroundResource(R.drawable.pro_fab_fast_match);
                    }
                    else if(orderType.equals("fabOrderMatch")){
                        img_proImgPath.setBackgroundResource(R.drawable.pro_fab_order_match);
                    }
                    else{
                        XUtilsHelper.getInstance().bindCommonImage(img_proImgPath, orderdtl.getString("proImgPath"), true);
                    }

                    if(salePrice.equals("0")){
                        salePrice = "面议";
                    }
                    else{
                        salePrice += "元/"+orderdtl.getString("unit");
                    }
                    tv_salePrice.setText(salePrice);
                    tv_quantity.setText("×"+orderdtl.getString("quantity")+orderdtl.getString("unit"));

                    tv_refundMoney.setText(dtlmoney);
                    tv_totalMoney.setText("最多"+dtlmoney+"元");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });

        //设置监听
        rl_goodsState.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int count=-1;
                AlertDialog.Builder builder = new AlertDialog.Builder(RefundMoneyOneActivity.this);
                builder.setIcon(R.drawable.icon_logo);
                builder.setTitle("货物状态");
                final String[] receiveState = {"未收到货", "已收到货"};
                //    设置一个单项选择下拉框
                /**
                 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
                 * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
                 * 第三个参数给每一个单选项绑定一个监听器
                 */
                if(FormatUtil.isNoEmpty(isReceived)){
                    if(isReceived.equals("1")){
                        count=1;
                    }
                    else{
                        count=0;
                    }
                }
                builder.setSingleChoiceItems(receiveState, count, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(which == 0){
                            tv_showState.setText(receiveState[which]);
                            isReceived="0";
                        }
                        else if(which == 1){
                            tv_showState.setText(receiveState[which]);
                            isReceived="1";
                        }
                        //Toast.makeText(RefundMoneyOneActivity.this, "状态为：" + receiveState[which], Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(which == 0){
                            tv_showState.setText(receiveState[which]);
                            isReceived="0";
                        }
                        else if(which == 1){
                            tv_showState.setText(receiveState[which]);
                            isReceived="1";
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        tv_showState.setText("");
                        isReceived="";
                    }
                });
                builder.show();
            }
        });

        //设置监听
        rl_refundReason.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int count=-1;
                AlertDialog.Builder builder = new AlertDialog.Builder(RefundMoneyOneActivity.this);
                builder.setIcon(R.drawable.icon_logo);
                builder.setTitle("退款原因");
                final String[] refundReason = {"7天无理由退换货", "退运费", "做工问题", "质量问题", "大小/尺寸与商品描述不符", "颜色/图案/款式与商品描述不符", "材质面料与商品描述不符", "少件/漏发", "卖家发错货", "包装/商品损坏/污渍", "假冒品牌", "未按约定时间发货", "发票问题"};
                //    设置一个单项选择下拉框
                /**
                 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
                 * 第二个参数代表索引，指定默认哪一个单选框被勾选上，0开始
                 * 第三个参数给每一个单选项绑定一个监听器
                 */
                if(FormatUtil.isNoEmpty(reason)){
                    for(int i=0;i<refundReason.length;i++){
                        if(refundReason[i].equals(reason)){
                            count=i;
                            break;
                        }
                    }
                }
                builder.setSingleChoiceItems(refundReason, count, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(FormatUtil.isNoEmpty(which)){
                            if(which>-1) {
                                tv_showReason.setText(refundReason[which]);
                                reason = refundReason[which];
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
                                tv_showReason.setText(refundReason[which]);
                                reason = refundReason[which];
                            }
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        tv_showReason.setText("");
                        reason="";
                    }
                });
                builder.show();
            }
        });
    }

    @Event(R.id.btn_submit)
    private void submitClick(View v){
        money=tv_refundMoney.getText().toString();
        remark=et_refundMark.getText().toString();
        if(!FormatUtil.isNoEmpty(money)){
            CommonUtil.alter("退货金额不能为空！");
            return ;
        }
        else if(!FormatUtil.isNoEmpty(isReceived)){
            CommonUtil.alter("请选择收货状态！");
            return ;
        }
        else if(!FormatUtil.isNoEmpty(reason)){
            CommonUtil.alter("退货原因不能为空！");
            return ;
        }
        else{
            if(FormatUtil.toDouble(money)<=0){
                CommonUtil.alter("金额不能小于0！");
                return ;
            }
            if(FormatUtil.toDouble(money) > FormatUtil.toDouble(dtlmoney)){
                CommonUtil.alter("金额不能大于明细金额！");
                return ;
            }
        }

        new CommonDialog(RefundMoneyOneActivity.this, R.style.dialog, "确定提交？", new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    progressDialog.show();
                    Map<String, String> maps= new HashMap<String, String>();
                    maps.put("serverKey", skey);
                    maps.put("orderId", orderid);
                    maps.put("orderDtlId", orderdtlid);
                    maps.put("flag", "1");
                    maps.put("refundmemo", remark);
                    maps.put("reason", reason);
                    maps.put("isreceived", isReceived);
                    maps.put("fileurl", fileurl);
                    maps.put("mymoney", money);
                    XUtilsHelper.getInstance().post("app/submitRefund.htm", maps,new XUtilsHelper.XCallBack(){

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
                                    finish();
                                    Intent i =  new Intent(getApplicationContext(), RefundMoneyTwoActivity.class);
                                    i.putExtra("refundId", refundid);
                                    i.putExtra("orderId", orderid);
                                    i.putExtra("orderDtlId", orderdtlid);
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

    @Event(R.id.iv_close_uploadImg)
    private void closeclick(View v){
        iv_uploadImg.setImageBitmap(null);
        iv_uploadImg.setBackgroundResource(R.drawable.mall_upload_common);
        iv_close_uploadImg.setVisibility(View.INVISIBLE);
        fileurl="";
    }

    @Event(value=R.id.iv_uploadImg,type=View.OnTouchListener.class)
    private boolean uploadImg(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            if(mcd==null){
                mcd=new MyConfirmDialog(RefundMoneyOneActivity.this, "上传图片", "拍照上传", "本地上传");
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
                        progressDialog.hide();
                        try{
                            JSONObject res = FormatUtil.toJSONObject(result);
                            if(res != null){
                                if(res.get("d").equals("n")){
                                    CommonUtil.alter("图片上传失败");
                                }
                                else{
                                    fileurl=res.getString("fileUrl");
                                    iv_close_uploadImg.setVisibility(View.VISIBLE);
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
                bitmap =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
                iv_uploadImg.setImageBitmap(bitmap);
                Map<String, String> maps = new HashMap<String, String>();
                maps.put("fileUploadeFileName", TEMP_IMAGE_PATH1.substring(TEMP_IMAGE_PATH1.lastIndexOf("/")+1));
                maps.put("pathType","company");
                Map<String, File> file = new HashMap<String, File>();
                file.put("fileUploade",new File(TEMP_IMAGE_PATH1));
                XUtilsHelper.getInstance().upLoadFile("fileUploadOkJson.htm", maps, file, new XUtilsHelper.XCallBack() {

                    @Override
                    public void onResponse(String result) {
                        progressDialog.hide();
                        try{
                            JSONObject res = FormatUtil.toJSONObject(result);
                            if(res != null){
                                if(res.get("d").equals("n")){
                                    CommonUtil.alter("图片上传失败");
                                }
                                else{
                                    fileurl=res.getString("fileUrl");
                                    iv_close_uploadImg.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        catch(Exception e){e.printStackTrace();}
                    }
                });
            }
        }
    }
}
