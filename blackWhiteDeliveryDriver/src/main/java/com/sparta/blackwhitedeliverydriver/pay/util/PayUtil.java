package com.sparta.blackwhitedeliverydriver.pay.util;

import com.sparta.blackwhitedeliverydriver.model.order.entity.Order;
import com.sparta.blackwhitedeliverydriver.model.pay.entity.Pay;
import com.sparta.blackwhitedeliverydriver.model.user.entity.User;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class PayUtil {
    static final String CID = "TC0ONETIME";//테스트 코드

    @Value("${pay.key}")
    private String secretKey;

    @Value("${pay.domain}")
    private String domain;

    public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "SECRET_KEY " + secretKey;
        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/json");
        return httpHeaders;
    }

    public Map<String, String> getReadyPayParameters(User user, Order order) {
        // 카카오페이 요청 양식
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("partner_order_id", order.getId().toString());
        parameters.put("partner_user_id", user.getUsername());
        parameters.put("item_name", "흑백 기사 배달 주문");
        parameters.put("quantity", "1");
        parameters.put("total_amount", order.getFinalPay().toString());
        parameters.put("vat_amount", "0");
        parameters.put("tax_free_amount", "0");
        parameters.put("approval_url", domain + "/payment/success"); // 성공 시 redirect url -> 이 부분을 프론트엔드 url로 바꿔주어야 함
        parameters.put("cancel_url", domain + "/payment/fail"); // 취소 시 redirect url -> 서버의 주소
        parameters.put("fail_url", domain + "/payment/cancel"); // 실패 시 redirect url -> 서버의 주소
        // redirect url의 경우 나중에 연동시 프론트에서의 URL을 입력해주고 , 꼭 내가 도메인 변경을 해주어야 한다.

        return parameters;
    }

    public Map<String, String> getApprovePayParameters(String tid, String pgToken, Order order) {
        // 카카오 요청
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("tid", tid);
        parameters.put("partner_order_id", order.getId().toString());
        parameters.put("partner_user_id", order.getUser().getUsername());
        parameters.put("pg_token", pgToken);
        return parameters;
    }

    public Map<String, String> getAutoRefund(Pay pay) {
        // 카카오페이 요청
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("tid", pay.getTid());
        parameters.put("cancel_amount", pay.getPayAmount().toString());
        parameters.put("cancel_tax_free_amount", Integer.toString(0));
        return parameters;
    }

    public Map<String, String> getRefundParameters(Pay pay, int cancelAmount) {
        // 카카오페이 요청
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", CID);
        parameters.put("tid", pay.getTid());
        parameters.put("cancel_amount", Integer.toString(cancelAmount));
        parameters.put("cancel_tax_free_amount", Integer.toString(0));
        return parameters;
    }

}
