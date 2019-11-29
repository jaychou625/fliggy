package com.webbeds.fliggy.thread;

import com.alibaba.fastjson.JSONObject;
import com.webbeds.fliggy.entity.Fliggy_hotel_info;
import com.webbeds.fliggy.task.DotwHotelTask;
import com.webbeds.fliggy.utils.Common;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AddHotelsThread implements Runnable {

    public List<String> list;
    public Common common;
    public CountDownLatch latch;
    public DotwHotelTask dotwHotelTask;
    public List<JSONObject> listJSON;
    public String state;

    public AddHotelsThread(List<String> list, Common common, CountDownLatch latch,DotwHotelTask dotwHotelTask,List<JSONObject> listJSON,String state) {
        this.list = list;
        this.common = common;
        this.latch = latch;
        this.dotwHotelTask = dotwHotelTask;
        this.listJSON = listJSON;
        this.state = state;
    }

    @Override
    public void run() {
        Long start = new Date().getTime();
        try {
            dotwHotelTask.addHotelOperate(list,listJSON,state);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            latch.countDown(); //这句是关键
//            System.out.println(latch.getCount());
            System.out.println("ok"); //线程都跑完后输出
        }
        Long end = new Date().getTime();
        System.out.println("线程" + Thread.currentThread().getId() + "执行完毕，单次消耗：" + (end - start) / 1000 + "S");
    }
}
