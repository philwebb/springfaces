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
package org.springframework.springfaces.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.springfaces.SpringFacesIntegration;
import org.springframework.springfaces.config.SpringFacesNamespaceHandler;
import org.springframework.springfaces.convert.SpringFacesConverterSupport;
import org.springframework.springfaces.exceptionhandler.ObjectMessageExceptionHandler;
import org.springframework.springfaces.exceptionhandler.SpringFacesExceptionHandlerSupport;
import org.springframework.springfaces.expression.el.FacesStandardEvaluationContextPostProcessor;
import org.springframework.springfaces.expression.spel.support.StandardEvaluationContextPostProcessorSupport;
import org.springframework.springfaces.validator.SpringFacesValidatorSupport;

/**
 * Tests for {@link SpringFacesNamespaceHandler}.
 * 
 * 
 * @author Phillip Webb
 */
public class SpringFacesNamespaceHandlerTest {

	private DefaultListableBeanFactory beanFactory;

	@Before
	public void setup() {
		this.beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this.beanFactory);
		reader.loadBeanDefinitions(new ClassPathResource("testSpringFacesNamespace.xml", getClass()));
	}

	@Test
	public void shouldAddIntegrationItems() {
		assertThat(this.beanFactory.getBeansOfType(SpringFacesIntegration.class).size(), is(1));
		assertThat(this.beanFactory.getBeansOfType(SpringFacesValidatorSupport.class).size(), is(1));
		assertThat(this.beanFactory.getBeansOfType(SpringFacesConverterSupport.class).size(), is(1));
		assertThat(this.beanFactory.getBeansOfType(SpringFacesExceptionHandlerSupport.class).size(), is(1));
		assertThat(this.beanFactory.getBeansOfType(ObjectMessageExceptionHandler.class).size(), is(1));
		assertThat(this.beanFactory.getBeansOfType(StandardEvaluationContextPostProcessorSupport.class).size(), is(1));
		assertThat(this.beanFactory.getBeansOfType(FacesStandardEvaluationContextPostProcessor.class).size(), is(1));
	}
}
