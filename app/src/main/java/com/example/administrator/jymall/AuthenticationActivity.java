package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.jymall.common.TopActivity;
import com.example.administrator.jymall.util.CommonUtil;
import com.example.administrator.jymall.util.FormatUtil;
import com.example.administrator.jymall.util.ImageFactory;
import com.example.administrator.jymall.util.XUtilsHelper;
import com.example.administrator.jymall.view.MyConfirmDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_authentication)
public class AuthenticationActivity extends TopActivity {

	private JSONObject user;
	
	private JSONObject company;
	
	@ViewInject(R.id.rl_businesslicense)
	private RelativeLayout rl_businesslicense;
	@ViewInject(R.id.rl_contractFile)
	private RelativeLayout rl_contractFile;
	@ViewInject(R.id.rl_organizationFile)
	private RelativeLayout rl_organizationFile;
	@ViewInject(R.id.rl_legalBackUrl)
	private RelativeLayout rl_legalBackUrl;
	
	
	@ViewInject(R.id.iv_businesslicense)
	private ImageView iv_businesslicense;
	@ViewInject(R.id.iv_contractFile)
	private ImageView iv_contractFile;
	@ViewInject(R.id.iv_organizationFile)
	private ImageView iv_organizationFile;
	@ViewInject(R.id.iv_legalUrl)
	private ImageView iv_legalUrl;
	@ViewInject(R.id.iv_legalBackUrl)
	private ImageView iv_legalBackUrl;
	
	@ViewInject(R.id.tv_legalUrl)
	private TextView tv_legalUrl;
	@ViewInject(R.id.tv_legalBackUrl)
	private TextView tv_legalBackUrl;
	
	@ViewInject(R.id.savebtn)
	private Button savebtn;
	@ViewInject(R.id.tv_ts)
	private TextView tv_ts;

	@ViewInject(R.id.tv_realname_comp)
	private TextView tv_realname_comp;
	
	private String businesslicense="";
	private String contractFile="";
	private String organizationFile="";
	private String legalUrl="";
	private String legalBackUrl = "";
	private String compName = "";

	
	private int ischeck; //审核状态
	
	private String TEMP_IMAGE_PATH;  
	private String TEMP_IMAGE_PATH1= Environment.getExternalStorageDirectory().getPath()+"/temp1.png"; 
	
	private Bitmap bitmap1 = null;
	private Bitmap bitmap2 = null;
	private Bitmap bitmap3 = null;
	private Bitmap bitmap4 = null;
	private Bitmap bitmap5 = null;
	
	private MyConfirmDialog mcd1 = null;
	private MyConfirmDialog mcd2 = null;
	private MyConfirmDialog mcd3 = null;
	private MyConfirmDialog mcd4 = null;
	private MyConfirmDialog mcd5 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("实名认证");
		
		getUserData();
	}
	
	private void getUserData(){
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey",super.serverKey );	
		
		XUtilsHelper.getInstance().post("app/getUser.htm", maps,new XUtilsHelper.XCallBack(){
			@SuppressLint("NewApi")
			@Override
			public void onResponse(String result)  {
				progressDialog.hide();
				JSONObject res;
				try {
					res = new JSONObject(result);
					setServerKey(res.get("serverKey").toString());					
					setUser(res.get("data").toString());
					try{
						user = FormatUtil.toJSONObject(res.get("data").toString());
						if(user != null){
							company = user.getJSONObject("company");
							datainit();
						}
					}
					catch(Exception e){e.printStackTrace();}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
	}
	
	private void datainit(){
		try{
			if(company!=null){
				businesslicense = company.getString("businesslicense");
				contractFile = company.getString("contractFile");
				organizationFile = company.getString("organizationFile");
				legalUrl = company.getString("legalUrl");
				legalBackUrl = company.getString("legalBackUrl");
				ischeck = company.getInt("ischeck");
				compName = company.getString("comp");

				tv_realname_comp.setText(compName);
				tv_realname_comp.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

				if(ischeck == 0){
					tv_ts.setText("您还没进行实名认证，请尽快实名认证。(点击图片可以上传)\n"+"注：图片限于jpg、jpeg、gif、png、bmp格式，不超过1MB或1024KB。");
				}
				else if(ischeck == 1){
					tv_ts.setText("恭喜您，您的资料已通过审核!");
					savebtn.setText("立即变更");
				}
				else if(ischeck == 2){
					tv_ts.setText("认证信息审核未通过，未审核原因："
							+company.getString("msg")+",请尽快修改信息或联系客服，谢谢！!");
					
					savebtn.setText("立即变更");
				}
				else if(ischeck == 3 || ischeck == 5){
					tv_ts.setText("认证信息已提交，我们将在1到2个工作日完成您的信息审核，请耐心等耐，如需帮助请致电0512-52686666!");
					//savebtn.setVisibility(View.GONE);
					savebtn.setText("资料变更");
				}
				else if(ischeck == 4){
					tv_ts.setText("恭喜您，您的资料已通过审核!");
					savebtn.setVisibility(View.GONE);
				}	
				if(FormatUtil.isNoEmpty(businesslicense)){
					XUtilsHelper.getInstance().bindCommonImage(iv_businesslicense, businesslicense, true);
				}
				if(FormatUtil.isNoEmpty(contractFile)){
					XUtilsHelper.getInstance().bindCommonImage(iv_contractFile, contractFile, true);
				}
				if(FormatUtil.isNoEmpty(organizationFile)){
					XUtilsHelper.getInstance().bindCommonImage(iv_organizationFile, organizationFile, true);
				}
				if(FormatUtil.isNoEmpty(legalUrl)){
					XUtilsHelper.getInstance().bindCommonImage(iv_legalUrl, legalUrl, true);
				}
				if(FormatUtil.isNoEmpty(legalBackUrl)){
					XUtilsHelper.getInstance().bindCommonImage(iv_legalBackUrl, legalBackUrl, true);
				}
				if(user.getInt("usertype")==1 ){
					rl_businesslicense.setVisibility(View.GONE);
					rl_contractFile.setVisibility(View.GONE);
					rl_organizationFile.setVisibility(View.GONE);
				}
				else{
					tv_legalUrl.setText("法人身份证（正面）");
					tv_legalBackUrl.setText("法人身份证（反面）");
				}
			}
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	@Event(R.id.savebtn)
	private void savebtnClick(View v) {
		try{
		if(user.getInt("usertype")==0 && businesslicense.equals("")){
			CommonUtil.alter("营业执照必须上传！！！");
			return;
		}
		if(user.getInt("usertype")==1 && legalUrl.equals("")){
			CommonUtil.alter("身份证照正面必须上传！！！");
			return;
		}
		if(user.getInt("usertype")==1 && legalBackUrl.equals("")){
			CommonUtil.alter("身份证照反面必须上传！！！");
			return;
		}
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey",super.serverKey );	
		maps.put("businesslicense",businesslicense );	
		maps.put("contractFile",contractFile);	
		maps.put("organizationFile",organizationFile);	
		maps.put("legalUrl",legalUrl);	
		maps.put("legalBackUrl",legalBackUrl);
		XUtilsHelper.getInstance().post("app/doRealnameLegalize.htm", maps,new XUtilsHelper.XCallBack(){
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
						CommonUtil.alter("提交成功，请等待审核！！！");
						startActivity(new Intent(getApplicationContext(),UserInfoActivity.class));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	
	
	@Event(value=R.id.iv_businesslicense,type=View.OnTouchListener.class)
	private boolean businesslicenseclick(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(mcd1==null){			
				mcd1=new MyConfirmDialog(AuthenticationActivity.this, "上传营业执照（副本）", "拍照上传", "本地上传");
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
					     String IMAGE_TYPE="image/*";  
					    Intent intent=new Intent(Intent.ACTION_PICK,null);   
					    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_TYPE); 
					    startActivityForResult(intent,101);		
					}
				});
			}
			mcd1.show();			
			return false;
		}
		return true;
	}
	
	@Event(value=R.id.iv_contractFile,type=View.OnTouchListener.class)
	private boolean contractFileclick(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(mcd2==null){			
				mcd2=new MyConfirmDialog(AuthenticationActivity.this, "税务登记证（副本）", "拍照上传", "本地上传");
				mcd2.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {					
					@Override
					public void doConfirm() {					   
					    TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";  
					    Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
					    Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));  
					    intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);  
					    startActivityForResult(intent1,202);  
					}					
					@Override
					public void doCancel() {  					    
					    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
					    intent.addCategory(Intent.CATEGORY_OPENABLE);
					    intent.setType("image/*");
					    startActivityForResult(intent, 102);
					}
				});
			}
			mcd2.show();			
			return false;
		}
		return true;
	}
	
	@Event(value=R.id.iv_organizationFile,type=View.OnTouchListener.class)
	private boolean organizationFileclick(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(mcd3==null){			
				mcd3=new MyConfirmDialog(AuthenticationActivity.this, "组织机构代码证（副本）", "拍照上传", "本地上传");
				mcd3.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {					
					@Override
					public void doConfirm() {					   
					    TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";  
					    Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
					    Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));  
					    intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);  
					    startActivityForResult(intent1,203);  
					}					
					@Override
					public void doCancel() {  	
					    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
					    intent.addCategory(Intent.CATEGORY_OPENABLE);
					    intent.setType("image/*");
					    startActivityForResult(intent, 103);
					}
				});
			}
			mcd3.show();			
			return false;
		}
		return true;
	}
	
	@Event(value=R.id.iv_legalUrl,type=View.OnTouchListener.class)
	private boolean legalUrlclick(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(mcd4==null){			
				mcd4=new MyConfirmDialog(AuthenticationActivity.this, "身份证（正面）", "拍照上传", "本地上传");
				mcd4.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {					
					@Override
					public void doConfirm() {					   
					    TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";  
					    Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
					    Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));  
					    intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);  
					    startActivityForResult(intent1,204);  
					}					
					@Override
					public void doCancel() {  
						 Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
						    intent.addCategory(Intent.CATEGORY_OPENABLE);
						    intent.setType("image/*");
						    startActivityForResult(intent, 104);
					}
				});
			}
			mcd4.show();			
			return false;
		}
		return true;
	}
	
	
	@Event(value=R.id.iv_legalBackUrl,type=View.OnTouchListener.class)
	private boolean legalBackUrlclick(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(mcd5==null){			
				mcd5=new MyConfirmDialog(AuthenticationActivity.this, "身份证（反面）", "拍照上传", "本地上传");
				mcd5.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {					
					@Override
					public void doConfirm() {					   
					    TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";  
					    Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
					    Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));  
					    intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);  
					    startActivityForResult(intent1,205);  
					}					
					@Override
					public void doCancel() {  
						 Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
						    intent.addCategory(Intent.CATEGORY_OPENABLE);
						    intent.setType("image/*");
						    startActivityForResult(intent, 105);	
					}
				});
			}
			mcd5.show();			
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
										businesslicense=res.getString("fileUrl");										
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
										businesslicense=res.getString("fileUrl");										
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }
	           else if(requestCode==102&&data!=null){  	
	        	   progressDialog.show();
	        	   mcd2.dismiss();
	               Uri uri = data.getData();   
	               if(bitmap2 != null)//如果不释放的话，不断取图片，将会内存不够  
                	   bitmap2.recycle();  
	               TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
	               ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap2 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
	               iv_contractFile.setImageBitmap(bitmap2); 
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
										contractFile=res.getString("fileUrl");										
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }
	           else if(requestCode==202){  
	        	   progressDialog.show();
	        	   mcd2.dismiss();
	        	   if(bitmap2 != null)
                	   bitmap2.recycle(); 
	        	   ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap2 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);  
	        	   iv_contractFile.setImageBitmap(bitmap2);
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
										contractFile=res.getString("fileUrl");	
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           } 
	         //营业执照
	           else if(requestCode==103&&data!=null){  
	        	   progressDialog.show();
	        	   mcd3.dismiss();
	               Uri uri = data.getData();   
	               if(bitmap3 != null)
	                   bitmap3.recycle();  
	               TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
	               ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap3 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
	               iv_organizationFile.setImageBitmap(bitmap3); 
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
										organizationFile=res.getString("fileUrl");										
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }
	           else if(requestCode==203){  
	        	   progressDialog.show();
	        	   mcd3.dismiss();
	        	   if(bitmap3 != null)
                	   bitmap3.recycle(); 
	        	   ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap3 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);   
	        	   iv_organizationFile.setImageBitmap(bitmap3);
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
										organizationFile=res.getString("fileUrl");	
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }  
	           else if(requestCode==104&&data!=null){  	
	        	   progressDialog.show();
	        	   mcd4.dismiss();
	               Uri uri = data.getData();   
	               if(bitmap4 != null)
	                   bitmap4.recycle();  
	               TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
	               ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap4 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
	               iv_legalUrl.setImageBitmap(bitmap4); 
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
										legalUrl=res.getString("fileUrl");										
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }
	           else if(requestCode==204){ 
	        	   progressDialog.show();
	        	   mcd4.dismiss();
	        	   if(bitmap4 != null)
                	   bitmap4.recycle(); 
	        	   ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap4 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);   
	        	   iv_legalUrl.setImageBitmap(bitmap4);
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
										legalUrl=res.getString("fileUrl");	
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }  
	           else if(requestCode==105&&data!=null){  	
	        	   progressDialog.show();
	        	   mcd5.dismiss();
	               Uri uri = data.getData();   
	               if(bitmap5 != null)
	                   bitmap5.recycle();  
	               TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
	               ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap5 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
	               iv_legalBackUrl.setImageBitmap(bitmap5); 
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
										legalBackUrl=res.getString("fileUrl");										
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }
	           else if(requestCode==205){ 
	        	   progressDialog.show();
	        	   mcd5.dismiss();
	        	   if(bitmap5 != null)
                	   bitmap5.recycle(); 
	        	   ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap5 =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);   
	               iv_legalBackUrl.setImageBitmap(bitmap5);
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
										legalBackUrl=res.getString("fileUrl");	
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
