package com.example.administrator.helloworld.view;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.example.administrator.helloworld.R;
import com.example.administrator.helloworld.common.MyApplication;
import com.example.administrator.helloworld.model.CityModel;
import com.example.administrator.helloworld.model.DistrictModel;
import com.example.administrator.helloworld.model.ProvinceModel;
import com.example.administrator.helloworld.service.XmlParserHandler;
import com.example.administrator.helloworld.util.ComputeCallBack;



import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class WheelCascade implements OnClickListener, OnWheelChangedListener{
	/**
	 * 所有省
	 */
	protected String[] mProvinceDatas;
	/**
	 * key - 省 value - 市
	 */
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区
	 */
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	
	/**
	 * key - 区 values - 邮编
	 */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>(); 

	/**
	 * 当前省的名称
	 */
	protected String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	protected String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	protected String mCurrentDistrictName ="";
	
	/**
	 * 当前区的邮政编码
	 */
	protected String mCurrentZipCode ="";
	
	/**
	 * 解析省市区的XML数据
	 */
	protected Window window;
	
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	private Button mBtnConfirm;
	
	private String province;
	private String city;
	private String district;
	
	private int provinceS=0;
	private int cityS=0;
	private int districtS=0;
	
	private ComputeCallBack ccb;
	
	 public WheelCascade(Window window,String v,ComputeCallBack ccb) {
		 String[] vs = v.split("-");
		 for(int i=0;i<vs.length;i++){
			 if(i==0)
				 province = vs[i];
			 else if(i==1)
				 city = vs[i];
			 else if(i==2)
				 district = vs[i];
		 }
		 this.window =  window;
		 this.ccb = ccb;
		 setUpViews();
			setUpListener();
			setUpData();
	 }
	 
	 private void setUpViews() {
			mViewProvince = (WheelView)window.findViewById(R.id.id_province);
			mViewCity = (WheelView)window.findViewById(R.id.id_city);
			mViewDistrict = (WheelView)window.findViewById(R.id.id_district);
			mBtnConfirm = (Button)window.findViewById(R.id.btn_confirm);
		}
		
		private void setUpListener() {
	    	// 添加change事件
	    	mViewProvince.addChangingListener(this);
	    	// 添加change事件
	    	mViewCity.addChangingListener(this);
	    	// 添加change事件
	    	mViewDistrict.addChangingListener(this);
	    	// 添加onclick事件
	    	mBtnConfirm.setOnClickListener(this);
	    }
		
		private void setUpData() {
			initProvinceDatas();
			mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(MyApplication.getInstance(), mProvinceDatas));
			mViewProvince.setCurrentItem(provinceS);
			// 设置可见条目数量
			mViewProvince.setVisibleItems(7);
			mViewCity.setVisibleItems(7);
			mViewDistrict.setVisibleItems(7);
			updateCities();
			mViewCity.setCurrentItem(cityS);
			updateAreas();
			mViewDistrict.setCurrentItem(provinceS);
		}
		
		/**
		 * 根据当前的市，更新区WheelView的信息
		 */
		private void updateAreas() {
			int pCurrent = mViewCity.getCurrentItem();
			mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
			String[] areas = mDistrictDatasMap.get(mCurrentCityName);

			if (areas == null) {
				areas = new String[] { "" };
			}
			mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(MyApplication.getInstance(), areas));
			mViewDistrict.setCurrentItem(0);
			
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
		}

		/**
		 * 根据当前的省，更新市WheelView的信息
		 */
		private void updateCities() {
			int pCurrent = mViewProvince.getCurrentItem();
			mCurrentProviceName = mProvinceDatas[pCurrent];
			String[] cities = mCitisDatasMap.get(mCurrentProviceName);
			if (cities == null) {
				cities = new String[] { "" };
			}
			mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(MyApplication.getInstance(), cities));
			mViewCity.setCurrentItem(0);
			updateAreas();
		}

	
    protected void initProvinceDatas()
	{
		List<ProvinceModel> provinceList = null;
    	AssetManager asset = MyApplication.getInstance().getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
			//*/ 初始化默认选中的省、市、区
			if (provinceList!= null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList!= null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					List<DistrictModel> districtList = cityList.get(0).getDistrictList();
					mCurrentDistrictName = districtList.get(0).getName();
					mCurrentZipCode = districtList.get(0).getZipcode();
				}
			}
			//*/
			mProvinceDatas = new String[provinceList.size()];
        	for (int i=0; i< provinceList.size(); i++) {
        		// 遍历所有省的数据
        		mProvinceDatas[i] = provinceList.get(i).getName();
        		if(provinceList.get(i).getName().equals(province)){
        			provinceS = i;
        		}
        		List<CityModel> cityList = provinceList.get(i).getCityList();
        		String[] cityNames = new String[cityList.size()];
        		for (int j=0; j< cityList.size(); j++) {
        			// 遍历省下面的所有市的数据
        			cityNames[j] = cityList.get(j).getName();
        			if(cityList.get(j).getName().equals(city)){
            			cityS = j;
            		}
        			List<DistrictModel> districtList = cityList.get(j).getDistrictList();
        			String[] distrinctNameArray = new String[districtList.size()];
        			DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
        			for (int k=0; k<districtList.size(); k++) {
        				if(districtList.get(k).getName().equals(district)){
        					districtS = k;
        				}
        				// 遍历市下面所有区/县的数据
        				DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
        				// 区/县对于的邮编，保存到mZipcodeDatasMap
        				mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
        				distrinctArray[k] = districtModel;
        				distrinctNameArray[k] = districtModel.getName();
        			}
        			// 市-区/县的数据，保存到mDistrictDatasMap
        			mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
        		}
        		// 省-市的数据，保存到mCitisDatasMap
        		mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
        	}
        } catch (Throwable e) {  
            e.printStackTrace();  
        } finally {
        	
        } 
	}
	
	
	
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_confirm:
			showSelectedResult();
			break;
		default:
			break;
		}
	}

	private void showSelectedResult() {
		ccb.onComputeEnd(mCurrentProviceName+"-"+mCurrentCityName+"-"
				+mCurrentDistrictName);
	}
}
