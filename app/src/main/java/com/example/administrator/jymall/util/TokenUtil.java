package com.example.administrator.jymall.util;

import java.util.Date;


/**
 * 金赢网APP的token类
 * 类名称：TokenUtil   
 * 类描述：   
 * 创建人：Administrator
 * 创建时间：2017年1月4日 下午3:10:55   
 * 修改人：Administrator
 * 修改时间：2017年1月4日 下午3:10:55   
 * 修改备注：   
 * @version
 */
public class TokenUtil {

	 /**                                                             
	  *用户名加密                                                    
	  *@param serverKey 字符串
	  *@return String 返回加密字符串                                 
	 */                                                              
	 public static String getUserid(String serverKey)                   
	 {                                                               
	   try                                                           
	   {                                                             
		 String[] strs=DesUtil.decrypt(serverKey).split("_");                                         
	     return strs[0];                                                
	   }catch(Exception e)                                           
	   {                                                             
	     e.printStackTrace() ;                                       
	     return null;                                                
	   }                                                             
	 }
	 
	 /**                                                             
	  *用户名加密                                                    
	  *@param serverKey 字符串
	  *@return String 返回加密字符串                                 
	 */                                                              
	 public static String getDid(String serverKey)                   
	 {                                                               
	   try                                                           
	   {                                                             
		 String[] strs=DesUtil.decrypt(serverKey).split("_");                                         
	     return strs[1];                                                
	   }catch(Exception e)                                           
	   {                                                             
	     e.printStackTrace() ;                                       
	     return null;                                                
	   }                                                             
	 }
	 
	 /**
	  * 更新serverkey
	  * @Title: updateServerkey  
	  * @Description: TODO 
	  * @param @param serverKey
	  * @param @return 
	  * @return String 
	  * @throws
	  */
	 public static String updateServerkey(String serverKey){
			String deserverkey=DesUtil.decrypt(serverKey);
			String[] keys=deserverkey.split("_");
			Long nowtime=(new Date()).getTime();
			String tmp=keys[0]+"_"+keys[1]+"_"+nowtime;
			return DesUtil.encrypt(tmp);
		}
	 
	 /**
	  * 设置serverkey
	  * @Title: setServerkey  
	  * @Description: TODO 
	  * @param @param uid
	  * @param @param deviceId
	  * @param @return 
	  * @return String 
	  * @throws
	  */
	 public static String setServerkey(String uid,String deviceId){
		 	String serverKey=uid+"_"+deviceId+"_"+(new Date()).getTime();
			return DesUtil.encrypt(serverKey);
	 }
	 
}
