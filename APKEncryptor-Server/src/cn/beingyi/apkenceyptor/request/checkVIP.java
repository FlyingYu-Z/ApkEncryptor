package cn.beingyi.apkenceyptor.request;

import cn.beingyi.apkenceyptor.utils.MD5;
import cn.beingyi.apkenceyptor.utils.UserThread;
import cn.beingyi.apkenceyptor.utils.UserUtils;

public class checkVIP extends BaseTask {
    public checkVIP(UserThread userThread) throws Exception {
        super(userThread);

        int Feature=jsonData.getInt("Feature");

        if(UserUtils.isVIP(Token)){
            resultJSON.put("result",true);
            resultJSON.put("key", MD5.encode("cn.beingyi.apkencryptor-en"));//所有文件解密密码
            resultJSON.put("pass", MD5.encode("cn.beingyi.apkencryptor2"));


        }else {
            resultJSON.put("result",false);
            resultJSON.put("msg",getString("you_are_not_a_vip_user"));

        }




        writeAndExit();
    }
}
