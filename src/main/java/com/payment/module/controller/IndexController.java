package com.payment.module.controller;

import com.payment.module.model.TbAdvertiser;
import com.payment.module.model.TbPayment;
import com.payment.module.model.TbPlan;
import com.payment.module.model.TbSubscription;
import com.payment.module.repository.*;
import com.payment.module.service.AdvertiserService;
import com.payment.module.service.PaymentService;
import com.payment.module.service.PlanService;
import com.payment.module.service.SubscriptionService;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.kcp.*;

import javax.naming.Name;
import javax.servlet.http.HttpServletRequest;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.sql.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Controller
public class IndexController{
    public String f_get_parm( String val )
    {
        if ( val == null ) val = "";
        return  val;
    }

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PlanService planService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private AdvertiserService advertiserService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public String payment_order(@RequestParam Map<String,String> allParams, ModelMap model, HttpServletRequest request) {
        model.addAttribute("advId", allParams.get("advId"));
        model.addAttribute("planId", allParams.get("planId"));

        String PLN_ID = request.getParameter("planId");
        String ADV_ID = request.getParameter("advId");

        int planId = Integer.parseInt(PLN_ID);
        int advId = Integer.parseInt(ADV_ID);

        try {
            TbPlan MyPlan = planService.get(planId);
            String planName = MyPlan.getPlnName();
            int planMonth = MyPlan.getPlnMonth();
            int planPrice = MyPlan.getPlnPriceMonth();
            long finalPrice = Math.round(planMonth * planPrice * 1.1);

            model.addAttribute("money", Integer.toString(planPrice));
            model.addAttribute("plan", planName);


            TbAdvertiser Advertiser = advertiserService.get(advId);
            String ADV_NAME = Advertiser.getAdvName();
            String ADV_EMAIL = Advertiser.getAdvEmail();
            String ADV_TEL = Advertiser.getAdvTel();

            model.addAttribute("name", ADV_NAME);
            model.addAttribute("email", ADV_EMAIL);
            model.addAttribute("phone", ADV_TEL);

            /*LocalDate currentLocalDate = LocalDate.now();
            LocalDate finishLocalDate = LocalDate.now().plusMonths(planMonth);

            Date currentDate = Date.valueOf(currentLocalDate);
            Date finishDate = Date.valueOf(finishLocalDate);

            subscriptionRepository.updateSubscription(ADV_ID);
            TbSubscription subscription = new TbSubscription();
            subscription.setPlnId(planId);
            subscription.setAdvId(advId);
            subscription.setSubStartDt(currentDate);
            subscription.setSubEndDt(finishDate);
            subscription.setSubStatus("2");
            subscription.setSubActive("1");
            subscriptionService.save(subscription);*/


            return "payment/order";
        } catch (Exception e){
            e.printStackTrace();
            return "payment/order";
        }


    }

    @RequestMapping(value = "/pp_cli_hub", method = RequestMethod.POST)
    public String pp_cli_hub(
//            @RequestParam Map<String,Object> allParams
            @RequestParam(value = "advId", required = true) String advId,
            @RequestParam(value = "planId", required = true) String planId,
            @RequestParam(value = "req_tx", required = true) String request_req_tx,
            @RequestParam(value = "tran_cd", required = true) String request_tran_cd,
            @RequestParam(value = "ordr_idxx", required = true) String request_ordr_idxx,
            @RequestParam(value = "good_name", required = true) String request_good_name,
            @RequestParam(value = "enc_data", required = true) String enc_data,
            @RequestParam(value = "enc_info", required = true) String enc_info,
            @RequestParam(value = "good_mny", required = true) String request_good_mny,
            @RequestParam(value = "use_pay_method", required = true) String request_use_pay_method,
            @RequestParam(value = "shop_user_id", required = false) String request_shop_user_id,
            @RequestParam(value = "cash_yn", required = true) String request_cash_yn,
            @RequestParam(value = "cash_tr_code", required = true) String request_cash_tr_code,
            @RequestParam(value = "cash_id_info", required = true) String request_cash_id_info,
            @RequestParam(value = "tno", required = false) String request_tno,
            HttpServletRequest request,
            ModelMap model
    ) {

        /* ============================================================================== */
        /* =   02. ?????? ?????? ?????? ??????                                                  = */
        /* = -------------------------------------------------------------------------- = */
        String req_tx         = f_get_parm( request_req_tx ); // ?????? ??????
        String tran_cd        = f_get_parm( request_tran_cd ); // ?????? ??????

        int advertiserId = Integer.parseInt(advId);
        int planIdInt = Integer.parseInt(planId);

        /* = -------------------------------------------------------------------------- = */
        String cust_ip        = f_get_parm( request.getRemoteAddr()                  ); // ?????? IP
        String ordr_idxx      = f_get_parm( request_ordr_idxx ); // ????????? ????????????
        String good_name      = f_get_parm( request_good_name ); // ?????????
        /* = -------------------------------------------------------------------------- = */
        String res_cd         = "";                                                     // ????????????
        String res_msg        = "";                                                     // ?????? ?????????
        String tno            = f_get_parm( request_tno ); // KCP ?????? ?????? ??????
        /* = -------------------------------------------------------------------------- = */
        String buyr_name      = f_get_parm( request.getParameter( "buyr_name"      ) ); // ????????????
        String buyr_tel1      = f_get_parm( request.getParameter( "buyr_tel1"      ) ); // ????????? ????????????
        String buyr_tel2      = f_get_parm( request.getParameter( "buyr_tel2"      ) ); // ????????? ????????? ??????
        String buyr_mail      = f_get_parm( request.getParameter( "buyr_mail"      ) ); // ????????? E-mail ??????
        /* = -------------------------------------------------------------------------- = */
        String use_pay_method = f_get_parm( request_use_pay_method ); // ?????? ??????
        String bSucc          = "";                                                     // ?????? DB ?????? ?????? ??????
        /* = -------------------------------------------------------------------------- = */
        String app_time       = "";                                                     // ???????????? (?????? ?????? ?????? ??????)
        String amount         = "";                                                     // KCP ?????? ????????????
        String total_amount   = "0";                                                    // ??????????????? ??? ????????????
        String coupon_mny     = "";                                                     // ????????????
        /* = -------------------------------------------------------------------------- = */
        String card_cd        = "";                                                     // ???????????? ??????
        String card_name      = "";                                                     // ???????????? ???
        String app_no         = "";                                                     // ???????????? ????????????
        String noinf          = "";                                                     // ???????????? ????????? ??????
        String quota          = "";                                                     // ???????????? ????????????
        String partcanc_yn    = "";                                                     // ???????????? ????????????
        String card_bin_type_01 = "";                                                   // ????????????1
        String card_bin_type_02 = "";                                                   // ????????????2
        String card_mny       = "";                                                     // ??????????????????
        /* = -------------------------------------------------------------------------- = */
        String bank_name      = "";                                                     // ?????????
        String bank_code      = "";                                                     // ????????????
        String bk_mny         = "";                                                     // ????????????????????????
        /* = -------------------------------------------------------------------------- = */
        String bankname       = "";                                                     // ?????? ?????????
        String depositor      = "";                                                     // ?????? ?????? ????????? ??????
        String account        = "";                                                     // ?????? ?????? ??????
        String va_date        = "";                                                     // ???????????? ??????????????????
        /* = -------------------------------------------------------------------------- = */
        String pnt_issue      = "";                                                     // ?????? ???????????? ??????
        String pnt_amount     = "";                                                     // ???????????? or ????????????
        String pnt_app_time   = "";                                                     // ????????????
        String pnt_app_no     = "";                                                     // ????????????
        String add_pnt        = "";                                                     // ?????? ?????????
        String use_pnt        = "";                                                     // ???????????? ?????????
        String rsv_pnt        = "";                                                     // ??? ?????? ?????????
        /* = -------------------------------------------------------------------------- = */
        String commid         = "";                                                     // ???????????????
        String mobile_no      = "";                                                     // ???????????????
        /* = -------------------------------------------------------------------------- = */
        String shop_user_id   = f_get_parm( request_shop_user_id ); // ????????? ?????? ?????????
        String tk_van_code    = "";                                                     // ???????????????
        String tk_app_no      = "";                                                     // ????????????
        /* = -------------------------------------------------------------------------- = */
        String cash_yn        = f_get_parm( request_cash_yn ); // ?????? ????????? ?????? ??????
        String cash_authno    = "";                                                     // ?????? ????????? ?????? ??????
        String cash_tr_code   = f_get_parm( request_cash_tr_code ); // ?????? ????????? ?????? ??????
        String cash_id_info   = f_get_parm( request_cash_id_info ); // ?????? ????????? ?????? ??????
        String cash_no        = "";                                                     // ?????? ????????? ?????? ??????


        /* ============================================================================== */
        /* =   02. ?????? ?????? ?????? ?????? END
        /* ============================================================================== */


        /* ============================================================================== */
        /* =   03. ???????????? ?????? ??? ?????????(?????? ??????)                                   = */
        /* = -------------------------------------------------------------------------- = */
        /* =       ????????? ????????? ??????????????? ???????????? ????????? ?????????.                     = */
        /* = -------------------------------------------------------------------------- = */
        //Test pay
        /*String g_conf_gw_url    = "testpaygw.kcp.co.kr";
        String g_conf_gw_port   = "8090";        // ????????????(????????????)
        int    g_conf_tx_mode   = 0;             // ????????????
        String g_conf_log_dir   = "C:\\Tomcat\\apache-tomcat-8.5.64\\logs"; // LOG ???????????? ???????????? ??????
        String g_conf_site_cd   = "T0000";
        String g_conf_site_key  = "3grptw1.zW0GSo4PQdaGvsF__";
        String g_conf_log_level = "3";*/

        //Real pay
        String g_conf_gw_url    = "paygw.kcp.co.kr";
        String g_conf_log_dir   = "C:\\Tomcat\\apache-tomcat-8.5.64\\logs"; // LOG ???????????? ???????????? ??????
        String g_conf_site_cd   = "ABJXF";
        String g_conf_site_key  = "0YrV3xlFox8xxG-35Ldbi1x__";
        String g_conf_log_level = "3";
        String g_conf_gw_port   = "8090";        // ????????????(????????????)
        int    g_conf_tx_mode   = 0;             // ????????????



        J_PP_CLI_N c_PayPlus = new J_PP_CLI_N();

        c_PayPlus.mf_init( "", g_conf_gw_url, g_conf_gw_port, g_conf_tx_mode, g_conf_log_dir );
        c_PayPlus.mf_init_set();

        /* ============================================================================== */
        /* =   03. ???????????? ?????? ??? ????????? END                                          = */
        /* ============================================================================== */


        /* ============================================================================== */
        /* =   04. ?????? ?????? ?????? ??????                                                  = */
        /* = -------------------------------------------------------------------------- = */
        /* = -------------------------------------------------------------------------- = */
        /* =   04-1. ?????? ?????? ?????? ??????                                                = */
        /* = -------------------------------------------------------------------------- = */
        if (req_tx.equals("pay")) {
            c_PayPlus.mf_set_enc_data( f_get_parm( enc_data ),
                    f_get_parm( enc_info ) );
            String good_mny      = f_get_parm( request_good_mny );

            if(good_mny.trim().length() > 0)
            {
                int ordr_data_set_no;
                ordr_data_set_no = c_PayPlus.mf_add_set( "ordr_data" );
                c_PayPlus.mf_set_us( ordr_data_set_no, "ordr_mony", good_mny );
            }
        }; // ?????? ??????
        /* = -------------------------------------------------------------------------- = */
        /* =   04. ?????? ?????? ?????? ?????? END                                              = */
        /* = ========================================================================== = */


        /* = ========================================================================== = */
        /* =   05. ??????                                                                 = */
        /* = -------------------------------------------------------------------------- = */
        if ( tran_cd.length() > 0 )
        {
            c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, "", ordr_idxx, g_conf_log_level, "0" );
        }
        else
        {
            c_PayPlus.m_res_cd  = "9562";
            c_PayPlus.m_res_msg = "?????? ??????|tran_cd?????? ???????????? ???????????????.";
        }

        res_cd  = c_PayPlus.m_res_cd;  // ?????? ??????
        res_msg = c_PayPlus.m_res_msg; // ?????? ?????????
        /* = -------------------------------------------------------------------------- = */
        /* =   05. ?????? END                                                             = */
        /* ============================================================================== */

        /* ============================================================================== */
        /* =   06. ?????? ?????? ??? ??????                                                    = */
        /* = -------------------------------------------------------------------------- = */
        if ( req_tx.equals( "pay" ) )
        {
            if ( res_cd.equals( "0000" ) )
            {
                tno       = c_PayPlus.mf_get_res( "tno"       ); // KCP ?????? ?????? ??????
                amount    = c_PayPlus.mf_get_res( "amount"    ); // KCP ?????? ?????? ??????
                pnt_issue = c_PayPlus.mf_get_res( "pnt_issue" ); // ?????? ???????????? ??????
                coupon_mny = c_PayPlus.mf_get_res( "coupon_mny" ); // ????????????

                /* = -------------------------------------------------------------------------- = */
                /* =   06-1. ???????????? ?????? ?????? ??????                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "100000000000" ) )
                {
                    card_cd   = c_PayPlus.mf_get_res( "card_cd"   ); // ????????? ??????
                    card_name = c_PayPlus.mf_get_res( "card_name" ); // ????????? ???
                    app_time  = c_PayPlus.mf_get_res( "app_time"  ); // ????????????
                    app_no    = c_PayPlus.mf_get_res( "app_no"    ); // ????????????
                    noinf     = c_PayPlus.mf_get_res( "noinf"     ); // ????????? ??????
                    quota     = c_PayPlus.mf_get_res( "quota"     ); // ?????? ?????? ???
                    partcanc_yn = c_PayPlus.mf_get_res( "partcanc_yn"     ); // ???????????? ????????????
                    card_bin_type_01 = c_PayPlus.mf_get_res( "card_bin_type_01" ); // ????????????1
                    card_bin_type_02 = c_PayPlus.mf_get_res( "card_bin_type_02" ); // ????????????2
                    card_mny = c_PayPlus.mf_get_res( "card_mny" ); // ??????????????????

                    /* = -------------------------------------------------------------- = */
                    /* =   06-1.1. ????????????(?????????+????????????) ?????? ?????? ??????             = */
                    /* = -------------------------------------------------------------- = */
                    if ( pnt_issue.equals( "SCSK" ) || pnt_issue.equals( "SCWB" ) )
                    {
                        pnt_amount   = c_PayPlus.mf_get_res( "pnt_amount"   ); // ???????????? or ????????????
                        pnt_app_time = c_PayPlus.mf_get_res( "pnt_app_time" ); // ????????????
                        pnt_app_no   = c_PayPlus.mf_get_res( "pnt_app_no"   ); // ????????????
                        add_pnt      = c_PayPlus.mf_get_res( "add_pnt"      ); // ?????? ?????????
                        use_pnt      = c_PayPlus.mf_get_res( "use_pnt"      ); // ???????????? ?????????
                        rsv_pnt      = c_PayPlus.mf_get_res( "rsv_pnt"      ); // ??? ?????? ?????????
                        total_amount = amount + pnt_amount;                    // ??????????????? ??? ????????????
                    }
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-2. ???????????? ?????? ?????? ??????                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals("010000000000") )
                {
                    app_time  = c_PayPlus.mf_get_res( "app_time"  ); // ????????????
                    bank_name = c_PayPlus.mf_get_res( "bank_name" ); // ?????????
                    bank_code = c_PayPlus.mf_get_res( "bank_code" ); // ????????????
                    bk_mny    = c_PayPlus.mf_get_res( "bk_mny"    ); // ????????????????????????
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-3. ???????????? ?????? ?????? ??????                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "001000000000" ) )
                {
                    bankname  = c_PayPlus.mf_get_res( "bankname"  ); // ????????? ?????? ??????
                    depositor = c_PayPlus.mf_get_res( "depositor" ); // ????????? ?????? ?????????
                    account   = c_PayPlus.mf_get_res( "account"   ); // ????????? ?????? ??????
                    va_date   = c_PayPlus.mf_get_res( "va_date"   ); // ???????????? ??????????????????
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-4. ????????? ?????? ?????? ??????                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000100000000" ) )
                {
                    pnt_amount   = c_PayPlus.mf_get_res( "pnt_amount"   ); // ???????????? or ????????????
                    pnt_app_time = c_PayPlus.mf_get_res( "pnt_app_time" ); // ????????????
                    pnt_app_no   = c_PayPlus.mf_get_res( "pnt_app_no"   ); // ????????????
                    add_pnt      = c_PayPlus.mf_get_res( "add_pnt"      ); // ?????? ?????????
                    use_pnt      = c_PayPlus.mf_get_res( "use_pnt"      ); // ???????????? ?????????
                    rsv_pnt      = c_PayPlus.mf_get_res( "rsv_pnt"      ); // ??? ?????? ?????????
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-5. ????????? ?????? ?????? ??????                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000010000000" ) )
                {
                    app_time = c_PayPlus.mf_get_res( "hp_app_time" ); // ?????? ??????
                    commid   = c_PayPlus.mf_get_res( "commid"      ); // ????????? ??????
                    mobile_no= c_PayPlus.mf_get_res( "mobile_no"   ); // ????????? ??????
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-6. ????????? ?????? ?????? ??????                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000000001000" ) )
                {
                    app_time    = c_PayPlus.mf_get_res( "tk_app_time" ); // ?????? ??????
                    tk_van_code = c_PayPlus.mf_get_res( "tk_van_code" ); // ????????? ??????
                    tk_app_no   = c_PayPlus.mf_get_res( "tk_app_no"   ); // ?????? ??????
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-7. ??????????????? ?????? ?????? ??????                                          = */
                /* = -------------------------------------------------------------------------- = */
                cash_authno = c_PayPlus.mf_get_res( "cash_authno" ); // ??????????????? ????????????
                cash_no     = c_PayPlus.mf_get_res( "cash_no"     ); // ??????????????? ????????????
            }
        }
        /* = -------------------------------------------------------------------------- = */
        /* =   06. ?????? ?????? ?????? END                                                   = */
        /* ============================================================================== */


        /* = ========================================================================== = */
        /* =   07. ?????? ??? ?????? ?????? DB ??????                                            = */
        /* = -------------------------------------------------------------------------- = */
        /* =      ????????? ?????? ??????????????? DB ?????? ??????????????? ???????????????.                 = */
        /* = -------------------------------------------------------------------------- = */

        if ( req_tx.equals( "pay" ) )
        {

            /* = -------------------------------------------------------------------------- = */
            /* =   07-1. ?????? ?????? DB ??????(res_cd == "0000")                                = */
            /* = -------------------------------------------------------------------------- = */
            /* =        ??? ??????????????? ??????????????? DB ????????? ????????? ????????????.                 = */
            /* = -------------------------------------------------------------------------- = */
            if ( res_cd.equals( "0000" ) )
            {
                TbPayment payment = new TbPayment();
                payment.setAdvId(advertiserId);
                payment.setPayTno(tno);
                payment.setPayAmount(amount);
                payment.setPayPntIssue(pnt_issue);
                payment.setPayCouponMny(coupon_mny);

                // 07-1-1. ????????????
                if ( use_pay_method.equals( "100000000000" ) )
                {
                    payment.setPayCardCd(card_cd);
                    payment.setPayCardName(card_name);
                    payment.setPayAppTime(app_time);
                    payment.setPayAppNo(app_no);
                    payment.setPayNoinf(noinf);
                    payment.setPayQuota(quota);
                    payment.setPayPartcancYn(partcanc_yn);
                    payment.setPayCardBinType01(card_bin_type_01);
                    payment.setPayCardBinType02(card_bin_type_02);
                    payment.setPayCardMny(card_mny);

                    // 07-1-1-1. ????????????(????????????+?????????)
                    if ( pnt_issue.equals( "SCSK" ) || pnt_issue.equals( "SCWB" ) )
                    {
                        payment.setPayPntAmount(pnt_amount);
                        payment.setPayPntAppTime(pnt_app_time);
                        payment.setPayPntAppNo(pnt_app_no);
                        payment.setPayAddPnt(add_pnt);
                        payment.setPayUsePnt(use_pnt);
                        payment.setPayRsvPnt(rsv_pnt);
                        payment.setPayTotalAmount(total_amount);
                    }
                }

                // 07-1-2. ????????????
                if ( use_pay_method.equals("010000000000") )
                {
                    payment.setPayAppTime(app_time);
                    payment.setPayBank_Name(bank_name);
                    payment.setPayBankCode(bank_code);
                    payment.setPayBkMny(bk_mny);
                }
                // 07-1-3. ????????????
                if ( use_pay_method.equals("001000000000") )
                {
                    payment.setPayBankname(bankname);
                    payment.setPayDepositor(depositor);
                    payment.setPayAccount(account);
                    payment.setPayVaDate(va_date);

                }
                // 07-1-4. ?????????
                if ( use_pay_method.equals("000100000000") )
                {
                    payment.setPayPntAmount(pnt_amount);
                    payment.setPayPntAppTime(pnt_app_time);
                    payment.setPayPntAppNo(pnt_app_no);
                    payment.setPayAddPnt(add_pnt);
                    payment.setPayUsePnt(use_pnt);
                    payment.setPayRsvPnt(rsv_pnt);
                }
                // 07-1-5. ?????????
                if ( use_pay_method.equals("000010000000") )
                {
                    payment.setPayAppTime(app_time);
                    payment.setPayCommid(commid);
                    payment.setPayMobileNo(mobile_no);
                }
                // 07-1-6. ?????????
                if ( use_pay_method.equals("000000001000") )
                {
                    payment.setPayAppTime(app_time);
                    payment.setPayTkVanCode(tk_van_code);
                    payment.setPayTkAppNo(tk_app_no);
                }

                payment.setPayCashAuthno(cash_authno);
                payment.setPayCashNo(cash_no);

                try {
                    TbPlan MyPlan = planService.get(planIdInt);
                    int planMonth = MyPlan.getPlnMonth();

                    LocalDate currentLocalDate = LocalDate.now();
                    LocalDate finishLocalDate = LocalDate.now().plusMonths(planMonth);

                    Date currentDate = Date.valueOf(currentLocalDate);
                    Date finishDate = Date.valueOf(finishLocalDate);

                    subscriptionRepository.updateSubscription(advId);
                    TbSubscription subscription = new TbSubscription();
                    subscription.setPlnId(planIdInt);
                    subscription.setAdvId(advertiserId);
                    subscription.setSubStartDt(currentDate);
                    subscription.setSubEndDt(finishDate);
                    subscription.setSubStatus("2");
                    subscription.setSubActive("1");
                    subscriptionService.save(subscription);

                    paymentService.save(payment);

                    try {
                        TbAdvertiser Advertiser = advertiserService.get(advertiserId);
                        String ADV_NAME = Advertiser.getAdvName();
                        String planName = MyPlan.getPlnName();
                        Integer planPriceMonth = MyPlan.getPlnPriceMonth();
                        Integer price = planPriceMonth * planMonth;
                        String PLN_PRICE = price.toString();
                        String CUR_DATE = currentDate.toString();

                        KakaoMessage Message1 = new KakaoMessage();
                        KakaoMessage Message2 = new KakaoMessage();
                        Message1.set("Andrian", "01026763937", ADV_NAME, CUR_DATE, PLN_PRICE, planName);
                        Message2.set("?????????", "01023270875", ADV_NAME, CUR_DATE, PLN_PRICE, planName);
                        Message1.sendMessage();
                        Message2.sendMessage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    bSucc = "false";
                }
            }

            /* = -------------------------------------------------------------------------- = */
            /* =   07-2. ?????? ?????? DB ??????(res_cd != "0000")                                = */
            /* = -------------------------------------------------------------------------- = */
            if( !"0000".equals ( res_cd ) )
            {
            }
        }
        /* = -------------------------------------------------------------------------- = */
        /* =   07. ?????? ??? ?????? ?????? DB ?????? END                                        = */
        /* = ========================================================================== = */


        /* = ========================================================================== = */
        /* =   08. ?????? ?????? DB ?????? ????????? : ????????????                                  = */
        /* = -------------------------------------------------------------------------- = */
        /* =      ?????? ????????? DB ?????? ?????? ???????????? ??????????????? ????????? ?????? ??????         = */
        /* =      DB ????????? ???????????? DB update ??? ???????????? ?????? ??????, ????????????          = */
        /* =      ?????? ?????? ????????? ?????? ??????????????? ???????????? ????????????.                   = */
        /* =                                                                            = */
        /* =      DB ????????? ?????? ??? ??????, bSucc ?????? ??????(String)??? ?????? "false"        = */
        /* =      ??? ????????? ????????? ????????????. (DB ?????? ????????? ???????????? "false" ?????????    = */
        /* =      ?????? ??????????????? ?????????.)                                              = */
        /* = -------------------------------------------------------------------------- = */

        // ?????? ?????? DB ?????? ????????? bSucc?????? false??? ???????????? ???????????? ?????? ??????
//        bSucc = "";

        if (req_tx.equals("pay") )
        {
            if (res_cd.equals("0000") )
            {
                if ( bSucc.equals("false") )
                {
                    int mod_data_set_no;

                    c_PayPlus.mf_init_set();

                    tran_cd = "00200000";

                    mod_data_set_no = c_PayPlus.mf_add_set( "mod_data" );

                    c_PayPlus.mf_set_us( mod_data_set_no, "tno",      tno      ); // KCP ????????? ????????????
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_type", "STSC"   ); // ????????? ?????? ?????? ??????
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_ip",   cust_ip  ); // ?????? ????????? IP
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_desc", "????????? ?????? ?????? ?????? - ??????????????? ?????? ??????"  ); // ?????? ??????

                    c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, "", ordr_idxx, g_conf_log_level, "0" );

                    res_cd  = c_PayPlus.m_res_cd;                                 // ?????? ??????
                    res_msg = c_PayPlus.m_res_msg;                                // ?????? ?????????
                }
            }
        }
        // End of [res_cd = "0000"]
        /* = -------------------------------------------------------------------------- = */
        /* =   08. ?????? ?????? DB ?????? END                                                = */
        /* = ========================================================================== = */

        model.addAttribute("req_tx", req_tx);
        model.addAttribute("use_pay_method", use_pay_method);
        model.addAttribute("bSucc", bSucc);
        model.addAttribute("amount", amount);
        model.addAttribute("res_cd", res_cd);
        model.addAttribute("res_msg", res_msg);
        model.addAttribute("ordr_idxx", ordr_idxx);
        model.addAttribute("tno", tno);
        model.addAttribute("good_name", good_name);
        model.addAttribute("buyr_name", buyr_name);
        model.addAttribute("buyr_tel1", buyr_tel1);
        model.addAttribute("buyr_tel2", buyr_tel2);
        model.addAttribute("buyr_mail", buyr_mail);
        model.addAttribute("app_time", app_time);
        model.addAttribute("card_cd", card_cd);
        model.addAttribute("card_name", card_name);
        model.addAttribute("app_no", app_no);
        model.addAttribute("noinf", noinf);
        model.addAttribute("quota", quota);
        model.addAttribute("partcanc_yn", partcanc_yn);
        model.addAttribute("card_bin_type_01", card_bin_type_01);
        model.addAttribute("card_bin_type_02", card_bin_type_02);
        model.addAttribute("bank_name", bank_name);
        model.addAttribute("bank_code", bank_code);
        model.addAttribute("bankname", bankname);
        model.addAttribute("depositor", depositor);
        model.addAttribute("account", account);
        model.addAttribute("va_date", va_date);
        model.addAttribute("pnt_issue", pnt_issue);
        model.addAttribute("pnt_app_time", pnt_app_time);
        model.addAttribute("pnt_app_no", pnt_app_no);
        model.addAttribute("pnt_amount", pnt_amount);
        model.addAttribute("add_pnt", add_pnt);
        model.addAttribute("use_pnt", use_pnt);
        model.addAttribute("rsv_pnt", rsv_pnt);
        model.addAttribute("commid", commid);
        model.addAttribute("mobile_no", mobile_no);
        model.addAttribute("tk_van_code", tk_van_code);
        model.addAttribute("tk_app_no", tk_app_no);
        model.addAttribute("cash_yn", cash_yn);
        model.addAttribute("cash_authno", cash_authno);
        model.addAttribute("cash_tr_code", cash_tr_code);
        model.addAttribute("cash_id_info", cash_id_info);
        model.addAttribute("cash_no", cash_no);

        return "payment/pp_cli_hub";
    }

    @RequestMapping(value = "/result")
    public String result(@RequestParam Map<String,Object> allParams, HttpServletRequest request) {
        return "payment/finalResult";
    }

    @RequestMapping(value = "/finalResult")
    public String finalResult() {
        return "payment/finalResult";
    }

    @RequestMapping(value = "/success")
    public String success() {
        return "payment/success";
    }

    @RequestMapping(value = "/failed")
    public String failed() {
        return "payment/failed";
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
    public String payment_web_order(@RequestParam Map<String,String> allParams, ModelMap model, HttpServletRequest request) {
        model.addAttribute("advId", allParams.get("advId"));
        model.addAttribute("planId", allParams.get("planId"));

        String PLN_ID = request.getParameter("planId");
        String ADV_ID = request.getParameter("advId");

        int planId = Integer.parseInt(PLN_ID);
        int advId = Integer.parseInt(ADV_ID);

        try {
            TbPlan MyPlan = planService.get(planId);
            String planName = MyPlan.getPlnName();
            int planMonth = MyPlan.getPlnMonth();
            int planPrice = MyPlan.getPlnPriceMonth();
            long finalPrice = Math.round(planMonth * planPrice * 1.1);

            model.addAttribute("money", Integer.toString(planPrice));
            model.addAttribute("plan", planName);


            TbAdvertiser Advertiser = advertiserService.get(advId);
            String ADV_NAME = Advertiser.getAdvName();
            String ADV_EMAIL = Advertiser.getAdvEmail();
            String ADV_TEL = Advertiser.getAdvTel();

            model.addAttribute("name", ADV_NAME);
            model.addAttribute("email", ADV_EMAIL);
            model.addAttribute("phone", ADV_TEL);

            /*LocalDate currentLocalDate = LocalDate.now();
            LocalDate finishLocalDate = LocalDate.now().plusMonths(planMonth);

            Date currentDate = Date.valueOf(currentLocalDate);
            Date finishDate = Date.valueOf(finishLocalDate);

            subscriptionRepository.updateSubscription(ADV_ID);
            TbSubscription subscription = new TbSubscription();
            subscription.setPlnId(planId);
            subscription.setAdvId(advId);
            subscription.setSubStartDt(currentDate);
            subscription.setSubEndDt(finishDate);
            subscription.setSubStatus("2");
            subscription.setSubActive("1");
            subscriptionService.save(subscription);*/


            return "payment/order";
        } catch (Exception e){
            e.printStackTrace();
            return "payment/order";
        }


    }

    @RequestMapping(value = "/payment/pp_cli_hub", method = RequestMethod.POST)
    public String payment_pp_cli_hub(
//            @RequestParam Map<String,Object> allParams
    @RequestParam(value = "advId", required = true) String advId,
    @RequestParam(value = "planId", required = true) String planId,
    @RequestParam(value = "req_tx", required = true) String request_req_tx,
    @RequestParam(value = "tran_cd", required = true) String request_tran_cd,
    @RequestParam(value = "ordr_idxx", required = true) String request_ordr_idxx,
    @RequestParam(value = "good_name", required = true) String request_good_name,
    @RequestParam(value = "enc_data", required = true) String enc_data,
    @RequestParam(value = "enc_info", required = true) String enc_info,
    @RequestParam(value = "good_mny", required = true) String request_good_mny,
    @RequestParam(value = "use_pay_method", required = true) String request_use_pay_method,
    @RequestParam(value = "shop_user_id", required = false) String request_shop_user_id,
    @RequestParam(value = "cash_yn", required = true) String request_cash_yn,
    @RequestParam(value = "cash_tr_code", required = true) String request_cash_tr_code,
    @RequestParam(value = "cash_id_info", required = true) String request_cash_id_info,
    @RequestParam(value = "tno", required = false) String request_tno,
    HttpServletRequest request,
    ModelMap model
    ) {

        /* ============================================================================== */
        /* =   02. ?????? ?????? ?????? ??????                                                  = */
        /* = -------------------------------------------------------------------------- = */
        String req_tx         = f_get_parm( request_req_tx ); // ?????? ??????
        String tran_cd        = f_get_parm( request_tran_cd ); // ?????? ??????

        int advertiserId = Integer.parseInt(advId);
        int planIdInt = Integer.parseInt(planId);

        /* = -------------------------------------------------------------------------- = */
        String cust_ip        = f_get_parm( request.getRemoteAddr()                  ); // ?????? IP
        String ordr_idxx      = f_get_parm( request_ordr_idxx ); // ????????? ????????????
        String good_name      = f_get_parm( request_good_name ); // ?????????
        /* = -------------------------------------------------------------------------- = */
        String res_cd         = "";                                                     // ????????????
        String res_msg        = "";                                                     // ?????? ?????????
        String tno            = f_get_parm( request_tno ); // KCP ?????? ?????? ??????
        /* = -------------------------------------------------------------------------- = */
        String buyr_name      = f_get_parm( request.getParameter( "buyr_name"      ) ); // ????????????
        String buyr_tel1      = f_get_parm( request.getParameter( "buyr_tel1"      ) ); // ????????? ????????????
        String buyr_tel2      = f_get_parm( request.getParameter( "buyr_tel2"      ) ); // ????????? ????????? ??????
        String buyr_mail      = f_get_parm( request.getParameter( "buyr_mail"      ) ); // ????????? E-mail ??????
        /* = -------------------------------------------------------------------------- = */
        String use_pay_method = f_get_parm( request_use_pay_method ); // ?????? ??????
        String bSucc          = "";                                                     // ?????? DB ?????? ?????? ??????
        /* = -------------------------------------------------------------------------- = */
        String app_time       = "";                                                     // ???????????? (?????? ?????? ?????? ??????)
        String amount         = "";                                                     // KCP ?????? ????????????
        String total_amount   = "0";                                                    // ??????????????? ??? ????????????
        String coupon_mny     = "";                                                     // ????????????
        /* = -------------------------------------------------------------------------- = */
        String card_cd        = "";                                                     // ???????????? ??????
        String card_name      = "";                                                     // ???????????? ???
        String app_no         = "";                                                     // ???????????? ????????????
        String noinf          = "";                                                     // ???????????? ????????? ??????
        String quota          = "";                                                     // ???????????? ????????????
        String partcanc_yn    = "";                                                     // ???????????? ????????????
        String card_bin_type_01 = "";                                                   // ????????????1
        String card_bin_type_02 = "";                                                   // ????????????2
        String card_mny       = "";                                                     // ??????????????????
        /* = -------------------------------------------------------------------------- = */
        String bank_name      = "";                                                     // ?????????
        String bank_code      = "";                                                     // ????????????
        String bk_mny         = "";                                                     // ????????????????????????
        /* = -------------------------------------------------------------------------- = */
        String bankname       = "";                                                     // ?????? ?????????
        String depositor      = "";                                                     // ?????? ?????? ????????? ??????
        String account        = "";                                                     // ?????? ?????? ??????
        String va_date        = "";                                                     // ???????????? ??????????????????
        /* = -------------------------------------------------------------------------- = */
        String pnt_issue      = "";                                                     // ?????? ???????????? ??????
        String pnt_amount     = "";                                                     // ???????????? or ????????????
        String pnt_app_time   = "";                                                     // ????????????
        String pnt_app_no     = "";                                                     // ????????????
        String add_pnt        = "";                                                     // ?????? ?????????
        String use_pnt        = "";                                                     // ???????????? ?????????
        String rsv_pnt        = "";                                                     // ??? ?????? ?????????
        /* = -------------------------------------------------------------------------- = */
        String commid         = "";                                                     // ???????????????
        String mobile_no      = "";                                                     // ???????????????
        /* = -------------------------------------------------------------------------- = */
        String shop_user_id   = f_get_parm( request_shop_user_id ); // ????????? ?????? ?????????
        String tk_van_code    = "";                                                     // ???????????????
        String tk_app_no      = "";                                                     // ????????????
        /* = -------------------------------------------------------------------------- = */
        String cash_yn        = f_get_parm( request_cash_yn ); // ?????? ????????? ?????? ??????
        String cash_authno    = "";                                                     // ?????? ????????? ?????? ??????
        String cash_tr_code   = f_get_parm( request_cash_tr_code ); // ?????? ????????? ?????? ??????
        String cash_id_info   = f_get_parm( request_cash_id_info ); // ?????? ????????? ?????? ??????
        String cash_no        = "";                                                     // ?????? ????????? ?????? ??????


        /* ============================================================================== */
        /* =   02. ?????? ?????? ?????? ?????? END
        /* ============================================================================== */


        /* ============================================================================== */
        /* =   03. ???????????? ?????? ??? ?????????(?????? ??????)                                   = */
        /* = -------------------------------------------------------------------------- = */
        /* =       ????????? ????????? ??????????????? ???????????? ????????? ?????????.                     = */
        /* = -------------------------------------------------------------------------- = */
        //Test pay
        /*String g_conf_gw_url    = "testpaygw.kcp.co.kr";
        String g_conf_gw_port   = "8090";        // ????????????(????????????)
        int    g_conf_tx_mode   = 0;             // ????????????
        String g_conf_log_dir   = "C:\\Tomcat\\apache-tomcat-8.5.64\\logs"; // LOG ???????????? ???????????? ??????
        String g_conf_site_cd   = "T0000";
        String g_conf_site_key  = "3grptw1.zW0GSo4PQdaGvsF__";
        String g_conf_log_level = "3";*/

        //Real pay
        String g_conf_gw_url    = "paygw.kcp.co.kr";
        String g_conf_log_dir   = "C:\\Tomcat\\apache-tomcat-8.5.64\\logs"; // LOG ???????????? ???????????? ??????
        String g_conf_site_cd   = "ABJXF";
        String g_conf_site_key  = "0YrV3xlFox8xxG-35Ldbi1x__";
        String g_conf_log_level = "3";
        String g_conf_gw_port   = "8090";        // ????????????(????????????)
        int    g_conf_tx_mode   = 0;             // ????????????



        J_PP_CLI_N c_PayPlus = new J_PP_CLI_N();

        c_PayPlus.mf_init( "", g_conf_gw_url, g_conf_gw_port, g_conf_tx_mode, g_conf_log_dir );
        c_PayPlus.mf_init_set();

        /* ============================================================================== */
        /* =   03. ???????????? ?????? ??? ????????? END                                          = */
        /* ============================================================================== */


        /* ============================================================================== */
        /* =   04. ?????? ?????? ?????? ??????                                                  = */
        /* = -------------------------------------------------------------------------- = */
        /* = -------------------------------------------------------------------------- = */
        /* =   04-1. ?????? ?????? ?????? ??????                                                = */
        /* = -------------------------------------------------------------------------- = */
        if (req_tx.equals("pay")) {
            c_PayPlus.mf_set_enc_data( f_get_parm( enc_data ),
                                       f_get_parm( enc_info ) );
            String good_mny      = f_get_parm( request_good_mny );

            if(good_mny.trim().length() > 0)
            {
                int ordr_data_set_no;
                ordr_data_set_no = c_PayPlus.mf_add_set( "ordr_data" );
                c_PayPlus.mf_set_us( ordr_data_set_no, "ordr_mony", good_mny );
            }
        }; // ?????? ??????
        /* = -------------------------------------------------------------------------- = */
        /* =   04. ?????? ?????? ?????? ?????? END                                              = */
        /* = ========================================================================== = */


        /* = ========================================================================== = */
        /* =   05. ??????                                                                 = */
        /* = -------------------------------------------------------------------------- = */
        if ( tran_cd.length() > 0 )
        {
            c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, "", ordr_idxx, g_conf_log_level, "0" );
        }
        else
        {
            c_PayPlus.m_res_cd  = "9562";
            c_PayPlus.m_res_msg = "?????? ??????|tran_cd?????? ???????????? ???????????????.";
        }

        res_cd  = c_PayPlus.m_res_cd;  // ?????? ??????
        res_msg = c_PayPlus.m_res_msg; // ?????? ?????????
        /* = -------------------------------------------------------------------------- = */
        /* =   05. ?????? END                                                             = */
        /* ============================================================================== */

        /* ============================================================================== */
        /* =   06. ?????? ?????? ??? ??????                                                    = */
        /* = -------------------------------------------------------------------------- = */
        if ( req_tx.equals( "pay" ) )
        {
            if ( res_cd.equals( "0000" ) )
            {
                tno       = c_PayPlus.mf_get_res( "tno"       ); // KCP ?????? ?????? ??????
                amount    = c_PayPlus.mf_get_res( "amount"    ); // KCP ?????? ?????? ??????
                pnt_issue = c_PayPlus.mf_get_res( "pnt_issue" ); // ?????? ???????????? ??????
                coupon_mny = c_PayPlus.mf_get_res( "coupon_mny" ); // ????????????

                /* = -------------------------------------------------------------------------- = */
                /* =   06-1. ???????????? ?????? ?????? ??????                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "100000000000" ) )
                {
                    card_cd   = c_PayPlus.mf_get_res( "card_cd"   ); // ????????? ??????
                    card_name = c_PayPlus.mf_get_res( "card_name" ); // ????????? ???
                    app_time  = c_PayPlus.mf_get_res( "app_time"  ); // ????????????
                    app_no    = c_PayPlus.mf_get_res( "app_no"    ); // ????????????
                    noinf     = c_PayPlus.mf_get_res( "noinf"     ); // ????????? ??????
                    quota     = c_PayPlus.mf_get_res( "quota"     ); // ?????? ?????? ???
                    partcanc_yn = c_PayPlus.mf_get_res( "partcanc_yn"     ); // ???????????? ????????????
                    card_bin_type_01 = c_PayPlus.mf_get_res( "card_bin_type_01" ); // ????????????1
                    card_bin_type_02 = c_PayPlus.mf_get_res( "card_bin_type_02" ); // ????????????2
                    card_mny = c_PayPlus.mf_get_res( "card_mny" ); // ??????????????????

                    /* = -------------------------------------------------------------- = */
                    /* =   06-1.1. ????????????(?????????+????????????) ?????? ?????? ??????             = */
                    /* = -------------------------------------------------------------- = */
                    if ( pnt_issue.equals( "SCSK" ) || pnt_issue.equals( "SCWB" ) )
                    {
                        pnt_amount   = c_PayPlus.mf_get_res( "pnt_amount"   ); // ???????????? or ????????????
                        pnt_app_time = c_PayPlus.mf_get_res( "pnt_app_time" ); // ????????????
                        pnt_app_no   = c_PayPlus.mf_get_res( "pnt_app_no"   ); // ????????????
                        add_pnt      = c_PayPlus.mf_get_res( "add_pnt"      ); // ?????? ?????????
                        use_pnt      = c_PayPlus.mf_get_res( "use_pnt"      ); // ???????????? ?????????
                        rsv_pnt      = c_PayPlus.mf_get_res( "rsv_pnt"      ); // ??? ?????? ?????????
                        total_amount = amount + pnt_amount;                    // ??????????????? ??? ????????????
                    }
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-2. ???????????? ?????? ?????? ??????                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals("010000000000") )
                {
                    app_time  = c_PayPlus.mf_get_res( "app_time"  ); // ????????????
                    bank_name = c_PayPlus.mf_get_res( "bank_name" ); // ?????????
                    bank_code = c_PayPlus.mf_get_res( "bank_code" ); // ????????????
                    bk_mny    = c_PayPlus.mf_get_res( "bk_mny"    ); // ????????????????????????
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-3. ???????????? ?????? ?????? ??????                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "001000000000" ) )
                {
                    bankname  = c_PayPlus.mf_get_res( "bankname"  ); // ????????? ?????? ??????
                    depositor = c_PayPlus.mf_get_res( "depositor" ); // ????????? ?????? ?????????
                    account   = c_PayPlus.mf_get_res( "account"   ); // ????????? ?????? ??????
                    va_date   = c_PayPlus.mf_get_res( "va_date"   ); // ???????????? ??????????????????
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-4. ????????? ?????? ?????? ??????                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000100000000" ) )
                {
                    pnt_amount   = c_PayPlus.mf_get_res( "pnt_amount"   ); // ???????????? or ????????????
                    pnt_app_time = c_PayPlus.mf_get_res( "pnt_app_time" ); // ????????????
                    pnt_app_no   = c_PayPlus.mf_get_res( "pnt_app_no"   ); // ????????????
                    add_pnt      = c_PayPlus.mf_get_res( "add_pnt"      ); // ?????? ?????????
                    use_pnt      = c_PayPlus.mf_get_res( "use_pnt"      ); // ???????????? ?????????
                    rsv_pnt      = c_PayPlus.mf_get_res( "rsv_pnt"      ); // ??? ?????? ?????????
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-5. ????????? ?????? ?????? ??????                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000010000000" ) )
                {
                    app_time = c_PayPlus.mf_get_res( "hp_app_time" ); // ?????? ??????
                    commid   = c_PayPlus.mf_get_res( "commid"      ); // ????????? ??????
                    mobile_no= c_PayPlus.mf_get_res( "mobile_no"   ); // ????????? ??????
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-6. ????????? ?????? ?????? ??????                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000000001000" ) )
                {
                    app_time    = c_PayPlus.mf_get_res( "tk_app_time" ); // ?????? ??????
                    tk_van_code = c_PayPlus.mf_get_res( "tk_van_code" ); // ????????? ??????
                    tk_app_no   = c_PayPlus.mf_get_res( "tk_app_no"   ); // ?????? ??????
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-7. ??????????????? ?????? ?????? ??????                                          = */
                /* = -------------------------------------------------------------------------- = */
                cash_authno = c_PayPlus.mf_get_res( "cash_authno" ); // ??????????????? ????????????
                cash_no     = c_PayPlus.mf_get_res( "cash_no"     ); // ??????????????? ????????????
            }
        }
        /* = -------------------------------------------------------------------------- = */
        /* =   06. ?????? ?????? ?????? END                                                   = */
        /* ============================================================================== */


        /* = ========================================================================== = */
        /* =   07. ?????? ??? ?????? ?????? DB ??????                                            = */
        /* = -------------------------------------------------------------------------- = */
        /* =      ????????? ?????? ??????????????? DB ?????? ??????????????? ???????????????.                 = */
        /* = -------------------------------------------------------------------------- = */

        if ( req_tx.equals( "pay" ) )
        {

            /* = -------------------------------------------------------------------------- = */
            /* =   07-1. ?????? ?????? DB ??????(res_cd == "0000")                                = */
            /* = -------------------------------------------------------------------------- = */
            /* =        ??? ??????????????? ??????????????? DB ????????? ????????? ????????????.                 = */
            /* = -------------------------------------------------------------------------- = */
            if ( res_cd.equals( "0000" ) )
            {
                TbPayment payment = new TbPayment();
                payment.setAdvId(advertiserId);
                payment.setPayTno(tno);
                payment.setPayAmount(amount);
                payment.setPayPntIssue(pnt_issue);
                payment.setPayCouponMny(coupon_mny);

                // 07-1-1. ????????????
                if ( use_pay_method.equals( "100000000000" ) )
                {
                    payment.setPayCardCd(card_cd);
                    payment.setPayCardName(card_name);
                    payment.setPayAppTime(app_time);
                    payment.setPayAppNo(app_no);
                    payment.setPayNoinf(noinf);
                    payment.setPayQuota(quota);
                    payment.setPayPartcancYn(partcanc_yn);
                    payment.setPayCardBinType01(card_bin_type_01);
                    payment.setPayCardBinType02(card_bin_type_02);
                    payment.setPayCardMny(card_mny);

                    // 07-1-1-1. ????????????(????????????+?????????)
                    if ( pnt_issue.equals( "SCSK" ) || pnt_issue.equals( "SCWB" ) )
                    {
                        payment.setPayPntAmount(pnt_amount);
                        payment.setPayPntAppTime(pnt_app_time);
                        payment.setPayPntAppNo(pnt_app_no);
                        payment.setPayAddPnt(add_pnt);
                        payment.setPayUsePnt(use_pnt);
                        payment.setPayRsvPnt(rsv_pnt);
                        payment.setPayTotalAmount(total_amount);
                    }
                }

                // 07-1-2. ????????????
                if ( use_pay_method.equals("010000000000") )
                {
                    payment.setPayAppTime(app_time);
                    payment.setPayBank_Name(bank_name);
                    payment.setPayBankCode(bank_code);
                    payment.setPayBkMny(bk_mny);
                }
                // 07-1-3. ????????????
                if ( use_pay_method.equals("001000000000") )
                {
                    payment.setPayBankname(bankname);
                    payment.setPayDepositor(depositor);
                    payment.setPayAccount(account);
                    payment.setPayVaDate(va_date);

                }
                // 07-1-4. ?????????
                if ( use_pay_method.equals("000100000000") )
                {
                    payment.setPayPntAmount(pnt_amount);
                    payment.setPayPntAppTime(pnt_app_time);
                    payment.setPayPntAppNo(pnt_app_no);
                    payment.setPayAddPnt(add_pnt);
                    payment.setPayUsePnt(use_pnt);
                    payment.setPayRsvPnt(rsv_pnt);
                }
                // 07-1-5. ?????????
                if ( use_pay_method.equals("000010000000") )
                {
                    payment.setPayAppTime(app_time);
                    payment.setPayCommid(commid);
                    payment.setPayMobileNo(mobile_no);
                }
                // 07-1-6. ?????????
                if ( use_pay_method.equals("000000001000") )
                {
                    payment.setPayAppTime(app_time);
                    payment.setPayTkVanCode(tk_van_code);
                    payment.setPayTkAppNo(tk_app_no);
                }

                payment.setPayCashAuthno(cash_authno);
                payment.setPayCashNo(cash_no);

                try {
                    TbPlan MyPlan = planService.get(planIdInt);
                    int planMonth = MyPlan.getPlnMonth();

                    LocalDate currentLocalDate = LocalDate.now();
                    LocalDate finishLocalDate = LocalDate.now().plusMonths(planMonth);

                    Date currentDate = Date.valueOf(currentLocalDate);
                    Date finishDate = Date.valueOf(finishLocalDate);

                    subscriptionRepository.updateSubscription(advId);
                    TbSubscription subscription = new TbSubscription();
                    subscription.setPlnId(planIdInt);
                    subscription.setAdvId(advertiserId);
                    subscription.setSubStartDt(currentDate);
                    subscription.setSubEndDt(finishDate);
                    subscription.setSubStatus("2");
                    subscription.setSubActive("1");
                    subscriptionService.save(subscription);

                    paymentService.save(payment);

                    try {
                        TbAdvertiser Advertiser = advertiserService.get(advertiserId);
                        String ADV_NAME = Advertiser.getAdvName();
                        String planName = MyPlan.getPlnName();
                        Integer planPriceMonth = MyPlan.getPlnPriceMonth();
                        Integer price = planPriceMonth * planMonth;
                        String PLN_PRICE = price.toString();
                        String CUR_DATE = currentDate.toString();

                        KakaoMessage Message1 = new KakaoMessage();
                        KakaoMessage Message2 = new KakaoMessage();
                        Message1.set("Andrian", "01026763937", ADV_NAME, CUR_DATE, PLN_PRICE, planName);
                        Message2.set("?????????", "01023270875", ADV_NAME, CUR_DATE, PLN_PRICE, planName);
                        Message1.sendMessage();
                        Message2.sendMessage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    bSucc = "false";
                }
            }

            /* = -------------------------------------------------------------------------- = */
            /* =   07-2. ?????? ?????? DB ??????(res_cd != "0000")                                = */
            /* = -------------------------------------------------------------------------- = */
            if( !"0000".equals ( res_cd ) )
            {
            }
        }
        /* = -------------------------------------------------------------------------- = */
        /* =   07. ?????? ??? ?????? ?????? DB ?????? END                                        = */
        /* = ========================================================================== = */


        /* = ========================================================================== = */
        /* =   08. ?????? ?????? DB ?????? ????????? : ????????????                                  = */
        /* = -------------------------------------------------------------------------- = */
        /* =      ?????? ????????? DB ?????? ?????? ???????????? ??????????????? ????????? ?????? ??????         = */
        /* =      DB ????????? ???????????? DB update ??? ???????????? ?????? ??????, ????????????          = */
        /* =      ?????? ?????? ????????? ?????? ??????????????? ???????????? ????????????.                   = */
        /* =                                                                            = */
        /* =      DB ????????? ?????? ??? ??????, bSucc ?????? ??????(String)??? ?????? "false"        = */
        /* =      ??? ????????? ????????? ????????????. (DB ?????? ????????? ???????????? "false" ?????????    = */
        /* =      ?????? ??????????????? ?????????.)                                              = */
        /* = -------------------------------------------------------------------------- = */

        // ?????? ?????? DB ?????? ????????? bSucc?????? false??? ???????????? ???????????? ?????? ??????
//        bSucc = "";

        if (req_tx.equals("pay") )
        {
            if (res_cd.equals("0000") )
            {
                if ( bSucc.equals("false") )
                {
                    int mod_data_set_no;

                    c_PayPlus.mf_init_set();

                    tran_cd = "00200000";

                    mod_data_set_no = c_PayPlus.mf_add_set( "mod_data" );

                    c_PayPlus.mf_set_us( mod_data_set_no, "tno",      tno      ); // KCP ????????? ????????????
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_type", "STSC"   ); // ????????? ?????? ?????? ??????
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_ip",   cust_ip  ); // ?????? ????????? IP
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_desc", "????????? ?????? ?????? ?????? - ??????????????? ?????? ??????"  ); // ?????? ??????

                    c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, "", ordr_idxx, g_conf_log_level, "0" );

                    res_cd  = c_PayPlus.m_res_cd;                                 // ?????? ??????
                    res_msg = c_PayPlus.m_res_msg;                                // ?????? ?????????
                }
            }
        }
        // End of [res_cd = "0000"]
        /* = -------------------------------------------------------------------------- = */
        /* =   08. ?????? ?????? DB ?????? END                                                = */
        /* = ========================================================================== = */

        model.addAttribute("req_tx", req_tx);
        model.addAttribute("use_pay_method", use_pay_method);
        model.addAttribute("bSucc", bSucc);
        model.addAttribute("amount", amount);
        model.addAttribute("res_cd", res_cd);
        model.addAttribute("res_msg", res_msg);
        model.addAttribute("ordr_idxx", ordr_idxx);
        model.addAttribute("tno", tno);
        model.addAttribute("good_name", good_name);
        model.addAttribute("buyr_name", buyr_name);
        model.addAttribute("buyr_tel1", buyr_tel1);
        model.addAttribute("buyr_tel2", buyr_tel2);
        model.addAttribute("buyr_mail", buyr_mail);
        model.addAttribute("app_time", app_time);
        model.addAttribute("card_cd", card_cd);
        model.addAttribute("card_name", card_name);
        model.addAttribute("app_no", app_no);
        model.addAttribute("noinf", noinf);
        model.addAttribute("quota", quota);
        model.addAttribute("partcanc_yn", partcanc_yn);
        model.addAttribute("card_bin_type_01", card_bin_type_01);
        model.addAttribute("card_bin_type_02", card_bin_type_02);
        model.addAttribute("bank_name", bank_name);
        model.addAttribute("bank_code", bank_code);
        model.addAttribute("bankname", bankname);
        model.addAttribute("depositor", depositor);
        model.addAttribute("account", account);
        model.addAttribute("va_date", va_date);
        model.addAttribute("pnt_issue", pnt_issue);
        model.addAttribute("pnt_app_time", pnt_app_time);
        model.addAttribute("pnt_app_no", pnt_app_no);
        model.addAttribute("pnt_amount", pnt_amount);
        model.addAttribute("add_pnt", add_pnt);
        model.addAttribute("use_pnt", use_pnt);
        model.addAttribute("rsv_pnt", rsv_pnt);
        model.addAttribute("commid", commid);
        model.addAttribute("mobile_no", mobile_no);
        model.addAttribute("tk_van_code", tk_van_code);
        model.addAttribute("tk_app_no", tk_app_no);
        model.addAttribute("cash_yn", cash_yn);
        model.addAttribute("cash_authno", cash_authno);
        model.addAttribute("cash_tr_code", cash_tr_code);
        model.addAttribute("cash_id_info", cash_id_info);
        model.addAttribute("cash_no", cash_no);

        return "payment/pp_cli_hub";
    }


    @RequestMapping(value = "/payment/result", method = RequestMethod.POST)
    public String payment_result(@RequestParam Map<String,Object> allParams, HttpServletRequest request) {
        return "payment/finalResult";
    }

    @RequestMapping(value = "/payment/finalResult")
    public String payment_finalResult() {
        return "payment/finalResult";
    }

    @RequestMapping(value = "/payment/success")
    public String payment_success() {
        return "payment/success";
    }

    @RequestMapping(value = "/payment/failed")
    public String payment_failed() {
        return "payment/failed";
    }
}