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
 * 调用具体的批处理业务
 */
public class QuartzJob extends QuartzJobBean{

    private static Logger logger = Logger.getLogger(QuartzJob.class.getName());
    
    /**
     * 调用jobmap中的配置的bean.method
     */
    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        
        try {
            System.out.println("进入到反射类中!");
            //ReflectionUtil.invokeMethod(SpringContext.getBean(beanName),methodName, null, null);
            System.out.println("开始执行啦: 找到具体的实现类 具体的方法!");
        } catch (Exception e) {
             logger.warn("批处理调用失败", e);
        }
    }

}
