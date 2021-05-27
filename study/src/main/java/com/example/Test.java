package com.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @program: spring
 * @description:
 * @author: xiaofan
 * @create: 2021-05-27 10:48
 **/
public class Test {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Appconfig.class);
		applicationContext.getBean(TestService.class).test();
		System.out.println("aaaaaaaaaaaaaaaaa");
	}
}
