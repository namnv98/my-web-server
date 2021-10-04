package com.nnv.core.context;

import com.nnv.core.beans.BeanFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ApplicationContext {
    BeanFactory beanFactory;

    ApplicationContext parent;

    String displayName;

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public ApplicationContext getParent() {
        return parent;
    }

    public void setParent(ApplicationContext parent) {
        this.parent = parent;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Object getBean(String className) {
        Object object = beanFactory.beans.get(className);
        if (object == null && parent != null) {
            object = parent.getBean(className);
        }
        return object;
    }

    public Object getBean(Class className) {
        Object object = beanFactory.beans.get(className.getName());
        if (object == null && parent != null) {
            object = parent.getBean(className);
        }
        return object;
    }

    public List<Object> findBeanByAnnotation(Class className) {
        List<Object> objectList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : beanFactory.beans.entrySet()) {
            Class<? extends Object> clazz = entry.getValue().getClass();
            if (clazz.isAnnotationPresent(className)) {
                objectList.add(entry.getValue());
            }
        }

        if (parent != null) {
            objectList.addAll((Collection<?>) parent.findBeanByAnnotation(className));
        }
        return objectList;
    }
}
