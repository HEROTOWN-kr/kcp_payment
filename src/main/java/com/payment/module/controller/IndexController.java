package com.payment.module.controller;

import org.springframework.stereotype.Controller;
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

    @GetMapping(value = "/payment")
    public String payment() {
        return "payment";
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

    @RequestMapping(value = "/payment/order", method = RequestMethod.GET)
    public String payment_web_order(@RequestParam Map<String,String> allParams, ModelMap model) {
        model.addAttribute("money", allParams.get("money"));
        model.addAttribute("plan", allParams.get("plan"));
        model.addAttribute("name", allParams.get("name"));
        model.addAttribute("email", allParams.get("email"));
        model.addAttribute("phone", allParams.get("phone"));
        return "payment/order";
    }
//@RequestParam(defaultValue = "1000") String money, Model model    model.addAttribute("money", money);

    @RequestMapping(value = "/pp_cli_hub", method = RequestMethod.POST)
    public String pp_cli_hub() {
        return "sample/pp_cli_hub";
    }

    @RequestMapping(value = "/payment/pp_cli_hub", method = RequestMethod.POST)
    public String payment_pp_cli_hub() {
        return "payment/pp_cli_hub";
    }

    @RequestMapping(value = "/result", method = RequestMethod.POST)
    public String result() {
        return "sample/result";
    }

    @RequestMapping(value = "/payment/result", method = RequestMethod.POST)
    public String payment_result() {
        return "payment/result";
    }
}
