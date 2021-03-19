package cn.beingyi.apkenceyptor.request;

import cn.beingyi.apkenceyptor.utils.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class activateKey extends BaseTask{

    public activateKey(UserThread userThread) throws Exception {
        super(userThread);

        String Key= ServerBES.encode(jsonData.getString("Key"));

        Connection con= JDBCUtils.getConnection();
        PreparedStatement ptmt=con.prepareStatement("select * from `keys` where `Key`=? and isUsed='0';");
        ptmt.setString(1,Key);
        ResultSet resultSet=ptmt.executeQuery();
        if(resultSet.next()){

            int Type=resultSet.getInt("Type");
            if(Type==1){
                UserUtils.addVIPDays(Token,31);
            }
            if(Type==2){
                UserUtils.addVIPDays(Token,91);
            }
            if(Type==3){
                UserUtils.addVIPDays(Token,366);
            }

            Tools tools=new Tools();
            tools.update("keys","isUsed","1","Key",Key);
            tools.close();

            resultJSON.put("result",true);
            resultJSON.put("msg",getString("key_activated_successfully"));

        }else{
            resultJSON.put("result",false);
            resultJSON.put("msg",getString("the_key_does_not_exist"));
        }


        ptmt.close();
        con.close();


        writeAndExit();
    }
}
