package com.example.administrator.helloworld;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.helloworld.common.MyApplication;
import com.example.administrator.helloworld.common.TopActivity;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.FormatUtil;
import com.example.administrator.helloworld.util.ImageFactory;
import com.example.administrator.helloworld.util.XUtilsHelper;
import com.example.administrator.helloworld.view.MyConfirmDialog;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

@ContentView(R.layout.activity_recharge)
public class RechargeActivity extends TopActivity {

	@ViewInject(R.id.btn_pay)
	private Button btn_pay;
	
	@ViewInject(R.id.et_rechargemoney)
	private EditText et_rechargemoney;
	
	private String rechargedocurl;
	
	@ViewInject(R.id.iv_businesslicense)
	private ImageView iv_businesslicense;
	private String TEMP_IMAGE_PATH;  	
	private String TEMP_IMAGE_PATH1= Environment.getExternalStorageDirectory().getPath()+"/temp1.png"; 
	private Bitmap bitmap1 = null;
	private MyConfirmDialog mcd1 = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("线下充值");
		super.progressDialog.hide();
	}


	@Event(R.id.btn_pay)
	private void btntopay(View v){
	
		if(et_rechargemoney.getText().length()<1 ){
			CommonUtil.alter("充值金额必须填写！");return ;
		}
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("rechargemoney", et_rechargemoney.getText().toString());
		maps.put("bankno", "533958898073");
		maps.put("bankname", "6");
		maps.put("rechargedocurl",rechargedocurl);
		XUtilsHelper.getInstance().post("app/saveRecharge.htm", maps,new XUtilsHelper.XCallBack(){

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
						CommonUtil.alter("充值成功，等待确认后到账");
						setResult(RESULT_OK);
						finish();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
	}
	
	@Event(value=R.id.iv_businesslicense,type=View.OnTouchListener.class)
	private boolean businesslicenseclick(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(mcd1==null){			
				mcd1=new MyConfirmDialog(RechargeActivity.this, "上传充值凭证", "拍照上传", "本地上传");
				mcd1.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {					
					@Override
					public void doConfirm() {					   
					    TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";  
					    Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
					    Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));  
					    intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);  
					    startActivityForResult(intent1,201);  
					}					
					@Override
					public void doCancel() {  
	
					    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
					    intent.addCategory(Intent.CATEGORY_OPENABLE);
					    intent.setType("image/*");
					    startActivityForResult(intent, 101);
					}
				});
			}
			mcd1.show();			
			return false;
		}
		return true;
	}
	
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    
		if(resultCode==RESULT_OK){ 
			//营业执照
	           if(requestCode==101&&data!=null){  
	        	   progressDialog.show();
	        	   mcd1.dismiss();
	        	   Uri uri = data.getData();  
	               if(bitmap1 != null)//如果不释放的话，不断取图片，将会内存不够  
                	   bitmap1.recycle();  
	               TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
	               ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap1 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
	               iv_businesslicense.setImageBitmap(bitmap1);

	               Map<String, String> maps = new HashMap<String, String>();
	               maps.put("fileUploadeFileName", TEMP_IMAGE_PATH1.substring(TEMP_IMAGE_PATH1.lastIndexOf("/")+1));
	               maps.put("pathType","company");
	               Map<String, File> file = new HashMap<String, File>();
	               file.put("fileUploade",new File(TEMP_IMAGE_PATH1));
	               XUtilsHelper.getInstance().upLoadFile("fileUploadOkJson.htm", maps, file, new XUtilsHelper.XCallBack() {					
						@Override
						public void onResponse(String result) {
							progressDialog.hide();
							try{
								JSONObject res = FormatUtil.toJSONObject(result);
								if(res != null){
									if(res.get("d").equals("n")){
										CommonUtil.alter("图片上传失败");
									}
									else{
										rechargedocurl=res.getString("fileUrl");	
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }
	           else if(requestCode==201){  
	        	   progressDialog.show();
	        	   mcd1.dismiss();
	        	   if(bitmap1 != null)//如果不释放的话，不断取图片，将会内存不够  
                	   bitmap1.recycle(); 
	        	   ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap1 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);  
	               iv_businesslicense.setImageBitmap(bitmap1);
	               Map<String, String> maps = new HashMap<String, String>();
	               maps.put("fileUploadeFileName", TEMP_IMAGE_PATH1.substring(TEMP_IMAGE_PATH1.lastIndexOf("/")+1));
	               maps.put("pathType","company");
	               Map<String, File> file = new HashMap<String, File>();
	               file.put("fileUploade",new File(TEMP_IMAGE_PATH1));
	               XUtilsHelper.getInstance().upLoadFile("fileUploadOkJson.htm", maps, file, new XUtilsHelper.XCallBack() {					
						@Override
						public void onResponse(String result) {
							progressDialog.hide();
							try{
								JSONObject res = FormatUtil.toJSONObject(result);
								if(res != null){
									if(res.get("d").equals("n")){
										CommonUtil.alter("图片上传失败");
									}
									else{
										rechargedocurl=res.getString("fileUrl");
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }
		}
	}

}
