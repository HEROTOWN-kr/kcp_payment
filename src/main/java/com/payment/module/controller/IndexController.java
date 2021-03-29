package com.payment.module.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping(value = "/mobile_sample/order_mobile", method = RequestMethod.GET)
    public String mobile_order() {
        return "mobile_sample/order_mobile";
    }

    @RequestMapping(value = "/sample/order", method = RequestMethod.GET)
    public String web_order(@RequestParam Map<String,String> allParams, ModelMap model) {
        model.addAttribute("money", allParams.get("money"));
        model.addAttribute("plan", allParams.get("plan"));
        model.addAttribute("name", allParams.get("name"));
        model.addAttribute("email", allParams.get("email"));
        model.addAttribute("phone", allParams.get("phone"));
        return "sample/order";
    }
//@RequestParam(defaultValue = "1000") String money, Model model    model.addAttribute("money", money);

    @RequestMapping(value = "/pp_cli_hub", method = RequestMethod.POST)
    public String pp_cli_hub() {
        return "sample/pp_cli_hub";
    }

    @RequestMapping(value = "/result", method = RequestMethod.POST)
    public String result() {
        return "sample/result";
    }
}
