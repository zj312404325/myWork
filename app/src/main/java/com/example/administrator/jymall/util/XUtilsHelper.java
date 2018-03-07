package com.example.administrator.jymall.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.example.administrator.jymall.R;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.Map;




public class XUtilsHelper {

	private volatile static XUtilsHelper instance;
    private Handler handler;
    private ImageOptions options;

    private XUtilsHelper() {
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static XUtilsHelper getInstance() {
        if (instance == null) {
            synchronized (XUtilsHelper.class) {
                if (instance == null) {
                    instance = new XUtilsHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 异步get请求
     *
     * @param url
     * @param maps
     * @param callBack
     */
    public void get(String url, Map<String, String> maps, final XCallBack callBack) {
        RequestParams params = new RequestParams(CommonUtil.getStrings(R.string.site_url)+url);
        if (maps != null && !maps.isEmpty()) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                params.addQueryStringParameter(entry.getKey(), entry.getValue());
            }
        }
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                onSuccessResponse(result, callBack);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 异步post请求
     *
     * @param url
     * @param maps
     * @param callback
     */
    public void post(String url, Map<String, String> maps, final XCallBack callback) {
        RequestParams params = new RequestParams(CommonUtil.getStrings(R.string.site_url)+url);
        if (maps != null && !maps.isEmpty()) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                params.addBodyParameter(entry.getKey(), entry.getValue());
            }
        }
        params.setConnectTimeout(100000);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                onSuccessResponse(result, callback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            	ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * 带缓存数据的异步 get请求
     *
     * @param url
     * @param maps
     * @param pnewCache
     * @param callback
     */
    public void getCache(String url, Map<String, String> maps, final boolean pnewCache, final XCallBack callback) {

        RequestParams params = new RequestParams(CommonUtil.getStrings(R.string.site_url)+url);
        if (maps != null && !maps.isEmpty()) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                params.addQueryStringParameter(entry.getKey(), entry.getValue());
            }
        }
        x.http().get(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                onSuccessResponse(result, callback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            	ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public boolean onCache(String result) {
                boolean newCache = pnewCache;
                if (newCache) {
                    newCache = !newCache;
                }
                if (!newCache) {
                    newCache = !newCache;
                    onSuccessResponse(result, callback);
                }
                return newCache;
            }
        });
    }

    /**
     * 带缓存数据的异步 post请求
     *
     * @param url
     * @param maps
     * @param pnewCache
     * @param callback
     */
    public void postCache(String url, Map<String, String> maps, final boolean pnewCache, final XCallBack callback) {
        RequestParams params = new RequestParams(CommonUtil.getStrings(R.string.site_url)+url);
        if (maps != null && !maps.isEmpty()) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                params.addBodyParameter(entry.getKey(), entry.getValue());
            }
        }

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                onSuccessResponse(result, callback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            	ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }


            @Override
            public void onFinished() {

            }

            @Override
            public boolean onCache(String result) {
                boolean newCache = pnewCache;
                if (newCache) {
                    newCache = !newCache;
                }
                if (!newCache) {
                    newCache = !newCache;
                    onSuccessResponse(result, callback);
                }
                return newCache;
            }
        });
    }


    /**
     * 带有前缀的图片显示
     *
     * @param iv
     * @param url
     * @param option
     */
    public void bindUrlImage(ImageView iv, String url, boolean option) {
        try{
            if (option) {
                options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.loading).setFailureDrawableId(R.drawable.noimg).build();
                x.image().bind(iv, url, options);
            } else {
                x.image().bind(iv, url);
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    /**
     * 正常图片显示
     *
     * @param iv
     * @param url
     * @param option
     */
    public void bindCommonImage(ImageView iv, String url, boolean option) {
    	try{
        if (option) {
            options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.loading).setFailureDrawableId(R.drawable.noimg).build();
            x.image().bind(iv, CommonUtil.getStrings(R.string.img_url)+url, options);
        } else {
            x.image().bind(iv, CommonUtil.getStrings(R.string.img_url)+url);
        }
    	}
    	catch(Exception e){e.printStackTrace();}
    }

    /**
     * 圆形图片显示
     *
     * @param iv
     * @param url
     * @param option
     */
    public void bindCircularImage(ImageView iv, String url, boolean option) {
        if (option) {
            options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.loading).setFailureDrawableId(R.drawable.noimg).setCircular(true).build();
            x.image().bind(iv, CommonUtil.getStrings(R.string.img_url)+url, options);
        } else {
            x.image().bind(iv, CommonUtil.getStrings(R.string.img_url)+url);
        }
    }


    /**
     * 文件上传
     *
     * @param url
     * @param maps
     * @param file
     * @param callback
     */
    public void upLoadFile(String url, Map<String, String> maps, Map<String, File> file, final XCallBack callback) {
        RequestParams params = new RequestParams(CommonUtil.getStrings(R.string.img_url)+url);
        if (maps != null && !maps.isEmpty()) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                params.addBodyParameter(entry.getKey(), entry.getValue());
            }
        }
        if (file != null) {
            for (Map.Entry<String, File> entry : file.entrySet()) {
                params.addBodyParameter(entry.getKey(), entry.getValue().getAbsoluteFile());
            }
        }
        // 有上传文件时使用multipart表单, 否则上传原始文件流.
        params.setMultipart(true);
        params.setConnectTimeout(100000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                onSuccessResponse(result, callback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            	ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    /**
     * 文件下载
     *
     * @param url
     * @param maps
     * @param callBack
     */
    public void downLoadFile(String url, Map<String, String> maps, final XDownLoadCallBack callBack) {

        RequestParams params = new RequestParams(url);
        if (maps != null && !maps.isEmpty()) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                params.addBodyParameter(entry.getKey(), entry.getValue());
            }
        }
        params.setAutoRename(true);// 断点续传
        params.setSaveFilePath(CommonUtil.getStrings(R.string.save_file_path));
        x.http().post(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(final File result) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) {
                            callBack.onResponse(result);
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            	ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) {
                            callBack.onFinished();
                        }
                    }
                });
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(final long total, final long current, final boolean isDownloading) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) {
                            callBack.onLoading(total, current, isDownloading);
                        }
                    }
                });
            }
        });

    }


    /**
     * 异步get请求返回结果,json字符串
     *
     * @param result
     * @param callBack
     */
    private void onSuccessResponse(final String result, final XCallBack callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onResponse(result);
                }
            }
        });
    }


    public interface XCallBack {
        void onResponse(String result);
    }


    public interface XDownLoadCallBack extends XCallBack {
        void onResponse(File result);

        void onLoading(long total, long current, boolean isDownloading);

        void onFinished();
    }
}
