package com.example.administrator.jymall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

@ContentView(R.layout.activity_user_info)
public class UserInfoActivity extends TopActivity {

	@ViewInject(R.id.uc_head)
	private RelativeLayout uc_head;
	
	@ViewInject(R.id.heador)
	private ImageView heador;
	
	@ViewInject(R.id.userinfoedit)
	private RelativeLayout userinfoedit;
	
	@ViewInject(R.id.userauthentication)
	private RelativeLayout userauthentication;
	
	@ViewInject(R.id.editpwd)
	private RelativeLayout editpwd;
	
	@ViewInject(R.id.address)
	private RelativeLayout address;
	
	@ViewInject(R.id.invoice)
	private RelativeLayout invoice;
	
	@ViewInject(R.id.bank)
	private RelativeLayout bank;
	
	@ViewInject(R.id.btn_userMoney)
	private RelativeLayout btn_userMoney;
	
	private String TEMP_IMAGE_PATH;  
	
	private String TEMP_IMAGE_PATH1= Environment.getExternalStorageDirectory().getPath()+"/temp1.png";  
	
	private Bitmap bitmap = null;
	
	private MyConfirmDialog mcd = null;
	
	private JSONObject user;
	
	@ViewInject(R.id.btn_exit)
	private Button btn_exit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		super.title.setText("账户管理");
		user = FormatUtil.toJSONObject(super.getUser());
		setUsr();
		progressDialog.hide();
	}
	
	/**
	 * 设置用户信息
	 * @throws JSONException 
	 */
	private void setUsr(){
		if(user != null){
			try {
				XUtilsHelper.getInstance().bindCommonImage(heador, user.getString("heador"), true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	@Event(value=R.id.userinfoedit,type=View.OnTouchListener.class)
	private boolean ucinfoTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			startActivity(new Intent(getApplicationContext(),UserInfoEditActivity.class));
			return false;
		}
		return true;
	}
	
	@Event(value=R.id.userauthentication,type=View.OnTouchListener.class)
	private boolean userauthenticationTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			startActivity(new Intent(getApplicationContext(),AuthenticationActivity.class));
			return false;
		}
		return true;
	}
	
	@Event(value=R.id.editpwd,type=View.OnTouchListener.class)
	private boolean editpwdTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			startActivity(new Intent(getApplicationContext(),EditPwdActivity.class));
			return false;
		}
		return true;
	}
	
	@Event(value=R.id.address,type=View.OnTouchListener.class)
	private boolean addressTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			startActivity(new Intent(getApplicationContext(),AddressActivity.class));
			return false;
		}
		return true;
	}
	
	@Event(value=R.id.invoice,type=View.OnTouchListener.class)
	private boolean invoiceTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			startActivity(new Intent(getApplicationContext(),InvoiceActivity.class));
			return false;
		}
		return true;
	}
	
	@Event(value=R.id.bank,type=View.OnTouchListener.class)
	private boolean bankTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			startActivity(new Intent(getApplicationContext(),BankActivity.class));
			return false;
		}
		return true;
	}
	
	@Event(value=R.id.btn_userMoney,type=View.OnTouchListener.class)
	private boolean userMoneyTouch(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			startActivity(new Intent(getApplicationContext(),UserMoneyActivity.class));
			return false;
		}
		return true;
	}

	@Event(value=R.id.uc_head,type=View.OnTouchListener.class)
	private boolean btn5(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(mcd==null){			
				mcd=new MyConfirmDialog(UserInfoActivity.this, "修改头像?", "拍照上传", "本地上传");
				mcd.setClicklistener(new MyConfirmDialog.ClickListenerInterface() {
					
					@Override
					public void doConfirm() {
					    //设置一个临时路径，保存所拍的照片  
					    
					    //获取该路径  
					    TEMP_IMAGE_PATH= Environment.getExternalStorageDirectory().getPath()+"/temp.png";  
					    //传入ACTION_IMAGE_CAPTURE:该action指向一个照相机app  
					    Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
					    //创建File并获取它的URI值  
					    Uri photoUri=Uri.fromFile(new File(TEMP_IMAGE_PATH));  
					    //MediaStore.EXTRA_OUTPUT为字符串"output"，即将该键值对放进intent中  
					    intent1.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);  
					    startActivityForResult(intent1,200);  
					}
					
					@Override
					public void doCancel() {  
					    /*//设置MIME码：表示image所有格式的文件均可  
					     String IMAGE_TYPE="image/*";  
					    //实例化Intent,传入ACTION_PICK,表示从Item中选取一个数据返回  
					    Intent intent=new Intent(Intent.ACTION_PICK,null);   
					    //设置Data和Type属性，前者是URI：表示系统图库的URI,后者是MIME码  
					    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_TYPE);  
					    //启动这个intent所指向的Activity  
					    startActivityForResult(intent,100);*/		
					    
					    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
					    intent.addCategory(Intent.CATEGORY_OPENABLE);
					    intent.setType("image/*");
					    startActivityForResult(intent, 100);
					    
					}
				});
			}
			mcd.show();
			
			return false;
		}
		return true;
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    
		if(resultCode==RESULT_OK){  
	           if(requestCode==100&&data!=null){  
	        	   progressDialog.show();
	        	   mcd.dismiss();
	               //选择图片  
	               Uri uri = data.getData();  
	               if(bitmap != null)//如果不释放的话，不断取图片，将会内存不够  
                	   bitmap.recycle();  
	               TEMP_IMAGE_PATH =ImageFactory.getPath(getApplicationContext(), uri);
	               ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);
	               heador.setImageBitmap(bitmap); 
	               Map<String, String> maps = new HashMap<String, String>();
	               maps.put("fileUploadeFileName", TEMP_IMAGE_PATH1.substring(TEMP_IMAGE_PATH1.lastIndexOf("/")+1));
	               maps.put("pathType","company");
	               Map<String, File> file = new HashMap<String, File>();
	               file.put("fileUploade",new File(TEMP_IMAGE_PATH1));
	               XUtilsHelper.getInstance().upLoadFile("fileUploadOkJson.htm", maps, file, new XUtilsHelper.XCallBack() {					
						@Override
						public void onResponse(String result) {
							// /storage/emulated/0/MIUI/Gallery/cloud/.thumbnailFile/0e2aea37f251033f41d0317b44166ea9b12082e7.jpg
							//{"d":"Y","error":null,"fileUrl":"upload//company/20170220/0e516842-3c27-4922-9e83-7957dd1aba45.jpg"}
							try{
								JSONObject res = FormatUtil.toJSONObject(result);
								if(res != null){
									if(res.get("d").equals("n")){
										CommonUtil.alter("头像上传失败");
										progressDialog.hide();
									}
									else{
										updateHeador(res.getString("fileUrl"));
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }else if(requestCode==200){  
	        	   progressDialog.show();
	        	   mcd.dismiss();
	        	   if(bitmap != null)//如果不释放的话，不断取图片，将会内存不够  
                	   bitmap.recycle(); 
	        	   ImageFactory.compressPicture(TEMP_IMAGE_PATH, TEMP_IMAGE_PATH1);
	               bitmap =BitmapFactory.decodeFile(TEMP_IMAGE_PATH1);  
				   heador.setImageBitmap(bitmap);
	               Map<String, String> maps = new HashMap<String, String>();
	               maps.put("fileUploadeFileName", TEMP_IMAGE_PATH1.substring(TEMP_IMAGE_PATH1.lastIndexOf("/")+1));
	               maps.put("pathType","company");
	               Map<String, File> file = new HashMap<String, File>();
	               file.put("fileUploade",new File(TEMP_IMAGE_PATH1));
	               XUtilsHelper.getInstance().upLoadFile("fileUploadOkJson.htm", maps, file, new XUtilsHelper.XCallBack() {
					
						@Override
						public void onResponse(String result) {
							// {"d":"Y","error":null,"fileUrl":"upload//company/20170220/f9452624-fcdb-4103-b86f-582fe2b2f38d.png"}
							try{
								JSONObject res = FormatUtil.toJSONObject(result);
								if(res != null){
									if(res.get("d").equals("n")){
										CommonUtil.alter("头像上传失败");
										progressDialog.hide();
									}
									else{
										updateHeador(res.getString("fileUrl"));
									}
								}
							}
							catch(Exception e){e.printStackTrace();}
						}
					});
	           }  
	       }  
        
    }  
	
	/**
	 * 头像上传
	 * @param imgurl
	 */
	private void updateHeador(String imgurl){
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey",super.serverKey );
		maps.put("imgurl",imgurl );		
		
		XUtilsHelper.getInstance().post("app/updateHeador.htm", maps,new XUtilsHelper.XCallBack(){

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
						setUser(res.get("data").toString());
						CommonUtil.alter("上传头像成功！！！");
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
	}
	
	@Event(R.id.btn_exit)
	private void exit(View v){
		super.clearServerKey();
		CommonUtil.alter("成功退出！！！");
		startActivity(new Intent(getApplicationContext(),LoginActivity.class));
	}

}
