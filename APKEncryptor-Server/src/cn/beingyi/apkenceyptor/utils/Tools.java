package cn.beingyi.apkenceyptor.utils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Tools {
	
	Connection con;
	
	public Tools() {
		try {
			con=JDBCUtils.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}


	public boolean update(String Table,String Field,String Value,String Where,Object WhereValue) {
		boolean result=false;
		
		try {
			String time = TimeUtils.getCurrentTime();
			
			String sql = "update `"+Table+"` set `"+Field+"` = ? where `"+Where+"`=? limit 1;";

			PreparedStatement ptmt=con.prepareStatement(sql);
			ptmt.setString(1, Value);
			ptmt.setString(2, WhereValue.toString());
			ptmt.executeUpdate();
			ptmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	

	public boolean update(String Table,String Field,int Value,String Where,String WhereValue) {
		boolean result=false;
		
		try {
			String time = TimeUtils.getCurrentTime();
			
			String sql = "update "+Table+" set "+Field+" = ? where "+Where+"=? limit 1;";

			PreparedStatement ptmt=con.prepareStatement(sql);
			ptmt.setInt(1, Value);
			ptmt.setString(2, WhereValue);

			ptmt.executeUpdate();
			ptmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}



	public boolean addSum(String Table,String Field,int addValue,String Where,String WhereValue) {
		boolean result=false;

		try {
			String time = TimeUtils.getCurrentTime();

			String sql = "update "+Table+" set "+Field+" = "+Field+"+"+addValue+" where "+Where+"=?;";

			PreparedStatement ptmt=con.prepareStatement(sql);
			ptmt.setString(1,WhereValue);
			ptmt.executeUpdate();
			ptmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}





	public boolean delete(String Table,String Where,String WhereValue) {
		boolean result=false;
		try {
			String sql = "delete from `"+Table+"` where "+Where+"=? limit 1";
			PreparedStatement ptmt = con.prepareStatement(sql);
			ptmt.setString(1,WhereValue);
			String time = TimeUtils.getCurrentTime();
			ptmt.executeUpdate();
			
			JDBCUtils.closeStatement(ptmt);;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		
		return result;	
	}
	
	
	
	public String find(String Table,String Field,String Where,String WhereValue) {
		String value="";
		
		try {
			String sql = "select "+Field+" from "+Table+" where "+Where+"=?;";
			PreparedStatement ptmt = con.prepareStatement(sql);
			ptmt.setString(1,WhereValue);
			ResultSet resultSet=ptmt.executeQuery();
			if(resultSet.next()) {
				value=resultSet.getString(1);
				
			}
			resultSet.close();
			JDBCUtils.closeStatement(ptmt);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return value;
	}
	
	
	
	public String find(String Table,String Field,String Where1,String WhereValue1,String Where2,String WhereValue2) {
		String value="";
		
		try {
			String sql = "select "+Field+" from "+Table+" where "+Where1+"=? And  "+Where2+"=? limit 1;";
			PreparedStatement ptmt = con.prepareStatement(sql);
			ptmt.setString(1,WhereValue1);
			ptmt.setString(2,WhereValue2);
			ResultSet resultSet=ptmt.executeQuery();
			if(resultSet.next()) {
				value=resultSet.getString(1);
				
			}
			resultSet.close();
			JDBCUtils.closeStatement(ptmt);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return value;
	}



	public int getTableRows(String table) throws Exception {
		int sum;
		Connection con = JDBCUtils.getConnection();
		String sql = "select count(*) as Sum from `"+table+"`;";
		PreparedStatement ptmt = con.prepareStatement(sql);
		ResultSet resultSet = ptmt.executeQuery();
		if(resultSet.next()){
			sum = resultSet.getInt("Sum");
		}else{
			sum=0;
		}
		resultSet.close();
		return sum;
	}



	public void close() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
