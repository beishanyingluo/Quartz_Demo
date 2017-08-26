package com.xu.test.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/** 
 * @author  xu
 * @version 1.0 
 * 通过ApplicationContextAware加载Spring上下文环境
 */
@Component
public class SpringContext implements ApplicationContextAware{

    private static ApplicationContext context;

    public void setApplicationContext(ApplicationContext applicationcontext)
            throws BeansException {
        SpringContext.context = applicationcontext;
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

}
