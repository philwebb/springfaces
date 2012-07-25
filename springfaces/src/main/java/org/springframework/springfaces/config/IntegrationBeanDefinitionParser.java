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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.springfaces.SpringFacesIntegration;
import org.springframework.springfaces.config.util.BeanDefinitionParserHelper;
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
		BeanDefinitionParserHelper helper = new BeanDefinitionParserHelper(element, parserContext);
		parserContext.pushContainingComponent(helper.getComponentDefinition());
		helper.register(SpringFacesIntegration.class);
		registerIfAttributeIsTrue(helper, "validators", SpringFacesValidatorSupport.class);
		registerIfAttributeIsTrue(helper, "converters", SpringFacesConverterSupport.class);
		registerIfAttributeIsTrue(helper, "exception-handlers", SpringFacesExceptionHandlerSupport.class,
				ObjectMessageExceptionHandler.class);
		registerIfAttributeIsTrue(helper, "spring-expressions", StandardEvaluationContextPostProcessorSupport.class,
				FacesStandardEvaluationContextPostProcessor.class);
		parserContext.popAndRegisterContainingComponent();
		return null;
	}

	private void registerIfAttributeIsTrue(BeanDefinitionParserHelper helper, String attribute, Class<?>... beanClasses) {
		if (Boolean.parseBoolean(helper.getElement().getAttribute(attribute))) {
			for (Class<?> beanClass : beanClasses) {
				helper.register(beanClass);
			}
		}
	}
}
