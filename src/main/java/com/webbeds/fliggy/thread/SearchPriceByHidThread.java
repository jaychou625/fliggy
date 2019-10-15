package com.webbeds.fliggy.thread;

import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.utils.Common;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SearchPriceByHidThread implements Runnable {

    public List<String> list;
    public Common common;
    public String mark;
    public CountDownLatch latch;

    public SearchPriceByHidThread(List<String> list, Common common, String mark, CountDownLatch latch){
        this.list = list;
        this.common = common;
        this.mark = mark;
        this.latch = latch;
    }
    @Override
    public void run() {
        Long start = new Date().getTime();
        try {
           if(mark.equals("first")){
                common.searchHotelPriceByHid(list,30);
            }else if(mark.equals("second")){
               common.searchHotelPriceByHidSecond(list);
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
