package com.webbeds.fliggy.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.controller.methodController;
import com.webbeds.fliggy.entity.Fliggy_roomType_info;
import com.webbeds.fliggy.service.DOTW.Fliggy_hotel_infoService;
import com.webbeds.fliggy.service.DOTW.Fliggy_roomTpye_infoService;
import com.webbeds.fliggy.utils.OutLookUtil;
import net.sf.json.xml.XMLSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class AutoUpdateSchedule {
    @Autowired
    methodController methodController;

    @Autowired
    Fliggy_roomTpye_infoService fliggy_roomTpye_infoService;

    @Autowired
    Fliggy_hotel_infoService fliggy_hotel_infoService;

    @Autowired
    OutLookUtil outLookUtil;

    //更新房型酒店信息（飞猪，周期7天）
    @Scheduled(fixedRate = 3600 * 1000 * 24 * 7)
    public void autoUpdateHotelRoomsTask() {
        /**
         * 更新本地库现有房型和酒店信息
         */
        System.out.println("更新本地酒店房型开始");
        methodController.updateHotelRooms();
        System.out.println("更新本地酒店房型结束");
        /**                                                                                                                                                                                                                                                                                                                             qqq
         * 统计房型发生变更的数量
         */
        List<Fliggy_roomType_info> listRoomType = fliggy_roomTpye_infoService.searchRoomByState("9");
        Integer changeNum = listRoomType.size();
        System.out.println("本周共有" + changeNum + "个房型发生变化");

        /**
         * 调用外部接口，获取近一月有价房型ID
         */
        //获取需要查价的酒店id
        List<String> hotelList = fliggy_hotel_infoService.findAllId();
        String roomIds = searchRoomInfo(hotelList);
        //根据获取到的有价房型id操作本地数据库，如果有价则将state改为9，重新推送
        List<String> roomIdList = Arrays.asList(roomIds.split(","));
        List<Fliggy_roomType_info> listFliggyRoom = fliggy_roomTpye_infoService.searchRoomByState("0");
        System.out.println("开始整合有价的房型");
        //本周新增有价房型
        Integer havePriceThisWeek = 0;
        for(Fliggy_roomType_info fliggy_roomType_info : listFliggyRoom){
            for(String roomid : roomIdList){
                //如果id相同说明有价则改变state为9
                if(fliggy_roomType_info.getOuter_id().equals(roomid)){
                    fliggy_roomType_info.setState("9");
                    havePriceThisWeek++;
                    fliggy_roomTpye_infoService.updateStateAndDate(fliggy_roomType_info);
                    System.out.println("房型：" + fliggy_roomType_info.getOuter_id() + "有价，需要重新推送");
                    break;
                }
            }
        }
        System.out.println("本周共新增有价房型：" + havePriceThisWeek + "个");
        System.out.println("整合有价的房型结束，等待床型整合");
        /**
         * 开始整合床型信息
         */
        System.out.println("开始整合床型信息");
        methodController.tempChangeBedType();
        System.out.println("整合床型信息完毕，等待推送");

        /**
         * 将改变的房型信息推送至飞猪端
         */
        System.out.println("开始推送房型信息");
        String updateNum = methodController.updateChangedRooms();
        System.out.println("推送房型信息完毕");
        System.out.println("共计成功推送：" + updateNum + "个房型");

        /**
         * 将结果发送邮件给目标
         */
        String toEmailAddress = "yun.liu@webbeds.com";
        String emailTitle = "Weekly_Fliggy_Data_Update(DOTW)";
        String emailContent = "Hi,all,\nbelow is weekly_fliggy_data_update report,for your reference.\n";
        emailContent += "There are " + changeNum + "pieces of room-type changed by account:AlitripXML in DOTW system this week.Total" + updateNum + "pieces of room-type pushed on fliggy system finaly\n";
        emailContent += "See you next week.";
        List<String> toEmailAddressList = new ArrayList<>();
        toEmailAddressList.add("yun.liu@webbeds.com");
        toEmailAddressList.add("joy.huang@webbeds.com");
        toEmailAddressList.add("Tracy.zhang@webbeds.com");
        sendReportByEmail(toEmailAddressList,emailTitle,emailContent);
    }

    /**
     * 向目的URL发送post请求
     *
     * @return String
     */
    public String searchRoomInfo(List<String> hotelId) {
        String account = "AlitripXML";
        String password = "CD84D683CC5612C69EFE115C80D0B7DC";
        String companyCode = "1494305";
        String version = "V3";
        /**
         * 520:USD
         * 2524:CNY
         */
        String currency = "2524";
        /**
         * 0,所有
         * 1，直连
         * 2，DI
         * 3,第三方
         */
        String rateType = "0";
        String url = "http://127.0.0.1:8081/searchRoomInfoByPrice_monthly_dotw?hotel_id=";
        /**
         * 拼接请求字段
         */
        System.out.println("酒店id个数：" + hotelId.size());
        String hotelIds = "";
        int tempIndex = 0;
        for(String str : hotelId){
            hotelIds += str + ";";
//            if(tempIndex < 100){
//                hotelIds += str + ";";
//                tempIndex++;
//            }

        }
        hotelIds = hotelIds.substring(0,hotelIds.length() - 1);
        url += hotelIds;
        //拼接account信息
        url += "&account=" + account + "&password=" + password + "&companyCode=" + companyCode + "&version=" + version + "&currency=" + currency + "&rateType=" + rateType;
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        RestTemplate client = new RestTemplate();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用String 类格式化
        long start = new Date().getTime();
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        long end = new Date().getTime();
        System.out.println("get请求用时：" + (end - start) + "ms");
        String result = response.getBody();
        return result;
    }

    public String sendReportByEmail(List<String> toEmailAddress,String emailTitle,String emailContent){
        String result = "";
//        String toEmailAddress = "yun.liu@webbeds.com";
//        String emailTitle = "飞猪房型更新";
//        String emailContent = "本周AlitripXML账号下共有142个房型名称发生改变\n" +
//                "本周共新增有价房型：848个\n" +
//                "共计成功推送房型：862个";
        try{
            //发送邮件
//            sendMailUtil.sendEmail(toEmailAddress, emailTitle, emailContent);
            outLookUtil.sendMail(toEmailAddress,emailTitle,emailContent);
            System.out.println("邮件发送成功");
            result = "推送成功";
        }catch(Exception e){
            result = "推送失败";
            System.out.println(e);
            System.out.println("邮件发送失败");
        }

        return result;
    }
}
