package com.xu.test.quartz;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;


/** 
 * @author  xu
 * @version 1.0 
 * 调度的任务，继承QuartzJobBean，其实QuartzJobBean实现了quartz框架中的Job接口
 */
public class QuartzTest2 extends QuartzJobBean{

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // TODO Auto-generated method stub
        System.err.println("测试2来啦:"+new Date());
    }

}
