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

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.springfaces.mvc.converter.GenericFacesConverter;

/**
 * Tests for {@link SpringFacesMvcNamespaceHandler}.
 * 
 * @author Phillip Webb
 */
public class SpringFacesMvcNamespaceHandlerTest {
	private DefaultListableBeanFactory beanFactory;

	@Before
	public void setup() {
		this.beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this.beanFactory);
		reader.loadBeanDefinitions(new ClassPathResource("testSpringFacesMvcNamespace.xml", getClass()));
	}

	@Test
	public void shouldConversionService() {
		assertThat(this.beanFactory.getBeansOfType(GenericConversionService.class).size(), is(1));
		assertThat(this.beanFactory.getBeansOfType(GenericFacesConverter.class).size(), is(1));
	}

}
