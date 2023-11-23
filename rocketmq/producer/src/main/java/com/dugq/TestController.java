package com.dugq;

import com.dugq.pojo.User;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dugq on 2023/10/27.
 */
@RestController
@RequestMapping("/mq/test")
public class TestController {
    @Resource
    private ProducerSender producerSender;
    @Resource
    private RocketMqProps rocketMqProps;


    @GetMapping("/commonMsg")
    public MqResult commonMsg(String name, Long id){
        User user = new User(name,id);
        SendResult result = producerSender.sendCommonMsgSync(rocketMqProps.getCommonTopic(), "common1", user);
        return MqResult.of(result);
    }

    @GetMapping("/delayMsg")
    public MqResult delayMsg(String name, Long id,int level){
        User user = new User(name,id);
        SendResult result = producerSender.sendDelayMsgSync(rocketMqProps.getCommonTopic(), "delayTag",level, user);
        return MqResult.of(result);
    }

    @GetMapping("/batchMsg")
    public MqResult batchSendMsgSync(String namePrefix, Long start,int size){
        List<User> list = new ArrayList<>(size);
        for (int i =0 ; i < size; i++){
            Long id = start + i;
            list.add(new User(namePrefix+id,id));
        }
        SendResult result = producerSender.batchSendMsgSync(rocketMqProps.getCommonTopic(),list);
        return MqResult.of(result);
    }


}
