package com.webbeds.fliggy.utils.searchUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.webbeds.fliggy.entity.search_dotw.Have_price_hotel;
import com.webbeds.fliggy.entity.search_dotw.Room_rate;
import com.webbeds.fliggy.entity.search_dotw.Search_info;
import com.webbeds.fliggy.service.DOTW.DOTW_hotel_infoService;
import com.webbeds.fliggy.thread.SearchPriceByHidThread;
import com.webbeds.fliggy.utils.Common;
import com.webbeds.fliggy.utils.DOTW_interface_util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * 通用工具类
 */
@Slf4j
public class SearchUtils {

    @Autowired
    DOTW_interface_util dotw_interface_util;

    @Autowired
    DOTW_hotel_infoService dotw_hotel_infoService;

   @Autowired
   Common common;

    Map<String,String> mealMap = new HashMap<>();
    //增加餐点信息，V4专用
    public void addMealMap(){
        mealMap.put("-1","Best Available");
        mealMap.put("0","Room Only");
        mealMap.put("1","All Rates");
        mealMap.put("1331","Breakfast");
        mealMap.put("15064","Do Not Use - Self Catering");
        mealMap.put("1334","Half Board");
        mealMap.put("1336","All Inclusive");
    }
    //根据当前时间获取若干天后是几号
    public String dateFormat(int day,String fromDate){
        //获取当前日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = null;
        try {
            today = sdf.parse(fromDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String endDate = sdf.format(today);//当前日期
        //获取三十天前日期
        Calendar theCa = Calendar.getInstance();
        theCa.setTime(today);
        theCa.add(theCa.DATE, day);//最后一个数字30可改，30天的意思
        Date start = theCa.getTime();
        String startDate = sdf.format(start);//三十天之前日期
        return startDate;
    }

    /**
     * 查询价格处理函数
     * @param file
     * @return
     */
    public boolean searchPriceByHidOprate(MultipartFile file,String account,String password,String accountId,String version,String fromDate,String toDate){
        addMealMap();
        Search_info search_info = new Search_info();
        //获得有价无价酒店集合，用于输出
        search_info.setHavePriceHotelJustOneDay(new ArrayList<>());
        search_info.setNoPriceHotelJustOneDay(new ArrayList<>());
        search_info.setHavePriceHotel(new ArrayList<>());
        search_info.setNoPriceHid(new ArrayList<>());
        search_info.setNoPriceHotel(new ArrayList<>());
        search_info.setHavePriceHotelFullDay(new ArrayList<>());
        search_info.setNoPriceHotelFullDay(new ArrayList<>());
        search_info.setMark(new ArrayList<>());
        search_info.setAccount(account);
        search_info.setPassword(password);
        search_info.setAccountId(accountId);
        search_info.setPath("http://us.DOTWconnect.com/gateway" + version + ".dotw");
        search_info.setVersion(version);
        search_info.setFromDate(fromDate);
        search_info.setToDate(toDate);
        boolean flag = false;
        //读取客户端上传的文件
        long  startTime=System.currentTimeMillis();
        String path="D:/webbeds/temp/"+file.getOriginalFilename();
        List<String> list = new ArrayList<>();
        File newFile=new File(path);
        //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
        try {
            file.transferTo(newFile);
            flag = true;
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }
        long  endTime=System.currentTimeMillis();
        System.out.println("采用file.Transto的运行时间："+String.valueOf(endTime-startTime)+"ms");

        try {
            //根据上传的excel文件获取酒店ID的list
            list = common.excel2String(path);
            System.out.println("共计" + list.size() + "条数据");
            //多线程初次询价
            List<List<String>> listThread = new ArrayList<>();
            if(list.size() > 29){
                listThread = common.splitListString(list,list.size() / 3);
            }else{
                listThread.add(list);
            }
            CountDownLatch latch = new CountDownLatch(listThread.size());
            //多线程询价，分段分天查询
            for(List<String> listTemp : listThread){
                SearchPriceByHidThread searchPriceByHidThread = new SearchPriceByHidThread(listTemp,this,"first",latch,search_info);
                Thread t = new Thread(searchPriceByHidThread);
                t.start();
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("第一次分段询价执行询价结束");

            CountDownLatch latchFullDay = new CountDownLatch(listThread.size());
            //多线程询价，连住查询
            for(List<String> listTemp : listThread){
                SearchPriceByHidThread searchPriceByHidThread = new SearchPriceByHidThread(listTemp,this,"second",latchFullDay,search_info);
                Thread t = new Thread(searchPriceByHidThread);
                t.start();
            }

            try {
                latchFullDay.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("第二次连查询价执行询价结束");

            flag = true;
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        //询价完毕后，删除对应的无连住房的内容
        for(String str : search_info.getMark()){
//            System.out.println("未能形成连住的酒店id：" + str);
            for (int i = 0; i < search_info.getHavePriceHotel().size(); i++){
                JSONObject obj = search_info.getHavePriceHotel().get(i);
                if(str.equals(obj.getString("hotel_id"))){
                    search_info.getHavePriceHotel().remove(obj);
                }
            }
        }
        //为没有一天有价的列表添加内容
        for(String str : list){
            boolean noPriceJustOneDayMark = true;
            for(JSONObject obj : search_info.getHavePriceHotelJustOneDay()){
                if(str.equals(obj.getString("hotel_id"))){
                    noPriceJustOneDayMark = false;
                    break;
                }
            }
            if(noPriceJustOneDayMark){
                JSONObject temp = JSON.parseObject("{\"hotel_id\":" + str + "}");
                search_info.getNoPriceHotelJustOneDay().add(temp);
            }
        }

        //根据list生成report
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        String fileName = "havePriceHotelJustOneDayAccount--" + account + "--searchDate--" + date + "--From-" + fromDate + "To-" + toDate;
        common.JSONToExcel(search_info.getHavePriceHotelJustOneDay(),fileName);
        fileName = "noPriceHotelJustOneDayAccount--" + account + "--searchDate--" + date + "--From-" + fromDate + "To-" + toDate;
        common.JSONToExcel(search_info.getNoPriceHotelJustOneDay(),fileName);
        fileName = "havePriceHotelPerDayAccount--" + account + "--searchDate--" + date + "--From-" + fromDate + "To-" + toDate;
        common.JSONToExcel(search_info.getHavePriceHotel(),fileName);
        fileName = "noPriceHotelPerDayAccount--" + account + "--searchDate--" + date + "--From-" + fromDate + "To-" + toDate;
        common.JSONToExcel(search_info.getNoPriceHotel(),fileName);
        fileName = "havePriceHotelEvenDayAccount--" + account + "--searchDate--" + date + "--From-" + fromDate + "To-" + toDate;
        common.JSONToExcel(search_info.getHavePriceHotelFullDay(),fileName);
        fileName = "noPriceHotelEvenDayAccount--" + account + "--searchDate--" + date + "--From-" + fromDate + "To-" + toDate;
        common.JSONToExcel(search_info.getNoPriceHotelFullDay(),fileName);
        return flag;
    }

    //根据Hid有价查询方法(分天查)
    public void searchHotelPriceByHid(List<String> list,Search_info search_info){
        List<List<String>> listThread = new ArrayList<>();
        //单次请求dotw数量控制，目前是100个id一次查询
        if(list.size() > 1){
            int length = list.size() >= 100 ? 100 : list.size();
            listThread = common.splitListString(list,length);
        }else{
            listThread.add(list);
        }
        int count = 0;
        for(int i = 0; i < listThread.size(); i++){
            searchPerDay(listThread.get(i),new ArrayList<>(),search_info,0);
        }

    }

    //根据Hid有价查询方法(查连住)
    public void searchHotelPriceByHidFullDay(List<String> list,Search_info search_info){
        List<List<String>> listThread = new ArrayList<>();
        //单次请求dotw数量控制，目前是100个id一次查询
        if(list.size() > 1){
            int length = list.size() >= 100 ? 100 : list.size();
            listThread = common.splitListString(list,length);
        }else{
            listThread.add(list);
        }
        int count = 0;
        for(int i = 0; i < listThread.size(); i++){
            searchEvenDay(listThread.get(i),search_info);
        }

    }

    //分段询价方法，可递归(根据Hid),
    public void firstSearchPriceByHidMethod(List<String> list,List<String> listHavePrice,Search_info search_info,Integer day){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = df.parse(search_info.getToDate());
            d2 = df.parse(search_info.getFromDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = d1.getTime() - d2.getTime();//这样得到的差值是毫秒级别
        long days = diff / (1000 * 60 * 60 * 24);
        int count = 0;
        String fromDate = "";
        String toDate = "";
        JSONObject jsonObject = null;
        //查询30天是否有价
        fromDate = dateFormat(day,search_info.getFromDate());
        toDate = dateFormat(day + 1,search_info.getFromDate());
        jsonObject = dotw_interface_util.getPriceInDotwByHotelIdAndAccount(list,fromDate,toDate,search_info.getAccount(),search_info.getPassword(),search_info.getAccountId(),search_info.getPath());

        if(search_info.getVersion().equals("V3")){
            count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
            if(count > 0){
                //如果查询条目=1只有一条，>1则有多条，一条用object多条用array
                if(count == 1){
                    //找到有价酒店并添加入库
                    JSONObject jsonObjectTemp = jsonObject.getJSONObject("hotels").getJSONObject("hotel");
                    //
                    search_info.getHavePriceHotel().add(jsonObjectTemp);
//                    //在集合中删除有价酒店并继续后续询价
                    for(int i = 0; i < list.size(); i++){
                        boolean flag = true;
                        for(int j = 0; j < listHavePrice.size(); j++){
                            if(list.get(i).equals(listHavePrice.get(j))){
                                flag = false;
                                break;
                            }
                        }
                        if(list.get(i).equals(jsonObjectTemp.getString("@hotelid")) && flag){
//                            log.warn("酒店编号：" + list.get(i) + "在第" + day + "天有价格");
                            listHavePrice.add(list.get(i));
//                        fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_info);
                        }
                    }
                }else{
                    JSONArray jsonArray = jsonObject.getJSONObject("hotels").getJSONArray("hotel");
                    for(int i = 0; i < jsonArray.size(); i++){
                        JSONObject jsonObjectTemp = jsonArray.getJSONObject(i);
                        search_info.getHavePriceHotel().add(jsonObjectTemp);
//                        //在集合中删除有价酒店并继续后续询价
                        for(int j = 0; j < list.size(); j++){
                            boolean flag = true;
                            for(int k = 0; k < listHavePrice.size(); k++){
                                if(list.get(j).equals(listHavePrice.get(k))){
                                    flag = false;
                                    break;
                                }
                            }
                            if(list.get(j).equals(jsonObjectTemp.getString("@hotelid")) && flag){
//                                log.warn("酒店编号：" + list.get(i) + "在第" + day + "天有价格");
                                listHavePrice.add(list.get(j));
                            }
                        }
                    }
                }
            }

            Integer dayTemp = day + 1;
            //删选出无价酒店列表继续查询60天是否有价,递归查询
            if(dayTemp < days){
                firstSearchPriceByHidMethod(list,listHavePrice,search_info,dayTemp);
            }else{
                for(String fliggy_hotel_infoTemp : list){
                    boolean flag = true;
                    for(String fliggy_hotel_infoTempHavePrice : listHavePrice){
                        if(fliggy_hotel_infoTempHavePrice.equals(fliggy_hotel_infoTemp)){
                            flag = false;
                            break;
                        }
                    }
                    if(flag){
//                        log.warn("酒店编号：" + fliggy_hotel_infoTemp + "第一轮询价完毕未找到有效价格");
//                    JSONObject jsonObject1 = JSON.parseObject("{\"hotelid\":" + fliggy_hotel_infoTemp + "}");
//                    noPriceHotel.add(jsonObject1);
                        if(!fliggy_hotel_infoTemp.equals("")){
                            JSONObject noPriceHotel = JSON.parseObject("{\"hotel_id\":" + fliggy_hotel_infoTemp + "}");
                            search_info.getNoPriceHotel().add(noPriceHotel);
                        }
//                   fliggy_hotel_infoService.updateHavePrice(fliggy_hotel_infoTemp);
                    }
                }
            }
        }
    }

    /**
     * 连续询价方法，非递归(根据Hid),
     * @param list
     * @param search_info
     */
    public void searchEvenDay(List<String> list,Search_info search_info){
        int count = 0;
        JSONObject jsonObject = null;

        jsonObject = dotw_interface_util.getPriceInDotwByHotelIdAndAccount(list,search_info.getFromDate(),search_info.getToDate(),search_info.getAccount(),search_info.getPassword(),search_info.getAccountId(),search_info.getPath());

        if(search_info.getVersion().equals("V3")){
            count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
            if(count > 0){
                //如果查询条目=1只有一条，>1则有多条，一条用object多条用array
                if(count == 1){
                    //找到有价酒店并添加入库
                    JSONObject jsonObjectTempFullDay = jsonObject.getJSONObject("hotels").getJSONObject("hotel");
                    Have_price_hotel have_price_hotelFullDay = getBeanByJSON(jsonObjectTempFullDay,search_info.getVersion(),search_info,search_info.getFromDate(),search_info.getToDate());
                    JSONObject have_price_hotelJSONFullDays = JSON.parseObject(JSON.toJSONString(have_price_hotelFullDay));
                    search_info.getHavePriceHotelFullDay().add(have_price_hotelJSONFullDays);
                }else{
                    JSONArray jsonArray = jsonObject.getJSONObject("hotels").getJSONArray("hotel");
                    for(int i = 0; i < jsonArray.size(); i++){
                        //找到有价酒店并添加入库
                        Object jsonObjectTempFullDay = jsonObject.getJSONObject("hotels").get("hotel");
                        if(jsonObjectTempFullDay instanceof JSONObject){
                            JSONObject jsonObjectFullDay = (JSONObject) jsonObjectTempFullDay;
                            Have_price_hotel have_price_hotelFullDay = getBeanByJSON(jsonObjectFullDay,search_info.getVersion(),search_info,search_info.getFromDate(),search_info.getToDate());
                            JSONObject have_price_hotelJSONFullDays = JSON.parseObject(JSON.toJSONString(have_price_hotelFullDay));
                            //如果集合里数量大于零则需要判断保证值增加一次
                            boolean flag = true;
                            if(search_info.getHavePriceHotelFullDay().size() > 0){
                                for(int fullDayIndex = 0; fullDayIndex < search_info.getHavePriceHotelFullDay().size(); fullDayIndex++){
                                    if(have_price_hotelFullDay.getHotel_id().equals(search_info.getHavePriceHotelFullDay().get(fullDayIndex).getString("hotel_id"))){
                                        flag = false;
                                        break;
                                    }
                                }
                            }
                            if(flag){
                                search_info.getHavePriceHotelFullDay().add(have_price_hotelJSONFullDays);
                            }
                        }else if(jsonObjectTempFullDay instanceof JSONArray){
                            JSONArray jsonArrayFullDay = (JSONArray) jsonObjectTempFullDay;
                            for(Object obj : jsonArrayFullDay){
                                JSONObject jsonObjectFullDay = (JSONObject) obj;
                                Have_price_hotel have_price_hotelFullDay = getBeanByJSON(jsonObjectFullDay,search_info.getVersion(),search_info,search_info.getFromDate(),search_info.getToDate());
                                JSONObject have_price_hotelJSONFullDays = JSON.parseObject(JSON.toJSONString(have_price_hotelFullDay));
                                //如果集合里数量大于零则需要判断保证值增加一次
                                boolean flag = true;
                                if(search_info.getHavePriceHotelFullDay().size() > 0){
                                    for(int fullDayIndex = 0; fullDayIndex < search_info.getHavePriceHotelFullDay().size(); fullDayIndex++){
                                        if(have_price_hotelFullDay.getHotel_id().equals(search_info.getHavePriceHotelFullDay().get(fullDayIndex).getString("hotel_id"))){
                                            flag = false;
                                            break;
                                        }
                                    }
                                }
                                if(flag){
                                    search_info.getHavePriceHotelFullDay().add(have_price_hotelJSONFullDays);
                                }
                            }
                        }
                    }
                }
            }
        }else if(search_info.getVersion().equals("V4")){
            //V4有些许不同
            Object tempObj = jsonObject.get("hotels");
            if(tempObj instanceof JSONObject){
                JSONObject hotelObj = jsonObject.getJSONObject("hotels");
                //找到有价酒店并添加入库
                JSONObject jsonObjectTempFullDay = jsonObject.getJSONObject("hotels").getJSONObject("hotel");
                Have_price_hotel have_price_hotelFullDay = getBeanByJSON(jsonObjectTempFullDay,search_info.getVersion(),search_info,search_info.getFromDate(),search_info.getToDate());
                JSONObject have_price_hotelJSONFullDay = JSON.parseObject(JSON.toJSONString(have_price_hotelFullDay));
                search_info.getHavePriceHotelFullDay().add(have_price_hotelJSONFullDay);
            }else if(tempObj instanceof JSONArray){
                JSONArray jsonArray = jsonObject.getJSONArray("hotels");
                for(int i = 0; i < jsonArray.size(); i++){
                    //找到有价酒店并添加入库
                    Object jsonObjectTempFullDay = jsonArray.get(i);
                    if(jsonObjectTempFullDay instanceof JSONObject){
                        JSONObject jsonObjectFullDay = (JSONObject) jsonObjectTempFullDay;
                        Have_price_hotel have_price_hotelFullDay = getBeanByJSON(jsonObjectFullDay,search_info.getVersion(),search_info,search_info.getFromDate(),search_info.getToDate());
                        JSONObject have_price_hotelJSONFullDay = JSON.parseObject(JSON.toJSONString(have_price_hotelFullDay));
                        //如果集合里数量大于零则需要判断保证值增加一次
                        boolean flag = true;
                        if(search_info.getHavePriceHotelFullDay().size() > 0){
                            for(int fullDayIndex = 0; fullDayIndex < search_info.getHavePriceHotelFullDay().size(); fullDayIndex++){
                                if(have_price_hotelFullDay.getHotel_id().equals(search_info.getHavePriceHotelFullDay().get(fullDayIndex).getString("hotel_id"))){
                                    flag = false;
                                    break;
                                }
                            }
                        }
                        if(flag){
                            search_info.getHavePriceHotelFullDay().add(have_price_hotelJSONFullDay);
                        }
                    }else if(jsonObjectTempFullDay instanceof JSONArray){
                        JSONArray jsonArrayFullDay = (JSONArray) jsonObjectTempFullDay;
                        for(Object obj : jsonArrayFullDay){
                            JSONObject jsonObjectFullDay = (JSONObject)obj;
                            Have_price_hotel have_price_hotelFullDay = getBeanByJSON(jsonObjectFullDay,search_info.getVersion(),search_info,search_info.getFromDate(),search_info.getToDate());
                            JSONObject have_price_hotelJSONFullDay = JSON.parseObject(JSON.toJSONString(have_price_hotelFullDay));
                            //如果集合里数量大于零则需要判断保证值增加一次
                            boolean flag = true;
                            if(search_info.getHavePriceHotelFullDay().size() > 0){
                                for(int fullDayIndex = 0; fullDayIndex < search_info.getHavePriceHotelFullDay().size(); fullDayIndex++){
                                    if(have_price_hotelFullDay.getHotel_id().equals(search_info.getHavePriceHotelFullDay().get(fullDayIndex).getString("hotel_id"))){
                                        flag = false;
                                        break;
                                    }
                                }
                            }
                            if(flag){
                                search_info.getHavePriceHotelFullDay().add(have_price_hotelJSONFullDay);
                            }
                        }
                    }
                }

            }
        }

        //执行完毕后根据连住数量重新定义有房无房list
        for(int indexHavePrice = 0; indexHavePrice < list.size(); indexHavePrice++){
            boolean flag = true;
            boolean flagPerDay = true;
            //为查询无房的酒店列表添加内容
            for(int i = 0; i < search_info.getHavePriceHotelFullDay().size();i++){
                JSONObject tempJson = search_info.getHavePriceHotelFullDay().get(i);
                if(list.get(indexHavePrice).equals(tempJson.getString("hotel_id"))){
                    flag = false;
                    break;
                }
            }
            if(flag){
                String hotelId = list.get(indexHavePrice);
                JSONObject temp = JSON.parseObject("{\"hotel_id\":" + hotelId + "}");
                search_info.getNoPriceHotelFullDay().add(temp);
            }
//            for(int i = 0; i < search_info.getHavePriceHotel().size();i++){
//                JSONObject tempJson = search_info.getHavePriceHotel().get(i);
//                if(list.get(indexHavePrice).equals(tempJson.getString("hotel_id"))){
//                    flagPerDay = false;
//                    break;
//                }
//            }
//            if(flagPerDay){
//                boolean markNoPrice = true;
//                for(int i = 0; i < search_info.getNoPriceHotel().size();i++){
//                    JSONObject tempJson = search_info.getNoPriceHotel().get(i);
//                    if(list.get(indexHavePrice).equals(tempJson.getString("hotel_id"))){
//                        markNoPrice = false;
//                        break;
//                    }
//                }
//                if(!markNoPrice){
//                    continue;
//                }
//                String hotelId = list.get(indexHavePrice);
//                JSONObject temp = JSON.parseObject("{\"hotel_id\":" + hotelId + "}");
//                search_info.getNoPriceHotelFullDay().add(temp);
//            }
        }
    }

    /**
     * 分段询价方法，非递归(根据Hid),
     * @param list
     * @param listHavePrice
     * @param search_info
     * @param day
     */
    public void searchPerDay(List<String> list,List<String> listHavePrice,Search_info search_info,Integer day){
        //当前list有房天数集合
        List<Integer> listHavePriceDays = new ArrayList<>();
        //初始化
        for(String str : list){
            listHavePriceDays.add(0);
        }
        //获取共要查询多少天
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = df.parse(search_info.getToDate());
            d2 = df.parse(search_info.getFromDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = d1.getTime() - d2.getTime();//这样得到的差值是毫秒级别
        long days = diff / (1000 * 60 * 60 * 24);
//        System.out.println("共查询：" + days + "天");
        for(int index = 0; index < days; index++){
            int count = 0;
            String fromDate = "";
            String toDate = "";
            JSONObject jsonObject = null;
            fromDate = dateFormat(day + index,search_info.getFromDate());
            toDate = dateFormat(day + 1 + index,search_info.getFromDate());
            jsonObject = dotw_interface_util.getPriceInDotwByHotelIdAndAccount(list,fromDate,toDate,search_info.getAccount(),search_info.getPassword(),search_info.getAccountId(),search_info.getPath());

            if(search_info.getVersion().equals("V3")){
                count = Integer.valueOf(jsonObject.getJSONObject("hotels").getString("@count"));
                if(count > 0){
                    //如果查询条目=1只有一条，>1则有多条，一条用object多条用array
                    if(count == 1){
                        //找到有价酒店并添加入库
                        JSONObject jsonObjectTemp = jsonObject.getJSONObject("hotels").getJSONObject("hotel");
//                        search_info.getHavePriceHotel().add(jsonObjectTemp);
                        Have_price_hotel have_price_hotel = getBeanByJSON(jsonObjectTemp,search_info.getVersion(),search_info,fromDate,toDate);
                        JSONObject have_price_hotelJSON = JSON.parseObject(JSON.toJSONString(have_price_hotel));
                        search_info.getHavePriceHotel().add(have_price_hotelJSON);
                        //增加一步，增加连查有房天数
                        for(int indexList = 0; indexList < list.size(); indexList++){
                            if(list.get(indexList).equals(jsonObjectTemp.getString("@hotelid"))){
                                //有且只有一天房价的酒店id
                                if(listHavePriceDays.get(indexList) == 0){
                                    String hotelId = jsonObjectTemp.getString("@hotelid");
                                    JSONObject temp = JSON.parseObject("{\"hotel_id\":" + hotelId + "}");
                                    search_info.getHavePriceHotelJustOneDay().add(temp);
                                }
                                listHavePriceDays.set(indexList,listHavePriceDays.get(indexList) + 1);
                            }
                        }
                    }else{
                        JSONArray jsonArray = jsonObject.getJSONObject("hotels").getJSONArray("hotel");
                        for(int i = 0; i < jsonArray.size(); i++){
                            JSONObject jsonObjectTemp = jsonArray.getJSONObject(i);
//                            search_info.getHavePriceHotel().add(jsonObjectTemp);
                            Have_price_hotel have_price_hotel = getBeanByJSON(jsonObjectTemp,search_info.getVersion(),search_info,fromDate,toDate);
                            JSONObject have_price_hotelJSON = JSON.parseObject(JSON.toJSONString(have_price_hotel));
                            search_info.getHavePriceHotel().add(have_price_hotelJSON);
                            //增加一步，增加连查有房天数
                            for(int indexList = 0; indexList < list.size(); indexList++){
                                if(list.get(indexList).equals(jsonObjectTemp.getString("@hotelid"))){
                                    //有且只有一天房价的酒店id
                                    if(listHavePriceDays.get(indexList) == 0){
                                        String hotelId = jsonObjectTemp.getString("@hotelid");
                                        JSONObject temp = JSON.parseObject("{\"hotel_id\":" + hotelId + "}");
                                        search_info.getHavePriceHotelJustOneDay().add(temp);
                                    }
                                    listHavePriceDays.set(indexList,listHavePriceDays.get(indexList) + 1);
                                }
                            }
                        }
                    }
                }
            }else if(search_info.getVersion().equals("V4")){
                //V4有些许不同
                Object tempObj = jsonObject.get("hotels");
                if(tempObj instanceof JSONObject){
                    JSONObject hotelObj = jsonObject.getJSONObject("hotels");
                    //找到有价酒店并添加入库
                    JSONObject jsonObjectTemp = jsonObject.getJSONObject("hotels").getJSONObject("hotel");
//                    search_info.getHavePriceHotel().add(jsonObjectTemp);
                    Have_price_hotel have_price_hotel = getBeanByJSON(jsonObjectTemp,search_info.getVersion(),search_info,fromDate,toDate);
                    JSONObject have_price_hotelJSON = JSON.parseObject(JSON.toJSONString(have_price_hotel));
                    search_info.getHavePriceHotel().add(have_price_hotelJSON);
                    //增加一步，增加连查有房天数
                    for(int indexList = 0; indexList < list.size(); indexList++){
                        if(list.get(indexList).equals(jsonObjectTemp.getString("@hotelid"))){
                            //有且只有一天房价的酒店id
                            if(listHavePriceDays.get(indexList) == 0){
                                String hotelId = jsonObjectTemp.getString("@hotelid");
                                JSONObject temp = JSON.parseObject("{\"hotel_id\":" + hotelId + "}");
                                search_info.getHavePriceHotelJustOneDay().add(temp);
                            }
                            listHavePriceDays.set(indexList,listHavePriceDays.get(indexList) + 1);
                        }
                    }
                }else if(tempObj instanceof JSONArray){
                    JSONArray jsonArray = jsonObject.getJSONArray("hotels");
                    for(int i = 0; i < jsonArray.size(); i++){
                        JSONObject jsonObjectTemp = jsonArray.getJSONObject(i);
//                        search_info.getHavePriceHotel().add(jsonObjectTemp);
                        Have_price_hotel have_price_hotel = getBeanByJSON(jsonObjectTemp,search_info.getVersion(),search_info,fromDate,toDate);
                        JSONObject have_price_hotelJSON = JSON.parseObject(JSON.toJSONString(have_price_hotel));
                        search_info.getHavePriceHotel().add(have_price_hotelJSON);
                        //增加一步，增加连查有房天数
                        for(int indexList = 0; indexList < list.size(); indexList++){
                            if(list.get(indexList).equals(jsonObjectTemp.getString("@hotelid"))){
                                //有且只有一天房价的酒店id
                                if(listHavePriceDays.get(indexList) == 0){
                                    String hotelId = jsonObjectTemp.getString("@hotelid");
                                    JSONObject temp = JSON.parseObject("{\"hotel_id\":" + hotelId + "}");
                                    search_info.getHavePriceHotelJustOneDay().add(temp);
                                }
                                listHavePriceDays.set(indexList,listHavePriceDays.get(indexList) + 1);
                            }
                        }
                    }

                }
            }
        }

        //执行完毕后根据连住数量重新定义有房无房list
        for(int indexHavePriceDays = 0; indexHavePriceDays < listHavePriceDays.size(); indexHavePriceDays++){
            boolean flag = true;
            //为查询无房的酒店列表添加内容
//            for(int i = 0; i < search_info.getHavePriceHotelJustOneDay().size();i++){
//                JSONObject tempJson = search_info.getHavePriceHotelJustOneDay().get(i);
//                if(list.get(indexHavePriceDays).equals(tempJson.getString("hotel_id"))){
//                    flag = false;
//                    break;
//                }
//            }
//            if(flag){
//                String hotelId = list.get(indexHavePriceDays);
//                JSONObject temp = JSON.parseObject("{\"hotel_id\":" + hotelId + "}");
//                search_info.getNoPriceHotelJustOneDay().add(temp);
//            }

            //如果判断小于连住天数则获取到对应的hotelid去相应list里删除元素和增加元素
            if(listHavePriceDays.get(indexHavePriceDays) < days){
                String hotelId = list.get(indexHavePriceDays);
                //增加无房酒店列表
                JSONObject noPriceHotel = JSON.parseObject("{\"hotel_id\":" + hotelId + "}");
                search_info.getNoPriceHotel().add(noPriceHotel);
//                //删除原来有房列表信息里房屋不足天数的内容
//                Iterator<JSONObject> iterator = search_info.getHavePriceHotel().iterator();
//                while (iterator.hasNext()){
//                    JSONObject havePriceJSON = iterator.next();
//                    if(hotelId.equals(havePriceJSON.getString("hotel_id"))){
//                        iterator.remove();
//                    }
//                }
                //逻辑：先记录要删除的下标，然后最终再删除，防止：ConcurrentModificationException
//                List<Integer> mark = new ArrayList<>();
                for(int indexHavePriceList = 0; indexHavePriceList < search_info.getHavePriceHotel().size();indexHavePriceList++){
                    JSONObject havePriceJSON = search_info.getHavePriceHotel().get(indexHavePriceList);
                    if(hotelId.equals(havePriceJSON.getString("hotel_id"))){
                        search_info.getMark().add(hotelId);
//                        mark.add(indexHavePriceList);
//                        search_info.getHavePriceHotel().remove(havePriceJSON);
                    }
                }
//                for(int markIndex = 0; markIndex < mark.size(); markIndex++){
//                    search_info.getHavePriceHotel().remove(markIndex);
//                }
            }
        }
    }

    /**
     * 根据JSON对象返回对应的有价房信息实体类对象
     * @param jsonObject
     * @param version
     * @param search_info
     * @return
     */
    public Have_price_hotel getBeanByJSON(JSONObject jsonObject,String version,Search_info search_info,String fromDate,String toDate){
        Have_price_hotel have_price_hotel = new Have_price_hotel();
        if(version.equals("V3")){
            //设置hotelid
            have_price_hotel.setHotel_id(jsonObject.getString("@hotelid"));
            //设置hotelname
            have_price_hotel.setHotel_name(jsonObject.getString("hotelName"));
            //设置room系列内容，可能会有多个，逻辑较为复杂
            String roomId = "";
            String roomName = "";
            int countRoom = Integer.valueOf(jsonObject.getJSONObject("rooms").getString("@count"));
            //如果条目数大于1说明有多个合适的房型
            if(countRoom > 1){
                JSONArray roomJsonArr = jsonObject.getJSONObject("rooms").getJSONArray("room");
                //遍历循环执行同样操作
                for(Object object : roomJsonArr){
                    JSONObject roomJson = (JSONObject) object;
                    //房型相关信息录入操作
                    roomInfoOprate(roomJson,have_price_hotel,search_info,fromDate,toDate);
                }
            }else if(countRoom == 1){
                JSONObject roomJson = jsonObject.getJSONObject("rooms").getJSONObject("room");
                //房型相关信息录入操作
                roomInfoOprate(roomJson,have_price_hotel,search_info,fromDate,toDate);
            }
            have_price_hotel.setRoom_id(have_price_hotel.getRoom_id().substring(0,have_price_hotel.getRoom_id().lastIndexOf(",")));
            have_price_hotel.setRoom_name(have_price_hotel.getRoom_name().substring(0,have_price_hotel.getRoom_name().lastIndexOf(",")));
        }else if(version.equals("V4")){
            //设置hotelid
            have_price_hotel.setHotel_id(jsonObject.getString("@hotelid"));
            //设置hotelname
            String hotelName =dotw_hotel_infoService.searchFullHotelByHid(jsonObject.getString("@hotelid"));
            have_price_hotel.setHotel_name(hotelName);
            //设置room系列内容，可能会有多个，逻辑较为复杂
            String roomId = "";
            String roomName = "";
            Object room = jsonObject.get("rooms");
            //如果条目数大于1说明有多个合适的房型
            if(room instanceof JSONArray){
                JSONArray roomJsonArr = (JSONArray) room;
                //遍历循环执行同样操作
                for(Object object : roomJsonArr){
                    JSONObject roomJson = (JSONObject) object;
                    //房型相关信息录入操作
                    roomInfoOprate(roomJson,have_price_hotel,search_info,fromDate,toDate);
                }
            }else if(room instanceof JSONObject){
                JSONObject roomJson = (JSONObject) room;
                //房型相关信息录入操作
                roomInfoOprate(roomJson,have_price_hotel,search_info,fromDate,toDate);
            }
            have_price_hotel.setRoom_id(have_price_hotel.getRoom_id().substring(0,have_price_hotel.getRoom_id().lastIndexOf(",")));
            have_price_hotel.setRoom_name(have_price_hotel.getRoom_name().substring(0,have_price_hotel.getRoom_name().lastIndexOf(",")));
        }
        return have_price_hotel;
    }

    /**
     * 房间信息操作函数
     * @param roomJson
     * @param have_price_hotel
     * @param search_info
     */
    public void roomInfoOprate(JSONObject roomJson,Have_price_hotel have_price_hotel,Search_info search_info,String fromDate,String toDate){
        if(search_info.getVersion().equals("V3")){
            int countRoomType = Integer.valueOf(roomJson.getString("@count"));
            String roomId = have_price_hotel.getRoom_id();
            String roomName = have_price_hotel.getRoom_name();
            List<Room_rate> room_rates = new ArrayList<>();
            if(countRoomType > 1){
                JSONArray roomTypeJson = roomJson.getJSONArray("roomType");
                for(Object obj : roomTypeJson){
                    JSONObject roomTypeObj = (JSONObject) obj;
                    roomId = have_price_hotel.getRoom_id();
                    roomName = have_price_hotel.getRoom_name();
                    room_rates = new ArrayList<>();
                    setRoomInfo(roomId,roomName,roomTypeObj,have_price_hotel,room_rates,search_info,fromDate,toDate);
                }
            }else if(countRoomType == 1){
                JSONObject roomTypeObj = roomJson.getJSONObject("roomType");
                roomId = have_price_hotel.getRoom_id();
                roomName = have_price_hotel.getRoom_name();
                room_rates = new ArrayList<>();
                setRoomInfo(roomId,roomName,roomTypeObj,have_price_hotel,room_rates,search_info,fromDate,toDate);
            }

        }else if(search_info.getVersion().equals("V4")){
            Object roomType = roomJson.getJSONObject("room").get("roomType");
            String roomId = have_price_hotel.getRoom_id();
            String roomName = have_price_hotel.getRoom_name();
            List<Room_rate> room_rates = new ArrayList<>();
            if(roomType instanceof JSONArray){
                JSONArray roomTypeJson = (JSONArray) roomType;
                for(Object obj : roomTypeJson){
                    JSONObject roomTypeObj = (JSONObject) obj;
                    roomId = have_price_hotel.getRoom_id();
                    roomName = have_price_hotel.getRoom_name();
                    room_rates = new ArrayList<>();
                    setRoomInfo(roomId,roomName,roomTypeObj,have_price_hotel,room_rates,search_info,fromDate,toDate);
                }
            }else if(roomType instanceof JSONObject){
                JSONObject roomTypeObj = (JSONObject) roomType;
                roomId = have_price_hotel.getRoom_id();
                roomName = have_price_hotel.getRoom_name();
                room_rates = new ArrayList<>();
                setRoomInfo(roomId,roomName,roomTypeObj,have_price_hotel,room_rates,search_info,fromDate,toDate);
            }
        }
    }

    /**
     * 根据对应roomJSON设置相关rate信息
     * @param roomId
     * @param roomName
     * @param roomTypeObj
     * @param have_price_hotel
     * @param room_rates
     * @param search_info
     */
    public void setRoomInfo(String roomId,String roomName,JSONObject roomTypeObj,Have_price_hotel have_price_hotel,List<Room_rate> room_rates,Search_info search_info,String fromDate,String toDate){
        if(search_info.getVersion().equals("V3")){
            Map<String,List<Room_rate>> map = new HashMap<>();
            if(roomId != null){
                roomId += roomTypeObj.getString("@roomtypecode") + ",";
                roomName += roomTypeObj.getString("name") + ",";
                map = have_price_hotel.getRoom_rate();

                //获取rate对象
                Object rateBases = roomTypeObj.getJSONObject("rateBases").get("rateBasis");
                if(rateBases instanceof JSONArray){
                    JSONArray rateBasesArray = (JSONArray) rateBases;
                    for(Object obj : rateBasesArray){
                        JSONObject rateBasesObj = (JSONObject) obj;
                        Room_rate room_rate = new Room_rate();
                        room_rate.setMeal(rateBasesObj.getString("@description"));
                        room_rate.setPrice(rateBasesObj.getJSONObject("total").getString("formatted"));
                        //判断取消规则，V3的规则如果@count为4说明可免费取消，其他则不可
                        int count = Integer.valueOf(rateBasesObj.getJSONObject("cancellationRules").getString("@count"));
                        if(count == 4){
                            room_rate.setCancel_before(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("toDate") + "前可免费取消");
                            room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(1).getString("fromDate"));
                        }else if(count == 3){
                            room_rate.setCancel_before(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("toDate") + "前可免费取消");
                            room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(1).getString("fromDate"));
                        }else {
                            room_rate.setCancel_before("不可免费取消");
                            room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("fromDate"));
                        }

                        room_rates.add(room_rate);
                    }
                }else if(rateBases instanceof JSONObject){
                    JSONObject rateBasesObj = (JSONObject) rateBases;
                    Room_rate room_rate = new Room_rate();
                    room_rate.setMeal(rateBasesObj.getString("@description"));
                    room_rate.setPrice(rateBasesObj.getJSONObject("total").getString("formatted"));
                    //判断取消规则，V3的规则如果@count为4说明可免费取消，其他则不可
                    int count = Integer.valueOf(rateBasesObj.getJSONObject("cancellationRules").getString("@count"));
                    if(count == 4){
                        room_rate.setCancel_before(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("toDate") + "前可免费取消");
                        room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(1).getString("fromDate"));
                    }else if(count == 3){
                        room_rate.setCancel_before(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("toDate") + "前可免费取消");
                        room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(1).getString("fromDate"));
                    }else {
                        room_rate.setCancel_before("不可免费取消");
                        room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("fromDate"));
                    }

                    room_rates.add(room_rate);
                }
                //最终赋值
                map.put(roomTypeObj.getString("@roomtypecode"),room_rates);
                have_price_hotel.setRoom_id(roomId);
                have_price_hotel.setRoom_name(roomName);
                have_price_hotel.setRoom_rate(map);
                have_price_hotel.setCheck_in(fromDate);
                have_price_hotel.setCheck_out(toDate);
                have_price_hotel.setPromotion(roomTypeObj.getString("specials"));
            }else{
                roomId = roomTypeObj.getString("@roomtypecode") + ",";
                roomName = roomTypeObj.getString("name") + ",";
                //获取rate对象
                Object rateBases = roomTypeObj.getJSONObject("rateBases").get("rateBasis");
                if(rateBases instanceof JSONArray){
                    JSONArray rateBasesArray = (JSONArray) rateBases;
                    for(Object obj : rateBasesArray){
                        JSONObject rateBasesObj = (JSONObject) obj;
                        Room_rate room_rate = new Room_rate();
                        room_rate.setMeal(rateBasesObj.getString("@description"));
                        room_rate.setPrice(rateBasesObj.getJSONObject("total").getString("formatted"));
                        //判断取消规则，V3的规则如果@count为4说明可免费取消，其他则不可
                        int count = Integer.valueOf(rateBasesObj.getJSONObject("cancellationRules").getString("@count"));
                        if(count == 4){
                            room_rate.setCancel_before(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("toDate") + "前可免费取消");
                            room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(1).getString("fromDate"));
                        }else if(count == 3){
                            room_rate.setCancel_before(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("toDate") + "前可免费取消");
                            room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(1).getString("fromDate"));
                        }else {
                            room_rate.setCancel_before("不可免费取消");
                            room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("fromDate"));
                        }

                        room_rates.add(room_rate);
                    }
                }else if(rateBases instanceof JSONObject){
                    JSONObject rateBasesObj = (JSONObject) rateBases;
                    Room_rate room_rate = new Room_rate();
                    room_rate.setMeal(rateBasesObj.getString("@description"));
                    room_rate.setPrice(rateBasesObj.getJSONObject("total").getString("formatted"));
                    //判断取消规则，V3的规则如果@count为4说明可免费取消，其他则不可
                    int count = Integer.valueOf(rateBasesObj.getJSONObject("cancellationRules").getString("@count"));
                    if(count == 4){
                        room_rate.setCancel_before(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("toDate") + "前可免费取消");
                        room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(1).getString("fromDate"));
                    }else if(count == 3){
                        room_rate.setCancel_before(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("toDate") + "前可免费取消");
                        room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(1).getString("fromDate"));
                    }else {
                        room_rate.setCancel_before("不可免费取消");
                        room_rate.setCancel_after(rateBasesObj.getJSONObject("cancellationRules").getJSONArray("rule").getJSONObject(0).getString("fromDate"));
                    }

                    room_rates.add(room_rate);
                }

                //最终赋值
                map.put(roomTypeObj.getString("@roomtypecode"),room_rates);
                have_price_hotel.setRoom_id(roomId);
                have_price_hotel.setRoom_name(roomName);
                have_price_hotel.setRoom_rate(map);
                have_price_hotel.setCheck_in(fromDate);
                have_price_hotel.setCheck_out(toDate);
                have_price_hotel.setPromotion(roomTypeObj.getString("specials"));
            }
        }else if(search_info.getVersion().equals("V4")){
            Map<String,List<Room_rate>> map = new HashMap<>();
            if(roomId != null){
                roomId += roomTypeObj.getString("@roomtypecode") + ",";
                roomName += roomTypeObj.getString("name") + ",";
                map = have_price_hotel.getRoom_rate();

                //获取rate对象
                Object rateBases = roomTypeObj.get("rateBases");
                if(rateBases instanceof JSONArray){
                    JSONArray rateBasesArray = (JSONArray) rateBases;
                    for(Object obj : rateBasesArray){
                        JSONObject rateBasesObj = (JSONObject) obj;
                        Room_rate room_rate = new Room_rate();

                        //V4版本比较坑，mealName需要用id匹配
                        String mealName = rateBasesObj.getString("@id");
                        for(String key : mealMap.keySet()){
                            if(mealName.equals(key)){
                                mealName = mealMap.get(key);
                                break;
                            }
                        }
                        room_rate.setMeal(mealName);
                        room_rate.setPrice(rateBasesObj.getString("total"));
                        //判断取消规则，V4
                        Object cancelRule = rateBasesObj.get("cancellationRules");
                        String cancelDate = "";
                        if(cancelRule instanceof JSONArray){
                            JSONArray cancellation = (JSONArray) cancelRule;
                            cancelDate = cancellation.getJSONObject(0).getString("fromDate");
                        }else if(cancelRule instanceof JSONObject){
                            JSONObject cancellation = (JSONObject) cancelRule;
                            cancelDate = cancellation.getString("fromDate");
                        }
                        room_rate.setCancel_before(cancelDate + "前可免费取消");
                        room_rate.setCancel_after(cancelDate);

                        room_rates.add(room_rate);
                    }
                }else if(rateBases instanceof JSONObject){
                    JSONObject rateBasesObj = (JSONObject) rateBases;
                    Room_rate room_rate = new Room_rate();

                    //V4版本比较坑，mealName需要用id匹配
                    String mealName = rateBasesObj.getJSONObject("rateBasis").getString("@id");
                    for(String key : mealMap.keySet()){
                        if(mealName.equals(key)){
                            mealName = mealMap.get(key);
                            break;
                        }
                    }
                    room_rate.setMeal(mealName);
                    room_rate.setPrice(rateBasesObj.getJSONObject("rateBasis").getString("total"));
                    //判断取消规则，V4
                    Object cancelRule = rateBasesObj.getJSONObject("rateBasis").get("cancellationRules");
                    String cancelDate = "";
                    if(cancelRule instanceof JSONArray){
                        JSONArray cancellation = (JSONArray) cancelRule;
                        cancelDate = cancellation.getJSONObject(0).getString("fromDate");
                    }else if(cancelRule instanceof JSONObject){
                        JSONObject cancellation = (JSONObject) cancelRule;
                        cancelDate = cancellation.getString("fromDate");
                    }
                    room_rate.setCancel_before(cancelDate + "前可免费取消");
                    room_rate.setCancel_after(cancelDate);

                    room_rates.add(room_rate);
                }
                //最终赋值
                map.put(roomTypeObj.getString("@roomtypecode"),room_rates);
                have_price_hotel.setRoom_id(roomId);
                have_price_hotel.setRoom_name(roomName);
                have_price_hotel.setRoom_rate(map);
                have_price_hotel.setCheck_in(fromDate);
                have_price_hotel.setCheck_out(toDate);
                have_price_hotel.setPromotion(roomTypeObj.getString("specials"));
            }else{
                roomId = roomTypeObj.getString("@roomtypecode") + ",";
                roomName = roomTypeObj.getString("name") + ",";
                //获取rate对象
                Object rateBases = roomTypeObj.get("rateBases");
                if(rateBases instanceof JSONArray){
                    JSONArray rateBasesArray = (JSONArray) rateBases;
                    for(Object obj : rateBasesArray){
                        JSONObject rateBasesObj = (JSONObject) obj;
                        Room_rate room_rate = new Room_rate();

                        //V4版本比较坑，mealName需要用id匹配
                        String mealName = rateBasesObj.getString("@id");
                        for(String key : mealMap.keySet()){
                            if(mealName.equals(key)){
                                mealName = mealMap.get(key);
                                break;
                            }
                        }
                        room_rate.setMeal(mealName);
                        room_rate.setPrice(rateBasesObj.getString("total"));
                        //判断取消规则，V4
                        Object cancelRule = rateBasesObj.get("cancellationRules");
                        String cancelDate = "";
                        if(cancelRule instanceof JSONArray){
                            JSONArray cancellation = (JSONArray) cancelRule;
                            cancelDate = cancellation.getJSONObject(0).getString("fromDate");
                        }else if(cancelRule instanceof JSONObject){
                            JSONObject cancellation = (JSONObject) cancelRule;
                            cancelDate = cancellation.getString("fromDate");
                        }
                        room_rate.setCancel_before(cancelDate + "前可免费取消");
                        room_rate.setCancel_after(cancelDate);

                        room_rates.add(room_rate);
                    }
                }else if(rateBases instanceof JSONObject){
                    JSONObject rateBasesObj = (JSONObject) rateBases;
                    Room_rate room_rate = new Room_rate();

                    //V4版本比较坑，mealName需要用id匹配
                    String mealName = rateBasesObj.getJSONObject("rateBasis").getString("@id");
                    for(String key : mealMap.keySet()){
                        if(mealName.equals(key)){
                            mealName = mealMap.get(key);
                            break;
                        }
                    }
                    room_rate.setMeal(mealName);
                    room_rate.setPrice(rateBasesObj.getJSONObject("rateBasis").getString("total"));
                    //判断取消规则，V4
                    Object cancelRule = rateBasesObj.getJSONObject("rateBasis").get("cancellationRules");
                    String cancelDate = "";
                    if(cancelRule instanceof JSONArray){
                        JSONArray cancellation = (JSONArray) cancelRule;
                        cancelDate = cancellation.getJSONObject(0).getString("fromDate");
                    }else if(cancelRule instanceof JSONObject){
                        JSONObject cancellation = (JSONObject) cancelRule;
                        cancelDate = cancellation.getString("fromDate");
                    }
                    room_rate.setCancel_before(cancelDate + "前可免费取消");
                    room_rate.setCancel_after(cancelDate);

                    room_rates.add(room_rate);
                }

                //最终赋值
                map.put(roomTypeObj.getString("@roomtypecode"),room_rates);
                have_price_hotel.setRoom_id(roomId);
                have_price_hotel.setRoom_name(roomName);
                have_price_hotel.setRoom_rate(map);
                have_price_hotel.setCheck_in(fromDate);
                have_price_hotel.setCheck_out(toDate);
                have_price_hotel.setPromotion(roomTypeObj.getString("specials"));
            }
        }
    }
}
