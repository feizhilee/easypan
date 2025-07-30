package com.easypan.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

// @Target 用来定义你的注解将应用于什么地方(例如是一个方法或者一个域)。
@Target({ElementType.METHOD, ElementType.TYPE})
// @Retention 用来定义该注解在哪一个级别可用，在源代码中(SOURCE)、类文件中(CLASS)或者运行时(RUNTIME)。
@Retention(RetentionPolicy.RUNTIME)
// 表明在生成 javadoc 的时候注解也会被包含进去
@Documented
@Mapping
public @interface GlobalInterceptor {

    /**
     * 校验参数
     *
     * @return
     */
    boolean checkParams() default false;

    /**
     * 校验登录
     *
     * @return
     */
    boolean checkLogin() default true;

    /**
     * 校验管理员
     *
     * @return
     */
    boolean checkAdmin() default false;

}
