package com.example.administrator.jymall;

/**
 * Created by Administrator on 2018-01-30.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopSearchTitleActivity;
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.FormatUtil;
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

@ContentView(R.layout.activity_mall_category)
public class MallCategoryActivity extends TopSearchTitleActivity implements IXListViewListener {

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;
    @ViewInject(R.id.listtv)
    private TextView listtv;

    @ViewInject(value=R.id.mygw)
    private MyGridView mygw;

    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
    public List<Map<String, Object>> dateTwoMaps= new ArrayList<Map<String, Object>>();
    private SimpleAdapter sap;
    private Handler mHandler;
    private int start = 1;
    private String categoryid = "";

    private String keyword = "";
    private String firstCategory = "";

    @ViewInject(R.id.search_btn)
    private Button search_btn;
    @ViewInject(R.id.txt_keyword)
    private TextView txt_keyword;

    @ViewInject(R.id.iv_img1)
    private ImageView iv_img1;
    @ViewInject(R.id.iv_img2)
    private ImageView iv_img2;
    @ViewInject(R.id.iv_img3)
    private ImageView iv_img3;
    @ViewInject(R.id.iv_img4)
    private ImageView iv_img4;
    @ViewInject(R.id.iv_img5)
    private ImageView iv_img5;
    @ViewInject(R.id.iv_img6)
    private ImageView iv_img6;

    @ViewInject(R.id.iv_select1)
    private ImageView iv_select1;
    @ViewInject(R.id.iv_select2)
    private ImageView iv_select2;
    @ViewInject(R.id.iv_select3)
    private ImageView iv_select3;
    @ViewInject(R.id.iv_select4)
    private ImageView iv_select4;
    @ViewInject(R.id.iv_select5)
    private ImageView iv_select5;
    @ViewInject(R.id.iv_select6)
    private ImageView iv_select6;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_category);
        x.view().inject(this);
        sap = new ProSimpleAdapter(MallCategoryActivity.this, dateTwoMaps,
                R.layout.listview_category,
                new String[]{},
                new int[]{});
        listViewAll.setAdapter(sap);
        listViewAll.setPullLoadEnable(true);
        listViewAll.setXListViewListener(this);
        getData(true,true);
        mHandler = new Handler();
        parentControl();
        progressDialog.hide();
    }

    @Event(value=R.id.search_btn)
    private void search_click(View v){
        keyword = txt_keyword.getText().toString();
        getData(true,true);
    }

    @Event(value={R.id.iv_img1,R.id.iv_img2,R.id.iv_img3,R.id.iv_img4,R.id.iv_img5,R.id.iv_img6})
    private void imgClick(View v){
        if(v.getId()==R.id.iv_img1){
            categoryid=iv_img1.getTag().toString();
            showSelect(R.id.iv_select1);
        }
        else if(v.getId()==R.id.iv_img2){
            categoryid=iv_img2.getTag().toString();
            showSelect(R.id.iv_select2);
        }
        else if(v.getId()==R.id.iv_img3){
            categoryid=iv_img3.getTag().toString();
            showSelect(R.id.iv_select3);
        }
        else if(v.getId()==R.id.iv_img4){
            categoryid=iv_img4.getTag().toString();
            showSelect(R.id.iv_select4);
        }
        else if(v.getId()==R.id.iv_img5){
            categoryid=iv_img5.getTag().toString();
            showSelect(R.id.iv_select5);
        }
        else if(v.getId()==R.id.iv_img6){
            categoryid=iv_img6.getTag().toString();
            showSelect(R.id.iv_select6);
        }

        getData(true,true);
    }

    private void getData(final boolean isShow,final boolean flag){
        if(isShow){
            progressDialog.show();
        }
        if(flag){
            start = 1;
        }
        if(!FormatUtil.isNoEmpty(categoryid)){
            categoryid="1524";
        }

        listtv.setVisibility(View.GONE);
        listViewAll.setPullLoadEnable(false);
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("categorylevel", "2");
        maps.put("currentPage", ""+start);
        maps.put("categoryid",categoryid);
        maps.put("keyword",keyword);

        XUtilsHelper.getInstance().post("app/getMallProductListForIndex.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                listtv.setVisibility(View.GONE);
                if(flag){
                    dateMaps.clear();
                    dateTwoMaps.clear();
                }
                if(isShow){
                    progressDialog.hide();
                }
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    JSONArray pcategoryList = (JSONArray)res.get("pcategoryList");
                    JSONArray pcategoryTwoList = (JSONArray)res.get("pcategoryTwoList");

                    //String parentcgid=res.get("parentcgid").toString();
                    if(pcategoryTwoList.length()==0 && start == 1) {
                        listtv.setVisibility(View.VISIBLE);
                    }
                    else if(pcategoryTwoList.length() ==  10 ) {
                        listViewAll.setPullLoadEnable(true);
                    }

                    for(int j=0;j<pcategoryTwoList.length();j++){
                        Map<String, Object> twoMap = new HashMap<String, Object>();
                        twoMap.put("id", pcategoryTwoList.getJSONObject(j).get("id"));
                        twoMap.put("name", pcategoryTwoList.getJSONObject(j).get("name"));
                        twoMap.put("levels", pcategoryTwoList.getJSONObject(j).get("levels"));

                        JSONArray threeList = pcategoryTwoList.getJSONObject(j).getJSONArray("list");
                        List<Map<String, Object>> dateThreeMaps= new ArrayList<Map<String, Object>>();
                        for(int k=0;k<threeList.length();k++){
                            Map<String, Object> threeMap = new HashMap<String, Object>();
                            threeMap.put("id", threeList.getJSONObject(k).get("id"));
                            threeMap.put("name", threeList.getJSONObject(k).get("name"));
                            threeMap.put("levels", threeList.getJSONObject(k).get("levels"));
                            dateThreeMaps.add(threeMap);
                        }
                        twoMap.put("threeList",dateThreeMaps);
                        dateTwoMaps.add(twoMap);
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
            /*holder=null;
            if(convertView==null){
                convertView=mInflater.inflate(R.layout.listview_category, null);
                holder=new ViewHolder();
                x.view().inject(holder,convertView);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }*/
            if(convertView==null){
                convertView=mInflater.inflate(R.layout.listview_category, null);
            }

            MyGridView mygw = (MyGridView) convertView.findViewById(R.id.mygw);
            TextView tv_secondCategory =(TextView) convertView.findViewById(R.id.tv_secondCategory);
            TextView listtv=(TextView) convertView.findViewById(R.id.listtv);

            final String id=myMaps.get(position).get("id").toString();
            final String levels=myMaps.get(position).get("levels").toString();
            final String name=myMaps.get(position).get("name").toString();

            tv_secondCategory.setText(myMaps.get(position).get("name").toString());

            final List<Map<String, Object>> dateMapinfo= (List<Map<String, Object>>) dateTwoMaps.get(position).get("threeList");
            if(dateMapinfo.size()>0){
                listtv.setVisibility(View.INVISIBLE);
                listtv.setText("");
            }
            else{
                listtv.setVisibility(View.VISIBLE);
                listtv.setText("暂无数据");
            }
            final SimpleAdapter sapinfo = new InfoSimpleAdapter(MallCategoryActivity.this, dateMapinfo,
                    R.layout.listview_category_item,
                    new String[]{},
                    new int[]{});
            mygw.setAdapter(sapinfo);

            tv_secondCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent i = new Intent(getApplicationContext(),SearchProductListActivity.class);
                    i.putExtra("categoryid", id);
                    i.putExtra("levels", levels);
                    startActivity(i);
                }
            });
            return super.getView(position, convertView, parent);
        }
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            try{
                if(convertView==null){
                    convertView=mInflater.inflate(R.layout.listview_category_item, null);
                }
                TextView tv_thirdCategory = (TextView)convertView.findViewById(R.id.tv_thirdCategory);

                final String id=mdata.get(position).get("id").toString();
                final String levels=mdata.get(position).get("levels").toString();
                final String name=mdata.get(position).get("name").toString();
                tv_thirdCategory.setText(mdata.get(position).get("name").toString());
                tv_thirdCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(getApplicationContext(),SearchProductListActivity.class);
                        i.putExtra("categoryid", id);
                        i.putExtra("keyword", name);
                        i.putExtra("levels", levels);
                        startActivity(i);
                    }
                });
            }
            catch(Exception e){
                //Log.v("PRO", e.getMessage());
            }
            return super.getView(position, convertView, parent);
        }
    }

    class ViewHolder{
        @ViewInject(R.id.tv_secondCategory)
        private TextView tv_secondCategory;
        @ViewInject(R.id.mygw)
        private MyGridView mygw;
    }

    private void showSelect(int rid){
        hideAll();
        if(rid == R.id.iv_select1){
            iv_select1.setVisibility(View.VISIBLE);
        }
        else if(rid == R.id.iv_select2){
            iv_select2.setVisibility(View.VISIBLE);
        }
        else if(rid == R.id.iv_select3){
            iv_select3.setVisibility(View.VISIBLE);
        }
        else if(rid == R.id.iv_select4){
            iv_select4.setVisibility(View.VISIBLE);
        }
        else if(rid == R.id.iv_select5){
            iv_select5.setVisibility(View.VISIBLE);
        }
        else if(rid == R.id.iv_select6){
            iv_select6.setVisibility(View.VISIBLE);
        }
        else {
            iv_select1.setVisibility(View.VISIBLE);
        }
    }

    private void hideAll(){
        iv_select1.setVisibility(View.GONE);
        iv_select2.setVisibility(View.GONE);
        iv_select3.setVisibility(View.GONE);
        iv_select4.setVisibility(View.GONE);
        iv_select5.setVisibility(View.GONE);
        iv_select6.setVisibility(View.GONE);
    }

    /**
     * 初始化控件
     */
    private void parentControl(){
        super.imageView2.setImageResource(R.drawable.buttom_tap_2s);
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

