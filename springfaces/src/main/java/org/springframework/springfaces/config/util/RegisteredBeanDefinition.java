/*
 * Copyright 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.config.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.Assert;

/**
 * A newly registered bean definition.
 * @author Phillip Webb
 */
public class RegisteredBeanDefinition {

	private BeanDefinition beanDefinition;

	private String name;

	public RegisteredBeanDefinition(BeanDefinition beanDefinition, String name) {
		Assert.notNull(beanDefinition, "BeanDefinition must not be null");
		Assert.notNull(name, "Name must not be null");
		this.beanDefinition = beanDefinition;
		this.name = name;
	}

	/**
	 * Return the bean definition that was registered.
	 * @return the beanDefinition
	 */
	public BeanDefinition getBeanDefinition() {
		return this.beanDefinition;
	}

	/**
	 * Return the name that was registered.
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return a {@link RuntimeBeanReference} to the name.
	 * @return the reference
	 */
	public RuntimeBeanReference asReference() {
		return new RuntimeBeanReference(this.name);
	}
}
