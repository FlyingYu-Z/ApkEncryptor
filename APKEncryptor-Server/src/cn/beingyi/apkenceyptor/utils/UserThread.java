package cn.beingyi.apkenceyptor.utils;

import cn.beingyi.apkenceyptor.Main;
import cn.beingyi.apkenceyptor.request.*;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UserThread extends Thread {

    public Main main;
    public Socket socket;
    public DataInputStream reader;
    public DataOutputStream writer;
    public String ID = "";
    public boolean isAlive = false;
    public JSONObject jsonData;

    public UserThread(Main mMain, Socket mSocket) throws Exception {
        this.main = mMain;
        this.socket = mSocket;
        this.reader = new DataInputStream(socket.getInputStream());
        this.writer = new DataOutputStream(socket.getOutputStream());
        this.isAlive = true;

    }

    private static String decode(String data) {
        String key = data.substring(data.length() - 16);
        String src = data.substring(0, data.length() - 16);
        return BES.decode(src, key);
    }

    @Override
    public void run() {
        super.run();

        try {
            int i = reader.readInt();
            if (i != 1) {
                return;
            }

            String data = decode(BES.decode(reader.readUTF(),MD5.encode16(Conf.getSign())));
            if (data == null) {
                return;
            }
            System.out.println("收到消息，准备解析:" + data);
            jsonData = new JSONObject(data);
            int Type = jsonData.getInt("Type");

            if (jsonData.has("Token")) {
                String Token = ServerBES.decode(jsonData.getString("Token"));
                this.ID = Token;
                System.out.println("用户ID：" + Token + "\r\n");
            }

            switch (Type) {
                case Custom.TYPE_LOGIN:
                    new login(this);
                    break;
                case Custom.TYPE_IS_ALLOW_SIGNUP:
                    new isAllowSignup(this);
                    break;
                case Custom.TYPE_SIGNUP_SEND_CODE:
                    new sendSignupCode(this);
                    break;

                case Custom.TYPE_SIGNUP:
                    new signup(this);
                    break;
                case Custom.TYPE_CHANGE_PASS_SEND_CODE:
                    new sendChangePassCode(this);
                    break;
                case Custom.TYPE_CHANGE_PASS_BY_CODE:
                    new changePassByCode(this);
                    break;
                case Custom.TYPE_GET_USERINFO:
                    new getUserInfo(this);
                    break;
                case Custom.TYPE_GET_PRICE:
                    new getPrice(this);
                    break;
                case Custom.TYPE_ACTIVATE_KEY:
                    new activateKey(this);
                    break;
                case Custom.TYPE_CHECK_VIP:
                    new checkVIP(this);
                    break;






                case Custom.TYPE_MANAGE_KEY:
                    new KeyManager(this);
                    break;


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeData(String data) throws IOException {

        String key = MD5.encode16(TimeUtils.getCurrentTime()).toLowerCase();
        System.out.println("输出：" + data + "\n");
        writer.writeUTF(BES.encode(data, key) + key);
        writer.flush();
    }


    public void checkAlive() {

        new Thread() {
            @Override
            public void run() {

                try {
                    Thread.sleep(30 * 1000);

                    if (!isAlive) {
                        closeSocketClient();
                        System.out.println(socket.getInetAddress() + "退出连接");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }


    public void closeSocketClient() throws IOException {
        if (this.socket != null && !this.socket.isClosed()) {
            if (this.reader != null)
                this.reader.close();
            if (this.writer != null)
                this.writer.close();
            this.socket.close();
        }
        main.userThreadList.remove(this);
        this.isAlive = false;
    }


}
