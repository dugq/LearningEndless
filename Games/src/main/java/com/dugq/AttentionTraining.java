package com.dugq;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by dugq on 2024/1/26.
 */
public class AttentionTraining {
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    public static void main(String[] args) {
        new AttentionTraining().generate();
    }


    private long startTime;

    private Button startButton = new Button("开始");

    private Button restartButton = new Button("重置");

    private JPanel body;

    private int pos;

    private JLabel timeMaker = new JLabel("0");

    private JLabel currentPos = new JLabel("0");

    private volatile boolean started = false;

    private JFrame frame = new JFrame();

    private int maxTime = Integer.MAX_VALUE;

    private volatile PriorityQueue<User> userRank = new PriorityQueue<>();

    private JPanel rankPanel;

    private Load load = new Load();
    public void generate() {
        load.init();
        frame.setTitle("注意力训练");
        frame.setSize(800, 600);

        JPanel rankPanel = buildRankPanel();
        frame.add(rankPanel);

        JPanel contentPane = buildContentPanel();
        frame.add(contentPane);
        frame.setVisible(true);

        executorService.execute(load::loadHistory);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowListener(){
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                load.saveHistory();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }


    private JPanel buildRankPanel() {
        this.rankPanel = new JPanel();
        rankPanel.setSize(200,600);
        genRank();
        return rankPanel;
    }

    private void genRank() {
        rankPanel.removeAll();
        rankPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        Iterator<User> iterator = userRank.iterator();
        int index = 0;
        gbc.fill= GridBagConstraints.NORTH;
        gbc.gridwidth = 2;
        gbc.ipady = 20;

        rankPanel.add(new JLabel("排行榜"),gbc);
        gbc.gridwidth = 1;
        gbc.ipady = 10;
        gbc.ipadx = 15;
        while (iterator.hasNext()) {
            User next = iterator.next();
            JLabel name = new JLabel(next.name);
            JLabel time = new JLabel(next.getScore());
            gbc.gridx = 0;
            gbc.gridy = index+1;
            rankPanel.add(name,gbc);

            gbc.gridx = 1;
            rankPanel.add(time,gbc);
            maxTime = next.time;
            if(++index==10){
                break;
            }
        }
        rankPanel.setVisible(true);
        rankPanel.updateUI();
    }

    private JPanel buildContentPanel() {
        JPanel contentPane = new JPanel();
        contentPane.setSize(600, 600);
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 游戏进程区域
        JPanel jPanel = genGameProcessPanel();
        gbc.fill= GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPane.add(jPanel,gbc);

        // 游戏区域
        body = genBody();
        gbc.fill= GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add(body,gbc);

        // 菜单区域
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPane.add(genMenu(),gbc);
        return contentPane;
    }

    public JPanel genGameProcessPanel(){
        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout());
        jp.add(new JLabel("时间："));
        jp.add(timeMaker);
        jp.add(new JLabel("进度："));
        jp.add(currentPos);
        return jp;
    }

    private JPanel genBody() {
        JPanel body = new JPanel();
        Border blackline = BorderFactory.createLineBorder(Color.black);
//        body.setBorder(blackline);
        body.setLayout(new GridLayout(5,5,1,1));
        body.setVisible(true);
        return body;
    }

    private void genQuestion(JPanel body) {
        body.removeAll();
        int[] nums = genNums();
        for (int i = 0;i<nums.length;i++){
            int num = nums[i];
            JButton button = new JButton(String.valueOf(num));
            button.addActionListener(e -> {
                if(pos==25){
                    started = false;
                    long result = System.currentTimeMillis() - startTime;
                    showSuccess(result);
                    restartButton.setVisible(false);
                    startButton.setVisible(true);
                    timeMaker.setText("0");
                    currentPos.setText("0");
                    body.removeAll();
                    return;
                }
                if(pos == num){
                    currentPos.setText(String.valueOf(pos));
                    pos++;
                }else{
                    button.setForeground(Color.RED);
                    button.updateUI();
                    executorService.schedule(()->{
                        button.setForeground(Color.BLACK);
                        button.updateUI();
                    },1, TimeUnit.SECONDS);
                }
            });
            button.setVisible(true);
            button.setPreferredSize(new Dimension(50,50));
            body.add(button);
        }
        body.updateUI();
    }

    private int[] genNums() {
        int[] result = new int[25];
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0;i<25;i++) {
            list.add(i+1);
        }
        int pos = 0;
        while(list.size()>0){
            int index = RandomUtils.nextInt(0, list.size());
            result[pos++] = list.get(index);
            list.remove(index);
        }
        return result;
    }


    private Component genMenu() {
        JPanel header = new JPanel();
        header.setSize(new Dimension(800,200));
        startButton.addActionListener(e -> {
            genQuestion(body);
            startButton.setVisible(false);
            pos = 1;
            started = true;
            restartButton.setVisible(true);
            startTime = System.currentTimeMillis();
            executorService.execute(myTimeMakerTask);
        });
        header.add(startButton);
        restartButton.addActionListener(e -> {
            body.removeAll();
            startButton.setVisible(true);
            started = false;
            pos = 0;
            restartButton.setVisible(false);
            header.updateUI();
        });
        restartButton.setVisible(false);
        header.add(restartButton);
        header.setVisible(true);
        return header;
    }

    public void showSuccess(long result) {
        User user = new User("", (int) result);
        if(result<maxTime || userRank.size()<10){
            String name = JOptionPane.showInputDialog(frame, "恭喜您进入榜单！！！\n 耗时：" + user.getScore() +"\n请留下您的姓名");
            if (StringUtils.isNotBlank(name)){
                user.name=name;
                userRank.add(user);
                genRank();
                executorService.execute(load::saveHistory);
            }
        }else{
            JOptionPane.showMessageDialog(frame,"恭喜您完成了训练！！！\n 耗时："+user.getScore());
        }
    }



    Runnable myTimeMakerTask = new Runnable(){

        @Override
        public void run() {
            while(started){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long currentTime = System.currentTimeMillis();
                long time = currentTime - startTime;
                timeMaker.setText(String.valueOf(time/1000));
                timeMaker.updateUI();
            }
        }
    };





    class User implements Comparable<User>{
     String name;
     Integer time;

        public User(String name, Integer time) {
            this.name = name;
            this.time = time;
        }

        @Override
        public int compareTo(User o) {
            if (Objects.isNull(o)){
                return -1;
            }
            return time-o.time;
        }

        public String getScore(){
            long s = time / 1000;
            long ms = time - s*1000;
            return s + "秒" + ms + "毫秒";
        }
    }


    class Load{
        String splitFlag = "&¥¥#@!@#¥¥&";

        File file ;

        public void init(){
            String path = System.getProperty("user.dir");
            file = new File(path+"/rank.text");
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void loadHistory() {
            try {
                FileReader reader = new FileReader(file);
                BufferedReader buffReader = new BufferedReader(reader);
                String line;
                while(StringUtils.isNotBlank(line = buffReader.readLine())){
                    String[] nameTime = line.split(splitFlag);
                    userRank.add(new User(nameTime[0],Integer.valueOf(nameTime[1])));
                }
                buffReader.close();
                reader.close();
                genRank();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        private void saveHistory() {
            if (userRank.size()==0){
                return;
            }
            try {
                FileWriter writer = new FileWriter(file);
                BufferedWriter buffReader = new BufferedWriter(writer);

                for (User user : userRank) {
                    String line = user.name + splitFlag + user.time + System.lineSeparator();
                    buffReader.append(line);
                }
                buffReader.close();
                writer.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

}
