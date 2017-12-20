package com.example.administrator.helloworld;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import com.example.administrator.helloworld.Order_Product_Activity.InfoSimpleAdapter;
import com.example.administrator.helloworld.common.MyApplication;
import com.example.administrator.helloworld.common.TopActivity;
import com.example.administrator.helloworld.util.BaseConst;
import com.example.administrator.helloworld.util.BigDecimalUtil;
import com.example.administrator.helloworld.util.CommonUtil;
import com.example.administrator.helloworld.util.FormatUtil;
import com.example.administrator.helloworld.util.ImageFactory;
import com.example.administrator.helloworld.util.XUtilsHelper;
import com.example.administrator.helloworld.view.MyConfirmDialog;
import com.example.administrator.helloworld.view.MyListView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@ContentView(R.layout.activity_refund)
public class RefundActivity extends TopActivity {
	
	private int refundtype;
	private String id;
	
	private List<Map<String, Object>> dateMapinfo=new ArrayList<Map<String,Object>>();
	
	private SimpleAdapter sapinfo;
	
	private JSONObject order;
	
	private JSONArray orderDtls;
	
	@ViewInject(R.id.list_orderinfo)
	private MyListView list_orderinfo;
	
	private double money = 0;
	
	@ViewInject(R.id.img_fileurl)
	private ImageView img_fileurl;
	@ViewInject(R.id.et_money)
	private EditText et_money;
	@ViewInject(R.id.et_refundmemo)
	private EditText et_refundmemo;
	
	private String fileurl;
	
	
	private String TEMP_IMAGE_PATH;  
	
	private String TEMP_IMAGE_PATH1= Environment.getExternalStorageDirectory().getPath()+"/temp1.png";  
	
	private Bitmap bitmap = null;
	
	private MyConfirmDialog mcd = null;
	@ViewInject(R.id.savebtn)
	private Button savebtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		Intent i = this.getIntent();
		refundtype = i.getIntExtra("refundtype", 0);
		id = i.getStringExtra("id");
		if(refundtype == 0){
			super.title.setText("我要退货");
		}
		else{
			super.title.setText("我要退款");
		}
		sapinfo = new InfoSimpleAdapter(RefundActivity.this, dateMapinfo, 
				R.layout.listview_orderxhrefundinfo, 
				new String[]{"proName"}, 
				new int[]{R.id.tv_proName});
		list_orderinfo.setAdapter(sapinfo);	
		getDate();
	}
	
	@Event(value=R.id.savebtn)
	private void saveclick(View v){
		String ids = "";
		for(int j=0;j<dateMapinfo.size();j++){
			if(dateMapinfo.get(j).get("isCheck").toString().equals("1")){
					ids += dateMapinfo.get(j).get("id").toString()+",";
			}
		}
		if(ids.equals("")){
			CommonUtil.alter("请选择需要所退物品！");return;
		}
		else{
			ids = ids.substring(0, ids.length()-1);
		}
		double mymoney =FormatUtil.toDouble( et_money.getText());
		if(mymoney>money){
			CommonUtil.alter("所退金额不能大于"+money);return;
		}
		if(mymoney<0.001){
			CommonUtil.alter("所退金额不能为0");return;
		}
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("orderid", id);
		maps.put("refundmemo", et_refundmemo.getText().toString());
		maps.put("fileurl",fileurl);
		maps.put("ids",ids);
		maps.put("mymoney",mymoney+"");
		maps.put("refundtype", refundtype+"");
		maps.put("flag", "0");
		XUtilsHelper.getInstance().post("app/submitReInfo.htm", maps,new XUtilsHelper.XCallBack(){

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
						CommonUtil.alter("操作成功！！！");
						setResult(BaseConst.RCODE_REFOUND);
						finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}			
		});
	}
	
	
	private void getDate(){
		progressDialog.show();
		Map<String, String> maps= new HashMap<String, String>();
		maps.put("serverKey", super.serverKey);
		maps.put("id", id);
		XUtilsHelper.getInstance().post("app/getRefundinfo.htm", maps,new XUtilsHelper.XCallBack(){

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
						MyApplication.getInstance().finishActivity();
					}
					else{
						order = res.getJSONObject("order");
						money = order.getDouble("money");
						et_money.setText(money+"");
						orderDtls = order.getJSONArray("orderDtls");
						for(int j=0;j<orderDtls.length();j++){
							Map<String, Object> dateMap1 = new HashMap<String, Object>();
							dateMap1.put("id", orderDtls.getJSONObject(j).get("ID"));
							dateMap1.put("proName", orderDtls.getJSONObject(j).get("proName"));
							dateMap1.put("orderInfo", orderDtls.getJSONObject(j).toString());
							dateMap1.put("isCheck", "1");
							dateMapinfo.add(dateMap1);							
						}
						sapinfo.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}			
		});
	}
	
	
	public class InfoSimpleAdapter  extends SimpleAdapter {
		private LayoutInflater mInflater;
		private List<Map<String, Object>> mdata;
		
		public InfoSimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mdata = (List<Map<String, Object>>) data;
		}
		
		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			try{
				if(convertView==null){ 
					convertView=mInflater.inflate(R.layout.listview_orderxhrefundinfo, null); 
				} 
				ImageView img_proImgPath = (ImageView)convertView.findViewById(R.id.img_proImgPath);
				TextView tv_proName = (TextView)convertView.findViewById(R.id.tv_proName);
				TextView tv_info = (TextView)convertView.findViewById(R.id.tv_info);
				TextView tv_salePrice = (TextView)convertView.findViewById(R.id.tv_salePrice);
				TextView tv_quantity = (TextView)convertView.findViewById(R.id.tv_quantity);
				CheckBox ck_checkinfo = (CheckBox)convertView.findViewById(R.id.ck_checkinfo);
				
				if(mdata.get(position).get("isCheck").toString().equals("1")){
					ck_checkinfo.setChecked(true);
				}
				else
					ck_checkinfo.setChecked(false);
				
				ck_checkinfo.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						boolean arg1 = ((CheckBox)arg0).isChecked();
						String isCheck = arg1?"1":"0";
						mdata.get(position).put("isCheck", isCheck);
						getAllMoney();
					}
				}); 
				/*{
					
					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						if(arg0.getId() == R.id.ck_checkinfo){
							CommonUtil.alter(position+"");
							// TODO Auto-generated method stub
							String isCheck = arg1?"1":"0";
							mdata.get(position).put("isCheck", isCheck);
							getAllMoney();
						}
					}
				});*/
				
				JSONObject temp  =FormatUtil.toJSONObject( mdata.get(position).get("orderInfo").toString());
				XUtilsHelper.getInstance().bindCommonImage(img_proImgPath, temp.getString("proImgPath"), true);
				
				int templateid = temp.getInt("templateid");
				String salePrice = temp.getString("salePrice");
				int payMethod = temp.getInt("payMethod");
				String info = "";
				if(templateid == 0){
					info = "规格："+temp.getString("proSpec")+"   "
							+"材质："+temp.getString("proQuality")+"   "
							+"编号："+temp.getString("proCode");
				}
				else if(templateid == 1){
					info = "规格："+temp.getString("proSpec")+"   "
							+"锌层："+temp.getString("znlayer")+"   "
							+"包装方式："+temp.getString("packType")+"   "
							+"捆包号："+temp.getString("proCode");
				}
				else if(templateid == 2){
					info = "规格："+temp.getString("proSpec")+"   "
							+"颜色："+temp.getString("color")+"   "
							+"漆膜厚度："+temp.getString("film")+"   "
							+"捆包号："+temp.getString("proCode");
				}
				else if(templateid == 3){
					info = "厚度："+temp.getString("thick")+"   "
							+"口径："+temp.getString("borer")+"   "
							+"捆包号："+temp.getString("proCode");
				}
				else if(templateid == 4 || templateid == 5){
					info = "规格："+temp.getString("proSpec")+"   "
							+"包装方式："+temp.getString("packType")+"   "
							+"捆包号："+temp.getString("proCode");
				}
				tv_info.setText(info);		
				if(salePrice.equals("0")){
					salePrice = "面议";
				}
				else{
					if(payMethod == 0){
						salePrice += "元/"+temp.getString("unit");
					}
					else{
						salePrice += "积分/"+temp.getString("unit");
					}
				}
				tv_salePrice.setText(salePrice);
				tv_quantity.setText("X"+temp.getString("quantity")+temp.getString("unit"));
				
			}			
			catch(Exception e){
				Log.v("PRO", e.getMessage());
			}
			return super.getView(position, convertView, parent);
		}		
	}
	
	
	
	private void getAllMoney(){
		double tem = 0;
		try{
			for(int i=0;i<dateMapinfo.size();i++){
				JSONObject temp  =FormatUtil.toJSONObject( dateMapinfo.get(i).get("orderInfo").toString());
				if(dateMapinfo.get(i).get("isCheck").toString().equals("1")){
					tem = BigDecimalUtil.add(tem,
							BigDecimalUtil.mul(temp.getDouble("salePrice"),
									temp.getDouble("quantity")));
				}
				
			}
		}
		catch(Exception e){		
		}
		money = tem;
		et_money.setText(money+"");
	}
	
	@Event(value=R.id.img_fileurl,type=View.OnTouchListener.class)
	private boolean btn5(View v, MotionEvent event){
		if (event.getAction() == event.ACTION_UP) {
			if(mcd==null){			
				mcd=new MyConfirmDialog(RefundActivity.this, "上传图片", "拍照上传", "本地上传");
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
	               img_fileurl.setImageBitmap(bitmap); 
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
								progressDialog.hide();
								JSONObject res = FormatUtil.toJSONObject(result);
								if(res != null){
									if(res.get("d").equals("n")){
										CommonUtil.alter("图片上传失败");
										
									}
									else{
										fileurl =res.getString("fileUrl");
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
	               img_fileurl.setImageBitmap(bitmap);
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
								progressDialog.hide();
								JSONObject res = FormatUtil.toJSONObject(result);
								if(res != null){
									if(res.get("d").equals("n")){
										CommonUtil.alter("图片上传失败");
										
									}
									else{
										fileurl =res.getString("fileUrl");
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
