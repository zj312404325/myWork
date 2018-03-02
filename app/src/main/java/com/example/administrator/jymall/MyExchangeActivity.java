package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Context;
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

@ContentView(R.layout.activity_myexchange)
public class MyExchangeActivity extends TopActivity implements IXListViewListener {

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;
    @ViewInject(R.id.listtv)
    private TextView listtv;
    @ViewInject(R.id.tv_myScore)
    private TextView tv_myScore;

    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
    private SimpleAdapter sap;
    private Handler mHandler;
    private int start = 1;
    private String myScore="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("我的兑换记录");
        progressDialog.hide();

        sap = new ProSimpleAdapter(MyExchangeActivity.this, dateMaps,
                R.layout.listview_myexchange,
                new String[]{},
                new int[]{});
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

        XUtilsHelper.getInstance().post("app/mallExchangeList.htm", maps,new XUtilsHelper.XCallBack(){

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
                    JSONArray exchangeList = (JSONArray)res.get("data");
                    myScore= FormatUtil.toString(res.get("score"));
                    tv_myScore.setText(myScore);

                    if(exchangeList.length()==0 && start == 1) {
                        listtv.setVisibility(View.VISIBLE);
                    }
                    else if(exchangeList.length() ==  10 ) {
                        listViewAll.setPullLoadEnable(true);
                    }

                    for(int i=0;i<exchangeList.length();i++){
                        Map<String, Object> dateMap = new HashMap<String, Object>();
                        dateMap.put("gift", exchangeList.getJSONObject(i).get("gift"));
                        dateMap.put("giftintegral", exchangeList.getJSONObject(i).get("giftintegral"));
                        dateMap.put("quantity", exchangeList.getJSONObject(i).get("quantity"));
                        dateMap.put("totalintegral", exchangeList.getJSONObject(i).get("totalintegral"));
                        dateMap.put("createdate", exchangeList.getJSONObject(i).get("createdate"));
                        dateMap.put("isAccepted", exchangeList.getJSONObject(i).get("isAccepted"));
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
                convertView=mInflater.inflate(R.layout.listview_myexchange, null);
                holder=new ViewHolder();
                x.view().inject(holder,convertView);
                convertView.setTag(holder);
            }
            else{
                holder=(ViewHolder) convertView.getTag();
            }

            String gift=myMaps.get(position).get("gift").toString();
            String giftintegral=myMaps.get(position).get("giftintegral").toString();
            String quantity=myMaps.get(position).get("quantity").toString();
            String totalintegral=myMaps.get(position).get("totalintegral").toString();
            String createdate=myMaps.get(position).get("createdate").toString();
            String isAccepted=myMaps.get(position).get("isAccepted").toString();

            holder.tv_exchangeTime.setText("兑换时间："+createdate);
            holder.tv_giftName.setText("礼品名称："+gift);
            holder.tv_quantity.setText("兑换数量："+quantity);
            holder.tv_giftScore.setText("礼品积分："+giftintegral);
            holder.tv_totalScore.setText("总计积分："+totalintegral);

            if(isAccepted.equals("1")){
                holder.tv_permitState.setText("审核状态：已审核");
            }
            else {
                holder.tv_permitState.setText("审核状态：未审核");
            }
            return super.getView(position, convertView, parent);
        }
    }

    class ViewHolder{
        @ViewInject(R.id.tv_exchangeTime)
        private TextView tv_exchangeTime;
        @ViewInject(R.id.tv_giftName)
        private TextView tv_giftName;
        @ViewInject(R.id.tv_giftScore)
        private TextView tv_giftScore;
        @ViewInject(R.id.tv_quantity)
        private TextView tv_quantity;
        @ViewInject(R.id.tv_totalScore)
        private TextView tv_totalScore;
        @ViewInject(R.id.tv_permitState)
        private TextView tv_permitState;
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
