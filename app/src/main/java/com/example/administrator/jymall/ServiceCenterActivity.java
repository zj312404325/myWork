package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyGridView;
import com.example.administrator.jymall.view.XListView;
import com.example.administrator.jymall.view.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_service_center)
public class ServiceCenterActivity extends TopActivity implements IXListViewListener {

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;
    @ViewInject(R.id.listtv)
    private TextView listtv;

    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
    public List<Map<String, Object>> provinceMaps= new ArrayList<Map<String, Object>>();
    public List<Map<String, Object>> resultMaps= new ArrayList<Map<String, Object>>();
    private SimpleAdapter sap;
    private Handler mHandler;
    private int start = 1;

    private String keyword = "";

    @ViewInject(R.id.search_btn)
    private Button search_btn;
    @ViewInject(R.id.txt_keyword)
    private TextView txt_keyword;
    @ViewInject(R.id.tv_introduction)
    private TextView tv_introduction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("区域服务中心");
        progressDialog.hide();

        sap = new ProvinceSimpleAdapter(ServiceCenterActivity.this, resultMaps,
                R.layout.list_service_province,
                new String[]{},
                new int[]{});
        listViewAll.setAdapter(sap);
        listViewAll.setPullLoadEnable(true);
        listViewAll.setXListViewListener(this);
        getData(true,true);
        mHandler = new Handler();

        //首行缩进
        SpannableStringBuilder span = new SpannableStringBuilder("缩进"+tv_introduction.getText());
        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_introduction.setText(span);
        //parentControl();
    }

    @Event(value=R.id.search_btn)
    private void search_click(View v){
        keyword = txt_keyword.getText().toString();
        getData(true,true);
    }

    private void getData(final boolean isShow,final boolean flag){
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
        maps.put("keyword",keyword);

        XUtilsHelper.getInstance().post("app/getMallServiceCenterList.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                listtv.setVisibility(View.GONE);
                if(flag){
                    dateMaps.clear();
                    provinceMaps.clear();
                    resultMaps.clear();
                }
                if(isShow){
                    progressDialog.hide();
                }
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    JSONArray serviceList = (JSONArray)res.get("data");
                    JSONArray provinceList = (JSONArray)res.get("provinceList");
                    if(serviceList.length()==0 && start == 1)
                        listtv.setVisibility(View.VISIBLE);
                    else if(serviceList.length() ==  20 )
                        listViewAll.setPullLoadEnable(true);

                    for(int i=0;i<serviceList.length();i++){
                        Map<String, Object> dateMap = new HashMap<String, Object>();
                        dateMap.put("name", serviceList.getJSONObject(i).get("name"));
                        dateMap.put("province", serviceList.getJSONObject(i).get("province"));
                        dateMap.put("city", serviceList.getJSONObject(i).get("city"));
                        dateMap.put("district", serviceList.getJSONObject(i).get("district"));
                        dateMap.put("address", serviceList.getJSONObject(i).get("address"));
                        dateMap.put("mobile", serviceList.getJSONObject(i).get("mobile"));
                        dateMap.put("contact", serviceList.getJSONObject(i).get("contact"));
                        dateMap.put("fax", serviceList.getJSONObject(i).get("fax"));
                        dateMap.put("postcode", serviceList.getJSONObject(i).get("postcode"));
                        dateMaps.add(dateMap);
                    }

                    for(int i=0;i<provinceList.length();i++){
                        Map<String, Object> dateMap = new HashMap<String, Object>();
                        dateMap.put("province", provinceList.getJSONObject(i).get("province"));
                        provinceMaps.add(dateMap);
                    }

                    for(int i=0;i<provinceList.length();i++){
                        Map<String, Object> dateMap = new HashMap<String, Object>();
                        dateMap.put("province", provinceList.getJSONObject(i).get("province"));

                        List<Map<String, Object>> centerMaps= new ArrayList<Map<String, Object>>();
                        for(int j=0;j<serviceList.length();j++){
                            if(serviceList.getJSONObject(j).get("province").equals(provinceList.getJSONObject(i).get("province"))){
                                Map<String, Object> detailMap = new HashMap<String, Object>();
                                detailMap.put("name", serviceList.getJSONObject(j).get("name"));
                                detailMap.put("province", serviceList.getJSONObject(j).get("province"));
                                detailMap.put("city", serviceList.getJSONObject(j).get("city"));
                                detailMap.put("district", serviceList.getJSONObject(j).get("district"));
                                detailMap.put("address", serviceList.getJSONObject(j).get("address"));
                                detailMap.put("mobile", serviceList.getJSONObject(j).get("mobile"));
                                detailMap.put("contact", serviceList.getJSONObject(j).get("contact"));
                                detailMap.put("fax", serviceList.getJSONObject(j).get("fax"));
                                detailMap.put("postcode", serviceList.getJSONObject(j).get("postcode"));
                                centerMaps.add(detailMap);
                            }
                        }
                        dateMap.put("detailList", centerMaps);
                        resultMaps.add(dateMap);
                    }

                    sap.notifyDataSetChanged();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });

    }

    public class ProvinceSimpleAdapter  extends SimpleAdapter {

        public ViewHolder holder;
        private LayoutInflater mInflater;
        private List<Map<String, Object>> myMaps;

        public ProvinceSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myMaps = (List<Map<String, Object>>) data;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=mInflater.inflate(R.layout.list_service_province, null);
            }

            MyGridView mygw = (MyGridView) convertView.findViewById(R.id.mygw);
            TextView tv_province =(TextView) convertView.findViewById(R.id.tv_province);
            TextView listtv=(TextView) convertView.findViewById(R.id.listtv);

            final String name=myMaps.get(position).get("province").toString();

            tv_province.setText(name);

            final List<Map<String, Object>> dateMapinfo= (List<Map<String, Object>>) resultMaps.get(position).get("detailList");
            if(dateMapinfo.size()>0){
                listtv.setVisibility(View.INVISIBLE);
                listtv.setText("");
            }
            else{
                listtv.setVisibility(View.VISIBLE);
                listtv.setText("暂无数据");
            }
            final SimpleAdapter sapinfo = new ProSimpleAdapter(ServiceCenterActivity.this, dateMapinfo,
                    R.layout.list_service_center,
                    new String[]{},
                    new int[]{});
            mygw.setAdapter(sapinfo);

            return super.getView(position, convertView, parent);
        }
    }


    public class ProSimpleAdapter  extends SimpleAdapter {

        public ViewHolder holder;
        private LayoutInflater mInflater;
        private List<Map<String, Object>> myMaps;

        public ProSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myMaps = (List<Map<String, Object>>) data;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            holder=null;
            if(convertView==null){
                convertView=mInflater.inflate(R.layout.list_service_center, null);
                holder=new ViewHolder();
                x.view().inject(holder,convertView);
                convertView.setTag(holder);
            }
            else{
                holder=(ViewHolder) convertView.getTag();
            }
            String name=myMaps.get(position).get("name").toString();
            String address=myMaps.get(position).get("province").toString()+myMaps.get(position).get("city").toString()+myMaps.get(position).get("district").toString()+myMaps.get(position).get("address").toString();
            String contact=myMaps.get(position).get("contact").toString();
            String mobile=myMaps.get(position).get("mobile").toString();
            String fax=myMaps.get(position).get("fax").toString();
            String postcode=myMaps.get(position).get("postcode").toString();

            holder.tv_centerName.setText(name);
            holder.tv_address.setText(address);
            holder.tv_contact.setText(contact);
            holder.tv_phone.setText(mobile);
            holder.tv_fax.setText(fax);
            holder.tv_email.setText(postcode);

            TextView mapinfo = (TextView)convertView.findViewById(R.id.mapinfo);
            mapinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(getApplicationContext(),MapInfoActivity.class);
                    intent.putExtra("address", myMaps.get(position).get("province").toString()+myMaps.get(position).get("city").toString()+myMaps.get(position).get("district").toString()+myMaps.get(position).get("address").toString());
                    intent.putExtra("mobile", myMaps.get(position).get("mobile").toString());
                    intent.putExtra("contact", myMaps.get(position).get("contact").toString());
                    intent.putExtra("city", myMaps.get(position).get("city").toString());
                    startActivityForResult(intent,11);
                }
            });
            return super.getView(position, convertView, parent);
        }
    }

    class ViewHolder{
        @ViewInject(R.id.tv_centerName)
        private TextView tv_centerName;
        @ViewInject(R.id.tv_address)
        private TextView tv_address;
        @ViewInject(R.id.tv_contact)
        private TextView tv_contact;
        @ViewInject(R.id.tv_phone)
        private TextView tv_phone;
        @ViewInject(R.id.tv_fax)
        private TextView tv_fax;
        @ViewInject(R.id.tv_email)
        private TextView tv_email;
    }


    /**
     * 初始化控件
     */
    /*private void parentControl(){
        super.imageView3.setImageResource(R.drawable.buttom_tap_3s);
    }*/


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
