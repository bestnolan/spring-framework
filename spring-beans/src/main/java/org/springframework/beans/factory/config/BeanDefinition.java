/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * A BeanDefinition describes a bean instance, which has property values,
 * constructor argument values, and further information supplied by
 * concrete implementations.
 *
 * <p>This is just a minimal interface: The main intention is to allow a
 * {@link BeanFactoryPostProcessor} to introspect and modify property values
 * and other bean metadata.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 19.03.2004
 * @see ConfigurableListableBeanFactory#getBeanDefinition
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

	// 我们可以看到，默认只提供 sington 和 prototype 两种，
	// 很多读者可能知道还有 request, session, globalSession, application, websocket 这几种，
	// 不过，它们属于基于 web 的扩展。

	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;


	// 比较不重要，直接跳过吧
	int ROLE_APPLICATION = 0;
	int ROLE_SUPPORT = 1;
	int ROLE_INFRASTRUCTURE = 2;


	// 设置父 Bean，这里涉及到 bean 继承，不是 java 继承。请参见附录的详细介绍
	// 一句话就是：继承父 Bean 的配置信息而已
	void setParentName(@Nullable String parentName);

	// 获取父 Bean
	@Nullable
	String getParentName();

	// 设置 Bean 的类名称，将来是要通过反射来生成实例的
	void setBeanClassName(@Nullable String beanClassName);

	// 获取 Bean 的类名称
	@Nullable
	String getBeanClassName();

	// 设置 bean 的 scope
	void setScope(@Nullable String scope);

	@Nullable
	String getScope();

	// 设置是否懒加载
	void setLazyInit(boolean lazyInit);

	boolean isLazyInit();

	// 设置该 Bean 依赖的所有的 Bean，注意，这里的依赖不是指属性依赖(如 @Autowire 标记的)，
	// 是 depends-on="" 属性设置的值。
	void setDependsOn(@Nullable String... dependsOn);

	// 返回该 Bean 的所有依赖
	@Nullable
	String[] getDependsOn();

	// 设置该 Bean 是否可以注入到其他 Bean 中，只对根据类型注入有效，
	// 如果根据名称注入，即使这边设置了 false，也是可以的
	void setAutowireCandidate(boolean autowireCandidate);

	// 该 Bean 是否可以注入到其他 Bean 中
	boolean isAutowireCandidate();

	// 主要的。同一接口的多个实现，如果不指定名字的话，Spring 会优先选择设置 primary 为 true 的 bean
	void setPrimary(boolean primary);

	// 是否是 primary 的
	boolean isPrimary();

	// 如果该 Bean 采用工厂方法生成，指定工厂名称。对工厂不熟悉的读者，请参加附录
	// 一句话就是：有些实例不是用反射生成的，而是用工厂模式生成的
	void setFactoryBeanName(@Nullable String factoryBeanName);

	// 获取工厂名称
	@Nullable
	String getFactoryBeanName();

	// 指定工厂类中的 工厂方法名称
	void setFactoryMethodName(@Nullable String factoryMethodName);

	// 获取工厂类中的 工厂方法名称
	@Nullable
	String getFactoryMethodName();

	// 构造器参数
	ConstructorArgumentValues getConstructorArgumentValues();

	/**
	 * Return if there are constructor argument values defined for this bean.
	 * @since 5.0.2
	 */
	default boolean hasConstructorArgumentValues() {
		return !getConstructorArgumentValues().isEmpty();
	}

	// Bean 中的属性值，后面给 bean 注入属性值的时候会说到
	MutablePropertyValues getPropertyValues();

	/**
	 * Return if there are property values defined for this bean.
	 * @since 5.0.2
	 */
	default boolean hasPropertyValues() {
		return !getPropertyValues().isEmpty();
	}

	/**
	 * Set the name of the initializer method.
	 * @since 5.1
	 */
	void setInitMethodName(@Nullable String initMethodName);

	/**
	 * Return the name of the initializer method.
	 * @since 5.1
	 */
	@Nullable
	String getInitMethodName();

	/**
	 * Set the name of the destroy method.
	 * @since 5.1
	 */
	void setDestroyMethodName(@Nullable String destroyMethodName);

	/**
	 * Return the name of the destroy method.
	 * @since 5.1
	 */
	@Nullable
	String getDestroyMethodName();

	/**
	 * Set the role hint for this {@code BeanDefinition}. The role hint
	 * provides the frameworks as well as tools an indication of
	 * the role and importance of a particular {@code BeanDefinition}.
	 * @since 5.1
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_SUPPORT
	 * @see #ROLE_INFRASTRUCTURE
	 */
	void setRole(int role);

	/**
	 * Get the role hint for this {@code BeanDefinition}. The role hint
	 * provides the frameworks as well as tools an indication of
	 * the role and importance of a particular {@code BeanDefinition}.
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_SUPPORT
	 * @see #ROLE_INFRASTRUCTURE
	 */
	int getRole();

	/**
	 * Set a human-readable description of this bean definition.
	 * @since 5.1
	 */
	void setDescription(@Nullable String description);

	/**
	 * Return a human-readable description of this bean definition.
	 */
	@Nullable
	String getDescription();


	// Read-only attributes

	/**
	 * Return a resolvable type for this bean definition,
	 * based on the bean class or other specific metadata.
	 * <p>This is typically fully resolved on a runtime-merged bean definition
	 * but not necessarily on a configuration-time definition instance.
	 * @return the resolvable type (potentially {@link ResolvableType#NONE})
	 * @since 5.2
	 * @see ConfigurableBeanFactory#getMergedBeanDefinition
	 */
	ResolvableType getResolvableType();

	// 是否 singleton
	boolean isSingleton();

	// 是否 prototype
	boolean isPrototype();

	// 如果这个 Bean 是被设置为 abstract，那么不能实例化，
	// 常用于作为 父bean 用于继承，其实也很少用......
	boolean isAbstract();

	/**
	 * Return a description of the resource that this bean definition
	 * came from (for the purpose of showing context in case of errors).
	 */
	@Nullable
	String getResourceDescription();

	/**
	 * Return the originating BeanDefinition, or {@code null} if none.
	 * <p>Allows for retrieving the decorated bean definition, if any.
	 * <p>Note that this method returns the immediate originator. Iterate through the
	 * originator chain to find the original BeanDefinition as defined by the user.
	 */
	@Nullable
	BeanDefinition getOriginatingBeanDefinition();

}
