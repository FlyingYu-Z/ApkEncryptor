package cn.beingyi.apkenceyptor.request;

import cn.beingyi.apkenceyptor.utils.*;

import java.sql.*;

public class signup extends BaseTask {
    public signup(UserThread userThread) throws Exception {
        super(userThread);


        String Email = jsonData.getString("Email");
        String Password = jsonData.getString("Password");
        String Code = jsonData.getString("Code");

        if (UserUtils.isEmailExists(Email)) {
            resultJSON.put("result", false);
            resultJSON.put("msg", getString("the_email_you_inputed_already_exists"));
            writeAndExit();
            return;
        }

        if (!isCodeValid(Email, Code)) {
            resultJSON.put("result", false);
            resultJSON.put("msg", getString("the_code_is_invalid_or_wrong"));
            writeAndExit();
            return;
        }

        if(!insertUser(Email,Password)){
            resultJSON.put("result", false);
            resultJSON.put("msg", getString("failed_to_signup"));
            writeAndExit();
            return;
        }
        try {
            setCodeUsed(Email, Code);
        }catch (Exception e){
            e.printStackTrace();
        }

        resultJSON.put("result", true);
        resultJSON.put("msg", getString("signup_successfully"));
        writeAndExit();
    }

    private boolean isCodeValid(String Email, String code) throws Exception {

        Connection con = JDBCUtils.getConnection();
        String sql = "select * from code where Email =? and Code =? and isUsed='0';";
        PreparedStatement ptmt = con.prepareStatement(sql);
        ptmt.setString(1, Email);
        ptmt.setString(2, code);
        ResultSet resultSet = ptmt.executeQuery();
        if (resultSet.next()) {
            String time = resultSet.getString("CreateTime");

            long seconds = TimeUtils.getTimeDifferenceSeconds(time, TimeUtils.getCurrentTime());

            if (seconds / 60 > 5) {
                return false;
            }

            return true;
        } else {
            return false;
        }

    }

    private void setCodeUsed(String Email, String Code) throws Exception {

        Connection con = JDBCUtils.getConnection();
        String sql = "update code set isUsed='1' where Email=? and Code=?;";
        PreparedStatement ptmt = con.prepareStatement(sql);
        ptmt.setString(1, Email);
        ptmt.setString(2, Code);
        ptmt.executeUpdate();
        JDBCUtils.closeResource(ptmt, con);

    }



    public boolean insertUser(String Email, String Password) throws Exception {
        boolean result = false;
        Connection con = JDBCUtils.getConnection();
        Statement stmt = con.createStatement();
        String time = TimeUtils.getCurrentTime();

        String sql = "insert into users(Email,Password,SignupTime,SignupIP) values (?,?,?,?)";
        PreparedStatement ptmt = con.prepareStatement(sql);
        ptmt.setString(1, Email);
        ptmt.setString(2, MD5.encode(Password));
        ptmt.setString(3, time);
        ptmt.setString(4, userThread.socket.getInetAddress().getHostName() + "");

        ptmt.execute();
        ptmt.close();

        JDBCUtils.closeResource(stmt, con);
        result = true;

        return result;
    }


}
