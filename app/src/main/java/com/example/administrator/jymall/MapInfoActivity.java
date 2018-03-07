package com.example.administrator.jymall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.administrator.jymall.common.TopActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_map_info)
public class MapInfoActivity extends TopActivity implements OnGetGeoCoderResultListener{
    public LocationClient mLocationClient;
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate=true;

    private String  city;
    private String  address;
    private String  mobile;
    private String  contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*mLocationClient=new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());*/
        SDKInitializer.initialize(getApplicationContext());//创建一个LocationClient的实例，
        // LocationClient的构建函数接收一个Context参数，这里调用getApplicationContext(),方法来获取一个全局的Context参数并传入。
        // 然后调用LocationClient的registerLocationListener（）方法来注册一个定位监听器，当获取到位置信息的时候，就会回调这个定位监听器。


        Intent i = getIntent();
        address = i.getStringExtra("address");
        mobile = i.getStringExtra("mobile");
        contact = i.getStringExtra("contact");
        city = i.getStringExtra("city");

        setContentView(R.layout.activity_map_info);

        x.view().inject(this);
        super.title.setText("地图信息");
        progressDialog.hide();

        mapView= (MapView) findViewById(R.id.bmapView);
        baiduMap=mapView.getMap();

        /*baiduMap.setMyLocationEnabled(true);
        positionText= (TextView) findViewById(R.id.position_text_view);
        List<String> permissionList=new ArrayList<>();*/

        // 初始化搜索模块，注册事件监听
        /**
         * public static GeoCoder newInstance()
         * 新建地理编码查询
         * @return 地理编码查询对象
         * */
        mSearch = GeoCoder.newInstance();
        /**
         * public void setOnGetGeoCodeResultListener(OnGetGeoCoderResultListener listener)
         * 设置查询结果监听者
         * @param listener - 监听者
         *
         * 需要实现OnGetGeoCoderResultListener的
         * onGetGeoCodeResult(GeoCodeResult result)和onGetReverseGeoCodeResult(ReverseGeoCodeResult result)
         * 方法
         * */
        mSearch.setOnGetGeoCodeResultListener(this);

        showCity(city,address);

        /*if(!permissionList.isEmpty()){
            String[]permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MapInfoActivity.this,permissions,1);
        }else{
            requestLocation();
        }*/
    }

    /**
     * 发起搜索
     *
     * @param
     */
    private void showCity(String city, String address) {
            // 地理位置搜索
            //city(editCity.getText().toString())可以不写，GeoCodeOption共有两个方法，一个是查询城市，一个是查询地址；
            //但是address（）方法必须写
            mSearch.geocode(new GeoCodeOption().city(city).address(address));
    }

    /**
     * 地理位置查询回调方法
     * @param result 返回的经纬度
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MapInfoActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        baiduMap.clear();
        //获取地址并且在地图上标注
        baiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_ali_location)));
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
    }

    /**
     * 反地理位置查询回调方法
     * @param result 返回的地理位置
     */
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MapInfoActivity.this, "抱歉，未能找到地址", Toast.LENGTH_LONG).show();
            return;
        }
        baiduMap.clear();
        //获取地址并且标注
        baiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_ali_location)));
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));

    }

    //ACCESS_FINE_LOCATION、ACCESS_COARSE_LOCATION、READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE这4个权限是危险权限
    // 需要进行运行时权限处理，不过ACCESS_FINE_LOCATION、ACCESS_COARSE_LOCATION属于同一个权限组，两者申请其一就可以了。
    // 这里运用了一比较新的用法在运行时一次性申请3个权限。首先创建了一个List集合，然后依次判断这3个权限有没有被授权，
    // 如果没有被授权，就添加到List集合中，最后将List转换成数组，在调用ActivityCompat.requestPermissions()方法一次性申请。
    /*private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }*/

   /* private void navigateTo(BDLocation location){
        if(isFirstLocate){
            LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());//LatLng类用于存放经纬度
            // 第一个参数是纬度值，第二个参数是精度值。这里输入的是本地位置。
            MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(ll);//将LatLng对象传入
            baiduMap.animateMapStatus(update);
            update=MapStatusUpdateFactory.zoomTo(16f);//百度地图缩放范围，限定在3-19之间，可以去小数点位值
            // 值越大，地图显示的信息越精细
            baiduMap.animateMapStatus(update);
            isFirstLocate=false;//防止多次调用animateMapStatus()方法，以为将地图移动到我们当前位置只需在程序
            // 第一次定位的时候调用一次就可以了。
        }
        MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData=locationBuilder.build();
        baiduMap.setMyLocationData(locationData);//获取我们的当地位置
    }*/

    /*private void initLocation() {
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(5000);//表示每5秒更新一下当前位置
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        // Hight_Accuracy表示高精确度模式，会在GPS信号正常的情况下优先使用GPS定位，在无法接收GPS信号的时候使用网络定位。
        // Battery_Saving表示节电模式，只会使用网络进行定位。
        // Device_Sensors表示传感器模式，只会使用GPS进行定位。
        mLocationClient.setLocOption(option);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mLocationClient.stop();//销毁之前，用stop()来停止定位
        mapView.onDestroy();
        mSearch.destroy();
        baiduMap.setMyLocationEnabled(false);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!= PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }//onRequestPermissionsResult()方法中，对权限申请结果进行逻辑判断。这里使用一个循环对每个权限进行判断，
        // 如果有任意一个权限被拒绝了，那么就会直接调用finish()方法关闭程序，只有当所有的权限被用户同意了，才会
        // 调用requestPermissions()方法开始地理位置定位。
    }*/

    /*public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location.getLocType()==BDLocation.TypeGpsLocation||location.getLocType()==BDLocation.TypeNetWorkLocation){
                navigateTo(location);
            }
        }
    }*/


}
