package com.example.administrator.jymall.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {

	/**
	 * 邮箱验证
	 * @param email
	 * @return
	 */
	public static boolean emailValidation(String email) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
	}
	
	/**
	 * 是否有特殊字符
	 * @param str
	 */
	public static boolean textValidation(String str){
		String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; 
		Pattern p = Pattern.compile(regEx); 
        Matcher m = p.matcher(str); 
        return m.find();
	}
	
	 public static String PasswordValidation(String str) {  
	        if (str.matches("^\\d+$")) {  
	            return "不能纯数字";  
	        } else if (str.matches("^[a-zA-Z]+$")) {  
	        	return "不能纯字母";  
	        } 
	       /* else if (str  
	                .matches("(?i)^((\\d+[\\da-z]*[a-z]+)|([a-z]+[\\da-z]*\\d+)|([a-z]+[\\da-z]*[a-z]*)|(\\d+[\\da-z]*\\d*))$")) {  
	            System.out.println("密码强");  
	        } */
	        else {  
	            return "";
	        }  
	}  
	 
	 
	 public String checkPassword(String passwordStr) {  
         String regexZ = "\\d*";  
         String regexS = "[a-zA-Z]+";  
         String regexT = "\\W+$";  
         String regexZT = "\\D*";  
         String regexST = "[\\d\\W]*";  
         String regexZS = "\\w*";  
         String regexZST = "[\\w\\W]*";  

         if (passwordStr.matches(regexZ)) {  
             return "弱";  
         }  
         if (passwordStr.matches(regexS)) {  
             return "弱";  
         }  
         if (passwordStr.matches(regexT)) {  
             return "弱";  
         }  
         if (passwordStr.matches(regexZT)) {  
             return "中";  
         }  
         if (passwordStr.matches(regexST)) {  
             return "中";  
         }  
         if (passwordStr.matches(regexZS)) {  
             return "中";  
         }  
         if (passwordStr.matches(regexZST)) {  
             return "强";  
         }  
         return "";  
 }  
}
