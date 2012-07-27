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

import java.util.Map;

import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.springfaces.config.util.BeanDefinitionParserHelper;
import org.springframework.springfaces.config.util.RegisteredBeanDefinition;
import org.springframework.springfaces.mvc.servlet.FacesResourceRequestHandler;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} that parses the <tt>resources</tt> element to configure a Spring Faces application.
 * @author Phillip Webb
 */
class ResourcesBeanDefinitionParser extends AbstractBeanDefinitionParser {

	// FIXME Test

	private static final String HANDLER_ADAPTER_BEAN_NAME = HttpRequestHandlerAdapter.class.getName();

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionParserHelper helper = new BeanDefinitionParserHelper(element, parserContext);

		// Register HttpRequestHandlerAdapter if not already defined
		if (!helper.getParserContext().getRegistry().containsBeanDefinition(HANDLER_ADAPTER_BEAN_NAME)) {
			RootBeanDefinition handlerAdapter = helper.rootBeanDefinition(HttpRequestHandlerAdapter.class);
			parserContext.getRegistry().registerBeanDefinition(HANDLER_ADAPTER_BEAN_NAME, handlerAdapter);
			parserContext.registerComponent(new BeanComponentDefinition(handlerAdapter, HANDLER_ADAPTER_BEAN_NAME));
		}

		// Register the handler
		RegisteredBeanDefinition resourceHandler = helper.register(FacesResourceRequestHandler.class);

		// Register the mapping
		Map<String, String> urlMap = new ManagedMap<String, String>();
		urlMap.put("/javax.faces.resource/**", resourceHandler.getName());
		RootBeanDefinition mapping = helper.rootBeanDefinition(SimpleUrlHandlerMapping.class);
		mapping.getPropertyValues().addPropertyValue("urlMap", urlMap);
		mapping.getPropertyValues().addPropertyValue("order", element.getAttribute("order"));
		helper.register(mapping);

		return null;
	}
}
