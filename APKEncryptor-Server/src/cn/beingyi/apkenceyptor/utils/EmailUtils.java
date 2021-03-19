package cn.beingyi.apkenceyptor.utils;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailUtils {

    public EmailUtils() {

    }

    public void sendEmail(String destEmail, String title, String content) throws Exception {
        String host = "smtp.exmail.qq.com";
        String sendUser = "";
        String sendPassWord = "";

        Properties props = new Properties();
        props.setProperty("mail.debug", "true");    // 开启debug调试
        props.setProperty("mail.smtp.auth", "true");    // 发送服务器需要身份验证
        props.setProperty("mail.host", host);        // 设置邮件服务器主机名
        props.setProperty("mail.transport.protocol", "smtp");    // 发送邮件协议名称
        String smtpPort = "465";
        props.setProperty("mail.smtp.port", smtpPort);//端口号
        props.setProperty("mail.smtp.ssl.enable", "true");//加认证机制*/

        // 设置环境信息
        Session session = Session.getInstance(props);
        //session.setDebug(true); //开启debug调试
        Message msg = new MimeMessage(session);
        msg.setSubject(title);
        msg.setText(content);
        msg.setSentDate(new Date());
        // 设置发件人
        msg.setFrom(new InternetAddress(sendUser));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(destEmail));
        msg.saveChanges();
        Transport transport = session.getTransport("smtp");
        // 连接邮件服务器
        transport.connect(host, sendUser, sendPassWord);
        //transport.sendMessage(msg, new Address[] {new InternetAddress(toUser)});
        transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));// 发送邮件
        transport.close();// 关闭连接
    }




}
