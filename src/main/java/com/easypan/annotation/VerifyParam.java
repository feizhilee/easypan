package com.easypan.annotation;

import com.easypan.entity.enums.VerifyRegexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyParam {

    // 最短多少
    int min() default -1;

    // 最长多少
    int max() default -1;

    // 是否必传
    boolean required() default false;

    // 正则校验，默认不校验
    VerifyRegexEnum regex() default VerifyRegexEnum.NO;

}
