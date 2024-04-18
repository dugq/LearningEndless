package com.dugq.messagecenter.server.handler.message;

import com.dugq.message.MessageContent;
import com.dugq.messagecenter.server.handler.connector.ConnectionManger;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2024/4/15.
 * 单线程，无锁化整个操作
 */
@Slf4j
public class MessageQueue extends SingleThreadEventExecutor {

    private int messageId ;

    private volatile Map<Integer,MessageContent> sendingList = new HashMap<>();

    private volatile Map<Integer,MessageWrap> sendedList = new HashMap<>();

    private volatile Map<Integer,MessageWrap> oldSendedList = new HashMap<>();

    private final ConnectionManger connectionManger;

    public MessageQueue(ConnectionManger connectionManger) {
        super(null, r -> {
            Thread thread = new Thread(r);
            thread.setName("my-send-message-thread");
            return thread;
        }, true);
        this.connectionManger = connectionManger;
        // 定时将消息写入文件
        scheduleAtFixedRate(this::consumerSendList,10,10, TimeUnit.SECONDS);
    }
    @Override
    protected void run() {
        for (;;) {
            try {
                Runnable task = takeTask();
                if (task != null) {
                    task.run();
                    updateLastExecutionTime();
                }
                if (confirmShutdown()) {
                    break;
                }
            }catch (Exception e){
                log.error("message queue consumer has exception:",e);
            }
        }
    }

    public void consumerMessageRequest(MessageRequest messageRequest) {
        MessageContent messageContent = messageRequest.getMessageContent();
        int id = messageId++;
        messageContent.setMessageId(id);
        if (messageContent.getType() == 0) {

        } else {
            addSendingList(messageContent);
        }
        DefaultPromise<Integer> defaultPromise = messageRequest.getDefaultPromise();
        if (defaultPromise != null) {
            defaultPromise.setSuccess(id);

        }
    }

    public void addSendingList(MessageContent content){
        if(sendingList.containsKey(content.getMessageId()) || sendedList.containsKey(content.getMessageId())){
            return;
        }
        sendingList.put(content.getMessageId(),content);
        //队列为空时，加一个容灾处理，200ms 后，无论如何一定要把消息推出去
        if (sendingList.size()==1){
            schedule(this::consumerSendingList,200,TimeUnit.MILLISECONDS);
        }
        if(sendingList.size()>10){
            consumerSendingList();
        }
    }

    public Map<Integer, MessageContent> swapSendingList(){
        Map<Integer, MessageContent> source = sendingList;
        sendingList = new HashMap<>();
        return source;
    }

    // 批量发送消息
    public void consumerSendingList(){
        if (sendingList.size() == 0){
            return;
        }
        Map<Integer, MessageContent> messageContents = swapSendingList();
        ArrayList<MessageContent> messages = new ArrayList<>(messageContents.values());
        connectionManger.batchSend(messages);
        addToSendedList(messageContents);
    }

    private void addToSendedList(Map<Integer, MessageContent> messageContents) {
        for (MessageContent value : messageContents.values()) {
            sendedList.put(value.getMessageId(),new MessageWrap(value));
        }
    }

    private Map<Integer, MessageWrap> swapSendedList(){
        Map<Integer, MessageWrap> list = sendedList;
        sendedList = new HashMap<>();
        return list;
    }

    public void consumerSendList() {
        // 前6s到前3s的可以入土了
        writeToFile(new ArrayList<>(oldSendedList.values()));
        if(sendedList.size()==0){
            return;
        }
        // 前3s现在的 转移到 前6s到前3s的,下一轮询再入土
        oldSendedList.putAll(swapSendedList());
    }

    private void writeToFile(List<MessageWrap> oldSendedList) {

    }

    public void addMessage(Channel channel ,MessageContent messageContent) {
        MessageRequest request = new MessageRequest(channel,this,messageContent.getType() == 1 ? 1 : 2,messageContent);
        addTask(()-> consumerMessageRequest(request));
    }

    public void updateResult(int id, int count){
        addTask(()->{
                MessageWrap messageContent = sendedList.get(id);
                if (Objects.nonNull(messageContent)){
                    messageContent.increaseReceivedNum(count);
                }
                messageContent = oldSendedList.get(id);
                if (Objects.nonNull(messageContent)){
                    messageContent.increaseReceivedNum(count);
                }
        });
    }


    public void location(List<Integer> messageIds, String location) {
        if (sendedList.size() == 0){
            return;
        }
        addTask(()->{
            for (Integer id : messageIds) {
                MessageWrap messageContent = sendedList.get(id);
                if (Objects.nonNull(messageContent)){
                    messageContent.setLocation(location);
                    continue;
                }
                messageContent = oldSendedList.get(id);
                if (Objects.nonNull(messageContent)){
                    messageContent.setLocation(location);
                }
            }

        });
    }

}
