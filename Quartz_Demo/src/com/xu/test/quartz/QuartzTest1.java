package com.xu.test.quartz;

import java.util.Date;

/** 
 * @author  xu
 * @version 1.0 
 * ִ�е�����
 */
public class QuartzTest1 {

  //��Ҫִ�еĲ�����
    public void execute() {
        execute2();
    }
    public void execute2(){
        System.err.println("�������ˣ�����Job1ѽ:"+new Date());
    }
}
