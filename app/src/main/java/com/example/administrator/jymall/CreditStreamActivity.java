package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ContentView(R.layout.activity_credit_stream)
public class CreditStreamActivity extends TopActivity implements IXListViewListener {

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;
    @ViewInject(R.id.listtv)
    private TextView listtv;
    @ViewInject(R.id.tv_myPay)
    private TextView tv_myPay;
    @ViewInject(R.id.et_pick_date)
    private EditText et_pick_date;
    @ViewInject(R.id.btn_query)
    private Button btn_query;

    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
    private SimpleAdapter sap;
    private Handler mHandler;
    private int start = 1;
    private String totalPay="";
    private String startDate="";

    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("信用支付流水");
        progressDialog.hide();

        sap = new ProSimpleAdapter(CreditStreamActivity.this, dateMaps,
                R.layout.listview_credit_stream,
                new String[]{},
                new int[]{});
        listViewAll.setAdapter(sap);
        listViewAll.setPullLoadEnable(true);
        listViewAll.setXListViewListener(this);
        getData(true,true);
        mHandler = new Handler();

        Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
        mYear = dateAndTime.get(Calendar.YEAR);
        mMonth = dateAndTime.get(Calendar.MONTH);
        mDay = dateAndTime.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog dateDialog = new DatePickerDialog(this, R.style.AppDateTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                String mm;
                String dd;
                if (monthOfYear < 9) {
                    mMonth = monthOfYear + 1;
                    mm = "0" + mMonth;
                } else {
                    mMonth = monthOfYear + 1;
                    mm = String.valueOf(mMonth);
                }
                if (dayOfMonth < 9) {
                    mDay = dayOfMonth;
                    dd = "0" + mDay;
                } else {
                    mDay = dayOfMonth;
                    dd = String.valueOf(mDay);
                }
                mDay = dayOfMonth;
                et_pick_date.setText(String.valueOf(mYear) +"-"+ mm);
                startDate=String.valueOf(mYear) +"-"+ mm;
            }
        },mYear,mMonth,mDay);

        et_pick_date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dateDialog.show();
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
        maps.put("currentPage", ""+start);
        maps.put("startDate", startDate);

        XUtilsHelper.getInstance().post("app/mallCreditStream.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                listtv.setVisibility(View.GONE);
                if(flag){
                    dateMaps.clear();
                }
                if(isShow){
                    progressDialog.hide();
                }
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    JSONArray streamList = (JSONArray)res.get("data");
                    totalPay= FormatUtil.toString(res.get("totalmoney"));
                    tv_myPay.setText(totalPay);

                    if(streamList.length()==0 && start == 1) {
                        listtv.setVisibility(View.VISIBLE);
                    }
                    else if(streamList.length() ==  10 ) {
                        listViewAll.setPullLoadEnable(true);
                    }

                    for(int i=0;i<streamList.length();i++){
                        Map<String, Object> dateMap = new HashMap<String, Object>();
                        dateMap.put("orderno", streamList.getJSONObject(i).get("orderno"));
                        dateMap.put("inusername", streamList.getJSONObject(i).get("inusername"));
                        dateMap.put("mtype", streamList.getJSONObject(i).get("mtype"));
                        dateMap.put("inmoney", streamList.getJSONObject(i).get("inmoney"));
                        dateMap.put("createdate", streamList.getJSONObject(i).get("createdate"));
                        dateMaps.add(dateMap);
                    }
                    sap.notifyDataSetChanged();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }


    @Event(R.id.btn_query)
    private void submitclick(View v){
        getData(true,true);
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
                convertView=mInflater.inflate(R.layout.listview_credit_stream, null);
                holder=new ViewHolder();
                x.view().inject(holder,convertView);
                convertView.setTag(holder);
            }
            else{
                holder=(ViewHolder) convertView.getTag();
            }

            String orderno=myMaps.get(position).get("orderno").toString();
            String inusername=myMaps.get(position).get("inusername").toString();
            String mtype=myMaps.get(position).get("mtype").toString();
            String inmoney=myMaps.get(position).get("inmoney").toString();
            String createdate=myMaps.get(position).get("createdate").toString();

            holder.tv_orderNo.setText("订单编号："+orderno);
            holder.tv_orderTime.setText(createdate);
            holder.tv_buyerName.setText("买家名称："+inusername);
            holder.tv_money.setText("流水金额："+inmoney+"元");
            if(mtype.equals("1")){
                holder.tv_type.setText("类    型：订单支付");
            }
            else if(mtype.equals("2")){
                holder.tv_type.setText("类    型：退款");
            }
            else if(mtype.equals("3")){
                holder.tv_type.setText("类    型：退货退款");
            }
            return super.getView(position, convertView, parent);
        }
    }

    class ViewHolder{
        @ViewInject(R.id.tv_orderNo)
        private TextView tv_orderNo;
        @ViewInject(R.id.tv_orderTime)
        private TextView tv_orderTime;
        @ViewInject(R.id.tv_buyerName)
        private TextView tv_buyerName;
        @ViewInject(R.id.tv_money)
        private TextView tv_money;
        @ViewInject(R.id.tv_type)
        private TextView tv_type;
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
