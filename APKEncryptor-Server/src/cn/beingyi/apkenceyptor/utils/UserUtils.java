package cn.beingyi.apkenceyptor.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//已修复MySQL注入漏洞
public class UserUtils {


	public static boolean isEmailExists(String Email)throws Exception{

		Connection con = JDBCUtils.getConnection();
		String sql = "select * from users where Email =?;";
		PreparedStatement ptmt = con.prepareStatement(sql);
		ptmt.setString(1,Email);
		ResultSet resultSet = ptmt.executeQuery();
		if(resultSet.next()){
			return true;
		}else {
			return false;
		}

	}

	
	public static void addVIPDays(int Token, int days) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Tools tools=new Tools();
		String vip=tools.find("users", "VIP", "ID", Token+"");
		
		Date endDate = null;
		if (UserUtils.isVIP(Token)) {
			endDate = sdf.parse(vip);
		} else {
			endDate = new Date();
		}
		
		endDate = addDate(endDate, days);
		tools.update("users", "VIP", sdf.format(endDate), "ID", Token+"");
		tools.close();
		
	}
	
	

	public static Date addDate(Date date, int day) {

		Date result = null;

		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		cl.add(Calendar.DAY_OF_YEAR, day);
		result = cl.getTime();
		return result;

	}
	

	
	public static boolean addMoney(String Token, String mMoney) {
		if(mMoney==null) {
			return false;
		}
		
		if(mMoney.equals("")) {
			return false;
		}
		
		
		boolean result = false;
		Float Money=0f;
		try {
			Money=Float.parseFloat(mMoney);
			Connection con = JDBCUtils.getConnection();

			String sql = "update users set Money=Money+" + Money + " where ID=?;";
			PreparedStatement ptmt = con.prepareStatement(sql);
			ptmt.setInt(1,Integer.parseInt(Token));
			ptmt.executeUpdate();
			result = true;
			JDBCUtils.closeResource(ptmt, con);
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	

	
	public static boolean reduceMoney(String Token, String mMoney) {
		if(mMoney==null) {
			return false;
		}
		
		if(mMoney.equals("")) {
			return false;
		}
		
		
		boolean result = false;
		Float Money=0f;
		try {
			Money=Float.parseFloat(mMoney);
			Connection con = JDBCUtils.getConnection();
			String sql = "update users set Money=Money-" + Money + " where ID=?;";
			PreparedStatement ptmt = con.prepareStatement(sql);
			ptmt.setInt(1,Integer.parseInt(Token));
			ptmt.executeUpdate();
			result = true;
			JDBCUtils.closeResource(ptmt, con);
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	
	


	

	


	
	
	

	public static boolean isVIP(int Token) {
		boolean result=false;
		
		try {
			Connection con = JDBCUtils.getConnection();

			String sql = "select * from users where ID=?;";
			PreparedStatement ptmt = con.prepareStatement(sql);
			ptmt.setInt(1,Token);
			ResultSet resultSet = ptmt.executeQuery();
			if(resultSet.next()) {
				
				String vip=resultSet.getString("VIP");
				if(vip!=null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					long days = getDaysBetween(new Date(), sdf.parse(vip));

					if (days <= 0) {
						result = false;
					} else {
						result = true;
					}
				}else {
					result=false;
				}
                
			}
			
			JDBCUtils.closeResource(resultSet, ptmt, con);
			
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		
		return result;
	}





	public static Long getDaysBetween(Date startDate, Date endDate) {  
        Calendar fromCalendar = Calendar.getInstance();  
        fromCalendar.setTime(startDate);  
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        fromCalendar.set(Calendar.MINUTE, 0);  
        fromCalendar.set(Calendar.SECOND, 0);  
        fromCalendar.set(Calendar.MILLISECOND, 0);  
  
        Calendar toCalendar = Calendar.getInstance();  
        toCalendar.setTime(endDate);  
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        toCalendar.set(Calendar.MINUTE, 0);  
        toCalendar.set(Calendar.SECOND, 0);  
        toCalendar.set(Calendar.MILLISECOND, 0);  
  
        return (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);  
    }  
	
	
}
