package cn.beingyi.apkenceyptor.request;

import cn.beingyi.apkenceyptor.utils.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class changePassByCode extends BaseTask {
    public changePassByCode(UserThread userThread) throws Exception {
        super(userThread);


        String Email = jsonData.getString("Email");
        String newPass = jsonData.getString("newPass");
        String Code = jsonData.getString("Code");

        if (!UserUtils.isEmailExists(Email)) {
            resultJSON.put("result", false);
            resultJSON.put("msg", getString("the_email_you_inputed_is_not_exists"));
            writeAndExit();
            return;
        }

        if (!isCodeValid(Email, Code)) {
            resultJSON.put("result", false);
            resultJSON.put("msg", getString("the_code_is_invalid_or_wrong"));
            writeAndExit();
            return;
        }


        resultJSON.put("result", false);
        resultJSON.put("msg", getString("failed_to_modify_password"));

        changePass(Email,newPass);
        setCodeUsed(Email,Code);

        resultJSON.put("result", true);
        resultJSON.put("msg", getString("password_modified_successfully"));

        writeAndExit();
    }


    private void changePass(String Email,String newPass)throws Exception{


        Connection con = JDBCUtils.getConnection();
        String sql = "update users set Password=? where Email =?;";
        PreparedStatement ptmt = con.prepareStatement(sql);
        ptmt.setString(1, MD5.encode(newPass));
        ptmt.setString(2, Email);
        ptmt.executeUpdate();
        ptmt.close();
        con.close();

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
                System.out.println("时间大于5分钟");
                return false;
            }

            return true;
        } else {
            System.out.println("验证码不存在");
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



}
