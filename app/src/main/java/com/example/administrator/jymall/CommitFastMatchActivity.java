package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.jymall.common.CommonDialog;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.DateStyle;
import com.example.administrator.jymall.util.DateUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.ImageFactory;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyConfirmDialog;
import com.example.administrator.jymall.view.XListView;
import com.example.administrator.jymall.view.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_order_appraise)
public class CommitFastMatchActivity extends TopActivity {

    private JSONObject order;
    private JSONArray orderDtls;
    private String fastMatchJsonArray;

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;
    @ViewInject(R.id.listtv)
    private TextView listtv;
    @ViewInject(R.id.ll_addView)
    private LinearLayout addHotelNameView;

    private List<Map<String, Object>> dateMaps= new ArrayList<Map<String, Object>>();

    private String skey;
    private String type;

    SimpleAdapter sap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        super.title.setText("标准件快速配");
        progressDialog.hide();
        skey=super.serverKey;
    }

    @Event(value=R.id.btn_submit)
    private void submitClick(View v){
        try {
            type = "1";
            getCommentJson();

            new CommonDialog(CommitFastMatchActivity.this, R.style.dialog, "确定提交？", new CommonDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        progressDialog.show();
                        Map<String, String> maps = new HashMap<String, String>();
                        maps.put("serverKey", skey);
                        maps.put("type", type);
                        maps.put("fastMatchJsonArray", fastMatchJsonArray);
                        XUtilsHelper.getInstance().post("app/saveFastMatch.htm", maps, new XUtilsHelper.XCallBack() {

                            @SuppressLint("NewApi")
                            @Override
                            public void onResponse(String result) {
                                progressDialog.hide();
                                JSONObject res;
                                try {
                                    res = new JSONObject(result);
                                    setServerKey(res.get("serverKey").toString());
                                    if (res.get("d").equals("n")) {
                                        CommonUtil.alter(res.get("msg").toString());
                                    } else {
                                        Intent i = new Intent(getApplicationContext(), CommitFastMatchOkActivity.class);
                                        startActivity(i);
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
        catch (Exception e){
            e.printStackTrace();
            Log.i("APPRAISE", "Exception");
        }
    }

    /**
     * Item排序
     */
    private void sortHotelViewItem() {
        //获取LinearLayout里面所有的view
        for (int i = 0; i < addHotelNameView.getChildCount(); i++) {
            final View childAt = addHotelNameView.getChildAt(i);
            final Button btn_remove = (Button) childAt.findViewById(R.id.btn_addHotel);
            btn_remove.setText("删除");
            btn_remove.setTag("remove");//设置删除标记
            btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //从LinearLayout容器中删除当前点击到的ViewItem
                    addHotelNameView.removeView(childAt);
                }
            });
            //如果是最后一个ViewItem，就设置为添加
            if (i == (addHotelNameView.getChildCount() - 1)) {
                Button btn_add = (Button) childAt.findViewById(R.id.btn_addHotel);
                btn_add.setText("+新增");
                btn_add.setTag("add");
                btn_add.setOnClickListener(this);
            }
        }
    }

    //添加ViewItem
    private void addViewItem(View view) {
        if (addHotelNameView.getChildCount() == 0) {//如果一个都没有，就添加一个
            View hotelEvaluateView = View.inflate(this, R.layout.item_hotel_evaluate, null);
            Button btn_add = (Button) hotelEvaluateView.findViewById(R.id.btn_addHotel);
            btn_add.setText("+新增");
            btn_add.setTag("add");
            btn_add.setOnClickListener(this);
            addHotelNameView.addView(hotelEvaluateView);
        } else if (((String) view.getTag()).equals("add")) {//如果有一个以上的Item,点击为添加的Item则添加
            View hotelEvaluateView = View.inflate(this, R.layout.item_hotel_evaluate, null);
            addHotelNameView.addView(hotelEvaluateView);
            sortHotelViewItem();
        }
    }

    private void getCommentJson() throws IOException{
        fastMatchJsonArray="[";
        for(int i=0; i<ll_addView.getChildCount();i++){
            String pic1="";
            String pic2="";
            String pic3="";
            String pic4="";
            String pic5="";
            String productLevel="";
            String remark="";
            LinearLayout layout =(LinearLayout) ll_addView.getChildAt(i);
            RatingBar rb_productLevel=layout.findViewById(R.id.rb_productLevel);
            EditText et_remark=layout.findViewById(R.id.et_remark);
            remark = java.net.URLEncoder.encode(et_remark.getText().toString(),"UTF-8");
            productLevel=FormatUtil.toString(rb_productLevel.getRating());
            if(i==ll_addView.getChildCount()-1){
                fastMatchJsonArray += "{\"productid\":\""+ dateMaps.get(i).get("proID").toString() +"\",\"productLevel\":\""+ productLevel +"\",\"remark\":\""+ remark +"\",\"pic1\":\""+ pic1 +"\",\"pic2\":\""+ pic2 +"\",\"pic3\":\""+ pic3 +"\",\"pic4\":\""+ pic4 +"\",\"pic5\":\""+ pic5 +"\"}";
            }
            else{
                fastMatchJsonArray += "{\"productid\":\""+ dateMaps.get(i).get("proID").toString() +"\",\"productLevel\":\""+ productLevel +"\",\"remark\":\""+ remark +"\",\"pic1\":\""+ pic1 +"\",\"pic2\":\""+ pic2 +"\",\"pic3\":\""+ pic3 +"\",\"pic4\":\""+ pic4 +"\",\"pic5\":\""+ pic5 +"\"},";
            }
        }
        fastMatchJsonArray+="]";
    }

}
