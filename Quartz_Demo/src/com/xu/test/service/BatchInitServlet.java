package com.xu.test.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.xu.test.utils.SpringContext;

/**
 * @author xu
 * ���õ� web.xml:
 * <load-on-startup>3</load-on-startup>,��ʾ����ʱ����
 */
public class BatchInitServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(BatchInitServlet.class.getName());
    
    private QuartzService quartzService = null;
    
    public void init() {
        logger.info("��������ؿ�ʼ");

        try {
            super.init();

            // ȡspring�����е�������ȷ���bean
            if(null == quartzService){              
                quartzService = (QuartzService) SpringContext.getBean("quartzServiceImpl");
            }

            // ����service��ʼ��������
            quartzService.startQuartz();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("���������ʧ��" + e);
        }

        logger.info("��������ؽ���");
    }
}
