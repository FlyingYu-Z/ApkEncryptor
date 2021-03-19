package com.fly.apkencryptor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

	public static String getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}

	public static String getCurrentTime(String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		return df.format(new Date());
	}

	public static String getAddedCurrentTime(String pattern,int minute) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);

		Date date = new Date();
		Calendar cl = Calendar.getInstance();
		cl.add(Calendar.MINUTE, minute);
		date = cl.getTime();

		return df.format(date);

	}



	public static String getCurrentDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(new Date());
	}
	
	

	public static String getCurrentDay() {
		SimpleDateFormat df = new SimpleDateFormat("dd");
		return df.format(new Date());
	}


	public static int getNowMinute() {
		SimpleDateFormat df = new SimpleDateFormat("mm");
		return Integer.parseInt(df.format(new Date()));
	}

	public static int getNowSecond() {
		SimpleDateFormat df = new SimpleDateFormat("ss");
		return Integer.parseInt(df.format(new Date()));
	}


	public static String getToday() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(new Date());
	}



	/*
	 * 将时间转换为时间戳
	 */
	public static String dateToStamp(String s) throws ParseException {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = simpleDateFormat.parse(s);
		long ts = date.getTime();
		res = String.valueOf(ts);
		return res;
	}

	/*
	 * 将时间戳转换为时间
	 */
	public static String stampToDate(String s){
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long lt = new Long(s);
		Date date = new Date(lt);
		res = simpleDateFormat.format(date);
		return res;
	}



}
