package cn.beingyi.apkenceyptor.request;


import cn.beingyi.apkenceyptor.strings.Strings;
import cn.beingyi.apkenceyptor.utils.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class login extends BaseTask {


    public login(UserThread userThread) throws Exception {
        super(userThread);

        String Account = jsonData.getString("Account");
        String Password = jsonData.getString("Password");
        String IP = userThread.socket.getInetAddress().getHostName();

        Connection con = JDBCUtils.getConnection();
        String sql = "select * from users where Email =? And Password=?;";
        PreparedStatement ptmt = con.prepareStatement(sql);
        ptmt.setString(1,Account);
        ptmt.setString(2, MD5.encode(Password));

        String time = TimeUtils.getCurrentTime();

        ResultSet resultSet = ptmt.executeQuery();

        if (resultSet.next()) {

            if(!jsonData.has("LoginKey")) {

                boolean isForbidden = resultSet.getString("isForbidden").equals("1");
                this.Token = resultSet.getInt("ID");
                resultJSON.put("result", !isForbidden);
                resultJSON.put("Token", ServerBES.encode(String.valueOf(Token)));
                resultJSON.put("ID", Token);
                resultJSON.put("LoginKey", MD5.encode(time));
                resultJSON.put("msg", getString("login_successfully"));

                Tools tools = new Tools();
                tools.update("users", "LoginTime", time, "ID", Token);
                tools.update("users", "LoginIP", IP, "ID", Token);
                tools.update("users", "LoginKey", MD5.encode(time), "ID", Token);
                tools.close();
            }else {
                if(!jsonData.getString("LoginKey").equals(resultSet.getString("LoginKey"))){
                    resultJSON.put("result", false);
                    resultJSON.put("Token", "");
                    resultJSON.put("msg", getString("login_failed"));
                }else {
                    resultJSON.put("result", true);
                }

            }

        } else {
            resultJSON.put("result", false);
            resultJSON.put("Token", "");
            resultJSON.put("msg", getString("wrong_account_or_password"));

        }


        JDBCUtils.closeResource(resultSet, ptmt, con);
        writeAndExit();

    }



}
