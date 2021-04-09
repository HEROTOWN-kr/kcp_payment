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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.kcp.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;

import java.time.LocalDate;
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
        /* =   02. 지불 요청 정보 설정                                                  = */
        /* = -------------------------------------------------------------------------- = */
        String req_tx         = f_get_parm( request_req_tx ); // 요청 종류
        String tran_cd        = f_get_parm( request_tran_cd ); // 처리 종류

        int advertiserId = Integer.parseInt(advId);
        int planIdInt = Integer.parseInt(planId);

        /* = -------------------------------------------------------------------------- = */
        String cust_ip        = f_get_parm( request.getRemoteAddr()                  ); // 요청 IP
        String ordr_idxx      = f_get_parm( request_ordr_idxx ); // 쇼핑몰 주문번호
        String good_name      = f_get_parm( request_good_name ); // 상품명
        /* = -------------------------------------------------------------------------- = */
        String res_cd         = "";                                                     // 응답코드
        String res_msg        = "";                                                     // 응답 메세지
        String tno            = f_get_parm( request_tno ); // KCP 거래 고유 번호
        /* = -------------------------------------------------------------------------- = */
        String buyr_name      = f_get_parm( request.getParameter( "buyr_name"      ) ); // 주문자명
        String buyr_tel1      = f_get_parm( request.getParameter( "buyr_tel1"      ) ); // 주문자 전화번호
        String buyr_tel2      = f_get_parm( request.getParameter( "buyr_tel2"      ) ); // 주문자 핸드폰 번호
        String buyr_mail      = f_get_parm( request.getParameter( "buyr_mail"      ) ); // 주문자 E-mail 주소
        /* = -------------------------------------------------------------------------- = */
        String use_pay_method = f_get_parm( request_use_pay_method ); // 결제 방법
        String bSucc          = "";                                                     // 업체 DB 처리 성공 여부
        /* = -------------------------------------------------------------------------- = */
        String app_time       = "";                                                     // 승인시간 (모든 결제 수단 공통)
        String amount         = "";                                                     // KCP 실제 거래금액
        String total_amount   = "0";                                                    // 복합결제시 총 거래금액
        String coupon_mny     = "";                                                     // 쿠폰금액
        /* = -------------------------------------------------------------------------- = */
        String card_cd        = "";                                                     // 신용카드 코드
        String card_name      = "";                                                     // 신용카드 명
        String app_no         = "";                                                     // 신용카드 승인번호
        String noinf          = "";                                                     // 신용카드 무이자 여부
        String quota          = "";                                                     // 신용카드 할부개월
        String partcanc_yn    = "";                                                     // 부분취소 가능유무
        String card_bin_type_01 = "";                                                   // 카드구분1
        String card_bin_type_02 = "";                                                   // 카드구분2
        String card_mny       = "";                                                     // 카드결제금액
        /* = -------------------------------------------------------------------------- = */
        String bank_name      = "";                                                     // 은행명
        String bank_code      = "";                                                     // 은행코드
        String bk_mny         = "";                                                     // 계좌이체결제금액
        /* = -------------------------------------------------------------------------- = */
        String bankname       = "";                                                     // 입금 은행명
        String depositor      = "";                                                     // 입금 계좌 예금주 성명
        String account        = "";                                                     // 입금 계좌 번호
        String va_date        = "";                                                     // 가상계좌 입금마감시간
        /* = -------------------------------------------------------------------------- = */
        String pnt_issue      = "";                                                     // 결제 포인트사 코드
        String pnt_amount     = "";                                                     // 적립금액 or 사용금액
        String pnt_app_time   = "";                                                     // 승인시간
        String pnt_app_no     = "";                                                     // 승인번호
        String add_pnt        = "";                                                     // 발생 포인트
        String use_pnt        = "";                                                     // 사용가능 포인트
        String rsv_pnt        = "";                                                     // 총 누적 포인트
        /* = -------------------------------------------------------------------------- = */
        String commid         = "";                                                     // 통신사코드
        String mobile_no      = "";                                                     // 휴대폰번호
        /* = -------------------------------------------------------------------------- = */
        String shop_user_id   = f_get_parm( request_shop_user_id ); // 가맹점 고객 아이디
        String tk_van_code    = "";                                                     // 발급사코드
        String tk_app_no      = "";                                                     // 승인번호
        /* = -------------------------------------------------------------------------- = */
        String cash_yn        = f_get_parm( request_cash_yn ); // 현금 영수증 등록 여부
        String cash_authno    = "";                                                     // 현금 영수증 승인 번호
        String cash_tr_code   = f_get_parm( request_cash_tr_code ); // 현금 영수증 발행 구분
        String cash_id_info   = f_get_parm( request_cash_id_info ); // 현금 영수증 등록 번호
        String cash_no        = "";                                                     // 현금 영수증 거래 번호


        /* ============================================================================== */
        /* =   02. 지불 요청 정보 설정 END
        /* ============================================================================== */


        /* ============================================================================== */
        /* =   03. 인스턴스 생성 및 초기화(변경 불가)                                   = */
        /* = -------------------------------------------------------------------------- = */
        /* =       결제에 필요한 인스턴스를 생성하고 초기화 합니다.                     = */
        /* = -------------------------------------------------------------------------- = */
       /* String g_conf_gw_url    = "testpaygw.kcp.co.kr";
        String g_conf_gw_port   = "8090";        // 포트번호(변경불가)
        int    g_conf_tx_mode   = 0;             // 변경불가
        String g_conf_log_dir   = "C:\\Tomcat\\apache-tomcat-8.5.64\\logs"; // LOG 디렉토리 절대경로 입력
        String g_conf_site_cd   = "T0000";
        String g_conf_site_key  = "3grptw1.zW0GSo4PQdaGvsF__";
        String g_conf_log_level = "3";*/

        String g_conf_gw_url    = "paygw.kcp.co.kr";
        String g_conf_log_dir   = "C:\\Tomcat\\apache-tomcat-8.5.64\\logs"; // LOG 디렉토리 절대경로 입력
        String g_conf_site_cd   = "ABJXF";
        String g_conf_site_key  = "0YrV3xlFox8xxG-35Ldbi1x__";
        String g_conf_log_level = "3";
        String g_conf_gw_port   = "8090";        // 포트번호(변경불가)
        int    g_conf_tx_mode   = 0;             // 변경불가



        J_PP_CLI_N c_PayPlus = new J_PP_CLI_N();

        c_PayPlus.mf_init( "", g_conf_gw_url, g_conf_gw_port, g_conf_tx_mode, g_conf_log_dir );
        c_PayPlus.mf_init_set();

        /* ============================================================================== */
        /* =   03. 인스턴스 생성 및 초기화 END                                          = */
        /* ============================================================================== */


        /* ============================================================================== */
        /* =   04. 처리 요청 정보 설정                                                  = */
        /* = -------------------------------------------------------------------------- = */
        /* = -------------------------------------------------------------------------- = */
        /* =   04-1. 승인 요청 정보 설정                                                = */
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
        }; // 요청 종류
        /* = -------------------------------------------------------------------------- = */
        /* =   04. 처리 요청 정보 설정 END                                              = */
        /* = ========================================================================== = */


        /* = ========================================================================== = */
        /* =   05. 실행                                                                 = */
        /* = -------------------------------------------------------------------------- = */
        if ( tran_cd.length() > 0 )
        {
            c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, "", ordr_idxx, g_conf_log_level, "0" );
        }
        else
        {
            c_PayPlus.m_res_cd  = "9562";
            c_PayPlus.m_res_msg = "연동 오류|tran_cd값이 설정되지 않았습니다.";
        }

        res_cd  = c_PayPlus.m_res_cd;  // 결과 코드
        res_msg = c_PayPlus.m_res_msg; // 결과 메시지
        /* = -------------------------------------------------------------------------- = */
        /* =   05. 실행 END                                                             = */
        /* ============================================================================== */

        /* ============================================================================== */
        /* =   06. 승인 결과 값 추출                                                    = */
        /* = -------------------------------------------------------------------------- = */
        if ( req_tx.equals( "pay" ) )
        {
            if ( res_cd.equals( "0000" ) )
            {
                tno       = c_PayPlus.mf_get_res( "tno"       ); // KCP 거래 고유 번호
                amount    = c_PayPlus.mf_get_res( "amount"    ); // KCP 실제 거래 금액
                pnt_issue = c_PayPlus.mf_get_res( "pnt_issue" ); // 결제 포인트사 코드
                coupon_mny = c_PayPlus.mf_get_res( "coupon_mny" ); // 쿠폰금액

                /* = -------------------------------------------------------------------------- = */
                /* =   06-1. 신용카드 승인 결과 처리                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "100000000000" ) )
                {
                    card_cd   = c_PayPlus.mf_get_res( "card_cd"   ); // 카드사 코드
                    card_name = c_PayPlus.mf_get_res( "card_name" ); // 카드사 명
                    app_time  = c_PayPlus.mf_get_res( "app_time"  ); // 승인시간
                    app_no    = c_PayPlus.mf_get_res( "app_no"    ); // 승인번호
                    noinf     = c_PayPlus.mf_get_res( "noinf"     ); // 무이자 여부
                    quota     = c_PayPlus.mf_get_res( "quota"     ); // 할부 개월 수
                    partcanc_yn = c_PayPlus.mf_get_res( "partcanc_yn"     ); // 부분취소 가능유무
                    card_bin_type_01 = c_PayPlus.mf_get_res( "card_bin_type_01" ); // 카드구분1
                    card_bin_type_02 = c_PayPlus.mf_get_res( "card_bin_type_02" ); // 카드구분2
                    card_mny = c_PayPlus.mf_get_res( "card_mny" ); // 카드결제금액

                    /* = -------------------------------------------------------------- = */
                    /* =   06-1.1. 복합결제(포인트+신용카드) 승인 결과 처리             = */
                    /* = -------------------------------------------------------------- = */
                    if ( pnt_issue.equals( "SCSK" ) || pnt_issue.equals( "SCWB" ) )
                    {
                        pnt_amount   = c_PayPlus.mf_get_res( "pnt_amount"   ); // 적립금액 or 사용금액
                        pnt_app_time = c_PayPlus.mf_get_res( "pnt_app_time" ); // 승인시간
                        pnt_app_no   = c_PayPlus.mf_get_res( "pnt_app_no"   ); // 승인번호
                        add_pnt      = c_PayPlus.mf_get_res( "add_pnt"      ); // 발생 포인트
                        use_pnt      = c_PayPlus.mf_get_res( "use_pnt"      ); // 사용가능 포인트
                        rsv_pnt      = c_PayPlus.mf_get_res( "rsv_pnt"      ); // 총 누적 포인트
                        total_amount = amount + pnt_amount;                    // 복합결제시 총 거래금액
                    }
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-2. 계좌이체 승인 결과 처리                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals("010000000000") )
                {
                    app_time  = c_PayPlus.mf_get_res( "app_time"  ); // 승인시간
                    bank_name = c_PayPlus.mf_get_res( "bank_name" ); // 은행명
                    bank_code = c_PayPlus.mf_get_res( "bank_code" ); // 은행코드
                    bk_mny    = c_PayPlus.mf_get_res( "bk_mny"    ); // 계좌이체결제금액
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-3. 가상계좌 승인 결과 처리                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "001000000000" ) )
                {
                    bankname  = c_PayPlus.mf_get_res( "bankname"  ); // 입금할 은행 이름
                    depositor = c_PayPlus.mf_get_res( "depositor" ); // 입금할 계좌 예금주
                    account   = c_PayPlus.mf_get_res( "account"   ); // 입금할 계좌 번호
                    va_date   = c_PayPlus.mf_get_res( "va_date"   ); // 가상계좌 입금마감시간
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-4. 포인트 승인 결과 처리                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000100000000" ) )
                {
                    pnt_amount   = c_PayPlus.mf_get_res( "pnt_amount"   ); // 적립금액 or 사용금액
                    pnt_app_time = c_PayPlus.mf_get_res( "pnt_app_time" ); // 승인시간
                    pnt_app_no   = c_PayPlus.mf_get_res( "pnt_app_no"   ); // 승인번호
                    add_pnt      = c_PayPlus.mf_get_res( "add_pnt"      ); // 발생 포인트
                    use_pnt      = c_PayPlus.mf_get_res( "use_pnt"      ); // 사용가능 포인트
                    rsv_pnt      = c_PayPlus.mf_get_res( "rsv_pnt"      ); // 총 누적 포인트
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-5. 휴대폰 승인 결과 처리                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000010000000" ) )
                {
                    app_time = c_PayPlus.mf_get_res( "hp_app_time" ); // 승인 시간
                    commid   = c_PayPlus.mf_get_res( "commid"      ); // 통신사 코드
                    mobile_no= c_PayPlus.mf_get_res( "mobile_no"   ); // 휴대폰 번호
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-6. 상품권 승인 결과 처리                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000000001000" ) )
                {
                    app_time    = c_PayPlus.mf_get_res( "tk_app_time" ); // 승인 시간
                    tk_van_code = c_PayPlus.mf_get_res( "tk_van_code" ); // 발급사 코드
                    tk_app_no   = c_PayPlus.mf_get_res( "tk_app_no"   ); // 승인 번호
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-7. 현금영수증 승인 결과 처리                                          = */
                /* = -------------------------------------------------------------------------- = */
                cash_authno = c_PayPlus.mf_get_res( "cash_authno" ); // 현금영수증 승인번호
                cash_no     = c_PayPlus.mf_get_res( "cash_no"     ); // 현금영수증 거래번호
            }
        }
        /* = -------------------------------------------------------------------------- = */
        /* =   06. 승인 결과 처리 END                                                   = */
        /* ============================================================================== */


        /* = ========================================================================== = */
        /* =   07. 승인 및 실패 결과 DB 처리                                            = */
        /* = -------------------------------------------------------------------------- = */
        /* =      결과를 업체 자체적으로 DB 처리 작업하시는 부분입니다.                 = */
        /* = -------------------------------------------------------------------------- = */

        if ( req_tx.equals( "pay" ) )
        {

            /* = -------------------------------------------------------------------------- = */
            /* =   07-1. 승인 결과 DB 처리(res_cd == "0000")                                = */
            /* = -------------------------------------------------------------------------- = */
            /* =        각 결제수단을 구분하시어 DB 처리를 하시기 바랍니다.                 = */
            /* = -------------------------------------------------------------------------- = */
            if ( res_cd.equals( "0000" ) )
            {
                TbPayment payment = new TbPayment();
                payment.setAdvId(advertiserId);
                payment.setPayTno(tno);
                payment.setPayAmount(amount);
                payment.setPayPntIssue(pnt_issue);
                payment.setPayCouponMny(coupon_mny);

                // 07-1-1. 신용카드
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

                    // 07-1-1-1. 복합결제(신용카드+포인트)
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

                // 07-1-2. 계좌이체
                if ( use_pay_method.equals("010000000000") )
                {
                    payment.setPayAppTime(app_time);
                    payment.setPayBank_Name(bank_name);
                    payment.setPayBankCode(bank_code);
                    payment.setPayBkMny(bk_mny);
                }
                // 07-1-3. 가상계좌
                if ( use_pay_method.equals("001000000000") )
                {
                    payment.setPayBankname(bankname);
                    payment.setPayDepositor(depositor);
                    payment.setPayAccount(account);
                    payment.setPayVaDate(va_date);

                }
                // 07-1-4. 포인트
                if ( use_pay_method.equals("000100000000") )
                {
                    payment.setPayPntAmount(pnt_amount);
                    payment.setPayPntAppTime(pnt_app_time);
                    payment.setPayPntAppNo(pnt_app_no);
                    payment.setPayAddPnt(add_pnt);
                    payment.setPayUsePnt(use_pnt);
                    payment.setPayRsvPnt(rsv_pnt);
                }
                // 07-1-5. 휴대폰
                if ( use_pay_method.equals("000010000000") )
                {
                    payment.setPayAppTime(app_time);
                    payment.setPayCommid(commid);
                    payment.setPayMobileNo(mobile_no);
                }
                // 07-1-6. 상품권
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
                } catch (Exception e){
                    e.printStackTrace();
                    bSucc = "false";
                }
            }

            /* = -------------------------------------------------------------------------- = */
            /* =   07-2. 승인 실패 DB 처리(res_cd != "0000")                                = */
            /* = -------------------------------------------------------------------------- = */
            if( !"0000".equals ( res_cd ) )
            {
            }
        }
        /* = -------------------------------------------------------------------------- = */
        /* =   07. 승인 및 실패 결과 DB 처리 END                                        = */
        /* = ========================================================================== = */


        /* = ========================================================================== = */
        /* =   08. 승인 결과 DB 처리 실패시 : 자동취소                                  = */
        /* = -------------------------------------------------------------------------- = */
        /* =      승인 결과를 DB 작업 하는 과정에서 정상적으로 승인된 건에 대해         = */
        /* =      DB 작업을 실패하여 DB update 가 완료되지 않은 경우, 자동으로          = */
        /* =      승인 취소 요청을 하는 프로세스가 구성되어 있습니다.                   = */
        /* =                                                                            = */
        /* =      DB 작업이 실패 한 경우, bSucc 라는 변수(String)의 값을 "false"        = */
        /* =      로 설정해 주시기 바랍니다. (DB 작업 성공의 경우에는 "false" 이외의    = */
        /* =      값을 설정하시면 됩니다.)                                              = */
        /* = -------------------------------------------------------------------------- = */

        // 승인 결과 DB 처리 에러시 bSucc값을 false로 설정하여 거래건을 취소 요청
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

                    c_PayPlus.mf_set_us( mod_data_set_no, "tno",      tno      ); // KCP 원거래 거래번호
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_type", "STSC"   ); // 원거래 변경 요청 종류
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_ip",   cust_ip  ); // 변경 요청자 IP
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_desc", "가맹점 결과 처리 오류 - 가맹점에서 취소 요청"  ); // 변경 사유

                    c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, "", ordr_idxx, g_conf_log_level, "0" );

                    res_cd  = c_PayPlus.m_res_cd;                                 // 결과 코드
                    res_msg = c_PayPlus.m_res_msg;                                // 결과 메시지
                }
            }
        }
        // End of [res_cd = "0000"]
        /* = -------------------------------------------------------------------------- = */
        /* =   08. 승인 결과 DB 처리 END                                                = */
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
        /* =   02. 지불 요청 정보 설정                                                  = */
        /* = -------------------------------------------------------------------------- = */
        String req_tx         = f_get_parm( request_req_tx ); // 요청 종류
        String tran_cd        = f_get_parm( request_tran_cd ); // 처리 종류

        int advertiserId = Integer.parseInt(advId);
        int planIdInt = Integer.parseInt(planId);

        /* = -------------------------------------------------------------------------- = */
        String cust_ip        = f_get_parm( request.getRemoteAddr()                  ); // 요청 IP
        String ordr_idxx      = f_get_parm( request_ordr_idxx ); // 쇼핑몰 주문번호
        String good_name      = f_get_parm( request_good_name ); // 상품명
        /* = -------------------------------------------------------------------------- = */
        String res_cd         = "";                                                     // 응답코드
        String res_msg        = "";                                                     // 응답 메세지
        String tno            = f_get_parm( request_tno ); // KCP 거래 고유 번호
        /* = -------------------------------------------------------------------------- = */
        String buyr_name      = f_get_parm( request.getParameter( "buyr_name"      ) ); // 주문자명
        String buyr_tel1      = f_get_parm( request.getParameter( "buyr_tel1"      ) ); // 주문자 전화번호
        String buyr_tel2      = f_get_parm( request.getParameter( "buyr_tel2"      ) ); // 주문자 핸드폰 번호
        String buyr_mail      = f_get_parm( request.getParameter( "buyr_mail"      ) ); // 주문자 E-mail 주소
        /* = -------------------------------------------------------------------------- = */
        String use_pay_method = f_get_parm( request_use_pay_method ); // 결제 방법
        String bSucc          = "";                                                     // 업체 DB 처리 성공 여부
        /* = -------------------------------------------------------------------------- = */
        String app_time       = "";                                                     // 승인시간 (모든 결제 수단 공통)
        String amount         = "";                                                     // KCP 실제 거래금액
        String total_amount   = "0";                                                    // 복합결제시 총 거래금액
        String coupon_mny     = "";                                                     // 쿠폰금액
        /* = -------------------------------------------------------------------------- = */
        String card_cd        = "";                                                     // 신용카드 코드
        String card_name      = "";                                                     // 신용카드 명
        String app_no         = "";                                                     // 신용카드 승인번호
        String noinf          = "";                                                     // 신용카드 무이자 여부
        String quota          = "";                                                     // 신용카드 할부개월
        String partcanc_yn    = "";                                                     // 부분취소 가능유무
        String card_bin_type_01 = "";                                                   // 카드구분1
        String card_bin_type_02 = "";                                                   // 카드구분2
        String card_mny       = "";                                                     // 카드결제금액
        /* = -------------------------------------------------------------------------- = */
        String bank_name      = "";                                                     // 은행명
        String bank_code      = "";                                                     // 은행코드
        String bk_mny         = "";                                                     // 계좌이체결제금액
        /* = -------------------------------------------------------------------------- = */
        String bankname       = "";                                                     // 입금 은행명
        String depositor      = "";                                                     // 입금 계좌 예금주 성명
        String account        = "";                                                     // 입금 계좌 번호
        String va_date        = "";                                                     // 가상계좌 입금마감시간
        /* = -------------------------------------------------------------------------- = */
        String pnt_issue      = "";                                                     // 결제 포인트사 코드
        String pnt_amount     = "";                                                     // 적립금액 or 사용금액
        String pnt_app_time   = "";                                                     // 승인시간
        String pnt_app_no     = "";                                                     // 승인번호
        String add_pnt        = "";                                                     // 발생 포인트
        String use_pnt        = "";                                                     // 사용가능 포인트
        String rsv_pnt        = "";                                                     // 총 누적 포인트
        /* = -------------------------------------------------------------------------- = */
        String commid         = "";                                                     // 통신사코드
        String mobile_no      = "";                                                     // 휴대폰번호
        /* = -------------------------------------------------------------------------- = */
        String shop_user_id   = f_get_parm( request_shop_user_id ); // 가맹점 고객 아이디
        String tk_van_code    = "";                                                     // 발급사코드
        String tk_app_no      = "";                                                     // 승인번호
        /* = -------------------------------------------------------------------------- = */
        String cash_yn        = f_get_parm( request_cash_yn ); // 현금 영수증 등록 여부
        String cash_authno    = "";                                                     // 현금 영수증 승인 번호
        String cash_tr_code   = f_get_parm( request_cash_tr_code ); // 현금 영수증 발행 구분
        String cash_id_info   = f_get_parm( request_cash_id_info ); // 현금 영수증 등록 번호
        String cash_no        = "";                                                     // 현금 영수증 거래 번호


        /* ============================================================================== */
        /* =   02. 지불 요청 정보 설정 END
        /* ============================================================================== */


        /* ============================================================================== */
        /* =   03. 인스턴스 생성 및 초기화(변경 불가)                                   = */
        /* = -------------------------------------------------------------------------- = */
        /* =       결제에 필요한 인스턴스를 생성하고 초기화 합니다.                     = */
        /* = -------------------------------------------------------------------------- = */
        /*String g_conf_gw_url    = "testpaygw.kcp.co.kr";
        String g_conf_gw_port   = "8090";        // 포트번호(변경불가)
        int    g_conf_tx_mode   = 0;             // 변경불가
        String g_conf_log_dir   = "C:\\Tomcat\\apache-tomcat-8.5.64\\logs"; // LOG 디렉토리 절대경로 입력
        String g_conf_site_cd   = "T0000";
        String g_conf_site_key  = "3grptw1.zW0GSo4PQdaGvsF__";
        String g_conf_log_level = "3";*/

        String g_conf_gw_url    = "paygw.kcp.co.kr";
        String g_conf_log_dir   = "C:\\Tomcat\\apache-tomcat-8.5.64\\logs"; // LOG 디렉토리 절대경로 입력
        String g_conf_site_cd   = "ABJXF";
        String g_conf_site_key  = "0YrV3xlFox8xxG-35Ldbi1x__";
        String g_conf_log_level = "3";
        String g_conf_gw_port   = "8090";        // 포트번호(변경불가)
        int    g_conf_tx_mode   = 0;             // 변경불가



        J_PP_CLI_N c_PayPlus = new J_PP_CLI_N();

        c_PayPlus.mf_init( "", g_conf_gw_url, g_conf_gw_port, g_conf_tx_mode, g_conf_log_dir );
        c_PayPlus.mf_init_set();

        /* ============================================================================== */
        /* =   03. 인스턴스 생성 및 초기화 END                                          = */
        /* ============================================================================== */


        /* ============================================================================== */
        /* =   04. 처리 요청 정보 설정                                                  = */
        /* = -------------------------------------------------------------------------- = */
        /* = -------------------------------------------------------------------------- = */
        /* =   04-1. 승인 요청 정보 설정                                                = */
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
        }; // 요청 종류
        /* = -------------------------------------------------------------------------- = */
        /* =   04. 처리 요청 정보 설정 END                                              = */
        /* = ========================================================================== = */


        /* = ========================================================================== = */
        /* =   05. 실행                                                                 = */
        /* = -------------------------------------------------------------------------- = */
        if ( tran_cd.length() > 0 )
        {
            c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, "", ordr_idxx, g_conf_log_level, "0" );
        }
        else
        {
            c_PayPlus.m_res_cd  = "9562";
            c_PayPlus.m_res_msg = "연동 오류|tran_cd값이 설정되지 않았습니다.";
        }

        res_cd  = c_PayPlus.m_res_cd;  // 결과 코드
        res_msg = c_PayPlus.m_res_msg; // 결과 메시지
        /* = -------------------------------------------------------------------------- = */
        /* =   05. 실행 END                                                             = */
        /* ============================================================================== */

        /* ============================================================================== */
        /* =   06. 승인 결과 값 추출                                                    = */
        /* = -------------------------------------------------------------------------- = */
        if ( req_tx.equals( "pay" ) )
        {
            if ( res_cd.equals( "0000" ) )
            {
                tno       = c_PayPlus.mf_get_res( "tno"       ); // KCP 거래 고유 번호
                amount    = c_PayPlus.mf_get_res( "amount"    ); // KCP 실제 거래 금액
                pnt_issue = c_PayPlus.mf_get_res( "pnt_issue" ); // 결제 포인트사 코드
                coupon_mny = c_PayPlus.mf_get_res( "coupon_mny" ); // 쿠폰금액

                /* = -------------------------------------------------------------------------- = */
                /* =   06-1. 신용카드 승인 결과 처리                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "100000000000" ) )
                {
                    card_cd   = c_PayPlus.mf_get_res( "card_cd"   ); // 카드사 코드
                    card_name = c_PayPlus.mf_get_res( "card_name" ); // 카드사 명
                    app_time  = c_PayPlus.mf_get_res( "app_time"  ); // 승인시간
                    app_no    = c_PayPlus.mf_get_res( "app_no"    ); // 승인번호
                    noinf     = c_PayPlus.mf_get_res( "noinf"     ); // 무이자 여부
                    quota     = c_PayPlus.mf_get_res( "quota"     ); // 할부 개월 수
                    partcanc_yn = c_PayPlus.mf_get_res( "partcanc_yn"     ); // 부분취소 가능유무
                    card_bin_type_01 = c_PayPlus.mf_get_res( "card_bin_type_01" ); // 카드구분1
                    card_bin_type_02 = c_PayPlus.mf_get_res( "card_bin_type_02" ); // 카드구분2
                    card_mny = c_PayPlus.mf_get_res( "card_mny" ); // 카드결제금액

                    /* = -------------------------------------------------------------- = */
                    /* =   06-1.1. 복합결제(포인트+신용카드) 승인 결과 처리             = */
                    /* = -------------------------------------------------------------- = */
                    if ( pnt_issue.equals( "SCSK" ) || pnt_issue.equals( "SCWB" ) )
                    {
                        pnt_amount   = c_PayPlus.mf_get_res( "pnt_amount"   ); // 적립금액 or 사용금액
                        pnt_app_time = c_PayPlus.mf_get_res( "pnt_app_time" ); // 승인시간
                        pnt_app_no   = c_PayPlus.mf_get_res( "pnt_app_no"   ); // 승인번호
                        add_pnt      = c_PayPlus.mf_get_res( "add_pnt"      ); // 발생 포인트
                        use_pnt      = c_PayPlus.mf_get_res( "use_pnt"      ); // 사용가능 포인트
                        rsv_pnt      = c_PayPlus.mf_get_res( "rsv_pnt"      ); // 총 누적 포인트
                        total_amount = amount + pnt_amount;                    // 복합결제시 총 거래금액
                    }
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-2. 계좌이체 승인 결과 처리                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals("010000000000") )
                {
                    app_time  = c_PayPlus.mf_get_res( "app_time"  ); // 승인시간
                    bank_name = c_PayPlus.mf_get_res( "bank_name" ); // 은행명
                    bank_code = c_PayPlus.mf_get_res( "bank_code" ); // 은행코드
                    bk_mny    = c_PayPlus.mf_get_res( "bk_mny"    ); // 계좌이체결제금액
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-3. 가상계좌 승인 결과 처리                                            = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "001000000000" ) )
                {
                    bankname  = c_PayPlus.mf_get_res( "bankname"  ); // 입금할 은행 이름
                    depositor = c_PayPlus.mf_get_res( "depositor" ); // 입금할 계좌 예금주
                    account   = c_PayPlus.mf_get_res( "account"   ); // 입금할 계좌 번호
                    va_date   = c_PayPlus.mf_get_res( "va_date"   ); // 가상계좌 입금마감시간
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-4. 포인트 승인 결과 처리                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000100000000" ) )
                {
                    pnt_amount   = c_PayPlus.mf_get_res( "pnt_amount"   ); // 적립금액 or 사용금액
                    pnt_app_time = c_PayPlus.mf_get_res( "pnt_app_time" ); // 승인시간
                    pnt_app_no   = c_PayPlus.mf_get_res( "pnt_app_no"   ); // 승인번호
                    add_pnt      = c_PayPlus.mf_get_res( "add_pnt"      ); // 발생 포인트
                    use_pnt      = c_PayPlus.mf_get_res( "use_pnt"      ); // 사용가능 포인트
                    rsv_pnt      = c_PayPlus.mf_get_res( "rsv_pnt"      ); // 총 누적 포인트
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-5. 휴대폰 승인 결과 처리                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000010000000" ) )
                {
                    app_time = c_PayPlus.mf_get_res( "hp_app_time" ); // 승인 시간
                    commid   = c_PayPlus.mf_get_res( "commid"      ); // 통신사 코드
                    mobile_no= c_PayPlus.mf_get_res( "mobile_no"   ); // 휴대폰 번호
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-6. 상품권 승인 결과 처리                                              = */
                /* = -------------------------------------------------------------------------- = */
                if ( use_pay_method.equals( "000000001000" ) )
                {
                    app_time    = c_PayPlus.mf_get_res( "tk_app_time" ); // 승인 시간
                    tk_van_code = c_PayPlus.mf_get_res( "tk_van_code" ); // 발급사 코드
                    tk_app_no   = c_PayPlus.mf_get_res( "tk_app_no"   ); // 승인 번호
                }

                /* = -------------------------------------------------------------------------- = */
                /* =   06-7. 현금영수증 승인 결과 처리                                          = */
                /* = -------------------------------------------------------------------------- = */
                cash_authno = c_PayPlus.mf_get_res( "cash_authno" ); // 현금영수증 승인번호
                cash_no     = c_PayPlus.mf_get_res( "cash_no"     ); // 현금영수증 거래번호
            }
        }
        /* = -------------------------------------------------------------------------- = */
        /* =   06. 승인 결과 처리 END                                                   = */
        /* ============================================================================== */


        /* = ========================================================================== = */
        /* =   07. 승인 및 실패 결과 DB 처리                                            = */
        /* = -------------------------------------------------------------------------- = */
        /* =      결과를 업체 자체적으로 DB 처리 작업하시는 부분입니다.                 = */
        /* = -------------------------------------------------------------------------- = */

        if ( req_tx.equals( "pay" ) )
        {

            /* = -------------------------------------------------------------------------- = */
            /* =   07-1. 승인 결과 DB 처리(res_cd == "0000")                                = */
            /* = -------------------------------------------------------------------------- = */
            /* =        각 결제수단을 구분하시어 DB 처리를 하시기 바랍니다.                 = */
            /* = -------------------------------------------------------------------------- = */
            if ( res_cd.equals( "0000" ) )
            {
                TbPayment payment = new TbPayment();
                payment.setAdvId(advertiserId);
                payment.setPayTno(tno);
                payment.setPayAmount(amount);
                payment.setPayPntIssue(pnt_issue);
                payment.setPayCouponMny(coupon_mny);

                // 07-1-1. 신용카드
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

                    // 07-1-1-1. 복합결제(신용카드+포인트)
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

                // 07-1-2. 계좌이체
                if ( use_pay_method.equals("010000000000") )
                {
                    payment.setPayAppTime(app_time);
                    payment.setPayBank_Name(bank_name);
                    payment.setPayBankCode(bank_code);
                    payment.setPayBkMny(bk_mny);
                }
                // 07-1-3. 가상계좌
                if ( use_pay_method.equals("001000000000") )
                {
                    payment.setPayBankname(bankname);
                    payment.setPayDepositor(depositor);
                    payment.setPayAccount(account);
                    payment.setPayVaDate(va_date);

                }
                // 07-1-4. 포인트
                if ( use_pay_method.equals("000100000000") )
                {
                    payment.setPayPntAmount(pnt_amount);
                    payment.setPayPntAppTime(pnt_app_time);
                    payment.setPayPntAppNo(pnt_app_no);
                    payment.setPayAddPnt(add_pnt);
                    payment.setPayUsePnt(use_pnt);
                    payment.setPayRsvPnt(rsv_pnt);
                }
                // 07-1-5. 휴대폰
                if ( use_pay_method.equals("000010000000") )
                {
                    payment.setPayAppTime(app_time);
                    payment.setPayCommid(commid);
                    payment.setPayMobileNo(mobile_no);
                }
                // 07-1-6. 상품권
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
                } catch (Exception e){
                    e.printStackTrace();
                    bSucc = "false";
                }
            }

            /* = -------------------------------------------------------------------------- = */
            /* =   07-2. 승인 실패 DB 처리(res_cd != "0000")                                = */
            /* = -------------------------------------------------------------------------- = */
            if( !"0000".equals ( res_cd ) )
            {
            }
        }
        /* = -------------------------------------------------------------------------- = */
        /* =   07. 승인 및 실패 결과 DB 처리 END                                        = */
        /* = ========================================================================== = */


        /* = ========================================================================== = */
        /* =   08. 승인 결과 DB 처리 실패시 : 자동취소                                  = */
        /* = -------------------------------------------------------------------------- = */
        /* =      승인 결과를 DB 작업 하는 과정에서 정상적으로 승인된 건에 대해         = */
        /* =      DB 작업을 실패하여 DB update 가 완료되지 않은 경우, 자동으로          = */
        /* =      승인 취소 요청을 하는 프로세스가 구성되어 있습니다.                   = */
        /* =                                                                            = */
        /* =      DB 작업이 실패 한 경우, bSucc 라는 변수(String)의 값을 "false"        = */
        /* =      로 설정해 주시기 바랍니다. (DB 작업 성공의 경우에는 "false" 이외의    = */
        /* =      값을 설정하시면 됩니다.)                                              = */
        /* = -------------------------------------------------------------------------- = */

        // 승인 결과 DB 처리 에러시 bSucc값을 false로 설정하여 거래건을 취소 요청
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

                    c_PayPlus.mf_set_us( mod_data_set_no, "tno",      tno      ); // KCP 원거래 거래번호
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_type", "STSC"   ); // 원거래 변경 요청 종류
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_ip",   cust_ip  ); // 변경 요청자 IP
                    c_PayPlus.mf_set_us( mod_data_set_no, "mod_desc", "가맹점 결과 처리 오류 - 가맹점에서 취소 요청"  ); // 변경 사유

                    c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, "", ordr_idxx, g_conf_log_level, "0" );

                    res_cd  = c_PayPlus.m_res_cd;                                 // 결과 코드
                    res_msg = c_PayPlus.m_res_msg;                                // 결과 메시지
                }
            }
        }
        // End of [res_cd = "0000"]
        /* = -------------------------------------------------------------------------- = */
        /* =   08. 승인 결과 DB 처리 END                                                = */
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