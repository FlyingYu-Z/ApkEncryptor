package cn.beingyi.apkenceyptor.utils;

import java.security.MessageDigest;

public class MD5
{
    
	public static String encode(String str){
		MessageDigest md5 = null;  
		try{  
			md5 = MessageDigest.getInstance("MD5");  
		}catch (Exception e){  
			System.out.println(e.toString());  
			e.printStackTrace();  
			return "";  
		}  
		char[] charArray = str.toCharArray();  
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)  
			byteArray[i] = (byte) charArray[i];  
		byte[] md5Bytes = md5.digest(byteArray);  
		StringBuffer hexValue = new StringBuffer();  
		for (int i = 0; i < md5Bytes.length; i++){  
			int val = ((int) md5Bytes[i]) & 0xff;  
			if (val < 16)  
				hexValue.append("0");  
			hexValue.append(Integer.toHexString(val));  
		}  
		return hexValue.toString().toUpperCase();
	}


	public static String encode16(String encryptStr) {
		return encode(encryptStr).substring(8, 24).toUpperCase();
	}



}
