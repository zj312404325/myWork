package com.example.administrator.helloworld.util;

import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;

public class PricesUtil {

	/**
	 * 得到最小起订量
	 * @param productPrices
	 * @return
	 */
	public static double getMinQuantity(JSONArray productPrices) {
		double num = 0;
        for(int i=0;i<productPrices.length();i++){
        	double tem;
			try {
				tem = productPrices.getJSONObject(i).getDouble("moq");
				if(num == 0){
					num = tem;
				}
				else{
					if(num > tem){
		        		num = tem;
		        	}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        	
        }
        return num;
    }
	
	/**
	 * 根据起订量得到最小价格
	 * @param quantity
	 * @param productPrices
	 * @return
	 */
	public static double getMinPrice(double quantity,JSONArray productPrices){
		double salePrice = 0;
        for(int i=0;i<productPrices.length();i++){
        	double tem;
        	double temprice;
			try {
				tem = productPrices.getJSONObject(i).getDouble("moq");
				if(quantity >= tem){
					temprice = productPrices.getJSONObject(i).getDouble("price");
					if(salePrice == 0){
						salePrice = temprice;
					}
					else{
						if(salePrice >temprice ){
							salePrice = temprice;
						}
					}
	        	}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        	
        }
        return salePrice;
	}
}
