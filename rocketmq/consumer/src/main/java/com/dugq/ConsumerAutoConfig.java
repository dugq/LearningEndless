package com.dugq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2023/10/27.
 */
@Configuration
@Slf4j
public class ConsumerAutoConfig {


    @Resource
    private RocketMqProps rocketMqProps;
    private ExecutorService executorService = new ThreadPoolExecutor(10, 10, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000), new ThreadFactory() {
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("mq-producer-async-pool");
        return thread;
    }
});

    //@PostConstruct
    public void buildPullConsumer() throws MQClientException {
        // 原始模式，一切靠自己.不常用了。
        DefaultMQPullConsumer defaultMQPullConsumer = new DefaultMQPullConsumer(rocketMqProps.getConsumer().getGroupName());
        /**
         *@see DefaultMQPullConsumer#pull(MessageQueue, String, long, int)
         * */


        // 初始化一个producer并设置Producer group name
        DefaultLitePullConsumer litePullConsumer = new DefaultLitePullConsumer(rocketMqProps.getConsumer().getGroupName()); //（1）
        litePullConsumer.setNamesrvAddr(rocketMqProps.getNameServerHost());
        litePullConsumer.subscribe("TopicTest", "*");


        // assign模式，和  subscribe模式类似，但是少了负载均衡的监听器。
        /**
         * @see DefaultLitePullConsumer#fetchMessageQueues
         * @see DefaultLitePullConsumer#assign
         */
         // Collection<MessageQueue> queues = litePullConsumer.fetchMessageQueues("TopicTest");
         // litePullConsumer.assign(queues);


        litePullConsumer.setPullBatchSize(20);
        // 可修改
        litePullConsumer.setMessageModel(MessageModel.CLUSTERING);
        litePullConsumer.start();
        //
        //litePullConsumer.setAutoCommit(false);
        try {
            while (true) {
                List<MessageExt> messageExts = litePullConsumer.poll();
                System.out.printf("%s%n", messageExts);
            }
        }catch (Exception e){

        }finally {
            litePullConsumer.shutdown();
        }
    }

    @PostConstruct
    public void buildPushConsumer() throws MQClientException {
        // 初始化consumer，并设置consumer group name
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketMqProps.getConsumer().getGroupName());

        // 设置NameServer地址
        consumer.setNamesrvAddr(rocketMqProps.getNameServerHost());
        //订阅一个或多个topic，并指定tag过滤条件，这里指定*表示接收所有tag的消息
        consumer.subscribe("TopicTest", "*");
        consumer.setPullBatchSize(10);
        //注册回调接口来处理从Broker中收到的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                // 返回消息消费状态，ConsumeConcurrentlyStatus.CONSUME_SUCCESS为消费成功
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动Consumer
        consumer.start();
        System.out.printf("Consumer Started.%n");
    }

}
