package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.CommonDialog;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.ImageFactory;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyConfirmDialog;
import com.example.administrator.jymall.view.XListView;
import com.example.administrator.jymall.view.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_order_appraise)
public class OrderAppraiseActivity extends TopActivity implements IXListViewListener{

    private String id; //订单ID
    @ViewInject(R.id.btn_pay)
    private Button btn_pay;

    private JSONObject order;
    private JSONArray orderDtls;
    private String appraiseJsonArray;

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;
    @ViewInject(R.id.listtv)
    private TextView listtv;

    @ViewInject(R.id.cb_hideName)
    private CheckBox cb_hideName;

    @ViewInject(R.id.rb_package)
    private RatingBar rb_package;

    @ViewInject(R.id.rb_sendGoods)
    private RatingBar rb_sendGoods;

    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();

    private String packageLevel;
    private String transLevel;
    private String skey;
    private String showName="0";
    private Handler mHandler;
    private int start = 1;
    private int orderDtlCount = 0;

    private String TEMP_IMAGE_PATH;
    private String TEMP_IMAGE_PATH1= Environment.getExternalStorageDirectory().getPath()+"/temp1.png";
    private Bitmap bitmap1 = null;
    private Bitmap bitmap2 = null;
    private Bitmap bitmap3 = null;
    private MyConfirmDialog mcd1 = null;
    private MyConfirmDialog mcd2 = null;
    private MyConfirmDialog mcd3 = null;

    private int TEMP_PIC_ID;

    SimpleAdapter sap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("订单评价");
        progressDialog.hide();

        Intent i = this.getIntent();
        id = i.getStringExtra("id");
        skey=super.serverKey;
        sap = new InfoSimpleAdapter(OrderAppraiseActivity.this, dateMaps,
                R.layout.listview_order_appraise,
                new String[]{"proName"},
                new int[]{R.id.tv_proName});
        listViewAll.setAdapter(sap);
        listViewAll.setPullLoadEnable(true);
        listViewAll.setXListViewListener(this);
        getData(true,true);
        mHandler = new Handler();

        cb_hideName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TOakDO Auto-generated method stub
                checkShowName(arg1);
            }
        });
    }

    private void getData(final boolean isShow,final boolean flag){
        dateMaps.clear();
        if(isShow){
            progressDialog.show();
        }
        if(flag){
            start = 1;
        }
        listtv.setVisibility(View.GONE);
        listViewAll.setPullLoadEnable(false);

        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("currentPage", ""+start);
        maps.put("OrderId", id);
        XUtilsHelper.getInstance().post("app/mallOrderAppraise.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                progressDialog.hide();
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    order = res.getJSONObject("order");
                    orderDtls = order.getJSONArray("orderDtls");
                    orderDtlCount=orderDtls.length();

                    for(int j=0;j<orderDtls.length();j++){
                        Map<String, Object> dateMap1 = new HashMap<String, Object>();
                        dateMap1.put("id", orderDtls.getJSONObject(j).get("ID"));
                        dateMap1.put("proName", orderDtls.getJSONObject(j).get("proName"));
                        dateMap1.put("proID", orderDtls.getJSONObject(j).get("proID"));
                        dateMap1.put("proImgPath", orderDtls.getJSONObject(j).get("proImgPath"));
                        dateMaps.add(dateMap1);
                    }
                    sap.notifyDataSetChanged();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    public class InfoSimpleAdapter  extends SimpleAdapter {
        private LayoutInflater mInflater;
        private List<Map<String, Object>> mdata;

        public InfoSimpleAdapter(Context context,
                                 List<? extends Map<String, ?>> data, int resource, String[] from,
                                 int[] to) {
            super(context, data, resource, from, to);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mdata = (List<Map<String, Object>>) data;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try{
                if(convertView==null){
                    convertView=mInflater.inflate(R.layout.listview_order_appraise, null);
                }
                ImageView img_proImgPath = (ImageView)convertView.findViewById(R.id.img_proImgPath);
                TextView tv_proName = (TextView)convertView.findViewById(R.id.tv_proName);
                EditText et_remark = (EditText)convertView.findViewById(R.id.et_remark);
                final ImageView iv_pic1 = (ImageView)convertView.findViewById(R.id.iv_pic1);
                final ImageView iv_pic2 = (ImageView)convertView.findViewById(R.id.iv_pic2);
                final ImageView iv_pic3 = (ImageView)convertView.findViewById(R.id.iv_pic3);

                img_proImgPath.setBackgroundResource(0);
                XUtilsHelper.getInstance().bindCommonImage(img_proImgPath, mdata.get(position).get("proImgPath").toString(), true);

                String proName = mdata.get(position).get("proName").toString();
                tv_proName.setText(proName);

                iv_pic1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == event.ACTION_UP) {
                            TEMP_PIC_ID=v.getId();
                            if(mcd1==null){
                                mcd1=new MyConfirmDialog(OrderAppraiseActivity.this, "上传照片", "拍照上传", "本地上传");
                                mcd1.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
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
                            return false;
                        }
                        return true;
                    }
                });


                iv_pic2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == event.ACTION_UP) {
                            TEMP_PIC_ID=v.getId();
                            if(mcd2==null){
                                mcd2=new MyConfirmDialog(OrderAppraiseActivity.this, "上传照片", "拍照上传", "本地上传");
                                mcd2.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
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
                            return false;
                        }
                        return true;
                    }
                });
                iv_pic3.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == event.ACTION_UP) {
                            TEMP_PIC_ID=v.getId();
                            if(mcd3==null){
                                mcd3=new MyConfirmDialog(OrderAppraiseActivity.this, "上传照片", "拍照上传", "本地上传");
                                mcd3.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
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
                            return false;
                        }
                        return true;
                    }
                });
            }
            catch(Exception e){
                Log.v("PRO", e.getMessage());
            }
            return super.getView(position, convertView, parent);
        }
    }

    @Event(value=R.id.btn_submit)
    private void submitClick(View v){
        try {
            getCommentJson();
            packageLevel = FormatUtil.toString(rb_package.getRating());
            transLevel = FormatUtil.toString(rb_sendGoods.getRating());
            if (!FormatUtil.isNoEmpty(packageLevel)) {
                CommonUtil.alter("请评价商品包装！");
                return;
            } else if (!FormatUtil.isNoEmpty(transLevel)) {
                CommonUtil.alter("请评价送货速度！");
                return;
            }

            new CommonDialog(OrderAppraiseActivity.this, R.style.dialog, "确定提交？", new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        progressDialog.show();
                        Map<String, String> maps = new HashMap<String, String>();
                        maps.put("serverKey", skey);
                        maps.put("id", id);
                        maps.put("packageLevel", packageLevel);
                        maps.put("transLevel", transLevel);
                        maps.put("showName", showName);
                        maps.put("appraiseJsonArray", appraiseJsonArray);
                        XUtilsHelper.getInstance().post("app/saveMallOrderAppraise.htm", maps, new XUtilsHelper.XCallBack() {

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
                                        Intent i = new Intent(getApplicationContext(), AppraiseOkActivity.class);
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
        catch (Exception e){
            e.printStackTrace();
            Log.i("APPRAISE", "Exception");
        }
    }

    private void getCommentJson() throws IOException{
        appraiseJsonArray="[";
        int count=0;
        for(int i=0; i<listViewAll.getChildCount();i++){
            String pic1="";
            String pic2="";
            String pic3="";
            String pic4="";
            String pic5="";
            String productLevel="";
            String remark="";
            if(listViewAll.getChildAt(i).getId() == R.id.ll_id) {
                LinearLayout layout = (LinearLayout) listViewAll.getChildAt(i);
                RatingBar rb_productLevel = layout.findViewById(R.id.rb_productLevel);
                EditText et_remark = layout.findViewById(R.id.et_remark);
                ImageView iv_pic1=layout.findViewById(R.id.iv_pic1);
                ImageView iv_pic2=layout.findViewById(R.id.iv_pic2);
                ImageView iv_pic3=layout.findViewById(R.id.iv_pic3);
                if (FormatUtil.isNoEmpty(et_remark)) {
                    if (FormatUtil.isNoEmpty(et_remark.getText())) {
                        remark = java.net.URLEncoder.encode(FormatUtil.toString(et_remark.getText()), "UTF-8");
                    }
                }

                if(FormatUtil.isNoEmpty(FormatUtil.toString(iv_pic1.getTag()))){
                    pic1=iv_pic1.getTag().toString();
                }
                if(FormatUtil.isNoEmpty(FormatUtil.toString(iv_pic2.getTag()))){
                    pic2=iv_pic2.getTag().toString();
                }
                if(FormatUtil.isNoEmpty(FormatUtil.toString(iv_pic3.getTag()))){
                    pic3=iv_pic3.getTag().toString();
                }

                productLevel = FormatUtil.toString(rb_productLevel.getRating());
                appraiseJsonArray += "{\"productid\":\"" + dateMaps.get(count).get("proID").toString() + "\",\"productLevel\":\"" + productLevel + "\",\"remark\":\"" + remark + "\",\"pic1\":\"" + pic1 + "\",\"pic2\":\"" + pic2 + "\",\"pic3\":\"" + pic3 + "\",\"pic4\":\"" + pic4 + "\",\"pic5\":\"" + pic5 + "\"},";
                count++;
                /*if (i == listViewAll.getChildCount() - 1) {
                    appraiseJsonArray += "{\"productid\":\"" + dateMaps.get(i).get("proID").toString() + "\",\"productLevel\":\"" + productLevel + "\",\"remark\":\"" + remark + "\",\"pic1\":\"" + pic1 + "\",\"pic2\":\"" + pic2 + "\",\"pic3\":\"" + pic3 + "\",\"pic4\":\"" + pic4 + "\",\"pic5\":\"" + pic5 + "\"}";
                } else {
                    appraiseJsonArray += "{\"productid\":\"" + dateMaps.get(i).get("proID").toString() + "\",\"productLevel\":\"" + productLevel + "\",\"remark\":\"" + remark + "\",\"pic1\":\"" + pic1 + "\",\"pic2\":\"" + pic2 + "\",\"pic3\":\"" + pic3 + "\",\"pic4\":\"" + pic4 + "\",\"pic5\":\"" + pic5 + "\"},";
                }*/
            }

        }
        appraiseJsonArray = appraiseJsonArray.substring(0, appraiseJsonArray.length()-1);
        appraiseJsonArray+="]";
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            //上传照片
            if(requestCode==11&&data!=null){
                progressDialog.show();
                mcd1.dismiss();
                Uri uri = data.getData();

                //如果不释放的话，不断取图片，将会内存不够
                if(bitmap1 != null && !bitmap1.isRecycled()){
                    bitmap1.recycle();
                    bitmap1 = null;
                }

                TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap1 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);

                //iv_pic1.setImageBitmap(bitmap1);
                final ImageView iv_pic1=(ImageView) findViewById(FormatUtil.toInt(TEMP_PIC_ID));
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
                if(bitmap1 != null && !bitmap1.isRecycled()){
                    bitmap1.recycle();
                    bitmap1 = null;
                }
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap1 = BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);

                final ImageView iv_pic1=(ImageView) findViewById(FormatUtil.toInt(TEMP_PIC_ID));
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
                if(bitmap2 != null && !bitmap2.isRecycled()){
                    bitmap2.recycle();
                    bitmap2 = null;
                }
                TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap2 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);

                //iv_pic1.setImageBitmap(bitmap1);
                final ImageView iv_pic1=(ImageView) findViewById(FormatUtil.toInt(TEMP_PIC_ID));
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
                if(bitmap2 != null && !bitmap2.isRecycled()){
                    bitmap2.recycle();
                    bitmap2 = null;
                }
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap2 = BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);

                final ImageView iv_pic1=(ImageView) findViewById(FormatUtil.toInt(TEMP_PIC_ID));
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
                if(bitmap3 != null && !bitmap3.isRecycled()){
                    bitmap3.recycle();
                    bitmap3 = null;
                }
                TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap3 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);

                //iv_pic1.setImageBitmap(bitmap1);
                final ImageView iv_pic1=(ImageView) findViewById(FormatUtil.toInt(TEMP_PIC_ID));
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

                final ImageView iv_pic1=(ImageView) findViewById(FormatUtil.toInt(TEMP_PIC_ID));
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

    private void checkShowName(boolean f){
        String isCheck = f?"1":"0";
        if(isCheck.equals("0")){
            cb_hideName.setChecked(false);
            showName="0";
        }
        else{
            cb_hideName.setChecked(true);
            showName="1";
        }
    }

    private void onLoad() {
        listViewAll.stopRefresh();
        listViewAll.stopLoadMore();
        listViewAll.setRefreshTime(DateUtil.DateToString(new Date(), DateStyle.HH_MM_SS));
    }
    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start = 1;
                getData(true,true);
                onLoad();
            }
        }, 1);

    }
    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start++;
                getData(true,false);
                onLoad();
            }
        }, 1);

    }
}
