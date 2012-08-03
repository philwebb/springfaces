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
package org.springframework.springfaces.mvc.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

/**
 * Base class for namespace tests.
 * 
 * @author Phillip Webb
 */
public abstract class AbstractNamespaceTest {

	public void assertHasBean(ApplicationContext applicationContext, Class<?> beanClass) {
		assertThat(applicationContext.getBeansOfType(beanClass).size(), is(1));
	}

	public StaticWebApplicationContext loadMvcApplicationContext(String string) {
		return loadApplicationContext("<mvc:annotation-driven/>" + string);
	}

	public StaticWebApplicationContext loadApplicationContext(String content) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n"
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ "xmlns:mvc=\"http://www.springframework.org/schema/mvc\"\n"
				+ "xmlns:faces=\"http://www.springframework.org/schema/springfaces\"\n xsi:schemaLocation=\"\n"
				+ "http://www.springframework.org/schema/beans\n"
				+ "http://www.springframework.org/schema/beans/spring-beans.xsd\n"
				+ "http://www.springframework.org/schema/mvc\n"
				+ "http://www.springframework.org/schema/mvc/spring-mvc.xsd\n"
				+ "http://www.springframework.org/schema/springfaces\n"
				+ "http://www.springframework.org/schema/springfaces/springfaces.xsd\">\n" + content + "\n</beans>";
		return loadApplicationContext(new ByteArrayResource(xml.getBytes()));
	}

	public StaticWebApplicationContext loadApplicationContext(Resource resource) {
		StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
		applicationContext.setServletContext(new MockServletContext());
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(applicationContext.getDefaultListableBeanFactory());
		reader.loadBeanDefinitions(resource);
		applicationContext.refresh();
		return applicationContext;
	}
}
