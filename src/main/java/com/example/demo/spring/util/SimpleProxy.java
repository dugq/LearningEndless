package com.example.demo.spring.util;

/**
 * Created by dugq on 2018\4\22 0022.
 */
public class SimpleProxy implements Person{
    PersonBean personBean;

    public SimpleProxy(PersonBean personBean) {
        this.personBean = personBean;
    }

    public void say() {
        personBean.say();
    }

}

interface Person{
    void say();
}

class PersonBean implements Person{
    String name;

    public PersonBean(String name) {
        this.name = name;
    }

    @Override
    public void say() {
        System.out.println("my name is "+name);
    }
}
