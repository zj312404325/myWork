package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopSearchActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.DensityUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.UpdateApp;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyGridView;
import com.example.administrator.jymall.view.ShufflingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_index)
public class IndexActivity extends TopSearchActivity  {

    private ShufflingView shufflingView;
    private List<String> mImageIds = new ArrayList<String>();

    @ViewInject(value=R.id.index_p1)
    private ImageButton index_p1;
    @ViewInject(value=R.id.index_p2)
    private ImageButton index_p2;
    @ViewInject(value=R.id.index_p3)
    private ImageButton index_p3;
    @ViewInject(value=R.id.index_p4)
    private ImageButton index_p4;
    @ViewInject(value=R.id.index_p5)
    private ImageButton index_p5;
    @ViewInject(value=R.id.index_p6)
    private ImageButton index_p6;
    @ViewInject(value=R.id.mygw)
    private MyGridView mygw;
    @ViewInject(R.id.listtv)
    private TextView listtv;
    @ViewInject(value=R.id.mydiscountgw)
    private MyGridView mydiscountgw;
    @ViewInject(R.id.listdiscount)
    private TextView listdiscount;


    @ViewInject(value=R.id.index_fast_btn)
    private ImageButton index_fast_btn;

    @ViewInject(value=R.id.index_order_btn)
    private ImageButton index_order_btn;

    private Proadapter proadapter;
    private Proadapter hotadapter;

    private List<Map<String, Object>> data_list= new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> hot_data_list= new ArrayList<Map<String, Object>>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new UpdateApp("app/versionCheck.htm",this);
        x.view().inject(this);

        if(!super.isLogin){return;}
        else{
            if(super.isRealName){
                index_fast_btn.setBackgroundResource(R.drawable.index_fast_match);
                index_order_btn.setBackgroundResource(R.drawable.index_order_match);
                index_fast_btn.setEnabled(true);
                index_order_btn.setEnabled(true);
            }
            else{
                index_fast_btn.setBackgroundResource(R.drawable.index_fast_match_grey);
                index_order_btn.setBackgroundResource(R.drawable.index_order_match_grey);
                index_fast_btn.setEnabled(false);
                index_order_btn.setEnabled(false);
            }
        }
        progressDialog.hide();
        try{
            getImgData();
            String [] from ={"proName"};
            int [] to = {R.id.giftName};
            proadapter = new Proadapter(this, data_list, R.layout.list_indexprolist, from, to);
            hotadapter = new Proadapter(this, hot_data_list, R.layout.list_indexprolist, from, to);

            mygw.setAdapter(proadapter);
            mydiscountgw.setAdapter(hotadapter);

            String indexData = getIndexData();
            String hotData = getIndexHotData();
            if(indexData.equals("") || hotData.equals("")) {
                getData();
                getHotData();
            }
            else {
                daDiv(indexData);
                daHotDiv(hotData);
            }
            parentControl();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Event(value={R.id.index_p1,R.id.index_p2,R.id.index_p3,R.id.index_p4,R.id.index_p5,R.id.index_p6})
    private void imgClick(View v){
        Intent i = new Intent(getApplicationContext(),SearchProductListActivity.class);
        i.putExtra("categoryid", v.getTag().toString());
        startActivity(i);
    }

    @Event(value=R.id.index_fast_btn)
    private void fastMatchClick(View v){
        Intent i = new Intent(getApplicationContext(), CommitFastMatchActivity.class);
        startActivity(i);
    }

    @Event(value=R.id.index_order_btn)
    private void orderMatchClick(View v){
        Intent i = new Intent(getApplicationContext(), CommitOrderMatchActivity.class);
        startActivity(i);
    }

    public void getImgData(){
        progressDialog.show();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("id", "FF8080815940854F01599037A80B5DB4");
        XUtilsHelper.getInstance().post("app/indexAdData.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                progressDialog.hide();
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    JSONArray resjarr = (JSONArray)res.get("data");
                    for(int i=0;i<resjarr.length();i++){
                        mImageIds.add(CommonUtil.getStrings(R.string.img_url)+resjarr.getJSONObject(i).get("ad_filepath").toString());
                    }
                    //初始化图片
                    shufflingView = (ShufflingView) findViewById(R.id.shufflingView);
                    //设置广告链接
                    shufflingView.setImagers(mImageIds);
                    //点击监听
                    shufflingView.setOnitenClicklistener(new ShufflingView.OnitemClicklistener(){

                        @Override
                        public void setOnitemClicklistener(int position) {
                            // TODO Auto-generated method stub
                            //Toast.makeText(getApplicationContext(), "点击了:" + position, Toast.LENGTH_SHORT).show();
                        }

                    });
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });

    }

    public void daDiv(String data) throws JSONException{
        JSONArray resjarr = FormatUtil.toJSONArray(data);
        int length =0;
        if(FormatUtil.isNoEmpty(resjarr)){
            length =resjarr.length();
        }

        for(int i=0;i<length;i++){
            Map<String, Object> dateMap = new HashMap<String, Object>();
            dateMap.put("unit", resjarr.getJSONObject(i).get("unit"));
            dateMap.put("imagePath", resjarr.getJSONObject(i).get("imagePath"));
            dateMap.put("proName", resjarr.getJSONObject(i).get("proName"));
            dateMap.put("salePrice", resjarr.getJSONObject(i).get("salePrice"));
            dateMap.put("newSaleprice", resjarr.getJSONObject(i).get("newSaleprice"));
            dateMap.put("isDiscount", resjarr.getJSONObject(i).get("isDiscount"));
            dateMap.put("quantity", resjarr.getJSONObject(i).get("quantity"));
            dateMap.put("id", resjarr.getJSONObject(i).get("id"));
            data_list.add(dateMap);
        }
        proadapter.notifyDataSetChanged();
    }

    public void daHotDiv(String data) throws JSONException{
        JSONArray resjarr = FormatUtil.toJSONArray(data);
        int length =0;
        if(FormatUtil.isNoEmpty(resjarr)) {
            length = resjarr.length();
        }

        for(int i=0;i<length;i++){
            Map<String, Object> dateMap = new HashMap<String, Object>();
            dateMap.put("unit", resjarr.getJSONObject(i).get("unit"));
            dateMap.put("imagePath", resjarr.getJSONObject(i).get("imagePath"));
            dateMap.put("proName", resjarr.getJSONObject(i).get("proName"));
            dateMap.put("salePrice", resjarr.getJSONObject(i).get("salePrice"));
            dateMap.put("newSaleprice", resjarr.getJSONObject(i).get("newSaleprice"));
            dateMap.put("isDiscount", resjarr.getJSONObject(i).get("isDiscount"));
            dateMap.put("quantity", resjarr.getJSONObject(i).get("quantity"));
            dateMap.put("id", resjarr.getJSONObject(i).get("id"));
            hot_data_list.add(dateMap);

        }
        hotadapter.notifyDataSetChanged();
    }


    public void getData(){
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        listtv.setVisibility(View.VISIBLE);
        XUtilsHelper.getInstance().post("app/indexMallLoadData.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                listtv.setVisibility(View.GONE);
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    setIndexData(res.getString("data"));
                    daDiv(res.getString("data"));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    public void getHotData(){
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        listdiscount.setVisibility(View.VISIBLE);
        XUtilsHelper.getInstance().post("app/indexMallLoadDiscountData.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                listdiscount.setVisibility(View.GONE);
                JSONObject res;
                try {

                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    setIndexHotData(res.getString("data"));
                    daHotDiv(res.getString("data"));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }



    @SuppressLint("ResourceAsColor")
    public class Proadapter  extends SimpleAdapter {
        public ViewHolder holder;
        private LayoutInflater mInflater;

        public Proadapter(Context context,
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
                    convertView=mInflater.inflate(R.layout.list_indexprolist, null);
                    holder=new ViewHolder();
                    x.view().inject(holder,convertView);
                    convertView.setTag(holder);
                }
                else{
                    holder=(ViewHolder) convertView.getTag();
                }
                holder.protitle.setText(data_list.get(position).get("proName").toString());
                holder.promoney.setText("￥"+data_list.get(position).get("salePrice").toString()
                        +"/"+data_list.get(position).get("unit").toString());
                XUtilsHelper.getInstance().bindCommonImage(holder.proimg, data_list.get(position).get("imagePath").toString(), true);

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
                            Intent i = new Intent(getApplicationContext(),ProductInfoActivity.class);
                            i.putExtra("id", data_list.get(position).get("id").toString());
                            startActivity(i);
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
        @ViewInject(R.id.promoney)
        private TextView promoney;
        @ViewInject(R.id.pro)
        private RelativeLayout pro;

    }




















    /**
     * 主页按退出按钮
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            CommonUtil.openQiutDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化控件
     */
    private void parentControl(){
        super.imageView1.setImageResource(R.drawable.buttom_tap_1s);
    }

}

