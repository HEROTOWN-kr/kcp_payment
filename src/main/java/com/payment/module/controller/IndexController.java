package com.payment.module.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    public String web_order() {
        return "sample/order";
    }

    @RequestMapping(value = "/pp_cli_hub", method = RequestMethod.POST)
    public String pp_cli_hub() {
        return "sample/pp_cli_hub";
    }

    @RequestMapping(value = "/result", method = RequestMethod.POST)
    public String result() {
        return "sample/result";
    }
}
