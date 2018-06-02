package com.tipray.core.annotation;

import java.lang.annotation.*;

/**
 * Mybatis返回Map而不是List<Map>注解
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MapResultAnno {
    /**
     * 键类型
     *
     * @return {@link Class}
     */
    Class<?> keyType();

    /**
     * 值类型
     *
     * @return {@link Class}
     */
    Class<?> valType();
}


