package cn.beingyi.apkenceyptor.request;

import cn.beingyi.apkenceyptor.strings.Strings;
import cn.beingyi.apkenceyptor.utils.UserThread;
import org.json.JSONObject;

import java.io.IOException;

public class BaseTask {

    public UserThread userThread;
    public JSONObject jsonData;
    public JSONObject resultJSON = new JSONObject();
    public int Token=0;
    public String IP;
    public String language="";

    public BaseTask(UserThread userThread)throws Exception{
        this.userThread=userThread;
        this.jsonData=userThread.jsonData;
        if(!userThread.ID.isEmpty()) {
            this.Token = Integer.parseInt(userThread.ID);
        }
        this.IP=userThread.socket.getInetAddress().getHostName();
        this.language=jsonData.optString("language");
        if(language.isEmpty()){
            language="en";
        }

    }

    public String getString(String id){
        return Strings.getString(language,id);
    }

    public void writeAndExit(){
        try {
            userThread.writeData(resultJSON.toString());
            userThread.closeSocketClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
