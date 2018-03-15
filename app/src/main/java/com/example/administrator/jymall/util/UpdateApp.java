package com.example.administrator.jymall.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.example.administrator.jymall.BuildConfig;
import com.example.administrator.jymall.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//import com.example.administrator.helloworld.AddOrderActivity;
//import com.example.administrator.helloworld.UserCenterActivity;

public class UpdateApp {
	/* 版本号检查路径 */
    private String checkPath;
    /* 新版本号 */
    private int newVerCode;
    /* 新版本名称 */
    private String newVerName;
    /* APK 下载路径 */
    private String downloadPath;
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    /* 上下文 */
    private Context mContext;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    
    /*下载更新提示*/
    private String versionInfo;

    protected static final String LOG_TAG = "UpdateApp";

    public UpdateApp(String url,Context context){
    	this.checkPath = url;
    	this.mContext = context;
    	checkAndUpdate();
    }
    
    

    /**
     * 检查更新
     */
    private void checkAndUpdate() {
        Runnable runnable = new Runnable() {
            public void run() {
                getServerVerInfo();
            }
        };
        runnable.run();
    }

    /**
     * 获取应用当前版本代码versionCode
     *
     * @return
     */
    private int getCurrentVerCode() {
        String packageName = this.mContext.getPackageName();
        int currentVer = -1;
        try {
            currentVer = this.mContext.getPackageManager().getPackageInfo(
                    packageName, 0).versionCode;
        } catch (NameNotFoundException e) {
            Log.d(LOG_TAG, "获取应用当前版本代码versionCode异常：" + e.toString());
        }
        return currentVer;
    }

    /**
     * 获取应用当前版本代码versionName
     *
     * @return
     */
    private String getCurrentVerName() {
        String packageName = this.mContext.getPackageName();
        String currentVer = "";
        try {
            currentVer = this.mContext.getPackageManager().getPackageInfo(
                    packageName, 0).versionName;
        } catch (NameNotFoundException e) {
            Log.d(LOG_TAG, "获取应用当前版本代码versionName异常：" + e.toString());
        }
        return currentVer;
    }

    /**
     * 获取服务器上的版本信息
     *
     * @return
     * @throws Exception
     */
    private void getServerVerInfo() {
    	/*XUtilsHelper.getInstance().post("checkPath", null,new XUtilsHelper.XCallBack(){

			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				JSONArray response;
				try {
					response = new JSONArray(result);
					newVerCode = response.getJSONObject(0).getInt("verCode");
	                newVerName = response.getJSONObject(0).getString("verName");
	                downloadPath = response.getJSONObject(0).getString("apkPath");
	                versionInfo = response.getJSONObject(0).getString("versionInfo");
	                int currentVerCode = getCurrentVerCode();
                    if (newVerCode > currentVerCode) {
                        showNoticeDialog();
                    }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});*/
    	
    	
          	HttpUtil.get(checkPath, new JsonHttpResponseHandler(){

          		@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONArray response) {
          			try{
						newVerCode = response.getJSONObject(0).getInt("verCode");
		                newVerName = response.getJSONObject(0).getString("verName");
		                downloadPath = response.getJSONObject(0).getString("apkPath");
		                versionInfo = response.getJSONObject(0).getString("versionInfo");
		                int currentVerCode = getCurrentVerCode();
	                    if (newVerCode > currentVerCode) {
	                        showNoticeDialog();
	                    }
					}
					catch(Exception e){
						Log.e(LOG_TAG, e.getMessage());
					}	
					super.onSuccess(statusCode, headers, response);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					// TODO Auto-generated method stub
          			CommonUtil.alter(responseString);
					super.onFailure(statusCode, headers, responseString, throwable);
				}
          		
				     		
        	});        	
          	
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog() {
        Runnable runnable = new Runnable() {
            public void run() {
                // 构造对话框
                Builder builder = new Builder(mContext);
                builder.setTitle(R.string.soft_update_title);
                builder.setMessage(versionInfo);
                // 更新
                builder.setPositiveButton(R.string.soft_update_updatebtn,
                        new DialogInterface.OnClickListener() {
                	@Override        
                	public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                                // 显示下载对话框
                                showDownloadDialog();
                            }

							
							
                        });
                // 稍后更新
                builder.setNegativeButton(R.string.soft_update_later,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        });
                Dialog noticeDialog = builder.create();
                noticeDialog.show();
            }
        };
        runnable.run();
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        // 构造软件下载对话框
        Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
//        mProgress = new ProgressBar(mContext,null,android.R.attr.progressBarStyleHorizontal);    //进度条，在下载的时候实时更新进度，提高用户友好度        
//        builder.setView(mProgress);
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.appupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        mProgress.setIndeterminate(false);
        builder.setView(v);
        // 取消更新
        builder.setNegativeButton(R.string.soft_update_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 设置取消状态
                        cancelUpdate = true;
                    }
                });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        // 现在文件
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
        	try{
	            switch (msg.what) {
	            case 1:
	            	mProgress.setMax(msg.arg1);
	            	break;
	            // 正在下载
	            case 2:
	                // 设置进度条位置
	                mProgress.setProgress(msg.arg1);
	                break;
	            case 3:
	                // 安装文件
	                installApk();
	                break;
	            default:
	                break;
	            }
        	}
        	catch(Exception e){ CommonUtil.alter(e.getMessage());}
        };
    };

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory()
                            + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(downloadPath);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    length = 9999;
                    Message msg = new Message();
                    msg.what = 1;
                    msg.arg1 = length;
                    mHandler.sendMessage(msg);
                    
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, newVerName);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                      do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        Message msg1 = new Message();
                        msg1.what = 2;
                        msg1.arg1 = progress;
                        mHandler.sendMessage(msg1);
                       // mHandler.sendEmptyMessage(progress);
                        if (numread <= 0) {
                            // 下载完成
                        	Message msg2 = new Message();
                        	msg2.what = 3;
                            mHandler.sendMessage(msg2);
                           // mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                } else {
                    Log.d(LOG_TAG, "手机没有SD卡");
                }
            } catch (MalformedURLException e) {
            	//CommonUtil.alter("下载文件线程异常MalformedURLException：" );
                Log.i("update", "下载文件线程异常MalformedURLException：");
            } catch (IOException e) {
                Log.i("update", "下载文件线程异常IOException：");
                //CommonUtil.alter( "下载文件线程异常IOException：" );
            }catch(Exception e){
                Log.i("update", "e.getMessage()");
            	//CommonUtil.alter(e.getMessage());
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    };

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, newVerName);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);

        //判断是否是AndroidN以及更高的版本 解决安卓7.0无法安装更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", apkfile);
            i.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            //i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        /*i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");*/

        mContext.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
