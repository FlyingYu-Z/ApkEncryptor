package cn.beingyi.apkenceyptor.sql;

import cn.beingyi.apkenceyptor.utils.FileUtils;
import cn.beingyi.apkenceyptor.utils.JDBCUtils;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static cn.beingyi.apkenceyptor.utils.JDBCUtils.getConnection;


public class InitDataBase {


    public static String readTxt(String name)throws Exception{
        InputStream in= InitDataBase.class.getResourceAsStream(name);
        byte[] data= FileUtils.toByteArray(in);
        return new String(data,"UTF-8");
    }

    public InitDataBase(String tableName,String fileName) throws Exception {

        List<String> tableNames = getTableNames();
        if (!tableNames.contains(tableName)) {
            createTable(tableName);
        }

        List<String> columns=getColumnNames(tableName);
        String[] lines=readTxt(fileName).split("\n");

        for (String line : lines) {
            if(line.contains(",")) {
                String column=line.split(",")[0];
                String type=line.split(",")[1];
                if (!columns.contains(column)) {
                    addColumn(tableName, column,type);
                }
            }
        }


    }


    public void addColumn(String tableName,String columnName,String type)throws  Exception{

        String sql = "alter table `"+tableName+"` add `"+columnName+"` "+type+" null";

        Connection con = getConnection();
        Statement stmt = con.createStatement();

        if (stmt.executeLargeUpdate(sql)==0) {
            System.out.println("初始化"+columnName+"成功！");
        }else{
            System.out.println("初始化"+columnName+"失败！");
        }

        JDBCUtils.closeResource(stmt, con);


    }

    public void createTable(String tableName) throws Exception {


        String sql = "create table if not exists `" + tableName + "` (\n" +
                "  `ID` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  PRIMARY KEY (`ID`)\n" +
                ");";

        Connection con = getConnection();
        Statement stmt = con.createStatement();

        if (stmt.executeLargeUpdate(sql) == 0) {
            System.out.println("初始化"+tableName+"成功！");
        }else{
            System.out.println("初始化"+tableName+"失败！");
        }

        JDBCUtils.closeResource(stmt, con);

    }

    public List<String> getTableNames() throws Exception {
        List<String> tableNames = new ArrayList<>();
        Connection con = getConnection();
        ResultSet resultSet = null;
        DatabaseMetaData db = con.getMetaData();
        resultSet = db.getTables(null, null, null, new String[]{"TABLE"});
        while (resultSet.next()) {
            tableNames.add(resultSet.getString(3));
        }
        resultSet.close();
        con.close();
        return tableNames;
    }

    /**
     * 获取表中所有字段名称
     *
     * @param tableName 表名
     * @return
     */
    public static List<String> getColumnNames(String tableName) throws Exception {
        List<String> columnNames = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement stmt = null;
        String tableSql = "select * from `" + tableName+"`";
        stmt = con.prepareStatement(tableSql);
        ResultSetMetaData rsmd = stmt.getMetaData();
        int size = rsmd.getColumnCount();
        for (int i = 0; i < size; i++) {
            columnNames.add(rsmd.getColumnName(i + 1));
        }

        stmt.close();
        con.close();

        return columnNames;
    }




    public static void initConfs() throws Exception {

        String sql = "create table if not exists `" + "confs" + "` (\n" +
                "  `Name` varchar(255) NOT NULL,\n" +
                "  `Value` longtext NULL,\n" +
                "  `Mark` varchar(255) NULL,\n" +
                "  PRIMARY KEY (`Name`)\n" +
                ");";

        Connection con = getConnection();
        Statement stmt = con.createStatement();

        if (stmt.executeLargeUpdate(sql) == 0) {
            System.out.println("初始化confs成功！");
        }else{
            System.out.println("初始化confs失败！");
        }

        JDBCUtils.closeResource(stmt, con);


    }




    public static void checkConf(String Name,String defaultValue,String Mark) throws Exception {
        Connection con = getConnection();
        Statement stmt = con.createStatement();


        String sql = "select * from confs where Name='" + Name + "';";
        ResultSet resultSet = stmt.executeQuery(sql);
        if(!resultSet.next()){

            PreparedStatement ptmt = con.prepareStatement("insert into confs(Name,Value,Mark) values (?,?,?)");

            ptmt.setString(1, Name);
            ptmt.setString(2, defaultValue);
            ptmt.setString(3, Mark);
            ptmt.execute();
            ptmt.close();


            System.out.println("初始化："+Name+"->"+defaultValue);
        }

        JDBCUtils.closeResource(resultSet,stmt, con);

    }





}
