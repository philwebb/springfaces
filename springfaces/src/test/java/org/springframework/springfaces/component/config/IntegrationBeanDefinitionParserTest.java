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
package org.springframework.springfaces.component.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.springfaces.convert.SpringFacesConverterSupport;
import org.springframework.springfaces.exceptionhandler.ObjectMessageExceptionHandler;
import org.springframework.springfaces.exceptionhandler.SpringFacesExceptionHandlerSupport;
import org.springframework.springfaces.expression.el.FacesStandardEvaluationContextPostProcessor;
import org.springframework.springfaces.expression.spel.support.StandardEvaluationContextPostProcessorSupport;
import org.springframework.springfaces.validator.SpringFacesValidatorSupport;
import org.w3c.dom.Element;

/**
 * Tests for {@link IntegrationBeanDefinitionParser}.
 * 
 * @author Phillip Webb
 */
public class IntegrationBeanDefinitionParserTest {

	private IntegrationBeanDefinitionParser parser;

	private ParserContext parserContext;

	@Mock
	private XmlReaderContext readerContext;

	@Mock
	private BeanDefinitionParserDelegate delegate;

	@Mock
	private Element element;

	private List<BeanDefinition> beanDefinitions = new ArrayList<BeanDefinition>();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.parser = new IntegrationBeanDefinitionParser();
		this.parserContext = new ParserContext(this.readerContext, this.delegate);
		given(this.element.getTagName()).willReturn("integration");
		given(this.readerContext.registerWithGeneratedName(any(BeanDefinition.class))).willAnswer(new Answer<String>() {

			public String answer(InvocationOnMock invocation) throws Throwable {
				BeanDefinition beanDefinition = (BeanDefinition) invocation.getArguments()[0];
				IntegrationBeanDefinitionParserTest.this.beanDefinitions.add(beanDefinition);
				return beanDefinition.getBeanClassName();
			}
		});
	}

	@Test
	public void shouldOnlyRegisterIntegration() throws Exception {
		// The mock element has missing attributes so by default only the integration bean is registered. In the live
		// environment the xsd defaults the attributes to true.
		this.parser.parse(this.element, this.parserContext);
		assertThat(this.beanDefinitions.size(), is(1));
	}

	@Test
	public void shouldRegisterValidatorSupport() throws Exception {
		given(this.element.getAttribute("validators")).willReturn("true");
		this.parser.parse(this.element, this.parserContext);
		assertThat(this.beanDefinitions.size(), is(2));
		assertThat(this.beanDefinitions.get(1).getBeanClassName(), is(SpringFacesValidatorSupport.class.getName()));
	}

	@Test
	public void shouldRegisterConverterSupport() throws Exception {
		given(this.element.getAttribute("converters")).willReturn("true");
		this.parser.parse(this.element, this.parserContext);
		assertThat(this.beanDefinitions.size(), is(2));
		assertThat(this.beanDefinitions.get(1).getBeanClassName(), is(SpringFacesConverterSupport.class.getName()));

	}

	@Test
	public void shouldRegisterExceptionHandlerSupport() throws Exception {
		given(this.element.getAttribute("exception-handlers")).willReturn("true");
		this.parser.parse(this.element, this.parserContext);
		assertThat(this.beanDefinitions.size(), is(3));
		assertThat(this.beanDefinitions.get(1).getBeanClassName(),
				is(SpringFacesExceptionHandlerSupport.class.getName()));
		assertThat(this.beanDefinitions.get(2).getBeanClassName(), is(ObjectMessageExceptionHandler.class.getName()));
	}

	@Test
	public void shouldRegisterSpringExpressionSupport() throws Exception {
		given(this.element.getAttribute("spring-expressions")).willReturn("true");
		this.parser.parse(this.element, this.parserContext);
		assertThat(this.beanDefinitions.size(), is(3));
		assertThat(this.beanDefinitions.get(1).getBeanClassName(),
				is(StandardEvaluationContextPostProcessorSupport.class.getName()));
		assertThat(this.beanDefinitions.get(2).getBeanClassName(),
				is(FacesStandardEvaluationContextPostProcessor.class.getName()));
	}
}
