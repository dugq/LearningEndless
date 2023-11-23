package com.dugq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by dugq on 2023/10/27.
 */
@Service
@Slf4j
public class ProducerSender {
    private MQProducer MQProducer;

    public SendResult sendCommonMsgSync(String topic, String tag, Object body) {
        Message msg = null; /* Message body */
        try {
            msg = new Message("TopicTest" /* Topic */,
                    "TagA" /* Tag */,
                    JSON.toJSONString(body).getBytes(RemotingHelper.DEFAULT_CHARSET));
            return MQProducer.send(msg);   //（4）
        } catch (Exception e) {
            log.error("",e);
        }
        return null;
    }

    public CompletableFuture<SendResult> sendCommonMsgAsync(String topic, String tag, Object body) {
        CompletableFuture<SendResult> future =new CompletableFuture<>();
            Message msg = null; /* Message body */
            try {
                msg = new Message("TopicTest" /* Topic */,
                        "TagA" /* Tag */,
                        JSON.toJSONString(body).getBytes(RemotingHelper.DEFAULT_CHARSET));
                MQProducer.send(msg, new SendCallback() {

                    @Override
                    public void onSuccess(SendResult sendResult) {
                        future.complete(sendResult);
                    }

                    @Override
                    public void onException(Throwable e) {
                        future.completeExceptionally(e);
                    }
                });   //（4）
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        return future;
    }

    private MessageQueueSelector selector = (mqs, msg, arg) -> {
        Integer id = arg.hashCode();
        int index = id % mqs.size();
        return mqs.get(index);
    };

    /**
     * 顺序消息
     * 将消息 根据给定的分区值hash后，投放到固定的队列中。
     * 例如：根据用户ID进行分区，某一个用户的消息始终投递到同一个队列，从使得该用户的所有消息存储在同一个队列，消费者保持有序消费后，该队列即成为有序队列
     * 例如：partitionedValue 如果设置为常量，则所有消息投递到同一个队列，则所有消息都具有有序性
     * @param partitionedValue 分区值
     * @return
     */
    public SendResult sendSequentialMsgSync(String topic, String tag,Object partitionedValue, Object body) {
        Message msg = null; /* Message body */
        try {
            msg = new Message("TopicTest" /* Topic */,
                    "TagA" /* Tag */,
                    JSON.toJSONString(body).getBytes(RemotingHelper.DEFAULT_CHARSET));
            return MQProducer.send(msg,selector,partitionedValue);   //（4）
        } catch (Exception e) {
            log.error("",e);
        }
        return null;
    }

    public SendResult sendDelayMsgSync(String topic, String tag,int level, Object body) {
        if (level<1 || level > 18){
            throw new UnsupportedOperationException("level 只能设置在1-18之间");
        }
        Message msg = null; /* Message body */
        try {
            msg = new Message("TopicTest" /* Topic */,
                    "TagA" /* Tag */,
                    JSON.toJSONString(body).getBytes(RemotingHelper.DEFAULT_CHARSET));
            msg.setDelayTimeLevel(level);
            return MQProducer.send(msg);   //（4）
        } catch (Exception e) {
            log.error("",e);
        }
        return null;
    }

    public SendResult batchSendMsgSync(String topic, List<?> bodys) {
        if (CollectionUtils.isEmpty(bodys)){
            return null;
        }
        try {
            List<Message> messages = new ArrayList<>(bodys.size());
            for (Object body : bodys) {
                messages.add(new Message("TopicTest" /* Topic */,
                        "TagA" /* Tag */,
                        JSON.toJSONString(bodys).getBytes(RemotingHelper.DEFAULT_CHARSET)));
            }
            return MQProducer.send(messages);   //（4）
        } catch (Exception e) {
            log.error("",e);
        }
        return null;
    }

}
