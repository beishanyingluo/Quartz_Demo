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
 * 将任务 JobBean对象传进来，
 * 获得一个JobdetailBean对象,
 * 将JobDetailBean的对象给 新创建的定时器
 */
@Component
public class QuartzTest3 {

    @Autowired
    private Scheduler scheduler;
    
    public void jobtest(JobBean jobBean) throws Exception {
        System.out.println("进来啦！！");
        // 创建一个job
        JobDetailBean jobDetail = createJobDetail(jobBean);
        scheduler.addJob(jobDetail, true);
        // 创建一个定时器
        CronTriggerBean trigger = new CronTriggerBean();
        trigger.setCronExpression(jobBean.getCronExpression());
        trigger.setJobDetail(jobDetail);
        trigger.setName(jobBean.getJobName());
        trigger.setJobName(jobDetail.getName());
        scheduler.scheduleJob(trigger);
    }
    
    /**
     * 新建一个 JobDetailBean
     * @param quartzJobDTO
     * @return
     */
    private JobDetailBean createJobDetail(JobBean jobBean) {
        //创建一个job
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
