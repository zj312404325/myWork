package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.CommonDialog;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyListView;

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

@ContentView(R.layout.activity_order_appraise)
public class OrderAppraiseActivity extends TopActivity {

    private String id; //订单ID
    @ViewInject(R.id.btn_pay)
    private Button btn_pay;

    private JSONObject order;
    private JSONArray orderDtls;
    private JSONArray appraiseJsonArray;

    @ViewInject(R.id.list_productInfo)
    private MyListView list_productInfo;
    public List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();

    String packageLevel;
    String transLevel;
    String showName;
    String skey;

    SimpleAdapter sapinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("订单详情");
        progressDialog.hide();

        Intent i = this.getIntent();
        id = i.getStringExtra("id");
        skey=super.serverKey;
        dataInit();
    }

    private void dataInit(){
        progressDialog.show();

        dateMaps.clear();
        sapinfo = new InfoSimpleAdapter(OrderAppraiseActivity.this, dateMaps,
                R.layout.listview_order_appraise,
                new String[]{"proName"},
                new int[]{R.id.tv_proName});
        sapinfo.notifyDataSetChanged();

        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        maps.put("id", id);
        XUtilsHelper.getInstance().post("app/mallOrderAppraise.htm", maps,new XUtilsHelper.XCallBack(){

            @SuppressLint("NewApi")
            @Override
            public void onResponse(String result)  {
                progressDialog.hide();
                JSONObject res;
                try {
                    res = new JSONObject(result);
                    setServerKey(res.get("serverKey").toString());
                    order = res.getJSONObject("order");
                    orderDtls = order.getJSONArray("orderDtls");

                    for(int j=0;j<orderDtls.length();j++){
                        Map<String, Object> dateMap1 = new HashMap<String, Object>();
                        dateMap1.put("id", orderDtls.getJSONObject(j).get("ID"));
                        dateMap1.put("proName", orderDtls.getJSONObject(j).get("proName"));
                        dateMap1.put("proImgPath", orderDtls.getJSONObject(j).get("proImgPath"));
                        dateMaps.add(dateMap1);
                    }
                    sapinfo = new InfoSimpleAdapter(OrderAppraiseActivity.this, dateMaps,
                            R.layout.listview_order_appraise,
                            new String[]{"proName"},
                            new int[]{R.id.tv_proName});
                    list_productInfo.setAdapter(sapinfo);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
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
        public View getView(int position, View convertView, ViewGroup parent) {
            try{
                if(convertView==null){
                    convertView=mInflater.inflate(R.layout.listview_order_appraise, null);
                }
                ImageView img_proImgPath = (ImageView)convertView.findViewById(R.id.img_proImgPath);
                TextView tv_proName = (TextView)convertView.findViewById(R.id.tv_proName);

                JSONObject temp  =FormatUtil.toJSONObject( mdata.get(position).get("orderInfo").toString());
                img_proImgPath.setBackgroundResource(0);
                XUtilsHelper.getInstance().bindCommonImage(img_proImgPath, temp.getString("proImgPath"), true);

                String proName = temp.getString("proName");
                tv_proName.setText(proName);

            }
            catch(Exception e){
                Log.v("PRO", e.getMessage());
            }
            return super.getView(position, convertView, parent);
        }
    }

    @Event(value=R.id.btn_submit)
    private void submitClick(View v){
        packageLevel="5";
        transLevel="5";
        if(!FormatUtil.isNoEmpty(packageLevel)){
            CommonUtil.alter("请评价商品包装！");
            return ;
        }
        else if(!FormatUtil.isNoEmpty(transLevel)){
            CommonUtil.alter("请评价送货速度！");
            return ;
        }

        new CommonDialog(OrderAppraiseActivity.this, R.style.dialog, "确定提交？", new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    progressDialog.show();
                    Map<String, String> maps= new HashMap<String, String>();
                    maps.put("serverKey", skey);
                    maps.put("id", id);
                    maps.put("packageLevel", packageLevel);
                    maps.put("transLevel", transLevel);
                    maps.put("showName", showName);
                    maps.put("appraiseJsonArray", appraiseJsonArray.toString());
                    XUtilsHelper.getInstance().post("app/saveMallOrderAppraise.htm", maps,new XUtilsHelper.XCallBack(){

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
                                    CommonUtil.alter(res.get("msg").toString());
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
    }

}
