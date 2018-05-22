package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.CommonDialog;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
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

@ContentView(R.layout.activity_order_match_list)
public class FabOrderMatchListActivity extends TopActivity implements IXListViewListener {

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
    private String skey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_match_list);
        x.view().inject(this);
        super.title.setText("工厂定制配管理");
        progressDialog.hide();
        skey=super.serverKey;

        sap = new ProSimpleAdapter(FabOrderMatchListActivity.this, dateMaps,
                R.layout.listview_ordermatch,
                new String[]{"matchNo"},
                new int[]{R.id.tv_matchNo});
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

        XUtilsHelper.getInstance().post("app/mallFabOrderMatchList.htm", maps,new XUtilsHelper.XCallBack(){

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
                    JSONArray matchList = (JSONArray)res.get("matchlist");

                    if(matchList.length()==0 && start == 1) {
                        listtv.setVisibility(View.VISIBLE);
                    }
                    else if(matchList.length() ==  10 ) {
                        listViewAll.setPullLoadEnable(true);
                    }

                    for(int i=0;i<matchList.length();i++){
                        Map<String, Object> dateMap = new HashMap<String, Object>();
                        dateMap.put("id", matchList.getJSONObject(i).get("id"));
                        dateMap.put("matchNo", matchList.getJSONObject(i).get("fastno"));
                        dateMap.put("isOrder", matchList.getJSONObject(i).get("isorder"));
                        dateMap.put("status", matchList.getJSONObject(i).get("status"));
                        dateMap.put("isDel", matchList.getJSONObject(i).get("isdel"));
                        dateMap.put("createDate", matchList.getJSONObject(i).get("createdate"));
                        dateMap.put("matchType", matchList.getJSONObject(i).get("matchtype"));
                        dateMap.put("fileName", matchList.getJSONObject(i).get("fileName"));
                        dateMap.put("fileUrl", matchList.getJSONObject(i).get("fileUrl"));
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
                convertView=mInflater.inflate(R.layout.listview_ordermatch, null);
                holder=new ViewHolder();
                x.view().inject(holder,convertView);
                convertView.setTag(holder);
            }
            else{
                holder=(ViewHolder) convertView.getTag();
            }

            final String id=myMaps.get(position).get("id").toString();
            final String matchNo=myMaps.get(position).get("matchNo").toString();
            final String isOrder=myMaps.get(position).get("isOrder").toString();
            final String status=myMaps.get(position).get("status").toString();
            final String isDel=myMaps.get(position).get("isDel").toString();
            final String createDate=myMaps.get(position).get("createDate").toString();

            holder.tv_matchNo.setText(matchNo);
            holder.tv_matchDate.setText(createDate);

            if(status.equals("1")){
                holder.tv_matchState.setText("已受理");
                holder.ll_matchInfo.setClickable(true);
                holder.btn_cancel.setVisibility(View.GONE);
                holder.ll_matchInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(getApplicationContext(), OrderMatchDetailActivity.class);
                        i.putExtra("matchid", id);
                        startActivity(i);
                    }
                });
            }
            else if(status.equals("2")) {
                holder.tv_matchState.setText("已取消");
                holder.ll_matchInfo.setClickable(false);
                holder.btn_cancel.setVisibility(View.GONE);
            }
            else if(status.equals("3")) {
                holder.tv_matchState.setText("受理中");
                holder.ll_matchInfo.setClickable(true);
                holder.btn_cancel.setVisibility(View.GONE);
                holder.ll_matchInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(getApplicationContext(), OrderMatchDetailActivity.class);
                        i.putExtra("matchid", id);
                        startActivity(i);
                    }
                });
            }
            else if(status.equals("4")) {
                holder.tv_matchState.setText("受理完成");
                holder.ll_matchInfo.setClickable(true);
                holder.btn_cancel.setVisibility(View.GONE);
                holder.ll_matchInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(getApplicationContext(), OrderMatchDetailActivity.class);
                        i.putExtra("matchid", id);
                        startActivity(i);
                    }
                });
            }
            else{
                holder.tv_matchState.setText("待受理");
                holder.ll_matchInfo.setClickable(true);
                holder.ll_matchInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(getApplicationContext(), EditOrderMatchDetailActivity.class);
                        i.putExtra("matchid", id);
                        startActivity(i);
                    }
                });
                holder.btn_cancel.setVisibility(View.VISIBLE);
                holder.btn_cancel.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View arg0, MotionEvent e) {
                        if(e.getAction() == MotionEvent.ACTION_UP){
                            new CommonDialog(FabOrderMatchListActivity.this, R.style.dialog, "确定取消？", new CommonDialog.OnCloseListener() {
                                @Override
                                public void onClick(Dialog dialog, boolean confirm) {
                                    if(confirm){
                                        progressDialog.show();
                                        Map<String, String> maps= new HashMap<String, String>();
                                        maps.put("serverKey", skey);
                                        maps.put("id", id);
                                        XUtilsHelper.getInstance().post("app/cancelOrderMatch.htm", maps,new XUtilsHelper.XCallBack(){

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
                                                        sap.notifyDataSetChanged();
                                                        getData(true,true);
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
                            return false;
                        }
                        return true;
                    }
                });
            }
            return super.getView(position, convertView, parent);
        }
    }

    class ViewHolder{
        @ViewInject(R.id.tv_matchNo)
        private TextView tv_matchNo;
        @ViewInject(R.id.tv_matchDate)
        private TextView tv_matchDate;
        @ViewInject(R.id.tv_matchState)
        private TextView tv_matchState;
        @ViewInject(R.id.ll_matchInfo)
        private LinearLayout ll_matchInfo;
        @ViewInject(R.id.ll_cancel)
        private LinearLayout ll_cancel;
        @ViewInject(R.id.btn_cancel)
        private Button btn_cancel;
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
