package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopSearchTitleActivity;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_search_product)
public class SearchProductListActivity extends TopSearchTitleActivity implements IXListViewListener {

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;

    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();
    private SimpleAdapter sap;
    private Handler mHandler;
    private int start = 1;
    private String orderString = "";
    private String cId="";
    private String priceStart="";
    private String priceEnd = "";
    private String keyword = "";
    private String isfuture = "";
    private String zone = "";
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    ArrayList<String> list = new ArrayList<String>();

    @ViewInject(R.id.listtv)
    private TextView listtv;

    @ViewInject(R.id.tab1)
    private RelativeLayout tab1;
    @ViewInject(R.id.tab2)
    private RelativeLayout tab2;
    @ViewInject(R.id.tab3)
    private RelativeLayout tab3;
    @ViewInject(R.id.tab4)
    private RelativeLayout tab4;
    @ViewInject(R.id.tab5)
    private RelativeLayout tab5;

    @ViewInject(R.id.future)
    private LinearLayout future;
    @ViewInject(R.id.searech)
    private LinearLayout searech;

    @ViewInject(R.id.tab_img2)
    private ImageButton tab_img2;
    @ViewInject(R.id.tab_img3)
    private ImageButton tab_img3;
    @ViewInject(R.id.tab_img4)
    private ImageButton tab_img4;

    @ViewInject(R.id.tab_txt1)
    private TextView tab_txt1;
    @ViewInject(R.id.tab_txt2)
    private TextView tab_txt2;
    @ViewInject(R.id.tab_txt3)
    private TextView tab_txt3;
    @ViewInject(R.id.tab_txt4)
    private TextView tab_txt4;
    @ViewInject(R.id.tab_txt5)
    private TextView tab_txt5;
    @ViewInject(R.id.txt_priceStart)
    private TextView txt_priceStart;
    @ViewInject(R.id.txt_priceEnd)
    private TextView txt_priceEnd;
    @ViewInject(R.id.txt_zone)
    private TextView txt_zone;

    @ViewInject(R.id.f1)
    private TextView f1;
    @ViewInject(R.id.f2)
    private TextView f2;
    @ViewInject(R.id.f3)
    private TextView f3;
    @ViewInject(R.id.f4)
    private TextView f4;

    @ViewInject(R.id.chongzhi)
    private Button chongzhi;
    @ViewInject(R.id.queding)
    private Button queding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        progressDialog.hide();
        Intent i = this.getIntent();
        cId = i.getStringExtra("categoryid");
        keyword= i.getStringExtra("keyword");

        if(FormatUtil.isNoEmpty(keyword)){
            top_searchbar_input_txt.setText(keyword);
        }
        else{
            top_searchbar_input_txt.setText(i.getStringExtra("categoryName"));
        }

        sap = new ProSimpleAdapter(SearchProductListActivity.this, dateMaps,
                R.layout.listview_search_product,
                new String[]{"proName"},
                new int[]{R.id.proName});
        listViewAll.setAdapter(sap);
        listViewAll.setPullLoadEnable(true);
        listViewAll.setXListViewListener(this);
        getDate(true,true);
        mHandler = new Handler();

    }

    @SuppressLint("ResourceAsColor")
    @Event(value={R.id.f1,R.id.f2,R.id.f3,R.id.f4},type=View.OnTouchListener.class)
    private boolean fbtn(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            if(v.getId() == R.id.f1){
                isfuture = "0";
                fcss("1");
                f1.setTextColor(Color.parseColor("#ffffff"));
                f1.setBackgroundResource(R.color.bg_selectvlaue_blue);
            }
            else if(v.getId() == R.id.f2){
                isfuture = "1";
                fcss("2");
                f2.setTextColor(Color.parseColor("#ffffff"));
                f2.setBackgroundResource(R.color.bg_selectvlaue_blue);
            }
            else if(v.getId() == R.id.f3){
                isfuture = "2";
                fcss("3");
                f3.setTextColor(Color.parseColor("#ffffff"));
                f3.setBackgroundResource(R.color.bg_selectvlaue_blue);
            }
            else if(v.getId() == R.id.f4){
                isfuture = "";
                fcss("4");
                f4.setTextColor(Color.parseColor("#ffffff"));
                f4.setBackgroundResource(R.color.bg_selectvlaue_blue);
            }
            future.setVisibility(View.GONE);
            tab_txt1.setTextColor(Color.parseColor("#939393"));
            tab1.setTag("1");
            getDate(true,true);
            return false;
        }
        return true;
    }

    @SuppressLint("ResourceAsColor")
    private void fcss(String f){
        if(!f.equals("1")){
            f1.setTextColor(Color.parseColor("#939393"));
            f1.setBackgroundResource(R.color.bg_selectvlaue_gary);
        }
        if(!f.equals("2")){
            f2.setTextColor(Color.parseColor("#939393"));
            f2.setBackgroundResource(R.color.bg_selectvlaue_gary);
        }
        if(!f.equals("3")){
            f3.setTextColor(Color.parseColor("#939393"));
            f3.setBackgroundResource(R.color.bg_selectvlaue_gary);
        }
        if(!f.equals("4")){
            f4.setTextColor(Color.parseColor("#939393"));
            f4.setBackgroundResource(R.color.bg_selectvlaue_gary);
        }
    }

    @Event(value=R.id.tab1)
    private void btn_tab1(View v){
        css("2");
        tab_txt1.setTextColor(Color.parseColor("#1a3688"));
        orderString = "";
        getDate(true,true);
    }

    @Event(value=R.id.tab2)
    private void btn_tab2(View v){
        css("2");

        tab_txt2.setTextColor(Color.parseColor("#1a3688"));
        if(v.getTag().equals("1")){
            tab_img2.setBackgroundResource(R.drawable.tab_s2);
            v.setTag("2");
            orderString = "salePriceAsc";
        }
        else{
            tab_img2.setBackgroundResource(R.drawable.tab_s1);
            v.setTag("1");
            orderString = "salePriceDesc";
        }
        getDate(true,true);
    }

    @Event(value=R.id.tab3)
    private void btn_tab3(View v){
        css("3");

        tab_txt3.setTextColor(Color.parseColor("#1a3688"));
        if(v.getTag().equals("1")){
            tab_img3.setBackgroundResource(R.drawable.tab_s2);
            v.setTag("2");
            orderString = "saleQuantityAsc";
        }
        else{
            tab_img3.setBackgroundResource(R.drawable.tab_s1);
            v.setTag("1");
            orderString = "saleQuantityDesc";
        }
        getDate(true,true);
    }


    @Event(value={R.id.tab1,R.id.tab4},type=View.OnTouchListener.class)
    private boolean btn1(View v, MotionEvent event){
        if (event.getAction() == event.ACTION_UP) {
            if(v.getId() == R.id.tab1){
                css("1");
                if(v.getTag().equals("1")){
                    tab_txt1.setTextColor(Color.parseColor("#1a3688"));
                    v.setTag("2");
                }
                else{
                    tab_txt1.setTextColor(Color.parseColor("#939393"));
                    v.setTag("1");
                }
                orderString = "";
                getDate(true,true);
            }
            else if(v.getId() == R.id.tab4){
                css("4");
                if(v.getTag().equals("1")){
                    searech.setVisibility(View.VISIBLE);
                    tab_img4.setBackgroundResource(R.drawable.searchup);
                    tab_txt4.setTextColor(Color.parseColor("#1a3688"));

                    v.setTag("2");
                }
                else{
                    searech.setVisibility(View.GONE);
                    tab_img4.setBackgroundResource(R.drawable.searchdown);
                    tab_txt4.setTextColor(Color.parseColor("#939393"));
                    v.setTag("1");
                }
            }

            return false;
        }
        return true;
    }


    public void css(String f){
        if(!f.equals("1")){
            tab_txt1.setTextColor(Color.parseColor("#939393"));
            tab1.setTag("1");
        }
        if(!f.equals("2")){
            tab_txt2.setTextColor(Color.parseColor("#939393"));
            tab_img2.setBackgroundResource(R.drawable.tab_s1);
            tab2.setTag("2");
        }
        if(!f.equals("3")){
            tab_txt3.setTextColor(Color.parseColor("#939393"));
            tab_img3.setBackgroundResource(R.drawable.tab_s1);
            tab3.setTag("2");
        }
        if(!f.equals("4")){
            tab_txt4.setTextColor(Color.parseColor("#939393"));
            searech.setVisibility(View.GONE);
            tab_img4.setBackgroundResource(R.drawable.searchdown);
            tab4.setTag("1");
        }

    }

    @Event(value={R.id.tv_selectBrand},type=View.OnTouchListener.class)
    private boolean selectBrandTouch(View v, MotionEvent arg1) {
        if(arg1.getAction() == KeyEvent.ACTION_UP){
            Context context = SearchProductListActivity.this;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.form_common_list, null);
            ListView myListView = (ListView) layout.findViewById(R.id.formcustomspinner_list);
            MyAdapter adapter = new MyAdapter(context, list);
            myListView.setAdapter(adapter);
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                    //在这里面就是执行点击后要进行的操作,这里只是做一个显示
                    //Toast.makeText(SearchProductListActivity.this, "您点击的是"+list.get(position).toString(), Toast.LENGTH_LONG).show();
                    txt_zone.setText(list.get(position).toString());
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

    @Event(value={R.id.chongzhi,R.id.queding})
    private void btn_click(View v){
        if(v.getId() == R.id.chongzhi){
            txt_priceStart.setText("");
            txt_priceEnd.setText("");
            txt_zone.setText("");
        }
        else if(v.getId() == R.id.queding){
            priceStart =txt_priceStart.getText().toString();
            priceEnd = txt_priceEnd.getText().toString();
            zone = txt_zone.getText().toString();
            searech.setVisibility(View.GONE);
            tab_img4.setBackgroundResource(R.drawable.searchdown);
            tab_txt4.setTextColor(Color.parseColor("#939393"));
            tab4.setTag("1");
            getDate(true,true);
        }
    }

    @Event(value={R.id.top_searchbar_input_txt},type=View.OnKeyListener.class )
    private  boolean searchonKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER){
            keyword = top_searchbar_input_txt.getText().toString();
            getDate(true,true);
            return false;
        }
        return false;
    }

    private void getDate(final boolean isShow,final boolean flag){
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
        maps.put("categoryid", cId);
        maps.put("keyword", keyword);
        maps.put("orderString", orderString);
        maps.put("zone", zone);
        maps.put("brand", zone);
        maps.put("priceStart", ""+priceStart);
        maps.put("priceEnd", ""+priceEnd);


        XUtilsHelper.getInstance().post("app/getMallProductCenterData.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                listtv.setVisibility(View.GONE);
                if(flag){
                    dateMaps.clear();
                    list.clear();
                }
                if(isShow){
                    progressDialog.hide();
                }
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    JSONArray resjarr = (JSONArray)res.get("rows");
                    JSONArray brandjarr = (JSONArray)res.get("brandList");
                    if(resjarr.length()==0 && start == 1) {
                        listtv.setVisibility(View.VISIBLE);
                    }
                    else if(resjarr.length() ==  16 ) {
                        listViewAll.setPullLoadEnable(true);
                    }

                    for(int i=0;i<resjarr.length();i++){
                        Map<String, Object> dateMap = new HashMap<String, Object>();
                        dateMap.put("isfuture", resjarr.getJSONObject(i).get("isfuture"));
                        dateMap.put("proQuality", resjarr.getJSONObject(i).get("proQuality"));
                        dateMap.put("prospec", resjarr.getJSONObject(i).get("prospec"));
                        dateMap.put("proImage", resjarr.getJSONObject(i).get("proImage"));
                        dateMap.put("proName", resjarr.getJSONObject(i).get("proName"));
                        dateMap.put("brand", resjarr.getJSONObject(i).get("brand"));
                        dateMap.put("ownerID", resjarr.getJSONObject(i).get("ownerID"));
                        dateMap.put("salePrice", resjarr.getJSONObject(i).get("salePrice"));
                        dateMap.put("unit", resjarr.getJSONObject(i).get("unit"));
                        dateMap.put("id", resjarr.getJSONObject(i).get("id"));
                        dateMaps.add(dateMap);
                    }

                    for(int i=0;i<brandjarr.length();i++){
                        list.add(brandjarr.getString(i));
                    }
                    sap.notifyDataSetChanged();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });

    }



    /**
     * 产品list
     * @author Administrator
     *
     */

    private void onLoad() {
        listViewAll.stopRefresh();
        listViewAll.stopLoadMore();
        listViewAll.setRefreshTime(DateUtil.DateToString(new Date(), DateStyle.HH_MM_SS));
    }


    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start = 1;
                getDate(true,true);
                onLoad();
            }
        }, 1);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start++;
                getDate(true,false);
                onLoad();
            }
        }, 1);
    }



    public class ProSimpleAdapter  extends SimpleAdapter {

        private LayoutInflater mInflater;

        public ProSimpleAdapter(Context context,
                                List<? extends Map<String, ?>> data, int resource, String[] from,
                                int[] to) {
            super(context, data, resource, from, to);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try{
                if(convertView==null){
                    convertView=mInflater.inflate(R.layout.listview_search_product, null);
                }
                ImageView proImage = (ImageView) convertView.findViewById(R.id.proImage);
                LinearLayout ll_proInfo= (LinearLayout) convertView.findViewById(R.id.ll_proInfo);
                TextView proName= (TextView) convertView.findViewById(R.id.proName);
                TextView prospec= (TextView) convertView.findViewById(R.id.prospec);
                TextView salePrice= (TextView) convertView.findViewById(R.id.salePrice);
                TextView tv_stockQuantity= (TextView) convertView.findViewById(R.id.tv_stockQuantity);
                TextView tv_brand= (TextView) convertView.findViewById(R.id.tv_brand);

                x.image().bind(proImage, dateMaps.get(position).get("proImage").toString());
                proName.setText(dateMaps.get(position).get("proName").toString());
                String brand= dateMaps.get(position).get("brand").toString();
                tv_brand.setText(dateMaps.get(position).get("brand").toString());
                //prospec.setText(dateMaps.get(position).get("prospec").toString());

                String salePricestr= dateMaps.get(position).get("salePrice").toString();
                salePrice.setText(salePricestr.equals("0")? "面议":Html.fromHtml("￥"+salePricestr+ "<font color=\"#b1b1b1\">/"+dateMaps.get(position).get("unit").toString()+"</font>"));
                tv_stockQuantity.setText(dateMaps.get(position).get("proQuality").toString()+dateMaps.get(position).get("unit").toString());

                ll_proInfo.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View arg0, MotionEvent me) {
                        if(me.getAction() == MotionEvent.ACTION_UP){
                            Intent i = new Intent(getApplicationContext(),ProductInfoActivity.class);
                            i.putExtra("id", dateMaps.get(position).get("id").toString());
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

    //自定义的适配器
    class MyAdapter extends BaseAdapter {
        private List<String> mlist;
        private Context mContext;

        public MyAdapter(Context context, List<String> list) {
            this.mContext = context;
            mlist = new ArrayList<String>();
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
            Person person = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.form_dialog_item,null);
                person = new Person();
                person.name = (TextView)convertView.findViewById(R.id.tv_name);
                convertView.setTag(person);
            }else{
                person = (Person)convertView.getTag();
            }
            person.name.setText(list.get(position).toString());
            return convertView;
        }
        class Person{
            TextView name;
        }
    }



}
