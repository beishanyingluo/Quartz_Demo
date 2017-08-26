package com.xu.test.service;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.xu.test.bean.JobBean;
import com.xu.test.quartz.QuartzJob;
import com.xu.test.quartz.QuartzTest3;


/** 
 * @author  xu
 * @version 1.0 
 */
@Service     
public class QuartzServiceImpl implements QuartzService{

    protected final Logger logger = Logger.getLogger(getClass());
    
    private QuartzTest3 quartzTest3;

    @Resource
    public void setQuartzTest3(QuartzTest3 quartzTest3) {
        this.quartzTest3 = quartzTest3;
    }
    
    public void startQuartz() throws Exception {

        System.out.println("============kaishi============");

        JobBean job = new JobBean();

        // ���̷߳�ʽִ������
        job.setJobClass(QuartzJob.class);
        job.setRepeatInterval(1000);
        job.setStartDelay(20);
        job.setJobName("c");
        //ʱ���趨
        job.setCronExpression("0 0/1 * * * ?");
        
        logger.info("----��ʼ��������");
        quartzTest3.jobtest(job);
        //quartzTest3.test();
        logger.info("----�ɹ���������");

        logger.info("��������ȡ���������");
    }
}
