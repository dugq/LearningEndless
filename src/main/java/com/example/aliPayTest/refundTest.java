package com.example.aliPayTest;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import org.junit.jupiter.api.Test;

/**
 * Created by dugq on 2018/7/23.
 */
public class refundTest {
    @Test
    public void test() throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",AlipayUtil.partner,AlipayUtil.private_key,"json",AlipayUtil.input_charset,AlipayUtil.ali_public_key,"RSA2");
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        request.setBizContent("{" +
                "\"trade_no\":\"20150320010101001\"," +
                "\"out_trade_no\":\"2014112611001004680073956707\"," +
                "\"out_request_no\":\"2014112611001004680073956707\"" +
                "  }");
        AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }


    }

}
