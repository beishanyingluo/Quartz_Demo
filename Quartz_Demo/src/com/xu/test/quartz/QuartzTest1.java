package com.xu.test.quartz;

import java.util.Date;

/** 
 * @author  xu
 * @version 1.0 
 * 执行的任务
 */
public class QuartzTest1 {

  //把要执行的操作，
    public void execute() {
        execute2();
    }
    public void execute2(){
        System.err.println("测试来了，这是Job1呀:"+new Date());
    }
}
