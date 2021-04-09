<%@ page language="java" contentType="text/html;charset=euc-kr"%>
<%
    /* ============================================================================== */
    /* =   PAGE : 결제 결과 출력 PAGE                                               = */
    /* = -------------------------------------------------------------------------- = */
    /* =   결제 요청 결과값을 출력하는 페이지입니다.                                = */
    /* = -------------------------------------------------------------------------- = */
    /* =   연동시 오류가 발생하는 경우 아래의 주소로 접속하셔서 확인하시기 바랍니다.= */
    /* =   접속 주소 : http://kcp.co.kr/technique.requestcode.do                    = */
    /* = -------------------------------------------------------------------------- = */
    /* =   Copyright (c)  2016  NHN KCP Inc.   All Rights Reserverd.                = */
    /* ============================================================================== */
%>
<%!
    /* ============================================================================== */
    /* =   null 값을 처리하는 메소드                                                = */
    /* = -------------------------------------------------------------------------- = */
    public String f_get_parm( String val )
    {
        if ( val == null ) val = "";
        return  val;
    }
    /* ============================================================================== */
%>
<%
    request.setCharacterEncoding("euc-kr") ;
    /* ============================================================================== */
    /* =   지불 결과                                                                = */
    /* = -------------------------------------------------------------------------- = */
    String req_tx           = f_get_parm( request.getParameter( "req_tx"         ) );      // 요청 구분(승인/취소)
    String bSucc            = f_get_parm( request.getParameter( "bSucc"          ) );      // 업체 DB 정상처리 완료 여부
    /* = -------------------------------------------------------------------------- = */
    String res_cd           = f_get_parm( request.getParameter( "res_cd"         ) );      // 결과 코드
    String res_msg_bsucc    = "";
    /* ============================================================================== */

    if ( "pay".equals ( req_tx ) )
    {
        // 업체 DB 처리 실패
        if ( "false".equals ( bSucc ) )
        {
            if ( "0000".equals ( res_cd ) )
            {
                res_msg_bsucc = "결제는 정상적으로 이루어졌지만 업체에서 결제 결과를 처리하는 중 오류가 발생하여 시스템에서 자동으로 취소 요청을 하였습니다. <br> 업체로 전화하여 확인하시기 바랍니다." ;
            }
            else
            {
                res_msg_bsucc = "결제는 정상적으로 이루어졌지만 업체에서 결제 결과를 처리하는 중 오류가 발생하여 시스템에서 자동으로 취소 요청을 하였으나, <br> <b>취소가 실패 되었습니다.</b><br> 업체로 전화하여 확인하시기 바랍니다." ;
            }
        }
    }

    /* = -------------------------------------------------------------------------- = */
    /* =   가맹점 측 DB 처리 실패시 상세 결과 메시지 설정 끝                        = */
    /* ============================================================================== */

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" >

<head>
    <title>*** NHN KCP [AX-HUB Version] ***</title>
    <meta http-equiv="Content-Type" content="text/html; charset=euc-kr" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="-1">
    <link href="payment/css/style.css" rel="stylesheet" type="text/css" id="cssLink"/>
    <script type="text/javascript">
        /* 신용카드 영수증 */
        /* 실결제시 : "https://admin8.kcp.co.kr/assist/bill.BillActionNew.do?cmd=card_bill&tno=" */
        /* 테스트시 : "https://testadmin8.kcp.co.kr/assist/bill.BillActionNew.do?cmd=card_bill&tno=" */
        function receiptView( tno, ordr_idxx, amount )
        {
            receiptWin = "https://admin8.kcp.co.kr/assist/bill.BillActionNew.do?cmd=card_bill&tno=";
            receiptWin += tno + "&";
            receiptWin += "order_no=" + ordr_idxx + "&";
            receiptWin += "trade_mony=" + amount ;

            window.open(receiptWin, "", "width=455, height=815");
        }

        /* 현금 영수증 */
        /* 실결제시 : "https://admin8.kcp.co.kr/assist/bill.BillActionNew.do" */
        /* 테스트시 : "https://testadmin8.kcp.co.kr/assist/bill.BillActionNew.do" */
        function receiptView2( cash_no, ordr_idxx, amount )
        {
            receiptWin2 = "https://testadmin8.kcp.co.kr/assist/bill.BillActionNew.do?cmd=cash_bill&cash_no=";
            receiptWin2 += cash_no + "&";
            receiptWin2 += "order_id="     + ordr_idxx + "&";
            receiptWin2 += "trade_mony="  + amount ;

            window.open(receiptWin2, "", "width=370, height=625");
        }

        /* 가상 계좌 모의입금 페이지 호출 */
        /* 테스트시에만 사용가능 */
        /* 실결제시 해당 스크립트 주석처리 */
        function receiptView3()
        {
            receiptWin3 = "http://devadmin.kcp.co.kr/Modules/Noti/TEST_Vcnt_Noti.jsp";
            window.open(receiptWin3, "", "width=520, height=300");
        }
    </script>
</head>

<body>
<form name="cancel" method="post">
    <div id="sample_wrap">
        <h1><span>결제 결과</span></h1>
        <div class="sample" style="padding: 15px; box-sizing: border-box; width:500px;">

            <%
                if ( ! "false".equals ( bSucc ) &&  "0000".equals(res_cd)) {
            %>

            <h2 style="font-size: 16px; margin: 20px 0; text-align: center;">결제 완료되었습니다</h2>

            <div class="btnset" style="margin: 15px 0 0 0;">
                <a href="/payment/success" class="home">확인</a>
            </div>

            <%
                } else if("false".equals ( bSucc ) &&  "0000".equals(res_cd)) {
            %>

            <h2 style="font-size: 16px; margin: 20px 0; text-align: center">
                <%=res_msg_bsucc%>
            </h2>

            <div class="btnset" style="margin: 15px 0 0 0;">
                <a href="/payment/failed" class="home">닫기</a>
            </div>

            <%
            } else {
            %>

            <h2 style="font-size: 16px; margin: 20px 0; text-align: center; color: red">결제 실패되었습니다</h2>

            <div class="btnset" style="margin: 15px 0 0 0;">
                <a href="/payment/failed" class="home">닫기</a>
            </div>

            <%
                }
            %>
        </div>
        <div class="footer">
            Copyright (c) NHN KCP INC. All Rights reserved.
        </div>
    </div>
</body>
</html>
