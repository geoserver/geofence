/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.spring;

import org.springframework.context.ApplicationContext;

// TODO: Auto-generated Javadoc
/**
 * The Class ApplicationContextUtil.
 */
public class ApplicationContextUtil {

    /** The INSTANCE. */
    private static ApplicationContextUtil INSTANCE;

    /** The spring context. */
    private ApplicationContext springContext;

    /**
     * Gets the single instance of ApplicationContextUtil.
     * 
     * @return single instance of ApplicationContextUtil
     */
    public static synchronized ApplicationContextUtil getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ApplicationContextUtil();
        return INSTANCE;
    }

    /**
     * Gets the bean.
     * 
     * @param beanName
     *            the bean name
     * @return the bean
     */
    public Object getBean(String beanName) {
        return springContext.getBean(beanName);
    }

    /**
     * Gets the spring context.
     * 
     * @return the spring context
     */
    public ApplicationContext getSpringContext() {
        return springContext;
    }

    /**
     * Sets the spring context.
     * 
     * @param springContext
     *            the new spring context
     */
    public void setSpringContext(ApplicationContext springContext) {
        this.springContext = springContext;
    }
}
