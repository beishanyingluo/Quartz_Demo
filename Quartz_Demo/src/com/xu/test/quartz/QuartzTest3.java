package com.xu.test.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.stereotype.Component;

import com.xu.test.bean.JobBean;

/** 
 * @author  xu
 * @version 1.0 
 * ������ JobBean���󴫽�����
 * ���һ��JobdetailBean����,
 * ��JobDetailBean�Ķ���� �´����Ķ�ʱ��
 */
@Component
public class QuartzTest3 {

    @Autowired
    private Scheduler scheduler;
    
    public void jobtest(JobBean jobBean) throws Exception {
        System.out.println("����������");
        // ����һ��job
        JobDetailBean jobDetail = createJobDetail(jobBean);
        scheduler.addJob(jobDetail, true);
        // ����һ����ʱ��
        CronTriggerBean trigger = new CronTriggerBean();
        trigger.setCronExpression(jobBean.getCronExpression());
        trigger.setJobDetail(jobDetail);
        trigger.setName(jobBean.getJobName());
        trigger.setJobName(jobDetail.getName());
        scheduler.scheduleJob(trigger);
    }
    
    /**
     * �½�һ�� JobDetailBean
     * @param quartzJobDTO
     * @return
     */
    private JobDetailBean createJobDetail(JobBean jobBean) {
        //����һ��job
        JobDetailBean jobDetail = new JobDetailBean();
        jobDetail.setName(jobBean.getJobName());
        jobDetail.setJobClass(jobBean.getJobClass());
        jobDetail.getJobDataMap().putAll(jobBean.getJobDataMap());
        return jobDetail;
    }
    
    public void test() {
        System.out.println("++++ ===== ++++++");
    }
}
