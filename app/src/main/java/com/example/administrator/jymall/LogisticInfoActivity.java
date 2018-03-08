package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.XListView;
import com.example.administrator.jymall.view.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ContentView(R.layout.activity_logistic_info)
public class LogisticInfoActivity extends TopActivity implements IXListViewListener{

    @ViewInject(R.id.xListView)
    private XListView listViewAll = null ;
    @ViewInject(R.id.listtv)
    private TextView listtv;
    @ViewInject(R.id.tv_logisticNo)
    private TextView tv_logisticNo;
    @ViewInject(R.id.tv_logisticComp)
    private TextView tv_logisticComp;
    @ViewInject(R.id.tv_sendAddress)
    private TextView tv_sendAddress;
    @ViewInject(R.id.tv_receiveAddress)
    private TextView tv_receiveAddress;

    private SimpleAdapter sap;
    private Handler mHandler;
    private int start = 1;
    private String orderId="";
    private String logisticno="";
    private String logisticcode="";
    private List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();

    private String isNoLogistic="0";
    private String isErrorLogistic="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("查看物流");
        progressDialog.hide();
        Intent i = getIntent();
        orderId = i.getStringExtra("id");

        sap = new ProSimpleAdapter(LogisticInfoActivity.this, dateMaps,
                R.layout.listview_logistic_info,
                new String[]{},
                new int[]{});
        listViewAll.setAdapter(sap);
        listViewAll.setPullLoadEnable(true);
        listViewAll.setXListViewListener(this);
        getOrderInfo();
        mHandler = new Handler();
    }

    private void getOrderInfo(){
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("orderId", orderId);
        XUtilsHelper.getInstance().post("app/mallLogisticInfo.htm", maps,new XUtilsHelper.XCallBack() {

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result) {
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    JSONObject order = (JSONObject)res.get("data");
                    logisticno= order.get("logisticno").toString();
                    logisticcode= order.get("logisticcode").toString();
                    tv_logisticNo.setText(logisticno);
                    tv_logisticComp.setText(order.get("logisticmemo").toString());
                    tv_sendAddress.setText(order.get("saleAddr").toString());
                    tv_receiveAddress.setText(order.get("buyAddr").toString());
                    getData(true,true);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
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
        maps.put("logisticno", logisticno);
        maps.put("logisticcode", logisticcode);
        maps.put("currentPage", ""+start);

        XUtilsHelper.getInstance().post("app/getLogisticJson.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                listtv.setVisibility(View.GONE);
                if(flag){
                    dateMaps.clear();
                    isNoLogistic="0";
                    isErrorLogistic="0";
                    listtv.setVisibility(View.GONE);
                }
                if(isShow){
                    progressDialog.hide();
                }
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());

                    if(res.get("status").toString().equals("y")){
                        JSONObject jsonData1 = new JSONObject(res.get("data").toString());
                        if(jsonData1.get("status").toString().equals("200")){
                            JSONArray logisticList = (JSONArray)jsonData1.get("data");
                            if(logisticList.length()==0 && start == 1) {
                                listtv.setVisibility(View.VISIBLE);
                            }
                            else if(logisticList.length() ==  99 ) {
                                listViewAll.setPullLoadEnable(true);
                            }
                            if(logisticList.length()>0){
                                for(int i=0;i<logisticList.length();i++){
                                    JSONObject item=logisticList.getJSONObject(i);
                                    String dateTime=item.get("time").toString();
                                    String [] dateTimeStr=dateTime.split(" ");

                                    Map<String, Object> dateMap = new HashMap<String, Object>();
                                    dateMap.put("dateString", dateTimeStr[0]);
                                    dateMap.put("timeString", dateTimeStr[1]);
                                    dateMap.put("context", item.get("context"));
                                    dateMaps.add(dateMap);
                                }
                                listtv.setVisibility(View.GONE);
                                listViewAll.setVisibility(View.VISIBLE);
                            }
                            else{
                                //暂无快递信息！
                                isNoLogistic="1";
                                listtv.setText("暂无物流信息");
                                listtv.setVisibility(View.VISIBLE);
                                listViewAll.setVisibility(View.GONE);
                            }
                        }
                        else{
                            //物流信息异常！
                            isErrorLogistic="1";
                            listtv.setText("物流信息异常");
                            listtv.setVisibility(View.VISIBLE);
                            listViewAll.setVisibility(View.GONE);
                        }
                    }
                    else{
                        //暂无物流信息
                        isNoLogistic="1";
                        listtv.setText("暂无物流信息");
                        listtv.setVisibility(View.VISIBLE);
                        listViewAll.setVisibility(View.GONE);
                    }

                    sap.notifyDataSetChanged();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }

    public class ProSimpleAdapter  extends SimpleAdapter {

        public LogisticInfoActivity.ViewHolder holder;
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
                convertView=mInflater.inflate(R.layout.listview_logistic_info, null);
                holder=new LogisticInfoActivity.ViewHolder();
                x.view().inject(holder,convertView);
                convertView.setTag(holder);
            }
            else{
                holder=(LogisticInfoActivity.ViewHolder) convertView.getTag();
            }

            String dateString=myMaps.get(position).get("dateString").toString();
            String timeString=myMaps.get(position).get("timeString").toString();
            String content=myMaps.get(position).get("context").toString();

            holder.tv_dateString.setText(dateString);
            holder.tv_timeString.setText(timeString);
            holder.tv_context.setText(content);

            if(position==0){
                holder.iv_state.setBackgroundResource(R.drawable.icon_logistic_blue);
            }
            else{
                holder.iv_state.setBackgroundResource(R.drawable.icon_logistic_grey);
            }

            return super.getView(position, convertView, parent);
        }

        @Override
        public int getCount() {
            return this.myMaps.size();
        }
    }

    class ViewHolder{
        @ViewInject(R.id.tv_dateString)
        private TextView tv_dateString;
        @ViewInject(R.id.tv_timeString)
        private TextView tv_timeString;
        @ViewInject(R.id.tv_context)
        private TextView tv_context;
        @ViewInject(R.id.iv_state)
        private ImageView iv_state;
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
                getOrderInfo();
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
                getOrderInfo();
                onLoad();
            }
        }, 1);

    }
}
