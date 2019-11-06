package com.webbeds.fliggy.controller.webController;

import com.webbeds.fliggy.controller.Hotel_info_controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 飞猪项目页面路由控制器
 */
@Controller
public class Common_web_controller {


    /**
     * 提交数据主页面
     *
     * @param model
     * @return
     */
    @GetMapping("/searchPrice")
    public String main(Model model) {
        List<String> list = new ArrayList<>();
        list.add("alitrip");
        list.add("dida");
        list.add("alading");
        model.addAttribute("agentList", list);
        return "searchPrice";
    }
}
