package cn.beingyi.apkenceyptor.request;

import cn.beingyi.apkenceyptor.utils.EmailUtils;
import cn.beingyi.apkenceyptor.utils.JDBCUtils;
import cn.beingyi.apkenceyptor.utils.TimeUtils;
import cn.beingyi.apkenceyptor.utils.UserThread;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

public class sendSignupCode extends BaseTask {
    public sendSignupCode(UserThread userThread) throws Exception {
        super(userThread);

        String Email = jsonData.getString("Email");
        int code=(int)((Math.random()*9+1)*100000);
        String title="APK Encryptor Signup Verification Code";
        String content="Your code for signup is:"+code;
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
