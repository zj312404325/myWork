package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.example.administrator.jymall.common.CommonDialog;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.ImageFactory;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyConfirmDialog;
import com.example.administrator.jymall.view.MyImageView;
import com.example.administrator.jymall.view.XListView;

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

@ContentView(R.layout.activity_commit_ordermatch)
public class CommitOrderMatchActivity extends TopActivity {

    private String orderMatchJsonArray;

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;

    @ViewInject(R.id.ll_addView)
    private LinearLayout ll_addView;

    @ViewInject(R.id.top_add)
    private ImageView top_add;

    private String skey;
    private String type;
    private int errorTimes=0;

    SimpleAdapter sap;

    //上传图片
    private String TEMP_IMAGE_PATH;
    private String TEMP_IMAGE_PATH1= Environment.getExternalStorageDirectory().getPath()+"/temp1.png";

    //记录Layout 行数
    private int TEMP_LL_COUNT=0;

    //记录imageview序号
    private int TEMP_IMAGE_COUNT=1;

    private List<Bitmap> bitmapList1 =new ArrayList<Bitmap>() ;
    private List<Bitmap> bitmapList2 =new ArrayList<Bitmap>();
    private List<Bitmap> bitmapList3 =new ArrayList<Bitmap>();

    private Bitmap bitmap1 = null;
    private Bitmap bitmap2 = null;
    private Bitmap bitmap3 = null;

    private MyConfirmDialog mcd1 = null;
    private MyConfirmDialog mcd2 = null;
    private MyConfirmDialog mcd3 = null;

    private int TEMP_PIC_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_ordermatch);
        x.view().inject(this);
        super.title.setText("定制件一键配");
        top_add.setVisibility(View.VISIBLE);
        progressDialog.hide();
        skey=super.serverKey;
        //默认添加一个Item
        addViewItem(null);
    }

    @Event(value=R.id.btn_submit)
    private void submitClick(View v){
        try {
            type = "1";
            getFastMatchJson();
            if (errorTimes == 0) {
                new CommonDialog(CommitOrderMatchActivity.this, R.style.dialog, "确定提交？", new CommonDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            progressDialog.show();
                            Map<String, String> maps = new HashMap<String, String>();
                            maps.put("serverKey", skey);
                            maps.put("type", type);
                            maps.put("orderMatchJsonArray", orderMatchJsonArray);
                            XUtilsHelper.getInstance().post("app/saveOrderMatch.htm", maps, new XUtilsHelper.XCallBack() {

                                @SuppressLint("NewApi")
                                @Override
                                public void onResponse(String result) {
                                    progressDialog.hide();
                                    JSONObject res;
                                    try {
                                        res = new JSONObject(result);
                                        setServerKey(res.get("serverKey").toString());
                                        if (res.get("d").equals("n")) {
                                            CommonUtil.alter(res.get("msg").toString());
                                        } else {
                                            if (res.get("import_status").equals("n")) {
                                                CommonUtil.alter(res.get("import_info").toString());
                                            } else {
                                                Intent i = new Intent(getApplicationContext(), CommitOrderMatchOkActivity.class);
                                                startActivity(i);
                                            }
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
            errorTimes=0;
        }
        catch (Exception e){
            e.printStackTrace();
            Log.i("APPRAISE", "Exception");
        }
    }

    @Event(value=R.id.top_add)
    private void addClick(View v){
        addViewItem(v);
    }

    /**
     * Item排序
     */
    private void sortViewItem() {
        //获取LinearLayout里面所有的view
        for (int i = 0; i < ll_addView.getChildCount(); i++) {
            bitmapList1.add(null);
            bitmapList2.add(null);
            bitmapList3.add(null);
            final int count=i;
            final View childAt = ll_addView.getChildAt(i);
            final Button btn_delete = (Button) childAt.findViewById(R.id.btn_delete);
            final MyImageView iv_pic1 = (MyImageView) childAt.findViewById(R.id.iv_pic1);
            final MyImageView iv_pic2 = (MyImageView) childAt.findViewById(R.id.iv_pic2);
            final MyImageView iv_pic3 = (MyImageView) childAt.findViewById(R.id.iv_pic3);

            btn_delete.setTag("remove");//设置删除标记
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //从LinearLayout容器中删除当前点击到的ViewItem
                    ll_addView.removeView(childAt);
                }
            });

            iv_pic1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TEMP_LL_COUNT=count;
                    TEMP_IMAGE_COUNT=1;
                    final int viewId=iv_pic1.getId();
                    if(mcd1==null){
                        TEMP_PIC_ID=viewId;
                        mcd1=new MyConfirmDialog(CommitOrderMatchActivity.this, "上传照片", "拍照上传", "本地上传");
                        mcd1.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                            @Override
                            public void doConfirm() {
                                TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";
                                Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                                startActivityForResult(intent1,12);
                            }
                            @Override
                            public void doCancel() {
                                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("image/*");
                                startActivityForResult(intent, 11);
                            }
                        });
                    }
                    mcd1.show();
                }
            });

            iv_pic2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TEMP_LL_COUNT=count;
                    TEMP_IMAGE_COUNT=2;
                    final int viewId=iv_pic2.getId();
                    if(mcd2==null){
                        TEMP_PIC_ID=viewId;
                        mcd2=new MyConfirmDialog(CommitOrderMatchActivity.this, "上传照片", "拍照上传", "本地上传");
                        mcd2.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                            @Override
                            public void doConfirm() {
                                TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";
                                Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                                startActivityForResult(intent1,22);
                            }
                            @Override
                            public void doCancel() {
                                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("image/*");
                                startActivityForResult(intent, 21);
                            }
                        });
                    }
                    mcd2.show();
                }
            });

            iv_pic3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TEMP_LL_COUNT=count;
                    TEMP_IMAGE_COUNT=3;
                    final int viewId=iv_pic3.getId();
                    if(mcd3==null){
                        TEMP_PIC_ID=viewId;
                        mcd3=new MyConfirmDialog(CommitOrderMatchActivity.this, "上传照片", "拍照上传", "本地上传");
                        mcd3.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                            @Override
                            public void doConfirm() {
                                TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";
                                Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                                startActivityForResult(intent1,32);
                            }
                            @Override
                            public void doCancel() {
                                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("image/*");
                                startActivityForResult(intent, 31);
                            }
                        });
                    }
                    mcd3.show();
                }
            });

            if (i == (ll_addView.getChildCount()-1)) {
                btn_delete.setVisibility(View.GONE);
            }
            else{
                btn_delete.setVisibility(View.VISIBLE);
            }
        }
    }

    //添加ViewItem
    private void addViewItem(View view) {
        if (ll_addView.getChildCount() == 0) {//如果一个都没有，就添加一个
            View newView = View.inflate(this, R.layout.item_ordermatch, null);
            top_add.setTag("add");
            ll_addView.addView(newView);
            sortViewItem();
        }
        else{//如果有一个以上的Item,点击为添加的Item则添加
            View newView = View.inflate(this, R.layout.item_ordermatch, null);
            ll_addView.addView(newView);
            sortViewItem();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            //上传照片
            RelativeLayout layout =(RelativeLayout) ll_addView.getChildAt(TEMP_LL_COUNT);
            MyImageView iv_pic=null;
            if(TEMP_IMAGE_COUNT==1) {
                iv_pic = layout.findViewById(R.id.iv_pic1);
            }
            else if(TEMP_IMAGE_COUNT==2) {
                iv_pic = layout.findViewById(R.id.iv_pic2);
            }
            else if(TEMP_IMAGE_COUNT==3) {
                iv_pic = layout.findViewById(R.id.iv_pic3);
            }
            final MyImageView iv_pic1=iv_pic;
            if(requestCode==11&&data!=null){
                progressDialog.show();
                mcd1.dismiss();
                Uri uri = data.getData();

                //如果不释放的话，不断取图片，将会内存不够
                if(bitmapList1.size()>0){
                    bitmap1 = bitmapList1.get(TEMP_LL_COUNT);
                }
                if(bitmap1 != null && !bitmap1.isRecycled()){
                    bitmap1.recycle();
                    bitmap1 = null;
                }

                TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap1 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
                bitmapList1.set(TEMP_LL_COUNT,bitmap1);

                //iv_pic1.setImageBitmap(bitmap1);
                iv_pic1.setImageBitmap(bitmap1);

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
                                    iv_pic1.setTag(res.getString("fileUrl"));
                                }
                            }
                        }
                        catch(Exception e){e.printStackTrace();}
                    }
                });
            }
            else if(requestCode==12){
                progressDialog.show();
                mcd1.dismiss();
                //如果不释放的话，不断取图片，将会内存不够
                if(bitmapList1.size()>0){
                    bitmap1 = bitmapList1.get(TEMP_LL_COUNT);
                }
                if(bitmap1 != null && !bitmap1.isRecycled()){
                    bitmap1.recycle();
                    bitmap1 = null;
                }
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap1 = BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
                bitmapList1.set(TEMP_LL_COUNT,bitmap1);

                iv_pic1.setImageBitmap(bitmap1);

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
                                    //pic1=res.getString("fileUrl");
                                    iv_pic1.setTag(res.getString("fileUrl"));
                                }
                            }
                        }
                        catch(Exception e){e.printStackTrace();}
                    }
                });
            }
            else if(requestCode==21){
                progressDialog.show();
                mcd2.dismiss();
                Uri uri = data.getData();

                //如果不释放的话，不断取图片，将会内存不够
                if(bitmapList2.size()>0){
                    bitmap2 = bitmapList2.get(TEMP_LL_COUNT);
                }
                if(bitmap2 != null && !bitmap2.isRecycled()){
                    bitmap2.recycle();
                    bitmap2 = null;
                }
                TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap2 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
                bitmapList2.set(TEMP_LL_COUNT,bitmap2);

                //iv_pic1.setImageBitmap(bitmap1);
                iv_pic1.setImageBitmap(bitmap2);

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
                                    iv_pic1.setTag(res.getString("fileUrl"));
                                }
                            }
                        }
                        catch(Exception e){e.printStackTrace();}
                    }
                });
            }
            else if(requestCode==22){
                progressDialog.show();
                mcd2.dismiss();
                //如果不释放的话，不断取图片，将会内存不够
                if(bitmapList2.size()>0){
                    bitmap2 = bitmapList2.get(TEMP_LL_COUNT);
                }
                if(bitmap2 != null && !bitmap2.isRecycled()){
                    bitmap2.recycle();
                    bitmap2 = null;
                }
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap2 = BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
                bitmapList2.set(TEMP_LL_COUNT,bitmap2);

                iv_pic1.setImageBitmap(bitmap2);

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
                                    //pic1=res.getString("fileUrl");
                                    iv_pic1.setTag(res.getString("fileUrl"));
                                }
                            }
                        }
                        catch(Exception e){e.printStackTrace();}
                    }
                });
            }
            else if(requestCode==31){
                progressDialog.show();
                mcd3.dismiss();
                Uri uri = data.getData();

                //如果不释放的话，不断取图片，将会内存不够
                if(bitmapList3.size()>0){
                    bitmap3 = bitmapList3.get(TEMP_LL_COUNT);
                }
                if(bitmap3 != null && !bitmap3.isRecycled()){
                    bitmap3.recycle();
                    bitmap3 = null;
                }
                TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap3 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
                bitmapList3.set(TEMP_LL_COUNT,bitmap3);

                //iv_pic1.setImageBitmap(bitmap1);
                iv_pic1.setImageBitmap(bitmap3);

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
                                    iv_pic1.setTag(res.getString("fileUrl"));
                                }
                            }
                        }
                        catch(Exception e){e.printStackTrace();}
                    }
                });
            }
            else if(requestCode==32){
                progressDialog.show();
                mcd3.dismiss();
                //如果不释放的话，不断取图片，将会内存不够
                if(bitmap3 != null && !bitmap3.isRecycled()){
                    bitmap3.recycle();
                    bitmap3 = null;
                }
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap3 = BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);

                iv_pic1.setImageBitmap(bitmap3);

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
                                    //pic1=res.getString("fileUrl");
                                    iv_pic1.setTag(res.getString("fileUrl"));
                                }
                            }
                        }
                        catch(Exception e){e.printStackTrace();}
                    }
                });
            }
        }
    }

    private void getFastMatchJson(){
        orderMatchJsonArray="[";
        for(int i=0; i<ll_addView.getChildCount();i++){
            String proName="";
            String brand="";
            String proquantity="";
            String spec="";
            String quantity="";
            String unit="";
            String functionReq="";
            String parameter="";
            String remark="";

            String pic1="";
            String pic2="";
            String pic3="";
            String pic4="";
            String pic5="";

            RelativeLayout layout =(RelativeLayout) ll_addView.getChildAt(i);
            EditText et_proName=layout.findViewById(R.id.et_proName);
            EditText et_brand=layout.findViewById(R.id.et_brand);
            EditText et_proQuality=layout.findViewById(R.id.et_proQuality);
            EditText et_spec=layout.findViewById(R.id.et_spec);
            EditText et_quantity=layout.findViewById(R.id.et_quantity);
            EditText et_unit=layout.findViewById(R.id.et_unit);
            EditText et_functionReq=layout.findViewById(R.id.et_functionReq);
            EditText et_parameter=layout.findViewById(R.id.et_parameter);
            EditText et_remark=layout.findViewById(R.id.et_remark);
            MyImageView iv_pic1=layout.findViewById(R.id.iv_pic1);
            MyImageView iv_pic2=layout.findViewById(R.id.iv_pic2);
            MyImageView iv_pic3=layout.findViewById(R.id.iv_pic3);

            proName=et_proName.getText().toString();
            brand=et_brand.getText().toString();
            proquantity=et_proQuality.getText().toString();
            spec=et_spec.getText().toString();
            quantity=et_quantity.getText().toString();
            unit=et_unit.getText().toString();
            functionReq=et_functionReq.getText().toString();
            parameter=et_parameter.getText().toString();
            remark=et_remark.getText().toString();

            //非空校验
            if(!FormatUtil.isNoEmpty(FormatUtil.toString(proName))){
                CommonUtil.alter("商品名称不能为空！");
                errorTimes++;
                return;
            }
            else if(FormatUtil.getStringLength(FormatUtil.toString(proName))>25){
                CommonUtil.alter("商品名称过长！");
                errorTimes++;
                return;
            }
            if(!FormatUtil.isNoEmpty(FormatUtil.toString(proquantity))){
                CommonUtil.alter("材质不能为空！");
                errorTimes++;
                return;
            }
            else if(FormatUtil.getStringLength(FormatUtil.toString(proquantity))>25){
                CommonUtil.alter("材质过长！");
                errorTimes++;
                return;
            }
            if(!FormatUtil.isNoEmpty(FormatUtil.toString(spec))){
                CommonUtil.alter("规格不能为空！");
                errorTimes++;
                return;
            }
            else if(FormatUtil.getStringLength(FormatUtil.toString(spec))>25){
                CommonUtil.alter("规格过长！");
                errorTimes++;
                return;
            }
            if(!FormatUtil.isNoEmpty(FormatUtil.toString(quantity))){
                CommonUtil.alter("数量不能为空！");
                errorTimes++;
                return;
            }
            if(!FormatUtil.isNoEmpty(FormatUtil.toString(unit))){
                CommonUtil.alter("单位不能为空！");
                errorTimes++;
                return;
            }
            else if(FormatUtil.getStringLength(FormatUtil.toString(unit))>25){
                CommonUtil.alter("单位过长！");
                errorTimes++;
                return;
            }
            if(!FormatUtil.isNoEmpty(FormatUtil.toString(parameter))){
                CommonUtil.alter("参数不能为空！");
                errorTimes++;
                return;
            }
            else if(FormatUtil.getStringLength(FormatUtil.toString(parameter))>25){
                CommonUtil.alter("参数过长！");
                errorTimes++;
                return;
            }

            //非必填项校验
            if(FormatUtil.getStringLength(FormatUtil.toString(brand))>25){
                CommonUtil.alter("品牌过长！");
                errorTimes++;
                return;
            }
            if(FormatUtil.getStringLength(FormatUtil.toString(functionReq))>25){
                CommonUtil.alter("性能要求过长！");
                errorTimes++;
                return;
            }
            if(FormatUtil.getStringLength(FormatUtil.toString(remark))>50){
                CommonUtil.alter("备注过长！");
                errorTimes++;
                return;
            }
            //

            if(FormatUtil.isNoEmpty(FormatUtil.toString(iv_pic1.getTag()))){
                pic1=iv_pic1.getTag().toString();
            }
            if(FormatUtil.isNoEmpty(FormatUtil.toString(iv_pic2.getTag()))){
                pic2=iv_pic2.getTag().toString();
            }
            if(FormatUtil.isNoEmpty(FormatUtil.toString(iv_pic3.getTag()))){
                pic3=iv_pic3.getTag().toString();
            }

            if(i==ll_addView.getChildCount()-1){
                orderMatchJsonArray += "{\"proName\":\""+ proName +"\",\"brand\":\""+ brand +"\",\"proQuality\":\""+ proquantity +"\",\"proSpec\":\""+ spec +"\",\"quantity\":\""+ quantity +"\",\"unit\":\""+ unit +"\",\"functionReq\":\""+ functionReq +"\",\"parameter\":\""+ parameter +"\",\"remark\":\""+ remark +"\",\"pic1\":\""+ pic1 +"\",\"pic2\":\""+ pic2 +"\",\"pic3\":\""+ pic3 +"\"}";
            }
            else{
                orderMatchJsonArray += "{\"proName\":\""+ proName +"\",\"brand\":\""+ brand +"\",\"proQuality\":\""+ proquantity +"\",\"proSpec\":\""+ spec +"\",\"quantity\":\""+ quantity +"\",\"unit\":\""+ unit +"\",\"functionReq\":\""+ functionReq +"\",\"parameter\":\""+ parameter +"\",\"remark\":\""+ remark +"\",\"pic1\":\""+ pic1 +"\",\"pic2\":\""+ pic2 +"\",\"pic3\":\""+ pic3 +"\"},";
            }
        }
        orderMatchJsonArray+="]";
    }

}
