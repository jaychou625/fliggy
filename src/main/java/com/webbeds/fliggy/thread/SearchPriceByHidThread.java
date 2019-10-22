package com.webbeds.fliggy.thread;

import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.entity.search_dotw.Search_info;
import com.webbeds.fliggy.utils.Common;
import com.webbeds.fliggy.utils.searchUtils.SearchUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SearchPriceByHidThread implements Runnable {

    public List<String> list;
    public SearchUtils searchUtils;
    public String mark;
    public CountDownLatch latch;
    public Search_info search_info;

    public SearchPriceByHidThread(List<String> list, SearchUtils searchUtils, String mark, CountDownLatch latch, Search_info search_info){
        this.list = list;
        this.searchUtils = searchUtils;
        this.mark = mark;
        this.latch = latch;
        this.search_info = search_info;
    }
    @Override
    public void run() {
        Long start = new Date().getTime();
        try {
           if(mark.equals("first")){
               searchUtils.searchHotelPriceByHid(list,search_info);
            }else if(mark.equals("second")){
               searchUtils.searchHotelPriceByHidFullDay(list,search_info);
           }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            latch.countDown(); //这句是关键
//            System.out.println(latch.getCount());
            System.out.println("ok"); //线程都跑完后输出
        }
        Long end = new Date().getTime();
        System.out.println("线程" + Thread.currentThread().getId() + "执行完毕，单次消耗：" + (end - start) / 1000 + "S");
    }
}
