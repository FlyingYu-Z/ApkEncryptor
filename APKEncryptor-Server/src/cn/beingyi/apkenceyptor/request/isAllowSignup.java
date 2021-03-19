package cn.beingyi.apkenceyptor.request;

import cn.beingyi.apkenceyptor.utils.JDBCUtils;
import cn.beingyi.apkenceyptor.utils.MD5;
import cn.beingyi.apkenceyptor.utils.TimeUtils;
import cn.beingyi.apkenceyptor.utils.UserThread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class isAllowSignup extends BaseTask {
    public isAllowSignup(UserThread userThread) throws Exception {
        super(userThread);

        String Email=jsonData.getString("Email");


        Connection con = JDBCUtils.getConnection();
        String sql = "select * from users where Email =?;";
        PreparedStatement ptmt = con.prepareStatement(sql);
        ptmt.setString(1,Email);
        ResultSet resultSet = ptmt.executeQuery();
        if(resultSet.next()){
            resultJSON.put("result",false);
            resultJSON.put("msg",getString("the_email_you_inputed_already_exists"));
        }else {
            resultJSON.put("result",true);
        }

        writeAndExit();
    }


}
