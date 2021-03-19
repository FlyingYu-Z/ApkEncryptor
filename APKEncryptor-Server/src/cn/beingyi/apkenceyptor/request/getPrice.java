package cn.beingyi.apkenceyptor.request;

import cn.beingyi.apkenceyptor.utils.Conf;
import cn.beingyi.apkenceyptor.utils.Confs;
import cn.beingyi.apkenceyptor.utils.UserThread;

public class getPrice extends BaseTask {
    public getPrice(UserThread userThread) throws Exception {
        super(userThread);

        Confs confs=new Confs();

        String MonthVipPrice=confs.getValue("MonthVipPrice");
        String SeasonVipPrice=confs.getValue("SeasonVipPrice");
        String YearVipPrice=confs.getValue("YearVipPrice");

        StringBuilder sb=new StringBuilder();
        sb.append("Price of key:\n");
        sb.append("Month Vip Price:$"+MonthVipPrice+"\n");
        sb.append("Season Vip Price:$"+SeasonVipPrice+"\n");
        sb.append("Year Vip Price:$"+YearVipPrice+"\n");


        confs.close();

        resultJSON.put("BuyKeyUrl","https://t.me/zxcv512");
        resultJSON.put("price",sb.toString());
        resultJSON.put("result",true);
        writeAndExit();
    }
}
