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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.springfaces.mvc.converter.GenericFacesConverter;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} that parses the <tt>mvc-conversion-service</tt> element to configure a Spring Faces
 * application.
 * 
 * @author Phillip Webb
 */
class ConversionServiceBeanDefinitionParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		Object source = parserContext.extractSource(element);
		BeanDefinition genericFacesConverter = registerGenericFacesConverter(parserContext, source);
		BeanDefinitionBuilder factoryBuilder = BeanDefinitionBuilder
				.rootBeanDefinition(FormattingConversionServiceFactoryBean.class);
		ManagedList<BeanDefinition> converters = new ManagedList<BeanDefinition>();
		converters.add(genericFacesConverter);
		factoryBuilder.addPropertyValue("converters", converters);
		factoryBuilder.getRawBeanDefinition().setSource(source);
		return factoryBuilder.getBeanDefinition();
	}

	private BeanDefinition registerGenericFacesConverter(ParserContext parserContext, Object source) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(GenericFacesConverter.class);
		builder.getRawBeanDefinition().setSource(source);
		AbstractBeanDefinition definition = builder.getBeanDefinition();
		String name = parserContext.getReaderContext().registerWithGeneratedName(definition);
		parserContext.registerComponent(new BeanComponentDefinition(definition, name));
		return definition;
	}
}
