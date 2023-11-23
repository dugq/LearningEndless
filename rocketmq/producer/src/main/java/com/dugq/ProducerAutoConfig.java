package com.dugq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2023/10/27.
 */
@Configuration
public class ProducerAutoConfig {
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

    @Bean
    public MQProducer getMQProducer() throws MQClientException {
        // 初始化一个producer并设置Producer group name
        DefaultMQProducer producer = new DefaultMQProducer(rocketMqProps.getProducer().getGroupName()); //（1）
        // 设置NameServer地址
        producer.setNamesrvAddr(rocketMqProps.getProducer().getNameServerHost());  //（2）
        producer.setAsyncSenderExecutor(executorService);
        // 高可用的关键。在随机队列进行负载均衡时，执行退避策略：对于之前不可用的broker，暂停一段时间不可用。具体时间随着失败次数递增
        producer.setSendLatencyFaultEnable(true);
        // 设置命名空间。可用于隔离业务
        producer.setNamespace("testNameSp");
        // 当发送失败时，是否切换broker发送。 只对同步发送生效。默认不生效
        producer.setRetryAnotherBrokerWhenNotStoreOK(true);
        // 此属性配合上面属性使用。配置切换broker次数
        producer.setRetryTimesWhenSendFailed(2);
        // 一定生效。默认两次
        producer.setRetryTimesWhenSendAsyncFailed(2);
        // 启动producer
        producer.start();
        return producer;
    }

    @Bean
    public TransactionMQProducer getTransactionProducer() throws MQClientException {
        // 初始化一个producer并设置Producer group name
        TransactionMQProducer producer = new TransactionMQProducer(rocketMqProps.getProducer().getGroupName()); //（1）
        // 设置NameServer地址
        producer.setNamesrvAddr(rocketMqProps.getProducer().getNameServerHost());  //（2）
        producer.setAsyncSenderExecutor(executorService);
        // 高可用的关键。在随机队列进行负载均衡时，执行退避策略：对于之前不可用的broker，暂停一段时间不可用。具体时间随着失败次数递增
        producer.setSendLatencyFaultEnable(true);
        // 设置命名空间。可用于隔离业务
        producer.setNamespace("testNameSp");
        // 当发送失败时，是否切换broker发送。 只对同步发送生效。默认不生效
        producer.setRetryAnotherBrokerWhenNotStoreOK(true);
        // 此属性配合上面属性使用。配置切换broker次数
        producer.setRetryTimesWhenSendFailed(2);
        // 一定生效。默认两次
        producer.setRetryTimesWhenSendAsyncFailed(2);
        // 启动producer
        producer.start();
        return producer;
    }

}
