package com.xu.test.quartz;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;


/** 
 * @author  xu
 * @version 1.0 
 * ���ȵ����񣬼̳�QuartzJobBean����ʵQuartzJobBeanʵ����quartz����е�Job�ӿ�
 */
public class QuartzTest2 extends QuartzJobBean{

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // TODO Auto-generated method stub
        System.err.println("����2����:"+new Date());
    }

}
