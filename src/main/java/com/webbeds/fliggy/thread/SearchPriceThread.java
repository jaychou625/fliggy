package com.webbeds.fliggy.thread;

import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.utils.Common;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SearchPriceThread implements Runnable {

    public List<Fliggy_hotel_info> list;
    public Common common;
    public String mark;
    public CountDownLatch latch;

    public SearchPriceThread(List<Fliggy_hotel_info> list,Common common,String mark,CountDownLatch latch){
        this.list = list;
        this.common = common;
        this.mark = mark;
        this.latch = latch;
    }
    @Override
    public void run() {
        Long start = new Date().getTime();
        try {
            if(mark.equals("second")){
//                for(Fliggy_hotel_info fliggy_hotel_info : list){
//                    common.searchHotelPriceAgain(list);
//                }
                common.searchHotelPriceAgain(list);
            }else if(mark.equals("first")){
//                for(Fliggy_hotel_info fliggy_hotel_info : list){
//                    common.searchHotelPrice(list);
//                }
//                common.searchHotelPrice(list);
                common.searchHotelPriceTemp(list,30);
            }else if(mark.equals("addHotel2Fliggy")){
//                for(Fliggy_hotel_info fliggy_hotel_info : list){
//                    common.add2Fliggy(list);
//                }
                common.add2Fliggy(list);
            }else if(mark.equals("updateCity")){
//                for(Fliggy_hotel_info fliggy_hotel_info : list){
//                    common.updateCityId(list);
//                }
                common.updateCityId(list);
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
