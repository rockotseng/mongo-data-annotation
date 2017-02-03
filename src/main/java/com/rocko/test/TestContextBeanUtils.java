package com.rocko.test;


import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.test.context.TestContext;
import org.springframework.util.StringUtils;

public class TestContextBeanUtils {

    public static <T> T retrieveBean(TestContext testContext, String name, Class<T> type) {

        BeanFactory bf = testContext.getApplicationContext().getAutowireCapableBeanFactory();

        if (StringUtils.hasText(name)) {
            return bf.getBean(name, type);
        }

        return StringUtils.hasText(name) ? bf.getBean(name, type) : retrieveBean(testContext, type);
    }

    public static <T> T retrieveBean(TestContext testContext, Class<T> type) {

        BeanFactory bf = testContext.getApplicationContext().getAutowireCapableBeanFactory();

        if (bf instanceof ListableBeanFactory) {
            ListableBeanFactory lbf = (ListableBeanFactory) bf;
            Map<String, T> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(lbf, type);
            if (beans.size() == 1) {
                return beans.values().iterator().next();
            }
            return bf.getBean(type);
        }
        return null;
    }

}
