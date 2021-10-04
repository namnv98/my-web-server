package com.nnv.core.beans;

import com.nnv.core.anotation.Bean;
import com.nnv.core.anotation.MyController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class BeanFactory {
    public List<String> classNames = new ArrayList<>();
    public Map<String, Object> beans = new HashMap<>();
    private static Properties properties = new Properties();

    public BeanFactory() {
    }

    public BeanFactory(String location) {
        doLoadConfig(location);
        doScanner(properties.getProperty("scanPackage"));
        doInstance();
    }

    private void doLoadConfig(String location) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = cl.getResourceAsStream(location);
        try {
            //用Properties文件加载文件里的内容
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关流
            if (null != resourceAsStream) {
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void doScanner(String packageName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        //把所有的.替换成/
        URL url = cl.getResource(packageName);
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                //递归读取包
                doScanner(packageName + "." + file.getName());
            } else {
                String className = packageName + "." + file.getName().replace(".class", "");
                classNames.add(className);
            }
        }
    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        for (String className : classNames) {
            try {
                //把类搞出来,反射来实例化(只有加@MyController需要实例化)
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(MyController.class) || clazz.isAnnotationPresent(Bean.class)) {
                    beans.put(toLowerFirstWord(clazz.getSimpleName()), clazz.newInstance());
                } else {
                    continue;
                }


            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * 把字符串的首字母小写
     *
     * @param name
     * @return
     */
    private String toLowerFirstWord(String name) {
        char[] charArray = name.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }

}
