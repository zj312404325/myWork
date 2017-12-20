package com.example.administrator.helloworld.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class FormatUtil {
	//------------------------------   格式化数值   ---------------------------------
    /** 把Double类型的数值转化成"###,###.00"的字符串   */
    public static String toStringWithDecimal(Double price) {
        try {return  new DecimalFormat("###,###.00").format(price);} catch (Exception pe) { return null;}
    }
    /** 把String类型的数值转化成"###,###.00"的字符串   */
    public static String toStringWithDecimal(String decimal) {
        try {return  new DecimalFormat("###,###.00").format(new Double(decimal));} catch (Exception pe) { return null;}
    } 
    /** 把String类型的货币数值转化成"###,###"的字符串   */
    public static String toStringWithoutDecimal(String nodecimal) {
        try {return  new DecimalFormat("###,###").format(new Double(nodecimal));} catch (Exception pe) { return null;}
    } 
    /** 把类型为"###,###.00"的字符串转化成Double类型的货币数值 */
    public static Double toDoubleCurrency(String price) {
        try {return new Double(new DecimalFormat("###,###.00").parse(String.valueOf(price)).doubleValue());} catch (Exception pe) { return null;}
    }    
    
    /** 把String类型的数值转化成"###.00"的字符串   */
    public static String toStrWithDecimal(String decimal) {
        try {return  new DecimalFormat("###.00").format(new Double(decimal));} catch (Exception pe) { return null;}
    } 
    
    /** 把类型为"###.00"的字符串转化成Double类型的货币数值 */
    public static Double toDoubleData(String price) {
        try {return new Double(new DecimalFormat("###.00").parse(String.valueOf(price)).doubleValue());} catch (Exception pe) { return null;}
    }  
	//------------------------------   char型数值与整型数据间的转换   ---------------------
	public static int toInt(char c){try{return c;}catch(Exception e){return 0;}}
	public static int toChar(int c){try{return c;}catch(Exception e){return 0;}}

	//------------------------------   Boolean型数值与   ---------------------
	/** 通用转换：true--> 1 false-->0  */
	public static int toIntYes2One(boolean status){if(status) return 1; return 0;}
	public static boolean toBooleanOne2Yes(int status){if(status == 1) return true; return false;}
	
	//------------------------------  Boolean型数值与数据库字典中定义的值相互转换  --------------------- 
	public static boolean toBoolean(String status){	
		if("1".equals(status.trim())) return true;
		if("TRUE".equals(status.toUpperCase().trim())) return true;
		return false;
	} 

	//-------------------------   通用类型转换：先转成Double在根据需求转换   ------------- 
	//简单类型
	public static int     toInt(Object obj) {       try{return toDouble(obj).intValue();}catch(Exception e){return 0;} } 
	public static long    toLongSmp(Object obj) {   try{return toDouble(obj).longValue();}catch(Exception e){return 0;}}  
	public static float   toFloatSmp(Object obj) {  try{return toDouble(obj).floatValue();}catch(Exception e){return 0;}} 
	public static double  toDoubleSmp(Object obj) { try{return toDouble(obj).doubleValue();}catch(Exception e){return 0;}} 	
	
	//引用类型
	public static Integer toInteger(Object obj) {   try{return toDouble(obj).intValue();}catch(Exception e){return 0;}} 
	public static Long    toLong(Object obj) {      try{return toDouble(obj).longValue();}catch(Exception e){return 0L;}} 	
	public static Float   toFloat(Object obj) {     try{return toDouble(obj).floatValue();}catch(Exception e){return 0f;}  } 	
	public static Double  toDouble(Object obj){     try{return new Double(obj.toString());}catch(Exception e){return 0d;} } 
	
	public static String  toString(Object obj){  
		if(obj == null)
			return "";
		try{
			if(obj instanceof Double)	return new DecimalFormat("0.00").format(obj);
			if(obj instanceof Float)	return new DecimalFormat("0.00").format(obj);			
			return obj.toString().replaceAll("\"", "\\\"");
		}catch(Exception e){return "";} 	
	} 
	
	//3位小数
	public static String  toString1(Object obj){  
		if(obj == null)
			return "";
		try{
			if(obj instanceof Double)	return new DecimalFormat("0.000").format(obj);
			if(obj instanceof Float)	return new DecimalFormat("0.000").format(obj);			
			return obj.toString().replaceAll("\"", "\\\"");
		}catch(Exception e){return "";} 	
	} 
	
	public static String trim(Object obj)
	{
		String str = toString(obj).trim();
		//str = str.replaceAll("null", "");
		return str.replaceAll("\\s*", "");
		 //str.replaceAll("undefined", "");
	}
	
	public static String  toString6(Object obj){    
		try{
			if(obj instanceof Double)	return new DecimalFormat("0.000000").format(obj);
			if(obj instanceof Float)	return new DecimalFormat("0.000000").format(obj);			
			return obj.toString();
		}catch(Exception e){return null;} 	
	} 
	
	
	/**
	 * BigDecimal转换为String
	 * 
	 * @param number BigDecimal数字
	 * @param length 字符串长度
	 * @return
	 */
	public static String format(BigDecimal number, int length) {
		String str = "";
		
		if (length == 0) {
			return "";
		}
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMinimumIntegerDigits(length);
		
		//去除结果中的","
		try {
			str = formatter.format(number).replace(",", "");
		} catch (Exception e) {
			return "";
		}
		
		return str;
	}
	
	/**
	 * 日期转字符
	 * @param date 日期类型
	 * @param pattern 格式如yyyy-MM-dd
	 * @return
	 */
	public static String dateToString(Date date,String pattern){
		if(date == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	/**
	 * 日期字符格式化成字符串
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String StringToString(String date,String pattern){
		return dateToString(StringToDate(date, pattern), pattern);
	}
	
	/**
	 * 判断参数是否为空
	 * @param obj
	 * @return
	 */
	public static boolean isNoEmpty(Object obj){
		if(obj==null)
			return false;
		String s = trim(obj);
		if(s.equals("") )
			return false;
		return true;		
	}
	
	 /* 字符串转换到时间格式
	 * @param dateStr 需要转换的字符串
	 * @param formatStr 需要格式的目标字符串  举例 yyyy-MM-dd
	 * @return Date 返回转换后的时间
	 * @throws ParseException 转换异常
	 */
	public static Date StringToDate(String dateStr,String formatStr){
		if(dateStr==null || dateStr.equals(""))
			return null;
		DateFormat sdf=new SimpleDateFormat(formatStr);
		Date date=null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	} 
	
	public static String DateTimeToString(long dateTime,String formatStr){
		Date date = new Date(dateTime);
		return DateToString(date,formatStr);
	}
	
	public static String DateToString(Date date,String formatStr){
		return new SimpleDateFormat(formatStr).format(date);
	}
	
	/**
	 * 搜索sql语句查询出来的map值
	 * @param obj
	 * @param key
	 * @return
	 */
	public static Object MapKeyToValue(Map obj,String key){
		if(obj == null)
			return "";
		if(obj.get(key.toUpperCase()) == null)
			return "";
		return obj.get(key.toUpperCase());
	}
	
	public static boolean IsHasString(String[] strs,String str){
		if(strs==null || strs.length==0){
			return false;
		}
		for(String s : strs){
			if(s.equals(str)){
				return true;
			}
		}
		return false;
	}
	
	public static String IdsToIds(String ids){
		String str = "";
		if(isNoEmpty(ids)){
			String[] idss = ids.split(",");
			String f = "";
			for(String v : idss){
				str += f+"'"+v+"'";
				f = ",";
			}			
		}
		return str;
	}
	
	public static String ClobToString(Object obj) {
		if(obj == null)
			return "";
		Clob clob=(Clob) obj;
		StringBuffer sb = null;
		try {
			Reader rd=clob.getCharacterStream();
			BufferedReader br=new BufferedReader(rd);
			String s="";
			sb=new StringBuffer();
			while((s=br.readLine())!=null){
				sb.append(s);
			}
		} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

		return sb.toString();
	}
	
	/**
	 * 去除html标签
	 * @param htmlStr
	 * @return
	 */
	 public static String delHTMLTag(String htmlStr){ 
//         String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>";
//         String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; 
//         String regEx_html="<[^>]+>";
//         Pattern p_script=Pattern.compile(regEx_script, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
//         Matcher m_script=p_script.matcher(htmlStr);
//         htmlStr=m_script.replaceAll("");    
//         Pattern p_style=Pattern.compile(regEx_style, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
//         Matcher m_style=p_style.matcher(htmlStr);
//         htmlStr=m_style.replaceAll("");     
//         Pattern p_html=Pattern.compile(regEx_html, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
//         Matcher m_html=p_html.matcher(htmlStr);
//         htmlStr=m_html.replaceAll("");
//        return htmlStr.trim(); 
		  String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
          String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
          String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
          Pattern  p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
          Matcher m_script = p_script.matcher(htmlStr);
          htmlStr = m_script.replaceAll(""); //过滤script标签

          Pattern p_style = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
          Matcher m_style = p_style.matcher(htmlStr);
          htmlStr = m_style.replaceAll(""); //过滤style标签

          Pattern p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
          Matcher m_html = p_html.matcher(htmlStr);
          htmlStr = m_html.replaceAll(""); //过滤html标签
          htmlStr=htmlStr.replace("&nbsp;","");
          return htmlStr;
     }
	 
	
	 /**
	  * 获得uuid
	  * @return
	  */
	 public static String getUUID(){
		 String uuID=UUID.randomUUID().toString();
		 String id = "";
			String oid[] = uuID.split("-");
			for(int i = 0 ; i < oid.length ; i++){
				id +=  oid[i];   
			}
			return id;
	 }
	 
	 /**  
	     * 计算两个日期之间相差的天数  
	     * @param smdate 较小的时间 
	     * @param bdate  较大的时间 
	     * @return 相差天数 
	     * @throws ParseException  
	     */    
	    public static int daysBetween(Date smdate,Date bdate)    
	    {    
	        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
	        try {
				smdate=sdf.parse(sdf.format(smdate));
				bdate=sdf.parse(sdf.format(bdate));  
			} catch (ParseException e) {
				e.printStackTrace();
			}  
	        Calendar cal = Calendar.getInstance();    
	        cal.setTime(smdate);    
	        long time1 = cal.getTimeInMillis();                 
	        cal.setTime(bdate);    
	        long time2 = cal.getTimeInMillis();         
	        long between_days=(time2-time1)/(1000*3600*24);  
	            
	       return Integer.parseInt(String.valueOf(between_days));           
	    }    
	    
	    /**
	     * 比较2个时间的前后
	     * @param minDate 较小的时间 
	     * @param maxDate 较大的时间 
	     * @return 相差的时间毫秒数
	     */
	    public static long dayTimeBetween(Date minDate,Date maxDate)    
	    {    
	    	return maxDate.getTime()-minDate.getTime();
	    }
	    
	    public static Date addDateDay(Date date,int day) {  
	        if (null == date) {  
	            return date;  
	        }  
	        Calendar c = Calendar.getInstance();  
	        c.setTime(date);   //设置当前日期  
	        c.add(Calendar.DATE, day); //日期加1天  
	        date = c.getTime();  
	        return date;  
	    } 
	    
	    public static Object getJSONObject(JSONObject j,String k){
	    	try{
	    		return j.get(k);
	    	}
	    	catch(Exception e){
	    		return null;
	    	}
	    }
	    
	    public static boolean dequals(double a, double b)
	    {
	    	int retval =Double.compare(a, b);
	    	if(retval == 0){
	    		return true;
	    	}
	    	else{
	    		return false;
	    	}
	    }
	    
	    public static JSONObject toJSONObject(String str){
	    	try{
	    		if(str.equals("{}")){
	    			return null;
	    		}
	    		return new JSONObject(str);
	    	}
	    	catch(Exception e){ 
	    		e.printStackTrace();
	    		return null;
	    	}
	    }
	    
	    public static JSONArray toJSONArray(String str){
	    	try{
	    		if(str.equals("[]")){
	    			return null;
	    		}
	    		return new JSONArray(str);
	    	}
	    	catch(Exception e){ 
	    		e.printStackTrace();
	    		return null;
	    	}
	    }
}
