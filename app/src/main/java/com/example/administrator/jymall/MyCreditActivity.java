package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.SpinnerPopWindow;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.DensityUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.ImageFactory;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyConfirmDialog;
import com.example.administrator.jymall.view.MyGridView;

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

@ContentView(R.layout.activity_credit_state)
public class MyCreditActivity extends TopActivity {
    @ViewInject(R.id.ll_step1)
    private LinearLayout ll_step1;
    @ViewInject(R.id.ll_step2)
    private LinearLayout ll_step2;
    @ViewInject(R.id.ll_step3)
    private LinearLayout ll_step3;
    @ViewInject(R.id.ll_step4)
    private LinearLayout ll_step4;
    @ViewInject(R.id.ll_step_ok)
    private LinearLayout ll_step_ok;
    @ViewInject(R.id.ll_step_refuse)
    private LinearLayout ll_step_refuse;
    @ViewInject(R.id.ll_selectZone)
    private LinearLayout ll_selectZone;
    @ViewInject(R.id.tv_showFax)
    private TextView tv_showFax;
    @ViewInject(R.id.et_zone)
    private EditText et_zone;

    @ViewInject(R.id.et_compAddress)
    private EditText et_compAddress;
    @ViewInject(R.id.et_commonGoods)
    private EditText et_commonGoods;
    @ViewInject(R.id.tv_compName)
    private TextView tv_compName;
    @ViewInject(R.id.ll_monthly)
    private LinearLayout ll_monthly;
    @ViewInject(R.id.ll_yearly)
    private LinearLayout ll_yearly;
    @ViewInject(R.id.iv_monthly)
    private ImageView iv_monthly;
    @ViewInject(R.id.iv_yearly)
    private ImageView iv_yearly;
    @ViewInject(R.id.iv_customSeal)
    private ImageView iv_customSeal;

    @ViewInject(R.id.iv_officeImage)
    private ImageView iv_officeImage;

    @ViewInject(R.id.iv_close_officeImage)
    private ImageView iv_close_officeImage;

    @ViewInject(R.id.et_peopleCount)
    private EditText et_peopleCount;

    @ViewInject(R.id.tv_title)
    private TextView tv_title;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private String id="";
    private String pic1="";
    private String pic2="";
    private String pic3="";
    private String officeImage="";
    private String zoneid="";
    private String compType="";
    private String compName="";
    private String personNumber="";

    private String sealurl="";
    private String paytype="";
    private String address="";
    private String products="";

    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
    public List<Map<String, Object>> sealMaps= new ArrayList<Map<String, Object>>();


    private List<String> list;
    private TextView tvValue;
    private SpinnerPopWindow<String> mSpinerPopWindow;

    //上传图片
    private String TEMP_IMAGE_PATH;
    private String TEMP_IMAGE_PATH1= Environment.getExternalStorageDirectory().getPath()+"/temp1.png";

    private Bitmap bitmap1 = null;
    private MyConfirmDialog mcd1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("信用支付状态");
        tv_title.setText(Html.fromHtml("拥有VIP信用支付特权的用户，可享受订单<font color=\"#1a3688\">月度结算</font>或<font color=\"#1a3688\">年度结算</font>"));
        progressDialog.hide();
        iv_close_officeImage.setVisibility(View.INVISIBLE);
        getState();

        initData();
        tvValue = (TextView) findViewById(R.id.tv_value);
        tvValue.setOnClickListener(clickListener);
        mSpinerPopWindow = new SpinnerPopWindow<String>(this, list,itemClickListener);
        mSpinerPopWindow.setOnDismissListener(dismissListener);
    }

    /**
     * 监听popupwindow取消
     */
    private PopupWindow.OnDismissListener dismissListener=new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            setTextImage(R.drawable.icon_down);
        }
    };

    /**
     * popupwindow显示的ListView的item点击事件
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            tvValue.setText(list.get(position));
            compType=list.get(position);
            //Toast.makeText(MyScoreActivity.this, "点击了:" + list.get(position),Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 显示PopupWindow
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_value:
                    mSpinerPopWindow.setWidth(tvValue.getWidth());
                    mSpinerPopWindow.showAsDropDown(tvValue);
                    setTextImage(R.drawable.icon_up);
                    break;
            }
        }
    };

    /**
     * 初始化数据
     */
    private void initData() {
        list = new ArrayList<String>();
        list.add("生产企业" );
        list.add("加工企业" );
        list.add("仓储企业" );
        list.add("贸易商" );
        list.add("事业单位" );
    }

    /**
     * 给TextView右边设置图片
     * @param resId
     */
    private void setTextImage(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
        tvValue.setCompoundDrawables(null, null, drawable, null);
    }

    private void getState(){
        dateMaps.clear();
        sealMaps.clear();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        disableInit();
        XUtilsHelper.getInstance().post("app/mallCreditSupply.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    JSONArray servicelist = (JSONArray)res.get("servicelist");
                    JSONArray sealList = (JSONArray)res.get("sealList");
                    JSONObject credit;
                    String isEmpty = res.get("isEmpty").toString();
                    compName=res.get("compName").toString();
                    tv_compName.setText(compName);

                    if(isEmpty.equals("n")){
                        credit = (JSONObject)res.get("credit");
                        et_compAddress.setText(credit.getString("address"));
                        et_commonGoods.setText(credit.getString("products"));
                        String type=credit.getString("paytype");

                        id=credit.getString("id");
                        if(credit.getString("status").equals("0")){
                            ll_step3.setVisibility(View.VISIBLE);
                        }
                        if(credit.getString("status").equals("2")){
                            ll_step_refuse.setVisibility(View.VISIBLE);
                        }
                        if(credit.getString("status").equals("1") && !credit.getString("ischecked").equals("1")){
                            ll_step4.setVisibility(View.VISIBLE);
                        }
                        if(credit.getString("status").equals("1") && credit.getString("ischecked").equals("1")){
                            ll_step_ok.setVisibility(View.VISIBLE);
                        }

                        if(FormatUtil.isNoEmpty(credit.getString("sealUrl"))){
                            String url=credit.getString("sealUrl");
                            XUtilsHelper.getInstance().bindUrlImage(iv_customSeal, url, true);
                            sealurl=url;
                        }
                        if(FormatUtil.isNoEmpty(type)){
                            if(type.equals("0")){
                                iv_monthly.setBackgroundResource(R.drawable.icon_radio_selected);
                                iv_yearly.setBackgroundResource(R.drawable.icon_radio_unselected);
                                iv_monthly.setTag("1");
                                iv_yearly.setTag("0");
                                paytype="0";
                            }
                            else if(type.equals("1")){
                                iv_monthly.setBackgroundResource(R.drawable.icon_radio_unselected);
                                iv_yearly.setBackgroundResource(R.drawable.icon_radio_selected);
                                iv_monthly.setTag("0");
                                iv_yearly.setTag("1");
                                paytype="1";
                            }
                            else{
                                iv_monthly.setBackgroundResource(R.drawable.icon_radio_unselected);
                                iv_yearly.setBackgroundResource(R.drawable.icon_radio_unselected);
                                iv_monthly.setTag("0");
                                iv_yearly.setTag("0");
                                paytype="";
                            }
                        }
                        else{
                            iv_monthly.setBackgroundResource(R.drawable.icon_radio_unselected);
                            iv_yearly.setBackgroundResource(R.drawable.icon_radio_unselected);
                            iv_monthly.setTag("0");
                            iv_yearly.setTag("0");
                            paytype="";
                        }
                    }
                    else{
                        ll_step1.setVisibility(View.VISIBLE);
                        et_compAddress.setText("");
                        et_commonGoods.setText("");
                        iv_monthly.setBackgroundResource(R.drawable.icon_radio_unselected);
                        iv_yearly.setBackgroundResource(R.drawable.icon_radio_unselected);
                        iv_monthly.setTag("0");
                        iv_yearly.setTag("0");
                        paytype="";
                    }

                    /*if(res.get("isEmpty").toString().equals("n")) {
                        id=credit.getString("id");
                        if(credit.getString("status").equals("0")){
                            ll_step3.setVisibility(View.VISIBLE);
                        }
                        if(credit.getString("status").equals("2")){
                            ll_step_refuse.setVisibility(View.VISIBLE);
                        }
                        if(credit.getString("status").equals("1") && !credit.getString("ischecked").equals("1")){
                            ll_step4.setVisibility(View.VISIBLE);
                        }
                        if(credit.getString("status").equals("1") && credit.getString("ischecked").equals("1")){
                            ll_step_ok.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        ll_step1.setVisibility(View.VISIBLE);
                    }*/

                    for(int i=0;i<servicelist.length();i++){
                        Map<String, Object> dateMap = new HashMap<String, Object>();
                        dateMap.put("id", servicelist.getJSONObject(i).get("id"));
                        dateMap.put("fax", servicelist.getJSONObject(i).get("fax"));
                        dateMap.put("name", servicelist.getJSONObject(i).get("name"));
                        dateMaps.add(dateMap);
                    }

                    if(res.get("sealstatus").toString().equals("y")) {
                        for(int i=0;i<sealList.length();i++){
                            Map<String, Object> dateMap = new HashMap<String, Object>();
                            dateMap.put("sealName", sealList.getJSONObject(i).get("sealName"));
                            dateMap.put("picUrl", sealList.getJSONObject(i).get("picUrl"));
                            sealMaps.add(dateMap);
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }

    @Event(R.id.iv_close_officeImage)
    private void closeclick(View v){
        iv_officeImage.setImageBitmap(null);
        iv_officeImage.setBackgroundResource(R.drawable.mall_upload_common);
        iv_close_officeImage.setVisibility(View.INVISIBLE);
        officeImage="";
    }

    @Event(R.id.submitbtn)
    private void submitclick(View v){
        subClick(v);
    }

    @Event(R.id.resubmitbtn)
    private void resubmitclick(View v){
        subClick(v);
    }

    @Event(R.id.submit)
    private void dosubmitclick(View v){
        pic1=officeImage;
        compType=tvValue.getText().toString();
        personNumber=et_peopleCount.getText().toString();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("id", id);
        maps.put("pic1", pic1);
        maps.put("pic2", pic2);
        maps.put("pic3", pic3);
        maps.put("compType", compType);
        maps.put("personNumber", personNumber);
        XUtilsHelper.getInstance().post("app/supplyMallCredit.htm", maps,new XUtilsHelper.XCallBack(){

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
                        CommonUtil.alter("申请成功，请等待审核！");
                        getState();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    @Event(R.id.sub)
    private void subclick(View v){

        address=et_compAddress.getText().toString();
        products=et_commonGoods.getText().toString();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("id", id);
        maps.put("sealurl", sealurl);
        maps.put("address", address);
        maps.put("products", products);
        maps.put("paytype", paytype);
        XUtilsHelper.getInstance().post("app/editeMallCredit.htm", maps,new XUtilsHelper.XCallBack(){

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
                        CommonUtil.alter("在线签约成功,请等待审核!");
                        getState();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    @Event(value={R.id.ll_selectZone},type=View.OnTouchListener.class)
    private boolean selectZoneTouch(View v, MotionEvent arg1) {
        if(arg1.getAction() == KeyEvent.ACTION_UP){
            Context context = MyCreditActivity.this;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.form_common_list, null);
            ListView myListView = (ListView) layout.findViewById(R.id.formcustomspinner_list);
            MyAdapter adapter = new MyAdapter(context, dateMaps);
            myListView.setAdapter(adapter);
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                    //在这里面就是执行点击后要进行的操作,这里只是做一个显示
                    //Toast.makeText(SearchProductListActivity.this, "您点击的是"+list.get(position).toString(), Toast.LENGTH_LONG).show();
                    et_zone.setText(dateMaps.get(position).get("name").toString());
                    tv_showFax.setText("我方传真号："+dateMaps.get(position).get("fax").toString());
                    zoneid=dateMaps.get(position).get("id").toString();
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
            });

            builder = new AlertDialog.Builder(context);
            builder.setView(layout);
            alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
        return true;
    }

    @Event(value={R.id.doSeal},type=View.OnTouchListener.class)
    private boolean selectSealTouch(View v, MotionEvent arg1) {
        if(arg1.getAction() == KeyEvent.ACTION_UP){
            if(sealMaps.size()>0) {
                Context context = MyCreditActivity.this;
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.form_image_list, null);
                MyGridView mygw = (MyGridView) layout.findViewById(R.id.mygw);

                String [] from ={};
                int [] to = {};
                Sealadapter adapter = new Sealadapter(this, sealMaps, R.layout.form_image_list, from, to);
                mygw.setAdapter(adapter);
                /*mygw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                        //在这里面就是执行点击后要进行的操作,这里只是做一个显示
                        //Toast.makeText(SearchProductListActivity.this, "您点击的是"+list.get(position).toString(), Toast.LENGTH_LONG).show();
                        holder.setText(dateMaps.get(position).get("sealName").toString());
                        tv_showFax.setText("我方传真号：" + dateMaps.get(position).get("fax").toString());
                        zoneid = dateMaps.get(position).get("id").toString();
                        if (alertDialog != null) {
                            alertDialog.dismiss();
                        }
                    }
                });*/

                builder = new AlertDialog.Builder(context);
                builder.setView(layout);
                alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
            else{
                CommonUtil.alter("暂无印章!");
            }
        }
        return true;
    }

    @SuppressLint("NewApi")
    @Event(value={R.id.ll_monthly,R.id.ll_yearly},type=View.OnTouchListener.class)
    private boolean selectTouch(View v, MotionEvent e){
        if(e.getAction() == MotionEvent.ACTION_UP){
            if(v.getId() ==R.id.ll_monthly ){
                if(iv_monthly.getTag().toString().equals("0")){
                    iv_monthly.setBackgroundResource(R.drawable.icon_radio_selected);
                    iv_yearly.setBackgroundResource(R.drawable.icon_radio_unselected);
                    iv_monthly.setTag("1");
                    iv_yearly.setTag("0");
                    paytype="0";
                }
                else{
                    iv_monthly.setBackgroundResource(R.drawable.icon_radio_unselected);
                    iv_yearly.setBackgroundResource(R.drawable.icon_radio_selected);
                    iv_monthly.setTag("0");
                    iv_yearly.setTag("1");
                    paytype="1";
                }
            }else if(v.getId() ==R.id.ll_yearly ){
                if(iv_yearly.getTag().toString().equals("0")){
                    iv_monthly.setBackgroundResource(R.drawable.icon_radio_unselected);
                    iv_yearly.setBackgroundResource(R.drawable.icon_radio_selected);
                    iv_monthly.setTag("0");
                    iv_yearly.setTag("1");
                    paytype="1";
                }
                else{
                    iv_monthly.setBackgroundResource(R.drawable.icon_radio_selected);
                    iv_yearly.setBackgroundResource(R.drawable.icon_radio_unselected);
                    iv_monthly.setTag("1");
                    iv_yearly.setTag("0");
                    paytype="0";
                }
            }else{

            }
            return false;
        }
        return true;
    }

    private void disableInit() {
        ll_step1.setVisibility(View.GONE);
        ll_step2.setVisibility(View.GONE);
        ll_step3.setVisibility(View.GONE);
        ll_step4.setVisibility(View.GONE);
        ll_step_ok.setVisibility(View.GONE);
        ll_step_refuse.setVisibility(View.GONE);
    }

    private void subClick(View v){
        disableInit();
        ll_step2.setVisibility(View.VISIBLE);
    }

    //自定义的适配器
    class MyAdapter extends BaseAdapter {
        private List<Map<String, Object>> mlist;
        private Context mContext;

        public MyAdapter(Context context, List<Map<String, Object>> list) {
            this.mContext = context;
            mlist = new ArrayList<Map<String, Object>>();
            this.mlist = list;
        }

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Object getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyCreditActivity.MyAdapter.Person person = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.form_dialog_item,null);
                person = new MyCreditActivity.MyAdapter.Person();
                person.name = (TextView)convertView.findViewById(R.id.tv_name);
                convertView.setTag(person);
            }else{
                person = (MyCreditActivity.MyAdapter.Person)convertView.getTag();
            }
            person.name.setText(dateMaps.get(position).get("name").toString());
            return convertView;
        }
        class Person{
            TextView name;
        }
    }

    //自定义的适配器
    @SuppressLint("ResourceAsColor")
    public class Sealadapter  extends SimpleAdapter {
        public MyCreditActivity.ViewHolder holder;
        private LayoutInflater mInflater;

        public Sealadapter(Context context,
                          List<? extends Map<String, ?>> data, int resource, String[] from,
                          int[] to) {
            super(context, data, resource, from, to);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @SuppressLint({ "NewApi", "ResourceAsColor" })
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try{
                holder=null;
                if(convertView==null){
                    convertView=mInflater.inflate(R.layout.listview_seal, null);
                    holder=new MyCreditActivity.ViewHolder();
                    x.view().inject(holder,convertView);
                    convertView.setTag(holder);
                }
                else{
                    holder=(MyCreditActivity.ViewHolder) convertView.getTag();
                }
                holder.protitle.setText(sealMaps.get(position).get("sealName").toString());

                XUtilsHelper.getInstance().bindCommonImage(holder.proimg, sealMaps.get(position).get("picUrl").toString(), true);

                LinearLayout.LayoutParams lp1 =new LinearLayout.
                        LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.setMargins(0, 0, DensityUtil.dip2px( 2), DensityUtil.dip2px( 4));
                LinearLayout.LayoutParams lp2 =new LinearLayout.
                        LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp2.setMargins(DensityUtil.dip2px( 2), 0, 0, DensityUtil.dip2px( 4));
                if(position%2 == 0){
                    holder.pro.setLayoutParams(lp1);
                }
                else
                {
                    holder.pro.setLayoutParams(lp2);
                }


                holder.pro.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View arg0, MotionEvent e) {
                        if(e.getAction() == MotionEvent.ACTION_UP){
                            String url=sealMaps.get(position).get("picUrl").toString();
                            XUtilsHelper.getInstance().bindCommonImage(iv_customSeal, url, true);
                            sealurl=CommonUtil.getStrings(R.string.img_url)+url;
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                            }
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

    class ViewHolder{
        @ViewInject(R.id.proimg)
        private ImageView proimg;
        @ViewInject(R.id.protitle)
        private TextView protitle;
        @ViewInject(R.id.pro)
        private RelativeLayout pro;

    }

    @Event(value=R.id.iv_officeImage,type=View.OnTouchListener.class)
    private boolean officeImageclick(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            if(mcd1==null){
                mcd1=new MyConfirmDialog(MyCreditActivity.this, "办公室照片", "拍照上传", "本地上传");
                mcd1.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm() {
                        TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";
                        Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));
                        intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                        startActivityForResult(intent1,201);
                    }
                    @Override
                    public void doCancel() {
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, 101);
                    }
                });
            }
            mcd1.show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            //办公室照片
            if(requestCode==101&&data!=null){
                progressDialog.show();
                mcd1.dismiss();
                Uri uri = data.getData();
                if(bitmap1 != null)//如果不释放的话，不断取图片，将会内存不够
                    bitmap1.recycle();
                TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap1 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
                iv_officeImage.setImageBitmap(bitmap1);
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
                                    officeImage=res.getString("fileUrl");
                                    iv_close_officeImage.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        catch(Exception e){e.printStackTrace();}
                    }
                });
            }
            else if(requestCode==201){
                progressDialog.show();
                mcd1.dismiss();
                if(bitmap1 != null)//如果不释放的话，不断取图片，将会内存不够
                    bitmap1.recycle();
                ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
                bitmap1 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
                iv_officeImage.setImageBitmap(bitmap1);
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
                                    officeImage=res.getString("fileUrl");
                                    iv_close_officeImage.setVisibility(View.VISIBLE);
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