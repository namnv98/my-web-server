package com.nnv;

public class Configer {

    // 发布项目的存放路径
    public static String WEBAPPS = System.getProperty("user.dir") +"\\web-server"+ "\\webapps";

    // 发布项目的初始化文件
    public static String WEBXML = "web.xml";

    // 初始化文件中代表当前应用名称配置的节点
    public static String APPNAME = "app-name";

    // 初始化文件中代表请求配置的节点
    public static String REQUEST = "request";

    // 初始化文件中代表请求url配置的节点
    public static String URL = "url";

    // 初始化文件中代表请求url对应处理类配置的节点
    public static String CLASSPATH = "classpath";

    // 初始化文件中代表请求url对应处理类处理方法配置的节点
    public static String METHOD = "method";

    // 项目class包文件夹所属的文件夹名称
    public static String CLASSES = "classes";

    // 项目文件的classes文件夹和web.xml的位置
    public static String WEBINF = "WEB-INF";

    // 项目文件的class的后缀
    public static String CLASSPOSTFIX = ".class";
}