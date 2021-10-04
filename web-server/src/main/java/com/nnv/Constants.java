package com.nnv;

import java.io.File;

/**
 * @author Liuweian
 * @version 1.0.0
 * @desc 常量。
 * @createTime 2019/12/18 9:35
 */
public class Constants {

    private Constants() {

    }

    public final static String RESOURCES_PATH = "/tomcat-002-basisservlet/src/main/resources";

    public final static String JAVA_PATH = "/tomcat-002-basisservlet/src/main/java";

    public final static String DEFAULT_ROOT_PATH = "/webapps";

    public static final String WEB_ROOT = System.getProperty("user.dir");

//    public static final String DEFAULT_WEB_ROOT = WEB_ROOT + RESOURCES_PATH + File.separator + DEFAULT_ROOT_PATH;
    public static final String DEFAULT_WEB_ROOT = WEB_ROOT + RESOURCES_PATH + File.separator + DEFAULT_ROOT_PATH;

    public static final String SERVLET_CLASS_PATH = WEB_ROOT + JAVA_PATH + File.separator + "com/liu/tomcat/servlet";

}
