package com.xu.test.quartz;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.xu.test.utils.ReflectionUtil;
import com.xu.test.utils.SpringContext;

/** 
 * @author  xu
 * @version 1.0 
 * ���þ����������ҵ��
 */
public class QuartzJob extends QuartzJobBean{

    private static Logger logger = Logger.getLogger(QuartzJob.class.getName());
    
    /**
     * ����jobmap�е����õ�bean.method
     */
    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        
        try {
            System.out.println("���뵽��������!");
            //ReflectionUtil.invokeMethod(SpringContext.getBean(beanName),methodName, null, null);
            System.out.println("��ʼִ����: �ҵ������ʵ���� ����ķ���!");
        } catch (Exception e) {
             logger.warn("���������ʧ��", e);
        }
    }

}
