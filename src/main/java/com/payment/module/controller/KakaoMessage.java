package com.payment.module.controller;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class KakaoMessage {
    String name, phone, advertiserName, date, price, planName;

    public void set(String nameValue, String phoneValue, String advNameValue, String dateValue, String priceValue, String planNameValue) {
        name = nameValue;
        phone = phoneValue;
        advertiserName = advNameValue;
        date = dateValue;
        price = priceValue;
        planName = planNameValue;
    }

    public void sendMessage() {
        try{
            URL url = new URL("http://api.apistore.co.kr/kko/1.6/msg/herotown");
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("phone", phone);
            params.put("callback", "01023270875");
            params.put("msg", "안녕하세요. 인플라이입니다.\n\n"
                    + name + "님!\n"
                    + advertiserName + "님이\n"
                    + date + "에\n"
                    + price + "원짜리 " + planName + "플랜을 결제하고 구독했습니다.\n\n"
                    + "Admin 페이지에서 확인하시고, 승인해주세요.");
            params.put("template_code", "KM18");
            params.put("failed_type", "N");
            params.put("btn_types", "웹링크");
            params.put("btn_txts", "바로 가기");
            params.put("btn_urls1", "https://admin.inflai.com/Payment");
            params.put("btn_urls2", "https://admin.inflai.com/Payment");

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param: params.entrySet()) {
                if(postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            con.setRequestProperty("x-waple-authorization", "MTMwOTAtMTU5MTE2NTg4NjcyOC0xMmRiOGQzYi1mOTY0LTRiNTAtOWI4ZC0zYmY5NjQ3YjUwZjg=");
            con.setDoOutput(true);
            con.getOutputStream().write(postDataBytes);
            int responseCode = con.getResponseCode();
            System.out.println("Response code : " + responseCode);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
