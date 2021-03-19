package cn.beingyi.apkenceyptor.request;

import cn.beingyi.apkenceyptor.utils.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class KeyManager extends BaseTask {
    public KeyManager(UserThread userThread) throws Exception {
        super(userThread);

        int Code=jsonData.getInt("Code");
        if(Code==1){
            int cuSize=jsonData.getInt("cuSize");
            getKeys(cuSize);
        }

        if(Code==2){
            int Sum=jsonData.getInt("Sum");
            int KeyType=jsonData.getInt("KeyType");
            String Mark=jsonData.getString("Mark");

            createKeys(Sum,KeyType,Mark);
        }

        if(Code==3){
            String List=jsonData.getString("List");
            deleteKeys(List);
        }

        writeAndExit();
    }

    private void getKeys(int cuSize) throws Exception {
        Connection con = JDBCUtils.getConnection();
        Statement stmt = con.createStatement();
        String time = TimeUtils.getCurrentTime();
        String sql = "select * from `keys` order by CreateTime desc limit " + cuSize + ",50;";
        ResultSet resultSet = stmt.executeQuery(sql);
        JSONArray jsonArray=new JSONArray();
        while (resultSet.next()) {
            String ID=resultSet.getString("ID");
            String Key=resultSet.getString("Key");
            String Type=resultSet.getString("Type");
            String UserID=resultSet.getString("UserID");
            String isUsed=resultSet.getString("isUsed");
            String Mark=resultSet.getString("Mark");
            String CreateTime=resultSet.getString("CreateTime");
            String ActivateTime=resultSet.getString("ActivateTime");

            JSONObject obj=new JSONObject();
            obj.put("ID",ID);
            obj.put("Key",ServerBES.decode(Key));
            obj.put("Type",Type);
            obj.put("UserID",UserID);
            obj.put("isUsed",isUsed);
            obj.put("Mark",Mark);
            obj.put("CreateTime",CreateTime);
            obj.put("ActivateTime",ActivateTime);

            jsonArray.put(obj);


        }
        JDBCUtils.closeResource(resultSet, stmt, con);

        resultJSON.put("result",true);
        resultJSON.put("msg","获取成功");
        resultJSON.put("Array",jsonArray);

    }


    private void createKeys(int sum, int type,String mark) throws Exception {

        Connection con = JDBCUtils.getConnection();

        for (int i = 0; i < sum; i++) {
            String time = TimeUtils.getCurrentTime();
            String sql = "insert into `keys`(`Key`,`Type`,Mark,CreateTime) values (?,?,?,?)";
            PreparedStatement ptmt = con.prepareStatement(sql);
            long code = (long) ((Math.random() * 9 + 1) * 1000) + System.currentTimeMillis();
            String key = ServerBES.encode(MD5.encode(code + ""));
            ptmt.setString(1, key);
            ptmt.setInt(2, type);
            ptmt.setString(3, mark);
            ptmt.setString(4, time);
            ptmt.execute();
            ptmt.close();

        }
        con.close();


        resultJSON.put("result",true);
        resultJSON.put("msg","生成成功");
    }

    private void deleteKeys(String data)throws Exception{
        List<String> list=ListUtil.StringToList(data);
        Tools tools=new Tools();
        for(String ID:list){
            tools.delete("keys","ID",ID);
        }
        tools.close();

        resultJSON.put("result",true);
        resultJSON.put("msg","删除成功");
    }

}
