package com.light.cyan.design.model.structural;

import lombok.Getter;
import lombok.Setter;

/**
 * 桥接模式 Notifier 接口允许消息内容和发送方式都可以变化
 */
public class WrapperModelTest {

    interface Notifier{
        Result send(Message message);
    }

    class SMSNotifier implements Notifier{

        @Override
        public Result send(Message message) {
            return new SMSResult();
        }
    }

    class QQNotifier implements Notifier{

        @Override
        public Result send(Message message) {
            return null;
        }
    }

    //包装
    class QQAndSMSDecorator implements Notifier{
        private SMSNotifier smsNotifier;
        private QQNotifier qqNotifier;

        @Override
        public Result send(Message message) {
            Result send = smsNotifier.send(message);
            Result send2 = qqNotifier.send(message);
            return new TupleResult(send,send2);
        }
    }

    //再包装
    class WechatDecorator extends QQAndSMSDecorator{
        @Override
        public Result send(Message message) {
             super.send(message);
             // 微信消息发送逻辑
            return new Result();
        }
    }





    static interface Message{
       String getAccount();
       String getBody();
    }

    @Getter
    static class BaseMessage implements Message{
        private String account;
        private String body;

    }

    static class SMSMessage extends BaseMessage{
        private String phoneNumber;
    }

    static class Result{
        private String status;
        private String message;
    }

    static class SMSResult extends Result{

    }

    @Setter
    static class TupleResult extends Result{
        private Result result1;
        private Result result2;

        public TupleResult(Result send, Result send2) {
            this.result1 = send;
            this.result2 = send2;
        }
    }




}
