package com.nnv.core.anotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    /**
     * 表示给controller注册别名
     * @return
     */
    String value() default "";

}