package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_myscore)
public class MyExchangeActivity extends TopActivity implements IXListViewListener {

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;
    @ViewInject(R.id.listtv)
    private TextView listtv;
    @ViewInject(R.id.total_income)
    private TextView total_income;
    @ViewInject(R.id.total_pay)
    private TextView total_pay;

    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
    public List<Map<String, Object>> provinceMaps= new ArrayList<Map<String, Object>>();
    private SimpleAdapter sap;
    private Handler mHandler;
    private int start = 1;
    private String totalIncome="";
    private String totalPay="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("我的积分");
        progressDialog.hide();

        sap = new ProSimpleAdapter(MyScoreActivity.this, dateMaps,
                R.layout.listview_myscore,
                new String[]{"scoreType"},
                new int[]{R.id.tv_scoreType});
        listViewAll.setAdapter(sap);
        listViewAll.setPullLoadEnable(true);
        listViewAll.setXListViewListener(this);
        getData(true,true);
        mHandler = new Handler();
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

        XUtilsHelper.getInstance().post("app/mallIntegralList.htm", maps,new XUtilsHelper.XCallBack(){

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
                    JSONArray scoreList = (JSONArray)res.get("data");
                    totalIncome= FormatUtil.toString(res.get("total_get"));
                    totalPay=FormatUtil.toString(res.get("total_used"));

                    total_income.setText("+"+totalIncome);
                    total_pay.setText("-"+totalPay);

                    if(scoreList.length()==0 && start == 1) {
                        listtv.setVisibility(View.VISIBLE);
                    }
                    else if(scoreList.length() ==  10 ) {
                        listViewAll.setPullLoadEnable(true);
                    }

                    for(int i=0;i<scoreList.length();i++){
                        Map<String, Object> dateMap = new HashMap<String, Object>();
                        dateMap.put("username", scoreList.getJSONObject(i).get("username"));
                        dateMap.put("title", scoreList.getJSONObject(i).get("title"));
                        dateMap.put("content", scoreList.getJSONObject(i).get("content"));
                        dateMap.put("fraction", scoreList.getJSONObject(i).get("fraction"));
                        dateMap.put("inoutflag", scoreList.getJSONObject(i).get("inoutflag"));
                        dateMap.put("createdate", scoreList.getJSONObject(i).get("createdate"));
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
                convertView=mInflater.inflate(R.layout.listview_myscore, null);
                holder=new ViewHolder();
                x.view().inject(holder,convertView);
                convertView.setTag(holder);
            }
            else{
                holder=(ViewHolder) convertView.getTag();
            }

            String title=myMaps.get(position).get("title").toString();
            String content=myMaps.get(position).get("content").toString();
            String fraction=myMaps.get(position).get("fraction").toString();
            String inoutflag=myMaps.get(position).get("inoutflag").toString();
            String createdate=myMaps.get(position).get("createdate").toString();

            holder.tv_scoreType.setText(title);
            holder.tv_scoreTime.setText(createdate);
            if(inoutflag.equals("0")){
                Resources resource = (Resources) getBaseContext().getResources();
                ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.red);
                if (csl != null) {
                    holder.tv_score.setTextColor(csl);
                }
                holder.tv_score.setText("+"+fraction);
            }
            else if(inoutflag.equals("1")){
                Resources resource = (Resources) getBaseContext().getResources();
                ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.green);
                if (csl != null) {
                    holder.tv_score.setTextColor(csl);
                }
                holder.tv_score.setText("-"+fraction);
            }

            return super.getView(position, convertView, parent);
        }
    }

    class ViewHolder{
        @ViewInject(R.id.tv_scoreType)
        private TextView tv_scoreType;
        @ViewInject(R.id.tv_scoreTime)
        private TextView tv_scoreTime;
        @ViewInject(R.id.tv_score)
        private TextView tv_score;
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
