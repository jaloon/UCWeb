package com.tipray.core.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Repository;

/**
 * 公共日志切面 主要是为了输出一些用户的操作日志
 * 
 * @author chends
 * 
 */
//@Aspect
//@Repository("exampleAspect")
public class ExampleAspect {

	@Around("execution(public * com.tipray.controller.*Controller.find*(..))")
	public Object find(ProceedingJoinPoint joinPoint) throws Throwable {
		Class<?> clazz = joinPoint.getTarget().getClass();
		String methodName = joinPoint.getSignature().getName();
		Object[] params = joinPoint.getArgs();
		System.out.println("aspect @Around before:"+clazz+"."+methodName);
		// 执行原有方法
		Object result = joinPoint.proceed(params);
		System.out.println("aspect @Around after");
		return result;
	}
	@Before("execution(public * com.tipray.controller.*Controller.add*(..))")
	public void beforAction(JoinPoint joinPoint) {
		String clazz = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		System.out.println("aspect @Before:"+clazz+"."+methodName);
	}
	
	@After("execution(public * com.tipray.controller.*Controller.update*(..))")
	public void add(JoinPoint joinPoint) {
		String clazz = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		System.out.println("aspect @After:"+clazz+"."+methodName);
	}

	@AfterReturning(pointcut = "execution(public * com.tipray.controller.*Controller.update*(..))", returning = "rtv")
	public void afterReturningAction(JoinPoint joinPoint, Object rtv) {
		String clazz = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		System.out.println("aspect @AfterReturning:"+clazz+"."+methodName);
	}

	@AfterThrowing(pointcut = "execution(public * com.tipray.controller.*Controller.update*(..))" , throwing = "ex")
	public void afterThrowingAction(JoinPoint joinPoint, Exception ex) {
		String clazz = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		System.out.println("aspect @AfterThrowing:"+clazz+"."+methodName);
	}

}
