package com.tipray.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**  
 *权限判断的注解
 */    
    
@Target({ElementType.TYPE, ElementType.METHOD})    
@Retention(RetentionPolicy.RUNTIME)    
@Documented 
public @interface PermissionAnno {
	/**
	 * 权限的ename
	 *     如果多个权限，用逗号作为分隔符
	 * @return
	 */
	String value();
	/**
	 * 多个权限的连接符：
	 *       and    or
	 * @return
	 */
	String mode() default "and";
}
