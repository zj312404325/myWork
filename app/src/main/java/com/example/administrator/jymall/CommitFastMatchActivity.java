package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.example.administrator.jymall.common.CommonDialog;
import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.XListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_commit_fastmatch)
public class CommitFastMatchActivity extends TopActivity {

    private String fastMatchJsonArray;

    @ViewInject(R.id.xListView)
    public XListView listViewAll = null ;

    @ViewInject(R.id.ll_addView)
    private LinearLayout ll_addView;

    @ViewInject(R.id.top_add)
    private ImageView top_add;

    private String skey;
    private String type;

    SimpleAdapter sap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_fastmatch);
        x.view().inject(this);
        super.title.setText("标准件快速配");
        top_add.setVisibility(View.VISIBLE);
        progressDialog.hide();
        skey=super.serverKey;
        //默认添加一个Item
        addViewItem(null);
    }

    @Event(value=R.id.btn_submit)
    private void submitClick(View v){
        try {
            type = "0";
            getFastMatchJson();

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

    @Event(value=R.id.top_add)
    private void addClick(View v){
        addViewItem(v);
    }

    /**
     * Item排序
     */
    private void sortViewItem() {
        //获取LinearLayout里面所有的view
        for (int i = 0; i < ll_addView.getChildCount(); i++) {
            final View childAt = ll_addView.getChildAt(i);
            final Button btn_delete = (Button) childAt.findViewById(R.id.btn_delete);
            btn_delete.setTag("remove");//设置删除标记
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //从LinearLayout容器中删除当前点击到的ViewItem
                    ll_addView.removeView(childAt);
                }
            });

            if (i == (ll_addView.getChildCount()-1)) {
                btn_delete.setVisibility(View.GONE);
            }
            else{
                btn_delete.setVisibility(View.VISIBLE);
            }
        }
    }

    //添加ViewItem
    private void addViewItem(View view) {
        if (ll_addView.getChildCount() == 0) {//如果一个都没有，就添加一个
            View newView = View.inflate(this, R.layout.item_fastmatch, null);
            top_add.setTag("add");
            ll_addView.addView(newView);
        }
        else{//如果有一个以上的Item,点击为添加的Item则添加
            View newView = View.inflate(this, R.layout.item_fastmatch, null);
            ll_addView.addView(newView);
            sortViewItem();
        }
    }

    private void getFastMatchJson(){
        fastMatchJsonArray="[";
        for(int i=0; i<ll_addView.getChildCount();i++){
            String proName="";
            String brand="";
            String proquantity="";
            String model="";
            String spec="";
            String quantity="";
            String unit="";
            String proDesc="";
            String require="";

            String pic1="";
            String pic2="";
            String pic3="";
            String pic4="";
            String pic5="";

            RelativeLayout layout =(RelativeLayout) ll_addView.getChildAt(i);
            EditText et_proName=layout.findViewById(R.id.et_proName);
            EditText et_brand=layout.findViewById(R.id.et_brand);
            EditText et_proQuality=layout.findViewById(R.id.et_proQuality);
            EditText et_model=layout.findViewById(R.id.et_model);
            EditText et_spec=layout.findViewById(R.id.et_spec);
            EditText et_quantity=layout.findViewById(R.id.et_quantity);
            EditText et_unit=layout.findViewById(R.id.et_unit);
            EditText et_proDesc=layout.findViewById(R.id.et_proDesc);
            EditText et_require=layout.findViewById(R.id.et_require);
            ImageView iv_pic1=layout.findViewById(R.id.iv_pic1);
            ImageView iv_pic2=layout.findViewById(R.id.iv_pic2);
            ImageView iv_pic3=layout.findViewById(R.id.iv_pic3);

            proName=et_proName.getText().toString();
            brand=et_brand.getText().toString();
            proquantity=et_proQuality.getText().toString();
            model=et_model.getText().toString();
            spec=et_spec.getText().toString();
            quantity=et_quantity.getText().toString();
            unit=et_unit.getText().toString();
            proDesc=et_proDesc.getText().toString();
            require=et_require.getText().toString();

            if(i==ll_addView.getChildCount()-1){
                fastMatchJsonArray += "{\"proName\":\""+ proName +"\",\"brand\":\""+ brand +"\",\"proQuality\":\""+ proquantity +"\",\"model\":\""+ model +"\",\"proSpec\":\""+ spec +"\",\"quantity\":\""+ quantity +"\",\"unit\":\""+ unit +"\",\"proDesc\":\""+ proDesc +"\",\"require\":\""+ require +"\",\"pic1\":\""+ pic1 +"\",\"pic2\":\""+ pic2 +"\",\"pic3\":\""+ pic3 +"\"}";
            }
            else{
                fastMatchJsonArray += "{\"proName\":\""+ proName +"\",\"brand\":\""+ brand +"\",\"proQuality\":\""+ proquantity +"\",\"model\":\""+ model +"\",\"proSpec\":\""+ spec +"\",\"quantity\":\""+ quantity +"\",\"unit\":\""+ unit +"\",\"proDesc\":\""+ proDesc +"\",\"require\":\""+ require +"\",\"pic1\":\""+ pic1 +"\",\"pic2\":\""+ pic2 +"\",\"pic3\":\""+ pic3 +"\"},";
            }
        }
        fastMatchJsonArray+="]";
    }

}
