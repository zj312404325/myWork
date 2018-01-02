package com.example.administrator.jymall.util;

import com.example.administrator.jymall.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {  
    
    private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象  
  
    static {  
    	client.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        client.setTimeout(10000); // 设置链接超时，如果不设置，默认为10s  
    }  
  
    // 用一个完整url获取一个string对象  
    public static void get(String urlString, AsyncHttpResponseHandler res)   
    {  
        client.post(CommonUtil.getStrings(R.string.site_url)+urlString, res);          
    }  
  
    // url里面带参数  
    public static void get(String urlString, RequestParams params,  
            AsyncHttpResponseHandler res){
       client.post(CommonUtil.getStrings(R.string.site_url)+urlString, params, res);
    }  
  
    // 不带参数，获取json对象或者数组  
    public static void get(String urlString, JsonHttpResponseHandler res) {
        client.post(CommonUtil.getStrings(R.string.site_url)+urlString, res);
    }  
  
    // 带参数，获取json对象或者数组  
    public static void get(String urlString, RequestParams params,  
            JsonHttpResponseHandler res) {  
        client.post(CommonUtil.getStrings(R.string.site_url)+urlString, params, res);  
        
  
    }  
  
    // 下载数据使用，会返回byte数据  
    public static void get(String uString, BinaryHttpResponseHandler bHandler) {
       client.post(CommonUtil.getStrings(R.string.site_url)+uString, bHandler);
    }  
  
    public static AsyncHttpClient getClient(){  
        return client;  
    }  
  
}  