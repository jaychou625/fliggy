package com.webbeds.fliggy.utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;

public class OutLookUtil {
    /**
     * @Title: 发送邮件工具类
     * @Description: 需要outlook邮箱账号密码
     * @param: to
     * @return: String
     * @throws
     */
    public String sendMail(List<String> to, String title, String content) {
        String from = "lei.pan19860625@outlook.com";// 发件人电子邮箱
        //获取系统属性，主要用于设置邮件相关的参数。
        Properties properties = System.getProperties();
        //设置邮件传输服务器，由于本次是发送邮件操作，所需我们需要配置smtp协议，按outlook官方同步邮件的要求，依次配置协议地址，端口号和加密方法
        properties.setProperty("mail.smtp.host", "smtp.office365.com");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        //用户验证并返回Session，开启用户验证，设置发送邮箱的账号密码。
        properties.setProperty("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("lei.pan19860625@outlook.com", "Panlei625");//账号密码
            }
        });

        //创建MimeMessage消息对象，消息头配置了收发邮箱的地址，消息体包含了邮件标题和邮件内容。接收者类型：TO代表直接发送，CC代表抄送，BCC代表秘密抄送。
        try {
            MimeMessage message = new MimeMessage(session);
//            message.addHeader("X-Mailer","Microsoft Outlook Express 6.00.2900.2869");
            message.setFrom(new InternetAddress(from));
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            Address[] addresses = new InternetAddress[to.size()];
            for(int i = 0; i < to.size(); i++){
                addresses[i] = new InternetAddress(to.get(i));
            }
            message.addRecipients(Message.RecipientType.TO,addresses);
            message.setSubject(title);
            message.setText(content);
		    Transport.send(message);
		    System.out.println("发送成功！");

//            // 1.创建复合消息体
//            Multipart multipart = new MimeMultipart();
//            // 2.添加附件
//            BodyPart filePart = new MimeBodyPart();
//            String filePath = "/data/ISC2019130.xlsx";
//            DataSource source = new FileDataSource(filePath);
//            filePart.setDataHandler(new DataHandler(source));
//            filePart.setFileName(source.getName());
//            multipart.addBodyPart(filePart);
//            // 3.添加文本内容
//            MimeBodyPart textPart = new MimeBodyPart();
//            textPart.setText("测试包含文本和附件的邮件！");
//            multipart.addBodyPart(textPart);
//            // 4.绑定消息对象
//            message.setContent(multipart);
//            // 5.发送邮件
//            Transport.send(message);
            return "success";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "failed";
        }

    }
}
