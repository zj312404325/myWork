package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.MyApplication;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.DensityUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_mall_integral)
public class MallIntegralActivity extends TopActivity {

    private List<Map<String, Object>> resMaps = new ArrayList<Map<String, Object>>();
    private SimpleAdapter sap;

    @ViewInject(value=R.id.gw_scorelist)
    private MyGridView gw_scorelist;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("积分商城");
        progressDialog.hide();
        sap = new ProSimpleAdapter(this, resMaps, R.layout.listview_score,
                new String[]{},
                new int[]{});
        gw_scorelist.setAdapter(sap);
        getData();
    }

    private void getData(){
        progressDialog.show();
        resMaps.clear();
        Map<String, String> maps= new HashMap<String, String>();
        maps.put("serverKey", super.serverKey);
        XUtilsHelper.getInstance().post("app/getMallIntegralList.htm", maps,new XUtilsHelper.XCallBack(){

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
                        MyApplication.getInstance().finishActivity();
                    }
                    else{
                        JSONArray jarr = res.getJSONArray("data");
                        for(int i=0;i<jarr.length();i++){
                            Map<String,Object> maptemp = new HashMap<String, Object>();
                            JSONObject jobj = jarr.getJSONObject(i);
                            maptemp.put("gift", jobj.toString());
                            maptemp.put("id", jobj.get("id"));
                            maptemp.put("giftname", jobj.get("gift"));
                            maptemp.put("quantity", jobj.get("quantity"));
                            maptemp.put("giftIntegral", jobj.get("giftIntegral"));
                            maptemp.put("unit", jobj.get("unit"));
                            maptemp.put("picurl", jobj.get("picurl"));
                            maptemp.put("giftno", jobj.get("giftNo"));
                            resMaps.add(maptemp);
                        }
                        sap.notifyDataSetChanged();
                    }

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
        public ProSimpleAdapter(Context context,
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
                    convertView=mInflater.inflate(R.layout.listview_score, null);
                    holder=new MallIntegralActivity.ViewHolder();
                    x.view().inject(holder,convertView);
                    convertView.setTag(holder);
                }
                else{
                    holder=(MallIntegralActivity.ViewHolder) convertView.getTag();
                }
                holder.giftName.setText(resMaps.get(position).get("giftname").toString());
                holder.giftScore.setText(resMaps.get(position).get("giftIntegral").toString()
                        +"积分");
                holder.av_quantity.setText(resMaps.get(position).get("quantity").toString()
                        +resMaps.get(position).get("unit").toString());
                XUtilsHelper.getInstance().bindCommonImage(holder.giftImg, resMaps.get(position).get("picurl").toString(), true);

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
                            Intent i = new Intent(getApplicationContext(),GiftDetailActivity.class);
                            i.putExtra("id", resMaps.get(position).get("id").toString());
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
        @ViewInject(R.id.pro)
        private RelativeLayout pro;
        @ViewInject(R.id.giftImg)
        private ImageView giftImg;
        @ViewInject(R.id.giftName)
        private TextView giftName;
        @ViewInject(R.id.giftScore)
        private TextView giftScore;
        @ViewInject(R.id.av_quantity)
        private TextView av_quantity;
    }

}
