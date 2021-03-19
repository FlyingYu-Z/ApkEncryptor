package cn.beingyi.apkenceyptor.request;

import cn.beingyi.apkenceyptor.utils.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class sendChangePassCode extends BaseTask {
    public sendChangePassCode(UserThread userThread) throws Exception {
        super(userThread);

        String Email = jsonData.getString("Email");
        if(!UserUtils.isEmailExists(Email)){
            resultJSON.put("result", false);
            resultJSON.put("msg", getString("the_email_you_inputed_is_not_exists"));
            writeAndExit();
            return;
        }
        int code=(int)((Math.random()*9+1)*100000);
        String title="APK Encryptor Verification Code For Changing Password";
        String content="Your code for changing password is:"+code;
        try {
            new EmailUtils().sendEmail(Email,title,content);
            resultJSON.put("result",true);
            insertCode(code,Email);

        }catch (Exception e){
            e.printStackTrace();
            resultJSON.put("result",false);
            resultJSON.put("msg", getString("failed_to_send_mail"));
        }

        writeAndExit();

    }



    private void insertCode(int code,String Email)throws Exception{
        Connection con = JDBCUtils.getConnection();
        Statement stmt = con.createStatement();
        String time = TimeUtils.getCurrentTime();

        String sql = "insert into code(Code,Email,CreateTime) values (?,?,?)";
        PreparedStatement ptmt = con.prepareStatement(sql);

        ptmt.setString(1, String.valueOf(code));
        ptmt.setString(2, Email);
        ptmt.setString(3, time);

        ptmt.execute();
        ptmt.close();

        JDBCUtils.closeResource(stmt, con);
    }


}
