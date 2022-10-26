package com.example.demo.spring.pojo.mbeans;

import javax.management.MXBean;

/**
 * Created by dugq on 2018\6\10 0010.
 * 1、需要有接口定义规则，实现类完成具体逻辑
 * 2、接口必须以MBean结尾
 * 3、注册 ：
 *    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      ObjectName mxbeanName = new ObjectName("com.example.demo.spring.pojo.mbeans.impl:type=TestMBeanImpl");
      TestMBeanImpl mxbean = new TestMBeanImpl();
      mbs.registerMBean(mxbean, mxbeanName);
 * 4、远程需要添加启动参数（启动参数优先级最高，不然会被默认值替换）  ： -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false 账号密码需要文件 文件模板存在jre中
 *
 *
 */
@MXBean
public interface TestMBean {
     double name();
}
