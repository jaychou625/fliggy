package com.webbeds.fliggy.controller;

import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.DOTW_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import com.webbeds.fliggy.service.DOTW.DOTW_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_roomTpye_infoService;
import com.webbeds.fliggy.task.DotwHotelTask;
import com.webbeds.fliggy.utils.Common;
import com.webbeds.fliggy.utils.Fliggy_interface_util;
import com.webbeds.fliggy.utils.OutLookUtil;
import com.webbeds.fliggy.utils.SendMailUtil;
import com.webbeds.fliggy.utils.searchUtils.SearchUtils;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提交方法控制器
 */
@RestController
@Slf4j
public class methodController {

    @Autowired
    Common common;

    @Autowired
    DOTW_hotel_infoService dotw_hotel_infoService;

    @Autowired
    Hotel_info_controller hotel_info_controller;

    @Autowired
    SearchUtils searchUtils;

    @Autowired
    Fliggy_interface_util fliggy_interface_util;

    @Autowired
    Fliggy_hotel_infoService fliggy_hotel_infoService;

    @Autowired
    Fliggy_roomTpye_infoService fliggy_roomTpye_infoService;

    @Autowired
    DotwHotelTask dotwHotelTask;

    @Autowired
    SendMailUtil sendMailUtil;

    @Autowired
    OutLookUtil outLookUtil;

    /**
     * 提交dotw酒店信息链接方法
     *
     * @param file
     * @return
     */
    @RequestMapping("/fileInputMethod")
    public ModelAndView fileInputMethod(@RequestParam("file") MultipartFile file) {
        boolean flag = false;
        ModelAndView modelAndView = new ModelAndView();
        //读取客户端上传的文件
        long startTime = System.currentTimeMillis();
        System.out.println("fileName：" + file.getOriginalFilename());
        String path = "D:/webbeds/temp/" + file.getOriginalFilename();

        File newFile = new File(path);
        //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
        try {
            file.transferTo(newFile);
            flag = true;
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("采用file.Transto的运行时间：" + String.valueOf(endTime - startTime) + "ms");

        try {
            List<DOTW_hotel_info> list = common.excel2Bean(path);
            for (DOTW_hotel_info dotw_hotel_info : list) {
                if (dotw_hotel_infoService.searchByHid(dotw_hotel_info.getHotelCode()) == 0) {
                    if (dotw_hotel_info.getReservationTelephone() == null) {
                        dotw_hotel_info.setReservationTelephone("00");
                    }
                    dotw_hotel_infoService.add(dotw_hotel_info);
                }
            }
            flag = true;
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        if (flag = true) {
            modelAndView.setViewName("/index");
        } else {
            modelAndView.setViewName("/insertError");
        }
        return modelAndView;
    }


    /**
     * 下载report
     *
     * @param fileName
     * @return
     */
    @RequestMapping("/downloadExcelReport")
    public String downloadExcelReport(String fileName, HttpServletRequest request, HttpServletResponse response) {

        String res = "";
        try {
            common.download(request, response);
            res = "success";
        } catch (IOException e) {
            res = "error";
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 点击调用dotw接口插入数据入本地库
     *
     * @param
     * @return
     */
    @RequestMapping("/addHotelInfo2LocalAnd2StepTwo")
    public ModelAndView addHotelInfo2LocalAnd2StepTwo() {
        ModelAndView modelAndView = new ModelAndView();
        hotel_info_controller.addHotelInLocalDatabase();
        modelAndView.setViewName("/addHotelInfo2LocalAnd2StepTwoSuccess");
        return modelAndView;
    }

    /**
     * 添加信息入飞猪本地库
     *
     * @param
     * @return
     */
    @RequestMapping("/updateHotelIntoFliggy")
    public ModelAndView updateHotelIntoFliggy() {
        ModelAndView modelAndView = new ModelAndView();
        hotel_info_controller.updateCity();
        hotel_info_controller.addHotelAndRoom();
        modelAndView.setViewName("/updateHotelIntoFliggySuccess");
        return modelAndView;
    }

    /**
     * 查询当前已更新所有的飞猪酒店和房型的匹配信息
     *
     * @param
     * @return
     */
    @RequestMapping("/searchFliggyHotelInfo")
    public ModelAndView searchFliggyHotelInfo() {
        ModelAndView modelAndView = new ModelAndView();
        hotel_info_controller.searchHotel();
        modelAndView.setViewName("/searchFliggyHotelInfoSuccess");
        return modelAndView;
    }

    /**
     * 酒店查价专用方法
     *
     * @param file
     * @return
     */
    @RequestMapping("/searchPriceByHid")
    public ModelAndView searchPriceByHid(@RequestParam("file") MultipartFile file, @RequestParam("account") String account, @RequestParam("password") String password, @RequestParam("accountId") String accountId, @RequestParam("version") String version, @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate) {
        boolean flag = false;
        ModelAndView modelAndView = new ModelAndView();
        flag = searchUtils.searchPriceByHidOprate(file, account, password, accountId, version, fromDate, toDate);
        if (flag) {
            modelAndView.addObject("account", account);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date());
            modelAndView.addObject("date", date);
            modelAndView.addObject("fromDate", fromDate);
            modelAndView.addObject("toDate", toDate);
            modelAndView.setViewName("downLoadPriceInfo");
        } else {
            modelAndView.setViewName("/insertError");
        }
        return modelAndView;
    }

    /**
     * 查询当前已更新所有的飞猪酒店和房型的匹配信息
     *
     * @param
     * @return
     */
    @RequestMapping("/tempAddHotel")
    public String tempAddHotel() {
        String hid = "1479648,2270745,2123465,1925255,911425,2283255,1602318,584445,2244655";
        String[] hids = hid.split(",");
        for (String id : hids) {
            Fliggy_hotel_info fliggy_hotel_info = fliggy_hotel_infoService.findHotelById(id);
            if (fliggy_hotel_info != null) {
                String result = fliggy_interface_util.xhotel_add(fliggy_hotel_info);
//                System.out.println(result);
            } else {
//                System.out.println("系统里没有这个酒店：" + id);
            }
        }
        return "";
    }

    /**
     * 增加房型至飞猪端
     *
     * @param
     * @return
     */
    @RequestMapping("/tempAddRooms")
    public String tempAddRooms() {
//        String hid = "2532775";
        List<Fliggy_roomType_info> fliggy_roomType_infos = fliggy_roomTpye_infoService.searchRoomByState("9");
        for (Fliggy_roomType_info fliggy_roomType_info : fliggy_roomType_infos) {
            if (fliggy_roomType_info != null) {
                String resRoom = fliggy_interface_util.xRoomType_add(fliggy_roomType_info);
                if (resRoom != null && resRoom.indexOf("xhotel_roomtype_add_response") != -1) {
                    System.out.println("房型添加成功!房型id:" + fliggy_roomType_info.getOuter_id());
                    fliggy_roomType_info.setInsertDate(new Date());
                    fliggy_roomType_info.setState("1");
                    fliggy_roomType_info.setError_msg("");
                    fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                } else {
//                            System.out.println("房型添加失败");
                    String error_msg = resRoom;
                    fliggy_roomType_info.setError_msg(error_msg);
                    fliggy_roomType_info.setState("2");
                    fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                }
            }
        }
//        String[] hids = hid.split(",");
//        for(String id : hids){
//            Fliggy_hotel_info fliggy_hotel_info = fliggy_hotel_infoService.findHotelById(id);
//            if(fliggy_hotel_info != null){
//                String result = fliggy_interface_util.xhotel_add(fliggy_hotel_info);
////                System.out.println(result);
//            }else{
////                System.out.println("系统里没有这个酒店：" + id);
//            }
//        }
        return "";
    }

    /**
     * 推送所有酒店
     *
     * @param
     * @return
     */
    @RequestMapping("/tempAddAllHotel")
    public String tempAddAllHotel() {
        List<Fliggy_hotel_info> list = fliggy_hotel_infoService.searchAllHotel();
        for (Fliggy_hotel_info fliggy_hotel_info : list) {
            if (fliggy_hotel_info != null) {
                String result = fliggy_interface_util.xhotel_add(fliggy_hotel_info);
                System.out.println(result);
//                System.out.println(result);
            } else {
//                System.out.println("系统里没有这个酒店：" + id);
            }
        }
        return "";
    }

    /**
     * 删除飞猪房型 慎用
     *
     * @param
     * @return
     */
    @RequestMapping("/delRoom_dotwebk")
    public String delRoom_FR() {
        //获取需要删除的房型信息
        //获取需要删除房型的信息
        List<Fliggy_roomType_info> list = fliggy_roomTpye_infoService.searchDelRoom();
        for (Fliggy_roomType_info fliggy_roomType_info_fr : list) {
            String msg = fliggy_interface_util.xRoomDel(fliggy_roomType_info_fr.getOuter_id());
            System.out.println(msg);
            fliggy_roomType_info_fr.setState("-1");
            fliggy_roomType_info_fr.setInsertDate(null);
            fliggy_roomType_info_fr.setError_msg(null);
            fliggy_roomType_info_fr.setError_msg(msg);
            fliggy_roomType_info_fr.setOuter_id(fliggy_roomType_info_fr.getOuter_id());
            fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info_fr);
        }
        return "";
    }

    /**
     * 改变飞猪床型
     *
     * @param
     * @return
     */
    @RequestMapping("/tempChangeBedType")
    public String tempChangeBedType() {
        System.out.println("开始");
        String result = "";
        List<Fliggy_roomType_info> fliggy_roomType_infos = fliggy_roomTpye_infoService.searchAllRomms();
        System.out.println("共有：" + fliggy_roomType_infos.size());
        for (Fliggy_roomType_info fliggy_roomType_info : fliggy_roomType_infos) {
            //逻辑判断结构化床型
            String roomName = fliggy_roomType_info.getName().toUpperCase();
            if (roomName.indexOf("SMOKING") != -1) {
                roomName = roomName.replace("SMOKING", "temp");
                System.out.println(roomName);
            }
            if (roomName.indexOf("DOUBLE") != -1 && roomName.indexOf("TWIN") != -1) {
                fliggy_roomType_info.setBed_type("1张大床/2张单人床");
            } else if (roomName.indexOf("KING") != -1 && roomName.indexOf("TWIN") != -1 && roomName.indexOf("SMOKING") == -1) {
                fliggy_roomType_info.setBed_type("1张特大床/2张单人床");
            } else if (roomName.indexOf("QUEEN") != -1 && roomName.indexOf("TWIN") != -1) {
                fliggy_roomType_info.setBed_type("1张大床/2张单人床");
            } else if (roomName.indexOf("KING") != -1 && roomName.indexOf("SMOKING") == -1) {
                if (roomName.indexOf("ONE KING") != -1 || roomName.indexOf("1 KING") != -1 || roomName.indexOf("1KING") != -1) {
                    fliggy_roomType_info.setBed_type("1张特大床");
                } else if (roomName.indexOf("TWO KING") != -1 || roomName.indexOf("2 KING") != -1 || roomName.indexOf("2KING") != -1) {
                    fliggy_roomType_info.setBed_type("2张特大床");
                } else {
                    fliggy_roomType_info.setBed_type("1张特大床");
                }
            } else if (roomName.indexOf("DOUBLE") != -1) {
                if (roomName.indexOf("ONE DOUBLE") != -1 || roomName.indexOf("1 DOUBLE") != -1 || roomName.indexOf("1DOUBLE") != -1) {
                    fliggy_roomType_info.setBed_type("1张大床");
                } else if (roomName.indexOf("TWO DOUBLE") != -1 || roomName.indexOf("2 DOUBLE") != -1 || roomName.indexOf("2DOUBLE") != -1) {
                    fliggy_roomType_info.setBed_type("2张大床");
                } else {
                    fliggy_roomType_info.setBed_type("1张大床");
                }
            } else if (roomName.indexOf("TWIN") != -1) {
                fliggy_roomType_info.setBed_type("2张单人床");
            } else if (roomName.indexOf("SINGLE") != -1) {
                if (roomName.indexOf("ONE SINGLE") != -1 || roomName.indexOf("1 SINGLE") != -1 || roomName.indexOf("1SINGLE") != -1) {
                    fliggy_roomType_info.setBed_type("1张单人床");
                } else if (roomName.indexOf("TWO SINGLE") != -1 || roomName.indexOf("2 SINGLE") != -1 || roomName.indexOf("2SINGLE") != -1) {
                    fliggy_roomType_info.setBed_type("2张单人床");
                } else {
                    fliggy_roomType_info.setBed_type("1张单人床");
                }
            } else if (roomName.indexOf("QUEEN") != -1) {
                if (roomName.indexOf("ONE QUEEN") != -1 || roomName.indexOf("1 QUEEN") != -1 || roomName.indexOf("1QUEEN") != -1) {
                    fliggy_roomType_info.setBed_type("1张大床");
                } else if (roomName.indexOf("TWO QUEEN") != -1 || roomName.indexOf("2 QUEEN") != -1 || roomName.indexOf("2QUEEN") != -1) {
                    fliggy_roomType_info.setBed_type("2张大床");
                } else {
                    fliggy_roomType_info.setBed_type("1张大床");
                }
            } else {
                fliggy_roomType_info.setBed_type("1张大床/2张单人床");
            }
            fliggy_roomType_info.setState("9");
            fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
        }
        System.out.println("运行结束");
        return result;
    }

    /**
     * 更新酒店房型本地库内容
     *
     * @param
     * @return
     */
    @RequestMapping("/updateHotelRooms")
    public String updateHotelRooms() {
        //新接口，调用酒店id，批处理提升效率
        Long start = new Date().getTime();
        List<String> list = fliggy_hotel_infoService.findAllId();
        System.out.println("共有" + list.size() + "条数据");
        List<JSONObject> listJSON = dotwHotelTask.oprateUpdateRoomsThread(list, "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        common.JSONToExcel(listJSON, "alitrip账户有信息的酒店" + date);
        Long end = new Date().getTime();
        System.out.println("执行完毕，共计消耗：" + (end - start) / 1000 + "S");
        return "完成";
    }

    /**
     * 增加更新的房型至飞猪端
     *
     * @param
     * @return
     */
    @RequestMapping("/updateChangedRooms")
    public String updateChangedRooms() {
//        String hid = "2532775";
        Integer successNum = 0;
        List<Fliggy_roomType_info> fliggy_roomType_infos = fliggy_roomTpye_infoService.searchRoomByState("9");
        for (Fliggy_roomType_info fliggy_roomType_info : fliggy_roomType_infos) {
            if (fliggy_roomType_info != null) {
                String resRoom = fliggy_interface_util.xRoomType_add(fliggy_roomType_info);
                if (resRoom != null && resRoom.indexOf("xhotel_roomtype_add_response") != -1) {
                    System.out.println("房型添加成功!房型id:" + fliggy_roomType_info.getOuter_id());
                    fliggy_roomType_info.setInsertDate(new Date());
                    fliggy_roomType_info.setState("1");
                    fliggy_roomType_info.setError_msg("");
                    fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                    successNum++;
                } else {
//                            System.out.println("房型添加失败");
                    String error_msg = resRoom;
                    fliggy_roomType_info.setError_msg(error_msg);
                    fliggy_roomType_info.setState("2");
                    fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                }
            }
        }
        return String.valueOf(successNum);
    }

    /**
     * 发送STMP邮件
     *
     * @param
     * @return
     */
    @RequestMapping("/sendSmtpEmail")
    public String sendSmtpEmail() {
        String result = "";
//        String toEmailAddress = "lei.pan@webbeds.com";
        String toEmailAddress = "56650666@qq.com";
        List<String> toEmailAddressList = new ArrayList<>();
        toEmailAddressList.add("lei.pan@webbeds.com");
        toEmailAddressList.add("jaychou19860625@163.com");
        String emailTitle = "飞猪房型更新";
        String emailContent = "本周AlitripXML账号下共有142个房型名称发生改变\n" +
                "本周共新增有价房型：848个\n" +
                "共计成功推送房型：862个";
        try{
            //发送邮件
//            sendMailUtil.sendEmail(toEmailAddress, emailTitle, emailContent);
            outLookUtil.sendMail(toEmailAddressList,emailTitle,emailContent);
            System.out.println("邮件发送成功");
            result = "推送成功";
        }catch(Exception e){
            result = "推送失败";
            System.out.println(e);
            System.out.println("邮件发送失败");
        }

        return result;
    }

    /**
     * 发送Exchange邮件
     * @return
     */
    @RequestMapping("/sendExchangeMail")
    public boolean sendExchangeMail(){
        boolean isOK=false;
        String host = "https://outlook.office365.com/owa/webbeds.com/";
        String username = "Lei.Pan@webbeds.com";
        String password = "Panlei625";
        String subject = "测试邮件";
        String content = "本周AlitripXML账号下共有142个房型名称发生变化\n" +
                "本周共新增有价房型：848个\n" +
                "共计成功推送：862个房型";
        String to = "Jensen.Wang@webbeds.com";
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
        ExchangeCredentials credentials = new WebCredentials(username,password);
        service.setCredentials(credentials);
        try {
            service.setUrl(new URI(host));
            EmailMessage msg = new EmailMessage(service);
            msg.setSubject(subject);
            MessageBody body = MessageBody.getMessageBodyFromText(content);
            body.setBodyType(BodyType.HTML);
            msg.setBody(body);
            //支持多个收件人
            InternetAddress[] addresses = InternetAddress.parse(to);
            for (InternetAddress address : addresses) {
                msg.getToRecipients().add(address.getAddress());
            }
            msg.send();
            System.out.println("发送成功");
            isOK=true;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("发送失败");
            isOK= false;
        }
        return isOK;

    }
}
