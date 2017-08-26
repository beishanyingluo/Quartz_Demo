package com.xu.test.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.xu.test.utils.SpringContext;

/**
 * @author xu
 * 配置到 web.xml:
 * <load-on-startup>3</load-on-startup>,表示启动时加载
 */
public class BatchInitServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(BatchInitServlet.class.getName());
    
    private QuartzService quartzService = null;
    
    public void init() {
        logger.info("批处理加载开始");

        try {
            super.init();

            // 取spring容器中的任务调度服务bean
            if(null == quartzService){              
                quartzService = (QuartzService) SpringContext.getBean("quartzServiceImpl");
            }

            // 调用service初始化批处理
            quartzService.startQuartz();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("批处理加载失败" + e);
        }

        logger.info("批处理加载结束");
    }
}
