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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.springfaces.SpringFacesIntegration;
import org.springframework.springfaces.convert.SpringFacesConverterSupport;
import org.springframework.springfaces.exceptionhandler.ObjectMessageExceptionHandler;
import org.springframework.springfaces.exceptionhandler.SpringFacesExceptionHandlerSupport;
import org.springframework.springfaces.expression.el.FacesStandardEvaluationContextPostProcessor;
import org.springframework.springfaces.expression.spel.support.StandardEvaluationContextPostProcessorSupport;
import org.springframework.springfaces.validator.SpringFacesValidatorSupport;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} that parses the <tt>integration</tt> element to configure a Spring Faces application.
 * 
 * @author Phillip Webb
 */
class IntegrationBeanDefinitionParser implements BeanDefinitionParser {

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		CompositeComponentDefinition composite = new CompositeComponentDefinition(element.getTagName(), source);
		parserContext.pushContainingComponent(composite);
		registerSpringIntegration(element, parserContext, source);
		registerValidatorSupport(element, parserContext, source);
		registerConverterSupport(element, parserContext, source);
		registerExceptionHandlerSupport(element, parserContext, source);
		registerSpringExpressionSupport(element, parserContext, source);
		parserContext.popAndRegisterContainingComponent();
		return null;
	}

	private void registerSpringIntegration(Element element, ParserContext parserContext, Object source) {
		register(parserContext, source, SpringFacesIntegration.class);
	}

	private void registerValidatorSupport(Element element, ParserContext parserContext, Object source) {
		if (isAttributeTrue(element, "validators")) {
			register(parserContext, source, SpringFacesValidatorSupport.class);
		}
	}

	private void registerConverterSupport(Element element, ParserContext parserContext, Object source) {
		if (isAttributeTrue(element, "converters")) {
			register(parserContext, source, SpringFacesConverterSupport.class);
		}
	}

	private void registerExceptionHandlerSupport(Element element, ParserContext parserContext, Object source) {
		if (isAttributeTrue(element, "exception-handlers")) {
			register(parserContext, source, SpringFacesExceptionHandlerSupport.class);
			register(parserContext, source, ObjectMessageExceptionHandler.class, BeanDefinition.ROLE_APPLICATION);
		}
	}

	private void registerSpringExpressionSupport(Element element, ParserContext parserContext, Object source) {
		if (isAttributeTrue(element, "spring-expressions")) {
			register(parserContext, source, StandardEvaluationContextPostProcessorSupport.class);
			register(parserContext, source, FacesStandardEvaluationContextPostProcessor.class);
		}
	}

	private void register(ParserContext parserContext, Object source, Class<?> beanClass) {
		register(parserContext, source, beanClass, BeanDefinition.ROLE_INFRASTRUCTURE);
	}

	private void register(ParserContext parserContext, Object source, Class<?> beanClass, int role) {
		RootBeanDefinition definition = new RootBeanDefinition(beanClass);
		definition.setRole(role);
		definition.setSource(source);
		String name = parserContext.getReaderContext().registerWithGeneratedName(definition);
		parserContext.registerComponent(new BeanComponentDefinition(definition, name));
	}

	private boolean isAttributeTrue(Element element, String name) {
		return Boolean.parseBoolean(element.getAttribute(name));
	}
}
