package cn.beingyi.apkenceyptor.request;

import cn.beingyi.apkenceyptor.utils.JDBCUtils;
import cn.beingyi.apkenceyptor.utils.TimeUtils;
import cn.beingyi.apkenceyptor.utils.UserThread;
import cn.beingyi.apkenceyptor.utils.UserUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static jdk.nashorn.internal.runtime.regexp.joni.Syntax.Java;

public class getUserInfo extends BaseTask {
    public getUserInfo(UserThread userThread) throws Exception {
        super(userThread);

        Connection con = JDBCUtils.getConnection();
        Statement stmt = con.createStatement();
        String time = TimeUtils.getCurrentTime();

        String sql = "select * from users where ID=\"" + Token + "\";";
        ResultSet resultSet = stmt.executeQuery(sql);


        if (resultSet.next()) {
            resultJSON.put("result",true);
            resultJSON.put("VIP",resultSet.getString("VIP")==null?"":resultSet.getString("VIP"));
            resultJSON.put("isLifeTimeVip",resultSet.getInt("isLifeTimeVip")==1);
            resultJSON.put("isForbidden",resultSet.getInt("isForbidden")==1);
            resultJSON.put("LoginTime",getDateFormat(resultSet.getTimestamp("LoginTime")));
            resultJSON.put("SignupTime",getDateFormat(resultSet.getTimestamp("SignupTime")));
            resultJSON.put("isVIP", UserUtils.isVIP(Token));



        }else{
            resultJSON.put("result",false);
        }




        JDBCUtils.closeResource(resultSet, stmt, con);
        writeAndExit();


    }


    private  String getDateFormat(Timestamp timestamp){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(timestamp);
        return date;
    }

}
