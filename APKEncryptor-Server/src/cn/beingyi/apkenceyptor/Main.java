package cn.beingyi.apkenceyptor;

import cn.beingyi.apkenceyptor.sql.InitDataBase;
import cn.beingyi.apkenceyptor.utils.*;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static cn.beingyi.apkenceyptor.utils.JDBCUtils.getConnection;

public class Main {

    public static void main(String[] args) {


        Main main=new Main();
        try {
            main.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public List<UserThread> userThreadList = new ArrayList<>();


    public void start() throws Exception {

        if(!new File(Conf.ConfPath).exists()){
            System.out.println("配置文件不存在");
            System.exit(0);
            return;
        }


        try {
            InitDataBase.initConfs();
            new InitDataBase("users","users.txt");
            new InitDataBase("feedback","feedback.txt");
            new InitDataBase("code","code.txt");
            new InitDataBase("keys","keys.txt");

            InitDataBase.checkConf("MonthVipPrice","2","月付会员价格");
            InitDataBase.checkConf("SeasonVipPrice","4","季付会员价格");
            InitDataBase.checkConf("YearVipPrice","12","年付会员价格");
            InitDataBase.checkConf("license","","用户使用协议");


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("安装失败");
            System.exit(0);
            return;
        }
        
        int port = 6666;
        ServerSocket server;
        try {
            server = new ServerSocket(port);
            System.out.println("服务器已开启:");
            System.out.println("监听端口：" + port);
            System.out.println("配置文件路径：" + Conf.ConfPath);

            InetAddress ia=InetAddress.getLocalHost();
            String ip=ia.getHostAddress();

            System.out.println("服务器IP:"+ip);
        }catch (Exception e){
            System.out.println("端口被占用："+port);
            System.exit(0);
            return;
        }


        Socket socket = null;
        while (true) {
            socket = server.accept();
            System.out.println("客户端已连接" + socket.getInetAddress());

            UserThread thread = new UserThread(this,socket);
            thread.start();
            userThreadList.add(thread);

        }
    }


}
