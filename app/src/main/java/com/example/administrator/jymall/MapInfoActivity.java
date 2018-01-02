package com.example.administrator.jymall;

import android.content.Intent;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.example.administrator.jymall.common.TopActivity;
import com.mobsandgeeks.saripaar.Validator;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_map_info)
public class MapInfoActivity extends TopActivity{

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        //super.title.setText("区域服务中心");
        progressDialog.hide();

        Intent i = getIntent();
        String address = i.getStringExtra("address");
        String mobile = i.getStringExtra("mobile");
        String contact = i.getStringExtra("contact");
        String city = i.getStringExtra("city");

        initView();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        //SDKInitializer.initialize(getApplicationContext());
        //setContentView(R.layout.activity_map_info);

        //createMap(address,mobile,contact,city);
    }

    private void initView() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        //获取百度地图
        mBaiduMap = mMapView.getMap();

        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        //开启交通图
        mBaiduMap.setTrafficEnabled(true);

        //卫星地图
        //mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    private void createMap(String address,String mobile,String contact,String city){

    }


}
